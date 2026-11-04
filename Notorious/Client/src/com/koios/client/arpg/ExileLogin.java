package com.koios.client.arpg;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.koios.client.graphic.DrawingArea;
import com.koios.client.graphic.Sprite;
import com.koios.client.graphic.TextDrawingArea;

/** Exile login presentation; one shared layout owns both painted fields and input targets. */
public final class ExileLogin {
 public static final class Layout {
  public final int x,y;
  public final Rectangle account,password,remember,submit;
  public Layout(int width,int height){x=width/2-389/2;y=height/2-213/2+20;account=new Rectangle(x+50,y+27,276,27);password=new Rectangle(x+50,y+70,276,33);remember=new Rectangle(x+50,y+105,24,25);submit=new Rectangle(width/2-147/2,y+140,147,44);}
 }
 /** Cover scaling and the wordmark are rasterized only when the logical viewport changes. */
 public static final class Backdrop {
  private final File asset;
  private BufferedImage source;
  private boolean loaded;
  private Sprite cached;
  private int width,height,builds;
  public Backdrop(File asset){this.asset=asset;}
  public boolean hasArt(){return source!=null;}
  public int builds(){return builds;}
  public Sprite frame(int w,int h){
   if(w<1||h<1)throw new IllegalArgumentException("Login viewport must be positive");
   if(cached!=null&&width==w&&height==h)return cached;
   if(!loaded){loaded=true;try{source=ImageIO.read(asset);}catch(IOException ignored){/* A readable charcoal fallback must survive missing optional artwork. */}}
   BufferedImage canvas=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);Graphics2D g=canvas.createGraphics();g.setColor(new Color(ExileTheme.BACKGROUND));g.fillRect(0,0,w,h);
   if(source!=null){double cover=Math.max(w/(double)source.getWidth(),h/(double)source.getHeight());int sw=(int)Math.ceil(source.getWidth()*cover),sh=(int)Math.ceil(source.getHeight()*cover);g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.drawImage(source,(w-sw)/2,(h-sh)/2,sw,sh,null);}
   g.setPaint(new GradientPaint(0,0,new Color(0,0,0,35),0,h,new Color(0,0,0,120)));g.fillRect(0,0,w,h);
   g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);g.setFont(new Font("Serif",Font.BOLD,58));String brand="E X I L E";FontMetrics fm=g.getFontMetrics();int bx=(w-fm.stringWidth(brand))/2,by=Math.max(64,h/2-127);g.setColor(new Color(0x050405));g.drawString(brand,bx+3,by+4);g.setColor(new Color(0x71303A));g.drawString(brand,bx+1,by+2);g.setColor(new Color(0xF0D59A));g.drawString(brand,bx,by);g.setColor(new Color(0xFFF0C2));g.drawString(brand,bx,by-1);g.setFont(new Font("Serif",Font.BOLD,11));String subtitle="FORGE YOUR OWN PATH";fm=g.getFontMetrics();int sx=(w-fm.stringWidth(subtitle))/2;g.setColor(new Color(0x080608));g.drawString(subtitle,sx+1,by+24);g.setColor(new Color(ExileTheme.MUTED));g.drawString(subtitle,sx,by+23);g.dispose();
   Sprite result=new Sprite(w,h);canvas.getRGB(0,0,w,h,result.myPixels,0,w);for(int i=0;i<result.myPixels.length;i++)result.myPixels[i]&=0xFFFFFF;cached=result;width=w;height=h;builds++;return cached;
  }
 }
 private static Layout cachedLayout;
 public static Layout layout(int width,int height){if(cachedLayout==null||cachedLayout.x!=width/2-389/2||cachedLayout.y!=height/2-213/2+20)cachedLayout=new Layout(width,height);return cachedLayout;}
 private static final Backdrop BACKDROP=new Backdrop(artFile());
 private static File artFile(){File file=new File(System.getProperty("user.home"),"koios-cache/arpg-art/exile-login-v1.png");return file.isFile()?file:new File("../koios-cache/arpg-art/exile-login-v1.png");}
 public static void background(){BACKDROP.frame(DrawingArea.width,DrawingArea.height).drawSprite(0,0);}
 public static void chrome(TextDrawingArea font,Layout l,int mx,int my,boolean remember,int cursor){
  ExileTheme.panel(l.x,l.y,389,213,ExileTheme.BORDER,ExileTheme.PANEL);DrawingArea.fillPixels(l.x+3,383,207,ExileTheme.BORDER_SUBTLE,l.y+3);DrawingArea.drawPixels(2,l.y+3,l.x+2,ExileTheme.CRIMSON,385);
  ExileTheme.label(font,l.account.x,l.account.y-6,"Account",ExileTheme.MUTED);ExileTheme.label(font,l.password.x,l.password.y-6,"Password",ExileTheme.MUTED);
  field(l.account,cursor==0||l.account.contains(mx,my));field(l.password,cursor==1||l.password.contains(mx,my));
  ExileTheme.inset(l.remember.x,l.remember.y,24,25,ExileTheme.BORDER,ExileTheme.SURFACE_DARK);if(remember){tick(l.remember.x+5,l.remember.y+13,l.remember.x+10,l.remember.y+18);tick(l.remember.x+10,l.remember.y+18,l.remember.x+19,l.remember.y+7);}ExileTheme.label(font,l.remember.x+34,l.remember.y+17,"Remember account",ExileTheme.MUTED);
  boolean hover=l.submit.contains(mx,my);ExileTheme.panel(l.submit.x,l.submit.y,l.submit.width,l.submit.height,hover?ExileTheme.GOLD:ExileTheme.CRIMSON,hover?ExileTheme.SELECTED:ExileTheme.OXBLOOD);ExileTheme.heading(l.submit.x+(l.submit.width-ExileTheme.headingWidth("Enter Exile"))/2,l.submit.y+27,"Enter Exile",ExileTheme.HEADING);
 }
 private static void tick(int x,int y,int tx,int ty){int steps=Math.max(Math.abs(tx-x),Math.abs(ty-y));for(int i=0;i<=steps;i++)DrawingArea.drawPixels(2,y+(ty-y)*i/steps-1,x+(tx-x)*i/steps,ExileTheme.GOLD,2);}
 private static void field(Rectangle r,boolean active){ExileTheme.inset(r.x,r.y,r.width,r.height,active?ExileTheme.GOLD:ExileTheme.BORDER,ExileTheme.SURFACE_DARK);}
 private static void box(int x,int y,int w,int h,int border,int fill){DrawingArea.drawPixels(h,y,x,fill,w);DrawingArea.fillPixels(x,w,h,border,y);}
 private ExileLogin(){}
}

