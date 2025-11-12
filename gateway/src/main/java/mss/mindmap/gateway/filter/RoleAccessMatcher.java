package mss.mindmap.gateway.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.http.HttpMethod;

import java.util.List;


@Component
public class RoleAccessMatcher {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final List<RoleAccessRule> RULES = List.of(
            // Identity Service - User Profile Management
            // GET - View own profile (All authenticated users)
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/identity/users/profile", "/premium/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "SUPER_ADMIN")),

            // PUT - Update own profile (All authenticated users)
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/identity/users/profile", "/identity/users/change-password"),
                    List.of("STUDENT", "TEACHER", "ADMIN", "SUPER_ADMIN")),

            // Admin Service - All admin endpoints
            // GET - All admin endpoints (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/admin/**"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // POST - All admin endpoints (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/admin/**"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // PUT - All admin endpoints (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/admin/**"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // PATCH - All admin endpoints (ADMIN, SUPER_ADMIN)
            new RoleAccessRule(HttpMethod.PATCH,
                    List.of("/admin/**"),
                    List.of("ADMIN", "SUPER_ADMIN")),

            // DELETE - Delete operations (SUPER_ADMIN only - highest permission)
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/admin/**"),
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
            // GET - View mindmaps
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/mindmap/mindmap/**", "/mindmap/nodes/**", "/mindmap/edges/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // POST - Create mindmaps and events
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/mindmap/mindmap/**", "/mindmap/event/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // PUT/PATCH - Update mindmaps
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/mindmap/mindmap/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            new RoleAccessRule(HttpMethod.PATCH,
                    List.of("/mindmap/mindmap/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // DELETE - Delete mindmaps
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/mindmap/mindmap/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),

            // Document Service
            // GET - View documents
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // POST - Upload documents
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // PUT - Update documents
            new RoleAccessRule(HttpMethod.PUT,
                    List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // DELETE - Delete documents
            new RoleAccessRule(HttpMethod.DELETE,
                    List.of("/documents/documents/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),

            // AI Service
            // GET - View AI history
            new RoleAccessRule(HttpMethod.GET,
                    List.of("/ai/ai/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN")),
            // POST - Generate mindmap with AI
            new RoleAccessRule(HttpMethod.POST,
                    List.of("/ai/ai/**", "/premium/**"),
                    List.of("STUDENT", "TEACHER", "ADMIN"))
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
