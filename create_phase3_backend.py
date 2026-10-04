import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

# Entities
create_file(f"{base_dir}/entity/WorkSite.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work_sites")
public class WorkSite {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID companyId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private Integer geofenceRadius;
    private String status;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
""")

create_file(f"{base_dir}/entity/Employee.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID userId;
    private UUID companyId;
    private String employeeCode;
    private String name;
    private String department;
    private String designation;
    private String status;
    
    // Using a direct association for simplicity in MVP phase
    private UUID assignedSiteId;
    private UUID assignedShiftId;
}
""")

create_file(f"{base_dir}/entity/Shift.java", """
package com.company.attendance.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "shifts")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID companyId;
    private String name;
    private String startTime;
    private String endTime;
    private Integer gracePeriodMinutes;
    private String status;
}
""")

# Repositories
create_file(f"{base_dir}/repository/WorkSiteRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.WorkSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WorkSiteRepository extends JpaRepository<WorkSite, UUID> {
    List<WorkSite> findByCompanyId(UUID companyId);
}
""")

create_file(f"{base_dir}/repository/EmployeeRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByCompanyId(UUID companyId);
    Employee findByUserId(UUID userId);
}
""")

create_file(f"{base_dir}/repository/ShiftRepository.java", """
package com.company.attendance.repository;

import com.company.attendance.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ShiftRepository extends JpaRepository<Shift, UUID> {
    List<Shift> findByCompanyId(UUID companyId);
}
""")

# Controllers
create_file(f"{base_dir}/controller/WorkSiteController.java", """
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
""")

create_file(f"{base_dir}/controller/EmployeeController.java", """
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
""")

create_file(f"{base_dir}/controller/ShiftController.java", """
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
""")

print("Phase 3 Backend files created successfully.")
