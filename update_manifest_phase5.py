path = 'android-app/app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

new_activity = '        <activity android:name=".ui.employee.FaceVerificationActivity" />\n'
if 'FaceVerificationActivity' not in content:
    content = content.replace('</application>', new_activity + '</application>')
    with open(path, 'w') as f:
        f.write(content)
