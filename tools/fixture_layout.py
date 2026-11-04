from pathlib import Path
r=Path('Notorious/Server/src/com/koios')
p=r/'game/arpg/MapRuns.java';s=p.read_text();a=s.index('   List<Location> cells=reachable(base.spawn,8);');b=s.index('\n  }',a);s=s[:a]+'''   List<Location> cells=reachable(base.spawn,8);
   for(int fixtureId:new int[]{409,13704,3193}){
    ObjectDefinition definition=ObjectDefinition.getById(fixtureId);int width=definition.xLength,height=definition.yLength;
    for(Location cell:cells){
     if(Location.getManhattanDistance(cell,base.spawn)<2)continue;boolean clear=true;
     for(int dx=0;dx<width;dx++)for(int dy=0;dy<height;dy++)if(MapRegion.getClipping(cell.getX()+dx,cell.getY()+dy,0)!=0)clear=false;
     if(!clear)continue;base.fixtures.add(cell);MapRegion.getRegion(cell.getX(),cell.getY()).addObject(new RSObject(cell.getX(),cell.getY(),0,fixtureId,10,0));
     for(int dx=0;dx<width;dx++)for(int dy=0;dy<height;dy++)MapRegion.getRegion(cell.getX()+dx,cell.getY()+dy).setClip(cell.getX()+dx,cell.getY()+dy,0,0x100);
     break;
    }
   }
   if(base.fixtures.size()!=3)throw new IllegalStateException("Base fixtures did not fit the layout");'''+s[b:];s=s.replace('i==1?14930:3193','i==1?13704:3193');p.write_text(s)
p=Path('Notorious/Client/src/com/koios/client/arpg/ItemTooltipRenderer.java');s=p.read_text().replace('int lineHeight=14,height=lines.length*lineHeight+24;', 'int lineHeight=Math.max(10,Math.min(14,(DrawingArea.height-36)/Math.max(1,lines.length))),height=lines.length*lineHeight+24;');p.write_text(s)
p=Path('Notorious/Client/src/com/koios/client/arpg/PrototypePanel.java');s=p.read_text().replace('DrawingArea.drawPixels(24,y,x,owned?', 'int parent=Integer.parseInt(f[1]);if(parent>0){int parentY=66+((parent-1)%7)*28;DrawingArea.method341(parentY+12,0x65717A,y-parentY,x-5);DrawingArea.method339(y+12,0x65717A,5,x-5);DrawingArea.method339(parentY+12,0x65717A,5,x-5);}\n    DrawingArea.drawPixels(24,y,x,owned?');p.write_text(s)
p=Path('Notorious/Client/src/com/koios/client/Client.java');s=p.read_text().replace('arpgSkillsOpen=!arpgSkillsOpen;if(arpgSkillsOpen)sendArpgSkill("status");','arpgSkillsOpen=!arpgSkillsOpen;if(arpgSkillsOpen){prototypeOpen=false;sendArpgSkill("status");}');p.write_text(s)
p=Path('Notorious/Server/test/PrototypeIntegrationCheck.java');s=p.read_text().replace('new int[]{409,14930,3193}','new int[]{409,13704,3193}');p.write_text(s)
