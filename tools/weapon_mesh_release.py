from pathlib import Path
p=Path('Notorious/Client/src/com/koios/client/arpg/CustomWeaponModels.java');s=p.read_text().replace('IRON=hsl(35,2,35),EDGE=hsl(33,2,82),GOLD=hsl(8,4,54),LEATHER=hsl(5,2,23),CYAN=hsl(32,6,65),VIOLET=hsl(48,5,54),RED=hsl(1,6,44),EMBER=hsl(6,6,60)','IRON=hsl(35,1,24),EDGE=hsl(33,1,66),GOLD=hsl(8,3,38),LEATHER=hsl(5,2,17),CYAN=hsl(32,5,42),VIOLET=hsl(48,4,36),RED=hsl(0,5,28),EMBER=hsl(5,6,45)');p.write_text(s)
p=Path('dev.ps1');s=p.read_text().replace('test/CustomItemVisualCheck.java test/ItemInventoryArtCheck.java','test/CustomItemVisualCheck.java test/ItemInventoryArtCheck.java test/WeaponMeshCheck.java').replace("@('CustomItemVisualCheck','ItemInventoryArtCheck')","@('WeaponMeshCheck','ItemInventoryArtCheck')");p.write_text(s)
p=Path('ITEM-ART.md');s=p.read_text().replace('Ground and equipped models currently use tinted cache geometry; original 3D meshes remain future work.','The four melee weapons now use authored low-poly geometry for ground and equipped rendering. Armour and utility item meshes still use tinted cache geometry.');s+='''

## Authored weapon meshes

`CustomWeaponModels.java` builds four original meshes: a chipped Ashbound blade with cyan inlay, a hooked Duskfang blade, an eight-flange Gravebell mace with ember core, and a crescent Riftcleaver axe with red inlay. These replace the cached weapon geometry; they are not just recolors. The approved inventory PNGs and loadout interface remain unchanged.

For worn items, the template supplies only the grip anchor and weapon-hand animation group (50). Every authored vertex binds to that group. Separate cache material styling continues for armour. Dropped weapons use the same authored shape with a ground-space orientation. Polygon triangulation, integer vertex cleanup, native lighting/rendering and skeletal group transforms are covered by WeaponMeshCheck.

`dev.ps1 Test` includes cache/atlas checks and authored-mesh checks. The native renderer preview is `Notorious/Server/build/weapon-meshes.png`. Live movement, attack animation and body clipping still need an in-game check. Original armour and currency/gem meshes remain later work.
''';p.write_text(s)
p=Path('PROTOTYPE-GUIDE.md');s=p.read_text();s+='''

The four melee weapons now have authored in-world meshes matching their item identities: chipped sword, hooked dagger, flanged mace and crescent axe. Inventory art and skills/loadout controls remain as approved. Use `::arpgbases` to get comparison samples, then equip each weapon and check walking, attacks and ground drops. Male/female weapon-hand binding and native rendering are tested; armour still uses recolored cache meshes.
''';p.write_text(s)
p=Path('TODO.md');s=p.read_text();s+='''
## Authored weapon geometry

- Four weapon meshes now replace the source geometry in ground and male/female equipment rendering. Validate hand grip and clipping during idle, movement, Q/W/E casts and normal attacks.
- Native mesh rendering and weapon-hand group transforms are covered by WeaponMeshCheck. Inventory art and skills/loadout UI stay unchanged.
- Author armour silhouettes and currency/gem ground models next; these still use cached shapes.
''';p.write_text(s)
