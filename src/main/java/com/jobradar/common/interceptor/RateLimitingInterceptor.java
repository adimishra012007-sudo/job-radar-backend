package com.jobradar.common.interceptor;

import com.jobradar.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    // Thread-safe access to per-IP buckets
    public boolean allowRequest(String ip, Supplier<Bucket> bucketSupplier) {
        Bucket bucket = buckets.computeIfAbsent(ip, k -> bucketSupplier.get());
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return probe.isConsumed();
    }

    public void rejectRequest(HttpServletResponse response) throws Exception {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        ApiResponse<Object> error = ApiResponse.error("Rate limit exceeded. Protocol suppressed.");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
