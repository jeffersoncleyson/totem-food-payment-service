package com.totem.food.framework.adapters.out.web.internal.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeignHeaderInterceptorTest {

    @Mock
    private RequestTemplate requestTemplate;

    @InjectMocks
    private FeignHeaderInterceptor feignHeaderInterceptor;

    @Test
    void apply_shouldAddHeaders() {

        //## Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("x-user-identifier", "test-user");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        //## When
        feignHeaderInterceptor.apply(requestTemplate);

        //## Then
        verify(requestTemplate).header("x-user-identifier", "test-user");
    }

}