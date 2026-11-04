import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;
import com.koios.client.arpg.*;
import com.koios.client.graphic.*;

/** Production-font spacing, input geometry and long connection feedback. No authentication. */
public final class ExileLoginCheck {
 static int checks;
 static void check(boolean value,String reason){checks++;if(!value)throw new AssertionError(reason);}
 static boolean changesInside(int[] before,int[] after,int width,Rectangle... allowed){
  for(int i=0;i<after.length;i++)if(before[i]!=after[i]){boolean inside=false;for(Rectangle rect:allowed)if(rect.contains(i%width,i/width)){inside=true;break;}if(!inside)return false;}
  return true;
 }
 public static void main(String[]args)throws Exception{
  ExileLogin.Backdrop backdrop=new ExileLogin.Backdrop(new File("../koios-cache/arpg-art/exile-login-v1.png"));
  TextDrawingArea font=ExileTheme.body();
  for(int[] size:new int[][]{{765,503},{1280,720},{1920,1080}}){
   int width=size[0],height=size[1];Sprite bg=backdrop.frame(width,height);
   check(backdrop.hasArt(),"Original background loads");int builds=backdrop.builds();
   check(backdrop.frame(width,height)==bg&&backdrop.builds()==builds,"Viewport reuses cached cover image");
   ExileLogin.Layout l=ExileLogin.layout(width,height);
   check(l==ExileLogin.layout(width,height),"Painting and input share cached geometry");
   for(Rectangle rect:new Rectangle[]{l.account,l.password,l.remember,l.submit,l.world})check(new Rectangle(0,0,width,height).contains(rect),"Input target fits viewport");
   check(l.account.y+l.account.height+3<=l.password.y-9-font.anInt1497,"Password label clears account field with production font");
   check(l.password.y+l.password.height+12<=l.remember.y,"Remember row clears password field");
   check(l.remember.y+l.remember.height+12<=l.submit.y,"Submit has its own band");
   check(l.submit.y+l.submit.height+6<=l.y+228-ExileTheme.small().anInt1497,"Feedback clears submit button");
   check(l.submit.width==147&&l.submit.height==44,"Painted submit target matches input");
   BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
   int[] pixels=((DataBufferInt)image.getRaster().getDataBuffer()).getData();DrawingArea.initDrawingArea(height,width,pixels);
   bg.drawSprite(0,0);ExileLogin.chrome(font,l,l.submit.x+5,l.submit.y+5,true,0);
   int[] before=pixels.clone();ExileLogin.credentials(l,"WWWWWWWWWWWW","********************",0,true);
   check(changesInside(before,pixels,width,l.account,l.password),"Long account/password and cursor stay in fields");
   before=pixels.clone();ExileLogin.feedback(l,"No response from loginserver. Please wait one minute and try again, or choose another world.");
   check(changesInside(before,pixels,width,new Rectangle(l.x+20,l.y+214,349,34)),"Long feedback stays in its own two-line band");
   ImageIO.write(image,"png",new File("build/exile-login-"+width+".png"));
  }
  ExileLogin.Backdrop missing=new ExileLogin.Backdrop(new File("build/does-not-exist-login-art.png"));
  Sprite fallback=missing.frame(765,503);check(!missing.hasArt()&&fallback!=null,"Missing optional art retains readable fallback");
  check(missing.frame(765,503)==fallback,"Fallback is cached");
  System.out.println("Passed "+checks+" login spacing, input and native-font checks.");
 }
}
