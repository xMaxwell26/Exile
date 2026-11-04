package com.koios.client.arpg;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.*;
import com.koios.client.graphic.DrawingArea;

/** Proportional, antialiased type with identical software and device-scale advances.
 * Legacy cache fonts remain available; custom interfaces opt in through TextDrawingArea.
 */
public final class ExileTypography {
    public enum Style { BODY, SMALL, HEADING }
    public static final class Face {
        final Font font;
        final FontRenderContext context=new FontRenderContext(null,true,false);
        final Map<Integer,Glyph> glyphs=new HashMap<>();
        public final int ascent,descent,lineHeight;
        Face(Font font) {
            this.font=font;
            LineMetrics metrics=font.getLineMetrics("Ag",context);
            ascent=(int)Math.ceil(metrics.getAscent());
            descent=(int)Math.ceil(metrics.getDescent());
            lineHeight=ascent+descent;
        }
        Glyph glyph(int codepoint) {
            Glyph glyph=glyphs.get(codepoint);
            if(glyph==null){glyph=new Glyph(this,codepoint);glyphs.put(codepoint,glyph);}
            return glyph;
        }
        public String family(){return font.getFamily();}
    }
    private static final class Glyph {
        final String text;
        final int advance,x,y,width,height;
        final int[] alpha;
        Glyph(Face face,int codepoint) {
            text=new String(Character.toChars(codepoint));
            GlyphVector vector=face.font.createGlyphVector(face.context,text);
            advance=Math.max(0,Math.round(vector.getGlyphMetrics(0).getAdvanceX()));
            Rectangle bounds=vector.getPixelBounds(face.context,0,0);
            x=bounds.x;y=bounds.y;width=Math.max(1,bounds.width);height=Math.max(1,bounds.height);
            BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics=image.createGraphics();configure(graphics);graphics.setFont(face.font);
            graphics.setColor(Color.WHITE);graphics.drawString(text,-x,-y);graphics.dispose();
            alpha=image.getRGB(0,0,width,height,null,0,width);
            for(int i=0;i<alpha.length;i++)alpha[i]>>>=24;
        }
    }
    private static final Face BODY=new Face(font(new String[]{"Segoe UI","Trebuchet MS","SansSerif"},13));
    private static final Face SMALL=new Face(font(new String[]{"Segoe UI","Trebuchet MS","SansSerif"},11.5f));
    private static final Face HEADING=new Face(font(new String[]{"Cambria","Georgia","Serif"},18));
    private static final class Surface {
        final ArrayList<Stamp> stamps=new ArrayList<>();
        int generation;
    }
    private static final Map<int[],Surface> SURFACES=new WeakHashMap<>();
    private static final String[] COLOR_NAMES={"red","gre","blu","yel","cya","mag","whi","bla","lre","dre","dbl","or1","or2","or3","gr1","gr2","gr3"};
    private static final int[] COLORS={0xFF0000,0x00FF00,0x0000FF,0xFFFF00,0x00FFFF,0xFF00FF,0xFFFFFF,0,0xFF9040,0x800000,0x000080,0xFFB000,0xFF7000,0xFF3000,0xC0FF00,0x80FF00,0x40FF00};
    private static Font font(String[] preferred,float size) {
        Set<String> installed=new HashSet<>(Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for(String family:preferred)if(installed.contains(family))return new Font(family,Font.PLAIN,12).deriveFont(size);
        return new Font(preferred[preferred.length-1],Font.PLAIN,12).deriveFont(size);
    }
    public static Face face(Style style){return style==Style.HEADING?HEADING:style==Style.SMALL?SMALL:BODY;}
    private static void configure(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }
    private static boolean tag(String text,int index){return text.charAt(index)=='@'&&index+4<text.length()&&text.charAt(index+4)=='@';}
    private static int htmlEnd(String text,int index){return text.charAt(index)=='<'?text.indexOf('>',index):-1;}
    public static int width(Face face,String text) {
        if(text==null)return 0;
        int width=0;
        for(int i=0;i<text.length();) {
            if(tag(text,i)){i+=5;continue;}
            int end=htmlEnd(text,i);if(end>=0){i=end+1;continue;}
            int codepoint=text.codePointAt(i);width+=face.glyph(codepoint).advance;i+=Character.charCount(codepoint);
        }
        return width;
    }
    private static int color(String tag,int fallback) {
        for(int i=0;i<COLOR_NAMES.length;i++)if(COLOR_NAMES[i].equals(tag))return COLORS[i];
        return fallback;
    }
    private static int htmlColor(String tag,int current,int initial) {
        if(tag.equals("/col"))return initial;
        if(tag.startsWith("col="))try{return Integer.parseInt(tag.substring(4),16)&0xFFFFFF;}catch(NumberFormatException ignored){}
        return current;
    }
    /** Blends cached glyph masks into the software raster, respecting its exact clip. */
    public static void draw(Face face,int x,int baseline,String text,int color,boolean shadow) {
        if(text==null||text.isEmpty()||DrawingArea.pixels==null)return;
        int width=width(face,text);
        int left=Math.max(DrawingArea.topX,x-2),right=Math.min(DrawingArea.bottomX,x+width+3);
        int top=Math.max(DrawingArea.topY,baseline-face.ascent-2),bottom=Math.min(DrawingArea.bottomY,baseline+face.descent+3);
        if(left>=right||top>=bottom)return;
        Surface surface=SURFACES.get(DrawingArea.pixels);
        if(surface==null){surface=new Surface();SURFACES.put(DrawingArea.pixels,surface);}
        long key=((long)x<<32)^(baseline&0xFFFFFFFFL)^((long)System.identityHashCode(face)<<8);
        Stamp stamp=null;
        for(int index=0;index<surface.stamps.size();index++)if(surface.stamps.get(index).key==key){stamp=surface.stamps.get(index);break;}
        if(stamp==null||stamp.x!=left||stamp.y!=top||stamp.width!=right-left||stamp.height!=bottom-top) {
            if(stamp!=null)surface.stamps.remove(stamp);
            stamp=new Stamp(left,top,right-left,bottom-top);stamp.key=key;surface.stamps.add(stamp);
        }
        stamp.generation=surface.generation;
        stamp.face=face;stamp.text=text;stamp.originX=x;stamp.baseline=baseline;stamp.color=color;stamp.shadow=shadow;
        stamp.capture(stamp.before);
        if(shadow)paint(face,x+1,baseline+1,text,0x080608,true);
        paint(face,x,baseline,text,color,false);
        stamp.capture(stamp.after);
        if(surface.stamps.size()>768)surface.stamps.remove(0);
    }
    private static void paint(Face face,int x,int baseline,String text,int color,boolean monochrome) {
        int initial=color;
        for(int i=0;i<text.length();) {
            if(tag(text,i)){if(!monochrome)color=color(text.substring(i+1,i+4),color);i+=5;continue;}
            int end=htmlEnd(text,i);if(end>=0){if(!monochrome)color=htmlColor(text.substring(i+1,end),color,initial);i=end+1;continue;}
            int codepoint=text.codePointAt(i);Glyph glyph=face.glyph(codepoint);
            for(int row=0;row<glyph.height;row++) {
                int y=baseline+glyph.y+row;if(y<DrawingArea.topY||y>=DrawingArea.bottomY)continue;
                for(int col=0;col<glyph.width;col++) {
                    int px=x+glyph.x+col;if(px<DrawingArea.topX||px>=DrawingArea.bottomX)continue;
                    int alpha=glyph.alpha[row*glyph.width+col];if(alpha==0)continue;
                    int index=px+y*DrawingArea.width,old=DrawingArea.pixels[index],inverse=255-alpha;
                    int red=((color>>16&255)*alpha+(old>>16&255)*inverse+127)/255;
                    int green=((color>>8&255)*alpha+(old>>8&255)*inverse+127)/255;
                    int blue=((color&255)*alpha+(old&255)*inverse+127)/255;
                    DrawingArea.pixels[index]=red<<16|green<<8|blue;
                }
            }
            x+=glyph.advance;i+=Character.charCount(codepoint);
        }
    }
    private static final class Stamp {
        final int x,y,width,height,stride;
        final int[] before,after;
        final BufferedImage background;
        final int[] restorePixels;
        final boolean[] visibility;
        BufferedImage nativeArt,filteredArt;
        int[] nativePixels,filteredPixels;
        int nativeScale;
        String renderedText;Face renderedFace;int renderedColor,renderedX,renderedBaseline;boolean renderedShadow,eligible,fullyVisible;
        Face face;String text;int originX,baseline,color,generation;long key;boolean shadow;
        Stamp(int x,int y,int width,int height) {
            this(x,y,width,height,DrawingArea.width);
        }
        Stamp(int x,int y,int width,int height,int stride) {
            this.x=x;this.y=y;this.width=width;this.height=height;this.stride=stride;
            before=new int[width*height];after=new int[before.length];
            background=new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
            restorePixels=((DataBufferInt)background.getRaster().getDataBuffer()).getData();
            visibility=new boolean[width*height];
        }
        void capture(int[] target){for(int row=0;row<height;row++)System.arraycopy(DrawingArea.pixels,(y+row)*DrawingArea.width+x,target,row*width,width);}
        boolean prepareArt(int scale) {
            int relativeX=originX-x,relativeBaseline=baseline-y;
            if(nativeArt!=null&&nativeScale==scale&&text.equals(renderedText)&&face==renderedFace&&color==renderedColor&&shadow==renderedShadow&&relativeX==renderedX&&relativeBaseline==renderedBaseline)return false;
            nativeScale=scale;
            nativeArt=new BufferedImage(width*scale,height*scale,BufferedImage.TYPE_INT_ARGB);
            filteredArt=new BufferedImage(width*scale,height*scale,BufferedImage.TYPE_INT_ARGB);
            nativePixels=((DataBufferInt)nativeArt.getRaster().getDataBuffer()).getData();
            filteredPixels=((DataBufferInt)filteredArt.getRaster().getDataBuffer()).getData();
            Graphics2D g=nativeArt.createGraphics();g.scale(scale,scale);configure(g);g.setFont(face.font);
            if(shadow)vector(g,this,relativeX+1,relativeBaseline+1,0x080608,true);
            vector(g,this,relativeX,relativeBaseline,color,false);g.dispose();
            renderedText=text;renderedFace=face;renderedColor=color;renderedShadow=shadow;renderedX=relativeX;renderedBaseline=relativeBaseline;
            return true;
        }
        void filterArt() {
            int rowWidth=width*nativeScale;
            for(int y=0;y<height;y++)for(int x=0;x<width;x++)for(int sy=0;sy<nativeScale;sy++) {
                int offset=(y*nativeScale+sy)*rowWidth+x*nativeScale;
                if(visibility[y*width+x])System.arraycopy(nativePixels,offset,filteredPixels,offset,nativeScale);
                else Arrays.fill(filteredPixels,offset,offset+nativeScale,0);
            }
        }
    }
    /** Begin a new software frame without discarding reusable stamp images/buffers. */
    public static void clear(int[] raster){Surface surface=SURFACES.get(raster);if(surface!=null)surface.generation++;}
    /** Transfer active local-panel runs after its pixels have been composited into a world surface.
     * Offsets translate source canvas coordinates, not just the cropped panel's corner.
     */
    public static void copySurface(int[] source,int[] target,int targetStride,int dx,int dy) {
        if(source==target||targetStride<=0||target==null)return;
        Surface from=SURFACES.get(source);if(from==null)return;
        Surface to=SURFACES.get(target);if(to==null){to=new Surface();SURFACES.put(target,to);}
        int targetHeight=target.length/targetStride;
        boolean activeTarget=DrawingArea.pixels==target;
        int clipLeft=activeTarget?Math.max(0,DrawingArea.topX):0;
        int clipTop=activeTarget?Math.max(0,DrawingArea.topY):0;
        int clipRight=activeTarget?Math.min(targetStride,DrawingArea.bottomX):targetStride;
        int clipBottom=activeTarget?Math.min(targetHeight,DrawingArea.bottomY):targetHeight;
        for(int index=0;index<from.stamps.size();index++) {
            Stamp old=from.stamps.get(index);if(old.generation!=from.generation)continue;
            int fullX=old.x+dx,fullY=old.y+dy;
            int x=Math.max(clipLeft,fullX),y=Math.max(clipTop,fullY);
            int width=Math.min(clipRight,fullX+old.width)-x,height=Math.min(clipBottom,fullY+old.height)-y;
            if(width<=0||height<=0)continue;
            int originX=old.originX+dx,baseline=old.baseline+dy;
            long key=((long)originX<<32)^(baseline&0xFFFFFFFFL)^((long)System.identityHashCode(old.face)<<8);
            Stamp copy=null;
            for(int at=0;at<to.stamps.size();at++)if(to.stamps.get(at).key==key){copy=to.stamps.get(at);break;}
            if(copy==null||copy.x!=x||copy.y!=y||copy.width!=width||copy.height!=height||copy.stride!=targetStride) {
                if(copy!=null)to.stamps.remove(copy);
                copy=new Stamp(x,y,width,height,targetStride);copy.key=key;to.stamps.add(copy);
            }
            copy.face=old.face;copy.text=old.text;copy.originX=originX;copy.baseline=baseline;
            copy.color=old.color;copy.shadow=old.shadow;copy.generation=to.generation;
            for(int row=0;row<height;row++) {
                int offset=(y-fullY+row)*old.width+x-fullX;
                System.arraycopy(old.before,offset,copy.before,row*width,width);
                System.arraycopy(old.after,offset,copy.after,row*width,width);
            }
        }
        while(to.stamps.size()>768)to.stamps.remove(0);
    }
    public static void copySurface(int[] source,int[] target,int dx,int dy){copySurface(source,target,DrawingArea.width,dx,dy);}
    /** Root hooks this after software/item presentation. Cached device-scale vector runs supply
     * native detail; pixel masks preserve occlusion without expensive geometric clip unions.
     */
    public static void present(Graphics graphics,int[] raster,int xOffset,int yOffset) {
        if(!(graphics instanceof Graphics2D)||((Graphics2D)graphics).getTransform().getScaleX()<=1.01)return;
        Surface surface=SURFACES.get(raster);if(surface==null)return;
        int scale=Math.max(1,Math.min(4,(int)Math.ceil(((Graphics2D)graphics).getTransform().getScaleX())));
        Graphics2D g=(Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        // First remove only surviving software ink. Restore all runs before replaying any native
        // art, so adjacent baselines cannot overwrite each other's higher-resolution edges.
        for(int stampIndex=0;stampIndex<surface.stamps.size();stampIndex++) {
            Stamp stamp=surface.stamps.get(stampIndex);if(stamp.generation!=surface.generation)continue;
            boolean dirty=stamp.prepareArt(scale);int painted=0,matched=0;stamp.fullyVisible=true;
            for(int row=0;row<stamp.height;row++) {
                for(int col=0;col<stamp.width;col++) {
                    int index=row*stamp.width+col;
                    boolean match=raster[(stamp.y+row)*stamp.stride+stamp.x+col]==stamp.after[index];
                    if(stamp.visibility[index]!=match){stamp.visibility[index]=match;dirty=true;}
                    boolean ink=stamp.before[index]!=stamp.after[index];
                    if(ink){painted++;if(match)matched++;}
                    stamp.restorePixels[index]=ink&&match?0xFF000000|stamp.before[index]:0;
                    if(!match)stamp.fullyVisible=false;
                }
            }
            stamp.eligible=painted>0&&matched>=painted/3;if(!stamp.eligible)continue;
            if(!stamp.fullyVisible&&dirty)stamp.filterArt();
            g.drawImage(stamp.background,xOffset+stamp.x,yOffset+stamp.y,null);
        }
        for(int index=0;index<surface.stamps.size();index++) {
            Stamp stamp=surface.stamps.get(index);if(stamp.generation!=surface.generation||!stamp.eligible)continue;
            g.drawImage(stamp.fullyVisible?stamp.nativeArt:stamp.filteredArt,xOffset+stamp.x,yOffset+stamp.y,stamp.width,stamp.height,null);
        }
        g.dispose();
    }
    private static void vector(Graphics2D graphics,Stamp stamp,int x,int baseline,int color,boolean monochrome) {
        if(stamp.text.indexOf('@')<0&&stamp.text.indexOf('<')<0) {
            graphics.setColor(new Color(color));graphics.drawString(stamp.text,x,baseline);return;
        }
        int initial=color;
        for(int i=0;i<stamp.text.length();) {
            if(tag(stamp.text,i)){if(!monochrome)color=color(stamp.text.substring(i+1,i+4),color);i+=5;continue;}
            int end=htmlEnd(stamp.text,i);if(end>=0){if(!monochrome)color=htmlColor(stamp.text.substring(i+1,end),color,initial);i=end+1;continue;}
            int codepoint=stamp.text.codePointAt(i);Glyph glyph=stamp.face.glyph(codepoint);
            graphics.setColor(new Color(color));graphics.drawString(glyph.text,x,baseline);
            x+=glyph.advance;i+=Character.charCount(codepoint);
        }
    }
    private ExileTypography(){}
}
