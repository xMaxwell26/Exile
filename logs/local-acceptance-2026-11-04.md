# Local acceptance — 2026-11-04

Branch tested: `codex/quality-pass`

## Passed

- Server and client compiled successfully on Windows.
- `ControlRoutingCheck` passed 64 actual AWT/client-drain routing, context, mouse-exclusion, and capture assertions.
- `ViewportInputCheck` passed actual render, hover, click, and drag scaling at four viewport sizes.
- A fresh `qacloud` character equipped the starter sword and body armour from inventory and advanced from tutorial step 1 to step 2.
- Equipped starter armour rendered without the previously reported obvious body clipping in the observed idle frame.
- `qathorn` completed `FULLSCREEN -> FIXED -> RESIZABLE -> FULLSCREEN`; state dimensions and display bounds recalculated correctly after every transition.
- The skills/loadout panel rendered cleanly at fullscreen. No socket labels overlapped their boxes.
- Thornwild Courtyard consumed the selected map, opened an independent instance, displayed its objective and return-portal instruction, spawned the intended enemy families, and entered live combat.
- No client exceptions were written during the pass.
- All Exile server/client Java processes were stopped after testing; zero workspace processes remained.

## Important visual failure / next target

Thornwild Courtyard is mechanically functional but does **not** yet meet the intended visual bar. In live fullscreen rendering it remains visibly prototype-grade:

- large flat grey floor areas;
- sparse environmental storytelling and decoration;
- abrupt rectangular terrain transitions;
- black/empty horizon around the instance;
- repeated wall/fence shapes with weak composition;
- enemies bunch around narrow wall geometry, making encounters visually noisy.

The next environment pass should prioritize a stronger arrival composition, varied ground materials and elevation, deliberate lanes/rooms, landmark silhouettes, boundary masking, prop clusters, lighting/color separation, and enemy spawn spacing.

## Runtime note

The live server must run on the project Java 8 runtime. A direct Java 21 launch fails in legacy XStream initialization because `java.base` does not open `java.util`. The normal project runtime path already uses Java 8; this was a QA-launch correction, not a gameplay regression.

## Hardware-only remainder

The synthetic and AWT mouse-routing checks passed, including the configured `Mouse 4` binding. A physical extra-mouse-button press was not performed because that would require taking over the user's hardware input. This remains the only local hardware-specific acceptance item from this pass.
