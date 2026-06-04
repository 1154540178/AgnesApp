@echo off
echo ========================================
echo  AgnesApp Android APK Build Script
echo ========================================
echo.

REM --- Find Java ---
set "JAVA_HOME_RAW="
for /d %%d in ("%USERPROFILE%\.workbuddy\binaries\jdk\17\*") do (
    if exist "%%d\bin\java.exe" (
        set "JAVA_HOME_RAW=%%d"
    )
)

if not defined JAVA_HOME_RAW (
    echo [ERROR] JDK 17 not found!
    echo Please run: python install_jdk.py
    exit /b 1
)

set JAVA_HOME=%JAVA_HOME_RAW%
set PATH=%JAVA_HOME%\bin;%PATH%

echo [INFO] JAVA_HOME=%JAVA_HOME%
"%JAVA_HOME%\bin\java" -version 2>&1 | findstr /V "^$"
echo.

REM --- Find Gradle ---
set "GRADLE_HOME="
for /d %%d in ("%~dp0gradle-8.2") do set "GRADLE_HOME=%%d"
if not defined GRADLE_HOME (
    echo [ERROR] Gradle 8.2 not found!
    echo Please run: python setup_build_tools.py
    exit /b 1
)
set "GRADLE_CMD=%GRADLE_HOME%\bin\gradle.bat"
echo [INFO] GRADLE_HOME=%GRADLE_HOME%
echo.

REM --- Find Android SDK ---
set "ANDROID_SDK_ROOT=%USERPROFILE%\.workbuddy\binaries\android-sdk"
if not exist "%ANDROID_SDK_ROOT%" (
    echo [ERROR] Android SDK not found at %ANDROID_SDK_ROOT%
    exit /b 1
)
echo [INFO] ANDROID_SDK_ROOT=%ANDROID_SDK_ROOT%
echo.

REM --- Create local.properties ---
(
echo sdk.dir=%ANDROID_SDK_ROOT%
) > "%~dp0local.properties"
echo [INFO] Created local.properties

REM --- Build ---
echo.
echo [BUILD] Starting Gradle build...
echo.

cd /d "%~dp0"

call "%GRADLE_CMD%" assembleDebug --no-daemon -Dorg.gradle.jvmargs="-Xmx2048m"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build failed!
    exit /b 1
)

echo.
echo ========================================
echo  BUILD SUCCESSFUL!
echo ========================================
echo.
echo APK location: app\build\outputs\apk\debug\app-debug.apk
echo.

REM --- Show APK info ---
for %%f in (app\build\outputs\apk\debug\app-debug.apk) do (
    echo APK size: %%~zf bytes
    echo APK path: %%~ff
)
