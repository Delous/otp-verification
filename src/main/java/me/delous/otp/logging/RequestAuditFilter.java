package me.delous.otp.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import me.delous.otp.security.AuthenticatedAccount;
import me.delous.otp.security.BearerTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(20)
public class RequestAuditFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestAuditFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        long started = System.nanoTime();
        log.info("http.request.start requestId={} method={} uri={}",
                requestId, request.getMethod(), request.getRequestURI());
        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMs = (System.nanoTime() - started) / 1_000_000;
            AuthenticatedAccount account = (AuthenticatedAccount) request.getAttribute(BearerTokenFilter.AUTH_ATTR);
            String accountId = account == null ? "anonymous" : String.valueOf(account.id());
            String role = account == null ? "none" : account.role().name();
            log.info("http.request.finish requestId={} method={} uri={} status={} elapsedMs={} accountId={} role={}",
                    requestId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    elapsedMs,
                    accountId,
                    role);
        }
    }
}
