import os
import re

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

create_file(f"{base_dir}/entity/AuditLog.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String action;
    private String username;
    private String details;
    private String ipAddress;
    
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}
""")

create_file(f"{base_dir}/repository/AuditLogRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}
""")

create_file(f"{base_dir}/exception/GlobalExceptionHandler.java", """
package com.company.attendance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "timestamp", LocalDateTime.now(),
            "status", 500,
            "error", "Internal Server Error",
            "message", ex.getMessage()
        ));
    }
}
""")

create_file(f"{base_dir}/config/MethodSecurityConfig.java", """
package com.company.attendance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
""")

create_file(f"{base_dir}/aspect/AuditLogAspect.java", """
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
""")

# Patch POM XML to include AOP
pom_path = "backend/pom.xml"
with open(pom_path, 'r') as f:
    pom_content = f.read()

aop_dep = '''
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
'''

if 'spring-boot-starter-aop' not in pom_content:
    pom_content = pom_content.replace('</dependencies>', aop_dep + '    </dependencies>')
    with open(pom_path, 'w') as f:
        f.write(pom_content)

print("Phase 11 Backend script complete.")
