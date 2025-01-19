package com.hhplush.eCommerce.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CustomLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logger.info("Request received: method={}, URI={}, query={}",
            httpRequest.getMethod(),
            httpRequest.getRequestURI(),
            httpRequest.getQueryString());

        // FilterChain을 호출하여 요청 처리
        chain.doFilter(request, response);

        // 응답 상태 코드별 추가 로그
        int status = httpResponse.getStatus();
        if (status >= 400 && status < 500) {
            switch (status) {
                case 400:
                    logger.warn("Bad Request (400) for URI: {}", httpRequest.getRequestURI());
                    break;
                case 404:
                    logger.warn("Not Found (404) for URI: {}", httpRequest.getRequestURI());
                    break;
                case 409:
                    logger.warn("Conflict (409) for URI: {}", httpRequest.getRequestURI());
                    break;
                default:
                    logger.warn("Client Error ({}): URI={}", status, httpRequest.getRequestURI());
            }
        } else if (status >= 500) {
            logger.error("Server Error ({}): URI={}", status, httpRequest.getRequestURI());
        } else {
            logger.info("Response sent: status={}, URI={}", status, httpRequest.getRequestURI());
        }
    }
}