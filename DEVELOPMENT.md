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
