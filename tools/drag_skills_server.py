from pathlib import Path
p=Path('Notorious/Server/src/com/koios/game/arpg/SkillState.java');s=p.read_text();idx=s.index(' private int actionLock;');s=s[:idx]+''' private final int[] hotbar={0,1,2};
 public int assigned(int slot){if(slot<0||slot>=3)throw new IllegalArgumentException("Invalid hotbar slot");return hotbar[slot];}
 public void assign(int slot,int skill){if(slot<0||slot>=3||skill<0||skill>=3)throw new IllegalArgumentException("Invalid skill assignment");int old=hotbar[slot];for(int i=0;i<3;i++)if(hotbar[i]==skill){hotbar[i]=old;break;}hotbar[slot]=skill;active=true;}
 public String keyFor(int skill){for(int i=0;i<3;i++)if(hotbar[i]==skill)return new String[]{"Q","W","E"}[i];return "?";}
 public String saveHotbar(){return hotbar[0]+","+hotbar[1]+","+hotbar[2];}
 public void loadHotbar(String value){String[] f=value.split(",");if(f.length!=3)throw new IllegalArgumentException("Invalid hotbar save");boolean[] used=new boolean[3];int[] next=new int[3];for(int i=0;i<3;i++){int id=Integer.parseInt(f[i]);if(id<0||id>=3||used[id])throw new IllegalArgumentException("Invalid hotbar save");next[i]=id;used[id]=true;}System.arraycopy(next,0,hotbar,0,3);}
 public void attach(int skill,int support,boolean enabled){if(skill<0||skill>=3||support<0||support>=3)throw new IllegalArgumentException("Invalid support assignment");if(enabled)supports[skill]|=1<<support;else supports[skill]&=~(1<<support);active=true;}
 public void moveSupport(int from,int to,int support){if(from<0||from>=3||to<0||to>=3||support<0||support>=3||(supports[from]&(1<<support))==0)throw new IllegalArgumentException("Invalid support move");if(from!=to){attach(from,support,false);attach(to,support,true);}}
'''+s[idx:];s=s.replace('for(int i=0;i<2;i++)if(cooldowns[i]>0)','for(int i=0;i<3;i++)if(cooldowns[i]>0)');s=s.replace('  if(payload.equals(lastPayload))','  payload+="|"+hotbar[0]+"|"+hotbar[1]+"|"+hotbar[2];\n  if(payload.equals(lastPayload))');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/ArpgSkills.java');s=p.read_text();s=s.replace('  try{\n   if(args.length', '  try{\n   if(!args[1].equals("status")&&(p.isDead()||p.isTrading()||p.getBankField().isBanking()||p.isTeleporting()))return true;\n   if(args.length',1);s=s.replace('   else if(args.length==3&&args[1].equals("cast"))', '''   else if(args.length==4&&args[1].equals("assign"))p.getArpgSkills().assign(Integer.parseInt(args[2]),Integer.parseInt(args[3]));
   else if(args.length==4&&(args[1].equals("attach")||args[1].equals("detach"))){p.getArpgSkills().attach(Integer.parseInt(args[2]),Integer.parseInt(args[3]),args[1].equals("attach"));if(p.getArpgSkills().getSupport(0)!=0)Tutorial.action(p,"support");}
   else if(args.length==5&&args[1].equals("move")){p.getArpgSkills().moveSupport(Integer.parseInt(args[2]),Integer.parseInt(args[3]),Integer.parseInt(args[4]));if(p.getArpgSkills().getSupport(0)!=0)Tutorial.action(p,"support");}
   else if(args.length==3&&args[1].equals("slot"))cast(p,p.getArpgSkills().assigned(Integer.parseInt(args[2])));
   else if(args.length==3&&args[1].equals("cast"))''');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/model/player/io/PlayerSaver.java');s=p.read_text().replace('writeValue(bw,"essential-arpg-gems",', 'writeValue(bw,"essential-arpg-hotbar",player.getArpgSkills().saveHotbar());\n                writeValue(bw,"essential-arpg-gems",');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/model/player/io/PlayerLoader.java');s=p.read_text().replace('case "essential-arpg-gems":', 'case "essential-arpg-hotbar":player.getArpgSkills().loadHotbar(value);break;\n        case "essential-arpg-gems":');p.write_text(s)
p=Path('Notorious/Server/src/com/koios/game/arpg/Tutorial.java');s=p.read_text().replace('Press K, select Heavy Strike, toggle a support.','Press K; drag a support onto Heavy Strike.');s=s.replace('String[] detail=lines[s].split("\\\\|");return', 'String[] detail=lines[s].split("\\\\|");if(s==5)detail[1]="Use "+p.getArpgSkills().keyFor(0)+" Heavy Strike and "+p.getArpgSkills().keyFor(1)+" Cleave.";return');p.write_text(s)
p=Path('Notorious/Client/src/com/koios/client/arpg/SkillDragState.java');p.write_text('''package com.koios.client.arpg;
/** Pointer state is separate from rendering so a release commits exactly once. */
public final class SkillDragState {
 public int selected,kind,skill=-1,support=-1,origin=-1;private int startX,startY;private boolean moved;
 public boolean active(){return kind!=0;}public boolean dragging(){return active()&&moved;}
 public void cancel(){kind=0;skill=support=origin=-1;moved=false;}
 public void begin(int x,int y,String[] state){cancel();startX=x;startY=y;int library=SkillPanel.libraryAt(x,y);if(library>=0){kind=2;support=library;return;}int slot=SkillPanel.hotbarAt(x,y);if(slot>=0){kind=4;skill=SkillPanel.assignment(state,slot);selected=skill;origin=slot;return;}int card=SkillPanel.skillAt(x,y);if(card>=0){selected=card;int socket=SkillPanel.socketAt(x,y);if(socket>=0&&(SkillPanel.mask(state,card)&1<<socket)!=0){kind=3;skill=card;support=socket;}else{kind=1;skill=card;}}}
 public void move(int x,int y){if(active()&&(x-startX)*(x-startX)+(y-startY)*(y-startY)>=36)moved=true;}
 public String release(int x,int y){move(x,y);String command=null;if(moved){int target=SkillPanel.skillAt(x,y),slot=SkillPanel.hotbarAt(x,y);if((kind==1||kind==4)&&slot>=0)command="assign "+slot+" "+skill;else if(kind==2&&target>=0)command="attach "+target+" "+support;else if(kind==3&&target>=0&&target!=skill)command="move "+skill+" "+target+" "+support;else if(kind==3&&SkillPanel.inLibrary(x,y))command="detach "+skill+" "+support;}cancel();return command;}
}
''')
