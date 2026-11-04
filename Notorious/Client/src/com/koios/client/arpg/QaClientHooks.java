package com.koios.client.arpg;

import java.awt.*;import java.awt.image.BufferedImage;import java.nio.file.*;import java.nio.charset.StandardCharsets;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.Client;import com.koios.client.ClientStatus;

/** Explicit LOCAL fixture instrumentation: no desktop input, no user account data. */
public final class QaClientHooks {
 private static long nextPoll,nextCapture,nextState,eventCaptureAt;private static int oldWidth,oldHeight,eventSequence;private static String eventLabel="effect";
 public static void event(String label){if(enabled()&&eventCaptureAt==0){eventLabel=label.replaceAll("[^a-zA-Z0-9-]","");eventCaptureAt=System.currentTimeMillis()+140;}}
 private static final Map<String,Pane> panes=new LinkedHashMap<>();
 private static final class Pane {BufferedImage image;int[] pixels;int x,y;Pane(BufferedImage image,int[] pixels,int x,int y){this.image=image;this.pixels=pixels;this.x=x;this.y=y;}}
 public static boolean enabled(){return Boolean.getBoolean("arpg.qaCapture")&&Client.clientStatus==ClientStatus.LOCAL&&Client.instance!=null&&Client.instance.isQaFixture();}
 public static Path directory(){return Paths.get("build","qa");}
 public static java.util.List<String> commands(){
  if(!enabled()||System.currentTimeMillis()<nextPoll)return Collections.emptyList();nextPoll=System.currentTimeMillis()+300;
  Path input=directory().resolve(System.getProperty("arpg.qaCharacter")+".commands");
  try{if(!Files.isRegularFile(input))return Collections.emptyList();java.util.List<String> lines=Files.readAllLines(input,StandardCharsets.UTF_8);Files.move(input,directory().resolve(System.getProperty("arpg.qaCharacter")+".last-commands"),StandardCopyOption.REPLACE_EXISTING);return lines;}catch(Exception failure){System.err.println("QA command read: "+failure);return Collections.emptyList();}
 }
 public static void state(String text){if(!enabled()||System.currentTimeMillis()<nextState)return;nextState=System.currentTimeMillis()+1000;try{Files.createDirectories(directory());Files.write(directory().resolve(System.getProperty("arpg.qaCharacter")+".state.txt"),text.getBytes(StandardCharsets.UTF_8));}catch(Exception failure){System.err.println("QA state export: "+failure);}}
 public static void present(BufferedImage image,int[] pixels,int x,int y,Graphics display){
  if(!enabled())return;
  if(oldWidth!=Client.frameWidth||oldHeight!=Client.frameHeight){panes.clear();oldWidth=Client.frameWidth;oldHeight=Client.frameHeight;}
  panes.put(x+":"+y+":"+image.getWidth()+":"+image.getHeight(),new Pane(image,pixels,x,y));
  boolean eventFrame=eventCaptureAt!=0&&System.currentTimeMillis()>=eventCaptureAt;
  if(!eventFrame&&System.currentTimeMillis()<nextCapture)return;nextCapture=System.currentTimeMillis()+1800;
  double scale=display instanceof Graphics2D?Math.abs(((Graphics2D)display).getTransform().getScaleX()):1;
  if(scale<.5||scale>4)scale=1;
  BufferedImage frame=new BufferedImage(Math.max(1,(int)Math.round(Client.frameWidth*scale)),Math.max(1,(int)Math.round(Client.frameHeight*scale)),BufferedImage.TYPE_INT_RGB);
  Graphics2D g=frame.createGraphics();g.scale(scale,scale);
  for(Pane pane:panes.values()){g.drawImage(pane.image,pane.x,pane.y,null);HighDpiArt.draw(g,pane.pixels,pane.x,pane.y);}
  g.dispose();try{Files.createDirectories(directory());Path temporary=directory().resolve(System.getProperty("arpg.qaCharacter")+".frame.tmp.png"),target=directory().resolve(System.getProperty("arpg.qaCharacter")+".frame.png");ImageIO.write(frame,"png",temporary.toFile());Files.move(temporary,target,StandardCopyOption.REPLACE_EXISTING);if(eventFrame){Files.copy(target,directory().resolve(System.getProperty("arpg.qaCharacter")+".event-"+(eventSequence++%12)+"-"+eventLabel+".png"),StandardCopyOption.REPLACE_EXISTING);eventCaptureAt=0;}}catch(Exception failure){System.err.println("QA frame export: "+failure);}
 }
}
