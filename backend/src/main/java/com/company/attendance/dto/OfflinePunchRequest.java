package com.company.attendance.dto;
import lombok.Data;
import java.util.List;

@Data
public class OfflinePunchRequest {
    private List<PunchRequest> offlinePunches;
}
