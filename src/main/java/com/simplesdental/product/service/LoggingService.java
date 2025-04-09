package com.simplesdental.product.service;

import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;


@Service
public class LoggingService {


    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void logWithField(Logger logger, String level, String message, String key, Object value) {
        switch (level.toUpperCase()) {
            case "DEBUG":
                if (logger.isDebugEnabled()) {
                    logger.debug(message, StructuredArguments.kv(key, value));
                }
                break;
            case "INFO":
                logger.info(message, StructuredArguments.kv(key, value));
                break;
            case "WARN":
                logger.warn(message, StructuredArguments.kv(key, value));
                break;
            case "ERROR":
                logger.error(message, StructuredArguments.kv(key, value));
                break;
            case "TRACE":
                if (logger.isTraceEnabled()) {
                    logger.trace(message, StructuredArguments.kv(key, value));
                }
                break;
            default:
                logger.info(message, StructuredArguments.kv(key, value));
                break;
        }
    }


    public static void logWithFields(Logger logger, String level, String message, Map<String, Object> fields) {
        Object[] args = fields.entrySet().stream()
                .map(entry -> StructuredArguments.kv(entry.getKey(), entry.getValue()))
                .toArray();

        switch (level.toUpperCase()) {
            case "DEBUG":
                if (logger.isDebugEnabled()) {
                    logger.debug(message, args);
                }
                break;
            case "INFO":
                logger.info(message, args);
                break;
            case "WARN":
                logger.warn(message, args);
                break;
            case "ERROR":
                logger.error(message, args);
                break;
            case "TRACE":
                if (logger.isTraceEnabled()) {
                    logger.trace(message, args);
                }
                break;
            default:
                logger.info(message, args);
                break;
        }
    }

    public static String startOperation(Logger logger, String operation) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("operation", operation);

        logger.info("Starting operation: {}",
                StructuredArguments.kv("operation", operation));

        return requestId;
    }


    public static void endOperation(Logger logger, String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;

        logger.info("Operation completed: {}",
                StructuredArguments.fields(
                        Map.of(
                                "operation", operation,
                                "durationMs", duration
                        )
                ));

        MDC.remove("requestId");
        MDC.remove("operation");
    }

    public static void logError(Logger logger, String operation, Throwable error) {
        logger.error("Error during operation: {}",
                StructuredArguments.fields(
                        Map.of(
                                "operation", operation,
                                "errorType", error.getClass().getName(),
                                "errorMessage", error.getMessage()
                        )
                ),
                error);
    }
}