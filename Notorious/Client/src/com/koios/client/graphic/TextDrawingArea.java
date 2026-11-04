package com.koios.client.graphic;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import com.koios.client.stream.Stream;
import com.koios.client.stream.StreamLoader;

public final class TextDrawingArea extends DrawingArea {
    private com.koios.client.arpg.ExileTypography.Face exileFace;
    private static final class ExileFonts {
        static final TextDrawingArea BODY=new TextDrawingArea(com.koios.client.arpg.ExileTypography.Style.BODY);
        static final TextDrawingArea SMALL=new TextDrawingArea(com.koios.client.arpg.ExileTypography.Style.SMALL);
        static final TextDrawingArea HEADING=new TextDrawingArea(com.koios.client.arpg.ExileTypography.Style.HEADING);
    }
    private TextDrawingArea(com.koios.client.arpg.ExileTypography.Style style) {
        exileFace=com.koios.client.arpg.ExileTypography.face(style);
        anInt1497=exileFace.ascent;
        CHARACTER_WIDTH_VALUES=new int[256];
        for(int i=0;i<256;i++)CHARACTER_WIDTH_VALUES[i]=com.koios.client.arpg.ExileTypography.width(exileFace,Character.toString((char)i));
    }
    public static TextDrawingArea exileBody(){return ExileFonts.BODY;}
    public static TextDrawingArea exileSmall(){return ExileFonts.SMALL;}
    public static TextDrawingArea exileHeading(){return ExileFonts.HEADING;}
    public boolean isExile(){return exileFace!=null;}
    public int lineHeight(){return exileFace==null?anInt1497+2:exileFace.lineHeight;}

	public TextDrawingArea(boolean flag, String s, StreamLoader streamLoader) {
		aByteArrayArray1491 = new byte[256][];
		anIntArray1492 = new int[256];
		anIntArray1493 = new int[256];
		anIntArray1494 = new int[256];
		anIntArray1495 = new int[256];
		CHARACTER_WIDTH_VALUES = new int[256];
		aBoolean1499 = false;
		Stream stream = new Stream(streamLoader.getDataForName(s + ".dat"));
		Stream stream_1 = new Stream(streamLoader.getDataForName("index.dat"));
		stream_1.currentOffset = stream.readUnsignedShort() + 4;
		int k = stream_1.readUnsignedByte();
		if (k > 0)
			stream_1.currentOffset += 3 * (k - 1);
		for (int l = 0; l < 256; l++) {
			anIntArray1494[l] = stream_1.readUnsignedByte();
			anIntArray1495[l] = stream_1.readUnsignedByte();
			int i1 = anIntArray1492[l] = stream_1.readUnsignedShort();
			int j1 = anIntArray1493[l] = stream_1.readUnsignedShort();
			int k1 = stream_1.readUnsignedByte();
			int l1 = i1 * j1;
			aByteArrayArray1491[l] = new byte[l1];
			if (k1 == 0) {
				for (int i2 = 0; i2 < l1; i2++)
					aByteArrayArray1491[l][i2] = stream.readSignedByte();

			} else if (k1 == 1) {
				for (int j2 = 0; j2 < i1; j2++) {
					for (int l2 = 0; l2 < j1; l2++)
						aByteArrayArray1491[l][j2 + l2 * i1] = stream.readSignedByte();
				}
			}
			if (j1 > anInt1497 && l < 128)
				anInt1497 = j1;
			anIntArray1494[l] = 1;
			CHARACTER_WIDTH_VALUES[l] = i1 + 2;
			int k2 = 0;
			for (int i3 = j1 / 7; i3 < j1; i3++)
				k2 += aByteArrayArray1491[l][i3 * i1];
			if (k2 <= j1 / 7) {
				CHARACTER_WIDTH_VALUES[l]--;
				anIntArray1494[l] = 0;
			}
			k2 = 0;
			for (int j3 = j1 / 7; j3 < j1; j3++)
				k2 += aByteArrayArray1491[l][(i1 - 1) + j3 * i1];
			if (k2 <= j1 / 7)
				CHARACTER_WIDTH_VALUES[l]--;
		}
		if (flag) {
			CHARACTER_WIDTH_VALUES[32] = CHARACTER_WIDTH_VALUES[73];
		} else {
			CHARACTER_WIDTH_VALUES[32] = CHARACTER_WIDTH_VALUES[105];
		}
	}

	public void method380(String s, int i, int j, int k) {
		method385(j, s, k, i - getRealTextWidth(s));
	}

	public void drawText(int i, String s, int k, int l) {
		method385(i, s, k, l - getRealTextWidth(s) / 2);
	}

	public void drawText(int color, int xPos, String text, int yPos, boolean textShadow) {
		method389(textShadow, xPos - getTextWidth(text) / 2, color, text, yPos);
	}

	public void drawChatInput(int i, int j, String s, int l, boolean flag) {
		method389(flag, j, i, s, l);
	}

	public int getTextWidth(String text) {
		if(exileFace!=null)return com.koios.client.arpg.ExileTypography.width(exileFace,text);
		if (text == null)
			return 0;
		int width = 0;
		for (int character = 0; character < text.length(); character++)
			if (text.charAt(character) == '@' && character + 4 < text.length() && text.charAt(character + 4) == '@')
				character += 4;
			else
				width += CHARACTER_WIDTH_VALUES[text.charAt(character)];
		return width;
	}

	public int getRealTextWidth(String text) {
		if(exileFace!=null)return com.koios.client.arpg.ExileTypography.width(exileFace,text);
		if (text == null)
			return 0;
		int width = 0;
		for (int character = 0; character < text.length(); character++)
			width += CHARACTER_WIDTH_VALUES[text.charAt(character)];
		return width;
	}

	public void method385(int i, String s, int j, int l) {
		if(exileFace!=null){com.koios.client.arpg.ExileTypography.draw(exileFace,l,j,s,i,false);return;}
		if (s == null)
			return;
		j -= anInt1497;
		for (int i1 = 0; i1 < s.length(); i1++) {
			char c = s.charAt(i1);
			if (c != ' ')
				method392(aByteArrayArray1491[c], l + anIntArray1494[c], j + anIntArray1495[c], anIntArray1492[c], anIntArray1493[c], i);
			l += CHARACTER_WIDTH_VALUES[c];
		}
	}

	public void method386(int i, String s, int j, int k, int l) {
		if (s == null)
			return;
		j -= getRealTextWidth(s) / 2;
		l -= anInt1497;
		for (int i1 = 0; i1 < s.length(); i1++) {
			char c = s.charAt(i1);
			if (c != ' ')
				method392(aByteArrayArray1491[c], j + anIntArray1494[c], l + anIntArray1495[c] + (int) (Math.sin(i1 / 2D + k / 5D) * 5D), anIntArray1492[c], anIntArray1493[c], i);
			j += CHARACTER_WIDTH_VALUES[c];
		}
	}

	public void method387(int i, String s, int j, int k, int l) {
		if (s == null)
			return;
		i -= getRealTextWidth(s) / 2;
		k -= anInt1497;
		for (int i1 = 0; i1 < s.length(); i1++) {
			char c = s.charAt(i1);
			if (c != ' ')
				method392(aByteArrayArray1491[c], i + anIntArray1494[c] + (int) (Math.sin(i1 / 5D + j / 5D) * 5D), k + anIntArray1495[c] + (int) (Math.sin(i1 / 3D + j / 5D) * 5D), anIntArray1492[c], anIntArray1493[c], l);
			i += CHARACTER_WIDTH_VALUES[c];
		}
	}

	public void method388(int i, String s, int j, int k, int l, int i1) {
		if (s == null)
			return;
		double d = 7D - i / 8D;
		if (d < 0.0D)
			d = 0.0D;
		l -= getRealTextWidth(s) / 2;
		k -= anInt1497;
		for (int k1 = 0; k1 < s.length(); k1++) {
			char c = s.charAt(k1);
			if (c != ' ')
				method392(aByteArrayArray1491[c], l + anIntArray1494[c], k + anIntArray1495[c] + (int) (Math.sin(k1 / 1.5D + j) * d), anIntArray1492[c], anIntArray1493[c], i1);
			l += CHARACTER_WIDTH_VALUES[c];
		}
	}

	public void drawCenteredString(int x, int y, String text, int color, boolean shadow) {
		drawLeftAlignedString(x - (getTextWidth(text) / 2), y, text, color, shadow);
	}

	public void drawLeftAlignedString(int x, int y, String text, int color, boolean shadow) {
		if(exileFace!=null){com.koios.client.arpg.ExileTypography.draw(exileFace,x,y,text,color,shadow);return;}
		aBoolean1499 = false;
		final int l = x;

		if (text == null) {
			return;
		}

		y -= anInt1497;

		for (int i1 = 0; i1 < text.length(); i1++) {
			if ((text.charAt(i1) == '@') && ((i1 + 4) < text.length()) && (text.charAt(i1 + 4) == '@')) {
				final int j1 = getColorByName(text.substring(i1 + 1, i1 + 4));

				if (j1 != -1) {
					color = j1;
				}

				i1 += 4;
			} else {
				final char c = text.charAt(i1);

				if (c != ' ') {
					if (shadow) {
						method392(aByteArrayArray1491[c], x + anIntArray1494[c] + 1, y + anIntArray1495[c] + 1, anIntArray1492[c], anIntArray1493[c], 0);
					}

					method392(aByteArrayArray1491[c], x + anIntArray1494[c], y + anIntArray1495[c], anIntArray1492[c], anIntArray1493[c], color);
				}

				x += CHARACTER_WIDTH_VALUES[c];
			}
		}

		if (aBoolean1499) {
			DrawingArea.method339(l, y + (int) (anInt1497 * 0.69999999999999996D), x - l, 0x800000);
		}
	}

	public void method389(boolean shadowText, int xPos, int color, String text, int yPos) {
		if(exileFace!=null){com.koios.client.arpg.ExileTypography.draw(exileFace,xPos,yPos,text,color,shadowText);return;}
		aBoolean1499 = false;
		int l = xPos;
		if (text == null)
			return;
		yPos -= anInt1497;
		for (int i1 = 0; i1 < text.length(); i1++)
			if (text.charAt(i1) == '@' && i1 + 4 < text.length() && text.charAt(i1 + 4) == '@') {
				int j1 = getColorByName(text.substring(i1 + 1, i1 + 4));
				if (j1 != -1)
					color = j1;
				i1 += 4;
			} else {
				char c = text.charAt(i1);
				if (c != ' ') {
					if (shadowText)
						method392(aByteArrayArray1491[c], xPos + anIntArray1494[c] + 1, yPos + anIntArray1495[c] + 1, anIntArray1492[c], anIntArray1493[c], 0);
					method392(aByteArrayArray1491[c], xPos + anIntArray1494[c], yPos + anIntArray1495[c], anIntArray1492[c], anIntArray1493[c], color);
				}
				xPos += CHARACTER_WIDTH_VALUES[c];
			}
		if (aBoolean1499)
			DrawingArea.method339(yPos + (int) (anInt1497 * 0.69999999999999996D), 0x800000, xPos - l, l);
	}

	public void method390(int i, int j, String s, int k, int i1) {
		if (s == null)
			return;
		int j1 = 192;
		i1 -= anInt1497;
		for (int k1 = 0; k1 < s.length(); k1++)
			if (s.charAt(k1) == '@' && k1 + 4 < s.length() && s.charAt(k1 + 4) == '@') {
				int l1 = getColorByName(s.substring(k1 + 1, k1 + 4));
				if (l1 != -1)
					j = l1;
				k1 += 4;
			} else {
				char c = s.charAt(k1);
				if (c != ' ') {
					method394(192, i + anIntArray1494[c] + 1, aByteArrayArray1491[c], anIntArray1492[c], i1 + anIntArray1495[c] + 1, anIntArray1493[c], 0);
					method394(j1, i + anIntArray1494[c], aByteArrayArray1491[c], anIntArray1492[c], i1 + anIntArray1495[c], anIntArray1493[c], j);
				}
				i += CHARACTER_WIDTH_VALUES[c];
			}
	}

	private int getColorByName(String s) {
		if (s.equals("369"))// color code, use as @###@
			return 0x336699;// hex code
		if (s.equals("mon"))
			return 0x00ff80;
		if (s.equals("red"))
			return 0xff0000;
		if (s.equals("gre"))
			return 65280;
		if (s.equals("blu"))
			return 255;
		if (s.equals("yel"))
			return 0xffff00;
		if (s.equals("cya"))
			return 65535;
		if (s.equals("mag"))
			return 0xff00ff;
		if (s.equals("whi"))
			return 0xffffff;
		if (s.equals("bla"))
			return 0;
		if (s.equals("lre"))
			return 0xff9040;
		if (s.equals("dre"))
			return 0x800000;
		if (s.equals("dbl"))
			return 128;
		if (s.equals("or1"))
			return 0xffb000;
		if (s.equals("or2"))
			return 0xff7000;
		if (s.equals("or3"))
			return 0xff3000;
		if (s.equals("gr1"))
			return 0xc0ff00;
		if (s.equals("gr2"))
			return 0x80ff00;
		if (s.equals("gr3"))
			return 0x40ff00;
		if (s.equals("str"))
			aBoolean1499 = true;
		if (s.equals("end"))
			aBoolean1499 = false;
		return -1;
	}

	private void method392(byte abyte0[], int i, int j, int k, int l, int i1) {
		int j1 = i + j * width;
		int k1 = width - k;
		int l1 = 0;
		int i2 = 0;
		if (j < topY) {
			int j2 = topY - j;
			l -= j2;
			j = topY;
			i2 += j2 * k;
			j1 += j2 * width;
		}
		if (j + l >= bottomY)
			l -= ((j + l) - bottomY);
		if (i < topX) {
			int k2 = topX - i;
			k -= k2;
			i = topX;
			i2 += k2;
			j1 += k2;
			l1 += k2;
			k1 += k2;
		}
		if (i + k >= bottomX) {
			int l2 = ((i + k) - bottomX);
			k -= l2;
			l1 += l2;
			k1 += l2;
		}
		if (!(k <= 0 || l <= 0)) {
			method393(pixels, abyte0, i1, i2, j1, k, l, k1, l1);
		}
	}

	private void method393(int ai[], byte abyte0[], int i, int j, int k, int l, int i1, int j1, int k1) {
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				if (abyte0[j++] != 0)
					ai[k++] = i;
				else
					k++;
				if (abyte0[j++] != 0)
					ai[k++] = i;
				else
					k++;
				if (abyte0[j++] != 0)
					ai[k++] = i;
				else
					k++;
				if (abyte0[j++] != 0)
					ai[k++] = i;
				else
					k++;
			}
			for (int k2 = l; k2 < 0; k2++)
				if (abyte0[j++] != 0)
					ai[k++] = i;
				else
					k++;

			k += j1;
			j += k1;
		}
	}

	private void method394(int i, int j, byte abyte0[], int k, int l, int i1, int j1) {
		int k1 = j + l * width;
		int l1 = width - k;
		int i2 = 0;
		int j2 = 0;
		if (l < topY) {
			int k2 = topY - l;
			i1 -= k2;
			l = topY;
			j2 += k2 * k;
			k1 += k2 * width;
		}
		if (l + i1 >= bottomY)
			i1 -= ((l + i1) - bottomY);
		if (j < topX) {
			int l2 = topX - j;
			k -= l2;
			j = topX;
			j2 += l2;
			k1 += l2;
			i2 += l2;
			l1 += l2;
		}
		if (j + k >= bottomX) {
			int i3 = ((j + k) - bottomX);
			k -= i3;
			i2 += i3;
			l1 += i3;
		}
		if (k <= 0 || i1 <= 0)
			return;
		method395(abyte0, i1, k1, pixels, j2, k, i2, l1, j1, i);
	}

	private void method395(byte abyte0[], int i, int j, int ai[], int l, int i1, int j1, int k1, int l1, int i2) {
		l1 = ((l1 & 0xff00ff) * i2 & 0xff00ff00) + ((l1 & 0xff00) * i2 & 0xff0000) >> 8;
		i2 = 256 - i2;
		for (int j2 = -i; j2 < 0; j2++) {
			for (int k2 = -i1; k2 < 0; k2++)
				if (abyte0[l++] != 0) {
					int l2 = ai[j];
					ai[j++] = (((l2 & 0xff00ff) * i2 & 0xff00ff00) + ((l2 & 0xff00) * i2 & 0xff0000) >> 8) + l1;
				} else {
					j++;
				}
			j += k1;
			l += j1;
		}
	}

	public byte[][] aByteArrayArray1491;
	public int[] anIntArray1492;
	public int[] anIntArray1493;
	public int[] anIntArray1494;
	public int[] anIntArray1495;
	public int[] CHARACTER_WIDTH_VALUES;
	public int anInt1497;
	public boolean aBoolean1499;
}
