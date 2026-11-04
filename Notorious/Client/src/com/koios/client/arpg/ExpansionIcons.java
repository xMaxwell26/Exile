package com.koios.client.arpg;
import java.awt.*;import java.awt.geom.AffineTransform;import java.awt.image.BufferedImage;import java.io.*;import java.util.*;import javax.imageio.ImageIO;
import com.koios.client.graphic.Sprite;
/** Eight original expansion illustrations; existing approved atlases remain unchanged. */
public final class ExpansionIcons {
 private static final int[] IDS={22430,22431,22432,22435,22436,22437,22438,22439};
 private static final Map<Integer,Sprite> CACHE=new HashMap<>();private static BufferedImage atlas;
 public static boolean supports(int id){for(int value:IDS)if(value==id)return true;return false;}
 public static Sprite icon(int id){
  if(CACHE.containsKey(id))return CACHE.get(id);int tile=-1;for(int i=0;i<IDS.length;i++)if(IDS[i]==id)tile=i;if(tile<0)return null;
  try{if(atlas==null){File path=new File(System.getProperty("user.home"),"koios-cache/arpg-art/expansion-equipment-v1.png");if(!path.isFile())path=new File("../koios-cache/arpg-art/expansion-equipment-v1.png");atlas=ImageIO.read(path);}int left=tile%4*atlas.getWidth()/4,right=(tile%4+1)*atlas.getWidth()/4,top=tile/4*atlas.getHeight()/2,bottom=(tile/4+1)*atlas.getHeight()/2,minX=right,minY=bottom,maxX=-1,maxY=-1;
   for(int y=top;y<bottom;y++)for(int x=left;x<right;x++)if((atlas.getRGB(x,y)>>>24)>=160){minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y);}if(maxX<minX)throw new IOException("Empty expansion icon "+id);
   BufferedImage cut=atlas.getSubimage(minX,minY,maxX-minX+1,maxY-minY+1);boolean weapon=tile<6;double angle=weapon?-Math.PI/4:0,c=Math.abs(Math.cos(angle)),s=Math.abs(Math.sin(angle)),boundsW=cut.getWidth()*c+cut.getHeight()*s,boundsH=cut.getWidth()*s+cut.getHeight()*c,scale=Math.min(118/boundsW,118/boundsH);
   BufferedImage high=new BufferedImage(128,128,BufferedImage.TYPE_INT_ARGB);Graphics2D g=high.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);AffineTransform transform=new AffineTransform();transform.translate(64,64);transform.rotate(angle);transform.scale(scale,scale);transform.translate(-cut.getWidth()/2.0,-cut.getHeight()/2.0);g.drawImage(cut,transform,null);g.dispose();
   BufferedImage small=new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);g=small.createGraphics();g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.drawImage(high,0,0,32,32,null);g.dispose();Sprite sprite=new Sprite(32,32);sprite.highResolutionArt=high;int[] pixels=small.getRGB(0,0,32,32,null,0,32);for(int i=0;i<pixels.length;i++)sprite.myPixels[i]=(pixels[i]>>>24)>80?pixels[i]&0xFFFFFF:0;CACHE.put(id,sprite);return sprite;
  }catch(IOException|RuntimeException ex){System.err.println("Expansion art: "+ex.getMessage());CACHE.put(id,null);return null;}
 }
}
