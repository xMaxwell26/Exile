# Exile Launcher

The launcher downloads a release ZIP only when the manifest version or SHA-256 differs, validates the archive, and starts the client with the host and port from the manifest. The release build embeds the manifest URL into `ExileLauncher.jar`, so players can double-click the JAR without a batch file. Host the ZIP and manifest on Dropbox, a static web host, or object storage. Use HTTPS. `-Dexile.manifest=...` and `EXILE_MANIFEST_URL` remain available for testing overrides.

Manifest example:

```json
{"version":"0.1.0","url":"https://files.example/exile-0.1.0.zip","sha256":"...64 lowercase hex...","host":"your.external.address","port":43594}
```
