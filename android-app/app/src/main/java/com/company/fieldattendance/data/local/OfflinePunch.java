package com.company.fieldattendance.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.UUID;

@Entity(tableName = "offline_punches")
public class OfflinePunch {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String employeeId;
    public Double latitude;
    public Double longitude;
    public Float accuracy;
    public String faceStatus;
    public long timestamp;
}
