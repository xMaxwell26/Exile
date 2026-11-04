from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
p=r/'game/arpg/ArpgSkills.java';s=p.read_text().replace('double effectiveness=state.effectiveness(skill)*(m!=primary&&skill==0?0.5:1);','double effectiveness=state.effectiveness(skill)*(m!=primary&&skill==0?0.5:1)*(splash&&m!=primary?1+0.02*(state.supportLevel(0)-1):1);');p.write_text(s)
p=r/'game/arpg/PrototypePanel.java';s=p.read_text().replace('if(MapRuns.isMap(item))return old.changed(rarity,true,0,old.getAffixes(),new ItemRoll.MapData(old.getMap().tier,1+random.nextInt(7)));','if(MapRuns.isMap(item)){int mods=1+random.nextInt(7);if(rarity==ItemRoll.Rarity.MAGIC&&Integer.bitCount(mods)>2)mods&=~Integer.lowestOneBit(mods);return old.changed(rarity,true,0,old.getAffixes(),new ItemRoll.MapData(old.getMap().tier,mods));}');p.write_text(s)
p=Path('Notorious/Server/test/ArpgFoundationTest.java');s=p.read_text();mark='System.out.println("Passed "+checks+';s=s.replace(mark,'''PassiveTree tree=new PassiveTree();tree.allocate(1,3);tree.allocate(2,3);tree.allocate(3,3);rejects(()->tree.allocate(4,3));rejects(()->tree.refund(1));tree.refund(3);tree.refund(2);tree.allocate(8,3);
  PassiveTree loadedTree=new PassiveTree();loadedTree.load(tree.save(),3);check(loadedTree.has(1)&&loadedTree.has(8)&&loadedTree.spent()==2,"Passive allocation persists");
  rejects(()->loadedTree.load("2",3));check(loadedTree.has(1)&&loadedTree.has(8),"Invalid passive load is atomic");
  for(int seed=0;seed<100;seed++){
   Item rolled=AffixCatalog.equipment(1,seed%2==0,new Random(seed));AffixRules.validateGenerated(rolled.getRoll());
   for(ItemRoll.Affix affix:rolled.getRoll().getAffixes())if(affix.getLayer()==ItemRoll.Layer.PREFIX||affix.getLayer()==ItemRoll.Layer.SUFFIX)check(affix.getTier()==10,"Item level gates starter tiers");
  }
  Item map=MapRuns.map(3,5);Item mapLoaded=ItemRollCodec.restore(new Item(map.getId()),new String[]{ItemRollCodec.encode(map)},0);check(MapRuns.isMap(mapLoaded)&&mapLoaded.getRoll().getMap().tier==3&&mapLoaded.getRoll().getMap().mods==5,"Map metadata survives save round trip");
  Item gem=MapRuns.gem(5,false);Item gemLoaded=ItemRollCodec.restore(new Item(gem.getId()),new String[]{ItemRollCodec.encode(gem)},0);check(MapRuns.isGem(gemLoaded)&&gemLoaded.getRoll().getGem().level==5,"Gem metadata survives save round trip");
  SkillState upgraded=new SkillState();rejects(()->upgraded.upgrade(0,5,1));upgraded.upgrade(0,5,20);check(upgraded.skillLevel(0)==5&&upgraded.effectiveness(0)>1.8,"Higher skill gem scales weapon effectiveness");
  SkillState restoredSkills=new SkillState();restoredSkills.loadLevels(upgraded.saveLevels());check(restoredSkills.skillLevel(0)==5,"Skill upgrades persist");
  RewardMailbox mailbox=new RewardMailbox();mailbox.add(map);mailbox.add(gem);RewardMailbox restoredMailbox=new RewardMailbox();restoredMailbox.load(mailbox.save());check(restoredMailbox.size()==2,"Overflow rewards persist");
  com.koios.client.arpg.ChatFocus focus=new com.koios.client.arpg.ChatFocus();check(focus.key('p')==com.koios.client.arpg.ChatFocus.Action.PROTOTYPE,"P opens prototype hub outside chat");focus.open();check(focus.key('p')==com.koios.client.arpg.ChatFocus.Action.TYPE,"P types when chat is active");
  System.out.println("Passed "+checks+''');p.write_text(s)
