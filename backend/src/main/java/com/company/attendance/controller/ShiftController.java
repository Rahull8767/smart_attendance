package com.company.attendance.controller;

import com.company.attendance.entity.Shift;
import com.company.attendance.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {

    @Autowired
    private ShiftRepository shiftRepository;

    @GetMapping
    public ResponseEntity<List<Shift>> getAllShifts() {
        return ResponseEntity.ok(shiftRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Shift> createShift(@RequestBody Shift shift) {
        shift.setStatus("ACTIVE");
        return ResponseEntity.ok(shiftRepository.save(shift));
    }
}
