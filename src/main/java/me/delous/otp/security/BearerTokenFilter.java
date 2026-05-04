package me.delous.otp.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import me.delous.otp.account.AccountRole;
import me.delous.otp.account.AuthTokenIssuer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {
    public static final String AUTH_ATTR = "authenticatedAccount";
    private static final Logger log = LoggerFactory.getLogger(BearerTokenFilter.class);
    private final AuthTokenIssuer tokenIssuer;

    public BearerTokenFilter(AuthTokenIssuer tokenIssuer) {
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (isPublicEndpoint(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            reject(response, "auth.token.missing", "Требуется bearer token");
            return;
        }

        try {
            DecodedJWT jwt = tokenIssuer.verify(header.substring(7));
            request.setAttribute(AUTH_ATTR, new AuthenticatedAccount(
                    Long.parseLong(jwt.getSubject()),
                    jwt.getClaim("username").asString(),
                    AccountRole.valueOf(jwt.getClaim("role").asString())
            ));
            filterChain.doFilter(request, response);
        } catch (JWTVerificationException | IllegalArgumentException ex) {
            log.info("auth.token.rejected reason={}", ex.getClass().getSimpleName());
            reject(response, "auth.token.rejected", "Токен недействителен или просрочен");
        }
    }

    private void reject(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
                {"data":null,"error":{"code":"%s","message":"%s"}}
                """.formatted(code, message));
    }

    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/auth/")
                || uri.equals("/docs");
    }
}
