# Wait for OracleXe db and listener activation
sleep 20
# Start SpringBootJwt application
java -Duser.timezone=CET -jar /app/springBootRest.jar