import psycopg2
conn = psycopg2.connect(host='localhost', port=5432, dbname='field_attendance', user='postgres', password='postgres')
cur = conn.cursor()
cur.execute("DELETE FROM attendance_records WHERE employee_id IN (SELECT id FROM employees WHERE employee_code = 'EMP-1024');")
conn.commit()
cur.close()
conn.close()
print("Rahul Sharma punch record cleaned for presentation test.")
