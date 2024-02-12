package com.totem.food.framework.adapters.out.sns;

import com.totem.food.application.ports.in.dtos.event.PaymentEventMessageDto;
import com.totem.food.application.ports.out.event.ISendEventPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
@Slf4j
public class SendSnsMessageAdapter implements ISendEventPort<PaymentEventMessageDto, Boolean> {

    private final SnsTemplate snsTemplate;

    @Override
    public Boolean sendMessage(PaymentEventMessageDto item) {
        Map<String, Object> headerAttributes = Map.of("order", "UPDATE");
        snsTemplate.convertAndSend("payment-topic", item, headerAttributes);
        return Boolean.TRUE;
    }
}
