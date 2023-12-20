set DBCA_ADMIN_PWD=1q2w3e4r

docker stop test-oracle-xe
docker rm test-oracle-xe

docker run --name test-oracle-xe ^
        -e ORACLE_PASSWORD=%DBCA_ADMIN_PWD% ^
        -p 8091:8091 ^
        -p 1521:1521 ^
        -v my-oracle-data:/opt/oracle/oradata ^
        --network oracle-xe-net ^
        -d gvenzl/oracle-xe

timeout 10
