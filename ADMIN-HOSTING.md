# Exile administration and vote callback

Run `powershell -ExecutionPolicy Bypass -File tools\ExileAdmin.ps1` to open the local control panel. It can start/stop the Java server, launch a client, show the machine's external address, and display the configured game port.

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
