package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.event.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogConsumer {
    private static final Map<String, Class<?>> EVENT_TYPE_MAP = new HashMap<>();

    static {
        EVENT_TYPE_MAP.put("LikeEvent", EventSchema.LikeChangedEvent.class);
        EVENT_TYPE_MAP.put("UnLikeEvent", EventSchema.LikeChangedEvent.class);
        EVENT_TYPE_MAP.put("StockAdjustedEvent", EventSchema.StockAdjustedEvent.class);
        EVENT_TYPE_MAP.put("ProductDetailViewedEvent", EventSchema.ProductDetailViewedEvent.class);
        EVENT_TYPE_MAP.put("ProductSoldEvent", EventSchema.ProductSoldEvent.class);
    }

    private final ObjectMapper objectMapper;
    private final EventHandledRepository eventHandledRepository;
    private final EventLogRepository eventLogRepository;

    @KafkaListener(
        topics = {"${kafka-topics.catalog-events.topic-name}"},
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "audit-log-consumer-group"
    )
    @Transactional
    public void topicCatalogEventsListener(
        List<ConsumerRecord<Object, Object>> messages,
        Acknowledgment acknowledgment
    ) {
        messages.forEach(record -> {
            String json = record.value().toString();
            try {
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                String eventId = (String) map.get("eventId");

                if (eventHandledRepository.exists(eventId, AuditLogConsumer.class.getName())) {
                    return;
                }

                String eventName = (String) map.get("eventName");
                Class<?> clazz = EVENT_TYPE_MAP.get(eventName);
                if (clazz == null) {
                    return;
                }

                Object eventObj = objectMapper.readValue(json, clazz);

                if (eventObj instanceof EventSchema.LikeChangedEvent event) {
                    eventLogRepository.save(new EventLog(event.eventId(), event.eventName(), event.memberId()));
                } else if (eventObj instanceof EventSchema.ProductDetailViewedEvent event) {
                    eventLogRepository.save(new EventLog(event.eventId(), event.eventName(), null));
                } else if (eventObj instanceof EventSchema.StockAdjustedEvent event) {
                    eventLogRepository.save(new EventLog(event.eventId(), event.eventName(), event.memberId()));
                }

                eventHandledRepository.save(new EventHandled(eventId, AuditLogConsumer.class.getName()));
            } catch (JsonProcessingException e) {
                log.error("Failed to process message: {}", json, e);
            } catch (DataIntegrityViolationException | ConstraintViolationException ignored) {
            }
        });
        acknowledgment.acknowledge();
    }

    @KafkaListener(
        topics = {"${kafka-topics.order-events.topic-name}"},
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "audit-log-consumer-group"
    )
    @Transactional
    public void topicOrderEventsListener(
        List<ConsumerRecord<Object, Object>> messages,
        Acknowledgment acknowledgment
    ) {
        messages.forEach(record -> {
            String json = record.value().toString();
            try {
                Map<String, Object> map = objectMapper.readValue(json, Map.class);
                String eventId = (String) map.get("eventId");
                if (eventHandledRepository.exists(eventId, AuditLogConsumer.class.getName())) {
                    return;
                }

                String eventName = (String) map.get("eventName");
                Class<?> clazz = EVENT_TYPE_MAP.get(eventName);
                Object eventObj = objectMapper.readValue(json, clazz);

                if (eventObj instanceof EventSchema.ProductSoldEvent event) {
                    eventLogRepository.save(new EventLog(event.eventId(), event.eventName(), event.memberId()));
                }

                eventHandledRepository.save(new EventHandled(eventId, AuditLogConsumer.class.getName()));
            } catch (JsonProcessingException e) {
                log.error("Failed to process message: {}", json, e);
            } catch (DataIntegrityViolationException | ConstraintViolationException ignored) {
            }
        });
        acknowledgment.acknowledge();
    }
}
