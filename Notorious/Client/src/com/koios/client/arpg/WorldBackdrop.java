package com.koios.client.arpg;
import java.util.Arrays;import com.koios.client.graphic.DrawingArea;
/** Muted ARPG scene clear. Does not alter terrain, camera, depth, textures or UI buffers. */
public final class WorldBackdrop {
 public enum Theme {BASE,SETTLEMENT,FOUNDRY,COURTYARD}
 // Exile uses charcoal/oxblood skies; biome horizons stay muted so world silhouettes remain readable.
 private static final int[] ZENITH={0x1B1417,0x211719,0x241418,0x191A15},HORIZON={0x62504C,0x6C5850,0x674944,0x5C6250};
 private static int cachedHeight=-1;private static int[][] rows;
 private static synchronized int[] colors(Theme theme,int height){if(rows==null||cachedHeight!=height){rows=new int[4][height];for(int style=0;style<4;style++)for(int y=0;y<height;y++){double t=y/(double)Math.max(1,height-1);double smooth=t*t*(3-2*t);int a=ZENITH[style],b=HORIZON[style],r=(int)Math.round((a>>16&255)*(1-smooth)+(b>>16&255)*smooth),g=(int)Math.round((a>>8&255)*(1-smooth)+(b>>8&255)*smooth),blue=(int)Math.round((a&255)*(1-smooth)+(b&255)*smooth);rows[style][y]=r<<16|g<<8|blue;}cachedHeight=height;}return rows[theme.ordinal()];}
 /** Call instead of the black scene clear, only while the ARPG world surface is active. */
 public static void draw(Theme theme){if(theme==null||DrawingArea.pixels==null||DrawingArea.width<=0||DrawingArea.height<=0)return;int left=Math.max(0,DrawingArea.topX),right=Math.min(DrawingArea.width,DrawingArea.bottomX),top=Math.max(0,DrawingArea.topY),bottom=Math.min(DrawingArea.height,DrawingArea.bottomY);if(left>=right||top>=bottom)return;int[] colors=colors(theme,DrawingArea.height);for(int y=top;y<bottom;y++)Arrays.fill(DrawingArea.pixels,y*DrawingArea.width+left,y*DrawingArea.width+right,colors[y]);}
 private WorldBackdrop(){}
}
