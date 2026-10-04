package com.company.attendance.service;

import java.util.List;
import java.util.UUID;

public interface FaceVerificationService {
    String enrollFace(UUID employeeId, List<Float> embedding);
    boolean verifyFace(UUID employeeId, List<Float> liveEmbedding);
}
