package com.jobradar.config;

import com.jobradar.common.interceptor.RateLimitingInterceptor;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;

    @Override
    public void addInterceptors(@org.springframework.lang.NonNull InterceptorRegistry registry) {
        // AUTH LIMITING (10 requests per minute)
        registry.addInterceptor(new HandlerInterceptorWrapper(() -> 
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(10).refillGreedy(10, Duration.ofMinutes(1)).build())
                .build()
        )).addPathPatterns("/api/v1/auth/**");

        // CHAT LIMITING (30 messages per minute)
        registry.addInterceptor(new HandlerInterceptorWrapper(() -> 
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(30).refillGreedy(30, Duration.ofMinutes(1)).build())
                .build()
        )).addPathPatterns("/api/v1/chat/**");

        // GENERAL LIMITING (100 requests per minute)
        registry.addInterceptor(new HandlerInterceptorWrapper(() -> 
            Bucket.builder()
                .addLimit(Bandwidth.builder().capacity(100).refillGreedy(100, Duration.ofMinutes(1)).build())
                .build()
        )).addPathPatterns("/api/v1/jobs/**");
    }

    private class HandlerInterceptorWrapper implements org.springframework.web.servlet.HandlerInterceptor {
        private final java.util.function.Supplier<Bucket> bucketSupplier;
        public HandlerInterceptorWrapper(java.util.function.Supplier<Bucket> bucketSupplier) {
            this.bucketSupplier = bucketSupplier;
        }

        @Override
        public boolean preHandle(@org.springframework.lang.NonNull jakarta.servlet.http.HttpServletRequest request, 
                                @org.springframework.lang.NonNull jakarta.servlet.http.HttpServletResponse response, 
                                @org.springframework.lang.NonNull Object handler) throws Exception {
            String ip = request.getRemoteAddr();
            if (!rateLimitingInterceptor.allowRequest(ip, bucketSupplier)) {
                rateLimitingInterceptor.rejectRequest(response);
                return false;
            }
            return true;
        }
    }
}
