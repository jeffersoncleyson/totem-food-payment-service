package com.totem.food.framework.adapters.out.web.internal.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component
public class FeignHeaderInterceptor implements RequestInterceptor {

	private static final String X_USER_IDENTIFIER = "x-user-identifier";

	@Override
	public void apply(RequestTemplate template) {

		var headers = this.getHeaders(X_USER_IDENTIFIER);
		for (var k : headers.entrySet()) {
			template.header(k.getKey(), k.getValue());
		}
	}

	private Map<String, String> getHeaders(String... headers) {

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Map<String, String> requestHeaders = new HashMap<>();
		if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
			HttpServletRequest request = servletRequestAttributes.getRequest();
			for (var h : headers) {
				requestHeaders.put(h, request.getHeader(h));
			}
		}
		return requestHeaders;
	}
}
