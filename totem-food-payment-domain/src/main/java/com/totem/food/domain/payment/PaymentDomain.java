package com.totem.food.domain.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDomain {

    private Integer id;
    private String order;
    private String customer;
    private double price;
    private String token;

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    private ZonedDateTime modifiedAt;
    private ZonedDateTime createAt;

    public void updateModifiedAt() {
        this.modifiedAt = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public void fillDates() {
        if (Objects.isNull(this.id)) {
            this.createAt = ZonedDateTime.now(ZoneOffset.UTC);
            this.modifiedAt = ZonedDateTime.now(ZoneOffset.UTC);
        }
    }

    public void updateStatus(PaymentStatus status) {
        if (PaymentStatus.COMPLETED.equals(status)) {
            this.status = status;
        }
    }


    public enum PaymentStatus {
        PENDING("PENDING"),
        COMPLETED("COMPLETED"),
        CANCELED("CANCELED");

        public final String key;

        PaymentStatus(String key) {
            this.key = key;
        }
    }
}
