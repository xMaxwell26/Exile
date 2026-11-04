# Exile administration and vote callback

Run `powershell -ExecutionPolicy Bypass -File C:\Users\mmuir\OneDrive\Documents\ChatGPT\POERSPS\tools\ExileAdmin.ps1` to open the local control panel. It can start/stop the Java server, launch a client, show the machine's external address, and display the configured game port.

The panel binds to `127.0.0.1` by default. Only use a LAN/WAN bind after configuring Windows Firewall and router port forwarding. The external address shown by the panel is not automatically published anywhere.

## MySQL vote callback

Run `Notorious\Server\sql\exile_votes.sql` against the MySQL database. Before launching an online server, set:

```powershell
$env:EXILE_VOTE_CALLBACK='true'
$env:EXILE_VOTE_BIND='127.0.0.1'
$env:EXILE_VOTE_PORT='8088'
$env:EXILE_VOTE_TOKEN='use-a-random-32-character-secret'
```

The server accepts authenticated `POST /vote` JSON with `player`, `site`, and `id`, stores it in `exile_votes`, and grants no game rewards. Use a reverse proxy for public HTTPS callbacks; do not expose the raw Java callback directly to the internet.

## Distributable client

Build a release with `powershell -ExecutionPolicy Bypass -File tools\BuildExileRelease.ps1 -Version 0.1.0 -HostAddress your.external.address`. Upload the generated ZIP and `manifest.json` to Dropbox or another HTTPS static host. Users run `java -jar ExileLauncher.jar` after setting `EXILE_MANIFEST_URL` to the hosted manifest URL.

The launcher validates SHA-256 before extracting and keeps each version in the user's `.exile` directory. Changing hosts only requires updating the manifest. Optional ProGuard configuration is provided in `tools\proguard-release.pro`; review it before publishing because aggressive obfuscation can break legacy reflective code.
