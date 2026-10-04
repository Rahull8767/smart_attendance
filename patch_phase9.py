path = 'android-app/app/src/main/java/com/company/fieldattendance/data/api/ApiService.java'
with open(path, 'r') as f:
    content = f.read()

new_imports = '''import com.company.fieldattendance.data.model.OfflinePunchRequest;'''

new_methods = '''    @POST("/api/sync/offline-punches")
    Call<ApiResponse> syncOfflinePunches(@Body OfflinePunchRequest request);
}'''

if 'OfflinePunchRequest' not in content:
    content = content.replace('import com.company.fieldattendance.data.model.PunchRequest;', 'import com.company.fieldattendance.data.model.PunchRequest;\n' + new_imports)
    content = content.replace('}', new_methods)
    with open(path, 'w') as f:
        f.write(content)
