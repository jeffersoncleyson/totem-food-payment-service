package com.totem.food.framework.adapters.out.sns;

import com.totem.food.application.ports.in.dtos.event.PaymentEventMessageDto;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendSnsUpdateOrderMessageAdapterTest {

    @Mock
    private SnsTemplate snsTemplate;

    @Mock
    private Environment environment;

    private SendSnsUpdateOrderMessageAdapter sendSnsUpdateOrderMessageAdapter;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        when(environment.getProperty("ms.internal.topic.payment")).thenReturn("paymentTopic");
        sendSnsUpdateOrderMessageAdapter = new SendSnsUpdateOrderMessageAdapter(environment, snsTemplate);
    }

    @SneakyThrows
    @AfterEach
    void tearDown() {
        autoCloseable.close();
    }

    @Test
    void testSendMessage() {

        Map<String, Object> headerAttributes = Map.of("order", "UPDATE");
        var messageDto = PaymentEventMessageDto.builder().id(1).build();

        Boolean result = sendSnsUpdateOrderMessageAdapter.sendMessage(messageDto);

        assertTrue(result);

        verify(snsTemplate, times(1)).convertAndSend("paymentTopic", messageDto, headerAttributes);

    }
}