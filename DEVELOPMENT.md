# Local development

Run from the POERSPS folder in PowerShell:

```powershell
.\dev.ps1 Build
.\dev.ps1 Server
# In another PowerShell window:
.\dev.ps1 Client
```

The server compiler uses Java 21 with --release 8. Java 8 compilation stalls in ClueScrollConstants; Java 21 successfully compiles the complete source. The client compiles and both applications run using Java 8 because of legacy client audio APIs and server dependencies.

Override installations with -JdkPath (Java 8) and -ServerJdkPath (Java 21). Defaults match the installed JDKs on this machine.

Local server: 127.0.0.1:43594, online/database mode disabled. Cache and character storage use Notorious/koios-cache and Notorious/koios-store through a process-local user.home setting. The client defaults to LOCAL.

Verified in recorded prototype batches: complete builds/checks, character login, secondary-monitor fullscreen, tutorial combat, physical map/portal travel, ground loot, crafting, gem fusion and reconnect persistence. New changes require their own integration gate; see logs/overnight-integration.md. Existing warnings include duplicate SLF4J bindings and legacy sun.audio usage.

The original batch files are historical and contain obsolete paths; use dev.ps1.

Packet fragmentation regression: `PacketFragmentationCheck` sends encrypted attack/skill/equip packets through the actual Netty decoder at every split point, including one-byte deliveries. Keep DECODE_LENGTH when its bytes have not arrived; resetting to DECODE_OPCODE consumes the cipher incorrectly and turns remaining command bytes into random actions.

Tutorial layout preview: compile/run tools/TutorialLayoutPreview.java with the test classpath from Server to write build/tutorial-layout.png. Open-panel instructions are pinned above the chat input; closing restores the normal objective HUD.

Death regression: PrototypeIntegrationCheck drives the actual PlayerDeathTask, preserving item identities and the tutorial checkpoint, returning to the private base, waiting there, and resuming the same instance through the map portal.

## Linux/cloud headless gate

`dev.sh` builds the server with Java 21 `--release 8`, builds the client with a
Java 8 JDK (required for its legacy audio APIs), and runs tests on Java 8:

```bash
export EXILE_JAVA8_HOME=/path/to/java8-jdk
export EXILE_JAVA21_HOME=/path/to/java21-jdk
./dev.sh build
./dev.sh test
```

Both toolchains must already be installed; the script never downloads tools or
starts a server/client GUI. Test output goes to `Notorious/Server/build/*Check.log`;
rendered PNGs go to the same build directory. Each test runs in its own JVM to
isolate static cache/world state. The real repository cache and local JARs are used.
The headless gate omits `ControlRoutingCheck` and `ViewportInputCheck`, which
construct AWT Applets. Their pure scaling, hit-boundary, chat/hotbar clearance and
tracker layout contracts are covered by `HeadlessLayoutCheck` at 16 display
sizes, including portrait, ultrawide, odd sizes and scale breakpoints.

New review artifacts include `courtyard-environment.png`,
`crafting-reference-1.png` through `crafting-reference-4.png`, `layout-1920.png`,
`layout-3840.png` and the existing tooltip/outfit pose sheets. These are native
offscreen renders, not evidence of physical mouse delivery, OS fullscreen
transitions or a logged-in runtime session. Occasional local acceptance still
covers monitor switching, mouse bindings, camera angles, and animated clothing
triangle interiors. No GUI/browser automation is part of the cloud gate.
