package com.totem.food.framework.adapters.out.sns;

import com.totem.food.application.ports.out.email.EmailNotificationDto;
import com.totem.food.application.ports.out.event.ISendEventPort;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendSnsEmailEventAdapter implements ISendEventPort<EmailNotificationDto, Boolean> {

    private final SnsTemplate snsTemplate;
    private final String emailTopicSns;

    public SendSnsEmailEventAdapter(Environment env, SnsTemplate snsTemplate) {
        this.emailTopicSns = env.getProperty("ms.internal.topic.email");
        this.snsTemplate = snsTemplate;
    }

    @Override
    public Boolean sendMessage(EmailNotificationDto item) {
        snsTemplate.convertAndSend(this.emailTopicSns, item);
        return Boolean.TRUE;
    }
}
