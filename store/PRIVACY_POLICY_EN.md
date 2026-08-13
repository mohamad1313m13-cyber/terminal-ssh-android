# Terminal SSH Privacy Policy

**Last updated: August 13, 2026**

Terminal SSH is an SSH client that connects directly to servers selected by the user. The app has no central user account, analytics service, advertising SDK, or third-party tracking.

Connection information is stored only on the device. Sensitive vault values are protected with AES-GCM using a non-exportable Android Keystore key. Ephemeral passwords are deleted after a successful connection, cancellation, failure, or Activity destruction. Stored host keys are used only to verify server identity.

Internet permission is required to establish SSH connections. SSH traffic flows directly between the user's device and the server chosen by the user. The developer does not receive terminal contents, passwords, or private keys through an intermediary service.

Cloud backup/device-transfer extraction of internal app data is excluded. Android cleartext traffic is disabled, and the terminal Activity uses FLAG_SECURE to reduce ordinary screenshot/screen-capture exposure.

Users can remove locally stored app data through Android system settings or by uninstalling the app.
