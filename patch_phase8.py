import re

# Patch ManagerDashboard layout
path = 'android-app/app/src/main/res/layout/activity_manager_dashboard.xml'
with open(path, 'r') as f:
    content = f.read()

new_button = '''
    <Button
        android:id="@+id/btnReports"
        android:layout_width="match_parent"
        android:layout_height="64dp"
        android:layout_marginBottom="32dp"
        android:text="View Reports &amp; Analytics" />
'''

if 'btnReports' not in content:
    content = content.replace('<Button\n        android:id="@+id/btnLogout"', new_button + '\n    <Button\n        android:id="@+id/btnLogout"')
    with open(path, 'w') as f:
        f.write(content)

# Patch ManagerDashboardActivity logic
path_act = 'android-app/app/src/main/java/com/company/fieldattendance/ui/manager/ManagerDashboardActivity.java'
with open(path_act, 'r') as f:
    content_act = f.read()

new_logic = '''
        Button btnReports = findViewById(R.id.btnReports);
        btnReports.setOnClickListener(v -> {
            startActivity(new Intent(this, ReportsActivity.class));
        });
'''

if 'ReportsActivity' not in content_act:
    content_act = content_act.replace('btnLogout.setOnClickListener', new_logic + '\n        btnLogout.setOnClickListener')
    with open(path_act, 'w') as f:
        f.write(content_act)

# Patch Manifest
path_man = 'android-app/app/src/main/AndroidManifest.xml'
with open(path_man, 'r') as f:
    content_man = f.read()

new_activity = '        <activity android:name=".ui.manager.ReportsActivity" />\n'

if 'ReportsActivity' not in content_man:
    content_man = content_man.replace('</application>', new_activity + '</application>')
    with open(path_man, 'w') as f:
        f.write(content_man)
