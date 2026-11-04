# Exile — Original NPC authoring

The first original model is the Furnace Sentinel (client alias 15001, cache skeleton template 90). Its visible mesh is authored in `Notorious/Client/src/com/koios/client/arpg/CustomNpcModels.java`. No skeleton/skull/armour triangles from template 90 are drawn. The template contributes its proven animation skin groups and pivot statistics.

## Change the Sentinel

`create` calls five component recipes in order. Edit the recipe that owns the feature:

| Recipe | Features |
| --- | --- |
| `chassis` | broad stone/iron shell, recessed amber furnace core, grille and rear vents |
| `hood` | recessed original iron head, protective hood, amber eye slits |
| `arms` | asymmetric angled pauldrons, upper arms, hammer gauntlet, three hooked claw fingers |
| `lowerBody` | pelvis, separated thighs/shins, broad squat metal feet |
| `jointCapsAndVents` | circular knee caps and side vent ribs |

Palette constants are at the top. `hsl(hue,saturation,lightness)` uses the native cache packed HSL format, not RGB. Iron, stone and steel stay muted; `EMBER` and `FIRE` make the core and eye slits readable. Geometry dimensions and palette do not affect damage, HP or loot: those remain server data/profile concerns.

Coordinates are native world units: X left/right, Y=0 ground and negative Y upward, negative Z chest front. Negative X is the hammer side of this bind pose. `VISIBLE_WIDTH_SCALE=.82` is applied to all authored X coordinates before anchoring. Final visible bind bounds are X-59..58, Y-222..0, Z-35..37: width 117 and depth 72 fit a one-tile 128-unit body at rest. Posed bounds still require animation review; a swinging arm can extend beyond a collision tile.

## Primitive recipes

- `box(x0,y0,z0,x1,y1,z1,bone,color,rim,bevel)` creates a solid chamfered prism. Every corner follows one bone; split limbs at joints instead of spanning multiple bones with one rigid box.
- `plate(polygon,z0,z1,bone,color,edge)` extrudes an XY polygon with solid caps/sides. The triangulator accepts concave silhouettes. Used for angled shoulder plates.
- `disc(x,y,z0,z1,radius,sides,bone,color,edge)` creates a solid cylinder along Z for mechanical joints and furnace cores.
- `hook` is the current six-point extruded claw recipe; edit or duplicate it for another claw shape.

Keep component overlap small enough to hide gaps while a joint rotates. Do not fill an entire thigh-to-shin span with one torso-bound piece. Test moving frames, not just the bind pose. Full-frame contact sheets use a fixed origin and ground guide; recentering each pose would hide floating feet.

## Proven native skin groups

These numbers belong specifically to template 90. They are named constants rather than unexplained values in component calls.

| Constant | Group | Part |
| --- | ---: | --- |
| `BONE_TORSO` | 3 | chest |
| `BONE_HEAD` | 1 | head/hood |
| `BONE_HAMMER_UPPER` | 15 | hammer-side upper arm/pauldron |
| `BONE_HAMMER_FOREARM` | 12 | hammer forearm/gauntlet |
| `BONE_CLAW_UPPER` | 8 | claw-side upper arm/pauldron |
| `BONE_CLAW_FOREARM` | 5 | claw forearm/fingers |
| `BONE_HIPS` | 54 | pelvis |
| `BONE_LEFT_THIGH`, `BONE_RIGHT_THIGH` | 32,40 | thighs |
| `BONE_LEFT_SHIN`, `BONE_RIGHT_SHIN` | 28,36 | shins/knees |
| `BONE_LEFT_FOOT`, `BONE_RIGHT_FOOT` | 44,26 | feet |

Other template groups remain as invisible rig anchors. The cache animation interpreter computes opcode 0 pivots from the centroid of selected vertex groups. Simply replacing a body with valid bone numbers still changes those centroids and can make joints rotate around the wrong point.

`Mesh.finish` preserves every group's vertex count and X/Y/Z sum by the same integer factor. The authored visible points plus invisible compensation points therefore retain exact combined bind-pose pivots. Invisible points have no faces; they do not show the original NPC geometry. Integer rounding during animation still requires actual-frame tests.

`PIVOT_MASS_FACTOR=11` leaves room for compensation points; lowering it to the smallest mathematical count produced extreme anchor coordinates and made the mesh disappear through native render bucket limits. Do not lower it or add many vertices to a small group without checking all-vertex bounds. Current mesh has 896 visible vertices/1,544 original faces and 4,950 total vertices including anchors. The renderer has an 8,000-vertex scratch limit; the factory rejects larger meshes. This anchor approach is intentionally more expensive than an offline skinned asset and should be profiled with realistic NPC counts.

## Runtime cache hook

The factory must run after the source body/recolour assembly and before `method469` groups vertices and before lighting/caching, in **both** `NPCDefinition.method164` overloads. For owned IDs, skip the generic `CustomNpcs.style` tint so furnace/steel materials survive. Cache under the alias ID (`interfaceType`), never under template 90. The actual `NPC.getRotatedModel` runtime uses the three-argument overload; testing only the six-argument overload is insufficient because that branch also applies 132/128 scaling.

The factory is now hooked in production source `Notorious/Client/src/com/koios/client/definition/NPCDefinition.java`, in both model overloads. The same helper also owns Briar Warden 15007; see `BRIAR-AUTHORING.md`. The helper does not install models into cache files and does not mutate template 90. Source promotion does not update an already running client: a full build/restart and live visual gate are still required.

## Add a different creature

1. Choose a new alias ID and server role. Leave the existing alias and cache template untouched.
2. Pick a proven rig and decode its actual idle/walk/attack/death frames. Probe its bone groups; template 90 constants do not automatically apply to another rig.
3. Add a named factory branch and separate component recipes. Reuse the `Mesh` primitive/anchor builder, but author a genuinely different silhouette and materials. A root warden could use a hollow bark trunk, branching arms and broad rooted feet rather than merely recolouring this construct.
4. Keep visible dimensions compatible with the server footprint and pathing. Report both visible and anchor bounds.
5. Replace **all** visible faces, clear source texture/skin/normal/group arrays, and retain alias-specific cache identity.
6. Run geometry tests and the actual NPC-path animation harness before applying the production hook.
7. Balance HP, windup, reach, cadence, rewards and drops separately in server data. An animation choice does not create damage delay automatically.

## Validation

`CustomNpcMeshCheck` checks original topology, nondegenerate faces, source immutability, exact per-bone mass/count pivots and fixed-ground bind views. `CustomNpcRigCheck` independently exercises actual NPC runtime, every selected idle/walk/attack/death frame at four angles, blended walk/action, alias/template cache interleaving and reloads. Native pages are under `Notorious/Server/build/custom-npc-15001-...`; bind preview is `custom-construct-bind.png`.

Attack 5487 is verified on the actual skeleton rig and selected for both authored NPC server profiles. Its 49 client cycles last about 980 ms; maximum extension near 560–620 ms matches the one-server-tick 600 ms windup. The server rechecks reach/LOS before damage, so leaving the attack can dodge it. Legacy attack 260 was rejected for this role because it mainly moved the torso/knee. Death 5491 deliberately collapses the original rig to zero coordinates in its final frames; matching disappearance is expected, not a new mesh failure. Actual live world lighting, camera angle, crowd performance and silhouette acceptance remain separate from passing rig tests.



## Balance without editing the model

Enemy HP, damage, attack cadence, reach, animation ID and windup are in `Notorious/Server/data/arpg/enemies.json`, loaded by `EncounterProfiles.java`. Overrides for NPC 15001 and 15007 keep these two roles separate. Edit that data and restart the server; do not change vertex coordinates to adjust combat power. The model factory only controls appearance and rig bindings. Loot uses the server's separate drop-table system.
