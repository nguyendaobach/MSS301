package mss.mindmap.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.http.HttpMethod;

import java.util.List;


@Component
public class RoleAccessMatcher {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<RoleAccessRule> RULES = List.of(
            // Admin Service - User Management
            // GET - View users, stats (ADMIN, SUPER_ADMIN only)
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/admin/users", "/admin/users/*", "/admin/users/role/*", "/admin/stats"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // GET - Health check and auth info (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/admin/health", "/admin/auth/**"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // POST - Create users, extract roles (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/admin/users", "/admin/auth/roles/extract"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // PUT - Update users (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/admin/users/*"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // DELETE - Delete users (SUPER_ADMIN only - highest permission)
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/admin/users/*"),
                    List.of("SUPER_ADMIN")),

            // PATCH - Toggle user status (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.PATCH,
                    List.of("/admin/users/*/toggle-status"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // Quiz Service
            //GET
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/quiz/quizzes/attempts/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),

            new RoleAccessRule(HttpMethod.GET,
                    List.of(
                            "/quiz/quizzes/attempts/*",
                            "/quiz/quizzes/attempts/history"
                    ),
                    List.of("STUDENT")),

            new RoleAccessRule(HttpMethod.POST,
                    List.of("/quiz/quizzes",
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
                    List.of("/mindmap/mindmap/**", "/mindmap/nodes/**", "/mindmap/edges/**"),
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
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),

            // AI Service
            //GET
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/ai/ai/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
            //GET
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/ai/ai/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),
            //GET
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/vectors/vectors/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "VIEWER")),

            new RoleAccessRule(HttpMethod.POST,
                    List.of("/vectors/embed-and-store"),
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
