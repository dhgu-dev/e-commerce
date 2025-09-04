package com.loopers.domain.metric;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "product_metrics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ProductMetricsId.class)
public class ProductMetrics {
    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Id
    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    @Column(name = "like_cnt", nullable = false)
    private long likeCnt;

    @Column(name = "view_cnt", nullable = false)
    private long viewCnt;

    @Column(name = "sales_cnt", nullable = false)
    private long salesCnt;

    public ProductMetrics(Long productId) {
        this.productId = productId;
        this.baseDate = LocalDate.now();
        this.likeCnt = 0;
        this.viewCnt = 0;
        this.salesCnt = 0;
    }

    public void incrementLikeCnt() {
        this.likeCnt++;
    }

    public void decrementLikeCnt() {
        if (this.likeCnt > 0) {
            this.likeCnt--;
        }
    }

    public void incrementViewCnt() {
        this.viewCnt++;
    }

    public void incrementSalesCnt(Long quantity) {
        this.salesCnt += quantity;
    }
}
