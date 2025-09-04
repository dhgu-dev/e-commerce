package com.loopers.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.config.kafka.KafkaConfig;
import com.loopers.domain.event.EventHandled;
import com.loopers.domain.event.EventHandledRepository;
import com.loopers.domain.event.EventSchema;
import com.loopers.domain.product.ProductInfo;
import com.loopers.support.CacheManager;
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
public class CacheManagerConsumer {
    private static final Map<String, Class<?>> EVENT_TYPE_MAP = new HashMap<>();

    static {
        EVENT_TYPE_MAP.put("LikeEvent", EventSchema.LikeChangedEvent.class);
        EVENT_TYPE_MAP.put("UnLikeEvent", EventSchema.LikeChangedEvent.class);
        EVENT_TYPE_MAP.put("StockAdjustedEvent", EventSchema.StockAdjustedEvent.class);
    }

    private final ObjectMapper objectMapper;
    private final EventHandledRepository eventHandledRepository;
    private final CacheManager<ProductInfo> productInfoCacheManager;

    @KafkaListener(
        topics = {"${kafka-topics.catalog-events.topic-name}"},
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "cache-manager-consumer-group"
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

                if (eventHandledRepository.exists(eventId, CacheManagerConsumer.class.getName())) {
                    return;
                }

                String eventName = (String) map.get("eventName");
                Class<?> clazz = EVENT_TYPE_MAP.get(eventName);
                if (clazz == null) {
                    return;
                }
                
                Object eventObj = objectMapper.readValue(json, clazz);

                if (eventObj instanceof EventSchema.LikeChangedEvent event) {
                    productInfoCacheManager.delete("product:detail:" + event.productId(), ProductInfo.class);
                } else if (eventObj instanceof EventSchema.StockAdjustedEvent event) {
                    if (event.stock() == 0) {
                        productInfoCacheManager.delete("product:detail:" + event.productId(), ProductInfo.class);
                    }
                }

                eventHandledRepository.save(new EventHandled(eventId, CacheManagerConsumer.class.getName()));
            } catch (JsonProcessingException e) {
                log.error("Failed to process message: {}", json, e);
            } catch (DataIntegrityViolationException | ConstraintViolationException ignored) {
            }
        });
        acknowledgment.acknowledge();
    }
}
