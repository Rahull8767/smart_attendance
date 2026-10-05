import requests
import json

base_url = "http://localhost:8080/api"

# 1. Login as Provider
print("1. Login as Provider")
login_payload = {
    "email": "provider@test.com",
    "password": "password123"
}
res = requests.post(f"{base_url}/auth/login", json=login_payload)
print("Login status:", res.status_code)
if res.status_code != 200:
    print(res.text)
    exit(1)
    
token = res.json()["accessToken"]
headers = {"Authorization": f"Bearer {token}"}
print("Login success, token received.")

# 2. Get Dashboard Metrics
print("2. Get Dashboard Metrics")
res = requests.get(f"{base_url}/provider/dashboard", headers=headers)
print("Dashboard status:", res.status_code)
print("Dashboard Metrics:", json.dumps(res.json(), indent=2))

# 3. Get Recent Activity
print("3. Get Recent Activity")
res = requests.get(f"{base_url}/provider/activity", headers=headers)
print("Activity status:", res.status_code)
print("Recent Activity:", json.dumps(res.json(), indent=2))

# 4. Get Workforce Metrics
print("4. Get Workforce Metrics")
res = requests.get(f"{base_url}/provider/workforce", headers=headers)
print("Workforce Metrics status:", res.status_code)
print("Workforce Metrics:", json.dumps(res.json(), indent=2))

# 5. Get Workforce Employees
print("5. Get Workforce Employees")
res = requests.get(f"{base_url}/provider/workforce/employees", headers=headers)
print("Workforce Employees status:", res.status_code)
print("Workforce Employees:", json.dumps(res.json(), indent=2))

# 6. Get Analytics
print("6. Get Analytics")
res = requests.get(f"{base_url}/provider/analytics", headers=headers)
print("Analytics status:", res.status_code)
print("Analytics:", json.dumps(res.json(), indent=2))

# 7. Get Alerts
print("7. Get Alerts")
res = requests.get(f"{base_url}/provider/alerts", headers=headers)
print("Alerts status:", res.status_code)
print("Alerts:", json.dumps(res.json(), indent=2))

