package com.company.attendance.controller;

import com.company.attendance.entity.FaceProfile;
import com.company.attendance.repository.FaceProfileRepository;
import com.company.attendance.service.FaceVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/face")
public class FaceController {

    @Autowired
    private FaceVerificationService faceVerificationService;
    
    @Autowired
    private FaceProfileRepository faceProfileRepository;

    private List<Float> parseEmbedding(Object raw) {
        if (raw == null) return java.util.Collections.emptyList();
        List<?> list = (List<?>) raw;
        List<Float> floats = new java.util.ArrayList<>(list.size());
        for (Object item : list) {
            if (item instanceof Number) {
                floats.add(((Number) item).floatValue());
            }
        }
        return floats;
    }

    @PostMapping("/enroll")
    public ResponseEntity<?> enrollFace(@RequestBody Map<String, Object> payload) {
        try {
            UUID employeeId = UUID.fromString((String) payload.get("employeeId"));
            List<Float> embedding = parseEmbedding(payload.get("embedding"));
            
            String status = faceVerificationService.enrollFace(employeeId, embedding);
            return ResponseEntity.ok(Map.of("success", true, "message", "Face enrolled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Enrollment failed: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyFace(@RequestBody Map<String, Object> payload) {
        try {
            UUID employeeId = UUID.fromString((String) payload.get("employeeId"));
            List<Float> embedding = parseEmbedding(payload.get("embedding"));
            
            boolean isVerified = faceVerificationService.verifyFace(employeeId, embedding);
            
            if (isVerified) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Face verified successfully"));
            } else {
                return ResponseEntity.ok(Map.of("success", false, "message", "Face verification failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Verification failed: " + e.getMessage()));
        }
    }

    @GetMapping("/profile/{employeeId}")
    public ResponseEntity<?> getFaceProfile(@PathVariable UUID employeeId) {
        boolean enrolled = faceProfileRepository.findByEmployeeId(employeeId).isPresent();
        return ResponseEntity.ok(Map.of("enrolled", enrolled, "employeeId", employeeId.toString()));
    }
}
