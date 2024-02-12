package com.totem.food.application.usecases.payment;

import com.totem.food.application.enums.OrderStatusEnum;
import com.totem.food.application.ports.in.dtos.event.PaymentEventMessageDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentElementDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
import com.totem.food.application.ports.out.event.ISendEventPort;
import com.totem.food.application.ports.out.internal.order.OrderFilterRequest;
import com.totem.food.application.ports.out.internal.order.OrderResponseRequest;
import com.totem.food.application.ports.out.internal.order.OrderUpdateRequest;
import com.totem.food.application.ports.out.persistence.commons.ISearchRepositoryPort;
import com.totem.food.application.ports.out.persistence.commons.IUpdateRepositoryPort;
import com.totem.food.application.ports.out.persistence.payment.PaymentModel;
import com.totem.food.application.ports.out.web.ISendRequestPort;
import com.totem.food.application.usecases.annotations.UseCase;
import com.totem.food.application.usecases.commons.IUpdateUseCase;
import com.totem.food.domain.payment.PaymentDomain;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@UseCase
public class UpdatePaymentUseCase implements IUpdateUseCase<PaymentFilterDto, Boolean> {

    private final IPaymentMapper iPaymentMapper;
    private final IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;
    private final ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> iSearchOrderModel;
    private final ISendRequestPort<OrderUpdateRequest, Boolean> iUpdateOrderRepositoryPort;
    private final ISearchRepositoryPort<PaymentFilterDto, List<PaymentModel>> iSearchRepositoryPort;
    private final ISendRequestPort<Integer, PaymentElementDto> iSendRequest;
    private final ISendEventPort<PaymentEventMessageDto, Boolean> sendPaymentEventPort;
    private final boolean isDevProfile;

    public UpdatePaymentUseCase(
            IPaymentMapper iPaymentMapper,
            IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort,
            ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> iSearchOrderModel,
            ISendRequestPort<OrderUpdateRequest, Boolean> iUpdateOrderRepositoryPort,
            ISearchRepositoryPort<PaymentFilterDto, List<PaymentModel>> iSearchRepositoryPort,
            ISendRequestPort<Integer, PaymentElementDto> iSendRequest,
            ISendEventPort<PaymentEventMessageDto, Boolean> sendPaymentEventPort,
            Environment environment
    ) {
        this.iPaymentMapper = iPaymentMapper;
        this.iUpdateRepositoryPort = iUpdateRepositoryPort;
        this.iSearchOrderModel = iSearchOrderModel;
        this.iUpdateOrderRepositoryPort = iUpdateOrderRepositoryPort;
        this.iSearchRepositoryPort = iSearchRepositoryPort;
        this.iSendRequest = iSendRequest;
        this.sendPaymentEventPort = sendPaymentEventPort;
        this.isDevProfile = Arrays.stream(environment.getActiveProfiles()).anyMatch(Predicate.isEqual("dev"));
    }


    @Override
    public Boolean updateItem(PaymentFilterDto item, String id) {

        var paymentsModel = iSearchRepositoryPort.findAll(item);

        if (ObjectUtils.isEmpty(paymentsModel)) {
            return Boolean.FALSE;
        }

        //## Search for payments in the partner and update payment in the database
        for (PaymentModel paymentModel : paymentsModel) {

            var paymentElementDto = iSendRequest.sendRequest(paymentModel.getId());

            if (Objects.nonNull(paymentElementDto) && isPaid(paymentElementDto)) {

                final var paymentDomain = iPaymentMapper.toDomain(paymentModel);

                updatePaymentCompleted(paymentDomain);

                sendPaymentEventPort.sendMessage(
                        PaymentEventMessageDto.builder()
                            .id(paymentDomain.getId())
                            .order(paymentDomain.getOrder())
                            .price(paymentDomain.getPrice())
                            .status(paymentDomain.getStatus().key)
                            .createAt(paymentDomain.getCreateAt().toString())
                            .modifiedAt(paymentDomain.getModifiedAt().toString())
                        .build()
                );

            }
        }
        return Boolean.TRUE;
    }

    private boolean isPaid(PaymentElementDto paymentElementDto) {

        if (isDevProfile) return true;
        return paymentElementDto.getOrderStatus().equals("paid");
    }

    //## Update Payment
    private void updatePaymentCompleted(PaymentDomain paymentDomain) {
        paymentDomain.updateStatus(PaymentDomain.PaymentStatus.COMPLETED);
        final var paymentModelConverted = iPaymentMapper.toModel(paymentDomain);
        iUpdateRepositoryPort.updateItem(paymentModelConverted);
    }
}