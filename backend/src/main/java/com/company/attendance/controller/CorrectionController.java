package com.company.attendance.controller;

import com.company.attendance.entity.AttendanceCorrection;
import com.company.attendance.repository.AttendanceCorrectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/corrections")
public class CorrectionController {

    @Autowired
    private AttendanceCorrectionRepository correctionRepository;

    @PostMapping("/request")
    public ResponseEntity<AttendanceCorrection> requestCorrection(@RequestBody AttendanceCorrection correction) {
        correction.setStatus("PENDING");
        return ResponseEntity.ok(correctionRepository.save(correction));
    }

    @PutMapping("/{id}/review")
    public ResponseEntity<AttendanceCorrection> reviewCorrection(@PathVariable UUID id, 
                                                                 @RequestParam String status, 
                                                                 @RequestParam UUID managerId,
                                                                 @RequestParam(required=false) String comment) {
        return correctionRepository.findById(id).map(correction -> {
            correction.setStatus(status);
            correction.setReviewedBy(managerId);
            correction.setReviewedAt(LocalDateTime.now());
            correction.setManagerComment(comment);
            return ResponseEntity.ok(correctionRepository.save(correction));
        }).orElse(ResponseEntity.notFound().build());
    }
}
