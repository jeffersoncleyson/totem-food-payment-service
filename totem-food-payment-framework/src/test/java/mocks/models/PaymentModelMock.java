package mocks.models;

import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.domain.payment.PaymentDomain;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public class PaymentModelMock {

    public static PaymentModel getPaymentDomain(PaymentDomain.PaymentStatus paymentStatus){
        return PaymentModel.builder()
                .id(1)
//@todo - refact
//                .order(OrderDomainMock.getStatusNewMock())
//                .customer(CustomerDomainMock.getMock())
                .price(50D)
                .token(UUID.randomUUID().toString())
                .status(paymentStatus)
                .modifiedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .createAt(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
    }
}
