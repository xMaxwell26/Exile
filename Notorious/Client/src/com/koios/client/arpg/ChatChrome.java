package com.koios.client.arpg;

import com.koios.client.graphic.*;

/** Chat chrome shares the inventory palette without changing legacy input bounds. */
public final class ChatChrome {
    public static final int TEXT=ExileTheme.TEXT, GOLD=0xD4BB87, INPUT=0xA5C7DF;
    private static final int[] START={5,71,137,203,269,335,404};
    private static final String[] NAMES={"All","Game","Public","Private","Clan","Trade","Report Abuse"};

    public static void backdrop(int y,int extension,boolean transparent){
        int top=y-extension;
        if(transparent)alphaRect(0,top,519,143+extension,ExileTheme.PANEL,176);
        else DrawingArea.drawPixels(143+extension,top,0,ExileTheme.SURFACE_DARK,519);
        DrawingArea.method339(top,ExileTheme.BORDER,519,0);
        DrawingArea.method341(top,ExileTheme.BORDER_SUBTLE,143+extension,0);
        DrawingArea.method341(top,ExileTheme.BORDER_SUBTLE,143+extension,518);
        if(transparent)alphaRect(1,y+121,517,22,ExileTheme.PANEL,218);else DrawingArea.drawPixels(22,y+121,1,ExileTheme.PANEL,517);
        DrawingArea.method339(y+121,ExileTheme.BORDER,517,1);
    }

    private static void alphaRect(int x,int y,int width,int height,int color,int alpha){
        int left=Math.max(x,DrawingArea.topX),right=Math.min(x+width,DrawingArea.bottomX);
        int top=Math.max(y,DrawingArea.topY),bottom=Math.min(y+height,DrawingArea.bottomY),inverse=256-alpha;
        int red=(color>>16&255)*alpha,green=(color>>8&255)*alpha,blue=(color&255)*alpha;
        for(int row=top;row<bottom;row++)for(int col=left;col<right;col++){
            int index=col+row*DrawingArea.width,old=DrawingArea.pixels[index];
            DrawingArea.pixels[index]=((red+(old>>16&255)*inverse)>>8)<<16|((green+(old>>8&255)*inverse)>>8)<<8|(blue+(old&255)*inverse)>>8;
        }
    }

    public static void channels(TextDrawingArea font,int y,int selected,int hovered,int[] modes){
        DrawingArea.drawPixels(22,y+143,0,ExileTheme.PANEL,519);
        DrawingArea.method339(y+143,ExileTheme.BORDER,519,0);
        String[] states={"On","Friends","Off","Hide"};
        int[] colors={0x94BFA6,0xCEB77D,0xBC8F89,0x8CA8BE};
        for(int i=0;i<START.length;i++){
            int width=i==6?110:64,x=START[i];
            boolean active=i==selected,hover=i==hovered;
            if(active||hover)DrawingArea.drawPixels(20,y+144,x,active?ExileTheme.SELECTED:ExileTheme.HOVER,width);
            if(active)DrawingArea.drawPixels(1,y+144,x+5,0xA79165,width-10);
            int center=x+width/2;
            font.drawText(active?GOLD:ExileTheme.MUTED,center,NAMES[i],y+(i>=2&&i<=5?153:158),true);
            if(i>=2&&i<=5){int mode=Math.max(0,Math.min(3,modes[i-2]));font.drawText(colors[mode],center,states[mode],y+164,true);}
        }
    }

    public static void scrollbar(int height,int position,int y,int x,int maximum){
        DrawingArea.drawPixels(height,y,x,ExileTheme.PANEL,16);
        DrawingArea.method341(y,ExileTheme.BORDER,height,x);
        DrawingArea.method341(y,ExileTheme.BORDER,height,x+15);
        for(int n=0;n<4;n++){
            DrawingArea.method339(y+5+n,ExileTheme.MUTED,1+n*2,x+7-n);
            DrawingArea.method339(y+height-6-n,ExileTheme.MUTED,1+n*2,x+7-n);
        }
        int track=Math.max(1,height-32),thumb=Math.max(8,Math.min(track,track*height/Math.max(1,maximum)));
        int offset=maximum<=height?0:(track-thumb)*Math.max(0,Math.min(maximum-height,position))/(maximum-height);
        DrawingArea.drawPixels(thumb,y+16+offset,x+3,ExileTheme.SURFACE,10);
        DrawingArea.drawPixels(Math.max(1,thumb-2),y+17+offset,x+4,ExileTheme.GOLD,1);
    }

    public static String readable(String text){
        if(text==null)return "";
        return text.replace("<col=800000>","<col=CF8B86>").replace("<col=000000>","<col=CBD5DD>")
            .replace("<col=0000ff>","<col=8DB6D8>").replace("@bla@","<col=CBD5DD>").replace("@blu@","<col=8DB6D8>");
    }
}
