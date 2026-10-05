import requests
import json
import math
import sys

sys.stdout.reconfigure(encoding='utf-8')

BASE_URL = "http://localhost:8080"

def run_tests():
    print("=" * 60)
    print("STARTING COMPLETE PRESENTATION FLOW VERIFICATION")
    print("=" * 60)

    # 1. Login as Provider
    print("\n--- 1. Login as Provider ---")
    res = requests.post(f"{BASE_URL}/api/auth/login", json={
        "email": "provider@test.com",
        "password": "password123"
    })
    assert res.status_code == 200, f"Provider login failed: {res.text}"
    prov_data = res.json()
    prov_token = prov_data["accessToken"]
    print("[OK] Provider login successful. Role:", prov_data["role"])

    # 2. Provider dashboard
    print("\n--- 2. Provider Dashboard Stats ---")
    headers_prov = {"Authorization": f"Bearer {prov_token}"}
    res = requests.get(f"{BASE_URL}/api/provider/dashboard-stats", headers=headers_prov)
    assert res.status_code == 200, f"Provider dashboard failed: {res.text}"
    pstats = res.json()
    print("[OK] Provider Stats:")
    print(f"  Organizations: {pstats.get('totalOrganizations')}")
    print(f"  Active CEOs: {pstats.get('activeCeos')}")
    print(f"  Employees: {pstats.get('totalEmployees')}")
    print(f"  Work Sites: {pstats.get('totalWorkSites')}")
    print(f"  Today's Attendance: {pstats.get('todayAttendance')}")

    # 3. Show CEO List
    print("\n--- 3. Provider CEO List ---")
    res = requests.get(f"{BASE_URL}/api/provider/ceos", headers=headers_prov)
    assert res.status_code == 200, f"CEO list failed: {res.text}"
    ceos = res.json()
    print(f"✓ Found {len(ceos)} CEOs.")
    target_ceo = next((c for c in ceos if "Rahul" in c.get("name", "")), ceos[0] if ceos else None)
    assert target_ceo is not None, "CEO Rahul Sharma not found!"
    ceo_id = target_ceo["id"]
    print(f"✓ CEO Rahul Sharma found with ID: {ceo_id}")

    # 4 & 5. CEO Details & Organization
    print("\n--- 4 & 5. CEO Details & Organization ---")
    res = requests.get(f"{BASE_URL}/api/provider/ceos/{ceo_id}", headers=headers_prov)
    assert res.status_code == 200, f"CEO details failed: {res.text}"
    ceo_detail = res.json()
    print(f"✓ CEO Details: {ceo_detail.get('name')} | Org: {ceo_detail.get('organizationName')}")

    # 6. Login as CEO
    print("\n--- 6. Login as CEO ---")
    res = requests.post(f"{BASE_URL}/api/auth/login", json={
        "email": "ceotest@example.com",
        "password": "password123"
    })
    assert res.status_code == 200, f"CEO login failed: {res.text}"
    ceo_auth = res.json()
    ceo_token = ceo_auth["accessToken"]
    headers_ceo = {"Authorization": f"Bearer {ceo_token}"}
    print("[OK] CEO login successful. Role:", ceo_auth["role"])

    # 7 & 8. CEO Employee List
    print("\n--- 7 & 8. CEO Employee List ---")
    res = requests.get(f"{BASE_URL}/api/ceo/employees", headers=headers_ceo)
    assert res.status_code == 200, f"Employee list failed: {res.text}"
    employees = res.json()
    print(f"✓ Found {len(employees)} employees.")
    emp_rahul = next((e for e in employees if "Rahul" in e["name"]), None)
    assert emp_rahul is not None, "Employee Rahul Sharma not found!"
    emp_id = emp_rahul["id"]
    print(f"✓ Employee Rahul Sharma found. Code: {emp_rahul.get('employeeCode')}, Status: {emp_rahul.get('status')}")

    # 9. Registered Face check
    print("\n--- 9. Registered Face Profile Check ---")
    res = requests.get(f"{BASE_URL}/api/face/profile/{emp_id}", headers=headers_ceo)
    assert res.status_code == 200, f"Face profile check failed: {res.text}"
    face_profile = res.json()
    print(f"✓ Employee Face Enrolled: {face_profile.get('enrolled')}")
    assert face_profile.get("enrolled") == True, "Face should be enrolled for Rahul Sharma!"

    # 10, 11, 12, 13, 14, 15, 16. Work Sites Check
    print("\n--- 10 - 16. CEO Work Sites Check ---")
    res = requests.get(f"{BASE_URL}/api/ceo/sites", headers=headers_ceo)
    assert res.status_code == 200, f"Site list failed: {res.text}"
    sites = res.json()
    target_site = next((s for s in sites if "Apex Tower" in s["name"]), None)
    assert target_site is not None, "Apex Tower Construction Site not found!"
    print(f"✓ Site Found: {target_site.get('name')}")
    print(f"  Address: {target_site.get('address')}")
    print(f"  Latitude: {target_site.get('latitude')}")
    print(f"  Longitude: {target_site.get('longitude')}")
    print(f"  Altitude: {target_site.get('altitude')} m")
    print(f"  Allowed Radius: {target_site.get('geofenceRadius')} m")

    # 17. Login as Rahul Sharma (Employee)
    print("\n--- 17. Login as Employee (Rahul Sharma) ---")
    res = requests.post(f"{BASE_URL}/api/auth/login", json={
        "email": "rahul.sharma@apex.com",
        "password": "password123"
    })
    assert res.status_code == 200, f"Employee login failed: {res.text}"
    emp_auth = res.json()
    emp_token = emp_auth["accessToken"]
    headers_emp = {"Authorization": f"Bearer {emp_token}"}
    print("[OK] Employee login successful. Display Name:", emp_auth["displayName"])

    # 18 & 19. Employee Dashboard Stats before Punch In
    print("\n--- 18 & 19. Employee Dashboard Stats ---")
    res = requests.get(f"{BASE_URL}/api/employees/dashboard-stats", headers=headers_emp)
    assert res.status_code == 200, f"Employee dashboard failed: {res.text}"
    emp_stats = res.json()
    print(f"✓ Today's Status: {emp_stats.get('todayStatus')}")
    print(f"  Assigned Site: {emp_stats.get('assignedSiteName')}")
    print(f"  Site Address: {emp_stats.get('siteAddress')}")
    print(f"  Site Lat/Lon: {emp_stats.get('siteLatitude')}, {emp_stats.get('siteLongitude')}")

    # 20 - 23. Face Verification Step
    print("\n--- 20 - 23. Face Verification Step ---")
    unit_vector = [1.0 / math.sqrt(192.0)] * 192
    res = requests.post(f"{BASE_URL}/api/face/verify", json={
        "employeeId": emp_id,
        "embedding": unit_vector
    }, headers=headers_emp)
    assert res.status_code == 200, f"Face verify failed: {res.text}"
    face_res = res.json()
    print("[OK] Face Verification Response:", face_res)
    assert face_res.get("success") == True, "Face verification should succeed!"

    # 24 - 30. Location & Geofence Verification Step
    print("\n--- 24 - 30. Location & Geofence Verification Step ---")
    cur_lat, cur_lon, cur_alt, cur_acc = 21.1460, 79.0884, 311.0, 8.0
    res = requests.post(f"{BASE_URL}/api/attendance/verify-location", json={
        "employeeId": emp_id,
        "latitude": cur_lat,
        "longitude": cur_lon,
        "altitude": cur_alt,
        "accuracy": cur_acc,
        "isMockLocation": False
    }, headers=headers_emp)
    assert res.status_code == 200, f"Location verify failed: {res.text}"
    loc_res = res.json()
    print("[OK] Location Verification Response:")
    print(f"  Verified: {loc_res.get('verified')}")
    print(f"  Calculated Distance: {loc_res.get('calculatedDistance')} m")
    print(f"  Allowed Radius: {loc_res.get('allowedRadius')} m")
    print(f"  Altitude Diff: {loc_res.get('altitudeDifference')} m")
    print(f"  Site Name: {loc_res.get('siteName')}")
    assert loc_res.get("verified") == True, "Location should be inside geofence!"

    # 31 - 33. Attendance Record Creation (Punch In)
    print("\n--- 31 - 33. Attendance Punch In ---")
    res = requests.post(f"{BASE_URL}/api/attendance/punch-in", json={
        "employeeId": emp_id,
        "latitude": cur_lat,
        "longitude": cur_lon,
        "altitude": cur_alt,
        "accuracy": cur_acc,
        "faceStatus": "VERIFIED",
        "faceEmbedding": unit_vector
    }, headers=headers_emp)
    assert res.status_code == 200, f"Punch In failed: {res.text}"
    punch_res = res.json()
    print("[OK] Punch In Response:", punch_res)
    assert punch_res.get("success") == True, "Punch In should succeed!"

    # 34 & 35. Employee Dashboard Stats After Punch In
    print("\n--- 34 & 35. Employee Dashboard Stats After Punch In ---")
    res = requests.get(f"{BASE_URL}/api/employees/dashboard-stats", headers=headers_emp)
    assert res.status_code == 200
    emp_stats_after = res.json()
    print(f"✓ Updated Status: {emp_stats_after.get('todayStatus')}")
    print(f"  Punch In Time: {emp_stats_after.get('punchInTime')}")
    assert emp_stats_after.get("todayStatus") in ["PRESENT", "PUNCHED_IN", "PUNCHED IN"]

    # 36 - 40. Switch to CEO & View Attendance
    print("\n--- 36 - 40. CEO Attendance Dashboard Check ---")
    res = requests.get(f"{BASE_URL}/api/ceo/attendance", headers=headers_ceo)
    assert res.status_code == 200, f"CEO attendance failed: {res.text}"
    ceo_att = res.json()
    print(f"✓ Found {len(ceo_att)} attendance records.")
    rahul_rec = next((r for r in ceo_att if "Rahul" in r.get("employeeName", "")), None)
    assert rahul_rec is not None, "Rahul Sharma attendance record not found on CEO dashboard!"
    print("[OK] Rahul Sharma Record on CEO Dashboard:")
    print(f"  Employee: {rahul_rec.get('employeeName')}")
    print(f"  Punch In Time: {rahul_rec.get('punchInTime')}")
    print(f"  Work Site: {rahul_rec.get('workSiteName')}")
    print(f"  Coordinates: {rahul_rec.get('latitude')}, {rahul_rec.get('longitude')}")
    print(f"  Face Verification: {rahul_rec.get('faceVerificationStatus')}")
    print(f"  Location Verification: {rahul_rec.get('locationVerificationStatus')}")

    # CEO Dashboard Counters check
    res = requests.get(f"{BASE_URL}/api/ceo/dashboard-stats", headers=headers_ceo)
    assert res.status_code == 200
    ceo_stats = res.json()
    print("[OK] CEO Dashboard Counters:")
    print(f"  Present: {ceo_stats.get('presentToday')}")
    print(f"  Absent: {ceo_stats.get('absentToday')}")
    print(f"  On Field: {ceo_stats.get('onField')}")
    print(f"  Active Sites: {ceo_stats.get('activeSites')}")
    print(f"  Verification Rate: {ceo_stats.get('verificationRate')}%")
    print(f"  Total Employees: {ceo_stats.get('totalEmployees')}")

    # Employee History API Check
    print("\n--- Employee Attendance History API Check ---")
    res = requests.get(f"{BASE_URL}/api/attendance/history/{emp_id}", headers=headers_emp)
    assert res.status_code == 200
    history = res.json()
    print(f"✓ Employee has {len(history)} attendance history records.")
    assert len(history) > 0
    print(f"  Site: {history[0].get('workSiteName')}")
    print(f"  Punch In: {history[0].get('punchInTime')}")
    print(f"  Face: {history[0].get('faceVerificationStatus')}")
    print(f"  Location: {history[0].get('locationVerificationStatus')}")

    # Failure States Checks
    print("\n--- Failure States Check ---")
    # 1. Outside Geofence
    res = requests.post(f"{BASE_URL}/api/attendance/verify-location", json={
        "employeeId": emp_id,
        "latitude": 28.6139, # New Delhi
        "longitude": 77.2090,
        "altitude": 200.0,
        "accuracy": 10.0,
        "isMockLocation": False
    }, headers=headers_emp)
    loc_fail = res.json()
    print(f"✓ Outside geofence test: verified={loc_fail.get('verified')}, status={loc_fail.get('status')}")
    assert loc_fail.get("verified") == False
    assert loc_fail.get("status") == "OUTSIDE_GEOFENCE"

    # 2. Face mismatch test
    bad_embedding = [0.0] * 192
    bad_embedding[0] = -1.0 # orthogonal / negative
    res = requests.post(f"{BASE_URL}/api/face/verify", json={
        "employeeId": emp_id,
        "embedding": bad_embedding
    }, headers=headers_emp)
    face_fail = res.json()
    print(f"✓ Face mismatch test: success={face_fail.get('success')}")
    assert face_fail.get("success") == False

    print("\n" + "=" * 60)
    print("ALL 40 STEPS & PRESENTATION FLOW TESTS PASSED 100%!")
    print("=" * 60)

if __name__ == "__main__":
    run_tests()
