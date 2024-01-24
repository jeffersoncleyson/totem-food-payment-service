package mocks.entity;

import com.totem.food.domain.payment.PaymentDomain;
import com.totem.food.framework.adapters.out.persistence.mysql.payment.entity.PaymentEntity;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PaymentEntityMock {

    public static PaymentEntity getPaymentEntity(PaymentDomain.PaymentStatus paymentStatus){
        return PaymentEntity.builder()
                .id(1)
                .order(UUID.randomUUID().toString())
                .customer(UUID.randomUUID().toString())
                .price(50D)
                .token(UUID.randomUUID().toString())
                .status(paymentStatus.key)
                .modifiedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .createAt(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
    }
}
