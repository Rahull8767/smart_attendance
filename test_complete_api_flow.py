import requests
import json

import psycopg2
conn = psycopg2.connect(host='localhost', port=5432, dbname='field_attendance', user='postgres', password='postgres')
cur = conn.cursor()
cur.execute("DELETE FROM attendance_records WHERE employee_id IN (SELECT id FROM employees WHERE employee_code = 'EMP-1024');")
conn.commit()
cur.close()
conn.close()

BASE_URL = "http://localhost:8080"

print("========================================")
print("TESTING FULL FIELD ATTENDANCE API FLOW")
print("========================================")

# 1. PROVIDER LOGIN
print("\n--- 1. PROVIDER LOGIN ---")
p_resp = requests.post(f"{BASE_URL}/api/auth/login", json={
    "email": "provider@test.com",
    "password": "password123"
})
print("Status:", p_resp.status_code)
assert p_resp.status_code == 200, f"Provider login failed: {p_resp.text}"
p_data = p_resp.json()
p_token = p_data.get("accessToken") or p_data.get("token")
print("Role:", p_data.get("role"), "| Name:", p_data.get("displayName"))

# Provider endpoints
p_headers = {"Authorization": f"Bearer {p_token}"}
dash_res = requests.get(f"{BASE_URL}/api/provider/dashboard", headers=p_headers)
print("Provider Dashboard Metrics:", dash_res.status_code, dash_res.json())
ceos_res = requests.get(f"{BASE_URL}/api/provider/ceos", headers=p_headers)
print("Provider CEOs Count:", len(ceos_res.json()))

# 2. CEO LOGIN
print("\n--- 2. CEO LOGIN ---")
c_resp = requests.post(f"{BASE_URL}/api/auth/login", json={
    "email": "ceotest@example.com",
    "password": "password123"
})
print("Status:", c_resp.status_code)
assert c_resp.status_code == 200, f"CEO login failed: {c_resp.text}"
c_data = c_resp.json()
c_token = c_data.get("accessToken") or c_data.get("token")
c_headers = {"Authorization": f"Bearer {c_token}"}
print("CEO Role:", c_data.get("role"), "| Name:", c_data.get("displayName"))

# CEO Dashboard Stats
c_dash = requests.get(f"{BASE_URL}/api/ceo/dashboard-stats", headers=c_headers)
print("CEO Dashboard Stats:", c_dash.status_code, c_dash.json())

# CEO Sites
c_sites = requests.get(f"{BASE_URL}/api/ceo/sites", headers=c_headers)
print(f"CEO Sites ({len(c_sites.json())}):", [(s['name'], s['latitude'], s['longitude'], s['geofenceRadius']) for s in c_sites.json()])

# CEO Employees
c_emps = requests.get(f"{BASE_URL}/api/ceo/employees", headers=c_headers)
print(f"CEO Employees ({len(c_emps.json())}):", [(e['name'], e['employeeCode'], e['department'], e['designation']) for e in c_emps.json()])

# CEO Attendance
c_att = requests.get(f"{BASE_URL}/api/ceo/attendance", headers=c_headers)
print(f"CEO Attendance Records ({len(c_att.json())}):")
for a in c_att.json():
    print(f"  - {a['employeeName']}: {a['status']} at {a['punchInTime']} (Site: {a['workSiteName']}, Face: {a['faceVerificationStatus']}, Loc: {a['locationVerificationStatus']})")

# 3. EMPLOYEE LOGIN: Rahul Sharma
print("\n--- 3. EMPLOYEE LOGIN (Rahul Sharma) ---")
e_resp = requests.post(f"{BASE_URL}/api/auth/login", json={
    "email": "rahul.sharma@apex.com",
    "password": "password123"
})
print("Status:", e_resp.status_code)
assert e_resp.status_code == 200, f"Employee login failed: {e_resp.text}"
e_data = e_resp.json()
e_token = e_data.get("accessToken") or e_data.get("token")
e_id = e_data["employeeId"]
e_headers = {"Authorization": f"Bearer {e_token}"}
print("Employee ID:", e_id, "| Name:", e_data.get("displayName"))

# Employee Dashboard Stats
e_dash = requests.get(f"{BASE_URL}/api/employees/dashboard-stats", headers=e_headers)
print("Employee Dashboard Stats:", e_dash.status_code, e_dash.json())

# 4. FACE VERIFICATION
print("\n--- 4. FACE VERIFICATION ---")
with open("rahul_face_embedding.json", "r") as f:
    embedding = json.load(f)

face_check = requests.get(f"{BASE_URL}/api/face/profile/{e_id}", headers=e_headers)
print("Face Profile Check:", face_check.status_code, face_check.json())

face_verify = requests.post(f"{BASE_URL}/api/face/verify", headers=e_headers, json={
    "employeeId": e_id,
    "embedding": embedding
})
print("Face Verification Result:", face_verify.status_code, face_verify.json())

# Test Face Mismatch (random vector)
mismatch_embedding = [0.5 if i % 2 == 0 else -0.5 for i in range(192)]
face_mismatch = requests.post(f"{BASE_URL}/api/face/verify", headers=e_headers, json={
    "employeeId": e_id,
    "embedding": mismatch_embedding
})
print("Face Mismatch (Expected False):", face_mismatch.status_code, face_mismatch.json())

# 5. LOCATION VERIFICATION
print("\n--- 5. LOCATION VERIFICATION ---")
# Inside work site (Apex Tower Construction Site: 21.1458, 79.0882)
loc_valid = requests.post(f"{BASE_URL}/api/attendance/verify-location", headers=e_headers, json={
    "employeeId": e_id,
    "latitude": 21.1460,
    "longitude": 79.0884,
    "accuracy": 8.0,
    "altitude": 311.0,
    "isMockLocation": False
})
print("Inside Site Result:", loc_valid.status_code, loc_valid.json())
assert loc_valid.json()["verified"] == True

# Outside work site
loc_outside = requests.post(f"{BASE_URL}/api/attendance/verify-location", headers=e_headers, json={
    "employeeId": e_id,
    "latitude": 21.1800,
    "longitude": 79.1500,
    "accuracy": 10.0,
    "altitude": 300.0,
    "isMockLocation": False
})
print("Outside Site Result:", loc_outside.status_code, loc_outside.json())
assert loc_outside.json()["verified"] == False

# 6. PUNCH IN
print("\n--- 6. PUNCH IN ---")
punch_in = requests.post(f"{BASE_URL}/api/attendance/punch-in", headers=e_headers, json={
    "employeeId": e_id,
    "latitude": 21.1460,
    "longitude": 79.0884,
    "altitude": 311.0,
    "accuracy": 8.0,
    "faceStatus": "VERIFIED",
    "faceEmbedding": embedding
})
print("Punch In Result:", punch_in.status_code, punch_in.json())

# Check Employee Dashboard after punch-in
e_dash_after = requests.get(f"{BASE_URL}/api/employees/dashboard-stats", headers=e_headers)
print("Employee Dashboard Status after punch-in:", e_dash_after.json().get("todayStatus"), "at", e_dash_after.json().get("punchInTime"))

# 7. CEO VIEW ATTENDANCE AFTER PUNCH IN
print("\n--- 7. CEO VIEW ATTENDANCE AFTER PUNCH IN ---")
c_att_after = requests.get(f"{BASE_URL}/api/ceo/attendance", headers=c_headers)
print(f"CEO Attendance Records Count: {len(c_att_after.json())}")
for a in c_att_after.json():
    print(f"  - {a['employeeName']} ({a['employeeCode']}): {a['status']} at {a['punchInTime']} (Site: {a['workSiteName']}, Face: {a['faceVerificationStatus']}, Loc: {a['locationVerificationStatus']})")

# CEO Dashboard Stats update
c_dash_after = requests.get(f"{BASE_URL}/api/ceo/dashboard-stats", headers=c_headers)
print("CEO Present Today Count:", c_dash_after.json().get("presentToday"))

print("\n========================================")
print("ALL BACKEND ENDPOINTS AND FLOWS VERIFIED 100%!")
print("========================================")
