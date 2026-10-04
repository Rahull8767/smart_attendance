import re

path = 'android-app/app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

new_activities = '''
        <activity android:name=".ui.manager.EmployeeManagementActivity" />
        <activity android:name=".ui.manager.SiteManagementActivity" />
'''

if 'EmployeeManagementActivity' not in content:
    content = content.replace('</application>', new_activities + '</application>')
    with open(path, 'w') as f:
        f.write(content)
