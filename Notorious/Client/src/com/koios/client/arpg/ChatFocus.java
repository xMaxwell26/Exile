package com.koios.client.arpg;
/** Explicit chat ownership separates typing from gameplay bindings. */
public final class ChatFocus {
 public enum Action { TYPE, OPEN, SUBMIT, CANCEL, HEAVY, CLEAVE, WHIRLWIND, PANEL, PROTOTYPE, IGNORE }
 private boolean editing;
 public boolean isEditing(){return editing;}
 public void open(){editing=true;}
 public void close(){editing=false;}
 public Action key(int key){
  if(key==27){editing=false;return Action.CANCEL;}
  if(key==10||key==13){if(editing){editing=false;return Action.SUBMIT;}editing=true;return Action.OPEN;}
  if(editing)return Action.TYPE;
  if(key=='q'||key=='Q')return Action.HEAVY;
  if(key=='w'||key=='W')return Action.CLEAVE;
  if(key=='e'||key=='E')return Action.WHIRLWIND;
  if(key=='k'||key=='K')return Action.PANEL;
  if(key=='p'||key=='P')return Action.PROTOTYPE;
  return Action.IGNORE;
 }
}
