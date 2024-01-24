package com.totem.food.application.usecases.payment;

import com.totem.food.application.exceptions.ElementNotFoundException;
import com.totem.food.application.ports.in.dtos.payment.PaymentElementDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.mappers.payment.IPaymentMapper;
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
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@UseCase
public class UpdatePaymentUseCase implements IUpdateUseCase<PaymentFilterDto, Boolean> {

    private final IPaymentMapper iPaymentMapper;
    private final IUpdateRepositoryPort<PaymentModel> iUpdateRepositoryPort;
    private final ISendRequestPort<OrderFilterRequest, Optional<OrderResponseRequest>> iSearchOrderModel;
    private final ISendRequestPort<OrderUpdateRequest, Boolean> iUpdateOrderRepositoryPort;
    private final ISearchRepositoryPort<PaymentFilterDto, List<PaymentModel>> iSearchRepositoryPort;
    private final ISendRequestPort<Integer, PaymentElementDto> iSendRequest;

    @Override
    public Boolean updateItem(PaymentFilterDto item, String id) {

        var paymentsModel = iSearchRepositoryPort.findAll(item);

        if (ObjectUtils.isEmpty(paymentsModel)) {
            return Boolean.FALSE;
        }

        //## Search for payments in the partner and update payment in the database
        for (PaymentModel paymentModel : paymentsModel) {

            var paymentElementDto = iSendRequest.sendRequest(paymentModel.getId());

            if (Objects.nonNull(paymentElementDto) && paymentElementDto.getOrderStatus().equals("paid")) {

                final var paymentDomain = iPaymentMapper.toDomain(paymentModel);

                final var orderId = paymentDomain.getOrder();
                final var orderFilterRequest = OrderFilterRequest.builder()
                        .orderId(orderId)
                        .build();
                final var orderModel = iSearchOrderModel.sendRequest(orderFilterRequest)
                        .orElseThrow(() -> new ElementNotFoundException(String.format("Order with orderId: [%s] not found", orderId)));

                if (verifyOrderPaid(paymentDomain, orderModel)) {
                    updatePaymentCompleted(paymentDomain);
                    updateOrderReceived(orderModel);
                }
            }
        }
        return Boolean.TRUE;
    }

    //## Verify Order is Received and Payment is Completed
    private static boolean verifyOrderPaid(PaymentDomain paymentDomain, OrderResponseRequest orderResponseRequest) {
        return !paymentDomain.getStatus().equals(PaymentDomain.PaymentStatus.COMPLETED)
                && !orderResponseRequest.getStatus().equals("RECEIVED"); //@todo - refact - colocar enum no lugar
    }

    //## Update Payment
    private void updatePaymentCompleted(PaymentDomain paymentDomain) {
        paymentDomain.updateStatus(PaymentDomain.PaymentStatus.COMPLETED);
        final var paymentModelConverted = iPaymentMapper.toModel(paymentDomain);
        iUpdateRepositoryPort.updateItem(paymentModelConverted);
    }

    //## Update Order
    private void updateOrderReceived(OrderResponseRequest orderResponseRequest) {
        final var orderUpdateRequest = OrderUpdateRequest.builder()
                .orderId(orderResponseRequest.getId())
                .status("RECEIVED") //@todo - refact - colocar enum no lugar
                .build();
        iUpdateOrderRepositoryPort.sendRequest(orderUpdateRequest);
    }
}