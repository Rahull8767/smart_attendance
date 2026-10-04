package com.company.fieldattendance.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {OfflinePunch.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OfflinePunchDao offlinePunchDao();
}
