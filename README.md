# Interceptor
Make the Android side of a FaceTime invite better.

Interceptor is an Android application that listens for incoming FaceTime web links (sent via SMS, WhatsApp, etc.) and seamlessly intercepts them. Instead of a boring text message with a link, Interceptor pops up a native-feeling, full-screen "Incoming Call" UI with a live background from your front-facing camera.

Tapping "Accept" automatically launches the FaceTime call in a Chrome Custom Tab for instant WebRTC compatibility.

## Setup
1. Launch the app.
2. Grant Camera permissions.
3. Grant Notification Listener permissions (required to detect incoming links).
4. You're set! Next time someone texts you a FaceTime link, it will ring like a real call.

## Development & Testing
You can use the provided script to quickly spin up a headless emulator, compile the app, and install it:

```bash
./run_emulator_debug.sh
```

You can then test the "Ringing" UI by tapping the **"Simulate Incoming FaceTime"** button on the setup screen.
