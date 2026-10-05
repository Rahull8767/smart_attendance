import psycopg2

conn = psycopg2.connect(host='localhost', port=5432, dbname='field_attendance', user='postgres', password='postgres')
cur = conn.cursor()

def print_table(table):
    cur.execute(f"SELECT column_name, data_type FROM information_schema.columns WHERE table_name = '{table}';")
    cols = cur.fetchall()
    print(f"\n--- {table} Columns: ---")
    for col in cols:
        print(f"  {col[0]} ({col[1]})")
    cur.execute(f"SELECT * FROM {table} LIMIT 5;")
    rows = cur.fetchall()
    print(f"--- {table} Rows ({len(rows)}): ---")
    for r in rows:
        print(r)

for t in ['users', 'providers', 'organizations', 'ceos', 'employees', 'work_sites', 'face_profiles', 'attendance_records']:
    print_table(t)

cur.close()
conn.close()
