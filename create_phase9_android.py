import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

base_dir = "android-app/app/src/main"
java_dir = f"{base_dir}/java/com/company/fieldattendance"

# Room Data base
create_file(f"{java_dir}/data/local/OfflinePunch.java", """
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
""")

create_file(f"{java_dir}/data/local/OfflinePunchDao.java", """
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
""")

create_file(f"{java_dir}/data/local/AppDatabase.java", """
package com.company.fieldattendance.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {OfflinePunch.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract OfflinePunchDao offlinePunchDao();
}
""")

create_file(f"{java_dir}/data/model/OfflinePunchRequest.java", """
package com.company.fieldattendance.data.model;
import java.util.List;
public class OfflinePunchRequest {
    public List<PunchRequest> offlinePunches;
    public OfflinePunchRequest(List<PunchRequest> offlinePunches) {
        this.offlinePunches = offlinePunches;
    }
}
""")

create_file(f"patch_apiservice_phase9.py", """
path = 'android-app/app/src/main/java/com/company/fieldattendance/data/api/ApiService.java'
with open(path, 'r') as f:
    content = f.read()

new_imports = '''import com.company.fieldattendance.data.model.OfflinePunchRequest;'''

new_methods = '''    @POST("/api/sync/offline-punches")
    Call<ApiResponse> syncOfflinePunches(@Body OfflinePunchRequest request);
}'''

if 'OfflinePunchRequest' not in content:
    content = content.replace('import com.company.fieldattendance.data.model.PunchRequest;', 'import com.company.fieldattendance.data.model.PunchRequest;\\n' + new_imports)
    content = content.replace('}', new_methods)
    with open(path, 'w') as f:
        f.write(content)
""")

print("Phase 9 Android script complete.")
