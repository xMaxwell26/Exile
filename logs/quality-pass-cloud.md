# Exile quality pass — 2026-11-04

Started from clean c2e2587; branch codex/quality-pass. Product direction remains
classless PvE. Existing com.koios packages and item/save namespaces are retained.

## Toolchain and final verification

Workspace-local Temurin 8u472-b08 builds the complete client and runs every
headless test. Temurin 21.0.9+10 compiles the complete server and tests with
--release 8. Existing sun.audio, obsolete Java 8 target and duplicate SLF4J
binding warnings remain. No dependency/cache replacement was needed. A final classfile sweep confirms
all 1,124 server and 178 client classes have Java 8 major version 52.

The complete `dev.sh test` gate passed 59 test entry points plus both configured
NPC rig profiles. After strengthening the layout render assertion, its focused
rerun passed 389 checks. A further all-base tooltip sweep passed 2,688 checks;
that test is now included in both entry points (60 tests plus two rig profiles).
The complete gate output is generated at logs/quality-gate.log; individual
outputs and PNGs are under Notorious/Server/build and remain ignored artifacts.

Key contracts:

- DropTableCheck: 8,281 reward/schema checks, including duplicate keys, strict
  syntax, unknown keys, quoted flags/numbers, fractional/overflowing integers,
  required fields, comments, canonical overrides and preview isolation.
- CourtyardEnvironmentCheck: 691 real-cache footprint, connected destination,
  two-tile garden crossing, clear nave/boss dodge-space and native-scene checks.
- ArmourSurfaceCheck: 600 covered added vertices have positive clearance at
  final integer XY. Before the fix, 16 vertices touched/penetrated the torso,
  with maximum penetration 0.375 model units. Reprojection fixes those fixtures
  without moving original body vertices or changing skin groups.
- HeadlessLayoutCheck: 389 contracts at 16 display sizes, including fractional
  scale breakpoints, portrait/ultrawide/4K, exact hotbar click boundaries,
  chat/minimap clearance and actual bar text with long rebound control labels.
- CraftingReferenceCheck: 86 server-packet/navigation/font-width contracts;
  all eleven recipe names and complete descriptions survive pagination.
- TooltipContentCheck: 2,688 assertions across 17 bases and 12 seeded rare rolls
  each. Computed properties appear once, base offsets stay out of the normal
  view, base properties have no explicit tiers, unidentified explicit values
  remain private and tooltip production never mutates saved metadata.
- Existing full-character, armour pose, packet fragmentation, loot identity,
  crafting transaction, map death, terrain, login, palette and art checks pass.
  Each custom NPC profile passes 488 contracts over 62 frames/248 native views.

## Review artifacts

Generated with the actual cache font, models, terrain and software renderer:

- build/courtyard-environment.png (arrival and northern court scene views)
- build/crafting-reference-1.png through crafting-reference-4.png
- build/layout-1920.png and layout-3840.png (logical native HUD surfaces)
- build/tooltip-normal.png, tooltip-advanced.png and tooltip-bounds-*.png
- build/armour-variant-poses.png, armour-variant-angles.png, outfit-angles.png

The Courtyard paths and first crafting reference page were inspected as local
image artifacts. These renders establish deterministic authored composition and
layout evidence; they are not logged-in gameplay screenshots.

## Failures resolved during the pass

The initial Linux bootstrap discovered that the installed Java 21 was a JRE;
workspace-local JDKs resolved compilation. A first script run reached the final
login check but hit an EOF because the script was edited while that process was
running. The final unmodified full run, including both rig profiles, exited 0.
A new translated-tab test initially used the inclusive upper boundary as an
excluded point; its expectation was corrected to the pixel above that boundary.
The new tooltip test initially passed a roll to an Item-taking codec; it was
corrected before the successful sweep. The armour probe deliberately failed on
the original geometry; the same independent projection check now passes.

## Local acceptance still required

No native GUI, browser, desktop helper or visual client automation was attempted.
ControlRoutingCheck and ViewportInputCheck construct Applets and are omitted
from the headless gate. Pure scale/layout contracts are covered independently.

Occasional local acceptance must still cover monitor/fullscreen transitions,
physical extra mouse buttons, live tutorial/services/clickable loot and combat
camera composition. Armour tests prove covered vertex clearance, not triangle
interiors, hand/body intersection in every animation frame, or release-quality
outfit art. Those limitations remain explicit; no broad no-clipping claim is made.
