package com.company.attendance.service.impl;

import com.company.attendance.entity.FaceProfile;
import com.company.attendance.repository.FaceProfileRepository;
import com.company.attendance.service.FaceVerificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FaceVerificationServiceImpl implements FaceVerificationService {
    
    @Autowired
    private FaceProfileRepository faceProfileRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final double SIMILARITY_THRESHOLD = 0.80; // Example threshold

    @Override
    public String enrollFace(UUID employeeId, List<Float> embedding) {
        try {
            FaceProfile profile = faceProfileRepository.findByEmployeeId(employeeId)
                    .orElse(new FaceProfile());
            profile.setEmployeeId(employeeId);
            profile.setFaceEmbeddingJson(objectMapper.writeValueAsString(embedding));
            profile.setEnrollmentStatus("ENROLLED");
            
            faceProfileRepository.save(profile);
            return "SUCCESS";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize face embedding", e);
        }
    }

    @Override
    public boolean verifyFace(UUID employeeId, List<Float> liveEmbedding) {
        FaceProfile profile = faceProfileRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("No face enrolled for employee"));
                
        try {
            List<Float> storedEmbedding = objectMapper.readValue(profile.getFaceEmbeddingJson(), new TypeReference<List<Float>>() {});
            double similarity = cosineSimilarity(storedEmbedding, liveEmbedding);
            return similarity >= SIMILARITY_THRESHOLD;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse stored face embedding", e);
        }
    }
    
    private double cosineSimilarity(List<Float> vectorA, List<Float> vectorB) {
        if (vectorA.size() != vectorB.size()) return 0.0;
        
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
