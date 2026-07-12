package com.incubyte.car_dealership_inventory_system.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Correlation-ID filter that enables end-to-end request tracing.
 *
 * <p>On every inbound request this filter:
 * <ol>
 *   <li>Reads the {@code X-Correlation-ID} header or generates a random UUID.</li>
 *   <li>Places the ID into SLF4J's MDC so all log lines within this request are tagged.</li>
 *   <li>Echoes the ID back in the response header for client-side tracing.</li>
 *   <li>Logs the method, URI, status code, and duration once the downstream chain completes.</li>
 * </ol>
 */
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String correlationId = Optional
                .ofNullable(request.getHeader("X-Correlation-ID"))
                .orElse(UUID.randomUUID().toString());

        MDC.put("correlationId", correlationId);
        response.addHeader("X-Correlation-ID", correlationId);

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("[{}] {} {} -> {} ({}ms)",
                    correlationId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration);
            // Prevent MDC leakage into subsequent requests handled by the same thread.
            MDC.clear();
        }
    }
}
