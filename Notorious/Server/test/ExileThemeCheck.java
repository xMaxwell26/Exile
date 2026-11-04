import com.koios.client.arpg.*;
/** Chrome contrast is independent of semantic skill, damage, mana and rarity colors. */
public final class ExileThemeCheck {
 static int checks;static void check(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 static double luminance(int rgb){double[] c={(rgb>>16&255)/255d,(rgb>>8&255)/255d,(rgb&255)/255d};double out=0;for(int i=0;i<3;i++){double v=c[i]<=.04045?c[i]/12.92:Math.pow((c[i]+.055)/1.055,2.4);out+=v*new double[]{.2126,.7152,.0722}[i];}return out;}
 static double contrast(int a,int b){double x=luminance(a),y=luminance(b);return (Math.max(x,y)+.05)/(Math.min(x,y)+.05);}
 public static void main(String[]args){for(int bg:new int[]{ExileTheme.PANEL,ExileTheme.SURFACE,ExileTheme.SURFACE_DARK,ExileTheme.HOVER,ExileTheme.OXBLOOD}){check(contrast(ExileTheme.TEXT,bg)>=7,"Body text remains clearly readable");check(contrast(ExileTheme.MUTED,bg)>=4.5,"Secondary text retains normal-text contrast");check(contrast(ExileTheme.HEADING,bg)>=4.5&&contrast(ExileTheme.GOLD,bg)>=4.5,"Gold headings/controls remain readable");}check(SkillPanel.color(1)==0x7CB9CE&&SkillPanel.color(2)==0xAD93DA,"Skill identity colors remain unchanged");check(com.koios.client.constants.Constants.CLIENT_NAME.equals("Exile"),"Visible client identity is Exile");System.out.println("Passed "+checks+" Exile chrome contrast and semantic-color checks.");}
}
