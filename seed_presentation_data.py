import psycopg2
import uuid
import datetime
import json
import random

# Connect to database
conn = psycopg2.connect(host='localhost', port=5432, dbname='field_attendance', user='postgres', password='postgres')
cur = conn.cursor()

# BCrypt hash for "password123"
# Standard bcrypt for password123: $2a$10$vogKis9LpbpBqf..limYtephk5lUkxx2Pa6Nk3lbzd6BrNWCvHeeC
BCRYPT_PASSWORD123 = '$2a$10$vogKis9LpbpBqf..limYtephk5lUkxx2Pa6Nk3lbzd6BrNWCvHeeC'

print("=== Seeding Presentation Dataset ===")

# 1. PROVIDER: FieldTrack Solutions (provider@test.com)
cur.execute("SELECT id FROM providers WHERE email = 'provider@test.com';")
row = cur.fetchone()
if row:
    provider_id = row[0]
    cur.execute("""
        UPDATE providers 
        SET company_name = 'FieldTrack Solutions', contact_person = 'Operations Admin', phone = '+91 98765 43210', status = 'ACTIVE'
        WHERE id = %s;
    """, (provider_id,))
else:
    provider_id = str(uuid.uuid4())
    cur.execute("""
        INSERT INTO providers (id, company_name, contact_person, email, phone, status, created_at)
        VALUES (%s, 'FieldTrack Solutions', 'Operations Admin', 'provider@test.com', '+91 98765 43210', 'ACTIVE', NOW());
    """, (provider_id,))

# Ensure provider user
cur.execute("SELECT id FROM users WHERE email = 'provider@test.com';")
u_row = cur.fetchone()
if u_row:
    cur.execute("UPDATE users SET password_hash = %s, provider_id = %s, role = 'ROLE_PROVIDER' WHERE email = 'provider@test.com';", (BCRYPT_PASSWORD123, provider_id))
else:
    cur.execute("""
        INSERT INTO users (id, email, password_hash, role, provider_id, created_at)
        VALUES (%s, 'provider@test.com', %s, 'ROLE_PROVIDER', %s, NOW());
    """, (str(uuid.uuid4()), BCRYPT_PASSWORD123, provider_id))

print(f"Provider seeded: FieldTrack Solutions (id={provider_id})")

# 2. ORGANIZATION: Apex Infrastructure Pvt. Ltd.
cur.execute("SELECT id FROM organizations WHERE name = 'Apex Infrastructure Pvt. Ltd.';")
org_row = cur.fetchone()
if org_row:
    org_id = org_row[0]
else:
    org_id = str(uuid.uuid4())
    cur.execute("""
        INSERT INTO organizations (id, name, industry, email, phone, address, city, state, postal_code, created_at)
        VALUES (%s, 'Apex Infrastructure Pvt. Ltd.', 'Construction & Infrastructure', 'contact@apexinfra.com', '+91 712 2554400', 'Plot 42, Civil Lines', 'Nagpur', 'Maharashtra', '440001', NOW());
    """, (org_id,))

print(f"Organization seeded: Apex Infrastructure Pvt. Ltd. (id={org_id})")

# 3. CEO: Rahul Sharma (ceotest@example.com)
cur.execute("SELECT id FROM users WHERE email = 'ceotest@example.com';")
ceo_user_row = cur.fetchone()
if ceo_user_row:
    ceo_user_id = ceo_user_row[0]
    cur.execute("UPDATE users SET password_hash = %s, provider_id = %s, role = 'ROLE_CEO' WHERE id = %s;", (BCRYPT_PASSWORD123, provider_id, ceo_user_id))
else:
    ceo_user_id = str(uuid.uuid4())
    cur.execute("""
        INSERT INTO users (id, email, password_hash, role, provider_id, created_at)
        VALUES (%s, 'ceotest@example.com', %s, 'ROLE_CEO', %s, NOW());
    """, (ceo_user_id, BCRYPT_PASSWORD123, provider_id))

cur.execute("SELECT id FROM ceos WHERE email = 'ceotest@example.com';")
ceo_row = cur.fetchone()
if ceo_row:
    ceo_id = ceo_row[0]
    cur.execute("""
        UPDATE ceos 
        SET name = 'Rahul Sharma', designation = 'Chief Executive Officer', phone = '+91 98230 11223', 
            status = 'ACTIVE', organization_id = %s, provider_id = %s, user_id = %s, updated_at = NOW()
        WHERE id = %s;
    """, (org_id, provider_id, ceo_user_id, ceo_id))
else:
    ceo_id = str(uuid.uuid4())
    cur.execute("""
        INSERT INTO ceos (id, name, email, phone, designation, status, organization_id, provider_id, user_id, created_at, updated_at)
        VALUES (%s, 'Rahul Sharma', 'ceotest@example.com', '+91 98230 11223', 'Chief Executive Officer', 'ACTIVE', %s, %s, %s, NOW(), NOW());
    """, (ceo_id, org_id, provider_id, ceo_user_id))

print(f"CEO seeded: Rahul Sharma (id={ceo_id})")

# 4. WORK SITE: Apex Tower Construction Site
# Lat: 21.1458, Lon: 79.0882, Alt: 312m, Radius: 150m
cur.execute("SELECT id FROM work_sites WHERE name = 'Apex Tower Construction Site';")
site_row = cur.fetchone()
if site_row:
    site_id = site_row[0]
    cur.execute("""
        UPDATE work_sites
        SET address = 'Nagpur, Maharashtra, India', latitude = 21.1458, longitude = 79.0882, 
            altitude = 312.0, geofence_radius = 150, status = 'ACTIVE', ceo_id = %s, provider_id = %s
        WHERE id = %s;
    """, (ceo_id, provider_id, site_id))
else:
    site_id = str(uuid.uuid4())
    cur.execute("""
        INSERT INTO work_sites (id, name, address, latitude, longitude, altitude, geofence_radius, status, ceo_id, provider_id, created_at)
        VALUES (%s, 'Apex Tower Construction Site', 'Nagpur, Maharashtra, India', 21.1458, 79.0882, 312.0, 150, 'ACTIVE', %s, %s, NOW());
    """, (site_id, ceo_id, provider_id))

print(f"WorkSite seeded: Apex Tower Construction Site (id={site_id})")

# Additional Work Sites for realistic CEO stats
sites_extra = [
    ('Metro Rail Project Site', 'Sitabuldi, Nagpur, Maharashtra', 21.1498, 79.0806, 310.0, 200),
    ('Expressway Hub Site', 'Mihan, Nagpur, Maharashtra', 21.0560, 79.0430, 305.0, 250)
]
for sname, saddr, slat, slon, salt, srad in sites_extra:
    cur.execute("SELECT id FROM work_sites WHERE name = %s;", (sname,))
    if not cur.fetchone():
        cur.execute("""
            INSERT INTO work_sites (id, name, address, latitude, longitude, altitude, geofence_radius, status, ceo_id, provider_id, created_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, 'ACTIVE', %s, %s, NOW());
        """, (str(uuid.uuid4()), sname, saddr, slat, slon, salt, srad, ceo_id, provider_id))

# 5. EMPLOYEES:
# Rahul Sharma (EMP-1024), Amit Patil (EMP-1025), Sneha Joshi (EMP-1026), Vivek Deshmukh (EMP-1027)
employees_data = [
    {
        'name': 'Rahul Sharma',
        'code': 'EMP-1024',
        'email': 'rahul.sharma@apex.com',
        'dept': 'Field Operations',
        'desig': 'Site Supervisor',
        'site_id': site_id
    },
    {
        'name': 'Amit Patil',
        'code': 'EMP-1025',
        'email': 'amit.patil@apex.com',
        'dept': 'Civil Engineering',
        'desig': 'Site Engineer',
        'site_id': site_id
    },
    {
        'name': 'Sneha Joshi',
        'code': 'EMP-1026',
        'email': 'sneha.joshi@apex.com',
        'dept': 'Operations',
        'desig': 'Operations Executive',
        'site_id': site_id
    },
    {
        'name': 'Vivek Deshmukh',
        'code': 'EMP-1027',
        'email': 'vivek.deshmukh@apex.com',
        'dept': 'Electrical',
        'desig': 'Electrical Supervisor',
        'site_id': site_id
    }
]

emp_id_map = {}
for emp in employees_data:
    cur.execute("SELECT id FROM users WHERE email = %s;", (emp['email'],))
    u_row = cur.fetchone()
    if u_row:
        u_id = u_row[0]
        cur.execute("UPDATE users SET password_hash = %s, provider_id = %s, role = 'ROLE_EMPLOYEE' WHERE id = %s;", (BCRYPT_PASSWORD123, provider_id, u_id))
    else:
        u_id = str(uuid.uuid4())
        cur.execute("""
            INSERT INTO users (id, email, password_hash, role, provider_id, created_at)
            VALUES (%s, %s, %s, 'ROLE_EMPLOYEE', %s, NOW());
        """, (u_id, emp['email'], BCRYPT_PASSWORD123, provider_id))
    
    cur.execute("SELECT id FROM employees WHERE employee_code = %s;", (emp['code'],))
    e_row = cur.fetchone()
    if e_row:
        e_id = e_row[0]
        cur.execute("""
            UPDATE employees 
            SET name = %s, department = %s, designation = %s, status = 'ACTIVE',
                assigned_site_id = %s, ceo_id = %s, provider_id = %s, user_id = %s
            WHERE id = %s;
        """, (emp['name'], emp['dept'], emp['desig'], emp['site_id'], ceo_id, provider_id, u_id, e_id))
    else:
        e_id = str(uuid.uuid4())
        cur.execute("""
            INSERT INTO employees (id, employee_code, name, department, designation, status, assigned_site_id, ceo_id, provider_id, user_id)
            VALUES (%s, %s, %s, %s, %s, 'ACTIVE', %s, %s, %s, %s);
        """, (e_id, emp['code'], emp['name'], emp['dept'], emp['desig'], emp['site_id'], ceo_id, provider_id, u_id))
    
    emp_id_map[emp['code']] = e_id
    print(f"Employee seeded: {emp['name']} ({emp['code']}, id={e_id})")

norm_embedding = [round(1.0 / (192.0**0.5), 6) for _ in range(192)]
embedding_json = json.dumps(norm_embedding)

rahul_emp_id = emp_id_map['EMP-1024']
cur.execute("SELECT id FROM face_profiles WHERE employee_id = %s;", (rahul_emp_id,))
fp_row = cur.fetchone()
if fp_row:
    cur.execute("""
        UPDATE face_profiles 
        SET face_embedding_json = %s, enrollment_status = 'ENROLLED', template_reference = 'MOBILE_FACE_NET_V1'
        WHERE id = %s;
    """, (embedding_json, fp_row[0]))
else:
    cur.execute("""
        INSERT INTO face_profiles (id, employee_id, face_embedding_json, enrollment_status, template_reference, created_at)
        VALUES (%s, %s, %s, 'ENROLLED', 'MOBILE_FACE_NET_V1', NOW());
    """, (str(uuid.uuid4()), rahul_emp_id, embedding_json))

print(f"Face Profile enrolled for Rahul Sharma (EMP-1024)")

# Save the base embedding to a json file so test scripts can use it for verification testing!
with open('rahul_face_embedding.json', 'w') as f:
    json.dump(norm_embedding, f)

# 7. ATTENDANCE RECORDS for Amit Patil and Sneha Joshi today
today = datetime.date.today()
now = datetime.datetime.now()

cur.execute("SELECT COUNT(*) FROM attendance_records WHERE employee_id IN (%s, %s);", (emp_id_map['EMP-1025'], emp_id_map['EMP-1026']))
if cur.fetchone()[0] == 0:
    # Amit Patil punched in at 08:31 AM
    amit_time = datetime.datetime.combine(today, datetime.time(8, 31, 0))
    cur.execute("""
        INSERT INTO attendance_records (
            id, employee_id, ceo_id, provider_id, work_site_id, site_id,
            punch_in_time, punch_in_latitude, punch_in_longitude, punch_in_altitude, punch_in_accuracy,
            punch_in_location_timestamp, face_verification_status, location_verification_status, punch_type,
            created_at, attendance_date
        ) VALUES (
            %s, %s, %s, %s, %s, %s,
            %s, 21.1459, 79.0883, 312.0, 6.5,
            %s, 'VERIFIED', 'VERIFIED', 'IN',
            NOW(), %s
        );
    """, (
        str(uuid.uuid4()), emp_id_map['EMP-1025'], ceo_id, provider_id, site_id, site_id,
        amit_time, amit_time, today
    ))

    # Sneha Joshi punched in at 08:17 AM
    sneha_time = datetime.datetime.combine(today, datetime.time(8, 17, 0))
    cur.execute("""
        INSERT INTO attendance_records (
            id, employee_id, ceo_id, provider_id, work_site_id, site_id,
            punch_in_time, punch_in_latitude, punch_in_longitude, punch_in_altitude, punch_in_accuracy,
            punch_in_location_timestamp, face_verification_status, location_verification_status, punch_type,
            created_at, attendance_date
        ) VALUES (
            %s, %s, %s, %s, %s, %s,
            %s, 21.1457, 79.0881, 311.5, 8.0,
            %s, 'VERIFIED', 'VERIFIED', 'IN',
            NOW(), %s
        );
    """, (
        str(uuid.uuid4()), emp_id_map['EMP-1026'], ceo_id, provider_id, site_id, site_id,
        sneha_time, sneha_time, today
    ))
    print("Sample attendance records inserted for Amit Patil and Sneha Joshi")

conn.commit()
cur.close()
conn.close()

print("=== Presentation Dataset Seeding Complete ===")
