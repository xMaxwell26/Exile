package com.koios.client.arpg;
import java.awt.*;import java.awt.geom.*;import java.awt.image.BufferedImage;
import com.koios.client.graphic.Sprite;
/** Original blue resource orb, sharing the minimap's existing hit target. */
public final class ManaOrb {
 private static final Sprite[] CACHE=new Sprite[101];
 public static Sprite sprite(double current,double maximum){
  int percent=maximum<=0?0:(int)Math.round(Math.max(0,Math.min(1,current/maximum))*100);if(CACHE[percent]!=null)return CACHE[percent];
  BufferedImage image=new BufferedImage(112,112,BufferedImage.TYPE_INT_ARGB);Graphics2D g=image.createGraphics();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
  g.setColor(new Color(0x0E1924));g.fillOval(3,3,106,106);g.setStroke(new BasicStroke(5));g.setColor(new Color(0x5B7889));g.drawOval(5,5,102,102);
  g.setClip(new Ellipse2D.Double(12,12,88,88));int top=100-(int)Math.round(88*percent/100.0);g.setPaint(new GradientPaint(12,top,new Color(0x52AED3),88,100,new Color(0x183E83)));g.fillRect(12,top,88,100-top);if(percent>0){g.setColor(new Color(0xACECF1));g.setStroke(new BasicStroke(2));g.drawLine(12,top+1,100,top+1);}g.setClip(null);
  Path2D gem=new Path2D.Double();gem.moveTo(56,29);gem.lineTo(70,55);gem.lineTo(56,77);gem.lineTo(42,55);gem.closePath();g.setColor(new Color(0xD2F3FA));g.setStroke(new BasicStroke(4));g.draw(gem);g.drawLine(56,31,56,75);g.dispose();
  BufferedImage small=new BufferedImage(28,28,BufferedImage.TYPE_INT_ARGB);g=small.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.drawImage(image,0,0,28,28,null);g.dispose();Sprite result=new Sprite(28,28);int[] pixels=small.getRGB(0,0,28,28,null,0,28);for(int i=0;i<pixels.length;i++)result.myPixels[i]=(pixels[i]>>>24)>80?pixels[i]&0xFFFFFF:0;result.highResolutionArt=image;return CACHE[percent]=result;
 }
 public static double number(String[] state,int index,double fallback){try{return Math.max(0,Double.parseDouble(state[index]));}catch(Exception ex){return fallback;}}
}
