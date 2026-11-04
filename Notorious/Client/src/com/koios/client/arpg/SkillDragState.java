package com.koios.client.arpg;
/** Pointer state is separate from rendering so a release commits exactly once. */
public final class SkillDragState {
 public int page,selected,kind,skill=-1,support=-1,origin=-1;private int startX,startY;private boolean moved;
 public boolean active(){return kind!=0;}public boolean dragging(){return active()&&moved;}
 public void cancel(){kind=0;skill=support=origin=-1;moved=false;}
 public void begin(int x,int y,String[] state){cancel();startX=x;startY=y;int slot=SkillPanel.loadoutSlotAt(x,y);if(slot<0)return;int equipped=SkillPanel.assignment(state,slot);selected=equipped;origin=slot;if(SkillPanel.activeSocketAt(x,y)==slot&&equipped>=0){kind=5;skill=equipped;return;}int socket=SkillPanel.socketAt(x,y);if(equipped>=0&&socket>=0&&(SkillPanel.mask(state,equipped)&1<<socket)!=0){kind=3;skill=equipped;support=socket;}}
 public void move(int x,int y){if(active()&&(x-startX)*(x-startX)+(y-startY)*(y-startY)>=36)moved=true;}
 public String release(int x,int y){move(x,y);cancel();return null;}
}
