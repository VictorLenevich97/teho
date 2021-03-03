@echo off
start /b "teho-1.0" java -noverify -Dspring.jmx.enabled=false -XX:TieredStopAtLevel=1 -Dspring.profiles.active=production -Dspring.config.location=classpath:application.properties -Dspring.main.lazy-initialization=true -Dserver.port=33657 -Dspring.datasource.password=tehoadmin -jar teho-1.0-RELEASE.jar

teho.exe

for /f  "useback tokens=* delims=" %%# in (
    `wmic process where "CommandLine like '%%teho-1.0-RELEASE.jar%%' and not CommandLine like '%%wmic%%' " get  ProcessId /Format:Value`
) do (
    for /f "tokens=* delims=" %%a in ("%%#") do set "%%a"
)

taskkill /pid %processId% /f