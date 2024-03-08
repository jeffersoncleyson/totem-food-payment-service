package com.totem.food.framework.adapters.out.sns;

import com.totem.food.application.ports.out.email.EmailNotificationDto;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendSnsEmailEventAdapterTest {

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private Environment environment;

    private SendSnsEmailEventAdapter sendSnsEmailEventAdapter;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        when(environment.getProperty("ms.internal.topic.email")).thenReturn("emailTopic");
        sendSnsEmailEventAdapter = new SendSnsEmailEventAdapter(environment, snsTemplate);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        autoCloseable.close();
    }

    @Test
    void sendMessage() {

        //## Given
        var emailDto = new EmailNotificationDto();
        emailDto.setMessage("notification");

        //## When
        Boolean result = sendSnsEmailEventAdapter.sendMessage(emailDto);

        //## Then
        assertTrue(result);
        verify(snsTemplate, times(1)).convertAndSend("emailTopic", emailDto);

    }
}