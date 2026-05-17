package com.interswitch.fraudtransactionapp.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor
public class ObservabilityAspect {

    private static final Logger logger = LoggerFactory.getLogger(ObservabilityAspect.class);
    private final MeterRegistry meterRegistry;

    private static final Pattern CARD_NO_PATTERN = Pattern.compile("\"cardNo\"\\s*:\\s*\"(\\d{6})(\\d+)(\\d{4})\"");

    @Around("execution(* com.interswitch.fraudtransactionapp.service..*(..)) || " +
            "@within(com.interswitch.fraudtransactionapp.config.TrackExecution) || " +
            "@annotation(com.interswitch.fraudtransactionapp.config.TrackExecution)")
    public Object monitorExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();
        String argsJson = "";

        try {
            argsJson = objectMapper.writeValueAsString(args);
            argsJson = maskSensitiveData(argsJson);
            logger.info("[INPUT] {} called with args: {}", methodName, argsJson);
        } catch (Exception e) {
            logger.warn("[INPUT] {} called with args (could not serialize): {}", methodName, argsJson, e);
        }

        try {
            Object result = joinPoint.proceed();
            String resultJson = "";

            try {
                resultJson = objectMapper.writeValueAsString(result);
                resultJson = maskSensitiveData(resultJson);
                logger.info("[OUTPUT] {} returned: {}", methodName, resultJson);
            } catch (Exception e) {
                logger.warn("[OUTPUT] {} returned (could not serialize): {}", methodName, resultJson, e);
            }

            return result;
        } catch (Exception ex) {
            logger.error("[ERROR] Method {} threw: {} - {}", methodName,
                    ex.getClass().getSimpleName(), ex.getMessage(), ex);
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - start;
            logger.info("[INFO] Method {} executed in {}ms", methodName, duration);

            if (meterRegistry != null) {
                Timer.builder("employee.service.execution")
                        .tag("method", methodName)
                        .register(meterRegistry)
                        .record(duration, TimeUnit.MILLISECONDS);
            }
        }
    }

    private String maskSensitiveData(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        return CARD_NO_PATTERN.matcher(json).replaceAll("\"cardNo\":\"$1****$3\"");
    }
}