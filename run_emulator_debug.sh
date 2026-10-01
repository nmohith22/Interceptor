#!/bin/bash
set -e

# Default to the known Windows path if ANDROID_HOME is not set
export JAVA_HOME="${JAVA_HOME:-C:/Users/mskyl/openjdk/jdk-17.0.11+9}"
export ANDROID_HOME="${ANDROID_HOME:-C:/Users/mskyl/android_sdk}"
export ANDROID_SDK="$ANDROID_HOME"
AVD_NAME="test_avd"

AVD_MANAGER="$ANDROID_SDK/cmdline-tools/latest/bin/avdmanager.bat"
EMULATOR="$ANDROID_SDK/emulator/emulator.exe"
ADB="$ANDROID_SDK/platform-tools/adb.exe"

echo "Creating AVD $AVD_NAME (if it doesn't exist)..."
echo "no" | "$AVD_MANAGER" create avd -n $AVD_NAME -k "system-images;android-33;google_apis;x86_64" --force || true

echo "Starting Emulator in background..."
"$EMULATOR" @"$AVD_NAME" -no-snapshot-save -no-boot-anim &

echo "Waiting for emulator to boot..."
"$ADB" wait-for-device

while true; do
    BOOT_COMPLETED=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
    if [ "$BOOT_COMPLETED" = "1" ]; then
        break
    fi
    sleep 5
done
echo "Emulator booted!"

echo "Building APK..."
./gradlew assembleDebug

echo "Installing APK..."
"$ADB" install -r app/build/outputs/apk/debug/app-debug.apk

echo "Launching App..."
"$ADB" shell am start -n "com.example.interceptor/com.example.interceptor.MainActivity"

echo "Waiting 3 seconds for UI to render..."
sleep 3

echo "Taking screenshot..."
"$ADB" exec-out screencap -p > debug_screen.png

echo "Done! Screenshot saved to ./debug_screen.png"

echo "Opening screenshot in VS Code..."
code ./debug_screen.png
