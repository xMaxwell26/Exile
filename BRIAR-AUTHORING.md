# Exile — Briar Warden authoring

Briar Warden is an original Courtyard creature authored in `Notorious/Client/src/com/koios/client/arpg/CustomNpcModels.java`. Its recipes apply only to alias 15007/template 90; the Furnace Sentinel 15001 recipes remain intact. Both models have been promoted into source. Running clients need the next build/restart before these source changes appear.

## Editable silhouette

| Recipe | Edit here for |
| --- | --- |
| `briarTrunk` | twisted hollow wood trunk, winding bark ribs, recessed green heart and mossy rear spine |
| `briarCrown` | recessed knot-face, green eyes and forked branch crown |
| `briarArms` | one heavy knotted striking limb, opposite tapered three-finger branch claw, shoulder shoots |
| `briarLegsAndRoots` | tapered woody thighs/shins and three splayed low roots per foot |

New materials are named `BARK`, `BARK_EDGE`, `MOSS`, `HEART` and `HEART_LIGHT` at the top. Their packed cache HSL values control appearance only. Keep heart/eyes green to distinguish this Courtyard role from the amber Foundry construct; Exile's surrounding UI uses the separate dark red theme.

All visible body parts are made from the new `Mesh.branch` primitive plus the small solid heart/eye discs. None calls the Furnace Sentinel's chassis, hood, arm, boot or shoulder recipes. No template skeleton faces are retained.

## Tapered branch primitive

`branch(x0,y0,z0,x1,y1,z1,radius0,radius1,bone,color,ridge)` builds a closed six-facet solid along an arbitrary 3D direction. It computes a stable perpendicular basis even for vertical stems; the end ring twists slightly to create irregular bark facets. Different endpoint radii give organic taper. Negative Y is up; Y=0 is ground; negative Z is the chest front.

Author a bent root/trunk as several touching branch segments. Change coordinates and endpoint radii to adjust curvature/mass. Split at animated joints: an entire branch belongs to one native bone. A long branch spanning thigh and shin would remain rigid and separate while walking. Use multiple branches to create forks; avoid adding dozens of tiny branches to a bone with few source vertices, because that increases pivot compensation cost.

Current foot roots spread forward, inward and backward from each ankle, tapering to radius 2 at ground 0. The crown is intentionally taller than the construct. Visible bind bounds: X-58..57 (width 115), Y-255..0, Z-27..42 (depth 69). This remains one tile wide/deep at rest; posed limb reach is an animation/gameplay consideration.

## Rig and limits

Briar reuses template 90's proven skeleton groups and the same exact group-count/coordinate-sum pivot balancing as the Furnace Sentinel. Refer to the workspace `NPC-AUTHORING.md` for the named bone table and cache-hook explanation. The new visible topology is 526 vertices/870 faces; total vertices including invisible pivot anchors are 4,950. The native renderer's 8,000-vertex limit is enforced. Invisible anchors are not an original creature mesh: they have no faces and preserve skeleton pivots only.

Actual alias 15007 runtime bind bounds including anchors: X-64..64, Y-255..6, Z-33..42. The model's overhead height is determined by the visible crown rather than an anchor outlier. Geometry recipes stay editable but require bounds/animation tests after changes. Do not reduce the mass factor or add too many fingers to a small bone group without checking compensation bounds.

## Runtime integration and validation

Production `NPCDefinition.java` calls the helper in both 164 overloads before vertex grouping, lighting and alias caching; generic tinting is skipped for owned models. The helper owns 15001 and 15007 and preserves the Furnace Sentinel geometry. Historical isolated staging snapshots remain under `Client/build/briar-next`; edit current production source for future changes.

`Server/build/briar-next/test/CustomBriarMeshCheck.java` passes 1,049 geometry checks, including exact pivot mass/count, nondegenerate original faces, source immutability and fixed-ground bind view. `CustomNpcRigCheck 15007 5487` passes 488 actual runtime/cache/animation checks across 62 frames/248 four-angle fixed-origin native views, including real NPC blended walk/action and alias/template cache reload isolation. Sequence 5487 is the verified branch-arm strike selected by the server profile. The one-tick 600 ms windup rechecks reach/LOS before damage; the model itself does not schedule damage. Tested idle 5483, walk 5481 and death 5491 reuse the same proven rig. Terminal death shrink/vanish matches the original rig.

Previews are under `Server/build/custom-briar-bind.png` and `custom-npc-15007-<role>-pageN.png`. Native attack page 1 was reviewed: the trunk leans, branch arm draws back and extends coherently. These tests do not establish final visual quality under live scene lighting, crowd density or every camera angle; live acceptance remains a separate gate. The design is a faceted bark creature, deliberately distinct from the construct, but finer organic sculpting/texturing is still future asset work.

To add another Exile creature, create a separate owned alias and named recipes, choose/probe its actual native rig, author an original silhouette with these primitives, retain exact pivots, verify footprint and all animation frames, and balance its server encounter/drop profile independently.


For gameplay balance, edit the 15007 override in `Notorious/Server/data/arpg/enemies.json` and restart the server. `attackAnimation` and `windupTicks` must remain consistent with verified motion/impact timing. Geometry, colors and bone bindings do not alter combat damage, HP or rewards.
