package com.company.fieldattendance.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface OfflinePunchDao {
    @Insert
    void insertPunch(OfflinePunch punch);

    @Query("SELECT * FROM offline_punches")
    List<OfflinePunch> getAllPunches();

    @Delete
    void deletePunches(List<OfflinePunch> punches);
}
