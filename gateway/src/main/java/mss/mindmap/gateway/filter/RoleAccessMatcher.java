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
                    List.of("/quiz/quizzes/",
                            "/quiz/quizzes/{quizId}/answer-key",
                            "/quiz/flies/upload"),
                    List.of("TEACHER", "ADMIN")),

            new RoleAccessRule(HttpMethod.POST,
                    List.of("/quiz/quizzes/{quizId}/attempts/{attemptId}/submit",
                            "/quiz/quizzes/{quizId}/attempts"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),


            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/quiz/quizzes/**"),
                    List.of("TEACHER", "ADMIN")),
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/quiz/quizzes/**"),
                    List.of("TEACHER", "ADMIN")),


            // MindMap Service
            //GET
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/mindmap/mindmap/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
            //GET
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/mindmap/mindmap/**",
                            "/mindmap/event/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
            //GET
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/mindmap/mindmap/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
            //GET
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/mindmap/mindmap/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),

            // Document Service
            //GET
            new RoleAccessRule(HttpMethod.GET,
                               List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
                            //GET
                            new RoleAccessRule(HttpMethod.POST,
                                               List.of("/documents/documents/****"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
                            //GET
                            new RoleAccessRule(HttpMethod.PUT,
                                               List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
                            //GET
                            new RoleAccessRule(HttpMethod.DELETE,
                                               List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER"))
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
