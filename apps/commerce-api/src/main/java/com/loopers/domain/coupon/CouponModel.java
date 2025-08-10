package com.loopers.domain.coupon;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private final Long id = 0L;

    @Getter
    @Version
    private Long version;

    @Column(unique = true, nullable = false, updatable = false)
    @Getter
    private String code;

    @Embedded
    @Getter
    private DiscountMethod discountMethod;

    @Column(name = "member_id")
    @Getter
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_scope", nullable = false)
    @Getter
    private TargetScope targetScope;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "issued_at")
    @Getter
    private ZonedDateTime issuedAt;

    @Column(name = "deleted_at")
    @Getter
    private ZonedDateTime deletedAt;

    public CouponModel(DiscountMethod discountMethod, TargetScope targetScope) {
        if (discountMethod == null || targetScope == null) {
            throw new IllegalArgumentException("할인 방법과 대상 범위는 null 일 수 없습니다.");
        }
        this.code = UUID.randomUUID().toString();
        this.discountMethod = discountMethod;
        this.targetScope = targetScope;
        this.version = 0L;
    }

    public CouponModel(DiscountMethod discountMethod, TargetScope targetScope, Long memberId) {
        if (discountMethod == null || targetScope == null || memberId == null) {
            throw new IllegalArgumentException("할인 방법, 대상 범위, 회원 아이디는 null 일 수 없습니다.");
        }
        this.code = UUID.randomUUID().toString();
        this.discountMethod = discountMethod;
        this.targetScope = targetScope;
        this.memberId = memberId;
        this.issuedAt = ZonedDateTime.now();
        this.version = 0L;
    }

    public void issueTo(Long memberId) {
        if (this.issuedAt != null) {
            throw new IllegalStateException("이미 발급된 쿠폰입니다.");
        }
        this.memberId = memberId;
        this.issuedAt = ZonedDateTime.now();
    }

    public BigDecimal apply(BigDecimal originalPrice) {
        if (this.discountMethod == null) {
            throw new IllegalStateException("할인 규칙이 설정되지 않았습니다.");
        }
        if (this.deletedAt != null) {
            throw new IllegalStateException("사용된 쿠폰입니다. 사용할 수 없습니다.");
        }
        delete();
        return this.discountMethod.toPolicy().applyDiscount(originalPrice);
    }

    public boolean hasOwned(Long memberId) {
        if (memberId == null || this.memberId == null) {
            return false;
        }
        return this.memberId.equals(memberId);
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = ZonedDateTime.now();
    }

    /**
     * delete 연산은 멱등하게 동작할 수 있도록 한다. (삭제된 엔티티를 다시 삭제해도 동일한 결과가 나오도록)
     */
    public void delete() {
        if (this.deletedAt == null) {
            this.deletedAt = ZonedDateTime.now();
        }
    }

    /**
     * restore 연산은 멱등하게 동작할 수 있도록 한다. (삭제되지 않은 엔티티를 복원해도 동일한 결과가 나오도록)
     */
    public void restore() {
        if (this.deletedAt != null) {
            this.deletedAt = null;
        }
    }

    public enum TargetScope {
        ORDER
    }
}
