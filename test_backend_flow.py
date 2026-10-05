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

# 2. Get CEOs
print("2. Get CEOs")
res = requests.get(f"{base_url}/provider/ceos", headers=headers)
print("CEOs list status:", res.status_code)
print("CEOs:", res.json())

# 3. Create CEO
print("3. Create CEO")
create_payload = {
    "name": "Test CEO",
    "email": "ceotest@example.com",
    "phone": "1234567890",
    "designation": "Chief Executive Officer",
    "companyName": "Test Company",
    "industry": "IT",
    "companyPhone": "0987654321",
    "companyEmail": "contact@testcompany.com",
    "address": "123 Test St",
    "city": "Test City",
    "state": "TS",
    "postalCode": "12345",
    "loginEmail": "ceotest@example.com",
    "password": "StrongPassword123!",
    "status": "ACTIVE"
}
res = requests.post(f"{base_url}/provider/ceos", json=create_payload, headers=headers)
print("Create CEO status:", res.status_code)
if res.status_code != 200:
    print(res.text)
    exit(1)

created_ceo = res.json()
print("Created CEO:", created_ceo)
ceo_id = created_ceo["id"]

# 4. Get CEO list again
print("4. Get CEO list again")
res = requests.get(f"{base_url}/provider/ceos", headers=headers)
print("CEOs list status:", res.status_code)
print("CEOs:", res.json())

# 5. Get CEO details
print("5. Get CEO details")
res = requests.get(f"{base_url}/provider/ceos/{ceo_id}", headers=headers)
print("CEO details status:", res.status_code)
print("CEO details:", res.json())

# 6. Deactivate CEO
print("6. Deactivate CEO")
res = requests.patch(f"{base_url}/provider/ceos/{ceo_id}/status", json={"status": "INACTIVE"}, headers=headers)
print("Deactivate status:", res.status_code)
print("Deactivated CEO:", res.json())

print("All tests passed.")
