package com.company.attendance.aspect;

import com.company.attendance.entity.AuditLog;
import com.company.attendance.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @AfterReturning("execution(* com.company.attendance.controller.AuthController.login(..))")
    public void logLoginAction(JoinPoint joinPoint) {
        AuditLog log = new AuditLog();
        log.setAction("LOGIN");
        
        String username = "Anonymous";
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        }
        
        log.setUsername(username);
        log.setDetails("User logged in via AuthController");
        
        auditLogRepository.save(log);
    }
}
