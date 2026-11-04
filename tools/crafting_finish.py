from pathlib import Path
p=Path('Notorious/Server/src/com/koios/game/arpg/PrototypePanel.java');s=p.read_text();s=s.replace('}else inventory(p,rows,false);','''}else{
     row(rows,"","Use a crafting material on equipment or a map in your inventory.");
     row(rows,"","Scroll of Wisdom: identify / Transmutation: normal to magic");
     row(rows,"","Alchemy: normal or magic to rare / Chaos: reroll modifiers");
     row(rows,"","Exalted: add a modifier / Vaal: corrupt (up to twice)");
     row(rows,"exchange","Exchange old rune materials for the new currencies");
     row(rows,"","Skill and support gems: select a gem below to upgrade a skill.");
     int shown=0;for(int i=0;i<28&&shown<10;i++){Item gem=p.getInventory().get(i);if(MapRuns.isGem(gem)){row(rows,"select "+i,MapRuns.name(gem));shown++;}}
    }''');s=s.replace('Crafting bench: choose an item and a currency action','Crafting: use a material directly on an inventory item');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/task/impl/PlayerDeathTask.java');s=p.read_text().replace('GroundItemManager.spawnNew(player, itemsToDrop.get(i).getId(), player.getX(), player.getY(), itemsToDrop.get(i).getCount());','GroundItemManager.spawnNew(player, itemsToDrop.get(i));');p.write_text(s)
p=Path('Notorious/Server/test/ArpgFoundationTest.java');s=p.read_text();needle='    System.out.println("Passed "+checks+';idx=s.index(needle);s=s[:idx]+'''    com.koios.client.arpg.PrototypePanel graph=new com.koios.client.arpg.PrototypePanel();graph.accept(java.util.Base64.getEncoder().encodeToString("tree|1|false".getBytes("UTF-8")));check("allocate 1".equals(graph.actionAt(211,132,false)),"Graph node hit area matches rendered node");check("refund 1".equals(graph.actionAt(211,132,true)),"Graph Ctrl click refunds same node");check(graph.actionAt(210,205,false)==null,"Empty graph space cannot allocate a node");
    for(int id:CraftingCurrency.IDS){check(ItemDefinition.forId(id).isStackable(),"Currency is stackable");check(ItemDefinition.forId(id).getName().contains("Orb")||ItemDefinition.forId(id).getName().contains("Scroll"),"Currency has an identifiable name");}
'''+s[idx:];p.write_text(s)
