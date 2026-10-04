import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

create_file(f"{base_dir}/entity/FaceProfile.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "face_profiles")
public class FaceProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID employeeId;
    private String templateReference; // ID referencing external AI service
    private String enrollmentStatus;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
""")

create_file(f"{base_dir}/repository/FaceProfileRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.FaceProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface FaceProfileRepository extends JpaRepository<FaceProfile, UUID> {
    Optional<FaceProfile> findByEmployeeId(UUID employeeId);
}
""")

create_file(f"{base_dir}/service/FaceVerificationService.java", """
package com.company.attendance.service;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface FaceVerificationService {
    String enrollFace(UUID employeeId, MultipartFile faceImage);
    double verifyFace(UUID employeeId, MultipartFile liveImage);
}
""")

create_file(f"{base_dir}/service/impl/MockFaceVerificationService.java", """
package com.company.attendance.service.impl;

import com.company.attendance.service.FaceVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
public class MockFaceVerificationService implements FaceVerificationService {
    
    @Override
    public String enrollFace(UUID employeeId, MultipartFile faceImage) {
        // In a real scenario, this calls AWS Rekognition or similar SDK
        return "MOCK_TEMPLATE_" + employeeId.toString();
    }

    @Override
    public double verifyFace(UUID employeeId, MultipartFile liveImage) {
        // Mock returning 99% confidence
        return 99.5;
    }
}
""")

create_file(f"{base_dir}/controller/FaceController.java", """
package com.company.attendance.controller;

import com.company.attendance.entity.FaceProfile;
import com.company.attendance.repository.FaceProfileRepository;
import com.company.attendance.service.FaceVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/face")
public class FaceController {

    @Autowired
    private FaceVerificationService faceVerificationService;
    
    @Autowired
    private FaceProfileRepository faceProfileRepository;

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollFace(@RequestParam("employeeId") UUID employeeId, 
                                        @RequestParam("image") MultipartFile image) {
        String templateRef = faceVerificationService.enrollFace(employeeId, image);
        
        FaceProfile profile = new FaceProfile();
        profile.setEmployeeId(employeeId);
        profile.setTemplateReference(templateRef);
        profile.setEnrollmentStatus("ENROLLED");
        faceProfileRepository.save(profile);
        
        return ResponseEntity.ok(Map.of("success", true, "message", "Face enrolled successfully"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyFace(@RequestParam("employeeId") UUID employeeId, 
                                        @RequestParam("image") MultipartFile image) {
                                            
        if (faceProfileRepository.findByEmployeeId(employeeId).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "No face profile found for employee"));
        }
        
        double confidence = faceVerificationService.verifyFace(employeeId, image);
        
        if (confidence > 90.0) {
            return ResponseEntity.ok(Map.of("success", true, "confidence", confidence, "message", "Face verified successfully"));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "confidence", confidence, "message", "Face verification failed"));
        }
    }
}
""")

print("Phase 5 Backend script complete.")
