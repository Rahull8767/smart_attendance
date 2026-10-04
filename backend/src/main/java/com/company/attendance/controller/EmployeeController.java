package com.company.attendance.controller;

import com.company.attendance.entity.Employee;
import com.company.attendance.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        employee.setStatus("ACTIVE");
        return ResponseEntity.ok(employeeRepository.save(employee));
    }
    
    @PutMapping("/{id}/assign-site")
    public ResponseEntity<Employee> assignSite(@PathVariable UUID id, @RequestParam UUID siteId) {
        return employeeRepository.findById(id).map(emp -> {
            emp.setAssignedSiteId(siteId);
            return ResponseEntity.ok(employeeRepository.save(emp));
        }).orElse(ResponseEntity.notFound().build());
    }
}
