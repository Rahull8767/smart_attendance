import re

path = 'android-app/app/src/main/java/com/company/fieldattendance/ui/employee/EmployeeDashboardActivity.java'
with open(path, 'r') as f:
    content = f.read()

# Replace the Toast in performLocationVerification with an Intent call
old_code = """                // In Phase 6 we will send this to backend. For Phase 4, we display the result locally."""
new_code = """                // Launch Face Verification if location succeeds
                Intent intent = new Intent(EmployeeDashboardActivity.this, FaceVerificationActivity.class);
                startActivity(intent);"""

if old_code in content:
    content = content.replace(old_code, new_code)
    with open(path, 'w') as f:
        f.write(content)
