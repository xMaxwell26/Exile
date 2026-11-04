from pathlib import Path
p=Path('dev.ps1');s=p.read_text().replace('test/ArpgFoundationTest.java test/PrototypeIntegrationCheck.java','test/ArpgFoundationTest.java test/PrototypeIntegrationCheck.java test/CustomItemVisualCheck.java test/ItemInventoryArtCheck.java');needle="        if ($LASTEXITCODE -ne 0) { throw 'Prototype integration checks failed.' }";s=s.replace(needle,needle+'''
        foreach ($visualCheck in @('CustomItemVisualCheck','ItemInventoryArtCheck')) {
            & (Join-Path $JdkPath 'bin/java.exe') -Djava.awt.headless=true -cp 'build/test-classes;build/classes;lib/*;../Client/build/classes' $visualCheck
            if ($LASTEXITCODE -ne 0) { throw "$visualCheck failed." }
        }''');p.write_text(s)
p=Path('PROTOTYPE-GUIDE.md');s=p.read_text();s+='''

## Illustrated item art

Equipment, map items, skill/support gems and crafting currencies now have illustrated inventory icons with consistent custom names and separate IDs. Existing saved prototype gear adopts the new IDs on load, preserving rolls and item identity. Old rune crafting materials can be exchanged under P / Guide. Ordinary RuneScape items keep their normal definitions. The skills/loadout interface is unchanged.

Use `::arpgbases` for the seven equipment samples. Full art assets, generation prompts and runtime import details are recorded in ITEM-ART.md. Ground/worn equipment has a material recolor pass on existing cache geometry; fully authored 3D meshes remain separate work.
''';p.write_text(s)
