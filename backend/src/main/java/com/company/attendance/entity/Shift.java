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
