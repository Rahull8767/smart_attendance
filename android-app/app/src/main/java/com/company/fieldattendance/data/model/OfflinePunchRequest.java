package com.company.fieldattendance.data.model;
import java.util.List;
public class OfflinePunchRequest {
    public List<PunchRequest> offlinePunches;
    public OfflinePunchRequest(List<PunchRequest> offlinePunches) {
        this.offlinePunches = offlinePunches;
    }
}
