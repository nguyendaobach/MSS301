package com.mss301.adminservice.aspect;

import com.mss301.adminservice.annotation.RequirePermission;
import com.mss301.adminservice.annotation.RequireRole;
import com.mss301.adminservice.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuthorizationAspect {

    @Before("@annotation(com.mss301.adminservice.annotation.RequireRole)")
    public void checkRolePermission(JoinPoint joinPoint) {
        HttpServletRequest request = getCurrentRequest();
        String userRole = (String) request.getAttribute("role");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);

        String[] allowedRoles = requireRole.value();

        if (userRole == null || !Arrays.asList(allowedRoles).contains(userRole)) {
            log.warn("Access denied. User role: {}, Required roles: {}", userRole, Arrays.toString(allowedRoles));
            throw new UnauthorizedException("You don't have permission to access this resource. Required role: " + Arrays.toString(allowedRoles));
        }

        log.debug("Role check passed for user with role: {}", userRole);
    }

    @Before("@annotation(com.mss301.adminservice.annotation.RequirePermission)")
    public void checkPermission(JoinPoint joinPoint) {
        HttpServletRequest request = getCurrentRequest();
        @SuppressWarnings("unchecked")
        List<String> userPermissions = (List<String>) request.getAttribute("permissions");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);

        String[] requiredPermissions = requirePermission.value();

        if (userPermissions == null || !userPermissions.containsAll(Arrays.asList(requiredPermissions))) {
            log.warn("Access denied. User permissions: {}, Required permissions: {}", userPermissions, Arrays.toString(requiredPermissions));
            throw new UnauthorizedException("You don't have permission to access this resource. Required permission: " + Arrays.toString(requiredPermissions));
        }

        log.debug("Permission check passed for user with permissions: {}", userPermissions);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new UnauthorizedException("No request context available");
        }
        return attributes.getRequest();
    }
}

