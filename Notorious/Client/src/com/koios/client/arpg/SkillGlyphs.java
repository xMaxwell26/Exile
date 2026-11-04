package com.koios.client.arpg;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import com.koios.client.graphic.Sprite;

/** Original, letter-free ability and support symbols drawn from simple readable silhouettes. */
public final class SkillGlyphs {
    private static final Map<Integer,Sprite> CACHE=new HashMap<>();
    private static final int GOLD=0xCDB17B,CYAN=0x88BEC9,VIOLET=0xAC9CCF,EMBER=0xC49B77,ROSE=0xC78D89;
    private static final Color EDGE=new Color(0x101A23);

    public static Sprite icon(int id,boolean support,int size){
        if(id<0||id>=(support?3:5)||size<8||size>128)return null;
        int key=(support?100:0)+id+size*1000;
        Sprite cached=CACHE.get(key);if(cached!=null)return cached;
        BufferedImage source=new BufferedImage(128,128,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=source.createGraphics();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        if(support){if(id==0)splash(g);else if(id==1)haste(g);else brutality(g);}
        else {if(id==0){g.translate(64,64);g.scale(1.15,1.15);g.translate(-64,-64);strike(g);}else if(id==1)cleave(g);else if(id==2)whirlwind(g);else if(id==3)slam(g);else execute(g);}
        g.dispose();BufferedImage output=new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);Graphics2D scaled=output.createGraphics();scaled.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);scaled.drawImage(source,0,0,size,size,null);scaled.dispose();
        Sprite sprite=new Sprite(size,size);int[] pixels=output.getRGB(0,0,size,size,null,0,size);for(int i=0;i<pixels.length;i++)sprite.myPixels[i]=(pixels[i]>>>24)>=80?pixels[i]&0xFFFFFF:0;
        sprite.highResolutionArt=source;CACHE.put(key,sprite);return sprite;
    }

    private static Path2D polygon(double...points){Path2D path=new Path2D.Double();path.moveTo(points[0],points[1]);for(int i=2;i<points.length;i+=2)path.lineTo(points[i],points[i+1]);path.closePath();return path;}
    private static void fill(Graphics2D g,Shape shape,int top,int bottom){g.setPaint(new GradientPaint(30,20,new Color(top),92,110,new Color(bottom)));g.fill(shape);g.setColor(EDGE);g.setStroke(new BasicStroke(4,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g.draw(shape);}
    private static void line(Graphics2D g,int color,float width,double...points){Path2D path=new Path2D.Double();path.moveTo(points[0],points[1]);for(int i=2;i<points.length;i+=2)path.lineTo(points[i],points[i+1]);g.setColor(new Color(color));g.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));g.draw(path);}
    private static int light(int color){return new Color(color).brighter().getRGB()&0xFFFFFF;}

    private static void sword(Graphics2D g,int color){
        fill(g,polygon(54,44,74,44,74,86,64,108,54,86),light(color),color);
        fill(g,polygon(58,17,70,17,70,39,58,39),0x907C5E,0x4D443A);
        fill(g,polygon(41,38,87,38,84,48,44,48),color,0x6F644F);
        line(g,0xE2E4DE,3,62,49,62,85);
    }
    private static void strike(Graphics2D g){
        sword(g,GOLD);
        line(g,GOLD,5,22,106,41,99);line(g,GOLD,5,87,99,106,106);
        line(g,0x8F7D59,4,34,88,43,91);line(g,0x8F7D59,4,85,91,94,88);
    }
    private static void cleave(Graphics2D g){
        Path2D arc=new Path2D.Double();arc.moveTo(17,90);arc.curveTo(12,39,60,13,104,30);arc.curveTo(67,24,32,49,17,90);arc.closePath();fill(g,arc,0xB9D8DC,CYAN);
        Graphics2D blade=(Graphics2D)g.create();blade.translate(63,66);blade.rotate(-Math.PI/4);blade.scale(.65,.73);blade.translate(-64,-64);sword(blade,CYAN);blade.dispose();
        line(g,0x527D8A,4,24,101,38,107,51,108);
    }
    private static void whirlwind(Graphics2D g){
        for(int arm=0;arm<3;arm++){
            Graphics2D spin=(Graphics2D)g.create();spin.rotate(arm*Math.PI*2/3,64,64);
            Path2D blade=new Path2D.Double();blade.moveTo(60,63);blade.curveTo(39,65,26,50,29,32);blade.curveTo(33,17,50,11,66,16);blade.curveTo(48,17,41,31,45,43);blade.curveTo(49,54,58,54,68,57);blade.closePath();fill(spin,blade,0xCFCAE2,VIOLET);spin.dispose();
        }
        fill(g,polygon(64,52,76,64,64,76,52,64),0xDAD7E9,0x76678E);
    }
    private static void slam(Graphics2D g){
        g.setColor(new Color(0x7D654F));g.setStroke(new BasicStroke(6));g.draw(new Arc2D.Double(17,79,94,29,4,172,Arc2D.OPEN));
        fill(g,polygon(52,45,70,45,75,82,64,93,55,82),0xA78D67,0x514739);
        fill(g,polygon(28,25,91,25,101,36,91,52,30,52,22,41),0xD1B99A,EMBER);
        fill(g,polygon(33,30,53,30,53,47,33,47),0xD8D6C9,0x8D9291);
        line(g,EMBER,5,64,96,52,104,61,111);line(g,EMBER,5,76,99,85,110);line(g,EMBER,5,43,95,32,103);
    }
    private static void execute(Graphics2D g){
        for(int side:new int[]{-1,1}){Graphics2D blade=(Graphics2D)g.create();blade.translate(64,64);blade.rotate(side*Math.PI/4);blade.scale(.95,.95);blade.translate(-64,-64);sword(blade,side<0?0xC3CED6:ROSE);blade.dispose();}
        fill(g,polygon(64,49,75,64,64,79,53,64),0xD6A4A0,0x805454);
        line(g,0xE4C3BC,3,64,54,64,67);
    }
    private static void splash(Graphics2D g){
        Path2D drop=new Path2D.Double();drop.moveTo(64,17);drop.curveTo(60,36,37,49,37,72);drop.curveTo(37,103,91,103,91,72);drop.curveTo(91,49,69,36,64,17);drop.closePath();fill(g,drop,0xB8E0E3,0x4D8596);
        fill(g,polygon(64,42,79,70,64,91,49,70),CYAN,0x406B80);line(g,0xE1F0EE,3,62,49,54,68);
        line(g,0x7DB8C4,5,22,63,16,53);line(g,0x7DB8C4,5,105,63,111,53);line(g,0x7DB8C4,5,31,92,21,98);line(g,0x7DB8C4,5,97,92,107,98);
    }
    private static void haste(Graphics2D g){
        fill(g,polygon(67,17,90,41,76,83,54,109,38,82,43,42),0xC9D5A6,0x6B946F);
        fill(g,polygon(67,24,67,68,47,83,49,44),0xAEC991,0x61816A);
        fill(g,polygon(67,24,84,43,71,78,67,68),0xD2DAB8,0x89A77F);
        line(g,0xB0C6A0,6,23,47,14,65,24,75);line(g,0xB0C6A0,6,104,47,113,65,103,75);
        line(g,0xDBD2AC,4,63,43,56,66,68,63,61,84);
    }
    private static void brutality(Graphics2D g){
        fill(g,polygon(64,12,100,43,94,82,64,112,34,82,28,43),0xC99998,0x794C5D);
        fill(g,polygon(64,25,87,48,64,94,41,48),0xC77E83,0x814455);
        fill(g,polygon(64,25,64,94,41,48),0xD39B98,0x9B5D69);
        fill(g,polygon(54,35,50,64,43,80,44,55),0xECD8C9,0xCFB4AB);
        fill(g,polygon(67,32,64,66,55,87,58,54),0xF0DCCE,0xCFB4AB);
        fill(g,polygon(79,43,74,68,68,83,70,58),0xECD8C9,0xCFB4AB);
    }
}
