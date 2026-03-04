@echo off
setlocal

REM Apache Maven Wrapper startup script (Windows)
REM Downloads the wrapper JAR (and then Maven) on first run.

set BASEDIR=%~dp0
set WRAPPER_DIR=%BASEDIR%.mvn\wrapper
set JAR_FILE=%WRAPPER_DIR%\maven-wrapper.jar
set WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar

if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"

if not exist "%JAR_FILE%" (
  echo Downloading Maven Wrapper JAR...
  powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%WRAPPER_URL%','%JAR_FILE%')" 
)

if "%JAVA_HOME%"=="" (
  set JAVACMD=java
) else (
  set JAVACMD=%JAVA_HOME%\bin\java
)

%JAVACMD% %MAVEN_OPTS% -classpath "%JAR_FILE%" -Dmaven.multiModuleProjectDirectory="%BASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*

endlocal
