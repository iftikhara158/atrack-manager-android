# aTrack Manager Android (Minimal WebView)

Short setup:
1. Add GitHub Secrets (Settings → Secrets → Actions):
   - KEYSTORE_BASE64   (base64 of your .jks keystore)
   - KEYSTORE_PASSWORD
   - KEY_ALIAS
   - KEY_PASSWORD
2. Push this repo to GitHub.
3. In Actions tab, select "Build Android App" and click Run workflow.
4. Download signed APK & AAB from the workflow artifacts.
