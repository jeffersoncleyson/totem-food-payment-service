package mock.domain;

import com.totem.food.domain.payment.PaymentDomain;

import java.time.ZonedDateTime;

public class PaymentDomainMock {

    public static PaymentDomain getPaymentToUpdatePaymentUseCase(PaymentDomain.PaymentStatus status) {
        return PaymentDomain.builder()
            .id(1)
            .order("1")
            .price(49.99)
            .token("token")
            .status(status)
            .createAt(ZonedDateTime.parse("2023-04-03T13:28:20.606-03:00"))
            .modifiedAt(ZonedDateTime.parse("2023-04-03T13:28:20.606-03:00"))
            .build();
    }

}
