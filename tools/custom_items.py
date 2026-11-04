from pathlib import Path
import json
p=Path('Notorious/Server/data/arpg/bases.json');data=json.loads(p.read_text());names=['Ashbound Sword','Duskfang Dagger','Gravebell Mace','Riftcleaver Axe','Ironward Carapace','Briarhide Vest','Aetherwoven Robe']
for i,base in enumerate(data):base['templateId']=base['itemId'];base['itemId']=22420+i;base['name']=names[i]
p.write_text(json.dumps(data,indent=2))
p=Path('Notorious/Server/src/com/koios/game/arpg/EquipmentBases.java');s=p.read_text().replace('int itemId;','int itemId,templateId;');idx=s.index(' public static String name(');s=s[:idx]+''' public static int template(int id){for(Base base:all())if(base.itemId==id)return base.templateId;return id;}
 public static void migrate(Item item){if(!item.hasRoll())return;ItemRoll roll=item.getRoll();if(roll.getMap()!=null&&item.getId()==8007)item.transform(22410);else if(roll.getGem()!=null&&(item.getId()==1623||item.getId()==1631))item.transform(roll.getGem().support?22412:22411);else for(ItemRoll.Affix affix:roll.getAffixes())if(affix.getLayer()==ItemRoll.Layer.BASE_IMPLICIT)for(Base base:all())if(affix.getId().equals("base_"+base.id)&&item.getId()==base.templateId)item.transform(base.itemId);}
'''+s[idx:];p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/def/ItemDefinition.java');s=p.read_text();needle='\t\tSystem.out.println("Finished reading';idx=s.index(needle);s=s[:idx]+'''  for(com.koios.game.arpg.EquipmentBases.Base base:com.koios.game.arpg.EquipmentBases.all())custom(base.itemId,base.templateId,base.name);
  custom(22410,8007,"Forsaken Settlement Map");custom(22411,1623,"Uncut Skill Gem");custom(22412,1631,"Uncut Support Gem");
'''+s[idx:];idx=s.index('\tpublic static ItemDefinition[] toArray');s=s[:idx]+''' private static void custom(int id,int template,String name){ItemDefinition source=definitions[template],d=definitions[id];d.name=name;d.description="An artifact from the forsaken lands.";d.stackable=false;d.noted=false;d.lended=false;d.untradeable=false;d.noteId=-1;d.lendId=-1;d.weapon=source.weapon;d.equipmentType=source.equipmentType;d.isTwoHanded=source.isTwoHanded;d.weight=source.weight;d.value=source.value;d.lowAlchemy=source.lowAlchemy;d.highAlchemy=source.highAlchemy;System.arraycopy(source.bonus,0,d.bonus,0,d.bonus.length);}

'''+s[idx:];p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/ItemRollCodec.java');s=p.read_text().replace('roll.validate();item.setRoll(roll);','roll.validate();item.setRoll(roll);EquipmentBases.migrate(item);');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/MapRuns.java');s=p.read_text().replace('MAP_BASE=8007','MAP_BASE=22410').replace('new Item(support?1631:1623)','new Item(support?22412:22411)');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/content/combat/melee/MeleeData.java');s=p.read_text();idx=s.index('public static int getWepAnim');a=s.index('\n\t\tOptional<ItemDefinition>',idx);s=s[:a]+'\n        weap=com.koios.game.arpg.EquipmentBases.template(weap);'+s[a:];p.write_text(s)
# client custom ranges, names and model material pass
p=Path('Notorious/Client/src/com/koios/client/definition/ItemDefinition.java');s=p.read_text();s=s.replace('if(i>=22400&&i<22407)return', 'if(com.koios.client.arpg.CustomEquipment.isCustom(i))return com.koios.client.arpg.EquipmentIcons.sprite(i);\n        if(i>=22400&&i<22407)return');s=s.replace('!(i>=22400&&i<22407)','!(i>=22400&&i<22407)&&!com.koios.client.arpg.CustomEquipment.isCustom(i)');s=s.replace('streamIndices[i>=22400','streamIndices[com.koios.client.arpg.CustomEquipment.isCustom(i)?com.koios.client.arpg.CustomEquipment.template(i):i>=22400');needle='\t\treturn itemDef;\n\t}';idx=s.index(needle,s.index('public static ItemDefinition forID'));s=s[:idx]+'''  if(com.koios.client.arpg.CustomEquipment.isCustom(i)){itemDef.name=com.koios.client.arpg.CustomEquipment.name(i);itemDef.description="An artifact from the forsaken lands.";itemDef.stackable=false;itemDef.stackIDs=null;itemDef.stackAmounts=null;itemDef.certID=-1;itemDef.certTemplateID=-1;itemDef.lendID=-1;itemDef.lendTemplateID=-1;itemDef.groundActions=new String[]{null,null,"Take",null,null};itemDef.itemActions=new String[]{null,i>=22420?"Wear":null,null,null,"Drop"};}
'''+s[idx:]
for signature,end in [('public Model method194','public boolean method195'),('public Model method196','private void setDefaults'),('public Model method202','public ItemDefinition()')]:
 a=s.index(signature);b=s.index(end,a);section=s[a:b];idx=section.rindex('\t\treturn model;');section=section[:idx]+'        com.koios.client.arpg.CustomEquipment.style(model,id);\n'+section[idx:];s=s[:a]+section+s[b:]
s=s.replace('\t\tmodel.method479(64 + anInt196,','        com.koios.client.arpg.CustomEquipment.style(model,id);\n\t\tmodel.method479(64 + anInt196,');p.write_text(s)
p=Path('Notorious/Client/src/com/koios/client/arpg/CustomEquipment.java');p.write_text('''package com.koios.client.arpg;
import com.koios.client.world.Model;
public final class CustomEquipment {
 public static final int[] IDS={22420,22421,22422,22423,22424,22425,22426,22410,22411,22412};
 private static final int[] TEMPLATES={1277,1205,1420,1351,1115,1129,577,8007,1623,1631};
 private static final String[] NAMES={"Ashbound Sword","Duskfang Dagger","Gravebell Mace","Riftcleaver Axe","Ironward Carapace","Briarhide Vest","Aetherwoven Robe","Forsaken Settlement Map","Uncut Skill Gem","Uncut Support Gem"};
 public static int index(int id){for(int i=0;i<IDS.length;i++)if(IDS[i]==id)return i;return -1;}
 public static boolean isCustom(int id){return index(id)>=0;}
 public static int template(int id){int index=index(id);return index<0?id:TEMPLATES[index];}
 public static String name(int id){int index=index(id);return index<0?"Artifact":NAMES[index];}
 public static void style(Model model,int id){int index=index(id);if(index<0||model==null||model.anIntArray1640==null)return;model.anIntArray1640=model.anIntArray1640.clone();int[] hue={32,48,5,1,35,15,43,9,34,6},sat={4,5,4,5,3,3,5,4,5,5};for(int i=0;i<model.anIntArray1640.length;i++){int color=model.anIntArray1640[i];if(color<2||color>=65534||model.anIntArray1637!=null&&(model.anIntArray1637[i]&2)!=0)continue;boolean skin=false;for(int body:new int[]{4550,4537,5681,5673,5790,6806,8076,4574})if(color==body)skin=true;if(skin)continue;int light=Math.max(16,Math.min(96,(color&127)+(i%17==0?16:-8)));model.anIntArray1640[i]=(hue[index]<<10)|(sat[index]<<7)|light;}}
}
''')
