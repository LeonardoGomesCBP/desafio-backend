package com.simplesdental.product.config;

import com.simplesdental.product.service.LoggingService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggingService.getLogger(RequestLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 1000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            logRequest(requestWrapper);

            filterChain.doFilter(requestWrapper, responseWrapper);

            logResponse(responseWrapper, System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            LoggingService.logError(logger, "http_request", e);
            throw e;
        } finally {
            responseWrapper.copyBodyToResponse();
            MDC.remove("requestId");
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String path = request.getRequestURI() + queryString;
        String method = request.getMethod();

        LoggingService.logWithFields(logger, "INFO", "HTTP request received",
                Map.of(
                        "method", method,
                        "path", path,
                        "clientIp", request.getRemoteAddr(),
                        "userAgent", request.getHeader("User-Agent")
                ));
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();

        String level = "INFO";
        if (status >= 400 && status < 500) {
            level = "WARN";
        } else if (status >= 500) {
            level = "ERROR";
        }

        LoggingService.logWithFields(logger, level, "HTTP request sent",
                Map.of(
                        "status", status,
                        "durationMs", duration,
                        "contentType", response.getContentType() != null ? response.getContentType() : "unknown"
                ));
    }
}