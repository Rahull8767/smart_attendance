import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "backend/src/main/java/com/company/attendance"

create_file(f"{base_dir}/util/GeoUtils.java", """
package com.company.attendance.util;

public class GeoUtils {
    // Calculates distance in meters using Haversine formula
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
""")

create_file(f"{base_dir}/dto/LocationVerificationRequest.java", """
package com.company.attendance.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class LocationVerificationRequest {
    private UUID employeeId;
    private Double latitude;
    private Double longitude;
    private Float accuracy;
    private Boolean isMockLocation;
}
""")

create_file(f"{base_dir}/dto/LocationVerificationResponse.java", """
package com.company.attendance.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationVerificationResponse {
    private boolean verified;
    private String status;
    private String message;
    private double calculatedDistance;
}
""")

create_file(f"{base_dir}/controller/AttendanceController.java", """
package com.company.attendance.controller;

import com.company.attendance.dto.LocationVerificationRequest;
import com.company.attendance.dto.LocationVerificationResponse;
import com.company.attendance.entity.Employee;
import com.company.attendance.entity.WorkSite;
import com.company.attendance.repository.EmployeeRepository;
import com.company.attendance.repository.WorkSiteRepository;
import com.company.attendance.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkSiteRepository workSiteRepository;

    @PostMapping("/verify-location")
    public ResponseEntity<LocationVerificationResponse> verifyLocation(@RequestBody LocationVerificationRequest request) {
        if (request.getIsMockLocation() != null && request.getIsMockLocation()) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "MOCK_LOCATION_SUSPECTED", "Mock location detected", 0));
        }
        if (request.getAccuracy() != null && request.getAccuracy() > 150) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "LOW_ACCURACY", "GPS accuracy is too low", 0));
        }

        Optional<Employee> employeeOpt = employeeRepository.findById(request.getEmployeeId());
        if (employeeOpt.isEmpty() || employeeOpt.get().getAssignedSiteId() == null) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "NO_SITE_ASSIGNED", "No assigned work site", 0));
        }

        Optional<WorkSite> siteOpt = workSiteRepository.findById(employeeOpt.get().getAssignedSiteId());
        if (siteOpt.isEmpty()) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "SITE_NOT_FOUND", "Assigned site not found", 0));
        }

        WorkSite site = siteOpt.get();
        if (site.getLatitude() == null || site.getLongitude() == null) {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "SITE_CONFIG_ERROR", "Site coordinates are not configured", 0));
        }

        double distance = GeoUtils.calculateDistance(request.getLatitude(), request.getLongitude(), site.getLatitude(), site.getLongitude());
        int radius = site.getGeofenceRadius() != null ? site.getGeofenceRadius() : 100;

        if (distance <= radius) {
            return ResponseEntity.ok(new LocationVerificationResponse(true, "VERIFIED", "Location verified successfully", distance));
        } else {
            return ResponseEntity.ok(new LocationVerificationResponse(false, "OUTSIDE_GEOFENCE", "You are " + (int)distance + " meters away from the site", distance));
        }
    }
}
""")

print("Phase 4 Backend script complete.")
