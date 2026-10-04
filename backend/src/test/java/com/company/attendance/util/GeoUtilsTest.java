package com.company.attendance.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeoUtilsTest {

    @Test
    public void testCalculateDistance_sameLocation() {
        double lat = 40.7128;
        double lon = -74.0060;
        double distance = GeoUtils.calculateDistance(lat, lon, lat, lon);
        assertTrue(distance < 1.0, "Distance to same point should be 0 meters");
    }

    @Test
    public void testCalculateDistance_differentLocations() {
        // New York (40.7128, -74.0060) to Los Angeles (34.0522, -118.2437)
        // Distance is approx 3935 km
        double nyLat = 40.7128;
        double nyLon = -74.0060;
        double laLat = 34.0522;
        double laLon = -118.2437;
        
        double distanceMeters = GeoUtils.calculateDistance(nyLat, nyLon, laLat, laLon);
        double distanceKm = distanceMeters / 1000;
        
        assertTrue(distanceKm > 3900 && distanceKm < 4000, "Distance between NY and LA should be ~3940km");
    }
}
