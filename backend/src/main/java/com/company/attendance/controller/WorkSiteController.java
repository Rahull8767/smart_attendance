package com.company.attendance.controller;

import com.company.attendance.entity.WorkSite;
import com.company.attendance.repository.WorkSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sites")
public class WorkSiteController {

    @Autowired
    private WorkSiteRepository workSiteRepository;

    @GetMapping
    public ResponseEntity<List<WorkSite>> getAllSites() {
        return ResponseEntity.ok(workSiteRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<WorkSite> createSite(@RequestBody WorkSite site) {
        site.setStatus("ACTIVE");
        return ResponseEntity.ok(workSiteRepository.save(site));
    }
}
