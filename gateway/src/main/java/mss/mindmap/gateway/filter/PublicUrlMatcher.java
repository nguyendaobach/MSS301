package mss.mindmap.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;

@Component
public class PublicUrlMatcher {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final Set<String> PUBLIC_EXACT_PATHS = Set.of(
            //identity
            "/auth/login",
            "/auth/register",
            "/auth/register-with-otp",
            "/auth/verify-otp",
            "/auth/introspect",
            //quiz
            "/quiz/quiz/**",
            "/quiz/document/**",
            "/quiz/file/upload"
    );

    private static final List<String> PUBLIC_WILDCARD_PATTERNS = List.of(
        "/auth/**",
        "/quiz/quiz/**",
        "/quiz/document/**"
    );

    public boolean matches(String path) {
        return PUBLIC_EXACT_PATHS.contains(path) || PUBLIC_WILDCARD_PATTERNS.stream().anyMatch(p -> pathMatcher.match(p, path));
    }
}
