#!/usr/bin/env python3
"""解压 JDK 和 Gradle，配置环境，然后编译 APK"""
import os
import sys
import zipfile
import subprocess
import shutil

JDK_DIR = r"C:\Users\Administrator\.workbuddy\binaries\jdk\17"
GRADLE_ZIP = r"C:\Users\Administrator\WorkBuddy\2026-06-04-14-07-57\AgnesApp\gradle-8.2.zip"
PROJECT_DIR = r"C:\Users\Administrator\WorkBuddy\2026-06-04-14-07-57\AgnesApp"
SDK_ROOT = r"C:\Users\Administrator\.workbuddy\binaries\android-sdk"

def extract_jdk():
    """解压 JDK"""
    zip_path = os.path.join(JDK_DIR, "jdk.zip")
    if not os.path.exists(zip_path):
        print(f"[ERROR] JDK zip not found: {zip_path}")
        return None
    
    # Check if already extracted
    for name in os.listdir(JDK_DIR):
        path = os.path.join(JDK_DIR, name)
        if os.path.isdir(path) and os.path.exists(os.path.join(path, "bin", "java.exe")):
            print(f"[JDK] Already installed: {path}")
            return path
    
    print(f"[JDK] Extracting from: {zip_path} ({os.path.getsize(zip_path)/1048576:.0f} MB)")
    with zipfile.ZipFile(zip_path, "r") as zf:
        zf.extractall(JDK_DIR)
    
    for name in os.listdir(JDK_DIR):
        path = os.path.join(JDK_DIR, name)
        if os.path.isdir(path) and os.path.exists(os.path.join(path, "bin", "java.exe")):
            print(f"[JDK] Installed: {path}")
            return path
    
    print("[ERROR] JDK extraction failed")
    return None

def extract_gradle():
    """解压 Gradle"""
    if not os.path.exists(GRADLE_ZIP):
        print(f"[ERROR] Gradle zip not found: {GRADLE_ZIP}")
        return None
    
    # Check if already extracted
    for name in os.listdir(PROJECT_DIR):
        path = os.path.join(PROJECT_DIR, name)
        if name.startswith("gradle-") and os.path.isdir(path):
            gb = os.path.join(path, "bin", "gradle.bat")
            if os.path.exists(gb):
                print(f"[Gradle] Already installed: {path}")
                return path
    
    print(f"[Gradle] Extracting from: {GRADLE_ZIP} ({os.path.getsize(GRADLE_ZIP)/1048576:.0f} MB)")
    with zipfile.ZipFile(GRADLE_ZIP, "r") as zf:
        zf.extractall(PROJECT_DIR)
    
    for name in os.listdir(PROJECT_DIR):
        path = os.path.join(PROJECT_DIR, name)
        if name.startswith("gradle-") and os.path.isdir(path):
            gb = os.path.join(path, "bin", "gradle.bat")
            if os.path.exists(gb):
                print(f"[Gradle] Installed: {path}")
                return path
    
    print("[ERROR] Gradle extraction failed")
    return None

def setup_sdk(java_home):
    """设置 Android SDK"""
    # Check if SDK already installed
    sdkmanager = os.path.join(SDK_ROOT, "cmdline-tools", "latest", "bin", "sdkmanager.bat")
    
    if not os.path.exists(sdkmanager):
        print("[SDK] Downloading Android SDK commandline tools...")
        tools_zip = os.path.join(PROJECT_DIR, "cmdline-tools.zip")
        cmdline_url = "https://dl.google.com/android/repository/commandlinetools-win-11076708_latest.zip"
        
        import urllib.request
        req = urllib.request.Request(cmdline_url, headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=600) as resp:
            with open(tools_zip, "wb") as f:
                while True:
                    chunk = resp.read(65536)
                    if not chunk:
                        break
                    f.write(chunk)
        
        cmdline_dir = os.path.join(SDK_ROOT, "cmdline-tools", "latest")
        os.makedirs(cmdline_dir, exist_ok=True)
        with zipfile.ZipFile(tools_zip, "r") as zf:
            zf.extractall(cmdline_dir)
        os.remove(tools_zip)
        
        # Fix nested cmdline-tools directory
        for name in os.listdir(cmdline_dir):
            path = os.path.join(cmdline_dir, name)
            if name == "cmdline-tools" and os.path.isdir(path):
                for item in os.listdir(path):
                    src = os.path.join(path, item)
                    dst = os.path.join(cmdline_dir, item)
                    if os.path.exists(dst):
                        if os.path.isdir(dst):
                            shutil.rmtree(dst)
                        else:
                            os.remove(dst)
                    shutil.move(src, dst)
                shutil.rmtree(path)
                break
    
    if not os.path.exists(sdkmanager):
        print("[ERROR] sdkmanager not found")
        return False
    
    # Install SDK components
    env = os.environ.copy()
    env["JAVA_HOME"] = java_home
    env["ANDROID_SDK_ROOT"] = SDK_ROOT
    
    components = [
        "platforms;android-34",
        "build-tools;34.0.0",
        "platform-tools",
    ]
    
    for comp in components:
        print(f"[SDK] Installing: {comp}")
        result = subprocess.run(
            [sdkmanager, "--sdk_root=" + SDK_ROOT, "--yes", comp],
            env=env,
            capture_output=True,
            text=True,
            timeout=600
        )
        if result.returncode != 0:
            print(f"  WARNING: {result.stderr[-300:]}")
    
    return True

def build_apk(java_home, gradle_home):
    """编译 APK"""
    gradle_cmd = os.path.join(gradle_home, "bin", "gradle.bat")
    java_cmd = os.path.join(java_home, "bin", "java.exe")
    
    env = os.environ.copy()
    env["JAVA_HOME"] = java_home
    env["PATH"] = f"{java_home}\\bin;{env.get('PATH', '')}"
    env["ANDROID_SDK_ROOT"] = SDK_ROOT
    
    # Create local.properties
    with open(os.path.join(PROJECT_DIR, "local.properties"), "w") as f:
        f.write(f"sdk.dir={SDK_ROOT}\n")
    
    print("[BUILD] Starting Gradle build...")
    print(f"  JAVA_HOME={java_home}")
    print(f"  SDK={SDK_ROOT}")
    
    result = subprocess.run(
        [gradle_cmd, "assembleDebug", "--no-daemon", "-Dorg.gradle.jvmargs=-Xmx2048m"],
        cwd=PROJECT_DIR,
        env=env,
        capture_output=False,
        timeout=1800
    )
    
    if result.returncode == 0:
        apk_path = os.path.join(PROJECT_DIR, "app", "build", "outputs", "apk", "debug", "app-debug.apk")
        if os.path.exists(apk_path):
            size_mb = os.path.getsize(apk_path) / 1048576
            print(f"\n[SUCCESS] APK built: {apk_path} ({size_mb:.1f} MB)")
            return apk_path
    
    print(f"\n[FAILED] Build failed with code {result.returncode}")
    return None

if __name__ == "__main__":
    print("=" * 50)
    print(" AgnesApp Build Pipeline")
    print("=" * 50)
    
    # Step 1: Extract JDK
    java_home = extract_jdk()
    if not java_home:
        sys.exit(1)
    
    # Step 2: Extract Gradle
    gradle_home = extract_gradle()
    if not gradle_home:
        sys.exit(1)
    
    # Step 3: Setup Android SDK
    setup_sdk(java_home)
    
    # Step 4: Build
    apk = build_apk(java_home, gradle_home)
    
    if apk:
        print(f"\nAPK ready at: {apk}")
    else:
        print("\nBuild failed. Check Gradle output above.")
        sys.exit(1)
