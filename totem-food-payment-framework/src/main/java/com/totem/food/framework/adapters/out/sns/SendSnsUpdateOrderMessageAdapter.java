package com.totem.food.framework.adapters.out.sns;

import com.totem.food.application.ports.in.dtos.event.PaymentEventMessageDto;
import com.totem.food.application.ports.out.event.ISendEventPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class SendSnsUpdateOrderMessageAdapter implements ISendEventPort<PaymentEventMessageDto, Boolean> {

    private final SnsTemplate snsTemplate;
    private final String paymentTopicSns;

    public SendSnsUpdateOrderMessageAdapter(Environment env, SnsTemplate snsTemplate) {
        this.paymentTopicSns = env.getProperty("ms.internal.topic.payment");
        this.snsTemplate = snsTemplate;
    }

    @Override
    public Boolean sendMessage(PaymentEventMessageDto item) {
        Map<String, Object> headerAttributes = Map.of("order", "UPDATE");
        snsTemplate.convertAndSend(this.paymentTopicSns, item, headerAttributes);
        return Boolean.TRUE;
    }
}
