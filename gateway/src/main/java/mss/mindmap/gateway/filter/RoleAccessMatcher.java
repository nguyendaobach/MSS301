package mss.mindmap.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.http.HttpMethod;
import java.util.List;


@Component
public class RoleAccessMatcher {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<RoleAccessRule> RULES = List.of(
            //GET
            new RoleAccessRule(HttpMethod.GET,
                    List.of(""),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/quiz/quizs/**"),
                    List.of("TEACHER", "ADMIN")),
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/quiz/quizs/**"),
                    List.of("TEACHER", "ADMIN")),
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/quiz/quizs/**"),
                    List.of("TEACHER", "ADMIN"))
    );
    public boolean isAuthorized(String path, List<String> roles, HttpMethod method) {
        if (method == null || roles == null || roles.isEmpty()) {
            return false;
        }

        return RULES.stream()
                .filter(rule -> rule.method.equals(method))
                .anyMatch(rule ->
                        rule.url.stream().anyMatch(pattern ->
                                pathMatcher.match(pattern, path)
                        )
                                && roles.stream().anyMatch(rule.roles::contains)
                );
    }


    private static class RoleAccessRule {
        HttpMethod method;
        List<String> url;
        List<String> roles;

        RoleAccessRule(HttpMethod method, List<String> url, List<String> roles) {
            this.method = method;
            this.url = url;
            this.roles = roles;
        }
    }
}
