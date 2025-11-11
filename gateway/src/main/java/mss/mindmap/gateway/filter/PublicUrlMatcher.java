package mss.mindmap.gateway.filter;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;

@Component
public class PublicUrlMatcher {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<PublicRule> PUBLIC_RULES = List.of(
            // ================= Identity Service =================
            new PublicRule(HttpMethod.POST, List.of(
                    "/identity/auth/login",
                    "/identity/auth/register",
                    "/identity/auth/register-with-otp",
                    "/identity/auth/verify-otp",
                    "/identity/auth/introspect"
            )),


            new PublicRule(HttpMethod.GET, List.of(
                    // Swagger/OpenAPI documentation endpoints
                    "/quiz/v3/api-docs/**",
                    "/mindmap/v3/api-docs/**",
                    "/identity/v3/api-docs/**",
                    "/ownership/v3/api-docs/**",
                    "/**/v3/api-docs/**",
                    "/documents/v3/api-docs/**",
                    "/vector/v3/api-docs/**",
                    "/ai/v3/api-docs/**",
                    "/payment/v3/api-docs/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/*-service/v3/api-docs/**",
                    "/*-service/swagger-ui/**",
                    "/quiz/quizzes",
                    "/quiz/quizzes/*"
            ))
    );

    public boolean matches(String path, HttpMethod method) {
        if (method == null) return false; // đề phòng null

        return PUBLIC_RULES.stream().anyMatch(rule ->
                rule.method.equals(method) &&
                        rule.urls.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))
        );
    }

    // inner class mô tả rule
    private static class PublicRule {
        HttpMethod method;
        List<String> urls;

        PublicRule(HttpMethod method, List<String> urls) {
            this.method = method;
            this.urls = urls;
        }
    }
}
