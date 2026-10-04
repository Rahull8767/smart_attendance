import re
path = 'android-app/app/src/main/java/com/company/fieldattendance/ui/employee/EmployeeDashboardActivity.java'
with open(path, 'r') as f:
    content = f.read()

old_code = '''                // Launch Face Verification if location succeeds
                Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                startActivity(intent);'''
                
new_code = '''                Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                intent.putExtra("LAT", location.getLatitude());
                intent.putExtra("LON", location.getLongitude());
                intent.putExtra("ACC", location.getAccuracy());
                startActivity(intent);'''

if old_code in content:
    content = content.replace(old_code, new_code)
    with open(path, 'w') as f:
        f.write(content)

# Update ApiService
path_api = 'android-app/app/src/main/java/com/company/fieldattendance/data/api/ApiService.java'
with open(path_api, 'r') as f:
    content_api = f.read()

new_imports = '''import com.company.fieldattendance.data.model.PunchRequest;
import com.company.fieldattendance.data.model.ApiResponse;'''

new_methods = '''    @POST("/api/attendance/punch-in")
    Call<ApiResponse> punchIn(@Body PunchRequest request);

    @POST("/api/attendance/punch-out")
    Call<ApiResponse> punchOut(@Body PunchRequest request);
}'''

if 'PunchRequest' not in content_api:
    content_api = content_api.replace('import retrofit2.http.POST;', 'import retrofit2.http.POST;\\n' + new_imports)
    content_api = content_api.replace('}', new_methods)
    with open(path_api, 'w') as f:
        f.write(content_api)
