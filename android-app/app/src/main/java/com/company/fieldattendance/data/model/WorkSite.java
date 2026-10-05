package com.company.fieldattendance.data.model;

import java.util.UUID;

public class WorkSite {
    public UUID id;
    public String name;
    public String address;
    public Double latitude;
    public Double longitude;
    public Double altitude;
    public Integer geofenceRadius;
    public String status;

    public WorkSite() {}

    public WorkSite(String name, String address, Double latitude, Double longitude, Double altitude, Integer geofenceRadius) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.geofenceRadius = geofenceRadius;
        this.status = "ACTIVE";
    }
}
