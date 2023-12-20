# Copy the content of /db_script
docker cp ../db_script/. test-oracle-xe:/opt/oracle
docker exec -u oracle test-oracle-xe /opt/oracle/product/21c/dbhomeXE/bin/sqlplus sys/1q2w3e4r as sysdba @/opt/oracle/create_user.sql
docker exec -u oracle test-oracle-xe /opt/oracle/product/21c/dbhomeXE/bin/sqlplus sys/1q2w3e4r as sysdba @/opt/oracle/init_db.sql
docker exec -u oracle test-oracle-xe /opt/oracle/product/21c/dbhomeXE/bin/sqlplus sys/1q2w3e4r as sysdba @/opt/oracle/init_security_db.sql
docker exec -u oracle test-oracle-xe /opt/oracle/product/21c/dbhomeXE/bin/sqlplus sys/1q2w3e4r as sysdba @/opt/oracle/insert_users_db.sql
