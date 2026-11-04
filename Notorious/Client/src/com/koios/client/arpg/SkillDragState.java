package com.koios.client.arpg;
/** Pointer state is separate from rendering so a release commits exactly once. */
public final class SkillDragState {
 public int page,selected,kind,skill=-1,support=-1,origin=-1;private int startX,startY;private boolean moved;
 public boolean active(){return kind!=0;}public boolean dragging(){return active()&&moved;}
 public void cancel(){kind=0;skill=support=origin=-1;moved=false;}
 public void begin(int x,int y,String[] state){cancel();if(y>=22&&y<44&&x>=300&&x<440){page=1-page;return;}startX=x;startY=y;int library=SkillPanel.libraryAt(x,y);if(library>=0&&SkillPanel.available(state,library)>0){kind=2;support=library;return;}int slot=SkillPanel.hotbarAt(x,y);if(slot>=0){kind=4;skill=SkillPanel.assignment(state,slot);if(skill<0){cancel();return;}selected=skill;origin=slot;return;}int card=SkillPanel.skillAt(x,y,page);if(card>=0){selected=card;int socket=SkillPanel.socketAt(x,y);if(socket>=0&&(SkillPanel.mask(state,card)&1<<socket)!=0){kind=3;skill=card;support=socket;}else{kind=1;skill=card;}}}
 public void move(int x,int y){if(active()&&(x-startX)*(x-startX)+(y-startY)*(y-startY)>=36)moved=true;}
 public String release(int x,int y){move(x,y);String command=null;if(moved){int target=SkillPanel.skillAt(x,y,page),slot=SkillPanel.hotbarAt(x,y);if((kind==1||kind==4)&&slot>=0)command="assign "+slot+" "+skill;else if(kind==4&&slot<0)command="clear "+origin;else if(kind==2&&target>=0)command="attach "+target+" "+support;else if(kind==3&&target>=0&&target!=skill)command="move "+skill+" "+target+" "+support;else if(kind==3&&SkillPanel.inLibrary(x,y))command="detach "+skill+" "+support;}cancel();return command;}
}
