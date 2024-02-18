package com.totem.food.framework.adapters.in.rest.payment.adapter;

import com.totem.food.application.ports.in.dtos.payment.PaymentCreateDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentFilterDto;
import com.totem.food.application.ports.in.dtos.payment.PaymentQRCodeDto;
import com.totem.food.application.ports.in.rest.ICreateRestApiPort;
import com.totem.food.application.ports.in.rest.IGetStatusRestApiPort;
import com.totem.food.application.ports.in.rest.ISearchImageApiPort;
import com.totem.food.application.ports.in.rest.IUpdateStatusByIdRestApiPort;
import com.totem.food.application.usecases.commons.ICreateImageUseCase;
import com.totem.food.application.usecases.commons.ICreateUseCase;
import com.totem.food.application.usecases.commons.ISearchUniqueUseCase;
import com.totem.food.application.usecases.commons.ISearchUseCase;
import com.totem.food.application.usecases.commons.IUpdateStatusUseCase;
import com.totem.food.domain.payment.PaymentDomain;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.totem.food.framework.adapters.in.rest.constants.Routes.API_VERSION_1;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.PAYMENT_ID;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.PAYMENT_ORDER_ID;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.PAYMENT_ORDER_ID_AND_STATUS;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.PAYMENT_ORDER_ID_CANCEL;
import static com.totem.food.framework.adapters.in.rest.constants.Routes.TOTEM_PAYMENT;

@RestController
@RequestMapping(value = API_VERSION_1 + TOTEM_PAYMENT)
@AllArgsConstructor
public class TotemPaymentRestApiAdapter implements
        ICreateRestApiPort<PaymentCreateDto, ResponseEntity<PaymentQRCodeDto>>,
        ISearchImageApiPort<ResponseEntity<Object>>,
        IGetStatusRestApiPort<ResponseEntity<PaymentDto>>,
        IUpdateStatusByIdRestApiPort<ResponseEntity<PaymentDto>> {

    private final ICreateUseCase<PaymentCreateDto, PaymentQRCodeDto> iCreateUseCase;
    private final ISearchUniqueUseCase<String, Optional<PaymentDto>> iSearchUniqueUseCase;
    private final ICreateImageUseCase<PaymentDto, byte[]> iCreateImageUseCase;
    private final ISearchUseCase<PaymentFilterDto, Optional<PaymentDto>> iSearchUseCase;
    private final IUpdateStatusUseCase<PaymentDto> iUpdateStatusUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PaymentQRCodeDto> create(@RequestBody PaymentCreateDto item) {
        final var created = iCreateUseCase.createItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping(value = PAYMENT_ORDER_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<Object> getImage(@PathVariable String orderId,
                                           @RequestHeader(value = "x-with-image-qrcode", defaultValue = "true") boolean withImageQrCode) {

        ResponseEntity<PaymentDto> paymentDto = iSearchUniqueUseCase.item(orderId).map(ResponseEntity.status(HttpStatus.OK)::body)
                .orElse(ResponseEntity.notFound().build());

        if (withImageQrCode) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(iCreateImageUseCase.createImage(paymentDto.getBody()), headers, HttpStatus.OK);
        }

        return ResponseEntity.ok().body(paymentDto.getBody());
    }

    @GetMapping(value = PAYMENT_ORDER_ID_AND_STATUS, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PaymentDto> getByIdAndStatus(@PathVariable("orderId") String orderId, @PathVariable("statusName") String status) {
        return iSearchUseCase.items(
                        PaymentFilterDto.builder()
                                .orderId(orderId)
                                .status(status)
                                .build()
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PatchMapping(value = PAYMENT_ORDER_ID_CANCEL, produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PaymentDto> update(@PathVariable("orderId") String orderId) {
        return Optional.ofNullable(iUpdateStatusUseCase.updateStatus(orderId, PaymentDomain.PaymentStatus.PENDING.key))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}