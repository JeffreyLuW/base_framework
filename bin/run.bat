@echo off
echo.
echo [信息] 运行Web工程。
echo.

cd %~dp0
cd ../digital-platform/digital-admin/target

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

chcp 65001
java  -Dfile.encoding=utf-8 -jar %JAVA_OPTS% digital-admin.jar

cd bin
pause