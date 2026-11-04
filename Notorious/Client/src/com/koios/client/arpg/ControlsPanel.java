package com.koios.client.arpg;
import java.awt.*;import java.awt.event.*;import java.io.*;import com.koios.client.graphic.*;

/** Modal controls editor. Caller routes capture before gameplay/chat, and suppresses typed characters. */
public final class ControlsPanel {
 private final InputBindings bindings;private int waiting=-1,conflict=-1;private InputBindings.Binding pending;private String status="Click a binding, then press a key or mouse button.";private boolean closed;
 public ControlsPanel(InputBindings bindings){if(bindings==null)throw new IllegalArgumentException("bindings");this.bindings=bindings;}
 public boolean isClosed(){return closed;}public boolean isCapturing(){return waiting>=0;}public boolean hasConflict(){return conflict>=0;}public int waitingSlot(){return waiting;}
 public String status(){return status;}public void cancelCapture(){waiting=-1;conflict=-1;pending=null;status="Binding capture cancelled.";}
 public void focusLost(){cancelCapture();}
 public void beginCapture(int slot){if(slot<0||slot>=3)throw new IllegalArgumentException("slot");waiting=slot;conflict=-1;pending=null;status="Press a key or mouse button. Escape cancels.";}
 /** True means consumed. Enter remains reserved; modifiers alone keep capture waiting. */
 public boolean captureKey(int code,int modifiers){if(waiting<0)return false;if(code==KeyEvent.VK_ESCAPE){cancelCapture();return true;}if(conflict>=0)return true;if(InputBindings.modifierKey(code))return true;InputBindings.Binding binding=InputBindings.key(code,modifiers);if(binding==null){status="Enter is reserved for chat. Choose another input.";return true;}accept(binding);return true;}
 public boolean captureMouse(int button,int modifiers){if(waiting<0)return false;if(conflict>=0)return true;InputBindings.Binding binding=InputBindings.mouse(button,modifiers);if(binding==null){status="This mouse button is not supported by AWT.";return true;}accept(binding);return true;}
 private void accept(InputBindings.Binding binding){int other=bindings.conflict(waiting,binding);if(other>=0){conflict=other;pending=binding;status="Already used by skill slot "+(other+1)+". Replace it?";return;}bindings.assign(waiting,binding,false);waiting=-1;save();}
 public void confirmReplace(){if(conflict<0)return;bindings.assign(waiting,pending,true);waiting=-1;conflict=-1;pending=null;save();}
 private void save(){try{bindings.save();status="Controls saved for this character account.";}catch(IOException ex){status="Applied for this session. Could not save controls.";}}
 /** Coordinates are local to the same512x334 panel canvas used by SkillPanel. */
 public boolean click(int x,int y){if(x<16||x>=496||y<16||y>=322)return false;if(x>=466&&y<48){cancelCapture();closed=true;return true;}if(conflict>=0){if(y>=246&&y<270){if(x>=252&&x<356)confirmReplace();else if(x>=366&&x<478)cancelCapture();}return true;}if(waiting>=0)return true;for(int slot=0;slot<3;slot++){int top=76+slot*50;if(y>=top&&y<top+40){if(x>=252&&x<396)beginCapture(slot);else if(x>=408&&x<478){bindings.clear(slot);save();}return true;}}if(x>=28&&x<159&&y>=276&&y<302){bindings.reset();save();}return true;}
 public static int mouseButtonCount(){try{return MouseInfo.getNumberOfButtons();}catch(HeadlessException|SecurityException ex){return -1;}}
 public void draw(TextDrawingArea font){box(16,16,480,306,ExileTheme.GOLD,ExileTheme.PANEL);text(font,28,38,"SKILL CONTROLS",ExileTheme.HEADING);text(font,476,38,"X",0xC5CCD2);text(font,28,58,"Bindings activate the three hotbar slots.",ExileTheme.MUTED);
  for(int slot=0;slot<3;slot++){int y=76+slot*50;box(28,y,450,40,waiting==slot?ExileTheme.GOLD:ExileTheme.BORDER,ExileTheme.SURFACE);text(font,40,y+24,"Skill slot "+(slot+1),ExileTheme.TEXT);box(252,y+5,144,30,waiting==slot?ExileTheme.GOLD:ExileTheme.BORDER,ExileTheme.OXBLOOD);String label=waiting==slot?(conflict>=0?"Confirm replacement":"Waiting for input..."):bindings.label(slot);while(font.getTextWidth(label)>132&&label.length()>3)label=label.substring(0,label.length()-2);text(font,259,y+25,label,ExileTheme.GOLD);box(408,y+5,70,30,ExileTheme.BORDER,ExileTheme.SURFACE);text(font,424,y+25,"Clear",ExileTheme.MUTED);}
  text(font,28,239,status,conflict>=0?ExileTheme.GOLD:0xA5C7DF);if(conflict>=0){button(font,252,246,104,"Replace",ExileTheme.GOLD);button(font,366,246,112,"Cancel",ExileTheme.TEXT);}else text(font,28,256,"Use Shift, Ctrl, Alt or Meta with keys or mouse buttons.",ExileTheme.DIM);
  button(font,28,276,131,"Reset to Q / W / E",ExileTheme.TEXT);int count=mouseButtonCount();text(font,176,292,count<0?"Mouse button count unavailable":"AWT reports "+count+" mouse buttons",ExileTheme.DIM);text(font,28,315,"Enter: chat / Escape: cancel / Mouse binds: world only",ExileTheme.DIM);
 }
 private static void button(TextDrawingArea f,int x,int y,int w,String s,int color){box(x,y,w,26,ExileTheme.BORDER,ExileTheme.OXBLOOD);text(f,x+8,y+18,s,color);}
 private static void box(int x,int y,int w,int h,int border,int fill){ExileTheme.panel(x,y,w,h,border,fill);}
 private static void text(TextDrawingArea f,int x,int y,String s,int color){ExileTheme.label(f,x,y,s,color);}
}
