package com.totem.food.framework.adapters.out.sns;

import com.totem.food.application.ports.out.email.EmailNotificationDto;
import com.totem.food.application.ports.out.event.ISendEventPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class SendEmailEventAdapter implements ISendEventPort<EmailNotificationDto, Boolean> {

    private final SnsTemplate snsTemplate;

    @Override
    public Boolean sendMessage(EmailNotificationDto item) {
        snsTemplate.convertAndSend("email-topic", item);
        return Boolean.TRUE;
    }
}
