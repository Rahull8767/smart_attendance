import re
# Patch EmployeeDashboard layout
path = 'android-app/app/src/main/res/layout/activity_employee_dashboard.xml'
with open(path, 'r') as f:
    content = f.read()

new_buttons = '''
    <Button
        android:id="@+id/btnHistory"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="View History"
        android:layout_marginBottom="8dp"/>
        
    <Button
        android:id="@+id/btnCorrection"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Request Correction"
        android:layout_marginBottom="16dp"/>
'''

if 'btnHistory' not in content:
    content = content.replace('<Button\n        android:id="@+id/btnLogout"', new_buttons + '\n    <Button\n        android:id="@+id/btnLogout"')
    with open(path, 'w') as f:
        f.write(content)

# Patch EmployeeDashboardActivity logic
path_act = 'android-app/app/src/main/java/com/company/fieldattendance/ui/employee/EmployeeDashboardActivity.java'
with open(path_act, 'r') as f:
    content_act = f.read()

new_logic = '''
        findViewById(R.id.btnHistory).setOnClickListener(v -> {
            startActivity(new Intent(this, AttendanceHistoryActivity.class));
        });
        
        findViewById(R.id.btnCorrection).setOnClickListener(v -> {
            startActivity(new Intent(this, CorrectionRequestActivity.class));
        });
'''

if 'btnHistory' not in content_act:
    content_act = content_act.replace('btnPunchIn.setOnClickListener', new_logic + '\n        btnPunchIn.setOnClickListener')
    with open(path_act, 'w') as f:
        f.write(content_act)

# Patch Manifest
path_man = 'android-app/app/src/main/AndroidManifest.xml'
with open(path_man, 'r') as f:
    content_man = f.read()

new_activities = '''
        <activity android:name=".ui.employee.AttendanceHistoryActivity" />
        <activity android:name=".ui.employee.CorrectionRequestActivity" />
'''

if 'AttendanceHistoryActivity' not in content_man:
    content_man = content_man.replace('</application>', new_activities + '</application>')
    with open(path_man, 'w') as f:
        f.write(content_man)
