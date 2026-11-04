package com.koios.client;

import java.applet.AppletContext;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.koios.client.cache.Decompressor;
import com.koios.client.cache.node.Node;
import com.koios.client.cache.node.NodeList;
import com.koios.client.cache.ondemand.OnDemandData;
import com.koios.client.cache.ondemand.OnDemandFetcher;
import com.koios.client.constants.Constants;
import com.koios.client.constants.SizeConstants;
import com.koios.client.constants.SkillConstants;
import com.koios.client.definition.Animation;
import com.koios.client.definition.FloorDefinition;
import com.koios.client.definition.FrameReader;
import com.koios.client.definition.IdentitiKit;
import com.koios.client.definition.ItemDefinition;
import com.koios.client.definition.NPCDefinition;
import com.koios.client.definition.ObjectDefinition;
import com.koios.client.definition.RSInterface;
import com.koios.client.definition.SpotAnim;
import com.koios.client.definition.VarBit;
import com.koios.client.definition.Varp;
import com.koios.client.graphic.Background;
import com.koios.client.graphic.DrawingArea;
import com.koios.client.graphic.ImageProducer;
import com.koios.client.graphic.RSFont;
import com.koios.client.graphic.Rasterizer;
import com.koios.client.graphic.Sprite;
import com.koios.client.graphic.SpriteLoader;
import com.koios.client.graphic.TextDrawingArea;
import com.koios.client.music.Class56;
import com.koios.client.music.Class56_Sub1_Sub1;
import com.koios.client.net.RSSocket;
import com.koios.client.sign.Signlink;
import com.koios.client.sound.Class25;
import com.koios.client.sound.Class3_Sub7;
import com.koios.client.sound.Class3_Sub7_Sub1;
import com.koios.client.sound.Class3_Sub7_Sub2;
import com.koios.client.sound.Class3_Sub9_Sub1;
import com.koios.client.sound.Class5;
import com.koios.client.sound.Class5_Sub1;
import com.koios.client.sound.Class5_Sub2;
import com.koios.client.sound.Class5_Sub2_Sub1;
import com.koios.client.sound.Class5_Sub2_Sub2;
import com.koios.client.sound.Sound;
import com.koios.client.stream.Stream;
import com.koios.client.stream.StreamLoader;
import com.koios.client.ui.RSApplet;
import com.koios.client.ui.ScreenMode;
import com.koios.client.utils.ArrayUtils;
import com.koios.client.utils.CacheDownloader;
import com.koios.client.utils.Censor;
import com.koios.client.utils.FileOperations;
import com.koios.client.utils.ISAACRandomGen;
import com.koios.client.utils.TextClass;
import com.koios.client.utils.TextInput;
import com.koios.client.utils.UsernameEncoding;
import com.koios.client.utils.CacheDownloader.FileType;
import com.koios.client.world.CustomObjects;
import com.koios.client.world.Model;
import com.koios.client.world.ObjectManager;
import com.koios.client.world.WorldController;
import com.koios.client.world.entity.Animable;
import com.koios.client.world.entity.Animable_Sub3;
import com.koios.client.world.entity.Animable_Sub4;
import com.koios.client.world.entity.Animable_Sub5;
import com.koios.client.world.entity.Entity;
import com.koios.client.world.entity.Item;
import com.koios.client.world.entity.NPC;
import com.koios.client.world.entity.Player;
import com.koios.client.world.objects.Object1;
import com.koios.client.world.objects.Object2;
import com.koios.client.world.objects.Object3;
import com.koios.client.world.objects.Object5;

public class Client extends RSApplet {

	public static boolean enableTweening = true;

	public static ScreenMode frameMode = ScreenMode.FIXED;
	public static int frameWidth = 765;
	public static int frameHeight = 503;
	public static int screenAreaWidth = 512;
	public static int screenAreaHeight = 334;
	public static int cameraZoom = 600;
	public static boolean showChatComponents = true;
	public static boolean showTabComponents = true;
	public static boolean changeTabArea = frameMode == ScreenMode.FIXED ? false : true;
	public static boolean changeChatArea = frameMode == ScreenMode.FIXED ? false : true;
	public static boolean transparentTabArea = false;
	public static boolean hdShading = false;
	public static boolean oneClickAttack = true;
	public static boolean hdMinimap = false;

	private static final int[] CLANCHAT_RANK_SPRITE = { 491, 500, 494, 495, 496, 497, 498, 499, 492, -1, -1, 493 };
	public static String clanName = "";
	public static ArrayList<ClanMember> rankedNames = new ArrayList<>();

	public void loadClan() {
		for (int i = 18144; i < 18244; i++) {
			setInterfaceText("", i);
		}

		for (int i = 38144; i <= 38244; i++) {
			RSInterface icons = RSInterface.interfaceCache[i];

			if (icons != null) {
				icons.sprite1 = icons.sprite2 = Client.cacheSprite[491];
			}
		}
	}

	/**
	 * The current connection status for the client
	 */
	private final com.koios.client.arpg.ItemTooltipCache arpgTooltips=new com.koios.client.arpg.ItemTooltipCache();
    private int arpgHoverWidget=-1,arpgHoverSlot=-1;
    public static ClientStatus clientStatus = ClientStatus.LOCAL;

	public RSFont newSmallFont, newRegularFont, newBoldFont;
	public RSFont newFancyFont;

	private int mapX;
	private int mapY;

	private boolean roofsOff;

	public static void setInterfaceText(String text, int componentId) {
		try {
			RSInterface component = RSInterface.interfaceCache[componentId];
			component.setText(text);

			if (component.type == 9) {
				component.hoverText = text;
			}

			if ((component.aString228 != null) && (component.aString228.length() > 0)) {
				component.aString228 = "";
			}
		} catch (Exception ex) {
			ex.getStackTrace();
		}
	}

	public String capitalize(String s) {
		return (s.length() > 0) ? Character.toUpperCase(s.charAt(0)) + s.substring(1) : s;
	}

	public void addObject(int x, int y, int objectId, int face, int type, int height) {
		int mX = mapX - 6;
		int mY = mapY - 6;
		int regionX = x - mX * 8;
		int regionY = y - mY * 8;
		int i15 = 40 >> 2;
		int l17 = anIntArray1177[i15];
		if (regionY > 0 && regionY < 103 && regionX > 0 && regionX < 103) {

			if (height == realPlane) {
				method130(-1, objectId, face, l17, regionY, type, height, regionX, 0);
			} else {
				method130(-1, CustomObjects.BLANK_OBJECT_ID, face, l17, regionY, type, plane, regionX, 0);
			}
		}
	}

 private File configFile(){String qa=System.getProperty("arpg.qaCharacter","");return new File(Signlink.findcachedir(),qa.matches("qa[a-z0-9]{3,9}")?"config-"+qa+".dat":"config.dat");}
	public void saveData() {
		try {

			File file = configFile();
			if (!file.exists()) {
				file.createNewFile();
			}
			DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
			stream.writeBoolean(rememberMe);
			stream.writeUTF(!rememberMe ? "" : myUsername);
			stream.writeUTF(!rememberMe ? "" : myPassword);
			stream.writeBoolean(hdShading);
			stream.writeBoolean(roofsOff);
			stream.flush();
			stream.close();
		} catch (IOException e) {
			System.err.println("Failed to save config file!");
		}
	}

	public void readData() {
		File file = configFile();

		if (!file.exists()) {
			return;
		}

		try {

			DataInputStream stream = new DataInputStream(new FileInputStream(file));
			rememberMe = stream.readBoolean();
			myUsername = stream.readUTF();
			myPassword = stream.readUTF();
			hdShading = stream.readBoolean();
			roofsOff = stream.readBoolean();

			stream.close();
		} catch (IOException e) {
			System.err.println("Failed to load config file!");
		}
	}

	public void frameMode(ScreenMode screenMode) {
		if (frameMode != screenMode) {
			frameMode = screenMode;
			if (screenMode == ScreenMode.FIXED) {
				frameWidth = forceWidth = 765;
				frameHeight = forceHeight = 503;
				cameraZoom = 600;
				WorldController.viewDistance = 9;
				changeChatArea = false;
				changeTabArea = false;
				welcomeScreenRaised = true;
			} else if (screenMode == ScreenMode.RESIZABLE) {
				frameWidth = super.myWidth = 766;
				frameHeight = super.myHeight = 563;
				cameraZoom = 850;
				forceWidth = forceHeight = -1;
				WorldController.viewDistance = 10;
				changeTabArea = true;
				changeChatArea = true;
				transparentTabArea = true;
			} else if (screenMode == ScreenMode.FULLSCREEN) {
				java.awt.Rectangle display=getGameComponent().getGraphicsConfiguration().getBounds();
                frameWidth = super.myWidth = display.width;
				frameHeight = super.myHeight = display.height;
				cameraZoom = 600;
				forceWidth = forceHeight = -1;
				WorldController.viewDistance = 10;// kk login and test it
			}
			rebuildFrameSize(screenMode, frameWidth, frameHeight);

			if (!loggedIn) {
				resetImageProducers();
			} else {
				resetImageProducers2();
			}
			updateScreen();
			saveData();
			showChatComponents = screenMode == ScreenMode.FIXED || showChatComponents;
			showTabComponents = screenMode == ScreenMode.FIXED || showTabComponents;
		}
	}

	public void rebuildFrameSize(ScreenMode screenMode, int screenWidth, int screenHeight) {
		try {
			screenAreaWidth = (screenMode == ScreenMode.FIXED) ? 512 : screenWidth;
			screenAreaHeight = (screenMode == ScreenMode.FIXED) ? 334 : screenHeight;
			frameWidth = screenWidth;
			frameHeight = screenHeight;
			instance.refreshFrameSize(screenMode == ScreenMode.FULLSCREEN, screenWidth, screenHeight, screenMode == ScreenMode.RESIZABLE, screenMode != ScreenMode.FIXED);
			if(screenMode!=ScreenMode.FIXED){frameWidth=super.myWidth=getRealScreenWidth();frameHeight=super.myHeight=getRealScreenHeight();}
            instance.updateScreen();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshFrameSize() {
		if (frameMode != ScreenMode.FIXED) {
			if (frameWidth != getRealScreenWidth()) {
				frameWidth = super.myWidth = screenAreaWidth = getRealScreenWidth();// super.getSize().width;
				updateScreen();
				;// just testing this code
			}
			if (frameHeight != getRealScreenHeight()) {
				frameHeight = super.myHeight = screenAreaHeight = getRealScreenHeight();// super.getSize().height;
				updateScreen();
				;
			}
		}
	}

	private void updateScreen() {
		screenAreaWidth = frameMode.isFixed() ? 512 : getScreenWidth();
		screenAreaHeight = frameMode.isFixed() ? 334 : getScreenHeight();
		if (aRSImageProducer_1165 == null || aRSImageProducer_1165.canvasWidth != screenAreaWidth || aRSImageProducer_1165.canvasHeight != screenAreaHeight) {
			aRSImageProducer_1165 = new ImageProducer(screenAreaWidth, screenAreaHeight);
		}

		setBounds();
		updateGraphics(false);
	}

	private void setBounds() {
		Rasterizer.method365(frameWidth, frameHeight);
		fullScreenTextureArray = Rasterizer.lineOffsets;
		Rasterizer.method365(frameMode == ScreenMode.FIXED ? (aRSImageProducer_1166 != null ? aRSImageProducer_1166.canvasWidth : 519) : frameWidth,
				frameMode == ScreenMode.FIXED ? (aRSImageProducer_1166 != null ? aRSImageProducer_1166.canvasHeight : 165) : frameHeight);
		anIntArray1180 = Rasterizer.lineOffsets;
		Rasterizer.method365(frameMode == ScreenMode.FIXED ? (aRSImageProducer_1163 != null ? aRSImageProducer_1163.canvasWidth : 249) : frameWidth,
				frameMode == ScreenMode.FIXED ? (aRSImageProducer_1163 != null ? aRSImageProducer_1163.canvasHeight : 335) : frameHeight);
		anIntArray1181 = Rasterizer.lineOffsets;
		Rasterizer.method365(screenAreaWidth, screenAreaHeight);
		anIntArray1182 = Rasterizer.lineOffsets;
		int ai[] = new int[9];
		for (int i8 = 0; i8 < 9; i8++) {
			int k8 = 128 + i8 * 32 + 15;
			int l8 = 600 + k8 * 3;
			int i9 = Rasterizer.SINE[k8];
			ai[i8] = l8 * i9 >> 16;
		}
		if (frameMode == ScreenMode.RESIZABLE && (frameWidth >= 756) && (frameWidth <= 1025) && (frameHeight >= 494) && (frameHeight <= 850)) {
			WorldController.viewDistance = 9;
			cameraZoom = 575;
		} else if (frameMode == ScreenMode.FIXED) {
			cameraZoom = 600;
		} else if (frameMode == ScreenMode.RESIZABLE) {
			WorldController.viewDistance = 10;
			cameraZoom = 600;
		}

		WorldController.method310(500, 800, screenAreaWidth, screenAreaHeight, ai);
		if (loggedIn) {
			aRSImageProducer_1165 = new ImageProducer(screenAreaWidth, screenAreaHeight);
		} else {
			resetImageProducers();
		}

		instance.updateGraphics(true);
	}

	public boolean getMousePositions() {
		if (mouseInRegion(frameWidth - (frameWidth <= 1000 ? 240 : 420), frameHeight - (frameWidth <= 1000 ? 90 : 37), frameWidth, frameHeight)) {
			return false;
		}
		if (showChatComponents) {
			if (changeChatArea) {
				if (super.mouseX > 0 && super.mouseX < 494 && super.mouseY > frameHeight - 175 - extendChatArea && super.mouseY < frameHeight) {
					return true;
				} else {
					if (super.mouseX > 494 && super.mouseX < 515 && super.mouseY > frameHeight - 175 - extendChatArea && super.mouseY < frameHeight) {
						return false;
					}
				}
			} else if (!changeChatArea) {
				if (super.mouseX > 0 && super.mouseX < 519 && super.mouseY > frameHeight - 175 - extendChatArea && super.mouseY < frameHeight) {
					return false;
				}
			}
		}
		if (mouseInRegion2(frameWidth - 216, frameWidth, 0, 172)) {
			return false;
		}
		if (!changeTabArea) {
			if (super.mouseX > 0 && super.mouseY > 0 && super.mouseY < frameWidth && super.mouseY < frameHeight) {
				if (super.mouseX >= frameWidth - 242 && super.mouseY >= frameHeight - 335) {
					return false;
				}
				return true;
			}
			return false;
		}
		if (showTabComponents) {
			if (frameWidth > 1000) {
				if (super.mouseX >= frameWidth - 420 && super.mouseX <= frameWidth && super.mouseY >= frameHeight - 37 && super.mouseY <= frameHeight
						|| super.mouseX > frameWidth - 225 && super.mouseX < frameWidth && super.mouseY > frameHeight - 37 - 274 && super.mouseY < frameHeight) {
					return false;
				}
			} else {
				if (super.mouseX >= frameWidth - 210 && super.mouseX <= frameWidth && super.mouseY >= frameHeight - 74 && super.mouseY <= frameHeight
						|| super.mouseX > frameWidth - 225 && super.mouseX < frameWidth && super.mouseY > frameHeight - 74 - 274 && super.mouseY < frameHeight) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean mouseInRegion2(int x1, int x2, int y1, int y2) {
		if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1 && super.mouseY <= y2) {
			return true;
		}
		return false;
	}

	public boolean mouseInRegion(int x1, int y1, int x2, int y2) {
		if (super.mouseX >= x1 && super.mouseX <= x2 && super.mouseY >= y1 && super.mouseY <= y2) {
			return true;
		}
		return false;
	}

	public boolean mouseMapPosition() {
		if (super.mouseX >= frameWidth - 21 && super.mouseX <= frameWidth && super.mouseY >= 0 && super.mouseY <= 21) {
			return false;
		}
		return true;
	}

	private void drawLoadingMessages(int used, String s, String s1) {
		int width = aTextDrawingArea_1271.getTextWidth(used == 1 ? s : s1);
		int height = s1 == null ? 25 : 38;
		DrawingArea.drawPixels(height, 1, 1, 0, width + 6);
		DrawingArea.drawPixels(1, 1, 1, 0xffffff, width + 6);
		DrawingArea.drawPixels(height, 1, 1, 0xffffff, 1);
		DrawingArea.drawPixels(1, height, 1, 0xffffff, width + 6);
		DrawingArea.drawPixels(height, 1, width + 6, 0xffffff, 1);
		aTextDrawingArea_1271.drawText(0xffffff, s, 18, width / 2 + 5);
		if (s1 != null) {
			aTextDrawingArea_1271.drawText(0xffffff, s1, 31, width / 2 + 5);
		}
	}

	private static final long serialVersionUID = 5707517957054703648L;

	/**
	 * newDamage enables or disables fake constitution.
	 */
	public static boolean newDamage = true;

	/**
	 * Client Preferences
	 *
	 * hitMarks554 can be enabled or disabled between 554 and 317. hitBar554 can
	 * be enabled or disabled between 554 and 317. sumOrb can be changed to
	 * runEnergy can be set to true to enable run energy support.
	 */
	public boolean hitmarks554 = true, hpBar554 = true, runEnergy = false;

	private static String intToKOrMilLongName(int i) {
		String s = String.valueOf(i);
		for (int k = s.length() - 3; k > 0; k -= 3)
			s = s.substring(0, k) + "," + s.substring(k);
		if (s.length() > 8)
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		else if (s.length() > 4)
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		return " " + s;
	}

	public final String methodR(int j) {
		if (j >= 0 && j < 10000)
			return String.valueOf(j);
		if (j >= 10000 && j < 10000000)
			return j / 1000 + "K";
		if (j >= 10000000 && j < 999999999)
			return j / 1000000 + "M";
		if (j >= 999999999)
			return "*";
		else
			return "?";
	}

	public static final byte[] ReadFile(String s) {
		try {
			byte abyte0[];
			File file = new File(s);
			int i = (int) file.length();
			abyte0 = new byte[i];
			DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new FileInputStream(s)));
			datainputstream.readFully(abyte0, 0, i);
			datainputstream.close();
			return abyte0;
		} catch (Exception e) {
			System.out.println((new StringBuilder()).append("Read Error: ").append(s).toString());
			return null;
		}
	}

	private void stopMidi() {
		Signlink.midifade = 0;
		Signlink.midi = "stop";
	}

	private boolean menuHasAddFriend(int j) {
		if (j < 0)
			return false;
		int k = menuActionID[j];
		if (k >= 2000)
			k -= 2000;
		return k == 337;
	}

	private final int[] modeX = { 164, 230, 296, 362 }, modeNamesX = { 26, 86, 150, 212, 286, 349, 427 }, modeNamesY = { 158, 158, 153, 153, 153, 153, 158 },
			channelButtonsX = { 5, 71, 137, 203, 269, 335, 404 };

	private final String[] modeNames = { "All", "Game", "Public", "Private", "Clan", "Trade", "Report Abuse" };

	private int modifiableXValue;

	public void drawChannelButtons() {
        int yOffset=frameMode==ScreenMode.FIXED?0:frameHeight-165;
        com.koios.client.arpg.ChatChrome.channels(com.koios.client.arpg.ExileTheme.small(),yOffset,cButtonCPos,cButtonHPos,new int[]{publicChatMode,privateChatMode,clanChatMode,tradeMode});
    }

	private boolean chatStateCheck() {
		return messagePromptRaised || inputDialogState != 0 || aString844 != null || backDialogID != -1 || dialogID != -1;
	}

	private void drawChatArea() {
		int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 165;
		if (frameMode == ScreenMode.FIXED) {
			aRSImageProducer_1166.initDrawingArea();
		}
		Rasterizer.lineOffsets = anIntArray1180;
		
		// if chat area is larger then frame, resize it
		if (extendChatArea + 230 > frameHeight) {
			extendChatArea = frameHeight - 230 - 25;
		}
        if(chatStateCheck()){extendChatArea=0;showChatComponents=true;}
        if(showChatComponents)com.koios.client.arpg.ChatChrome.backdrop(yOffset,extendChatArea,changeChatArea&&!chatStateCheck());
		drawChannelButtons();
		TextDrawingArea textDrawingArea = aTextDrawingArea_1271;
		if (messagePromptRaised) {
			extendChatArea = 0;
			newBoldFont.drawCenteredString(aString1121, 259, 60 + yOffset, 0xD4BB87, 0);
			newBoldFont.drawCenteredString(promptInput + "*", 259, 80 + yOffset, 0xCBD5DD, 0);
		} else if (inputDialogState == 1) {
			extendChatArea = 0;
			newBoldFont.drawCenteredString("Enter amount:", 259, yOffset + 60, 0xD4BB87, 0);
			newBoldFont.drawCenteredString(amountOrNameInput + "*", 259, 80 + yOffset, 0xCBD5DD, 0);
		} else if (inputDialogState == 2) {
			extendChatArea = 0;
			newBoldFont.drawCenteredString("Enter Name:", 259, 60 + yOffset, 0xD4BB87, 0);
			newBoldFont.drawCenteredString(amountOrNameInput + "*", 259, 80 + yOffset, 0xCBD5DD, 0);
		} else if (aString844 != null) {
			extendChatArea = 0;
			newBoldFont.drawCenteredString(aString844, 259, 60 + yOffset, 0xD4BB87, 0);
			newBoldFont.drawCenteredString("Click to continue", 259, 80 + yOffset, 0xCBD5DD, 0);
		} else if (backDialogID != -1) {
			extendChatArea = 0;
			drawInterface(0, 20, RSInterface.interfaceCache[backDialogID], 20 + yOffset);
		} else if (dialogID != -1) {
			extendChatArea = 0;
			drawInterface(0, 20, RSInterface.interfaceCache[dialogID], 20 + yOffset);
		} else if (showChatComponents) {

			int j77 = -3;
			int j = 0;
			DrawingArea.setDrawingArea(122 + yOffset, 8, 497, 7 + yOffset - extendChatArea);
			for (int k = 0; k < 500; k++) {
				if (chatMessages[k] != null) {
					int chatType = chatTypes[k];
					int yPos = 117 - (j77 + 3) * 16 + anInt1089;
					String s1 = chatNames[k];
					byte rights = 0;
					for (int i = 1; i <= modIcons.length; i++) {
						if (s1 != null && s1.startsWith("@cr" + i + "@")) {
							s1 = s1.substring(i > 9 ? 6 : 5);
							rights = (byte) i;
						}
					}

					int shadow = 0;
					if (chatType == 0) {
						if (chatTypeView == 5 || chatTypeView == 0) {
							newRegularFont.drawBasicString(com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), 11, yPos + yOffset, 0xCBD5DD, shadow);
							j++;
							j77++;
						}
					}
					if ((chatType == 1 || chatType == 2) && (chatType == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s1))) {
						if (chatTypeView == 1 || chatTypeView == 0) {
							int xPos = 11;
							if (rights > 0) {
								modIcons[rights - 1].drawBackground(xPos, yPos - 12 + yOffset);
								xPos += 14;
							}
							newRegularFont.drawBasicString(s1 + ":", xPos, yPos + yOffset, 0xCBD5DD, shadow);
							xPos += newRegularFont.getTextWidth(s1) + 8;
							newRegularFont.drawBasicString(com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), xPos, yPos + yOffset, 0xA5C7DF, 0);
							j++;
							j77++;
						}
					}
					if ((chatType == 3 || chatType == 7) && (splitPrivateChat == 0 || chatTypeView == 2) && (chatType == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s1))) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							int k1 = 11;
							newRegularFont.drawBasicString("From", k1, yPos + yOffset, 0xD4BB87, shadow);
							k1 += newRegularFont.getTextWidth("From ");
							if (rights > 0) {
								modIcons[rights - 1].drawBackground(k1, yPos - 12 + yOffset);
								k1 += 12;
							}
							newRegularFont.drawBasicString(s1 + ":", k1, yPos + yOffset, 0xCBD5DD, shadow);
							k1 += newRegularFont.getTextWidth(s1) + 8;
							newRegularFont.drawBasicString(com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), k1, yPos + yOffset, 0xBAACD1, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s1))) {
						if (chatTypeView == 3 || chatTypeView == 0) {
							newRegularFont.drawBasicString(s1 + " " + com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), 11, yPos + yOffset, 0xA1C4B5, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 5 && splitPrivateChat == 0 && privateChatMode < 2) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							newRegularFont.drawBasicString(s1 + " " + com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), 8, yPos + yOffset, 0xBAACD1, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 6 && (splitPrivateChat == 0 || chatTypeView == 2) && privateChatMode < 2) {
						if (chatTypeView == 2 || chatTypeView == 0) {
							newRegularFont.drawBasicString("To " + s1 + ":", 11, yPos + yOffset, 0xD4BB87, 0);
							newRegularFont.drawBasicString(com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), 15 + newRegularFont.getTextWidth("To :" + s1), yPos + yOffset, 0xBAACD1, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s1))) {
						if (chatTypeView == 3 || chatTypeView == 0) {
							newRegularFont.drawBasicString(s1 + " " + com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), 11, yPos + yOffset, 0xD2B38C, shadow);
							j++;
							j77++;
						}
						if (chatType == 11 && (clanChatMode == 0)) {
							if (chatTypeView == 11) {
								newRegularFont.drawBasicString(s1 + " " + com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), 11, yPos + yOffset, 0xD2B38C, shadow);
								j++;
								j77++;
							}
							if (chatType == 12) {
								newRegularFont.drawBasicString(chatMessages[k] + " @blu@", 11, yPos + yOffset, 0xD2B38C, shadow);
								j++;
							}
						}
					}
					if (chatType == 16) {
						int j2 = 40;
						int clanNameWidth = newRegularFont.getTextWidth(clanname);
						if (chatTypeView == 11 || chatTypeView == 0) {
							if (chatRights[k] > 0) {
								j2 += clanNameWidth;
								modIcons[chatRights[k] - 1].drawBackground(j2 - 18, yPos - 12 + yOffset);
								j2 += 14;
							} else {
								j2 += clanNameWidth;
							}

							newRegularFont.drawBasicString("[", 8, yPos + yOffset, 0xCBD5DD, shadow);
							newRegularFont.drawBasicString(clanname, 14, yPos + yOffset, 0xA5C7DF, shadow);
							newRegularFont.drawBasicString("]", clanNameWidth + 14, yPos + yOffset, 0xCBD5DD, shadow);
							newRegularFont.drawBasicString(chatNames[k] + ":", j2 - 17, yPos + yOffset, 0xCBD5DD, shadow);
							j2 += newRegularFont.getTextWidth(chatNames[k]) + 7;
							newRegularFont.drawBasicString(com.koios.client.arpg.ChatChrome.readable(chatMessages[k]), j2 - 16, yPos + yOffset, 0xBAACD1, shadow);
							j++;
							j77++;
						}
					}
				}
			}
			DrawingArea.defaultDrawingAreaSize();
			anInt1211 = j * 16 + 12;
			if (anInt1211 < 111) {
				anInt1211 = 111;
			}
			com.koios.client.arpg.ChatChrome.scrollbar(114 + extendChatArea, anInt1211 - anInt1089 - 113, 7 + yOffset - extendChatArea, 496, anInt1211 + extendChatArea);
			String s;
			if (myPlayer != null && myPlayer.name != null) {
				s = myPlayer.name;
			} else {
				s = UsernameEncoding.formalize(myUsername);
			}
			DrawingArea.setDrawingArea(140 + yOffset, 8, 509, 120 + yOffset);

			int shadow = 0;
			int xOffset = 0;
			if (myPrivilege > 0) {
				if (myPrivilege < 5)
					xOffset += 1;
				modIcons[myPrivilege - 1].drawBackground(10 + xOffset, 122 + yOffset);
				xOffset += 14;
			}
			newRegularFont.drawBasicString(s + ":", xOffset + 11, 133 + yOffset, 0xCBD5DD, shadow);
            int inputX=xOffset+12+newRegularFont.getTextWidth(s+": ");
			newRegularFont.drawBasicString(com.koios.client.arpg.ChatChrome.draft(com.koios.client.arpg.ExileTheme.body(),chatFocus.isEditing()?inputString+"*":"Press Enter or click to chat",500-inputX),inputX,133+yOffset,0xA5C7DF,shadow);
			DrawingArea.method339(121 + yOffset, 0x35434D, 506, 7);
			DrawingArea.defaultDrawingAreaSize();
		}
		
		
		if (changeChatArea && !messagePromptRaised && backDialogID == -1 && inputDialogState <= 0) {
			int yy = frameHeight - 160;
			int x = 256;
			int yDraw = yy - 10 - extendChatArea;
			DrawingArea.drawRectangle(x, yDraw + 7, 10, 10, 0x8C9CA7, 125);
		}
		
        if((prototypeOpen||arpgSkillsOpen)&&backDialogID==-1&&!messagePromptRaised&&inputDialogState==0){
            DrawingArea.defaultDrawingAreaSize();mapObjective.drawInChat(com.koios.client.arpg.ExileTheme.body(),yOffset);
        }
		if (menuOpen) {
			drawMenu(0, frameMode == ScreenMode.FIXED ? 338 : 0);
		}
		if (frameMode == ScreenMode.FIXED) {
			aRSImageProducer_1166.drawGraphics(338, super.graphics, 0);
		}
		
		
		aRSImageProducer_1165.initDrawingArea();
		Rasterizer.lineOffsets = anIntArray1182;
	}

	public void extendChatArea() {
		if (frameMode == ScreenMode.FIXED || !changeChatArea) {
			extendChatArea = 0;
			return;
		}
		int offsetY = frameHeight - 160;
		int x = 256;
		int y = offsetY - 10 - extendChatArea;
		
		if (super.mouseX >= x && super.mouseX <= x + 10 && super.mouseY >= y && super.mouseY <= y + 17) {
			if (super.clickMode2 == 1) {
				isExtendingChatArea = true;
			}
		}
		if (isExtendingChatArea) {
			int height = offsetY - super.mouseY;
			if (height < frameHeight - 230) {
				extendChatArea = height > 0 ? height : 0;
			}
		}
	}

	@Override
	public void init() {
		try {
			nodeID = 10;
			portOff = 0;
			setHighMem();
			isMembers = true;
			Signlink.storeid = 32;
			Signlink.startpriv(InetAddress.getLocalHost());
			initClientFrame(503, 765);
			instance = this;
		} catch (Exception exception) {
			return;
		}
	}

	@Override
	public void startRunnable(Runnable runnable, int i) {
		if (i > 10)
			i = 10;
		if (Signlink.mainapp != null) {
			Signlink.startthread(runnable, i);
		} else {
			super.startRunnable(runnable, i);
		}
	}

	public Socket openSocket(int port) throws IOException {
		return new Socket(InetAddress.getByName(clientStatus.getAddress()), port);
	}

	private void processMenuClick() {
		if (activeInterfaceType != 0)
			return;
		int j = super.clickMode3;
		if (spellSelected == 1 && super.saveClickX >= 516 && super.saveClickY >= 160 && super.saveClickX <= 765 && super.saveClickY <= 205)
			j = 0;
		if (menuOpen) {
			if (j != 1) {
				int k = super.mouseX;
				int j1 = super.mouseY;
				if (menuScreenArea == 0) {
					k -= 4;
					j1 -= 4;
				}
				if (menuScreenArea == 1) {
					k -= 519;
					j1 -= 168;
				}
				if (menuScreenArea == 2) {
					k -= 17;
					j1 -= 338;
				}
				if (menuScreenArea == 3) {
					k -= 519;
					j1 -= 0;
				}
				if (k < menuOffsetX - 10 || k > menuOffsetX + menuWidth + 10 || j1 < menuOffsetY - 10 || j1 > menuOffsetY + menuHeight + 10) {
					menuOpen = false;
					if (menuScreenArea == 1) {
					}
					if (menuScreenArea == 2)
						inputTaken = true;
				}
			}
			if (j == 1) {
				int l = menuOffsetX;
				int k1 = menuOffsetY;
				int i2 = menuWidth;
				int k2 = super.saveClickX;
				int l2 = super.saveClickY;
				switch (menuScreenArea) {
				case 0:
					k2 -= 4;
					l2 -= 4;
					break;
				case 1:
					k2 -= 519;
					l2 -= 168;
					break;
				case 2:
					k2 -= 5;
					l2 -= 338;
					break;
				case 3:
					k2 -= 519;
					l2 -= 0;
					break;
				}
				int i3 = -1;
				for (int j3 = 0; j3 < menuActionRow; j3++) {
					int k3 = k1 + 31 + (menuActionRow - 1 - j3) * 15;
					if (k2 > l && k2 < l + i2 && l2 > k3 - 13 && l2 < k3 + 3)
						i3 = j3;
				}
				if (i3 != -1)
					doAction(i3);
				menuOpen = false;
				if (menuScreenArea == 1) {
				}
				if (menuScreenArea == 2) {
					inputTaken = true;
				}
			}
		} else {
			if (j == 1 && menuActionRow > 0) {
				int i1 = menuActionID[menuActionRow - 1];
                if(com.koios.client.arpg.QaClientHooks.enabled()&&arpgSkillsOpen)System.out.println("QA loadout press: action="+i1+" slot="+menuActionCmd2[menuActionRow-1]+" widget="+menuActionCmd3[menuActionRow-1]);
				if (i1 == 632 || i1 == 78 || i1 == 867 || i1 == 431 || i1 == 53 || i1 == 74 || i1 == 454 || i1 == 539 || i1 == 493 || i1 == 847 || i1 == 447 || i1 == 1125) {
					int l1 = menuActionCmd2[menuActionRow - 1];
					int j2 = menuActionCmd3[menuActionRow - 1];
					RSInterface class9 = RSInterface.interfaceCache[j2];
					if (class9.allowSwapItems || class9.deletesTargetSlot) {
						aBoolean1242 = false;
						anInt989 = 0;
						anInt1084 = j2;
						itemFrom = l1;
						fromTab = class9.id;
						activeInterfaceType = 2;
						anInt1087 = super.saveClickX;
						anInt1088 = super.saveClickY;
						if (RSInterface.interfaceCache[j2].parentID == openInterfaceID)
							activeInterfaceType = 1;
						if (RSInterface.interfaceCache[j2].parentID == backDialogID)
							activeInterfaceType = 3;
						return;
					}
				}
			}
			if (j == 1 && (anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
				j = 2;
			if (j == 1 && menuActionRow > 0)
				doAction(menuActionRow - 1);
			if (j == 2 && menuActionRow > 0)
				determineMenuSize();
			processMainScreenClick();
			processTabClick();
			processChatModeClick();
			minimapHovers();
		}
	}

	public static String getFileNameWithoutExtension(String fileName) {
		File tmpFile = new File(fileName);
		tmpFile.getName();
		int whereDot = tmpFile.getName().lastIndexOf('.');
		if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
			return tmpFile.getName().substring(0, whereDot);
		}
		return "";
	}

	public void preloadModels() {
		File file = new File(Signlink.findcachedir() + "Raw/");
		File[] fileArray = file.listFiles();
		for (int y = 0; y < fileArray.length; y++) {
			String s = fileArray[y].getName();
			byte[] buffer = ReadFile(Signlink.findcachedir() + "Raw/" + s);
			Model.method460(buffer, Integer.parseInt(getFileNameWithoutExtension(s)));
		}
	}

	private void method22() {
		try {
			anInt985 = -1;
			aClass19_1056.removeAll();
			aClass19_1013.removeAll();
			Rasterizer.method366();
			unlinkMRUNodes();
			worldController.initToNull();
			System.gc();
			for (int i = 0; i < 4; i++)
				aClass11Array1230[i].method210();
			for (int l = 0; l < 4; l++) {
				for (int k1 = 0; k1 < 104; k1++) {
					for (int j2 = 0; j2 < 104; j2++)
						byteGroundArray[l][k1][j2] = 0;
				}
			}

			ObjectManager objectManager = new ObjectManager(byteGroundArray, intGroundArray);
			int k2 = aByteArrayArray1183.length;
			stream.createFrame(0);
			if (!aBoolean1159) {
				for (int i3 = 0; i3 < k2; i3++) {
					int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
					int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;
					byte abyte0[] = aByteArrayArray1183[i3];
					if (FileOperations.FileExists(Signlink.findcachedir() + "maps/" + anIntArray1235[i3] + ".dat"))
						abyte0 = FileOperations.ReadFile(Signlink.findcachedir() + "maps/" + anIntArray1235[i3] + ".dat");
					if (abyte0 != null)
						objectManager.method180(abyte0, k5, i4, (anInt1069 - 6) * 8, (anInt1070 - 6) * 8, aClass11Array1230);
				}
				for (int j4 = 0; j4 < k2; j4++) {
					int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
					int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;
					byte abyte2[] = aByteArrayArray1183[j4];
					if (abyte2 == null && anInt1070 < 800)
						objectManager.method174(k7, 64, 64, l5);
				}
				anInt1097++;
				if (anInt1097 > 160) {
					anInt1097 = 0;
					stream.createFrame(238);
					stream.writeLEShort(96);
				}
				stream.createFrame(0);
				for (int i6 = 0; i6 < k2; i6++) {
					byte abyte1[] = aByteArrayArray1247[i6];
					if (abyte1 != null) {
						int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
						int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;
						objectManager.method190(l8, aClass11Array1230, k9, worldController, abyte1);
					}
				}
				CustomObjects.load();
			}
			if (aBoolean1159) {
				for (int j3 = 0; j3 < 4; j3++) {
					for (int k4 = 0; k4 < 13; k4++) {
						for (int j6 = 0; j6 < 13; j6++) {
							int l7 = anIntArrayArrayArray1129[j3][k4][j6];
							if (l7 != -1) {
								int i9 = l7 >> 24 & 3;
								int l9 = l7 >> 1 & 3;
								int j10 = l7 >> 14 & 0x3ff;
								int l10 = l7 >> 3 & 0x7ff;
								int j11 = (j10 / 8 << 8) + l10 / 8;
								for (int l11 = 0; l11 < anIntArray1234.length; l11++) {
									if (anIntArray1234[l11] != j11 || aByteArrayArray1183[l11] == null)
										continue;
									objectManager.method179(i9, l9, aClass11Array1230, k4 * 8, (j10 & 7) * 8, aByteArrayArray1183[l11], (l10 & 7) * 8, j3, j6 * 8);
									break;
								}

							}
						}
					}
				}
				for (int l4 = 0; l4 < 13; l4++) {
					for (int k6 = 0; k6 < 13; k6++) {
						int i8 = anIntArrayArrayArray1129[0][l4][k6];
						if (i8 == -1)
							objectManager.method174(k6 * 8, 8, 8, l4 * 8);
					}
				}

				stream.createFrame(0);
				for (int l6 = 0; l6 < 4; l6++) {
					for (int j8 = 0; j8 < 13; j8++) {
						for (int j9 = 0; j9 < 13; j9++) {
							int i10 = anIntArrayArrayArray1129[l6][j8][j9];
							if (i10 != -1) {
								int k10 = i10 >> 24 & 3;
								int i11 = i10 >> 1 & 3;
								int k11 = i10 >> 14 & 0x3ff;
								int i12 = i10 >> 3 & 0x7ff;
								int j12 = (k11 / 8 << 8) + i12 / 8;
								for (int k12 = 0; k12 < anIntArray1234.length; k12++) {
									if (anIntArray1234[k12] != j12 || aByteArrayArray1247[k12] == null)
										continue;
									if (FileOperations.FileExists(Signlink.findcachedir() + "maps/" + anIntArray1235[k12] + ".dat"))
										FileOperations.ReadFile(Signlink.findcachedir() + "maps/" + anIntArray1235[k12] + ".dat");
									objectManager.method183(aClass11Array1230, worldController, k10, j8 * 8, (i12 & 7) * 8, l6, aByteArrayArray1247[k12], (k11 & 7) * 8, i11, j9 * 8);
									break;
								}

							}
						}

					}

				}

			}
			stream.createFrame(0);
			if(arpgTerrainLayout>0&&baseX==arpgTerrainBaseX&&baseY==arpgTerrainBaseY)objectManager.applyArpgArena(arpgTerrainLayout);
            objectManager.method171(aClass11Array1230, worldController);
			aRSImageProducer_1165.initDrawingArea();
			stream.createFrame(0);
			int k3 = ObjectManager.anInt145;
			if (k3 > plane)
				k3 = plane;
			if (k3 < plane - 1)
				k3 = plane - 1;
			if (lowMem)
				worldController.method275(ObjectManager.anInt145);
			else
				worldController.method275(0);
			for (int i5 = 0; i5 < 104; i5++) {
				for (int i7 = 0; i7 < 104; i7++)
					spawnGroundItem(i5, i7);

			}

			anInt1051++;
			if (anInt1051 > 98) {
				anInt1051 = 0;
				stream.createFrame(150);
			}
			method63();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		ObjectDefinition.mruNodes1.unlinkAll();
		if (super.gameFrame != null) {
			stream.createFrame(210);
			stream.writeInt(0x3f008edd);
		}
		// if (lowMem && Signlink.cache_dat != null) {
		// int j = onDemandFetcher.getModelCount();
		// for (int i1 = 0; i1 < j; i1++) {
		// int l1 = onDemandFetcher.getModelIndex(i1);
		// if ((l1 & 0x79) == 0)
		// Model.method461(i1);
		// }
		//
		// }
		System.gc();
		Rasterizer.method367();
		onDemandFetcher.method566();
		int k = (anInt1069 - 6) / 8 - 1;
		int j1 = (anInt1069 + 6) / 8 + 1;
		int i2 = (anInt1070 - 6) / 8 - 1;
		int l2 = (anInt1070 + 6) / 8 + 1;
		if (aBoolean1141) {
			k = 49;
			j1 = 50;
			i2 = 49;
			l2 = 50;
		}
		for (int l3 = k; l3 <= j1; l3++) {
			for (int j5 = i2; j5 <= l2; j5++)
				if (l3 == k || l3 == j1 || j5 == i2 || j5 == l2) {
					int j7 = onDemandFetcher.method562(0, j5, l3);
					if (j7 != -1)
						onDemandFetcher.method560(j7, 3);
					int k8 = onDemandFetcher.method562(1, j5, l3);
					if (k8 != -1)
						onDemandFetcher.method560(k8, 3);
				}

		}

	}

	private void unlinkMRUNodes() {
		ObjectDefinition.mruNodes1.unlinkAll();
		ObjectDefinition.mruNodes2.unlinkAll();
		NPCDefinition.mruNodes.unlinkAll();
		ItemDefinition.mruNodes2.unlinkAll();
		ItemDefinition.mruNodes1.unlinkAll();
		Player.mruNodes.unlinkAll();
		SpotAnim.aMRUNodes_415.unlinkAll();
	}

	private void method24(int i) {
		int ai[] = minimapImage.myPixels;
		int j = ai.length;
		for (int k = 0; k < j; k++)
			ai[k] = 0;

		for (int l = 1; l < 103; l++) {
			int i1 = 24628 + (103 - l) * 512 * 4;
			for (int k1 = 1; k1 < 103; k1++) {
				if ((byteGroundArray[i][k1][l] & 0x18) == 0)
					worldController.method309(ai, i1, i, k1, l);
				if (i < 3 && (byteGroundArray[i + 1][k1][l] & 8) != 0)
					worldController.method309(ai, i1, i + 1, k1, l);
				i1 += 4;
			}

		}

		int j1 = Color.WHITE.getRGB();
		int l1 = Color.RED.getRGB();
		
		if (loggedIn)
			minimapImage.method343();
		
		for (int i2 = 1; i2 < 103; i2++) {
			for (int j2 = 1; j2 < 103; j2++) {
				if ((byteGroundArray[i][j2][i2] & 0x18) == 0)
					method50(i2, j1, j2, l1, i);
				if (i < 3 && (byteGroundArray[i + 1][j2][i2] & 8) != 0)
					method50(i2, j1, j2, l1, i + 1);
			}

		}

		aRSImageProducer_1165.initDrawingArea();
		anInt1071 = 0;
		for (int k2 = 0; k2 < 104; k2++) {
			for (int l2 = 0; l2 < 104; l2++) {
				int i3 = worldController.method303(plane, k2, l2);
				if (i3 != 0) {
					i3 = i3 >> 14 & 0x7fff;
					int j3 = ObjectDefinition.forID(i3).anInt746;
					if (j3 >= 0) {
						int k3 = k2;
						int l3 = l2;
						aClass30_Sub2_Sub1_Sub1Array1140[anInt1071] = mapFunctions[j3];
						anIntArray1072[anInt1071] = k3;
						anIntArray1073[anInt1071] = l3;
						anInt1071++;
					}
				}
			}

		}

	}

	private void spawnGroundItem(int i, int j) {
		NodeList class19 = groundArray[plane][i][j];
		if (class19 == null) {
			worldController.method295(plane, i, j);
			return;
		}
		int k = 0xfa0a1f01;
		Object obj = null;
		for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext()) {
			ItemDefinition itemDef = ItemDefinition.forID(item.ID);
			int l = itemDef.value;
			if (itemDef.stackable)
				l *= item.anInt1559 + 1;
			// notifyItemSpawn(item, i + baseX, j + baseY);

			if (l > k) {
				k = l;
				obj = item;
			}
		}

		class19.insertTail(((Node) (obj)));
		Object obj1 = null;
		Object obj2 = null;
		for (Item class30_sub2_sub4_sub2_1 = (Item) class19.reverseGetFirst(); class30_sub2_sub4_sub2_1 != null; class30_sub2_sub4_sub2_1 = (Item) class19.reverseGetNext()) {
			if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID && obj1 == null)
				obj1 = class30_sub2_sub4_sub2_1;
			if (class30_sub2_sub4_sub2_1.ID != ((Item) (obj)).ID && class30_sub2_sub4_sub2_1.ID != ((Item) (obj1)).ID && obj2 == null)
				obj2 = class30_sub2_sub4_sub2_1;
		}

		int i1 = i + (j << 7) + 0x60000000;
		worldController.method281(i, i1, ((Animable) (obj1)), method42(plane, j * 128 + 64, i * 128 + 64), ((Animable) (obj2)), ((Animable) (obj)), plane, j);
	}

	private void method26(boolean flag) {
		for (int j = 0; j < npcCount; j++) {
			NPC npc = npcArray[npcIndices[j]];
			int k = 0x20000000 + (npcIndices[j] << 14);
			if (npc == null || !npc.isVisible() || npc.desc.aBoolean93 != flag)
				continue;
			int l = npc.x >> 7;
			int i1 = npc.y >> 7;
			if (l < 0 || l >= 104 || i1 < 0 || i1 >= 104)
				continue;
			if (npc.anInt1540 == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
				if (anIntArrayArray929[l][i1] == anInt1265)
					continue;
				anIntArrayArray929[l][i1] = anInt1265;
			}
			if (!npc.desc.aBoolean84)
				k += 0x80000000;
			worldController.method285(plane, npc.anInt1552, method42(plane, npc.y, npc.x), k, npc.y, (npc.anInt1540 - 1) * 64 + 60, npc.x, npc, npc.aBoolean1541);
		}
	}

	public static final NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

	public void drawHoverBox(RSInterface widget, int xPos, int yPos, String text) {
		if (text.contains("%")) {

			do {
				int index = text.indexOf("%1");

				if (index == -1) {
					break;
				}

				text = text.substring(0, index) + numberFormat.format(extractInterfaceValues(widget, 0)) + text.substring(index + 2);
			} while (true);

			do {
				int index = text.indexOf("%2");

				if (index == -1) {
					break;
				}

				text = text.substring(0, index) + numberFormat.format(extractInterfaceValues(widget, 1)) + text.substring(index + 2);
			} while (true);

			do {
				int index = text.indexOf("%3");

				if (index == -1) {
					break;
				}

				text = text.substring(0, index) + numberFormat.format(extractInterfaceValues(widget, 2)) + text.substring(index + 2);
			} while (true);

			do {
				int index = text.indexOf("%4");

				if (index == -1) {
					break;
				}

				text = text.substring(0, index) + numberFormat.format(extractInterfaceValues(widget, 3)) + text.substring(index + 2);
			} while (true);

			do {
				int index = text.indexOf("%5");

				if (index == -1) {
					break;
				}

				int value = extractInterfaceValues(widget, 4);

				text = text.substring(0, index) + numberFormat.format(value) + text.substring(index + 2);
			} while (true);

			do {
				int index = text.indexOf("%6");

				if (index == -1) {
					break;
				}

				text = text.substring(0, index) + numberFormat.format(extractInterfaceValues(widget, 5)) + text.substring(index + 2);
			} while (true);
		}

		String[] results = text.split("\n");
		int height = (results.length * 12) + 8;
		int width = smallText.getTextWidth(results[0]) + 6;

		for (int i = 1; i < results.length; i++) {
			if (width <= (smallText.getTextWidth(results[i]) + 6)) {
				width = smallText.getTextWidth(results[i]) + 6;
			}
		}

		int skillId = -1;

		for (int i = 0; i < SkillConstants.MORE_DETAILS_PANEL_ID.length; i++) {
			if (SkillConstants.MORE_DETAILS_PANEL_ID[i] == widget.id) {
				skillId = i;
				break;
			}
		}

		boolean skillHover = skillId != -1;

		if (skillHover) {
			int[] skillID = { 0, 0, 0, 1, 0, 0, 0, 2, 2, 1, 2, 2, 1, 2, 2, 1, 1, 1, 1, 2, 0, 0, 1, 2 };
			int[] skillID2 = { 1, 3, 2, 1, 4, 5, 6, 4, 6, 6, 3, 5, 5, 2, 1, 3, 2, 4, 7, 7, 7, 8, 8, 8 };

			xPos = (62 * skillID[skillId]) + 36 + (skillID[skillId] == 2 ? 20 : -10);
			yPos = (28 * (skillID2[skillId] >= 6 ? skillID2[skillId] - 3 : skillID2[skillId])) + 45 + (skillID2[skillId] >= 6 ? -4 : 0);

			if ((xPos + width) > 218) {
				xPos = 218 - width;
			}

			if (xPos < 56) {
				xPos = 56;
			}

			if ((statsSkillGoal[skillId][0] != -1) && (skillID2[skillId] >= 6)) {
				yPos -= 14 * 3;
			}

			if (frameMode != ScreenMode.FIXED) {
				xPos += (frameWidth - 766) + widget.width + (frameWidth > 1000 ? 33 : 0);
				yPos += ((frameHeight - 526) + widget.height) - 148 - (frameWidth > 1000 ? 0 : 36);
			}
		}

		width += skillHover ? 4 : 0;
		height += skillHover && (statsSkillGoal[skillId][0] != -1) ? 14 : 0;

		DrawingArea.drawPixels(height - 2, yPos, xPos, 0xFFFFA0, width);

		if (skillHover && (statsSkillGoal[skillId][0] != -1)) {
			int goal = statsSkillGoal[skillId][1] == 1 ? statsSkillGoal[skillId][0] : EXPERIENCE_BY_LEVEL[statsSkillGoal[skillId][0] - 2];
			int init = statsSkillGoal[skillId][2];
			double percentage = ((statsExperience[skillId] - init) / (double) (goal - init));
			if (percentage > 1) {
				percentage = 1;
			}
			for (int i = 0; i < 8; i++) {
				int col = ((0x88 + i) << 12) | ((0x88 + i) << 8);
				DrawingArea.drawPixels(10 - i, (yPos + height) - 16, xPos + 5, col << 8, width - 10);
				DrawingArea.drawPixels(10 - i, (yPos + height) - 16, xPos + 5, col, (int) ((width - 10) * percentage));
			}
			DrawingArea.fillPixels(xPos + 4, width - 8, 12, 0, (yPos + height) - 17);

			newSmallFont.drawCenteredString((int) (percentage * 100) + "%", xPos + 3 + (width / 2), (yPos + height) - 6, 0, -1);
		}
		DrawingArea.fillPixels(xPos, width, height - 2, 0, yPos);
		yPos += 14;

		for (int i = 0; i < results.length; i++) {
			if ((i > 0) && skillHover && results[i].contains(":")) {
				String pre = results[i].split(":")[0];
				String post = results[i].split(":")[1];
				newSmallFont.drawBasicString(pre + ":", xPos + 3, yPos, 0, -1);
				newSmallFont.drawBasicString(post, (xPos + width) - 4 - newSmallFont.getTextWidth(post), yPos, 0, -1);
			} else {
				newSmallFont.drawBasicString(results[i], xPos + 3, yPos, 0, -1);
			}
			yPos += 12;
		}
	}

	private void buildInterfaceMenu(int i, RSInterface class9, int k, int l, int i1, int j1) {
        if(class9.id==3917)return;
		if (class9 == null)
			class9 = RSInterface.interfaceCache[21356];
		if (class9.type != 0 || class9.children == null || class9.isMouseoverTriggered)
			return;
		if (k < i || i1 < l || k > i + class9.width || i1 > l + class9.height)
			return;
		int k1 = class9.children.length;
		for (int l1 = 0; l1 < k1; l1++) {
			int i2 = class9.childX[l1] + i;
			int j2 = (class9.childY[l1] + l) - j1;
			RSInterface children = RSInterface.interfaceCache[class9.children[l1]];
			i2 += children.xOffset;
			j2 += children.yOffset;
			if ((children.hoverType >= 0 || children.textColorSelected != 0) && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height)
				if (children.hoverType >= 0)
					anInt886 = children.hoverType;
				else
					anInt886 = children.id;
			if (children.type == 8 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
				anInt1315 = children.id;
			}
			if (children.type == 0) {
				buildInterfaceMenu(i2, children, k, j2, i1, children.scrollPosition);
				if (children.scrollMax > children.height)
					method65(i2 + children.width, children.height, k, i1, children, j2, true, children.scrollMax);
			} else {
				if (children.atActionType == 1 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
					boolean flag = false, flag1 = false;
					if (children.contentType != 0)
						flag = buildFriendsListMenu(children);

					if ((children.tooltip != null) && children.tooltip.startsWith("[CC]")) {
						flag1 = true;
						clanName = RSInterface.interfaceCache[children.id - 800].message;
					}
					if (!flag && !flag1) {
						menuActionName[menuActionRow] = children.tooltip;
						menuActionID[menuActionRow] = 315;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
					}
					if (flag1 && (RSInterface.interfaceCache[children.id - 800].getText() != "")) {
						menuActionName[menuActionRow] = "General";
						menuActionID[menuActionRow] = 1321;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
						menuActionName[menuActionRow] = "Captain";
						menuActionID[menuActionRow] = 1320;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
						menuActionName[menuActionRow] = "Lieutenant";
						menuActionID[menuActionRow] = 1319;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
						menuActionName[menuActionRow] = "Sergeant";
						menuActionID[menuActionRow] = 1318;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
						menuActionName[menuActionRow] = "Corporal";
						menuActionID[menuActionRow] = 1317;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
						menuActionName[menuActionRow] = "Recruit";
						menuActionID[menuActionRow] = 1316;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
						menuActionName[menuActionRow] = "Not ranked";
						menuActionID[menuActionRow] = 1315;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
					}
				}
				if (children.atActionType == 2 && spellSelected == 0 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
					String s = children.selectedActionName;
					if (s.indexOf(" ") != -1)
						s = s.substring(0, s.indexOf(" "));
					if (children.spellName.endsWith("Rush") || children.spellName.endsWith("Burst") || children.spellName.endsWith("Blitz") || children.spellName.endsWith("Barrage")
							|| children.spellName.endsWith("strike") || children.spellName.endsWith("bolt") || children.spellName.equals("Crumble undead") || children.spellName.endsWith("blast")
							|| children.spellName.endsWith("wave") || children.spellName.equals("Claws of Guthix") || children.spellName.equals("Flames of Zamorak")
							|| children.spellName.equals("Magic Dart")) {
						menuActionName[menuActionRow] = "Autocast @gre@" + children.spellName;
						menuActionID[menuActionRow] = 104;
						menuActionCmd3[menuActionRow] = children.id;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = s + " @gre@" + children.spellName;
					menuActionID[menuActionRow] = 626;
					menuActionCmd3[menuActionRow] = children.id;
					menuActionRow++;
				}
				if (children.atActionType == 3 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
					menuActionName[menuActionRow] = "Close";
					menuActionID[menuActionRow] = 200;
					menuActionCmd3[menuActionRow] = children.id;
					menuActionRow++;
				}
				if (children.atActionType == 4 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
					// System.out.println("2"+class9_1.tooltip + ", " +
					// class9_1.interfaceID);
					// menuActionName[menuActionRow] = class9_1.tooltip + ", " +
					// class9_1.id;
					menuActionName[menuActionRow] = children.tooltip;
					menuActionID[menuActionRow] = 169;
					menuActionCmd3[menuActionRow] = children.id;
					menuActionRow++;
					if (children.hoverText != null) {
						// drawHoverBox(k, l, class9_1.hoverText);
						// System.out.println("DRAWING INTERFACE: " +
						// class9_1.hoverText);
					}
				}
				if (children.atActionType == 5 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
					// System.out.println("3"+class9_1.tooltip + ", " +
					// class9_1.interfaceID);
					// menuActionName[menuActionRow] = class9_1.tooltip + ", " +
					// class9_1.id;
					menuActionName[menuActionRow] = children.tooltip;
					menuActionID[menuActionRow] = 646;
					menuActionCmd3[menuActionRow] = children.id;
					menuActionRow++;
				}
				if (children.atActionType == 6 && !aBoolean1149 && k >= i2 && i1 >= j2 && k < i2 + children.width && i1 < j2 + children.height) {
					// System.out.println("4"+class9_1.tooltip + ", " +
					// class9_1.interfaceID);
					// menuActionName[menuActionRow] = class9_1.tooltip + ", " +
					// class9_1.id;
					menuActionName[menuActionRow] = children.tooltip;
					menuActionID[menuActionRow] = 679;
					menuActionCmd3[menuActionRow] = children.id;
					menuActionRow++;
				}
				if (children.type == 2) {
					int k2 = 0;
					for (int l2 = 0; l2 < children.height; l2++) {
						for (int i3 = 0; i3 < children.width; i3++) {
							int j3 = i2 + i3 * (32 + children.invSpritePadX);
							int k3 = j2 + l2 * (32 + children.invSpritePadY);
							if (k2 < 20) {
								j3 += children.spritesX[k2];
								k3 += children.spritesY[k2];
							}
							if (k >= j3 && i1 >= k3 && k < j3 + 32 && i1 < k3 + 32) {
								mouseInvInterfaceIndex = k2;
								lastActiveInvInterface = children.id;
                                arpgHoverWidget=children.id;arpgHoverSlot=k2;
								if (children.container[k2] > 0) {
									ItemDefinition itemDef = ItemDefinition.forID(children.container[k2] - 1);
									if (itemSelected == 1 && children.isInventoryInterface) {
										if (children.id != anInt1284 || k2 != anInt1283) {
											menuActionName[menuActionRow] = "Use " + selectedItemName + " with @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 870;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = children.id;
											menuActionRow++;
										}
									} else if (spellSelected == 1 && children.isInventoryInterface) {
										if ((spellUsableOn & 0x10) == 16) {
											menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 543;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = children.id;
											menuActionRow++;
										}
									} else {
										if (children.isInventoryInterface) {
											for (int l3 = 4; l3 >= 3; l3--)
												if (itemDef.itemActions != null && itemDef.itemActions[l3] != null) {
													menuActionName[menuActionRow] = itemDef.itemActions[l3] + " @lre@" + itemDef.name + " "
															+ (clientData ? ("id: " + itemDef.id + ", modelId: " + itemDef.modelID) : "");
													if (l3 == 3)
														menuActionID[menuActionRow] = 493;
													if (l3 == 4)
														menuActionID[menuActionRow] = 847;
													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = children.id;
													menuActionRow++;
												} else if (l3 == 4) {
													menuActionName[menuActionRow] = "Drop @lre@" + itemDef.name;
													menuActionID[menuActionRow] = 847;
													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = children.id;
													menuActionRow++;
												}
										}
										if (children.usableItemInterface) {
											menuActionName[menuActionRow] = "Use @lre@" + itemDef.name;
											menuActionID[menuActionRow] = 447;
											menuActionCmd1[menuActionRow] = itemDef.id;
											menuActionCmd2[menuActionRow] = k2;
											menuActionCmd3[menuActionRow] = children.id;
											menuActionRow++;
										}
										if (children.isInventoryInterface && itemDef.itemActions != null) {
											for (int i4 = 2; i4 >= 0; i4--)
												if (itemDef.itemActions[i4] != null) {
													menuActionName[menuActionRow] = itemDef.itemActions[i4] + " @lre@" + itemDef.name;
													if (i4 == 0)
														menuActionID[menuActionRow] = 74;
													if (i4 == 1)
														menuActionID[menuActionRow] = 454;
													if (i4 == 2)
														menuActionID[menuActionRow] = 539;
													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = children.id;
													menuActionRow++;
												}

										}
										if (children.actions != null) {
											boolean childIsBank = (children.parentID >= RSInterface.BANK_CONTAINER) && (children.parentID <= (RSInterface.BANK_CONTAINER + 8));
											for (int actionIndex = childIsBank ? 6 : 4; actionIndex >= 0; actionIndex--) {
												if (childIsBank) {
													if (modifiableXValue > 0) {
														children.actions[5] = "Withdraw-" + modifiableXValue;
													}
												}
												if (children.actions[actionIndex] != null) {
													menuActionName[menuActionRow] = children.actions[actionIndex] + " @lre@" + itemDef.name;
													if (actionIndex == 0)
														menuActionID[menuActionRow] = 632;
													if (actionIndex == 1)
														menuActionID[menuActionRow] = 78;
													if (actionIndex == 2)
														menuActionID[menuActionRow] = 867;
													if (actionIndex == 3)
														menuActionID[menuActionRow] = 431;
													if (actionIndex == 4)
														menuActionID[menuActionRow] = 53;

													if (childIsBank) {
														if (actionIndex == 5) {
															menuActionID[menuActionRow] = 300;
														}
														if (actionIndex == 6) {
															menuActionID[menuActionRow] = 291;
														}
													}

													menuActionCmd1[menuActionRow] = itemDef.id;
													menuActionCmd2[menuActionRow] = k2;
													menuActionCmd3[menuActionRow] = children.id;
													menuActionRow++;
												}
											}

										}
										menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;
										menuActionID[menuActionRow] = 1125;
										menuActionCmd1[menuActionRow] = itemDef.id;
										menuActionCmd2[menuActionRow] = k2;
										menuActionCmd3[menuActionRow] = children.id;
										menuActionRow++;
									}
								}
							}
							k2++;
						}
					}
				}
			}
		}
	}

	public void drawTransparentScrollBar(int x, int y, int height, int maxScroll, int pos) {
		cacheSprite[29].drawARGBSprite(x, y, 120);
		cacheSprite[30].drawARGBSprite(x, y + height - 16, 120);
		DrawingArea.drawVerticalLine(x, y + 16, height - 32, 0xffffff, 64);
		DrawingArea.drawVerticalLine(x + 15, y + 16, height - 32, 0xffffff, 64);
		int barHeight = (height - 32) * height / maxScroll;
		if (barHeight < 10) {
			barHeight = 10;
		}
		int barPos = 0;
		if (maxScroll != height) {
			barPos = (height - 32 - barHeight) * pos / (maxScroll - height);
		}
		DrawingArea.drawRectangle(x, y + 16 + barPos, 16, 5 + y + 16 + barPos + barHeight - 5 - (y + 16 + barPos), 0xffffff, 32);
	}

	public void drawScrollbar(int height, int pos, int y, int x, int maxScroll, boolean transparent) {
		if (transparent) {
			drawTransparentScrollBar(x, y, height, maxScroll, pos);
		} else {
			scrollBar1.drawSprite(x, y);
			scrollBar2.drawSprite(x, (y + height) - 16);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x000001, 16);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x3d3426, 15);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x342d21, 13);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x2e281d, 11);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x29241b, 10);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x252019, 9);
			DrawingArea.drawPixels(height - 32, y + 16, x, 0x000001, 1);
			int k1 = ((height - 32) * height) / maxScroll;
			if (k1 < 8) {
				k1 = 8;
			}
			int l1 = ((height - 32 - k1) * pos) / (maxScroll - height);
			DrawingArea.drawPixels(k1, y + 16 + l1, x, barFillColor, 16);
			DrawingArea.method341(y + 16 + l1, 0x000001, k1, x);
			DrawingArea.method341(y + 16 + l1, 0x817051, k1, x + 1);
			DrawingArea.method341(y + 16 + l1, 0x73654a, k1, x + 2);
			DrawingArea.method341(y + 16 + l1, 0x6a5c43, k1, x + 3);
			DrawingArea.method341(y + 16 + l1, 0x6a5c43, k1, x + 4);
			DrawingArea.method341(y + 16 + l1, 0x655841, k1, x + 5);
			DrawingArea.method341(y + 16 + l1, 0x655841, k1, x + 6);
			DrawingArea.method341(y + 16 + l1, 0x61553e, k1, x + 7);
			DrawingArea.method341(y + 16 + l1, 0x61553e, k1, x + 8);
			DrawingArea.method341(y + 16 + l1, 0x5d513c, k1, x + 9);
			DrawingArea.method341(y + 16 + l1, 0x5d513c, k1, x + 10);
			DrawingArea.method341(y + 16 + l1, 0x594e3a, k1, x + 11);
			DrawingArea.method341(y + 16 + l1, 0x594e3a, k1, x + 12);
			DrawingArea.method341(y + 16 + l1, 0x514635, k1, x + 13);
			DrawingArea.method341(y + 16 + l1, 0x4b4131, k1, x + 14);
			DrawingArea.method339(y + 16 + l1, 0x000001, 15, x);
			DrawingArea.method339(y + 17 + l1, 0x000001, 15, x);
			DrawingArea.method339(y + 17 + l1, 0x655841, 14, x);
			DrawingArea.method339(y + 17 + l1, 0x6a5c43, 13, x);
			DrawingArea.method339(y + 17 + l1, 0x6d5f48, 11, x);
			DrawingArea.method339(y + 17 + l1, 0x73654a, 10, x);
			DrawingArea.method339(y + 17 + l1, 0x76684b, 7, x);
			DrawingArea.method339(y + 17 + l1, 0x7b6a4d, 5, x);
			DrawingArea.method339(y + 17 + l1, 0x7e6e50, 4, x);
			DrawingArea.method339(y + 17 + l1, 0x817051, 3, x);
			DrawingArea.method339(y + 17 + l1, 0x000001, 2, x);
			DrawingArea.method339(y + 18 + l1, 0x000001, 16, x);
			DrawingArea.method339(y + 18 + l1, 0x564b38, 15, x);
			DrawingArea.method339(y + 18 + l1, 0x5d513c, 14, x);
			DrawingArea.method339(y + 18 + l1, 0x625640, 11, x);
			DrawingArea.method339(y + 18 + l1, 0x655841, 10, x);
			DrawingArea.method339(y + 18 + l1, 0x6a5c43, 7, x);
			DrawingArea.method339(y + 18 + l1, 0x6e6046, 5, x);
			DrawingArea.method339(y + 18 + l1, 0x716247, 4, x);
			DrawingArea.method339(y + 18 + l1, 0x7b6a4d, 3, x);
			DrawingArea.method339(y + 18 + l1, 0x817051, 2, x);
			DrawingArea.method339(y + 18 + l1, 0x000001, 1, x);
			DrawingArea.method339(y + 19 + l1, 0x000001, 16, x);
			DrawingArea.method339(y + 19 + l1, 0x514635, 15, x);
			DrawingArea.method339(y + 19 + l1, 0x564b38, 14, x);
			DrawingArea.method339(y + 19 + l1, 0x5d513c, 11, x);
			DrawingArea.method339(y + 19 + l1, 0x61553e, 9, x);
			DrawingArea.method339(y + 19 + l1, 0x655841, 7, x);
			DrawingArea.method339(y + 19 + l1, 0x6a5c43, 5, x);
			DrawingArea.method339(y + 19 + l1, 0x6e6046, 4, x);
			DrawingArea.method339(y + 19 + l1, 0x73654a, 3, x);
			DrawingArea.method339(y + 19 + l1, 0x817051, 2, x);
			DrawingArea.method339(y + 19 + l1, 0x000001, 1, x);
			DrawingArea.method339(y + 20 + l1, 0x000001, 16, x);
			DrawingArea.method339(y + 20 + l1, 0x4b4131, 15, x);
			DrawingArea.method339(y + 20 + l1, 0x544936, 14, x);
			DrawingArea.method339(y + 20 + l1, 0x594e3a, 13, x);
			DrawingArea.method339(y + 20 + l1, 0x5d513c, 10, x);
			DrawingArea.method339(y + 20 + l1, 0x61553e, 8, x);
			DrawingArea.method339(y + 20 + l1, 0x655841, 6, x);
			DrawingArea.method339(y + 20 + l1, 0x6a5c43, 4, x);
			DrawingArea.method339(y + 20 + l1, 0x73654a, 3, x);
			DrawingArea.method339(y + 20 + l1, 0x817051, 2, x);
			DrawingArea.method339(y + 20 + l1, 0x000001, 1, x);
			DrawingArea.method341(y + 16 + l1, 0x000001, k1, x + 15);
			DrawingArea.method339(y + 15 + l1 + k1, 0x000001, 16, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x000001, 15, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x3f372a, 14, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x443c2d, 10, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x483e2f, 9, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x4a402f, 7, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x4b4131, 4, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x564b38, 3, x);
			DrawingArea.method339(y + 14 + l1 + k1, 0x000001, 2, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x000001, 16, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x443c2d, 15, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x4b4131, 11, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x514635, 9, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x544936, 7, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x564b38, 6, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x594e3a, 4, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x625640, 3, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x6a5c43, 2, x);
			DrawingArea.method339(y + 13 + l1 + k1, 0x000001, 1, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x000001, 16, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x443c2d, 15, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x4b4131, 14, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x544936, 12, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x564b38, 11, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x594e3a, 10, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x5d513c, 7, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x61553e, 4, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x6e6046, 3, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x7b6a4d, 2, x);
			DrawingArea.method339(y + 12 + l1 + k1, 0x000001, 1, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x000001, 16, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x4b4131, 15, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x514635, 14, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x564b38, 13, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x594e3a, 11, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x5d513c, 9, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x61553e, 7, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x655841, 5, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x6a5c43, 4, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x73654a, 3, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x7b6a4d, 2, x);
			DrawingArea.method339(y + 11 + l1 + k1, 0x000001, 1, x);
		}
	}

	private void updateNPCs(Stream stream, int i) {
		anInt839 = 0;
		anInt893 = 0;
		updateNpcMovement(stream);
		method46(i, stream);
		method86(stream);
		for (int k = 0; k < anInt839; k++) {
			int l = anIntArray840[k];
			if (npcArray[l].anInt1537 != loopCycle) {
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		if (stream.currentOffset != i) {
			Signlink.reporterror(myUsername + " size mismatch in getnpcpos - pos:" + stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < npcCount; i1++)
			if (npcArray[npcIndices[i1]] == null) {
				Signlink.reporterror(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
				throw new RuntimeException("eek");
			}

	}

	private int cButtonHPos;
	private int cButtonCPos;
	private int setChannel;

	public void processChatModeClick() {
		final int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 503;
		if (super.mouseX >= 5 && super.mouseX <= 61 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 0;
			inputTaken = true;
		} else if (super.mouseX >= 71 && super.mouseX <= 127 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 1;
			inputTaken = true;
		} else if (super.mouseX >= 137 && super.mouseX <= 193 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 2;
			inputTaken = true;
		} else if (super.mouseX >= 203 && super.mouseX <= 259 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 3;
			inputTaken = true;
		} else if (super.mouseX >= 269 && super.mouseX <= 325 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 4;
			inputTaken = true;
		} else if (super.mouseX >= 335 && super.mouseX <= 391 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 5;
			inputTaken = true;
		} else if (super.mouseX >= 404 && super.mouseX <= 515 && super.mouseY >= yOffset + 482 && super.mouseY <= yOffset + 503) {
			cButtonHPos = 6;
			inputTaken = true;
		} else {
			cButtonHPos = -1;
			inputTaken = true;
		}
		if (super.clickMode3 == 1) {
			if (super.saveClickX >= 5 && super.saveClickX <= 61 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (frameMode != ScreenMode.FIXED) {
					if (setChannel != 0) {
						cButtonCPos = 0;
						chatTypeView = 0;
						inputTaken = true;
						setChannel = 0;
					} else {
						showChatComponents = showChatComponents ? false : true;
					}
				} else {
					cButtonCPos = 0;
					chatTypeView = 0;
					inputTaken = true;
					setChannel = 0;
				}
			} else if (super.saveClickX >= 71 && super.saveClickX <= 127 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (frameMode != ScreenMode.FIXED) {
					if (setChannel != 1 && frameMode != ScreenMode.FIXED) {
						cButtonCPos = 1;
						chatTypeView = 5;
						inputTaken = true;
						setChannel = 1;
					} else {
						showChatComponents = showChatComponents ? false : true;
					}
				} else {
					cButtonCPos = 1;
					chatTypeView = 5;
					inputTaken = true;
					setChannel = 1;
				}
			} else if (super.saveClickX >= 137 && super.saveClickX <= 193 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (frameMode != ScreenMode.FIXED) {
					if (setChannel != 2 && frameMode != ScreenMode.FIXED) {
						cButtonCPos = 2;
						chatTypeView = 1;
						inputTaken = true;
						setChannel = 2;
					} else {
						showChatComponents = showChatComponents ? false : true;
					}
				} else {
					cButtonCPos = 2;
					chatTypeView = 1;
					inputTaken = true;
					setChannel = 2;
				}
			} else if (super.saveClickX >= 203 && super.saveClickX <= 259 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (frameMode != ScreenMode.FIXED) {
					if (setChannel != 3 && frameMode != ScreenMode.FIXED) {
						cButtonCPos = 3;
						chatTypeView = 2;
						inputTaken = true;
						setChannel = 3;
					} else {
						showChatComponents = showChatComponents ? false : true;
					}
				} else {
					cButtonCPos = 3;
					chatTypeView = 2;
					inputTaken = true;
					setChannel = 3;
				}
			} else if (super.saveClickX >= 269 && super.saveClickX <= 325 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (frameMode != ScreenMode.FIXED) {
					if (setChannel != 4 && frameMode != ScreenMode.FIXED) {
						cButtonCPos = 4;
						chatTypeView = 11;
						inputTaken = true;
						setChannel = 4;
					} else {
						showChatComponents = showChatComponents ? false : true;
					}
				} else {
					cButtonCPos = 4;
					chatTypeView = 11;
					inputTaken = true;
					setChannel = 4;
				}
			} else if (super.saveClickX >= 335 && super.saveClickX <= 391 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (frameMode != ScreenMode.FIXED) {
					if (setChannel != 5 && frameMode != ScreenMode.FIXED) {
						cButtonCPos = 5;
						chatTypeView = 3;
						inputTaken = true;
						setChannel = 5;
					} else {
						showChatComponents = showChatComponents ? false : true;
					}
				} else {
					cButtonCPos = 5;
					chatTypeView = 3;
					inputTaken = true;
					setChannel = 5;
				}
			} else if (super.saveClickX >= 404 && super.saveClickX <= 515 && super.saveClickY >= yOffset + 482 && super.saveClickY <= yOffset + 505) {
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					reportAbuseInput = "";
					canMute = false;
					for (int i = 0; i < RSInterface.interfaceCache.length; i++) {
						if (RSInterface.interfaceCache[i] == null || RSInterface.interfaceCache[i].contentType != 600) {
							continue;
						}
						reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i].parentID;
						break;
					}
				} else {
					pushMessage("Please close the interface you have open before using 'report abuse'", 0, "");
				}
			}
		}
	}

	private void method33(int i) {
		try {
			int j = Varp.cache[i].anInt709;
			if (j == 0)
				return;
			int k = variousSettings[i];

			if (j == 1) {
				if (k == 1)
					Rasterizer.method372(0.90000000000000002D);
				if (k == 2)
					Rasterizer.method372(0.80000000000000004D);
				if (k == 3)
					Rasterizer.method372(0.69999999999999996D);
				if (k == 4)
					Rasterizer.method372(0.59999999999999998D);
				ItemDefinition.mruNodes1.unlinkAll();
				welcomeScreenRaised = true;
			} else if (j == 3) {
				int volume = 0;

				if (k == 0) {
					volume = 255;
				} else if (k == 1) {
					volume = 192;
				} else if (k == 2) {
					volume = 128;
				} else if (k == 3) {
					volume = 64;
				} else if (k == 4) {
					volume = 0;
				}

				if (volume != musicVolume) {
					if (musicVolume != 0 || currentSong == -1) {
						if (volume != 0) {
							setVolume(volume);
						} else {
							method55(false);
							prevSong = 0;
						}
					} else {
						method56(volume, false, currentSong);
						prevSong = 0;
					}

					musicVolume = volume;
				}
			} else if (j == 4) {
				if (k == 0) {
					soundEffectVolume = 127;
				} else if (k == 1) {
					soundEffectVolume = 96;
				} else if (k == 2) {
					soundEffectVolume = 64;
				} else if (k == 3) {
					soundEffectVolume = 32;
				} else if (k == 4) {
					soundEffectVolume = 0;
				}
			}
			if (j == 5)
				anInt1253 = k;
			if (j == 6)
				anInt1249 = k;
			if (j == 8) {
				splitPrivateChat = k;
				inputTaken = true;
			}
			if (j == 9)
				anInt913 = k;
		} catch (Exception e) {
		}
	}

	public StreamLoader mediaStreamLoader;

	private final int[] hitmarks562 = { 31, 32, 33, 34 };

	public void updateEntities() {
		try {
			int anInt974 = 0;
			for (int j = -1; j < playerCount + npcCount; j++) {
				Object obj;
				if (j == -1)
					obj = myPlayer;
				else if (j < playerCount)
					obj = playerArray[playerIndices[j]];
				else
					obj = npcArray[npcIndices[j - playerCount]];
				if (obj == null || !((Entity) (obj)).isVisible())
					continue;
				if (obj instanceof NPC) {
					NPCDefinition entityDef = ((NPC) obj).desc;
					if (entityDef.childrenIDs != null)
						entityDef = entityDef.method161();
					if (entityDef == null)
						continue;
				}
				if (j < playerCount) {
					int l = 30;
					Player player = (Player) obj;
					if (player.headIcon >= 0) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							if (player.skullIcon < 2) {
								skullIcons[player.skullIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 25;
							}
							if (player.headIcon < 18) {
								headIcons[player.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 18;
							}
						}
					}
					if (j >= 0 && anInt855 == 10 && anInt933 == playerIndices[j]) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[player.hintIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
					}
				} else {
					NPCDefinition entityDef_1 = ((NPC) obj).desc;
					if (entityDef_1.anInt75 >= 0 && entityDef_1.anInt75 < headIcons.length) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIcons[entityDef_1.anInt75].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
					}
					if (anInt855 == 1 && anInt1222 == npcIndices[j - playerCount] && loopCycle % 20 < 10) {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
					}
				}
				if (((Entity) (obj)).textSpoken != null && (j >= playerCount || publicChatMode == 0 || publicChatMode == 3 || publicChatMode == 1 && isFriendOrSelf(((Player) obj).name))) {
					npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height);
					if (spriteDrawX > -1 && anInt974 < anInt975) {
						anIntArray979[anInt974] = chatTextDrawingArea.getRealTextWidth(((Entity) (obj)).textSpoken) / 2;
						anIntArray978[anInt974] = chatTextDrawingArea.anInt1497;
						anIntArray976[anInt974] = spriteDrawX;
						anIntArray977[anInt974] = spriteDrawY;
						anIntArray980[anInt974] = ((Entity) (obj)).anInt1513;
						anIntArray981[anInt974] = ((Entity) (obj)).anInt1531;
						anIntArray982[anInt974] = ((Entity) (obj)).textCycle;
						aStringArray983[anInt974++] = ((Entity) (obj)).textSpoken;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 >= 1 && ((Entity) (obj)).anInt1531 <= 3) {
							anIntArray978[anInt974] += 10;
							anIntArray977[anInt974] += 5;
						}
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 4)
							anIntArray979[anInt974] = 60;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 5)
							anIntArray978[anInt974] += 5;
					}
				}
				if (((Entity) (obj)).loopCycleStatus > loopCycle) {
					try {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1) {
							int i1 = (((Entity) (obj)).currentHealth * 30) / ((Entity) (obj)).maxHealth;
							int hpPercent = (((Entity) (obj)).currentHealth * 56) / ((Entity) (obj)).maxHealth;
							if (hpPercent > 56) {
								hpPercent = 56;
							}
							if (i1 > 30) {
								i1 = 30;
							}
							if (!hpBar554) {
								DrawingArea.drawPixels(5, spriteDrawY - 3, spriteDrawX - 15, 65280, i1);
								DrawingArea.drawPixels(5, spriteDrawY - 3, (spriteDrawX - 15) + i1, 0xff0000, 30 - i1);
							} else {
								cacheSprite[41].drawSprite(spriteDrawX - 28, spriteDrawY - 3);
								cacheSprite[40] = new Sprite(Signlink.findcachedir() + "Sprites/Attack/40.png", hpPercent, 7);
								cacheSprite[40].drawSprite(spriteDrawX - 28, spriteDrawY - 3);
							}
						}
					} catch (Exception e) {
					}
				}
				if (!hitmarks554) {
					for (int j1 = 0; j1 < 4; j1++) {
						if (((Entity) (obj)).hitsLoopCycle[j1] > loopCycle) {
							npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
							if (spriteDrawX > -1) {
								if (j1 == 1)
									spriteDrawY -= 20;
								if (j1 == 2) {
									spriteDrawX -= 15;
									spriteDrawY -= 10;
								}
								if (j1 == 3) {
									spriteDrawX += 15;
									spriteDrawY -= 10;
								}
								hitMarks[((Entity) (obj)).hitMarkTypes[j1]].drawSprite(spriteDrawX - 12, spriteDrawY - 12);
								smallText.drawText(0, String.valueOf(((Entity) (obj)).hitArray[j1]), spriteDrawY + 4, spriteDrawX);
								smallText.drawText(0xffffff, String.valueOf(((Entity) (obj)).hitArray[j1]), spriteDrawY + 3, spriteDrawX - 1);
							}
						}
					}
				} else {
					for (int j2 = 0; j2 < 4; j2++) {
						if (((Entity) (obj)).hitsLoopCycle[j2] > loopCycle) {
							npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
							if (spriteDrawX > -1) {
								if (j2 == 0 && ((Entity) (obj)).hitArray[j2] > 99)
									((Entity) (obj)).hitMarkTypes[j2] = 3;
								else if (j2 == 1 && ((Entity) (obj)).hitArray[j2] > 99)
									((Entity) (obj)).hitMarkTypes[j2] = 3;
								else if (j2 == 2 && ((Entity) (obj)).hitArray[j2] > 99)
									((Entity) (obj)).hitMarkTypes[j2] = 3;
								else if (j2 == 3 && ((Entity) (obj)).hitArray[j2] > 99)
									((Entity) (obj)).hitMarkTypes[j2] = 3;
								if (j2 == 1) {
									spriteDrawY -= 20;
								}
								if (j2 == 2) {
									spriteDrawX -= (((Entity) (obj)).hitArray[j2] > 99 ? 30 : 20);
									spriteDrawY -= 10;
								}
								if (j2 == 3) {
									spriteDrawX += (((Entity) (obj)).hitArray[j2] > 99 ? 30 : 20);
									spriteDrawY -= 10;
								}
								if (((Entity) (obj)).hitMarkTypes[j2] == 3) {
									spriteDrawX -= 8;
								}
								cacheSprite[hitmarks562[((Entity) (obj)).hitMarkTypes[j2]]].draw24BitSprite(spriteDrawX - 12, spriteDrawY - 12);
								smallText.drawText(0xffffff, String.valueOf(((Entity) (obj)).hitArray[j2]), spriteDrawY + 3,
										(((Entity) (obj)).hitMarkTypes[j2] == 3 ? spriteDrawX + 7 : spriteDrawX - 1));
							}
						}
					}
				}
			}
			for (int k = 0; k < anInt974; k++) {
				int k1 = anIntArray976[k];
				int l1 = anIntArray977[k];
				int j2 = anIntArray979[k];
				int k2 = anIntArray978[k];
				boolean flag = true;
				while (flag) {
					flag = false;
					for (int l2 = 0; l2 < k; l2++)
						if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2] && l1 - k2 < anIntArray977[l2] + 2 && k1 - j2 < anIntArray976[l2] + anIntArray979[l2]
								&& k1 + j2 > anIntArray976[l2] - anIntArray979[l2] && anIntArray977[l2] - anIntArray978[l2] < l1) {
							l1 = anIntArray977[l2] - anIntArray978[l2];
							flag = true;
						}

				}
				spriteDrawX = anIntArray976[k];
				spriteDrawY = anIntArray977[k] = l1;
				String s = aStringArray983[k];
				if (anInt1249 == 0) {
					int i3 = 0xffff00;
					if (anIntArray980[k] < 6)
						i3 = anIntArray965[anIntArray980[k]];
					if (anIntArray980[k] == 6)
						i3 = anInt1265 % 20 >= 10 ? 0xffff00 : 0xff0000;
					if (anIntArray980[k] == 7)
						i3 = anInt1265 % 20 >= 10 ? 65535 : 255;
					if (anIntArray980[k] == 8)
						i3 = anInt1265 % 20 >= 10 ? 0x80ff80 : 45056;
					if (anIntArray980[k] == 9) {
						int j3 = 150 - anIntArray982[k];
						if (j3 < 50)
							i3 = 0xff0000 + 1280 * j3;
						else if (j3 < 100)
							i3 = 0xffff00 - 0x50000 * (j3 - 50);
						else if (j3 < 150)
							i3 = 65280 + 5 * (j3 - 100);
					}
					if (anIntArray980[k] == 10) {
						int k3 = 150 - anIntArray982[k];
						if (k3 < 50)
							i3 = 0xff0000 + 5 * k3;
						else if (k3 < 100)
							i3 = 0xff00ff - 0x50000 * (k3 - 50);
						else if (k3 < 150)
							i3 = (255 + 0x50000 * (k3 - 100)) - 5 * (k3 - 100);
					}
					if (anIntArray980[k] == 11) {
						int l3 = 150 - anIntArray982[k];
						if (l3 < 50)
							i3 = 0xffffff - 0x50005 * l3;
						else if (l3 < 100)
							i3 = 65280 + 0x50005 * (l3 - 50);
						else if (l3 < 150)
							i3 = 0xffffff - 0x50000 * (l3 - 100);
					}
					if (anIntArray981[k] == 0) {
						chatTextDrawingArea.drawText(0, s, spriteDrawY + 1, spriteDrawX);
						chatTextDrawingArea.drawText(i3, s, spriteDrawY, spriteDrawX);
					}
					if (anIntArray981[k] == 1) {
						chatTextDrawingArea.method386(0, s, spriteDrawX, anInt1265, spriteDrawY + 1);
						chatTextDrawingArea.method386(i3, s, spriteDrawX, anInt1265, spriteDrawY);
					}
					if (anIntArray981[k] == 2) {
						chatTextDrawingArea.method387(spriteDrawX, s, anInt1265, spriteDrawY + 1, 0);
						chatTextDrawingArea.method387(spriteDrawX, s, anInt1265, spriteDrawY, i3);
					}
					if (anIntArray981[k] == 3) {
						chatTextDrawingArea.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY + 1, spriteDrawX, 0);
						chatTextDrawingArea.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY, spriteDrawX, i3);
					}
					if (anIntArray981[k] == 4) {
						int i4 = chatTextDrawingArea.getRealTextWidth(s);
						int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
						DrawingArea.setDrawingArea(334, spriteDrawX - 50, spriteDrawX + 50, 0);
						chatTextDrawingArea.method385(0, s, spriteDrawY + 1, (spriteDrawX + 50) - k4);
						chatTextDrawingArea.method385(i3, s, spriteDrawY, (spriteDrawX + 50) - k4);
						DrawingArea.defaultDrawingAreaSize();
					}
					if (anIntArray981[k] == 5) {
						int j4 = 150 - anIntArray982[k];
						int l4 = 0;
						if (j4 < 25)
							l4 = j4 - 25;
						else if (j4 > 125)
							l4 = j4 - 125;
						DrawingArea.setDrawingArea(spriteDrawY + 5, 0, 512, spriteDrawY - chatTextDrawingArea.anInt1497 - 1);
						chatTextDrawingArea.drawText(0, s, spriteDrawY + 1 + l4, spriteDrawX);
						chatTextDrawingArea.drawText(i3, s, spriteDrawY + l4, spriteDrawX);
						DrawingArea.defaultDrawingAreaSize();
					}
				} else {
					chatTextDrawingArea.drawText(0, s, spriteDrawY + 1, spriteDrawX);
					chatTextDrawingArea.drawText(0xffff00, s, spriteDrawY, spriteDrawX);
				}
			}
		} catch (Exception e) {
		}
	}

	private void delFriend(long l) {
		try {
			if (l == 0L)
				return;
			for (int i = 0; i < friendsCount; i++) {
				if (friendsListAsLongs[i] != l)
					continue;
				friendsCount--;
				for (int j = i; j < friendsCount; j++) {
					friendsList[j] = friendsList[j + 1];
					friendsNodeIDs[j] = friendsNodeIDs[j + 1];
					friendsListAsLongs[j] = friendsListAsLongs[j + 1];
				}

				stream.createFrame(215);
				stream.writeLong(l);
				break;
			}
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("18622, " + false + ", " + l + ", " + runtimeexception.toString());
			throw new RuntimeException();
		}
	}

	private final int[] sideIconsX = { 17, 49, 83, 114, 146, 180, 214, 16, 49, 82, 116, 148, 184, 216 }, sideIconsY = { 9, 7, 7, 5, 2, 3, 7, 303, 306, 306, 302, 305, 303, 303, 303 },
			sideIconsId = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 }, sideIconsTab = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };

	public void drawSideIcons() {
		int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 247;
		int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 336;
		if (frameMode == ScreenMode.FIXED || (!frameMode.isFixed() && !changeTabArea)) {
			for (int i = 0; i < sideIconsTab.length; i++) {
				if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
					if (sideIconsId[i] != -1) {
						com.koios.client.arpg.SidebarChrome.icon(sideIcons[sideIconsId[i]],sideIconsTab[i]==tabID).drawSprite(sideIconsX[i] + xOffset, sideIconsY[i] + yOffset);
					}
				}
			}
		} else if (changeTabArea && frameWidth < 1000) {
			int[] iconId = { 0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13 };
			int[] iconX = { 219, 189, 156, 126, 93, 62, 30, 219, 189, 156, 124, 92, 59, 28 };
			int[] iconY = { 67, 69, 67, 69, 72, 72, 69, 32, 29, 29, 32, 30, 33, 31, 32 };
			for (int i = 0; i < sideIconsTab.length; i++) {
				if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
					if (iconId[i] != -1) {
						com.koios.client.arpg.SidebarChrome.icon(sideIcons[iconId[i]],sideIconsTab[i]==tabID).drawSprite(frameWidth - iconX[i], frameHeight - iconY[i]);
					}
				}
			}
		} else if (changeTabArea && frameWidth >= 1000) {
			int[] iconId = { 0, 1, 2, 3, 4, 5, 6, -1, 8, 9, 7, 11, 12, 13 };
			int[] iconX = { 50, 80, 114, 143, 176, 208, 240, 242, 273, 306, 338, 370, 404, 433 };
			int[] iconY = { 30, 32, 30, 32, 34, 34, 32, 32, 29, 29, 32, 31, 32, 32, 32 };
			for (int i = 0; i < sideIconsTab.length; i++) {
				if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
					if (iconId[i] != -1) {
						com.koios.client.arpg.SidebarChrome.icon(sideIcons[iconId[i]],sideIconsTab[i]==tabID).drawSprite(frameWidth - 461 + iconX[i], frameHeight - iconY[i]);
					}
				}
			}
		}
	}

	private final int[] redStonesX = { 6, 44, 77, 110, 143, 176, 209, 6, 44, 77, 110, 143, 176, 209 }, redStonesY = { 0, 0, 0, 0, 0, 0, 0, 298, 298, 298, 298, 298, 298, 298 },
			redStonesId = { 35, 39, 39, 39, 39, 39, 36, 37, 39, 39, 39, 39, 39, 38 };

	private void drawRedStones() {
		int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 247;
		int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 336;
		if (tabID == 16) {
			return;
		}
		if (frameMode == ScreenMode.FIXED || (!frameMode.isFixed() && !changeTabArea)) {
			if (tabInterfaceIDs[tabID] != -1 && tabID != 15) {
				cacheSprite[redStonesId[tabID]].drawSprite(redStonesX[tabID] + xOffset, redStonesY[tabID] + yOffset);
			}
		} else if (changeTabArea && frameWidth < 1000) {
			int[] stoneX = { 226, 194, 162, 130, 99, 65, 34, 219, 195, 161, 130, 98, 65, 33 };
			int[] stoneY = { 73, 73, 73, 73, 73, 73, 73, -1, 37, 37, 37, 37, 37, 37, 37 };
			if (tabInterfaceIDs[tabID] != -1 && tabID != 10 && showTabComponents) {
				if (tabID == 7) {
					cacheSprite[39].drawSprite(frameWidth - 130, frameHeight - 37);
				}
				cacheSprite[39].drawSprite(frameWidth - stoneX[tabID], frameHeight - stoneY[tabID]);
			}
		} else if (changeTabArea && frameWidth >= 1000) {
			int[] stoneX = { 417, 385, 353, 321, 289, 256, 224, 129, 193, 161, 130, 98, 65, 33 };
			if (tabInterfaceIDs[tabID] != -1 && tabID != 10 && showTabComponents) {
				cacheSprite[39].drawSprite(frameWidth - stoneX[tabID], frameHeight - 37);
			}
		}
	}

	private void drawTabArea() {
		final int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 241;
		final int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 336;
		if (frameMode == ScreenMode.FIXED) {
			aRSImageProducer_1163.initDrawingArea();
		}
		Rasterizer.lineOffsets = anIntArray1181;
        if(frameMode==ScreenMode.FIXED)com.koios.client.arpg.SidebarChrome.fixed(0,0,tabID);
        else if(!changeTabArea)com.koios.client.arpg.SidebarChrome.fixed(frameWidth-247,frameHeight-336,tabID);
        else com.koios.client.arpg.SidebarChrome.compact(frameWidth,frameHeight,showTabComponents,tabID);
		if (invOverlayInterfaceID == -1) {
			drawSideIcons();
		}
		if (showTabComponents) {
			int x = frameMode == ScreenMode.FIXED ? 31 : frameWidth - 215;
			int y = frameMode == ScreenMode.FIXED ? 37 : frameHeight - 299;
			if (changeTabArea) {
				x = frameWidth - 196;
				y = frameWidth >= 1000 ? frameHeight - 303 : frameHeight - 340;
			}
			if (invOverlayInterfaceID != -1) {
				drawInterface(0, x, RSInterface.interfaceCache[invOverlayInterfaceID], y);
			} else if (com.koios.client.arpg.ArpgTabs.owns(tabID)) {
                com.koios.client.arpg.ArpgTabs.draw(com.koios.client.arpg.ExileTheme.body(),tabID,x,y,arpgSkillState);
			} else if (tabInterfaceIDs[tabID] != -1) {
				drawInterface(0, x, RSInterface.interfaceCache[tabInterfaceIDs[tabID]], y);
			}
		}
		if (menuOpen) {
			drawMenu(frameMode == ScreenMode.FIXED ? 516 : 0, frameMode == ScreenMode.FIXED ? 168 : 0);
		}
		if (frameMode == ScreenMode.FIXED) {
			aRSImageProducer_1163.drawGraphics(168, super.graphics, 516);
			aRSImageProducer_1165.initDrawingArea();
		}
		Rasterizer.lineOffsets = anIntArray1182;
	}

	private void method37(int j) {
		if (Rasterizer.anIntArray1480[17] >= j) {
			Background background = Rasterizer.aBackgroundArray1474s[17];
			int k = background.anInt1452 * background.anInt1453 - 1;
			int j1 = background.anInt1452 * anInt945 * 2;
			byte abyte0[] = background.aByteArray1450;
			byte abyte3[] = aByteArray912;
			for (int i2 = 0; i2 <= k; i2++)
				abyte3[i2] = abyte0[i2 - j1 & k];

			background.aByteArray1450 = abyte3;
			aByteArray912 = abyte0;
			Rasterizer.method370(17);
		}
		if (Rasterizer.anIntArray1480[24] >= j) {
			Background background_1 = Rasterizer.aBackgroundArray1474s[24];
			int l = background_1.anInt1452 * background_1.anInt1453 - 1;
			int k1 = background_1.anInt1452 * anInt945 * 2;
			byte abyte1[] = background_1.aByteArray1450;
			byte abyte4[] = aByteArray912;
			for (int j2 = 0; j2 <= l; j2++)
				abyte4[j2] = abyte1[j2 - k1 & l];

			background_1.aByteArray1450 = abyte4;
			aByteArray912 = abyte1;
			Rasterizer.method370(24);
		}
		if (Rasterizer.anIntArray1480[34] >= j) {
			Background background_2 = Rasterizer.aBackgroundArray1474s[34];
			int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
			int l1 = background_2.anInt1452 * anInt945 * 2;
			byte abyte2[] = background_2.aByteArray1450;
			byte abyte5[] = aByteArray912;
			for (int k2 = 0; k2 <= i1; k2++)
				abyte5[k2] = abyte2[k2 - l1 & i1];

			background_2.aByteArray1450 = abyte5;
			aByteArray912 = abyte2;
			Rasterizer.method370(34);
		}
		if (Rasterizer.anIntArray1480[40] >= j) {
			Background background_2 = Rasterizer.aBackgroundArray1474s[40];
			int i1 = background_2.anInt1452 * background_2.anInt1453 - 1;
			int l1 = background_2.anInt1452 * anInt945 * 2;
			byte abyte2[] = background_2.aByteArray1450;
			byte abyte5[] = aByteArray912;
			for (int k2 = 0; k2 <= i1; k2++)
				abyte5[k2] = abyte2[k2 - l1 & i1];

			background_2.aByteArray1450 = abyte5;
			aByteArray912 = abyte2;
			Rasterizer.method370(40);
		}
		// }
	}

	private void method38() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null && player.textCycle > 0) {
				player.textCycle--;
				if (player.textCycle == 0)
					player.textSpoken = null;
			}
		}
		for (int k = 0; k < npcCount; k++) {
			int l = npcIndices[k];
			NPC npc = npcArray[l];
			if (npc != null && npc.textCycle > 0) {
				npc.textCycle--;
				if (npc.textCycle == 0)
					npc.textSpoken = null;
			}
		}
	}

	private void calcCameraPos() {
		int i = anInt1098 * 128 + 64;
		int j = anInt1099 * 128 + 64;
		int k = method42(plane, j, i) - anInt1100;
		if (xCameraPos < i) {
			xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
			if (xCameraPos > i)
				xCameraPos = i;
		}
		if (xCameraPos > i) {
			xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
			if (xCameraPos < i)
				xCameraPos = i;
		}
		if (zCameraPos < k) {
			zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
			if (zCameraPos > k)
				zCameraPos = k;
		}
		if (zCameraPos > k) {
			zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
			if (zCameraPos < k)
				zCameraPos = k;
		}
		if (yCameraPos < j) {
			yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
			if (yCameraPos > j)
				yCameraPos = j;
		}
		if (yCameraPos > j) {
			yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
			if (yCameraPos < j)
				yCameraPos = j;
		}
		i = anInt995 * 128 + 64;
		j = anInt996 * 128 + 64;
		k = method42(plane, j, i) - anInt997;
		int l = i - xCameraPos;
		int i1 = k - zCameraPos;
		int j1 = j - yCameraPos;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if (l1 < 128)
			l1 = 128;
		if (l1 > 383)
			l1 = 383;
		if (yCameraCurve < l1) {
			yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
			if (yCameraCurve > l1)
				yCameraCurve = l1;
		}
		if (yCameraCurve > l1) {
			yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
			if (yCameraCurve < l1)
				yCameraCurve = l1;
		}
		int j2 = i2 - xCameraCurve;
		if (j2 > 1024)
			j2 -= 2048;
		if (j2 < -1024)
			j2 += 2048;
		if (j2 > 0) {
			xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		if (j2 < 0) {
			xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		int k2 = i2 - xCameraCurve;
		if (k2 > 1024)
			k2 -= 2048;
		if (k2 < -1024)
			k2 += 2048;
		if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
			xCameraCurve = i2;
	}

	public void drawMenu(int x, int y) {
		int xPos = menuOffsetX - (x - 4);
		int yPos = (-y + 4) + menuOffsetY;
		int w = menuWidth;
		int h = menuHeight + 1;
		inputTaken = true;
		tabAreaAltered = true;
		TextDrawingArea menuFont=com.koios.client.arpg.ExileTheme.body();
		com.koios.client.arpg.ExileTheme.panel(xPos,yPos,w,h,com.koios.client.arpg.ExileTheme.BORDER,com.koios.client.arpg.ExileTheme.PANEL);
		DrawingArea.drawHorizontalLine(xPos+4,yPos+18,w-8,com.koios.client.arpg.ExileTheme.BORDER_SUBTLE);
		com.koios.client.arpg.ExileTheme.label(menuFont,xPos+6,yPos+14,"Choose option",com.koios.client.arpg.ExileTheme.MUTED);
		int mouseX = super.mouseX - (x);
		int mouseY = (-y) + super.mouseY;
		for (int i = 0; i < menuActionRow; i++) {
			int textY = yPos + 31 + (menuActionRow - 1 - i) * 15;
			int textColor = com.koios.client.arpg.ExileTheme.TEXT;
			if (mouseX > xPos && mouseX < xPos + w && mouseY > textY - 13 && mouseY < textY + 3) {
				DrawingArea.drawPixels(15, textY - 12, xPos + 3, com.koios.client.arpg.ExileTheme.SELECTED, menuWidth - 6);
				textColor = com.koios.client.arpg.ExileTheme.HEADING;
			}
			com.koios.client.arpg.ExileTheme.label(menuFont,xPos+6,textY,menuActionName[i],textColor);
		}
	}

	private void addFriend(long l) {
		try {
			if (l == 0L)
				return;
			if (friendsCount >= 100 && anInt1046 != 1) {
				pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
				return;
			}
			if (friendsCount >= 200) {
				pushMessage("Your friendlist is full. Max of 100 for free users, and 200 for members", 0, "");
				return;
			}
			String s = UsernameEncoding.formalize(UsernameEncoding.decode(l));
			for (int i = 0; i < friendsCount; i++)
				if (friendsListAsLongs[i] == l) {
					pushMessage(s + " is already on your friend list", 0, "");
					return;
				}
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage("Please remove " + s + " from your ignore list first", 0, "");
					return;
				}

			if (s.equals(myPlayer.name)) {
				return;
			} else {
				friendsList[friendsCount] = s;
				friendsListAsLongs[friendsCount] = l;
				friendsNodeIDs[friendsCount] = 0;
				friendsCount++;
				stream.createFrame(188);
				stream.writeLong(l);
				return;
			}
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("15283, " + (byte) 68 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private int method42(int i, int j, int k) {
		int l = k >> 7;
		int i1 = j >> 7;
		if (l < 0 || i1 < 0 || l > 103 || i1 > 103)
			return 0;
		int j1 = i;
		if (j1 < 3 && (byteGroundArray[1][l][i1] & 2) == 2)
			j1++;
		int k1 = k & 0x7f;
		int l1 = j & 0x7f;
		int i2 = intGroundArray[j1][l][i1] * (128 - k1) + intGroundArray[j1][l + 1][i1] * k1 >> 7;
		int j2 = intGroundArray[j1][l][i1 + 1] * (128 - k1) + intGroundArray[j1][l + 1][i1 + 1] * k1 >> 7;
		return i2 * (128 - l1) + j2 * l1 >> 7;
	}

	private static String intToKOrMil(int j) {
		if (j < 0x186a0)
			return String.valueOf(j);
		if (j < 0x989680)
			return j / 1000 + "K";
		else
			return j / 0xf4240 + "M";
	}

	private void resetLogout() {
        chatFocus.close();arpgSkillsOpen=false;prototypeOpen=false;controlsPanel=null;controlActions.clear();bindingRequests.set(0);skillBindings=null;bindingUser="";bindingSignature="";mapObjective.clear();arpgHazardX=-1;battleEffects.clear();groundLoot.clear();skillDrag.cancel();
        arpgTooltips.clear();arpgHoverWidget=-1;arpgHoverSlot=-1;
		// Reset the screen mode
		frameMode(ScreenMode.FIXED);

		try {
			if (socketStream != null)
				socketStream.close();
		} catch (Exception _ex) {
		}
		socketStream = null;
		loggedIn = false;
		drawnLogo = false;
		if (!rememberMe) {
			myUsername = "";
			myPassword = "";
		}
		loginMessage1 = "";
		loginScreenCursorPos = 0;
		unlinkMRUNodes();
		worldController.initToNull();
		for (int i = 0; i < 4; i++)
			aClass11Array1230[i].method210();
		System.gc();
		stopMidi();
		currentSong = -1;
		nextSong = -1;
		prevSong = 0;
		method58(10, 0, false, 0);
		updateGraphics(true);
	}

	private void method45() {
		aBoolean1031 = true;
		for (int j = 0; j < 7; j++) {
			anIntArray1065[j] = -1;
			for (int k = 0; k < IdentitiKit.length; k++) {
				if (IdentitiKit.cache[k].aBoolean662 || IdentitiKit.cache[k].anInt657 != j + (aBoolean1047 ? 0 : 7))
					continue;
				anIntArray1065[j] = k;
				break;
			}
		}
	}

	private void method46(int i, Stream stream) {
		while ((stream.bitPosition + 21) < (i << 3)) {
			int npcIndex = stream.readBits(14);

			if (npcIndex == 16383)
				break;

			if (npcArray[npcIndex] == null)
				npcArray[npcIndex] = new NPC();

			NPC npc = npcArray[npcIndex];
			npcIndices[npcCount++] = npcIndex;
			npc.anInt1537 = loopCycle;
			int localY = stream.readBits(5);
			if (localY > 15)
				localY -= 32;
			int localX = stream.readBits(5);
			if (localX > 15)
				localX -= 32;
			int j1 = stream.readBits(1);
			npc.desc = NPCDefinition.forID(stream.readBits(Constants.NPC_BITS));
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = npcIndex;
			npc.anInt1540 = npc.desc.npcSizeInSquares;
			npc.anInt1504 = npc.desc.anInt79;
			npc.walkAnimation = npc.desc.walkAnimation;
			npc.turn180Animation = npc.desc.walkingBackwardsAnimation;
			npc.turn90CW = npc.desc.walkLeftAnimation;
			npc.turn90CCW = npc.desc.walkRightAnimation;
			npc.standAnimation = npc.desc.standAnimation;
			npc.setPos(myPlayer.smallX[0] + localX, myPlayer.smallY[0] + localY, j1 == 1);
		}
		stream.finishBitAccess();
	}

	@Override
	public void processGameLoop() {
		if (rsAlreadyLoaded || loadingError || genericLoadingError)
			return;
		loopCycle++;
		if (!loggedIn)
			processLoginScreenInput();
		else
			mainGameProcessor();
		processOnDemandQueue();
		method49();
		handleSounds();
	}

	private void method47(boolean flag) {
		if (myPlayer.x >> 7 == destX && myPlayer.y >> 7 == destY)
			destX = 0;
		int j = playerCount;
		if (flag)
			j = 1;
		for (int l = 0; l < j; l++) {
			Player player;
			int i1;
			if (flag) {
				player = myPlayer;
				i1 = myPlayerIndex << 14;
			} else {
				player = playerArray[playerIndices[l]];
				i1 = playerIndices[l] << 14;
			}
			if (player == null || !player.isVisible())
				continue;
			player.aBoolean1699 = (lowMem && playerCount > 50 || playerCount > 200) && !flag && player.anInt1517 == player.standAnimation;
			int j1 = player.x >> 7;
			int k1 = player.y >> 7;
			if (j1 < 0 || j1 >= 104 || k1 < 0 || k1 >= 104)
				continue;
			
			 // this bit prevents you seeing people while in low detail since you can only see 1 floor at a time
			if (lowMem && player.realPlane != plane) {
				continue;
			}
			
			if (player.aModel_1714 != null && loopCycle >= player.anInt1707 && loopCycle < player.anInt1708) {
				player.aBoolean1699 = false;
				player.anInt1709 = method42(player.realPlane, player.y, player.x);
				worldController.method286(player.realPlane, player.y, player, player.anInt1552, player.anInt1722, player.x, player.anInt1709, player.anInt1719, player.anInt1721, i1, player.anInt1720);
				continue;
			}
			if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
				if (anIntArrayArray929[j1][k1] == anInt1265)
					continue;
				anIntArrayArray929[j1][k1] = anInt1265;
			}
			player.anInt1709 = method42(player.realPlane, player.y, player.x);
			worldController.method285(player.realPlane, player.anInt1552, player.anInt1709, i1, player.y, 60, player.x, player, player.aBoolean1541);
		}
	}

	private int selectedSkillGoal = -1;

	private int fromTab;

	private boolean promptUserForInput(RSInterface class9) {
		int j = class9.contentType;

		if (privateMessageStatus == 2) {
			if (j == 201) {
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				friendsListAction = 1;
				aString1121 = "Enter name of friend to add to list";
			}
			if (j == 202) {
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				friendsListAction = 2;
				aString1121 = "Enter name of friend to delete from list";
			}
		}

		if (j == 997) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 12;
			aString1121 = "Enter your target level";
			int[] skillId = { 0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6, 9, 8, 20, 18, 19, 21, 22, 23, 24 };
			selectedSkillGoal = skillId[class9.id - 27000];
		}

		if (j == 998) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 13;
			aString1121 = "Enter your target experience";
			int[] skillId = { 0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6, 9, 8, 20, 18, 19, 21, 22, 23, 24 };
			selectedSkillGoal = skillId[class9.id - 27024];
		}

		if (j == 999) {
			int[] skillId = { 0, 3, 14, 2, 16, 13, 1, 15, 10, 4, 17, 7, 5, 12, 11, 6, 9, 8, 20, 18, 19, 21, 22, 23, 24 };
			stream.createFrame(62);
			stream.writeShort(skillId[class9.id - 27048]);
		}
		if (j == 205) {
			anInt1011 = 250;
			return true;
		}

		if (class9.id == (50007)) {
			stream.createFrame(185);
			stream.writeShort(50007);
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 11;
			aString1121 = "Enter a keyword to search for";
			variousSettings[1010] = variousSettings[1010] == 0 ? 1 : 0;
		}

		if (j == 501) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 4;
			aString1121 = "Enter name of player to add to list";
		}
		if (j == 502) {
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			friendsListAction = 5;
			aString1121 = "Enter name of player to delete from list";
		}
		if (j == 550) {
			if (RSInterface.interfaceCache[18135].message.startsWith("Join")) {
				stream.createFrame(185);
				stream.writeShort(18135);
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				friendsListAction = 6;
				aString1121 = "Enter the name of the chat you wish to join";
			} else {
				stream.createFrame(185);
				stream.writeShort(49627);
			}
		}

		if (j == 22222) {
			inputTaken = true;
			messagePromptRaised = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			friendsListAction = 6199;
			aString1121 = "Enter a name for the clan chat.";
		}

		if (j == 677) {
			inputTaken = true;
			messagePromptRaised = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			friendsListAction = 6200;
			aString1121 = "Enter name of the player you would like kicked.";
		}

		if (j >= 300 && j <= 313) {
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = anIntArray1065[k];
			if (i2 != -1) {
				do {
					if (j1 == 0 && --i2 < 0)
						i2 = IdentitiKit.length - 1;
					if (j1 == 1 && ++i2 >= IdentitiKit.length)
						i2 = 0;
				} while (IdentitiKit.cache[i2].aBoolean662 || IdentitiKit.cache[i2].anInt657 != k + (aBoolean1047 ? 0 : 7));
				anIntArray1065[k] = i2;
				aBoolean1031 = true;
			}
		}
		if (j >= 314 && j <= 323) {
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = anIntArray990[l];
			if (k1 == 0 && --j2 < 0)
				j2 = anIntArrayArray1003[l].length - 1;
			if (k1 == 1 && ++j2 >= anIntArrayArray1003[l].length)
				j2 = 0;
			anIntArray990[l] = j2;
			aBoolean1031 = true;
		}
		if (j == 324 && !aBoolean1047) {
			aBoolean1047 = true;
			method45();
		}
		if (j == 325 && aBoolean1047) {
			aBoolean1047 = false;
			method45();
		}
		if (j == 326) {
			stream.createFrame(101);
			stream.writeLEShort(aBoolean1047 ? 0 : 1);
			for (int i1 = 0; i1 < 7; i1++)
				stream.writeLEShort(anIntArray1065[i1]);

			for (int l1 = 0; l1 < 5; l1++)
				stream.writeLEShort(anIntArray990[l1]);

			return true;
		}
		if (j == 613)
			canMute = !canMute;
		if (j >= 601 && j <= 612) {
			clearTopInterfaces();
			if (reportAbuseInput.length() > 0) {
				stream.createFrame(218);
				stream.writeLong(UsernameEncoding.encode(reportAbuseInput));
				stream.writeLEShort(j - 601);
				stream.writeLEShort(canMute ? 1 : 0);
			}
		}
		return false;
	}

	private void method49(Stream stream) {
		for (int j = 0; j < anInt893; j++) {
			int k = anIntArray894[j];
			Player player = playerArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x40) != 0)
				l += stream.readUnsignedByte() << 8;
			method107(l, k, stream, player);
		}
	}

	private void method50(int i, int k, int l, int i1, int j1) {
		int k1 = worldController.method300(j1, l, i);
		if (k1 != 0) {
			int l1 = worldController.method304(j1, l, i, k1);
			int k2 = l1 >> 6 & 3;
			int i3 = l1 & 0x1f;
			int k3 = k;
			if (k1 > 0)
				k3 = i1;
			int ai[] = minimapImage.myPixels;
			int k4 = 24624 + l * 4 + (103 - i) * 512 * 4;
			int i5 = k1 >> 14 & 0x7fff;
			ObjectDefinition class46_2 = ObjectDefinition.forID(i5);
			if (class46_2.anInt758 != -1) {
				Background background_2 = mapScenes[class46_2.anInt758];
				if (background_2 != null) {
					int i6 = (class46_2.sizeX * 4 - background_2.anInt1452) / 2;
					int j6 = (class46_2.sizeY * 4 - background_2.anInt1453) / 2;
					background_2.drawBackground(48 + l * 4 + i6, 48 + (104 - i - class46_2.sizeY) * 4 + j6);
				}
			} else {
				if (i3 == 0 || i3 == 2)
					if (k2 == 0) {
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					} else if (k2 == 1) {
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					} else if (k2 == 2) {
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					} else if (k2 == 3) {
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
				if (i3 == 3)
					if (k2 == 0)
						ai[k4] = k3;
					else if (k2 == 1)
						ai[k4 + 3] = k3;
					else if (k2 == 2)
						ai[k4 + 3 + 1536] = k3;
					else if (k2 == 3)
						ai[k4 + 1536] = k3;
				if (i3 == 2)
					if (k2 == 3) {
						ai[k4] = k3;
						ai[k4 + 512] = k3;
						ai[k4 + 1024] = k3;
						ai[k4 + 1536] = k3;
					} else if (k2 == 0) {
						ai[k4] = k3;
						ai[k4 + 1] = k3;
						ai[k4 + 2] = k3;
						ai[k4 + 3] = k3;
					} else if (k2 == 1) {
						ai[k4 + 3] = k3;
						ai[k4 + 3 + 512] = k3;
						ai[k4 + 3 + 1024] = k3;
						ai[k4 + 3 + 1536] = k3;
					} else if (k2 == 2) {
						ai[k4 + 1536] = k3;
						ai[k4 + 1536 + 1] = k3;
						ai[k4 + 1536 + 2] = k3;
						ai[k4 + 1536 + 3] = k3;
					}
			}
		}
		k1 = worldController.method302(j1, l, i);
		if (k1 != 0) {
			int i2 = worldController.method304(j1, l, i, k1);
			int l2 = i2 >> 6 & 3;
			int j3 = i2 & 0x1f;
			int l3 = k1 >> 14 & 0x7fff;
			ObjectDefinition class46_1 = ObjectDefinition.forID(l3);
			if (class46_1.anInt758 != -1) {
				Background background_1 = mapScenes[class46_1.anInt758];
				if (background_1 != null) {
					int j5 = (class46_1.sizeX * 4 - background_1.anInt1452) / 2;
					int k5 = (class46_1.sizeY * 4 - background_1.anInt1453) / 2;
					background_1.drawBackground(48 + l * 4 + j5, 48 + (104 - i - class46_1.sizeY) * 4 + k5);
				}
			} else if (j3 == 9) {
				int l4 = 0xeeeeee;
				if (k1 > 0)
					l4 = 0xee0000;
				int ai1[] = minimapImage.myPixels;
				int l5 = 24624 + l * 4 + (103 - i) * 512 * 4;
				if (l2 == 0 || l2 == 2) {
					ai1[l5 + 1536] = l4;
					ai1[l5 + 1024 + 1] = l4;
					ai1[l5 + 512 + 2] = l4;
					ai1[l5 + 3] = l4;
				} else {
					ai1[l5] = l4;
					ai1[l5 + 512 + 1] = l4;
					ai1[l5 + 1024 + 2] = l4;
					ai1[l5 + 1536 + 3] = l4;
				}
			}
		}
		k1 = worldController.method303(j1, l, i);
		if (k1 != 0) {
			int j2 = k1 >> 14 & 0x7fff;
			ObjectDefinition class46 = ObjectDefinition.forID(j2);
			if (class46.anInt758 != -1) {
				Background background = mapScenes[class46.anInt758];
				if (background != null) {
					int i4 = (class46.sizeX * 4 - background.anInt1452) / 2;
					int j4 = (class46.sizeY * 4 - background.anInt1453) / 2;
					background.drawBackground(48 + l * 4 + i4, 48 + (104 - i - class46.sizeY) * 4 + j4);
				}
			}
		}
	}

	private void loadTitleScreen() {
		new Background(titleStreamLoader, "titlebox", 0);
		new Background(titleStreamLoader, "titlebutton", 0);
		aBackgroundArray1152s = new Background[12];
		int j = 0;
		try {
			j = Integer.parseInt(getParameter("fl_icon"));
		} catch (Exception _ex) {
		}
		if (j == 0) {
			for (int k = 0; k < 12; k++)
				aBackgroundArray1152s[k] = new Background(titleStreamLoader, "runes", k);

		} else {
			for (int l = 0; l < 12; l++)
				aBackgroundArray1152s[l] = new Background(titleStreamLoader, "runes", 12 + (l & 3));

		}
		new Sprite(128, 265);
		new Sprite(128, 265);

		anIntArray851 = new int[256];
		for (int k1 = 0; k1 < 64; k1++)
			anIntArray851[k1] = k1 * 0x40000;

		for (int l1 = 0; l1 < 64; l1++)
			anIntArray851[l1 + 64] = 0xff0000 + 1024 * l1;

		for (int i2 = 0; i2 < 64; i2++)
			anIntArray851[i2 + 128] = 0xffff00 + 4 * i2;

		for (int j2 = 0; j2 < 64; j2++)
			anIntArray851[j2 + 192] = 0xffffff;

		anIntArray852 = new int[256];
		for (int k2 = 0; k2 < 64; k2++)
			anIntArray852[k2] = k2 * 1024;

		for (int l2 = 0; l2 < 64; l2++)
			anIntArray852[l2 + 64] = 65280 + 4 * l2;

		for (int i3 = 0; i3 < 64; i3++)
			anIntArray852[i3 + 128] = 65535 + 0x40000 * i3;

		for (int j3 = 0; j3 < 64; j3++)
			anIntArray852[j3 + 192] = 0xffffff;

		anIntArray853 = new int[256];
		for (int k3 = 0; k3 < 64; k3++)
			anIntArray853[k3] = k3 * 4;

		for (int l3 = 0; l3 < 64; l3++)
			anIntArray853[l3 + 64] = 255 + 0x40000 * l3;

		for (int i4 = 0; i4 < 64; i4++)
			anIntArray853[i4 + 128] = 0xff00ff + 1024 * i4;

		for (int j4 = 0; j4 < 64; j4++)
			anIntArray853[j4 + 192] = 0xffffff;

		anIntArray1190 = new int[32768];
		anIntArray1191 = new int[32768];
		randomizeBackground(null);
	}

	private static void setHighMem() {
		WorldController.lowMem = false;
		Rasterizer.lowMem = false;
		lowMem = false;
		ObjectManager.lowMem = false;
		ObjectDefinition.lowMem = false;
	}

	private static void setLowMem() {
		WorldController.lowMem = true;
		Rasterizer.lowMem = true;
		lowMem = true;
		ObjectManager.lowMem = true;
		ObjectDefinition.lowMem = true;
	}

	public static void main(String args[]) {
		try {
			nodeID = 10;
			portOff = 0;
			setHighMem();
			isMembers = true;
			Signlink.storeid = 32;
			Signlink.startpriv(InetAddress.getLocalHost());
			instance = new Client();
			instance.createClientFrame(frameWidth, frameHeight);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Client instance;

	private void loadingStages() {
		if (lowMem && loadingStage == 2 && ObjectManager.anInt131 != plane) {
			aRSImageProducer_1165.initDrawingArea();
			drawLoadingMessages(1, "Loading - please wait.", null);
			aRSImageProducer_1165.drawGraphics(frameMode == ScreenMode.FIXED ? 4 : 0, super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0);
			loadingStage = 1;
			aLong824 = System.currentTimeMillis();
		}
		if (loadingStage == 1) {
			int j = method54();
			if (j != 0 && System.currentTimeMillis() - aLong824 > 0x57e40L) {
				Signlink.reporterror(
						myUsername + " glcfb " + aLong1215 + "," + j + "," + lowMem + "," + decompressors[0] + "," + onDemandFetcher.getNodeCount() + "," + plane + "," + anInt1069 + "," + anInt1070);
				aLong824 = System.currentTimeMillis();
			}
		}
		if (loadingStage == 2 && plane != anInt985) {
			anInt985 = plane;
			method24(plane);
		}
	}

	private String floorMaps = "";
	private String objectMaps = "";

	private int method54() {

		if (!floorMaps.isEmpty() || !objectMaps.isEmpty()) {
			floorMaps = "";
			objectMaps = "";
		}

		for (int i = 0; i < aByteArrayArray1183.length; i++) {

			floorMaps += "  " + anIntArray1235[i];
			objectMaps += "  " + anIntArray1236[i];

			if (aByteArrayArray1183[i] == null && anIntArray1235[i] != -1)
				return -1;
			if (aByteArrayArray1247[i] == null && anIntArray1236[i] != -1)
				return -2;
		}
		
		boolean flag = true;
		for (int j = 0; j < aByteArrayArray1183.length; j++) {
			byte abyte0[] = aByteArrayArray1247[j];
			if (abyte0 != null) {
				int k = (anIntArray1234[j] >> 8) * 64 - baseX;
				int l = (anIntArray1234[j] & 0xff) * 64 - baseY;
				if (aBoolean1159) {
					k = 10;
					l = 10;
				}
				flag &= ObjectManager.method189(k, abyte0, l);
			}
		}
		if (!flag)
			return -3;
		if (aBoolean1080) {
			return -4;
		} else {
			loadingStage = 2;
			ObjectManager.anInt131 = plane;
			method22();
			stream.createFrame(121);
			return 0;
		}
	}

	private void method55() {
		for (Animable_Sub4 class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013.reverseGetFirst(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
				.reverseGetNext())
			if (class30_sub2_sub4_sub4.anInt1597 != plane || loopCycle > class30_sub2_sub4_sub4.anInt1572)
				class30_sub2_sub4_sub4.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub4.anInt1571) {
				if (class30_sub2_sub4_sub4.anInt1590 > 0) {
					NPC npc = npcArray[class30_sub2_sub4_sub4.anInt1590 - 1];
					if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
						class30_sub2_sub4_sub4.method455(loopCycle, npc.y, method42(class30_sub2_sub4_sub4.anInt1597, npc.y, npc.x) - class30_sub2_sub4_sub4.anInt1583, npc.x);
				}
				if (class30_sub2_sub4_sub4.anInt1590 < 0) {
					int j = -class30_sub2_sub4_sub4.anInt1590 - 1;
					Player player;
					if (j == unknownInt10)
						player = myPlayer;
					else
						player = playerArray[j];
					if (player != null && player.x >= 0 && player.x < 13312 && player.y >= 0 && player.y < 13312)
						class30_sub2_sub4_sub4.method455(loopCycle, player.y, method42(class30_sub2_sub4_sub4.anInt1597, player.y, player.x) - class30_sub2_sub4_sub4.anInt1583, player.x);
				}
				class30_sub2_sub4_sub4.method456(anInt945);
				worldController.method285(plane, class30_sub2_sub4_sub4.anInt1595, (int) class30_sub2_sub4_sub4.aDouble1587, -1, (int) class30_sub2_sub4_sub4.aDouble1586, 60,
						(int) class30_sub2_sub4_sub4.aDouble1585, class30_sub2_sub4_sub4, false);
			}

	}

	@Override
	public AppletContext getAppletContext() {
		if (Signlink.mainapp != null)
			return Signlink.mainapp.getAppletContext();
		else
			return super.getAppletContext();
	}

	private void drawLogo() {
        loginScreenProducer.initDrawingArea();
        com.koios.client.arpg.ExileLogin.background();
    }

	private void processOnDemandQueue() {
		do {
			OnDemandData onDemandData;
			do {
				onDemandData = onDemandFetcher.getNextNode();
				if (onDemandData == null)
					return;
				if (onDemandData.dataType == 0) {
					Model.method460(onDemandData.buffer, onDemandData.ID);
					if (backDialogID != -1)
						inputTaken = true;
				}
				if (onDemandData.dataType == 1) {
					FrameReader.load634(onDemandData.ID, onDemandData.buffer);
				}
				if (onDemandData.dataType == 2 && onDemandData.ID == nextSong && onDemandData.buffer != null) {
					musicData = new byte[onDemandData.buffer.length];
					ArrayUtils.arraycopy(onDemandData.buffer, 0, musicData, 0, musicData.length);
					fetchMusic = true;
				}
				if (onDemandData.dataType == 3 && loadingStage == 1) {
					for (int i = 0; i < aByteArrayArray1183.length; i++) {
						if (anIntArray1235[i] == onDemandData.ID) {
							aByteArrayArray1183[i] = onDemandData.buffer;
							if (onDemandData.buffer == null)
								anIntArray1235[i] = -1;
							break;
						}
						if (anIntArray1236[i] != onDemandData.ID)
							continue;
						aByteArrayArray1247[i] = onDemandData.buffer;
						if (onDemandData.buffer == null)
							anIntArray1236[i] = -1;
						break;
					}

				}
			} while (onDemandData.dataType != 93 || !onDemandFetcher.method564(onDemandData.ID));
			ObjectManager.method173(new Stream(onDemandData.buffer), onDemandFetcher);
		} while (true);
	}

	private void method60(int i) {
		RSInterface class9 = RSInterface.interfaceCache[i];
		for (int j = 0; j < class9.children.length; j++) {
			if (class9.children[j] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[j]];
			if (class9_1.type == 1)
				method60(class9_1.id);
			class9_1.anInt246 = 0;
			class9_1.anInt208 = 0;
		}
	}

	private void drawHeadIcon() {
		if (anInt855 != 2)
			return;
		calcEntityScreenPos((anInt934 - baseX << 7) + anInt937, anInt936 * 2, (anInt935 - baseY << 7) + anInt938);
		if (spriteDrawX > -1 && loopCycle % 20 < 10)
			headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
	}

    public boolean isQaFixture(){String fixture=System.getProperty("arpg.qaCharacter","");return loggedIn&&fixture.matches("qa[a-z0-9]{3,9}")&&fixture.equalsIgnoreCase(myUsername);}
    private final int[] qaNpcLastAnimation = new int[16384];
	private void qaRunCommands(){
        if(!com.koios.client.arpg.QaClientHooks.enabled())return;
        for(String line:com.koios.client.arpg.QaClientHooks.commands())try{
            String[] words=line.trim().split(" ");
            if(line.startsWith("proto "))sendPrototype(line.substring(6));
            else if(line.startsWith("skill "))sendArpgSkill(line.substring(6));
            else if(line.startsWith("command ")){String command=line.substring(8);if(command.equals("arpgmaps")||command.equals("arpgbases")||command.matches("arpgqa (level [0-9]{1,2}|base [0-9]{5}|gems|materials)")){stream.createFrame(103);stream.writeLEShort(command.length()+1);stream.writeString(command);}}
            else if(words[0].equals("equip")||words[0].equals("drop")){int slot=Integer.parseInt(words[1]);RSInterface bag=RSInterface.interfaceCache[3214];if(slot>=0&&slot<bag.container.length&&bag.container[slot]>0){int id=bag.container[slot]-1;if(words[0].equals("equip")){stream.createFrame(41);stream.writeShort(id);stream.method432(slot);stream.method432(3214);}else{stream.createFrame(87);stream.method432(id);stream.writeShort(3214);stream.method432(slot);}}}
            else if(words[0].equals("use")){int source=Integer.parseInt(words[1]),target=Integer.parseInt(words[2]);RSInterface bag=RSInterface.interfaceCache[3214];if(source>=0&&target>=0&&source<bag.container.length&&target<bag.container.length&&bag.container[source]>0&&bag.container[target]>0){stream.createFrame(53);stream.writeShort(target);stream.method432(source);stream.method433(bag.container[target]-1);stream.writeShort(3214);stream.method431(bag.container[source]-1);stream.writeShort(3214);}}
            else if(words[0].equals("hover")){mouseX=Integer.parseInt(words[1]);mouseY=Integer.parseInt(words[2]);}
            else if(words[0].equals("tab"))setTab(Integer.parseInt(words[1]));
            else if(words[0].equals("move")){int tx=Integer.parseInt(words[1])-baseX,ty=Integer.parseInt(words[2])-baseY;if(tx>=1&&ty>=1&&tx<103&&ty<103)doWalkTo(0,0,0,0,myPlayer.smallY[0],0,0,ty,myPlayer.smallX[0],true,tx);}
            else if(words[0].equals("attack")){int index=Integer.parseInt(words[1]);NPC target=index>=0&&index<npcArray.length?npcArray[index]:null;if(target!=null){doWalkTo(2,0,1,0,myPlayer.smallY[0],1,0,target.smallY[0],myPlayer.smallX[0],false,target.smallX[0]);stream.createFrame(72);stream.method432(index);}}
            else if(words[0].equals("take")){stream.createFrame(236);stream.method431(Integer.parseInt(words[3]));stream.writeShort(Integer.parseInt(words[1]));stream.method431(Integer.parseInt(words[2]));}
            else if(words[0].equals("interact")){int index=Integer.parseInt(words[1]);NPC target=index>=0&&index<npcArray.length?npcArray[index]:null;if(target!=null){doWalkTo(2,0,1,0,myPlayer.smallY[0],1,0,target.smallY[0],myPlayer.smallX[0],false,target.smallX[0]);stream.createFrame(155);stream.method431(index);}}
            else if(words[0].equals("object")){int id=Integer.parseInt(words[1]),x=Integer.parseInt(words[2])-baseX,y=Integer.parseInt(words[3])-baseY;if(x>=0&&x<104&&y>=0&&y<104){int uid=worldController.method302(plane,x,y);if((uid>>14&0x7fff)==id&&method66(uid,y,x)){stream.createFrame(132);stream.method433(x+baseX);stream.writeShort(id);stream.method432(y+baseY);}}}
            else if(words[0].equals("camera")){minimapInt1=Integer.parseInt(words[1])&2047;anInt1184=Math.max(128,Math.min(383,Integer.parseInt(words[2])));anInt1186=anInt1187=0;anInt896=0;if(words.length>3)cameraZoom=Math.max(150,Math.min(1250,Integer.parseInt(words[3])));}
            else if(line.equals("panel controls"))openControls();
            else if(words[0].equals("click")){super.saveClickX=Integer.parseInt(words[1]);super.saveClickY=Integer.parseInt(words[2]);super.clickMode3=1;}
            else if(words[0].equals("focus"))focusGained(new java.awt.event.FocusEvent(getGameComponent(),java.awt.event.FocusEvent.FOCUS_GAINED));
            else if(words[0].equals("unfocus"))focusLost(new java.awt.event.FocusEvent(getGameComponent(),java.awt.event.FocusEvent.FOCUS_LOST));
            else if(words[0].equals("press")||words[0].equals("hold")||words[0].equals("release")){int code=Integer.parseInt(words[1]),mods=words.length>2?Integer.parseInt(words[2]):0;char ch=code>=65&&code<=90?(char)(code+32):code==java.awt.event.KeyEvent.VK_ENTER?'\n':java.awt.event.KeyEvent.CHAR_UNDEFINED;java.awt.event.KeyEvent event=new java.awt.event.KeyEvent(getGameComponent(),words[0].equals("release")?java.awt.event.KeyEvent.KEY_RELEASED:java.awt.event.KeyEvent.KEY_PRESSED,System.currentTimeMillis(),mods,code,ch);if(words[0].equals("release"))keyReleased(event);else{keyPressed(event);if(words[0].equals("press"))keyReleased(new java.awt.event.KeyEvent(getGameComponent(),java.awt.event.KeyEvent.KEY_RELEASED,System.currentTimeMillis(),0,code,ch));}}
            else if(words[0].equals("mouse")){int button=Integer.parseInt(words[1]),x=Integer.parseInt(words[2]),y=Integer.parseInt(words[3]),mods=words.length>4?Integer.parseInt(words[4]):0;java.awt.event.MouseEvent event=new java.awt.event.MouseEvent(getGameComponent(),java.awt.event.MouseEvent.MOUSE_PRESSED,System.currentTimeMillis(),mods,x,y,1,false,button);if(!routeArpgMouse(event,x,y)){super.saveClickX=x;super.saveClickY=y;super.clickMode3=button==3?2:button==1?1:0;}}
            else if(words[0].equals("loot")){for(com.koios.client.arpg.GroundLootLabels.Entry entry:groundLoot.snapshot())if(entry.key.equals(words[1])){pickupLoot(entry);break;}}
            else if(line.equals("panel skills")){arpgSkillsOpen=true;prototypeOpen=false;sendArpgSkill("status");}
            else if(line.equals("panel close")){arpgSkillsOpen=false;prototypeOpen=false;controlsPanel=null;inputTaken=true;}
            else if(words[0].equals("screen"))frameMode(ScreenMode.valueOf(words[1].toUpperCase(java.util.Locale.ROOT)));
        }catch(Exception failure){System.err.println("QA action: "+line+" / "+failure);}
        if(myPlayer!=null){StringBuilder state=new StringBuilder("mode="+frameMode+"\nsize="+frameWidth+"x"+frameHeight+"\ndisplay="+getGameComponent().getBounds()+"\nbase="+baseX+","+baseY+"\nplayer="+(baseX+myPlayer.smallX[0])+","+(baseY+myPlayer.smallY[0])+"\ncamera="+minimapInt1+","+anInt1184+" / rendered="+xCameraCurve+","+yCameraCurve+"\nterrain="+arpgTerrainLayout+","+arpgTerrainBaseX+","+arpgTerrainBaseY+"\nskills="+java.util.Arrays.toString(arpgSkillState)+"\ncharacter="+java.util.Arrays.toString(arpgCharacterLines)+"\ninventory=");RSInterface bag=RSInterface.interfaceCache[3214];if(bag!=null&&bag.container!=null)for(int slot=0;slot<bag.container.length;slot++)if(bag.container[slot]>0)state.append(slot).append(":").append(bag.container[slot]-1).append(" ");state.append("\ncontrols=").append(controlsPanel==null?"closed":controlsPanel.status()).append("\nbindings=").append(bindingSignature).append("\nloot=");for(com.koios.client.arpg.GroundLootLabels.Entry entry:groundLoot.snapshot())state.append(entry.key).append(":").append(entry.id).append(":").append(entry.worldX).append(",").append(entry.worldY).append(";");state.append("\nnpcs=");for(int i=0;i<npcCount;i++){NPC npc=npcArray[npcIndices[i]];if(npc!=null&&npc.desc!=null)state.append(npcIndices[i]).append(":").append(npc.desc.name).append(":").append(baseX+npc.smallX[0]).append(",").append(baseY+npc.smallY[0]).append(";");}state.append("\nfps=").append(super.fps).append("\nnpcVitals=");for(int ni=0;ni<npcCount;ni++){int index=npcIndices[ni];NPC npc=npcArray[index];if(npc!=null&&npc.desc!=null){state.append(index).append(":").append(npc.currentHealth).append("/").append(npc.maxHealth).append(":").append(npc.anim).append(":").append(npc.desc.interfaceType).append(";");if(index<qaNpcLastAnimation.length){if(npc.anim!=qaNpcLastAnimation[index]&&(npc.anim==5487||npc.anim==5491))com.koios.client.arpg.QaClientHooks.event("npc-"+npc.desc.interfaceType+"-anim-"+npc.anim);qaNpcLastAnimation[index]=npc.anim;}}}state.append("\nobjects=");for(int ox=Math.max(0,myPlayer.smallX[0]-8);ox<Math.min(104,myPlayer.smallX[0]+9);ox++)for(int oy=Math.max(0,myPlayer.smallY[0]-8);oy<Math.min(104,myPlayer.smallY[0]+9);oy++){int uid=worldController.method302(plane,ox,oy);if(uid!=0){int id=uid>>14&0x7fff;if(id==2465||id==2466||id==409||id==13704||id==3193)state.append(id).append(":").append(baseX+ox).append(",").append(baseY+oy).append(";");}}state.append("\nmenu=");for(int mi=0;mi<menuActionRow;mi++)state.append(menuActionID[mi]).append(":").append(menuActionName[mi]).append(";");com.koios.client.arpg.QaClientHooks.state(state.toString());}
    }
    private boolean qaMapsGranted,qaScreenApplied;
 private void mainGameProcessor() {
        processArpgControls();
        qaRunCommands();
        if(!qaScreenApplied&&loadingStage==2&&clientStatus==ClientStatus.LOCAL&&isQaFixture()&&Boolean.getBoolean("arpg.qaFullscreen")){qaScreenApplied=true;frameMode(ScreenMode.FULLSCREEN);}
        if(!qaMapsGranted&&clientStatus==ClientStatus.LOCAL&&isQaFixture()&&Boolean.getBoolean("arpg.qaMaps")){qaMapsGranted=true;String command="arpgmaps";stream.createFrame(103);stream.writeLEShort(command.length()+1);stream.writeString(command);}

        if(controlsPanel!=null&&super.clickMode3!=0){if(super.clickMode3==1)controlsPanel.click(overlayMouseX(super.saveClickX),overlayMouseY(super.saveClickY));super.clickMode3=super.clickMode2=0;inputTaken=true;}
        if(super.clickMode3==1&&!menuOpen&&controlsPanel==null&&!prototypeOpen&&!arpgSkillsOpen&&openInterfaceID==-1&&gameplayMousePoint(super.saveClickX,super.saveClickY)){int offset=frameMode==ScreenMode.FIXED?4:0;com.koios.client.arpg.GroundLootLabels.Entry loot=groundLoot.hit(super.saveClickX-offset,super.saveClickY-offset);if(loot!=null){chatFocus.close();pickupLoot(loot);super.clickMode3=0;}}
        // Keep an in-progress chat draft owned by chat across transient focus changes.
        if(super.clickMode3==1 && !menuOpen && !messagePromptRaised && inputDialogState==0 && backDialogID==-1){
            if(super.saveClickX>=8 && super.saveClickX<519 && super.saveClickY>=frameHeight-44 && super.saveClickY<frameHeight-22){
                chatFocus.open();inputTaken=true;super.clickMode3=0;
            }else if(chatFocus.isEditing()){chatFocus.close();inputTaken=true;}
            if(super.clickMode3==1&&showTabComponents&&invOverlayInterfaceID==-1&&com.koios.client.arpg.ArpgTabs.owns(tabID)){
                int tx=frameMode==ScreenMode.FIXED?547:changeTabArea?frameWidth-196:frameWidth-215;
                int ty=frameMode==ScreenMode.FIXED?205:changeTabArea?frameHeight-(frameWidth>=1000?303:340):frameHeight-299;
                String destination=com.koios.client.arpg.ArpgTabs.action(tabID,super.saveClickX,super.saveClickY,tx,ty);
                if(destination!=null){if(destination.equals("tree"))sendPrototype("tree");else{arpgSkillsOpen=true;prototypeOpen=false;sendArpgSkill("status");}super.clickMode3=0;}
            }
            if(prototypeOpen&&super.clickMode3==1&&!overSidebar(super.saveClickX,super.saveClickY)){int px=overlayMouseX(super.saveClickX),py=overlayMouseY(super.saveClickY);String action=prototypePanel.actionAt(px,py,arpgControlDown);if(action!=null){sendPrototype(action);if(action.startsWith("open ")||action.equals("intro")||action.equals("resume"))prototypeOpen=false;}super.clickMode3=0;}
            if(!arpgSkillsOpen&&!prototypeOpen&&openInterfaceID==-1&&super.clickMode3==1){int offset=frameMode==ScreenMode.FIXED?4:0;int slot=com.koios.client.arpg.SkillPanel.barSlotAt(super.saveClickX-offset,super.saveClickY-offset,frameMode==ScreenMode.FIXED?512:frameWidth,frameMode==ScreenMode.FIXED?334:frameHeight);if(slot>=0){sendArpgSkill("slot "+slot);super.clickMode3=0;}}
            if(arpgSkillsOpen && super.clickMode3==1&&!overSidebar(super.saveClickX,super.saveClickY)){
                int px=overlayMouseX(super.saveClickX),py=overlayMouseY(super.saveClickY);
                if(px>=200&&px<290&&py>=22&&py<45)openControls();
                else if(px>=460&&px<=496&&py>=16&&py<48){arpgSkillsOpen=false;skillDrag.cancel();}
                else if(awtFocus)skillDrag.begin(px,py,arpgSkillState);
                super.clickMode3=0;
            }
        }

        if(arpgSkillsOpen&&!menuOpen&&awtFocus&&!chatFocus.isEditing()&&!messagePromptRaised&&inputDialogState==0&&backDialogID==-1&&openInterfaceID==-1){
            if(skillDrag.active()){if(super.clickMode2==1)skillDrag.move(overlayMouseX(mouseX),overlayMouseY(mouseY));else if(super.clickMode2==0){if(tabID==3&&overSidebar(super.releasedX,super.releasedY)&&skillDrag.kind==3){sendArpgSkill("unsocket "+skillDrag.skill+" "+skillDrag.support);skillDrag.cancel();}else if(tabID==3&&overSidebar(super.releasedX,super.releasedY)&&skillDrag.kind==5){sendArpgSkill("unsocketactive "+skillDrag.origin);skillDrag.cancel();}else skillDrag.release(overlayMouseX(super.releasedX),overlayMouseY(super.releasedY));}else skillDrag.cancel();}
        }else skillDrag.cancel();

		refreshFrameSize();
		if (anInt1104 > 1)
			anInt1104--;
		if (anInt1011 > 0)
			anInt1011--;
		for (int j = 0; j < 5; j++)
			if (!parsePacket())
				break;

		if (!loggedIn)
			return;
		synchronized (mouseDetection.syncObject) {
			if (flagged) {
				if (super.clickMode3 != 0 || mouseDetection.coordsIndex >= 40) {
					stream.createFrame(45);
					stream.writeLEShort(0);
					int j2 = stream.currentOffset;
					int j3 = 0;
					for (int j4 = 0; j4 < mouseDetection.coordsIndex; j4++) {
						if (j2 - stream.currentOffset >= 240)
							break;
						j3++;
						int l4 = mouseDetection.coordsY[j4];
						if (l4 < 0)
							l4 = 0;
						else if (l4 > 502)
							l4 = 502;
						int k5 = mouseDetection.coordsX[j4];
						if (k5 < 0)
							k5 = 0;
						else if (k5 > 764)
							k5 = 764;
						int i6 = l4 * 765 + k5;
						if (mouseDetection.coordsY[j4] == -1 && mouseDetection.coordsX[j4] == -1) {
							k5 = -1;
							l4 = -1;
							i6 = 0x7ffff;
						}
						if (k5 == anInt1237 && l4 == anInt1238) {
							if (anInt1022 < 2047)
								anInt1022++;
						} else {
							int j6 = k5 - anInt1237;
							anInt1237 = k5;
							int k6 = l4 - anInt1238;
							anInt1238 = l4;
							if (anInt1022 < 8 && j6 >= -32 && j6 <= 31 && k6 >= -32 && k6 <= 31) {
								j6 += 32;
								k6 += 32;
								stream.writeShort((anInt1022 << 12) + (j6 << 6) + k6);
								anInt1022 = 0;
							} else if (anInt1022 < 8) {
								stream.writeDWordBigEndian(0x800000 + (anInt1022 << 19) + i6);
								anInt1022 = 0;
							} else {
								stream.writeInt(0xc0000000 + (anInt1022 << 19) + i6);
								anInt1022 = 0;
							}
						}
					}

					stream.writeBytes(stream.currentOffset - j2);
					if (j3 >= mouseDetection.coordsIndex) {
						mouseDetection.coordsIndex = 0;
					} else {
						mouseDetection.coordsIndex -= j3;
						for (int i5 = 0; i5 < mouseDetection.coordsIndex; i5++) {
							mouseDetection.coordsX[i5] = mouseDetection.coordsX[i5 + j3];
							mouseDetection.coordsY[i5] = mouseDetection.coordsY[i5 + j3];
						}

					}
				}
			} else {
				mouseDetection.coordsIndex = 0;
			}
		}
		if (super.clickMode3 != 0) {
			long l = (super.aLong29 - aLong1220) / 50L;
			if (l > 4095L)
				l = 4095L;
			aLong1220 = super.aLong29;
			int k2 = super.saveClickY;
			if (k2 < 0)
				k2 = 0;
			else if (k2 > 502)
				k2 = 502;
			int k3 = super.saveClickX;
			if (k3 < 0)
				k3 = 0;
			else if (k3 > 764)
				k3 = 764;
			int k4 = k2 * 765 + k3;
			int j5 = 0;
			if (super.clickMode3 == 2)
				j5 = 1;
			int l5 = (int) l;
			stream.createFrame(241);
			stream.writeInt((l5 << 20) + (j5 << 19) + k4);
		}
		if (anInt1016 > 0)
			anInt1016--;
		if (super.keyArray[1] == 1 || super.keyArray[2] == 1 || super.keyArray[3] == 1 || super.keyArray[4] == 1)
			aBoolean1017 = true;
		if (aBoolean1017 && anInt1016 <= 0) {
			anInt1016 = 20;
			aBoolean1017 = false;
			stream.createFrame(86);
			stream.writeShort(anInt1184);
			stream.method432(minimapInt1);
		}
		if (super.awtFocus && !aBoolean954) {
			aBoolean954 = true;
			stream.createFrame(3);
			stream.writeLEShort(1);
		}
		if (!super.awtFocus && aBoolean954) {
			aBoolean954 = false;
			stream.createFrame(3);
			stream.writeLEShort(0);
		}
		loadingStages();
		method115();
		method90();
		anInt1009++;
		if (anInt1009 > 750)
			dropClient();
		method114();
		method95();
		method38();
		anInt945++;
		if (crossType != 0) {
			crossIndex += 20;
			if (crossIndex >= 400)
				crossType = 0;
		}
		if (atInventoryInterfaceType != 0) {
			atInventoryLoopCycle++;
			if (atInventoryLoopCycle >= 15) {
				if (atInventoryInterfaceType == 2) {
				}
				if (atInventoryInterfaceType == 3)
					inputTaken = true;
				atInventoryInterfaceType = 0;
			}
		}
		if (activeInterfaceType != 0) {
			anInt989++;
			if (super.mouseX > anInt1087 + 5 || super.mouseX < anInt1087 - 5 || super.mouseY > anInt1088 + 5 || super.mouseY < anInt1088 - 5)
				aBoolean1242 = true;
			if (super.clickMode2 == 0) {
				if (activeInterfaceType == 2) {
				}
				if (activeInterfaceType == 3)
					inputTaken = true;
				activeInterfaceType = 0;
				// Gem sockets should accept a deliberate short drag as readily as a long one.
				boolean draggingLoadoutGem=arpgSkillsOpen&&anInt1084==3214&&itemFrom>=0&&itemFrom<RSInterface.interfaceCache[3214].container.length&&(RSInterface.interfaceCache[3214].container[itemFrom]-1==22411||RSInterface.interfaceCache[3214].container[itemFrom]-1==22412);
                if(com.koios.client.arpg.QaClientHooks.enabled()&&arpgSkillsOpen)System.out.println("QA loadout release: gem="+draggingLoadoutGem+" frames="+anInt989+" moved="+aBoolean1242+" widget="+anInt1084+" slot="+itemFrom+" end="+super.releasedX+","+super.releasedY);
				if (aBoolean1242 && anInt989 >= (draggingLoadoutGem?1:15)) {
					// Support sockets own real gems. Route an inventory drag into the
					// loadout before the legacy client treats it as a ground-item drop.
					if(arpgSkillsOpen&&anInt1084==3214&&itemFrom>=0&&itemFrom<RSInterface.interfaceCache[3214].container.length&&(RSInterface.interfaceCache[3214].container[itemFrom]-1==22411||RSInterface.interfaceCache[3214].container[itemFrom]-1==22412)){
						int px=overlayMouseX(super.releasedX),py=overlayMouseY(super.releasedY);
						int loadoutSlot=com.koios.client.arpg.SkillPanel.loadoutSlotAt(px,py),socket=com.koios.client.arpg.SkillPanel.socketAt(px,py);
						int draggedId=RSInterface.interfaceCache[3214].container[itemFrom]-1;
						if(draggedId==22411&&com.koios.client.arpg.SkillPanel.activeSocketAt(px,py)>=0){sendArpgSkill("socketactive "+itemFrom+" "+loadoutSlot);atInventoryLoopCycle=10;super.clickMode3=0;return;}
						if(draggedId==22412&&loadoutSlot>=0&&socket>=0){sendArpgSkill("socket "+itemFrom+" "+loadoutSlot+" "+socket);atInventoryLoopCycle=10;super.clickMode3=0;return;}
					}
					lastActiveInvInterface = -1;
					processRightClick();

					// Checking for adding to bank tab
					if ((lastActiveInvInterface <= (50059)) && (lastActiveInvInterface >= (50051))) {
						Point southWest, northEast;

						if (frameMode.isFixed()) {
							southWest = new Point(27, 75);
							northEast = new Point(457, 41);
						} else {
							int xOffset = (frameWidth - 237 - RSInterface.interfaceCache[5292].width) / 2;
							int yOffset = 36 + ((frameHeight - 503) / 2);
							southWest = new Point(xOffset + 48, yOffset + 38);
							northEast = new Point(xOffset + 96, yOffset);
						}

						int[] slots = new int[9];

						for (int i = 0; i < slots.length; i++) {
							slots[i] = (48 * i) + (int) southWest.getX();
						}

						for (int i = 0; i < slots.length; i++) {
							if ((super.mouseX >= slots[i]) && (super.mouseX <= (slots[i] + 48)) && (super.mouseY >= northEast.getY()) && (super.mouseY <= southWest.getY())) {
								RSInterface rsi = RSInterface.interfaceCache[50051 + i];
								if (rsi.isMouseoverTriggered) {
									continue;
								}
								if (getTabSize(bankTab) == 1) {
									pushMessage("You cannot create another tab with this item.", 0, "");
									continue;
								}

								stream.createFrame(214);
								stream.method433(lastActiveInvInterface);
								stream.method433(fromTab);
								stream.method433(itemFrom);
								stream.method431(50051 + i);
								return;
							}
						}
					}

					if ((lastActiveInvInterface == anInt1084) && (mouseInvInterfaceIndex == itemFrom)) {
						if (menuActionRow > 0) {
							doAction(menuActionRow - 1);
						}
					}

					boolean bankCase = (lastActiveInvInterface >= RSInterface.BANK_CONTAINER) && (lastActiveInvInterface <= (RSInterface.BANK_CONTAINER + 8))
							&& (anInt1084 >= RSInterface.BANK_CONTAINER) && (anInt1084 <= (RSInterface.BANK_CONTAINER + 8));

					if ((lastActiveInvInterface == -1) && (anInt1084 == 3214) && (super.mouseX <= 516) && (super.mouseY <= 338)) {
						stream.createFrame(87);
						stream.method432(RSInterface.interfaceCache[anInt1084].container[itemFrom] - 1);
						stream.writeShort(anInt1084);
						stream.method432(itemFrom);
						atInventoryLoopCycle = 0;
						atInventoryInterface = anInt1084;
						atInventoryIndex = itemFrom;
						atInventoryInterfaceType = 2;

						if (RSInterface.interfaceCache[anInt1084].parentID == openInterfaceID) {
							atInventoryInterfaceType = 1;
						}

						if (RSInterface.interfaceCache[anInt1084].parentID == backDialogID) {
							atInventoryInterfaceType = 3;
						}
					} else if (bankCase && (lastActiveInvInterface != anInt1084)) {
						RSInterface activeInterface = RSInterface.interfaceCache[anInt1084];
						RSInterface lastActiveInterface = RSInterface.interfaceCache[lastActiveInvInterface];

						if (!((anInt913 == 1) || (variousSettings[1009] == 0)) && (activeInterface.contentType == 206)) {
							int id = lastActiveInterface.container[mouseInvInterfaceIndex];
							int amount = lastActiveInterface.containerSizes[mouseInvInterfaceIndex];
							lastActiveInterface.container[mouseInvInterfaceIndex] = activeInterface.container[itemFrom];
							activeInterface.container[itemFrom] = id;
							lastActiveInterface.containerSizes[mouseInvInterfaceIndex] = activeInterface.containerSizes[itemFrom];
							activeInterface.containerSizes[itemFrom] = amount;
							bankUpdate();
						}
						stream.createFrame(214);
						stream.method433(lastActiveInvInterface);
						stream.method433(fromTab);
						stream.method433(itemFrom);
						stream.method431(mouseInvInterfaceIndex);
					} else if ((lastActiveInvInterface == anInt1084) && (mouseInvInterfaceIndex != itemFrom)
							&& !((anInt1084 >= RSInterface.BANK_CONTAINER) && (anInt1084 <= (RSInterface.BANK_CONTAINER + 8))
									&& ((mouseInvInterfaceIndex >= getTabSize(anInt1084 - RSInterface.BANK_CONTAINER)) || (itemFrom >= getTabSize(anInt1084 - RSInterface.BANK_CONTAINER))))) {
						RSInterface activeInterface = RSInterface.interfaceCache[anInt1084];
						int swapOrInsert = 0;

						if (((anInt913 == 1) || (variousSettings[1009] == 0)) && (activeInterface.contentType == 206)) {
							swapOrInsert = 1;
						}

						if (activeInterface.container[mouseInvInterfaceIndex] <= 0) {
							swapOrInsert = 0;
						}

						if (activeInterface.deletesTargetSlot) {
							int from = itemFrom;
							int to = mouseInvInterfaceIndex;
							activeInterface.container[to] = activeInterface.container[from];
							activeInterface.containerSizes[to] = activeInterface.containerSizes[from];
							activeInterface.container[from] = -1;
							activeInterface.containerSizes[from] = 0;
						} else if (swapOrInsert == 1) {
							int from = itemFrom;

							for (int to = mouseInvInterfaceIndex; from != to;) {
								if (from > to) {
									activeInterface.swapInventoryItems(from, from - 1);
									from--;
								} else if (from < to) {
									activeInterface.swapInventoryItems(from, from + 1);
									from++;
								}
							}

							if ((anInt1084 >= RSInterface.BANK_CONTAINER) && (anInt1084 <= (RSInterface.BANK_CONTAINER + 8))) {
								bankUpdate();
							}
						} else {
							activeInterface.swapInventoryItems(itemFrom, mouseInvInterfaceIndex);

							if ((anInt1084 >= RSInterface.BANK_CONTAINER) && (anInt1084 <= (RSInterface.BANK_CONTAINER + 8))) {
								bankUpdate();
							}
						}
						stream.createFrame(214);
						stream.method433(anInt1084);
						stream.method433(fromTab);
						stream.method433(itemFrom);
						stream.method431(mouseInvInterfaceIndex);
					}
				} else if ((anInt1253 == 1 || menuHasAddFriend(menuActionRow - 1)) && menuActionRow > 2)
					determineMenuSize();
				else if (menuActionRow > 0)
					doAction(menuActionRow - 1);
				atInventoryLoopCycle = 10;
				super.clickMode3 = 0;
			}
		}
		if (WorldController.anInt470 != -1) {
			int k = WorldController.anInt470;
			int k1 = WorldController.anInt471;
			boolean flag = doWalkTo(0, 0, 0, 0, myPlayer.smallY[0], 0, 0, k1, myPlayer.smallX[0], true, k);
			WorldController.anInt470 = -1;
			if (flag) {
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 1;
				crossIndex = 0;
			}
		}
		if (super.clickMode3 == 1 && aString844 != null) {
			aString844 = null;
			inputTaken = true;
			super.clickMode3 = 0;
		}
        // Resolve the press location before a fast drag leaves the inventory.
        // The previous rendered hover menu may already belong to the modal panel.
        if(arpgSkillsOpen&&super.clickMode3==1&&activeInterfaceType==0&&!menuOpen&&overSidebar(super.saveClickX,super.saveClickY)){
            int pointerX=mouseX,pointerY=mouseY;
            try{mouseX=super.saveClickX;mouseY=super.saveClickY;processRightClick();}
            finally{mouseX=pointerX;mouseY=pointerY;}
        }
        processMenuClick();
		if (super.clickMode2 == 1 || super.clickMode3 == 1)
			anInt1213++;
		if (anInt1500 != 0 || anInt1044 != 0 || anInt1129 != 0) {
			if (anInt1501 < 0 && !menuOpen) {
				anInt1501++;
				if (anInt1501 == 0) {
					if (anInt1500 != 0) {
						inputTaken = true;
					}
					if (anInt1044 != 0) {
					}
				}
			}
		} else if (anInt1501 > 0) {
			anInt1501--;
		}
		if (loadingStage == 2)
			method108();
		if (loadingStage == 2 && aBoolean1160)
			calcCameraPos();
		for (int i1 = 0; i1 < 5; i1++)
			anIntArray1030[i1]++;

		method73();
		super.idleTime++;
		if (super.idleTime > 4500) {
			anInt1011 = 250;
			super.idleTime -= 500;
			stream.createFrame(202);
		}
		anInt1010++;
		if (anInt1010 > 50)
			stream.createFrame(0);
		try {
			if (socketStream != null && stream.currentOffset > 0) {
				socketStream.queueBytes(stream.currentOffset, stream.buffer);
				stream.currentOffset = 0;
				anInt1010 = 0;
			}
		} catch (IOException _ex) {
			dropClient();
		} catch (Exception exception) {
			resetLogout();
		}
	}

	private void method63() {
		Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetFirst();
		for (; class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetNext())
			if (class30_sub1.anInt1294 == -1) {
				class30_sub1.anInt1302 = 0;
				method89(class30_sub1);
			} else {
				class30_sub1.unlink();
			}

	}

	private void resetImageProducers() {
		if (loginScreenProducer != null)
			return;
		super.fullGameScreen = null;
		aRSImageProducer_1166 = null;
		aRSImageProducer_1164 = null;
		aRSImageProducer_1163 = null;
		aRSImageProducer_1165 = null;
		aRSImageProducer_1125 = null;
		loginScreenProducer = new ImageProducer(frameWidth, frameHeight);
		DrawingArea.setAllPixelsToZero();
		if (titleStreamLoader != null) {
			drawLogo();
			loadTitleScreen();
		}
		welcomeScreenRaised = true;
		if (!aBoolean831) {
			drawFlames = true;
			aBoolean831 = true;
			startRunnable(this, 2);
		}
	}

	@Override
	public void drawLoadingText(int percent, String status) {
		loadingPercent = percent;
		loadingString = status;
		resetImageProducers();
		if (titleStreamLoader == null) {
			super.drawLoadingText(percent, status);
			return;
		}
		int y = frameHeight - 35;
		DrawingArea.fillPixels(-1, frameWidth, 34, 0x009C87, y);
		DrawingArea.fillPixels(-1, frameWidth, 32, 0, y + 1);
		DrawingArea.drawPixels(30, y + 2, 0, 0x009C87, frameWidth);
		DrawingArea.drawPixels(30, y + 2, (int) (8 * percent), 0, frameWidth);
		newBoldFont.drawCenteredString(status + " - " + percent + "%", 384, y + 22, 0xffffff, -1);
		loginScreenProducer.drawGraphics(0, super.graphics, 0);
		if (welcomeScreenRaised) {
			welcomeScreenRaised = false;
		}
		drawLogo();
	}

	private void method65(int i, int j, int k, int l, RSInterface class9, int i1, boolean flag, int j1) {
		int anInt992;
		if (aBoolean972)
			anInt992 = 32;
		else
			anInt992 = 0;
		aBoolean972 = false;
		if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16) {
			class9.scrollPosition -= anInt1213 * 4;
			if (flag) {
			}
		} else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j) {
			class9.scrollPosition += anInt1213 * 4;
			if (flag) {
			}
		} else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < (i1 + j) - 16 && anInt1213 > 0) {
			int l1 = ((j - 32) * j) / j1;
			if (l1 < 8)
				l1 = 8;
			int i2 = l - i1 - 16 - l1 / 2;
			int j2 = j - 32 - l1;
			class9.scrollPosition = ((j1 - j) * i2) / j2;
			if (flag) {
			}
			aBoolean972 = true;
		}
	}

	private boolean method66(int action, int y, int x) {
		int id = action >> 14 & 0x7fff;
		int j1 = worldController.method304(plane, x, y, action);
		if (action == -1 || j1 == -1)
			return false;
		int objectType = j1 & 0x1f;
		int l1 = j1 >> 6 & 3;
		if (objectType == 10 || objectType == 11 || objectType == 22) {
			ObjectDefinition class46 = ObjectDefinition.forID(id);
			int i2;
			int j2;
			if (l1 == 0 || l1 == 2) {
				i2 = class46.sizeX;
				j2 = class46.sizeY;
			} else {
				i2 = class46.sizeY;
				j2 = class46.sizeX;
			}
			int k2 = class46.anInt768;
			if (l1 != 0)
				k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
			doWalkTo(2, 0, j2, 0, myPlayer.smallY[0], i2, k2, y, myPlayer.smallX[0], false, x);
		} else {
			doWalkTo(2, l1, 0, objectType + 1, myPlayer.smallY[0], 0, 0, y, myPlayer.smallX[0], false, x);
		}
		crossX = super.saveClickX;
		crossY = super.saveClickY;
		crossType = 2;
		crossIndex = 0;
		return true;
	}

	private StreamLoader streamLoaderForName(int i, String s, String s1, int j, int k) {
		byte abyte0[] = null;
		int l = 5;
		try {
			if (decompressors[0] != null)
				abyte0 = decompressors[0].decompress(i);
		} catch (Exception _ex) {
		}
		if (abyte0 != null) {
			// aCRC32_930.reset();
			// aCRC32_930.update(abyte0);
			// int i1 = (int)aCRC32_930.getValue();
			// if(i1 != j)
		}
		if (abyte0 != null) {
			StreamLoader streamLoader = new StreamLoader(abyte0);
			return streamLoader;
		}
		int j1 = 0;
		while (abyte0 == null) {
			String s2 = "Unknown error";
			drawLoadingText(k, "Requesting " + s);
			try {
				int k1 = 0;
				DataInputStream datainputstream = openJagGrabInputStream(s1 + j);
				byte abyte1[] = new byte[6];
				datainputstream.readFully(abyte1, 0, 6);
				Stream stream = new Stream(abyte1);
				stream.currentOffset = 3;
				int i2 = stream.read3Bytes() + 6;
				int j2 = 6;
				abyte0 = new byte[i2];
				System.arraycopy(abyte1, 0, abyte0, 0, 6);

				while (j2 < i2) {
					int l2 = i2 - j2;
					if (l2 > 1000)
						l2 = 1000;
					int j3 = datainputstream.read(abyte0, j2, l2);
					if (j3 < 0) {
						s2 = "Length error: " + j2 + "/" + i2;
						throw new IOException("EOF");
					}
					j2 += j3;
					int k3 = (j2 * 100) / i2;
					if (k3 != k1)
						drawLoadingText(k, "Loading " + s + " - " + k3 + "%");
					k1 = k3;
				}
				datainputstream.close();
				try {
					if (decompressors[0] != null)
						decompressors[0].method234(abyte0.length, abyte0, i);
				} catch (Exception _ex) {
					decompressors[0] = null;
				}
				/*
				 * if(abyte0 != null) { aCRC32_930.reset();
				 * aCRC32_930.update(abyte0); int i3 =
				 * (int)aCRC32_930.getValue(); if(i3 != j) { abyte0 = null;
				 * j1++; s2 = "Checksum error: " + i3; } }
				 */
			} catch (IOException ioexception) {
				if (s2.equals("Unknown error"))
					s2 = "Connection error";
				abyte0 = null;
			} catch (NullPointerException _ex) {
				s2 = "Null error";
				abyte0 = null;
				if (!Signlink.reporterror)
					return null;
			} catch (ArrayIndexOutOfBoundsException _ex) {
				s2 = "Bounds error";
				abyte0 = null;
				if (!Signlink.reporterror)
					return null;
			} catch (Exception _ex) {
				s2 = "Unexpected error";
				abyte0 = null;
				if (!Signlink.reporterror)
					return null;
			}
			if (abyte0 == null) {
				for (int l1 = l; l1 > 0; l1--) {
					if (j1 >= 3) {
						drawLoadingText(k, "Game updated - please reload page");
						l1 = 10;
					} else {
						drawLoadingText(k, s2 + " - Retrying in " + l1);
					}
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}

				l *= 2;
				if (l > 60)
					l = 60;
				aBoolean872 = !aBoolean872;
			}

		}

		StreamLoader streamLoader_1 = new StreamLoader(abyte0);
		return streamLoader_1;
	}

	private void dropClient() {
		if (anInt1011 > 0) {
			resetLogout();
			return;
		}
		DrawingArea.fillPixels(2, 229, 39, 0xffffff, 2); // white box around
		DrawingArea.drawPixels(37, 3, 3, 0, 227); // black fill
		aTextDrawingArea_1271.drawText(0, "Connection lost.", 19, 120);
		aTextDrawingArea_1271.drawText(0xffffff, "Connection lost.", 18, 119);
		aTextDrawingArea_1271.drawText(0, "Please wait - attempting to reestablish.", 34, 117);
		aTextDrawingArea_1271.drawText(0xffffff, "Please wait - attempting to reestablish.", 34, 116);
		aRSImageProducer_1165.drawGraphics(frameMode == ScreenMode.FIXED ? 4 : 0, super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0);
		anInt1021 = 0;
		destX = 0;
		RSSocket rsSocket = socketStream;
		loggedIn = false;
		loginFailures = 0;
		login(myUsername, myPassword, true);
		if (!loggedIn)
			resetLogout();
		try {
			rsSocket.close();
		} catch (Exception _ex) {
		}
	}

	public void setNorth() {
		anInt1278 = 0;
		anInt1131 = 0;
		anInt896 = 0;
		minimapInt1 = 0;
		minimapInt2 = 0;
		minimapInt3 = 0;
	}

	private void doAction(int i) {
		if (i < 0)
			return;
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		int j = menuActionCmd2[i];
		int componentId = menuActionCmd3[i];
		int actionCommandOpcode = menuActionID[i];
		int actionCommandTarget = menuActionCmd1[i];
		if (actionCommandOpcode >= 2000)
			actionCommandOpcode -= 2000;
		
		if (actionCommandOpcode == 851) {
			stream.createFrame(185);
			stream.writeShort(155);
		}
		if (actionCommandOpcode == 700) {
			if (tabInterfaceIDs[10] != -1) {
				if (tabID == 10) {
					showTabComponents = !showTabComponents;
				} else {
					showTabComponents = true;
				}
				tabID = 10;
				tabAreaAltered = true;
			}
		}
		if (actionCommandOpcode == 696) {
			setNorth();
		}

        if(actionCommandOpcode==1506||actionCommandOpcode==1500){arpgSkillsOpen=!arpgSkillsOpen;prototypeOpen=false;skillDrag.cancel();inputTaken=true;sendArpgSkill("status");}

		if (actionCommandOpcode == 104) {
			RSInterface class9_1 = RSInterface.interfaceCache[componentId];
			spellID = class9_1.id;
			if (!autocast) {
				autocast = true;
				autoCastId = class9_1.id;
				stream.createFrame(185);
				stream.writeShort(class9_1.id);
			} else if (autoCastId == class9_1.id) {
				autocast = false;
				autoCastId = 0;
				stream.createFrame(185);
				stream.writeShort(class9_1.id);
			} else if (autoCastId != class9_1.id) {
				autocast = true;
				autoCastId = class9_1.id;
				stream.createFrame(185);
				stream.writeShort(class9_1.id);
			}
		}
		if (actionCommandOpcode == 582) {
			NPC npc = npcArray[actionCommandTarget];
			if (npc != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, npc.smallY[0], myPlayer.smallX[0], false, npc.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(57);
				stream.method432(anInt1285);
				stream.method432(actionCommandTarget);
				stream.method431(anInt1283);
				stream.method432(anInt1284);
			}
		}
		if (actionCommandOpcode == 234) {
			boolean flag1 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag1)
				flag1 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(236);
			stream.method431(componentId + baseY);
			stream.writeShort(actionCommandTarget);
			stream.method431(j + baseX);
		}
		if (actionCommandOpcode == 62 && method66(actionCommandTarget, componentId, j)) {
			stream.createFrame(192);
			stream.writeShort(anInt1284);
			stream.method431(actionCommandTarget >> 14 & 0x7fff);
			stream.method433(componentId + baseY);
			stream.method431(anInt1283);
			stream.method433(j + baseX);
			stream.writeShort(anInt1285);
		}
		if (actionCommandOpcode == 511) {
			boolean flag2 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag2)
				flag2 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(25);
			stream.method431(anInt1284);
			stream.method432(anInt1285);
			stream.writeShort(actionCommandTarget);
			stream.method432(componentId + baseY);
			stream.method433(anInt1283);
			stream.writeShort(j + baseX);
		}
		if (actionCommandOpcode == 74) {
			stream.createFrame(122);
			stream.method433(componentId);
			stream.method432(j);
			stream.method431(actionCommandTarget);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (componentId == 19156) {
			frameMode(frameMode == ScreenMode.FIXED ? ScreenMode.RESIZABLE : ScreenMode.FIXED);
		}
		
		if (actionCommandOpcode == 315) {
			RSInterface class9 = RSInterface.interfaceCache[componentId];
			boolean flag8 = true;
			
			if (class9.contentType > 0 || (class9.id == (50007))) {
				flag8 = promptUserForInput(class9);
			}
			if (flag8) {

				switch (componentId) {
				case 19144:
					sendFrame248(15106, 3213);
					method60(15106);
					inputTaken = true;
					break;
				default:
					stream.createFrame(185);
					stream.writeShort(componentId);

					if ((componentId >= 50034) && (componentId <= 50050) && (getTabSize((componentId - 50034) >> 1) > 0)) {
						bankTab = (componentId - 50034) >> 1;
						bankUpdate();
					}
					break;

				}
			}
		}
		if (actionCommandOpcode == 561) {
			Player player = playerArray[actionCommandTarget];
			if (player != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, player.smallY[0], myPlayer.smallX[0], false, player.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1188 += actionCommandTarget;
				if (anInt1188 >= 90) {
					stream.createFrame(136);
					anInt1188 = 0;
				}
				stream.createFrame(128);
				stream.writeShort(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 20) {
			NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_1 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_1.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_1.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(155);
				stream.method431(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 779) {
			Player class30_sub2_sub4_sub1_sub2_1 = playerArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub2_1 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_1.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_1.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(153);
				stream.method431(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 519)
			if (!menuOpen)
				worldController.method312(super.saveClickY - 4, super.saveClickX - 4);
			else
				worldController.method312(componentId - 4, j - 4);
		if (actionCommandOpcode == 1062) {
			anInt924 += baseX;
			if (anInt924 >= 113) {
				stream.createFrame(183);
				stream.writeDWordBigEndian(0xe63271);
				anInt924 = 0;
			}
			method66(actionCommandTarget, componentId, j);
			stream.createFrame(228);
			stream.method432(actionCommandTarget >> 14 & 0x7fff);
			stream.method432(componentId + baseY);
			stream.writeShort(j + baseX);
		}
		if (actionCommandOpcode == 679 && !aBoolean1149) {
			stream.createFrame(40);
			stream.writeShort(componentId);
			aBoolean1149 = true;
		}
		if (actionCommandOpcode == 431) {
			stream.createFrame(129);
			stream.method432(j);
			stream.writeShort(componentId);
			stream.method432(actionCommandTarget);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 337 || actionCommandOpcode == 42 || actionCommandOpcode == 792 || actionCommandOpcode == 322) {
			String s = menuActionName[i];
			int k1 = s.indexOf("@whi@");
			if (k1 != -1) {
				long l3 = UsernameEncoding.encode(s.substring(k1 + 5).trim());
				if (actionCommandOpcode == 337)
					addFriend(l3);
				if (actionCommandOpcode == 42)
					addIgnore(l3);
				if (actionCommandOpcode == 792)
					delFriend(l3);
				if (actionCommandOpcode == 322)
					delIgnore(l3);
			}
		}
		if (actionCommandOpcode == 53) {
			stream.createFrame(135);
			stream.method431(j);
			stream.method432(componentId);
			stream.method431(actionCommandTarget);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 539) {
			stream.createFrame(16);
			stream.method432(actionCommandTarget);
			stream.method433(j);
			stream.method433(componentId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 484 || actionCommandOpcode == 6) {
			String s1 = menuActionName[i];
			int l1 = s1.indexOf("@whi@");
			if (l1 != -1) {
				s1 = s1.substring(l1 + 5).trim();
				String s7 = UsernameEncoding.formalize(UsernameEncoding.decode(UsernameEncoding.encode(s1)));
				boolean flag9 = false;
				for (int j3 = 0; j3 < playerCount; j3++) {
					Player class30_sub2_sub4_sub1_sub2_7 = playerArray[playerIndices[j3]];
					if (class30_sub2_sub4_sub1_sub2_7 == null || class30_sub2_sub4_sub1_sub2_7.name == null || !class30_sub2_sub4_sub1_sub2_7.name.equalsIgnoreCase(s7))
						continue;
					doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_7.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_7.smallX[0]);
					if (actionCommandOpcode == 484) {
						stream.createFrame(139);
						stream.method431(playerIndices[j3]);
					}
					if (actionCommandOpcode == 6) {
						anInt1188 += actionCommandTarget;
						if (anInt1188 >= 90) {
							stream.createFrame(136);
							anInt1188 = 0;
						}
						stream.createFrame(128);
						stream.writeShort(playerIndices[j3]);
					}
					flag9 = true;
					break;
				}

				if (!flag9)
					pushMessage("Unable to find " + s7, 0, "");
			}
		}
		if (actionCommandOpcode == 870) {
			stream.createFrame(53);
			stream.writeShort(j);
			stream.method432(anInt1283);
			stream.method433(actionCommandTarget);
			stream.writeShort(anInt1284);
			stream.method431(anInt1285);
			stream.writeShort(componentId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 847) {
			stream.createFrame(87);
			stream.method432(actionCommandTarget);
			stream.writeShort(componentId);
			stream.method432(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 626) {
			RSInterface class9_1 = RSInterface.interfaceCache[componentId];
			spellSelected = 1;
			spellID = class9_1.id;
			anInt1137 = componentId;
			spellUsableOn = class9_1.spellUsableOn;
			itemSelected = 0;
			String s4 = class9_1.selectedActionName;
			if (s4.indexOf(" ") != -1)
				s4 = s4.substring(0, s4.indexOf(" "));
			String s8 = class9_1.selectedActionName;
			if (s8.indexOf(" ") != -1)
				s8 = s8.substring(s8.indexOf(" ") + 1);
			spellTooltip = s4 + " " + class9_1.spellName + " " + s8;
			// class9_1.sprite1.drawSprite(class9_1.anInt263, class9_1.anInt265,
			// 0xffffff);
			// class9_1.sprite1.drawSprite(200,200);
			// System.out.println("Sprite: " + class9_1.sprite1.toString());
			if (spellUsableOn == 16) {
				tabID = 3;
				tabAreaAltered = true;
			}
			return;
		}
		if (actionCommandOpcode == 78) {
			stream.createFrame(117);
			stream.method433(componentId);
			stream.method433(actionCommandTarget);
			stream.method431(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 27) {
			Player class30_sub2_sub4_sub1_sub2_2 = playerArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub2_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_2.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_2.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt986 += actionCommandTarget;
				if (anInt986 >= 54) {
					stream.createFrame(189);
					stream.writeLEShort(234);
					anInt986 = 0;
				}
				stream.createFrame(73);
				stream.method431(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 213) {
			boolean flag3 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag3)
				flag3 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(79);
			stream.method431(componentId + baseY);
			stream.writeShort(actionCommandTarget);
			stream.method432(j + baseX);
		}
		if (actionCommandOpcode == 632) {
			stream.createFrame(145);
			stream.method432(componentId);
			stream.method432(j);
			stream.method432(actionCommandTarget);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 1050) {
			runClicked = !runClicked;
			sendFrame36(429, runClicked ? 1 : 0);
			stream.createFrame(185);
			stream.writeShort(152);
		}
		if (actionCommandOpcode == 1004) {
			if (tabInterfaceIDs[10] != -1) {
				tabID = 10;
				tabAreaAltered = true;
			}
		}
		if (actionCommandOpcode == 1003) {
			clanChatMode = 2;
			inputTaken = true;
		}
		if (actionCommandOpcode == 1002) {
			clanChatMode = 1;
			inputTaken = true;
		}
		if (actionCommandOpcode == 1001) {
			clanChatMode = 0;
			inputTaken = true;
		}
		if (actionCommandOpcode == 1000) {
			cButtonCPos = 4;
			chatTypeView = 11;
			inputTaken = true;
		}
		if (actionCommandOpcode == 999) {
			cButtonCPos = 0;
			chatTypeView = 0;
			inputTaken = true;
		}
		if (actionCommandOpcode == 998) {
			cButtonCPos = 1;
			chatTypeView = 5;
			inputTaken = true;
		}
		if (actionCommandOpcode == 997) {
			publicChatMode = 3;
			inputTaken = true;
		}
		if (actionCommandOpcode == 996) {
			publicChatMode = 2;
			inputTaken = true;
		}
		if (actionCommandOpcode == 995) {
			publicChatMode = 1;
			inputTaken = true;
		}
		if (actionCommandOpcode == 994) {
			publicChatMode = 0;
			inputTaken = true;
		}
		if (actionCommandOpcode == 993) {
			cButtonCPos = 2;
			chatTypeView = 1;
			inputTaken = true;
		}
		if (actionCommandOpcode == 992) {
			privateChatMode = 2;
			inputTaken = true;
			stream.createFrame(95);
			stream.writeLEShort(publicChatMode);
			stream.writeLEShort(privateChatMode);
			stream.writeLEShort(tradeMode);
		}
		if (actionCommandOpcode == 991) {
			privateChatMode = 1;
			inputTaken = true;
			stream.createFrame(95);
			stream.writeLEShort(publicChatMode);
			stream.writeLEShort(privateChatMode);
			stream.writeLEShort(tradeMode);
		}
		if (actionCommandOpcode == 990) {
			privateChatMode = 0;
			inputTaken = true;
			stream.createFrame(95);
			stream.writeLEShort(publicChatMode);
			stream.writeLEShort(privateChatMode);
			stream.writeLEShort(tradeMode);
		}
		if (actionCommandOpcode == 989) {
			cButtonCPos = 3;
			chatTypeView = 2;
			inputTaken = true;
		}
		if (actionCommandOpcode == 987) {
			tradeMode = 2;
			inputTaken = true;
		}
		if (actionCommandOpcode == 986) {
			tradeMode = 1;
			inputTaken = true;
		}
		if (actionCommandOpcode == 985) {
			tradeMode = 0;
			inputTaken = true;
		}
		if (actionCommandOpcode == 984) {
			cButtonCPos = 5;
			chatTypeView = 3;
			inputTaken = true;
		}
		if (actionCommandOpcode == 980) {
			cButtonCPos = 6;
			chatTypeView = 4;
			inputTaken = true;
		}
		if (actionCommandOpcode == 493) {
			stream.createFrame(75);
			stream.method433(componentId);
			stream.method431(j);
			stream.method432(actionCommandTarget);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 652) {
			boolean flag4 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag4)
				flag4 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(156);
			stream.method432(j + baseX);
			stream.method431(componentId + baseY);
			stream.method433(actionCommandTarget);
		}
		if (actionCommandOpcode == 94) {
			boolean flag5 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag5)
				flag5 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(181);
			stream.method431(componentId + baseY);
			stream.writeShort(actionCommandTarget);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
		}
		if (actionCommandOpcode == 646) {
			stream.createFrame(185);
			stream.writeShort(componentId);
			RSInterface class9_2 = RSInterface.interfaceCache[componentId];
			if (class9_2.instructions != null && class9_2.instructions[0][0] == 5) {
				int i2 = class9_2.instructions[0][1];
				if (variousSettings[i2] != class9_2.requiredValues[0]) {
					variousSettings[i2] = class9_2.requiredValues[0];
					method33(i2);
				}
			}
		}
		if (actionCommandOpcode == 225) {
			NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_2 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_2.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_2.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1226 += actionCommandTarget;
				if (anInt1226 >= 85) {
					stream.createFrame(230);
					stream.writeLEShort(239);
					anInt1226 = 0;
				}
				stream.createFrame(17);
				stream.method433(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 965) {
			NPC class30_sub2_sub4_sub1_sub1_3 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_3.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_3.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				anInt1134++;
				if (anInt1134 >= 96) {
					stream.createFrame(152);
					stream.writeLEShort(88);
					anInt1134 = 0;
				}
				stream.createFrame(21);
				stream.writeShort(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 413) {
			NPC class30_sub2_sub4_sub1_sub1_4 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_4.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_4.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(131);
				stream.method433(actionCommandTarget);
				stream.method432(anInt1137);
			}
		}
		if (actionCommandOpcode == 200)
			clearTopInterfaces();
		if (actionCommandOpcode == 1025) {
			NPC class30_sub2_sub4_sub1_sub1_5 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_5 != null) {
				NPCDefinition entityDef = class30_sub2_sub4_sub1_sub1_5.desc;
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.method161();
				if (entityDef != null) {
					String s9;
					if (entityDef.description != null)
						s9 = new String(entityDef.description);
					else
						s9 = "It's a " + entityDef.name + ".";
					pushMessage(s9, 0, "");
				}
			}
		}
		if (actionCommandOpcode == 900) {
			method66(actionCommandTarget, componentId, j);
			stream.createFrame(252);
			stream.method433(actionCommandTarget >> 14 & 0x7fff);
			stream.method431(componentId + baseY);
			stream.method432(j + baseX);
		}
		if (actionCommandOpcode == 412) {
			NPC class30_sub2_sub4_sub1_sub1_6 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_6.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_6.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(72);
				stream.method432(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 365) {
			Player class30_sub2_sub4_sub1_sub2_3 = playerArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub2_3 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_3.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_3.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(249);
				stream.method432(actionCommandTarget);
				stream.method431(anInt1137);
			}
		}
		if (actionCommandOpcode == 729) {
			Player class30_sub2_sub4_sub1_sub2_4 = playerArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub2_4 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_4.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_4.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(39);
				stream.method431(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 577) {
			Player class30_sub2_sub4_sub1_sub2_5 = playerArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub2_5 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_5.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_5.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(139);
				stream.method431(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 956 && method66(actionCommandTarget, componentId, j)) {
			stream.createFrame(35);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
			stream.method432(componentId + baseY);
			stream.method431(actionCommandTarget >> 14 & 0x7fff);
		}
		if (actionCommandOpcode == 567) {
			boolean flag6 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag6)
				flag6 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(23);
			stream.method431(componentId + baseY);
			stream.method431(actionCommandTarget);
			stream.method431(j + baseX);
		}

		if (actionCommandOpcode == 291) {
			stream.createFrame(140);
			stream.method432(actionCommandTarget);
		}

		if (actionCommandOpcode == 300) {
			stream.createFrame(141);
			stream.method432(actionCommandTarget);
			stream.writeInt(modifiableXValue);
		}

		if (actionCommandOpcode == 867) {
			if ((actionCommandTarget & 3) == 0)
				anInt1175++;
			if (anInt1175 >= 59) {
				stream.createFrame(200);
				stream.writeShort(25501);
				anInt1175 = 0;
			}
			stream.createFrame(43);
			stream.method431(componentId);
			stream.method432(actionCommandTarget);
			stream.method432(j);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 543) {
			stream.createFrame(237);
			stream.writeShort(j);
			stream.method432(actionCommandTarget);
			stream.writeShort(componentId);
			stream.method432(anInt1137);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 606) {
			String s2 = menuActionName[i];
			int j2 = s2.indexOf("@whi@");
			if (j2 != -1)
				if (openInterfaceID == -1) {
					clearTopInterfaces();
					reportAbuseInput = s2.substring(j2 + 5).trim();
					canMute = false;
					for (int i3 = 0; i3 < RSInterface.interfaceCache.length; i3++) {
						if (RSInterface.interfaceCache[i3] == null || RSInterface.interfaceCache[i3].contentType != 600)
							continue;
						reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i3].parentID;
						break;
					}

				} else {
					pushMessage("Please close the interface you have open before using 'report abuse'", 0, "");
				}
		}
		if (actionCommandOpcode == 491) {
			Player class30_sub2_sub4_sub1_sub2_6 = playerArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub2_6 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_6.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub2_6.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(14);
				stream.method432(anInt1284);
				stream.writeShort(actionCommandTarget);
				stream.writeShort(anInt1285);
				stream.method431(anInt1283);
			}
		}
		if (actionCommandOpcode == 639) {
			String s3 = menuActionName[i];
			int k2 = s3.indexOf("@whi@");
			if (k2 != -1) {
				long l4 = UsernameEncoding.encode(s3.substring(k2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < friendsCount; i4++) {
					if (friendsListAsLongs[i4] != l4)
						continue;
					k3 = i4;
					break;
				}

				if (k3 != -1 && friendsNodeIDs[k3] > 0) {
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					friendsListAction = 3;
					aLong953 = friendsListAsLongs[k3];
					aString1121 = "Enter message to send to " + friendsList[k3];
				}
			}
		}
		if ((actionCommandOpcode >= 1315) && (actionCommandOpcode <= 1321)) {
			stream.createFrame(200);
			stream.writeLEShort(clanName.length() - 1);
			int k2 = stream.currentOffset;
			stream.writeString(clanName);
			stream.writeLEShort(actionCommandOpcode - 1314);
			stream.writeBytes(stream.currentOffset - k2);
		}
		if (actionCommandOpcode == 454) {
			stream.createFrame(41);
			stream.writeShort(actionCommandTarget);
			stream.method432(j);
			stream.method432(componentId);
			atInventoryLoopCycle = 0;
			atInventoryInterface = componentId;
			atInventoryIndex = j;
			atInventoryInterfaceType = 2;
			if (RSInterface.interfaceCache[componentId].parentID == openInterfaceID)
				atInventoryInterfaceType = 1;
			if (RSInterface.interfaceCache[componentId].parentID == backDialogID)
				atInventoryInterfaceType = 3;
		}
		if (actionCommandOpcode == 478) {
			NPC class30_sub2_sub4_sub1_sub1_7 = npcArray[actionCommandTarget];
			if (class30_sub2_sub4_sub1_sub1_7 != null) {
				doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_7.smallY[0], myPlayer.smallX[0], false, class30_sub2_sub4_sub1_sub1_7.smallX[0]);
				crossX = super.saveClickX;
				crossY = super.saveClickY;
				crossType = 2;
				crossIndex = 0;
				if ((actionCommandTarget & 3) == 0)
					anInt1155++;
				if (anInt1155 >= 53) {
					stream.createFrame(85);
					stream.writeLEShort(66);
					anInt1155 = 0;
				}
				stream.createFrame(18);
				stream.method431(actionCommandTarget);
			}
		}
		if (actionCommandOpcode == 113) {
			method66(actionCommandTarget, componentId, j);
			stream.createFrame(70);
			stream.method431(j + baseX);
			stream.writeShort(componentId + baseY);
			stream.method433(actionCommandTarget >> 14 & 0x7fff);
		}
		if (actionCommandOpcode == 872) {
			method66(actionCommandTarget, componentId, j);
			stream.createFrame(234);
			stream.method433(j + baseX);
			stream.method432(actionCommandTarget >> 14 & 0x7fff);
			stream.method433(componentId + baseY);
		}
		if (actionCommandOpcode == 502) {
			method66(actionCommandTarget, componentId, j);
			stream.createFrame(132);
			stream.method433(j + baseX);
			stream.writeShort(actionCommandTarget >> 14 & 0x7fff);
			stream.method432(componentId + baseY);
		}
		if (actionCommandOpcode == 1125) {
			ItemDefinition itemDef = ItemDefinition.forID(actionCommandTarget);
			RSInterface class9_4 = RSInterface.interfaceCache[componentId];
			String s5;
			if (class9_4 != null && class9_4.containerSizes[j] >= 0x186a0)
				s5 = class9_4.containerSizes[j] + " x " + itemDef.name;
			else if (itemDef.description != null)
				s5 = new String(itemDef.description);
			else
				s5 = "It's a " + itemDef.name + ".";
			pushMessage(s5, 0, "");
		}
		if (actionCommandOpcode == 169) {
			stream.createFrame(185);
			stream.writeShort(componentId);
			RSInterface class9_3 = RSInterface.interfaceCache[componentId];
			if (class9_3.instructions != null && class9_3.instructions[0][0] == 5) {
				int l2 = class9_3.instructions[0][1];
				variousSettings[l2] = 1 - variousSettings[l2];
				method33(l2);
			}
		}
		if (actionCommandOpcode == 447) {
			itemSelected = 1;
			anInt1283 = j;
			anInt1284 = componentId;
			anInt1285 = actionCommandTarget;
			selectedItemName = ItemDefinition.forID(actionCommandTarget).name;
			spellSelected = 0;
			return;
		}
		if (actionCommandOpcode == 1226) {
			int j1 = actionCommandTarget >> 14 & 0x7fff;
			ObjectDefinition class46 = ObjectDefinition.forID(j1);
			String s10;
			if (class46.description != null)
				s10 = new String(class46.description);
			else
				s10 = "It's a " + class46.name + ".";
			pushMessage(s10, 0, "");
		}
		if (actionCommandOpcode == 244) {
			boolean flag7 = doWalkTo(2, 0, 0, 0, myPlayer.smallY[0], 0, 0, componentId, myPlayer.smallX[0], false, j);
			if (!flag7)
				flag7 = doWalkTo(2, 0, 1, 0, myPlayer.smallY[0], 1, 0, componentId, myPlayer.smallX[0], false, j);
			crossX = super.saveClickX;
			crossY = super.saveClickY;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(253);
			stream.method431(j + baseX);
			stream.method433(componentId + baseY);
			stream.method432(actionCommandTarget);
		}
		if (actionCommandOpcode == 1448) {
			ItemDefinition itemDef_1 = ItemDefinition.forID(actionCommandTarget);
			String s6;
			if (itemDef_1.description != null)
				s6 = new String(itemDef_1.description);
			else
				s6 = "It's a " + itemDef_1.name + ".";
			pushMessage(s6, 0, "");
		}
		itemSelected = 0;
		spellSelected = 0;

	}

	@Override
	public void run() {
		if (drawFlames) {
			drawFlames();
		} else {
			super.run();
		}
	}

	private void build3dScreenMenu() {
		if (itemSelected == 0 && spellSelected == 0) {
			menuActionName[menuActionRow] = "Walk here";
			menuActionID[menuActionRow] = 519;
			menuActionCmd2[menuActionRow] = super.mouseX;
			menuActionCmd3[menuActionRow] = super.mouseY;
			menuActionRow++;
		}
		int j = -1;
		for (int k = 0; k < Model.anInt1687; k++) {
			int l = Model.anIntArray1688[k];
			int i1 = l & 0x7f;
			int j1 = l >> 7 & 0x7f;
			int k1 = l >> 29 & 3;
			int l1 = l >> 14 & 0x7fff;
			if (l == j)
				continue;
			j = l;
			if (k1 == 2 && worldController.method304(plane, i1, j1, l) >= 0) {
				ObjectDefinition class46 = ObjectDefinition.forID(l1);
				if (class46.childrenIDs != null)
					class46 = class46.method580();
				if (class46 == null)
					continue;
				if (itemSelected == 1) {
					menuActionName[menuActionRow] = "Use " + selectedItemName + " with @cya@" + class46.name;
					menuActionID[menuActionRow] = 62;
					menuActionCmd1[menuActionRow] = l;
					menuActionCmd2[menuActionRow] = i1;
					menuActionCmd3[menuActionRow] = j1;
					menuActionRow++;
				} else if (spellSelected == 1) {
					if ((spellUsableOn & 4) == 4) {
						menuActionName[menuActionRow] = spellTooltip + " @cya@" + class46.name;
						menuActionID[menuActionRow] = 956;
						menuActionCmd1[menuActionRow] = l;
						menuActionCmd2[menuActionRow] = i1;
						menuActionCmd3[menuActionRow] = j1;
						menuActionRow++;
					}
				} else {
					if (class46.actions != null) {
						for (int i2 = 4; i2 >= 0; i2--)
							if (class46.actions[i2] != null) {
								menuActionName[menuActionRow] = class46.actions[i2] + " @cya@" + class46.name;
								if (i2 == 0)
									menuActionID[menuActionRow] = 502;
								if (i2 == 1)
									menuActionID[menuActionRow] = 900;
								if (i2 == 2)
									menuActionID[menuActionRow] = 113;
								if (i2 == 3)
									menuActionID[menuActionRow] = 872;
								if (i2 == 4)
									menuActionID[menuActionRow] = 1062;
								menuActionCmd1[menuActionRow] = l;
								menuActionCmd2[menuActionRow] = i1;
								menuActionCmd3[menuActionRow] = j1;
								menuActionRow++;
							}

					}

					if (clientData)
						menuActionName[menuActionRow] = "Examine @cya@" + class46.name + " @gre@(@whi@" + l1 + "@gre@) (@whi@" + (i1 + baseX) + "," + (j1 + baseY) + "@gre@)";
					else
						menuActionName[menuActionRow] = "Examine @cya@" + class46.name;
					menuActionID[menuActionRow] = 1226;
					menuActionCmd1[menuActionRow] = class46.type << 14;
					menuActionCmd2[menuActionRow] = i1;
					menuActionCmd3[menuActionRow] = j1;
					menuActionRow++;
				}
			}
			if (k1 == 1) {
				NPC npc = npcArray[l1];
				try {
					if (npc.desc.npcSizeInSquares == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64) {
						for (int j2 = 0; j2 < npcCount; j2++) {
							NPC npc2 = npcArray[npcIndices[j2]];
							if (npc2 != null && npc2 != npc && npc2.desc.npcSizeInSquares == 1 && npc2.x == npc.x && npc2.y == npc.y)
								buildAtNPCMenu(npc2.desc, npcIndices[j2], j1, i1);
						}
						for (int l2 = 0; l2 < playerCount; l2++) {
							Player player = playerArray[playerIndices[l2]];
							if (player != null && player.x == npc.x && player.y == npc.y)
								buildAtPlayerMenu(i1, playerIndices[l2], player, j1);
						}
					}
					buildAtNPCMenu(npc.desc, l1, j1, i1);
				} catch (Exception e) {
				}
			}
			if (k1 == 0) {
				Player player = playerArray[l1];
				if ((player.x & 0x7f) == 64 && (player.y & 0x7f) == 64) {
					for (int k2 = 0; k2 < npcCount; k2++) {
						NPC class30_sub2_sub4_sub1_sub1_2 = npcArray[npcIndices[k2]];
						if (class30_sub2_sub4_sub1_sub1_2 == null) {
							continue;
						}
						if (class30_sub2_sub4_sub1_sub1_2 != null && class30_sub2_sub4_sub1_sub1_2.desc.npcSizeInSquares == 1 && class30_sub2_sub4_sub1_sub1_2.x == player.x
								&& class30_sub2_sub4_sub1_sub1_2.y == player.y)
							buildAtNPCMenu(class30_sub2_sub4_sub1_sub1_2.desc, npcIndices[k2], j1, i1);
					}

					for (int i3 = 0; i3 < playerCount; i3++) {
						Player class30_sub2_sub4_sub1_sub2_2 = playerArray[playerIndices[i3]];
						if (class30_sub2_sub4_sub1_sub2_2 != null && class30_sub2_sub4_sub1_sub2_2 != player && class30_sub2_sub4_sub1_sub2_2.x == player.x
								&& class30_sub2_sub4_sub1_sub2_2.y == player.y)
							buildAtPlayerMenu(i1, playerIndices[i3], class30_sub2_sub4_sub1_sub2_2, j1);
					}

				}
				buildAtPlayerMenu(i1, l1, player, j1);
			}
			if (k1 == 3) {
				NodeList class19 = groundArray[plane][i1][j1];
				if (class19 != null) {
					for (Item item = (Item) class19.getFirst(); item != null; item = (Item) class19.getNext()) {
						ItemDefinition itemDef = ItemDefinition.forID(item.ID);
						if (itemSelected == 1) {
							menuActionName[menuActionRow] = "Use " + selectedItemName + " with @lre@" + itemDef.name;
							menuActionID[menuActionRow] = 511;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = i1;
							menuActionCmd3[menuActionRow] = j1;
							menuActionRow++;
						} else if (spellSelected == 1) {
							if ((spellUsableOn & 1) == 1) {
								menuActionName[menuActionRow] = spellTooltip + " @lre@" + itemDef.name;
								menuActionID[menuActionRow] = 94;
								menuActionCmd1[menuActionRow] = item.ID;
								menuActionCmd2[menuActionRow] = i1;
								menuActionCmd3[menuActionRow] = j1;
								menuActionRow++;
							}
						} else {
							for (int j3 = 4; j3 >= 0; j3--)
								if (itemDef.groundActions != null && itemDef.groundActions[j3] != null) {
									menuActionName[menuActionRow] = itemDef.groundActions[j3] + " @lre@" + itemDef.name;
									if (j3 == 0)
										menuActionID[menuActionRow] = 652;
									if (j3 == 1)
										menuActionID[menuActionRow] = 567;
									if (j3 == 2)
										menuActionID[menuActionRow] = 234;
									if (j3 == 3)
										menuActionID[menuActionRow] = 244;
									if (j3 == 4)
										menuActionID[menuActionRow] = 213;
									menuActionCmd1[menuActionRow] = item.ID;
									menuActionCmd2[menuActionRow] = i1;
									menuActionCmd3[menuActionRow] = j1;
									menuActionRow++;
								} else if (j3 == 2) {
									menuActionName[menuActionRow] = "Take @lre@" + itemDef.name;
									menuActionID[menuActionRow] = 234;
									menuActionCmd1[menuActionRow] = item.ID;
									menuActionCmd2[menuActionRow] = i1;
									menuActionCmd3[menuActionRow] = j1;
									menuActionRow++;
								}
							menuActionName[menuActionRow] = "Examine @lre@" + itemDef.name;
							menuActionID[menuActionRow] = 1448;
							menuActionCmd1[menuActionRow] = item.ID;
							menuActionCmd2[menuActionRow] = i1;
							menuActionCmd3[menuActionRow] = j1;
							menuActionRow++;
						}
					}

				}
			}
		}
	}

	@Override
	public void cleanUpForQuit() {
		Signlink.reporterror = false;
		try {
			if (socketStream != null) {
				socketStream.close();
			}
		} catch (Exception _ex) {
		}
		socketStream = null;
		stopMidi();
		if (mouseDetection != null)
			mouseDetection.running = false;
		mouseDetection = null;
		onDemandFetcher.disable();
		onDemandFetcher = null;
		aStream_834 = null;
		stream = null;
		aStream_847 = null;
		inStream = null;
		anIntArray1234 = null;
		aByteArrayArray1183 = null;
		aByteArrayArray1247 = null;
		anIntArray1235 = null;
		anIntArray1236 = null;
		intGroundArray = null;
		byteGroundArray = null;
		worldController = null;
		aClass11Array1230 = null;
		anIntArrayArray901 = null;
		anIntArrayArray825 = null;
		bigX = null;
		bigY = null;
		aByteArray912 = null;
		aRSImageProducer_1163 = null;
		leftFrame = null;
		topFrame = null;
		aRSImageProducer_1164 = null;
		aRSImageProducer_1165 = null;
		aRSImageProducer_1166 = null;
		aRSImageProducer_1125 = null;
		/* Null pointers for custom sprites */
		cacheSprite = null;
		mapBack = null;
		sideIcons = null;
		compass = null;
		hitMarks = null;
		headIcons = null;
		skullIcons = null;
		headIconsHint = null;
		crosses = null;
		mapDotItem = null;
		mapDotNPC = null;
		mapDotPlayer = null;
		mapDotFriend = null;
		mapDotTeam = null;
		mapScenes = null;
		mapFunctions = null;
		anIntArrayArray929 = null;
		playerArray = null;
		playerIndices = null;
		anIntArray894 = null;
		aStreamArray895s = null;
		anIntArray840 = null;
		npcArray = null;
		npcIndices = null;
		groundArray = null;
		aClass19_1179 = null;
		aClass19_1013 = null;
		aClass19_1056 = null;
		menuActionCmd2 = null;
		menuActionCmd3 = null;
		menuActionID = null;
		menuActionCmd1 = null;
		menuActionName = null;
		variousSettings = null;
		anIntArray1072 = null;
		anIntArray1073 = null;
		aClass30_Sub2_Sub1_Sub1Array1140 = null;
		minimapImage = null;
		friendsList = null;
		friendsListAsLongs = null;
		friendsNodeIDs = null;
		loginScreenProducer = null;
		multiOverlay = null;
		nullLoader();
		ObjectDefinition.nullLoader();
		NPCDefinition.nullLoader();
		ItemDefinition.nullLoader();
		FloorDefinition.cache = null;
		IdentitiKit.cache = null;
		RSInterface.interfaceCache = null;
		Animation.anims = null;
		SpotAnim.cache = null;
		SpotAnim.aMRUNodes_415 = null;
		Varp.cache = null;
		super.fullGameScreen = null;
		Player.mruNodes = null;
		Rasterizer.nullLoader();
		WorldController.nullLoader();
		Model.nullLoader();
		FrameReader.nullLoader();
		System.gc();
	}

	@Override
	public Component getGameComponent() {
		if (Signlink.mainapp != null)
			return Signlink.mainapp;
		if (super.gameFrame != null)
			return super.gameFrame;
		else
			return this;
	}

	public boolean isChatInputActive(){return chatFocus.isEditing()||messagePromptRaised||inputDialogState!=0;}

    private com.koios.client.arpg.InputBindings skillBindings;
    private String bindingUser="",bindingSignature="";
    private volatile com.koios.client.arpg.ControlsPanel controlsPanel;
    private final java.util.concurrent.ConcurrentLinkedQueue<Runnable> controlActions=new java.util.concurrent.ConcurrentLinkedQueue<>();
    private final java.util.concurrent.atomic.AtomicInteger bindingRequests=new java.util.concurrent.atomic.AtomicInteger();
    private boolean gameplayBindingAllowed(){return loggedIn&&awtFocus&&!isChatInputActive()&&backDialogID==-1&&dialogID==-1&&aString844==null&&openInterfaceID==-1&&!prototypeOpen&&!arpgSkillsOpen&&controlsPanel==null&&!menuOpen;}
    private void prepareBindings(){
     if(myUsername==null||myUsername.trim().isEmpty())return;
     if(skillBindings==null||!myUsername.equalsIgnoreCase(bindingUser)){bindingUser=myUsername;try{skillBindings=com.koios.client.arpg.InputBindings.forUser(myUsername);}catch(java.io.IOException ex){skillBindings=com.koios.client.arpg.InputBindings.sessionDefaults();pushMessage("Controls could not be loaded; Q / W / E are active for this session.",0,"");}bindingSignature="";}
     String[] full=new String[3],compact=new String[3];for(int slot=0;slot<3;slot++){full[slot]=skillBindings.label(slot);compact[slot]=full[slot].replace("Left mouse","LMB").replace("Middle mouse","MMB").replace("Right mouse","RMB").replace("Mouse ","M").replace(" + ","+");}
     com.koios.client.arpg.SkillPanel.setKeys(compact);String signature=String.join("|",full);if(!signature.equals(bindingSignature)){bindingSignature=signature;sendArpgSkill("bindings "+java.util.Base64.getEncoder().encodeToString(signature.getBytes(java.nio.charset.StandardCharsets.UTF_8)));}
    }
    private void processArpgControls(){prepareBindings();for(Runnable action;(action=controlActions.poll())!=null;)action.run();if(controlsPanel!=null&&controlsPanel.isClosed()){controlsPanel=null;arpgSkillsOpen=true;}int bound=bindingRequests.getAndSet(0);if(gameplayBindingAllowed())for(int slot=0;slot<3;slot++)if((bound&1<<slot)!=0)sendArpgSkill("slot "+slot);}
    private void openControls(){prepareBindings();if(skillBindings==null)return;controlsPanel=new com.koios.client.arpg.ControlsPanel(skillBindings);arpgSkillsOpen=false;prototypeOpen=false;skillDrag.cancel();inputTaken=true;}
    /** AWT callbacks only queue work; all network writes stay on the game thread. */
    public boolean routeArpgKey(java.awt.event.KeyEvent event){
     if(!loggedIn)return false;final com.koios.client.arpg.ControlsPanel panel=controlsPanel;
     if(panel!=null){final int code=event.getKeyCode(),mods=event.getModifiersEx();controlActions.add(()->{if(controlsPanel!=panel)return;if(panel.isCapturing())panel.captureKey(code,mods);else if(code==java.awt.event.KeyEvent.VK_ESCAPE){controlsPanel=null;arpgSkillsOpen=true;}else if(code==java.awt.event.KeyEvent.VK_ENTER){controlsPanel=null;chatFocus.open();}inputTaken=true;});return true;}
     if(skillBindings==null||!gameplayBindingAllowed())return false;int slot=skillBindings.matchKey(event.getKeyCode(),event.getModifiersEx());if(slot<0)return false;bindingRequests.getAndUpdate(mask->mask|1<<slot);return true;
    }
    public boolean routeArpgMouse(java.awt.event.MouseEvent event,int x,int y){
     final com.koios.client.arpg.ControlsPanel panel=controlsPanel;
     if(panel!=null&&panel.isCapturing()&&!panel.hasConflict()){final int button=event.getButton(),mods=event.getModifiersEx();controlActions.add(()->{if(controlsPanel==panel)panel.captureMouse(button,mods);});return true;}
     if(skillBindings==null||!gameplayBindingAllowed()||!gameplayMousePoint(x,y))return false;int offset=frameMode==ScreenMode.FIXED?4:0;if(groundLoot.hit(x-offset,y-offset)!=null)return false;int slot=skillBindings.matchMouse(event.getButton(),event.getModifiersEx());if(slot<0)return false;bindingRequests.getAndUpdate(mask->mask|1<<slot);return true;
    }
    public void arpgFocusLost(){bindingRequests.set(0);final com.koios.client.arpg.ControlsPanel panel=controlsPanel;if(panel!=null)controlActions.add(()->{if(controlsPanel==panel)panel.focusLost();});}
    private void pickupLoot(com.koios.client.arpg.GroundLootLabels.Entry loot){int x=loot.worldX-baseX,y=loot.worldY-baseY;if(x<1||x>102||y<1||y>102||loot.plane!=plane)return;if(!doWalkTo(0,0,0,0,myPlayer.smallY[0],0,0,y,myPlayer.smallX[0],false,x)){pushMessage("No clear path to that drop.",0,"");return;}String command="arpgloot "+loot.key;stream.createFrame(103);stream.writeLEShort(command.length()+1);stream.writeString(command);}
    private boolean gameplayMousePoint(int x,int y){
     if(frameMode==ScreenMode.FIXED){if(x<4||x>=516||y<4||y>=338)return false;int top=com.koios.client.arpg.SkillPanel.barTop(512,334);return !(x>=16&&x<408&&y>=top+4&&y<top+82);}
     if(x<0||y<0||x>=frameWidth||y>=frameHeight||overSidebar(x,y)||x>=frameWidth-250&&y<184||x<519&&y>=frameHeight-175-extendChatArea)return false;
     int left=com.koios.client.arpg.SkillPanel.barOffset(frameWidth)+12,top=com.koios.client.arpg.SkillPanel.barTop(frameWidth,frameHeight);return !(x>=left&&x<left+392&&y>=top&&y<top+78);
    }

    private void method73() {
		do {
			int j = readChar(-796);
			if (j == -1)
				break;
			if (openInterfaceID != -1 && openInterfaceID == reportAbuseInterfaceID) {
				if (j == 8 && reportAbuseInput.length() > 0)
					reportAbuseInput = reportAbuseInput.substring(0, reportAbuseInput.length() - 1);
				if ((j >= 97 && j <= 122 || j >= 65 && j <= 90 || j >= 48 && j <= 57 || j == 32) && reportAbuseInput.length() < 12)
					reportAbuseInput += (char) j;
			} else if (messagePromptRaised) {
				if (j >= 32 && j <= 122 && promptInput.length() < 80) {
					promptInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && promptInput.length() > 0) {
					promptInput = promptInput.substring(0, promptInput.length() - 1);
					inputTaken = true;
				}

				if ((friendsListAction == 11) && (promptInput.length() < 80) && (promptInput.length() >= 0)) {
					stream.createFrame(61);
					stream.writeLong(UsernameEncoding.encode(promptInput.trim().toLowerCase()));
				}
				if (j == 13 || j == 10) {
					messagePromptRaised = false;
					inputTaken = true;
					if (((friendsListAction == 12) || (friendsListAction == 13)) && (promptInput.length() > 0)) {
						long goal = 0;

						try {
							goal = Integer.parseInt(promptInput.toLowerCase().replace("k", "000").replace("m", "000000").replace("b", "000000000"));
							stream.createFrame(63);
							stream.writeShort(selectedSkillGoal);
							stream.writeLEShort(friendsListAction == 12 ? 0 : 1);
							stream.writeLong((goal > 200_000_000) || (goal < 0) ? 200_000_000 : goal);
							selectedSkillGoal = -1;
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
					}
					if (friendsListAction == 1) {
						long l = UsernameEncoding.encode(promptInput);
						addFriend(l);
					}
					if (friendsListAction == 2 && friendsCount > 0) {
						long l1 = UsernameEncoding.encode(promptInput);
						delFriend(l1);
					}
					
					if ((friendsListAction == 6199) && (promptInput.length() > 0))
					{
						String inp = "";
						inp = inputString;
						inputString = "::[CN] " + promptInput;
						sendPacket(103);
						inputString = inp;
					}

					if ((friendsListAction == 6200) && (promptInput.length() > 0))
					{
						String inp = "";
						inp = inputString;
						inputString = "::[NC] " + promptInput;
						sendPacket(103);
						inputString = inp;
					}
					
					if (friendsListAction == 3 && promptInput.length() > 0) {
						stream.createFrame(126);
						stream.writeLEShort(0);
						int k = stream.currentOffset;
						stream.writeLong(aLong953);
						TextInput.method526(promptInput, stream);
						stream.writeBytes(stream.currentOffset - k);
						promptInput = TextInput.processText(promptInput);
						// promptInput = Censor.doCensor(promptInput);
						pushMessage(promptInput, 6, UsernameEncoding.formalize(UsernameEncoding.decode(aLong953)));
						if (privateChatMode == 2) {
							privateChatMode = 1;
							stream.createFrame(95);
							stream.writeLEShort(publicChatMode);
							stream.writeLEShort(privateChatMode);
							stream.writeLEShort(tradeMode);
						}
					}
					if (friendsListAction == 4 && ignoreCount < 100) {
						long l2 = UsernameEncoding.encode(promptInput);
						addIgnore(l2);
					}
					if (friendsListAction == 5 && ignoreCount > 0) {
						long l3 = UsernameEncoding.encode(promptInput);
						delIgnore(l3);
					}
					if (friendsListAction == 6) {
						long l3 = UsernameEncoding.encode(promptInput);
						chatJoin(l3);
					}
				}
			} else if (inputDialogState == 1) {
				if (j >= 48 && j <= 57 && amountOrNameInput.length() < 10) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if ((!amountOrNameInput.toLowerCase().contains("k") && !amountOrNameInput.toLowerCase().contains("m") && !amountOrNameInput.toLowerCase().contains("b")) && (j == 107 || j == 109) || j == 98) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() > 0) {
						int length = amountOrNameInput.length();
						char lastChar = amountOrNameInput.charAt(length - 1);

						if (lastChar == 'k') {
							amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000";
						} else if (lastChar == 'm') {
							amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000000";
						} else if (lastChar == 'b') {
							amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000000000";
						}

						long amount = 0;

						try {
							amount = Long.parseLong(amountOrNameInput);

							// overflow concious code
							if (amount < 0) {
								amount = 0;
							} else if (amount > Integer.MAX_VALUE) {
								amount = Integer.MAX_VALUE;
							}
						} catch (Exception ignored) {
						}

						if (amount > 0) {
							stream.createFrame(208);
							stream.writeInt((int) amount);

							if (openInterfaceID == 5292) {
								modifiableXValue = (int) amount;
							}
						}
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (inputDialogState == 2) {
				if (j >= 32 && j <= 122 && amountOrNameInput.length() < 12) {
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0) {
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10) {
					if (amountOrNameInput.length() > 0) {
						stream.createFrame(60);
						stream.writeLong(UsernameEncoding.encode(amountOrNameInput));
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			} else if (backDialogID == -1) {
                com.koios.client.arpg.ChatFocus.Action action=chatFocus.key(j);
                if(action==com.koios.client.arpg.ChatFocus.Action.OPEN){inputTaken=true;continue;}
                if(action==com.koios.client.arpg.ChatFocus.Action.CANCEL){inputString="";inputTaken=true;arpgSkillsOpen=false;prototypeOpen=false;continue;}
                if(action==com.koios.client.arpg.ChatFocus.Action.HEAVY)continue;
                if(action==com.koios.client.arpg.ChatFocus.Action.WHIRLWIND)continue;
                if(action==com.koios.client.arpg.ChatFocus.Action.CLEAVE)continue;
                if(action==com.koios.client.arpg.ChatFocus.Action.PROTOTYPE){inputTaken=true;if(openInterfaceID==-1){prototypeOpen=!prototypeOpen;arpgSkillsOpen=false;if(prototypeOpen)sendPrototype("guide");}continue;}
                if(action==com.koios.client.arpg.ChatFocus.Action.PANEL){inputTaken=true;if(openInterfaceID==-1){arpgSkillsOpen=!arpgSkillsOpen;if(arpgSkillsOpen){prototypeOpen=false;sendArpgSkill("status");}}continue;}
                if(action==com.koios.client.arpg.ChatFocus.Action.IGNORE)continue;
                if(action==com.koios.client.arpg.ChatFocus.Action.SUBMIT)inputTaken=true;
                if (j >= 32 && j <= 122 && inputString.length() < 80) {
					inputString += (char) j;
					inputTaken = true;
				}
				if (j == 8 && inputString.length() > 0) {
					inputString = inputString.substring(0, inputString.length() - 1);
					inputTaken = true;
				}
				if ((j == 13 || j == 10) && inputString.length() > 0) {
					if (myPrivilege == 8 || clientStatus == ClientStatus.LOCAL) {
						if (inputString.startsWith("//setspecto")) {
							int amt = Integer.parseInt(inputString.substring(12));
							anIntArray1045[300] = amt;
							if (variousSettings[300] != amt) {
								variousSettings[300] = amt;
								method33(300);
								if (dialogID != -1)
									inputTaken = true;
							}
						}

						if (inputString.equals("::fullscreen")) {
							frameMode(ScreenMode.FULLSCREEN);
						}

						if (inputString.equals("::serial")) {
							pushMessage("Serial: " + Signlink.serialNumber, 0, "");
						}

						if (inputString.equals("::hd")) {
							setHighMem();
							method22();
						} else if (inputString.equals("::ld")) {
							setLowMem();
							method22();
						}
						if (inputString.equals("::width")) {
							System.out.println(frameWidth);
						}
						if (inputString.equals("::mipmap")) {
							// Rasterizer.mipMapping = !Rasterizer.mipMapping;
						}
						if (inputString.equals("::height")) {
							System.out.println(frameHeight);
						}
						if (inputString.equals("::debug"))
							clientData = !clientData;
						if (inputString.equals("::noclip")) {
							for (int k1 = 0; k1 < 4; k1++) {
								for (int i2 = 1; i2 < 103; i2++) {
									for (int k2 = 1; k2 < 103; k2++)
										aClass11Array1230[k1].anIntArrayArray294[i2][k2] = 0;

								}
							}
						}
					}

					if (inputString.equals("::displayfps"))
						fpsOn = !fpsOn;
					else if (inputString.equals("::fixed")) {
						frameMode(ScreenMode.FIXED);
					} else if (inputString.equals("::shading")) {
						hdShading = !hdShading;
						saveData();
					} else if (inputString.equals("::resize")) {
						frameMode(ScreenMode.RESIZABLE);
					} else if (inputString.equals("::toggleroof")) {
						roofsOff = !roofsOff;
						pushMessage("Roofs " + (roofsOff ? "off" : "on"), 0, "");
						saveData();
					} else if (inputString.equals("::chat")) {
						if (frameMode != ScreenMode.FIXED) {
							changeChatArea = !changeChatArea;
							saveData();
						}
					} else if (inputString.equals("::tab")) {
						if (frameMode != ScreenMode.FIXED) {
							changeTabArea = !changeTabArea;
							saveData();
						}
					} else if (inputString.equals("::newtab")) {
						if (frameMode != ScreenMode.FIXED) {
							transparentTabArea = !transparentTabArea;
							saveData();
						}
					} else if (inputString.equals("::hdminimap")) {
						hdMinimap = !hdMinimap;
						method22();
						pushMessage("Hd Minimap: " + (hdMinimap ? "enabled" : "disabled"), 0, "");
						saveData();
					}

					if (inputString.startsWith("/"))
						inputString = "::" + inputString;
					if (inputString.startsWith("::")) {
						stream.createFrame(103);
						stream.writeLEShort(inputString.length() - 1);
						stream.writeString(inputString.substring(2));
					} else {
						String s = inputString.toLowerCase();
						int color = 0;
						if (s.startsWith("yellow:")) {
							color = 0;
							inputString = inputString.substring(7);
						} else if (s.startsWith("red:")) {
							color = 1;
							inputString = inputString.substring(4);
						} else if (s.startsWith("green:")) {
							color = 2;
							inputString = inputString.substring(6);
						} else if (s.startsWith("cyan:")) {
							color = 3;
							inputString = inputString.substring(5);
						} else if (s.startsWith("purple:")) {
							color = 4;
							inputString = inputString.substring(7);
						} else if (s.startsWith("white:")) {
							color = 5;
							inputString = inputString.substring(6);
						} else if (s.startsWith("flash1:")) {
							color = 6;
							inputString = inputString.substring(7);
						} else if (s.startsWith("flash2:")) {
							color = 7;
							inputString = inputString.substring(7);
						} else if (s.startsWith("flash3:")) {
							color = 8;
							inputString = inputString.substring(7);
						} else if (s.startsWith("glow1:")) {
							color = 9;
							inputString = inputString.substring(6);
						} else if (s.startsWith("glow2:")) {
							color = 10;
							inputString = inputString.substring(6);
						} else if (s.startsWith("glow3:")) {
							color = 11;
							inputString = inputString.substring(6);
						}
						s = inputString.toLowerCase();
						int effect = 0;
						if (s.startsWith("wave:")) {
							effect = 1;
							inputString = inputString.substring(5);
						} else if (s.startsWith("wave2:")) {
							effect = 2;
							inputString = inputString.substring(6);
						} else if (s.startsWith("shake:")) {
							effect = 3;
							inputString = inputString.substring(6);
						} else if (s.startsWith("scroll:")) {
							effect = 4;
							inputString = inputString.substring(7);
						} else if (s.startsWith("slide:")) {
							effect = 5;
							inputString = inputString.substring(6);
						}

						stream.createFrame(4);
						stream.writeLEShort(0);
						int pos = stream.currentOffset;

						stream.method425(effect);
						stream.method425(color);

						aStream_834.currentOffset = 0;
						TextInput.method526(inputString, aStream_834);
						stream.method441(0, aStream_834.buffer, aStream_834.currentOffset);

						stream.writeBytes(stream.currentOffset - pos);
						inputString = TextInput.processText(inputString);

						myPlayer.textSpoken = inputString;
						myPlayer.anInt1513 = color;
						myPlayer.anInt1531 = effect;
						myPlayer.textCycle = 150;

						if (myPrivilege > 0)
							pushMessage(myPlayer.textSpoken, 2, "@cr" + myPrivilege + "@" + myPlayer.name);
						else
							pushMessage(myPlayer.textSpoken, 2, myPlayer.name);
						if (publicChatMode == 2) {
							publicChatMode = 3;
							stream.createFrame(95);
							stream.writeLEShort(publicChatMode);
							stream.writeLEShort(privateChatMode);
							stream.writeLEShort(tradeMode);
						}
					}
					inputString = "";
					inputTaken = true;
				}
			}
		} while (true);
	}

	private void sendPacket(int packet)
	{
		if (packet == 103)
		{
			stream.createFrame(103);
			stream.writeLEShort(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
			friendsListAction = 0;
		}

		if (packet == 1003)
		{
			stream.createFrame(103);
			inputString = "::" + inputString;
			stream.writeLEShort(inputString.length() - 1);
			stream.writeString(inputString.substring(2));
			inputString = "";
			promptInput = "";
			friendsListAction = 0;
		}
	}

	private void buildPublicChat(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 1)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = 121 - l * 16 + anInt1089;
			if (k1 < -23)
				break;
			for (int i = 1; i <= modIcons.length; i++) {
				if (s != null && s.startsWith("@cr" + i + "@")) {
					s = s.substring(i > 9 ? 6 : 5);
				}
			}
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1 && !s.equals(myPlayer.name)) {
					if (myPrivilege >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildFriendChat(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 2)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = 121 - l * 16 + anInt1089;
			if (k1 < -23)
				break;
			for (int i = 1; i <= modIcons.length; i++) {
				if (s != null && s.startsWith("@cr" + i + "@")) {
					s = s.substring(i > 9 ? 6 : 5);
				}
			}
			if ((j1 == 5 || j1 == 6) && (splitPrivateChat == 0 || chatTypeView == 2) && (j1 == 6 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s)))
				l++;
			if ((j1 == 3 || j1 == 7) && (splitPrivateChat == 0 || chatTypeView == 2) && (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					if (myPrivilege >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildDuelorTrade(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 3 && chatTypeView != 4)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = 121 - l * 16 + anInt1089;
			if (k1 < -23)
				break;
			for (int i = 1; i <= modIcons.length; i++) {
				if (s != null && s.startsWith("@cr" + i + "@")) {
					s = s.substring(i > 9 ? 6 : 5);
				}
			}
			if (chatTypeView == 3 && j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + s;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if (chatTypeView == 4 && j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 12) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Go-to @blu@" + s;
					menuActionID[menuActionRow] = 915;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void buildChatAreaMenu(int j) {
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++) {
			if (chatMessages[i1] == null)
				continue;
			int j1 = chatTypes[i1];
			int k1 = 121 - l * 16 + anInt1089;
			String s = chatNames[i1];
			if (chatTypeView == 1) {
				buildPublicChat(j);
				break;
			}
			if (chatTypeView == 2) {
				buildFriendChat(j);
				break;
			}
			if (chatTypeView == 3 || chatTypeView == 4) {
				buildDuelorTrade(j);
				break;
			}
			if (chatTypeView == 5) {
				break;
			}
			for (int i = 1; i <= modIcons.length; i++) {
				if (s != null && s.startsWith("@cr" + i + "@")) {
					s = s.substring(i > 9 ? 6 : 5);
				}
			}
			if (j1 == 0)
				l++;
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1 && !s.equals(myPlayer.name)) {
					if (myPrivilege >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 3 || j1 == 7) && splitPrivateChat == 0 && (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					if (myPrivilege >= 1) {
						menuActionName[menuActionRow] = "Report abuse @whi@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Add ignore @whi@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add friend @whi@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept trade @whi@" + s;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 5 || j1 == 6) && splitPrivateChat == 0 && privateChatMode < 2)
				l++;
			if (j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isFriendOrSelf(s))) {
				if (j > k1 - 14 && j <= k1) {
					menuActionName[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	private void drawFriendsListOrWelcomeScreen(RSInterface class9) {
		int j = class9.contentType;
		if (j >= 1 && j <= 100 || j >= 701 && j <= 800) {
			if (j == 1 && privateMessageStatus == 0) {
				class9.message = "Loading friend list";
				class9.atActionType = 0;
				return;
			}

			if (j == 1 && privateMessageStatus == 1) {
				class9.message = "Connecting to friendserver";
				class9.atActionType = 0;
				return;
			}
			if (j == 2 && privateMessageStatus != 2) {
				class9.message = "Please wait...";
				class9.atActionType = 0;
				return;
			}
			int k = friendsCount;
			if (privateMessageStatus != 2)
				k = 0;
			if (j > 700)
				j -= 601;
			else
				j--;
			if (j >= k) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			} else {
				class9.message = friendsList[j];
				class9.atActionType = 1;
				return;
			}
		}
		if (j >= 101 && j <= 200 || j >= 801 && j <= 900) {
			int l = friendsCount;
			if (privateMessageStatus != 2)
				l = 0;
			if (j > 800)
				j -= 701;
			else
				j -= 101;
			if (j >= l) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			}
			if (friendsNodeIDs[j] == 0)
				class9.message = "@red@Offline";
			else if (friendsNodeIDs[j] == nodeID)
				class9.message = "@gre@Online"/* + (friendsNodeIDs[j] - 9) */;
			else
				class9.message = "@red@Offline"/* + (friendsNodeIDs[j] - 9) */;
			class9.atActionType = 1;
			return;
		}
		if (j == 203) {
			int i1 = friendsCount;
			if (privateMessageStatus != 2)
				i1 = 0;
			class9.scrollMax = i1 * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j >= 401 && j <= 500) {
			if ((j -= 401) == 0 && privateMessageStatus == 0) {
				class9.message = "Loading ignore list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && privateMessageStatus == 0) {
				class9.message = "Please wait...";
				class9.atActionType = 0;
				return;
			}
			int j1 = ignoreCount;
			if (privateMessageStatus == 0)
				j1 = 0;
			if (j >= j1) {
				class9.message = "";
				class9.atActionType = 0;
				return;
			} else {
				class9.message = UsernameEncoding.formalize(UsernameEncoding.decode(ignoreListAsLongs[j]));
				class9.atActionType = 1;
				return;
			}
		}
		if (j == 503) {
			class9.scrollMax = ignoreCount * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j == 327) {
			class9.modelRotation1 = 150;
			class9.modelRotation2 = (int) (Math.sin(loopCycle / 40D) * 256D) & 0x7ff;
			if (aBoolean1031) {
				for (int k1 = 0; k1 < 7; k1++) {
					int l1 = anIntArray1065[k1];
					if (l1 >= 0 && !IdentitiKit.cache[l1].method537())
						return;
				}

				aBoolean1031 = false;
				Model aclass30_sub2_sub4_sub6s[] = new Model[7];
				int i2 = 0;
				for (int j2 = 0; j2 < 7; j2++) {
					int k2 = anIntArray1065[j2];
					if (k2 >= 0)
						aclass30_sub2_sub4_sub6s[i2++] = IdentitiKit.cache[k2].method538();
				}

				Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0) {
						model.method476(anIntArrayArray1003[l2][0], anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							model.method476(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}

				model.method469();
				model.method470(Animation.anims[myPlayer.standAnimation].anIntArray353[0]);
				model.method479(64, 850, -30, -50, -30, true);
				class9.anInt233 = 5;
				class9.mediaID = 0;
				RSInterface.method208(aBoolean994, model);
			}
			return;
		}
		if (j == 328) {
			RSInterface rsInterface = class9;
			int verticleTilt = 150;
			int animationSpeed = (int) (Math.sin(loopCycle / 40D) * 256D) & 0x7ff;
			rsInterface.modelRotation1 = verticleTilt;
			rsInterface.modelRotation2 = animationSpeed;
			if (aBoolean1031) {
				Model characterDisplay = myPlayer.method452();
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0) {
						characterDisplay.method476(anIntArrayArray1003[l2][0], anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							characterDisplay.method476(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}
				int staticFrame = myPlayer.standAnimation;
				characterDisplay.method469();
				characterDisplay.method470(Animation.anims[staticFrame].anIntArray353[0]);
				// characterDisplay.method479(64, 850, -30, -50, -30, true);
				rsInterface.anInt233 = 5;
				rsInterface.mediaID = 0;
				RSInterface.method208(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 324) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = class9.sprite1;
				aClass30_Sub2_Sub1_Sub1_932 = class9.sprite2;
			}
			if (aBoolean1047) {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			} else {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
		}
		if (j == 325) {
			if (aClass30_Sub2_Sub1_Sub1_931 == null) {
				aClass30_Sub2_Sub1_Sub1_931 = class9.sprite1;
				aClass30_Sub2_Sub1_Sub1_932 = class9.sprite2;
			}
			if (aBoolean1047) {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_931;
				return;
			} else {
				class9.sprite1 = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
		}
		if (j == 600) {
			class9.message = reportAbuseInput;
			if (loopCycle % 20 < 10) {
				class9.message += "|";
				return;
			} else {
				class9.message += " ";
				return;
			}
		}
		if (j == 613)
			if (myPrivilege >= 1) {
				if (canMute) {
					class9.textColor = 0xff0000;
					class9.message = "Moderator option: Mute player for 48 hours: <ON>";
				} else {
					class9.textColor = 0xffffff;
					class9.message = "Moderator option: Mute player for 48 hours: <OFF>";
				}
			} else {
				class9.message = "";
			}
		if (j == 650 || j == 655)
			if (anInt1193 != 0) {
				String s;
				if (daysSinceLastLogin == 0)
					s = "earlier today";
				else if (daysSinceLastLogin == 1)
					s = "yesterday";
				else
					s = daysSinceLastLogin + " days ago";
				class9.message = "You last logged in " + s + " from: " + Signlink.dns;
			} else {
				class9.message = "";
			}
		if (j == 651) {
			if (unreadMessages == 0) {
				class9.message = "0 unread messages";
				class9.textColor = 0xffff00;
			}
			if (unreadMessages == 1) {
				class9.message = "1 unread message";
				class9.textColor = 65280;
			}
			if (unreadMessages > 1) {
				class9.message = unreadMessages + " unread messages";
				class9.textColor = 65280;
			}
		}
		if (j == 652)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					class9.message = "@yel@This is a non-members world: @whi@Since you are a member we";
				else
					class9.message = "";
			} else if (daysSinceRecovChange == 200) {
				class9.message = "You have not yet set any password recovery questions.";
			} else {
				String s1;
				if (daysSinceRecovChange == 0)
					s1 = "Earlier today";
				else if (daysSinceRecovChange == 1)
					s1 = "Yesterday";
				else
					s1 = daysSinceRecovChange + " days ago";
				class9.message = s1 + " you changed your recovery questions";
			}
		if (j == 653)
			if (daysSinceRecovChange == 201) {
				if (membersInt == 1)
					class9.message = "@whi@recommend you use a members world instead. You may use";
				else
					class9.message = "";
			} else if (daysSinceRecovChange == 200)
				class9.message = "We strongly recommend you do so now to secure your account.";
			else
				class9.message = "If you do not remember making this change then cancel it immediately";
		if (j == 654) {
			if (daysSinceRecovChange == 201)
				if (membersInt == 1) {
					class9.message = "@whi@this world but member benefits are unavailable whilst here.";
					return;
				} else {
					class9.message = "";
					return;
				}
			if (daysSinceRecovChange == 200) {
				class9.message = "Do this from the 'account management' area on our front webpage";
				return;
			}
			class9.message = "Do this from the 'account management' area on our front webpage";
		}
	}

	private void drawSplitPrivateChat() {
		if (splitPrivateChat == 0) {
			return;
		}
		TextDrawingArea textDrawingArea = aTextDrawingArea_1271;
		int i = 0;
		if (anInt1104 != 0) {
			i = 1;
		}
		for (int j = 0; j < 100; j++) {
			if (chatMessages[j] != null) {
				int type = chatTypes[j];
				String name = chatNames[j];
				byte rights = 0;
				for (int z = 1; z <= modIcons.length; z++) {
					if (name != null && name.startsWith("@cr" + z + "@")) {
						name = name.substring(z > 9 ? 6 : 5);
						rights = (byte) z;
					}
				}
				if ((type == 3 || type == 7) && (type == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(name))) {
					int yPos = 329 - i * 13;
					if (frameMode != ScreenMode.FIXED) {
						yPos = frameHeight - 170 - i * 13 - (extendChatArea);
					}
					int xPos = 4;
					textDrawingArea.method385(0, "From", yPos, xPos);
					textDrawingArea.method385(65535, "From", yPos - 1, xPos);
					xPos += textDrawingArea.getTextWidth("From ");
					if (rights > 0) {
						modIcons[rights - 1].drawBackground(xPos, yPos - 12);
						xPos += 12;
					}
					textDrawingArea.method385(0, name + ": " + chatMessages[j], yPos, xPos);
					textDrawingArea.method385(65535, name + ": " + chatMessages[j], yPos - 1, xPos);
					if (++i >= 5) {
						return;
					}
				}
				if (type == 5 && privateChatMode < 2) {
					int i1 = 329 - i * 13;
					if (frameMode != ScreenMode.FIXED) {
						i1 = frameHeight - 170 - i * 13 - (extendChatArea);
					}
					textDrawingArea.method385(0, chatMessages[j], i1, 4);
					textDrawingArea.method385(65535, chatMessages[j], i1 - 1, 4);
					if (++i >= 5) {
						return;
					}
				}
				if (type == 6 && privateChatMode < 2) {
					int j1 = 329 - i * 13;
					if (frameMode != ScreenMode.FIXED) {
						j1 = frameHeight - 170 - i * 13 - (extendChatArea);
					}
					textDrawingArea.method385(0, "To " + name + ": " + chatMessages[j], j1, 4);
					textDrawingArea.method385(65535, "To " + name + ": " + chatMessages[j], j1 - 1, 4);
					if (++i >= 5) {
						return;
					}
				}
			}
		}
	}

 public void pushMessage(String s,int i,String s1){if(i==0&&newRegularFont!=null){for(String line:com.koios.client.arpg.ChatWrap.lines(s,480,value->newRegularFont.getTextWidth(value)))pushChatLine(line,i,s1);}else pushChatLine(s,i,s1);}
 private void pushChatLine(String s,int i,String s1) {
		if (i == 0 && dialogID != -1) {
			aString844 = s;
			super.clickMode3 = 0;
		}
		if (backDialogID == -1)
			inputTaken = true;
		for (int j = 499; j > 0; j--) {
			chatTypes[j] = chatTypes[j - 1];
			chatNames[j] = chatNames[j - 1];
			chatMessages[j] = chatMessages[j - 1];
			chatRights[j] = chatRights[j - 1];
		}
		chatTypes[0] = i;
		chatNames[0] = s1;
		chatMessages[0] = s;
		chatRights[0] = rights;
	}

	public static void setTab(int id) {
		tabID = id;
		tabAreaAltered = true;
	}

	private final void minimapHovers() {
		int yOffset = 0;
		int offsetX = frameMode.isFixed() ? 516 : frameWidth - 217;
		if (!frameMode.isFixed()) {
			offsetX += 5;
			yOffset += 5;
		}
		prayHover = inSprite(false, cacheSprite[7], 1 + offsetX, 85 + yOffset);
		runHover = inSprite(false, cacheSprite[7], 26 + offsetX, 121 + yOffset);
		sumHover = inSprite(false, cacheSprite[56], (frameMode.isFixed() ? (181 + offsetX) : frameWidth - 63), frameMode == ScreenMode.FIXED ? 119 : 147);
	}

	private final int[] tabClickX = { 38, 33, 33, 33, 33, 33, 38, 38, 33, 33, 33, 33, 33, 38 }, tabClickStart = { 522, 560, 593, 625, 659, 692, 724, 522, 560, 593, 625, 659, 692, 724 },
			tabClickY = { 169, 169, 169, 169, 169, 169, 169, 466, 466, 466, 466, 466, 466, 466 };

	private void processTabClick() {
		if (super.clickMode3 == 1) {
			if (frameMode == ScreenMode.FIXED || (!frameMode.isFixed() && !changeTabArea)) {
				int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 765;
				int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 503;
				for (int i = 0; i < tabClickX.length; i++) {
					if (super.mouseX >= tabClickStart[i] + xOffset && super.mouseX <= tabClickStart[i] + tabClickX[i] + xOffset && super.mouseY >= tabClickY[i] + yOffset
							&& super.mouseY < tabClickY[i] + 37 + yOffset && tabInterfaceIDs[i] != -1) {
						tabID = i;
						tabAreaAltered = true;
						break;
					}
				}
			} else if (changeTabArea && frameWidth < 1000) {
				if (super.saveClickX >= frameWidth - 226 && super.saveClickX <= frameWidth - 195 && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40
						&& tabInterfaceIDs[0] != -1) {
					if (tabID == 0) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 0;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 194 && super.saveClickX <= frameWidth - 163 && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40
						&& tabInterfaceIDs[1] != -1) {
					if (tabID == 1) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 1;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 162 && super.saveClickX <= frameWidth - 131 && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40
						&& tabInterfaceIDs[2] != -1) {
					if (tabID == 2) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 2;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 129 && super.saveClickX <= frameWidth - 98 && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40
						&& tabInterfaceIDs[3] != -1) {
					if (tabID == 3) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 3;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 97 && super.saveClickX <= frameWidth - 66 && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40
						&& tabInterfaceIDs[4] != -1) {
					if (tabID == 4) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 4;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 65 && super.saveClickX <= frameWidth - 34 && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40
						&& tabInterfaceIDs[5] != -1) {
					if (tabID == 5) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 5;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 33 && super.saveClickX <= frameWidth && super.saveClickY >= frameHeight - 72 && super.saveClickY < frameHeight - 40 && tabInterfaceIDs[6] != -1) {
					if (tabID == 6) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 6;
					tabAreaAltered = true;

				}

				if (super.saveClickX >= frameWidth - 194 && super.saveClickX <= frameWidth - 163 && super.saveClickY >= frameHeight - 37 && super.saveClickY < frameHeight - 0
						&& tabInterfaceIDs[8] != -1) {
					if (tabID == 8) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 8;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 162 && super.saveClickX <= frameWidth - 131 && super.saveClickY >= frameHeight - 37 && super.saveClickY < frameHeight - 0
						&& tabInterfaceIDs[9] != -1) {
					if (tabID == 9) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 9;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 129 && super.saveClickX <= frameWidth - 98 && super.saveClickY >= frameHeight - 37 && super.saveClickY < frameHeight - 0
						&& tabInterfaceIDs[10] != -1) {
					if (tabID == 7) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 7;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 97 && super.saveClickX <= frameWidth - 66 && super.saveClickY >= frameHeight - 37 && super.saveClickY < frameHeight - 0
						&& tabInterfaceIDs[11] != -1) {
					if (tabID == 11) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 11;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 65 && super.saveClickX <= frameWidth - 34 && super.saveClickY >= frameHeight - 37 && super.saveClickY < frameHeight - 0
						&& tabInterfaceIDs[12] != -1) {
					if (tabID == 12) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 12;
					tabAreaAltered = true;

				}
				if (super.saveClickX >= frameWidth - 33 && super.saveClickX <= frameWidth && super.saveClickY >= frameHeight - 37 && super.saveClickY < frameHeight - 0 && tabInterfaceIDs[13] != -1) {
					if (tabID == 13) {
						showTabComponents = !showTabComponents;
					} else {
						showTabComponents = true;
					}
					tabID = 13;
					tabAreaAltered = true;

				}
			} else if (changeTabArea && frameWidth >= 1000) {
				if (super.mouseY >= frameHeight - 37 && super.mouseY <= frameHeight) {
					if (super.mouseX >= frameWidth - 417 && super.mouseX <= frameWidth - 386) {
						if (tabID == 0) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 0;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 385 && super.mouseX <= frameWidth - 354) {
						if (tabID == 1) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 1;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 353 && super.mouseX <= frameWidth - 322) {
						if (tabID == 2) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 2;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 321 && super.mouseX <= frameWidth - 290) {
						if (tabID == 3) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 3;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 289 && super.mouseX <= frameWidth - 258) {
						if (tabID == 4) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 4;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 257 && super.mouseX <= frameWidth - 226) {
						if (tabID == 5) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 5;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 225 && super.mouseX <= frameWidth - 194) {
						if (tabID == 6) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 6;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 193 && super.mouseX <= frameWidth - 163) {
						if (tabID == 8) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 8;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 162 && super.mouseX <= frameWidth - 131) {
						if (tabID == 9) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 9;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 130 && super.mouseX <= frameWidth - 99) {
						if (tabID == 7) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 7;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 98 && super.mouseX <= frameWidth - 67) {
						if (tabID == 11) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 11;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 66 && super.mouseX <= frameWidth - 45) {
						if (tabID == 12) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 12;
						tabAreaAltered = true;
					}
					if (super.mouseX >= frameWidth - 31 && super.mouseX <= frameWidth) {
						if (tabID == 13) {
							showTabComponents = !showTabComponents;
						} else {
							showTabComponents = true;
						}
						tabID = 13;
						tabAreaAltered = true;
					}
				}
			}
		}
	}

	private void resetImageProducers2() {
		if (aRSImageProducer_1166 != null)
			return;
		nullLoader();
		super.fullGameScreen = null;
		loginScreenProducer = null;
		aRSImageProducer_1166 = new ImageProducer(519, 165);// chatback
		aRSImageProducer_1164 = new ImageProducer(249, 168);// mapback
		DrawingArea.setAllPixelsToZero();
		cacheSprite[19].drawSprite(0, 0);
		aRSImageProducer_1163 = new ImageProducer(249, 335);// inventory
		aRSImageProducer_1165 = new ImageProducer(screenAreaWidth, screenAreaHeight);// gamescreen
		DrawingArea.setAllPixelsToZero();
		aRSImageProducer_1125 = new ImageProducer(249, 45);
		welcomeScreenRaised = true;
	}

	private void method81(Sprite sprite, int j, int k) {
		int l = k * k + j * j;
		if (l > 4225 && l < 0x15f90) {
			int i1 = minimapInt1 + minimapInt2 & 0x7ff;
			int j1 = Model.modelIntArray1[i1];
			int k1 = Model.modelIntArray2[i1];
			j1 = (j1 * 256) / (minimapInt3 + 256);
			k1 = (k1 * 256) / (minimapInt3 + 256);
		} else {
			markMinimap(sprite, k, j);
		}
	}

	public void rightClickChatButtons() {
		if (mouseY >= frameHeight - 22 && mouseY <= frameHeight) {
			if (super.mouseX >= 5 && super.mouseX <= 61) {
				menuActionName[1] = "View All";
				menuActionID[1] = 999;
				menuActionRow = 2;
			} else if (super.mouseX >= 71 && super.mouseX <= 127) {
				menuActionName[1] = "View Game";
				menuActionID[1] = 998;
				menuActionRow = 2;
			} else if (super.mouseX >= 137 && super.mouseX <= 193) {
				menuActionName[1] = "Hide public";
				menuActionID[1] = 997;
				menuActionName[2] = "Off public";
				menuActionID[2] = 996;
				menuActionName[3] = "Friends public";
				menuActionID[3] = 995;
				menuActionName[4] = "On public";
				menuActionID[4] = 994;
				menuActionName[5] = "View public";
				menuActionID[5] = 993;
				menuActionRow = 6;
			} else if (super.mouseX >= 203 && super.mouseX <= 259) {
				menuActionName[1] = "Off private";
				menuActionID[1] = 992;
				menuActionName[2] = "Friends private";
				menuActionID[2] = 991;
				menuActionName[3] = "On private";
				menuActionID[3] = 990;
				menuActionName[4] = "View private";
				menuActionID[4] = 989;
				menuActionRow = 5;
			} else if (super.mouseX >= 269 && super.mouseX <= 325) {
				menuActionName[1] = "Off clan chat";
				menuActionID[1] = 1003;
				menuActionName[2] = "Friends clan chat";
				menuActionID[2] = 1002;
				menuActionName[3] = "On clan chat";
				menuActionID[3] = 1001;
				menuActionName[4] = "View clan chat";
				menuActionID[4] = 1000;
				menuActionRow = 5;
			} else if (super.mouseX >= 335 && super.mouseX <= 391) {
				menuActionName[1] = "Off trade";
				menuActionID[1] = 987;
				menuActionName[2] = "Friends trade";
				menuActionID[2] = 986;
				menuActionName[3] = "On trade";
				menuActionID[3] = 985;
				menuActionName[4] = "View trade";
				menuActionID[4] = 984;
				menuActionRow = 5;
			} else if (super.mouseX >= 404 && super.mouseX <= 515) {
				menuActionName[1] = "Report Abuse";
				menuActionID[1] = 606;
				menuActionRow = 2;
			}
		}
	}

	public void processRightClick() {
        if((prototypeOpen||arpgSkillsOpen||controlsPanel!=null)&&!overSidebar(mouseX,mouseY)){arpgHoverWidget=-1;arpgHoverSlot=-1;menuActionRow=1;menuActionName[0]="Cancel";menuActionID[0]=1107;return;}
        arpgHoverWidget=-1;arpgHoverSlot=-1;
		if (activeInterfaceType != 0) {
			return;
		}
		menuActionName[0] = "Cancel";
		menuActionID[0] = 1107;
		menuActionRow = 1;
		if (showChatComponents) {
			buildSplitPrivateChatMenu();
		}
		
		anInt886 = 0;
		anInt1315 = 0;
		if (frameMode == ScreenMode.FIXED) {
			if (super.mouseX > 4 && super.mouseY > 4 && super.mouseX < 516 && super.mouseY < 338) {
				if (openInterfaceID != -1) {
					buildInterfaceMenu(4, RSInterface.interfaceCache[openInterfaceID], super.mouseX, 4, super.mouseY, 0);
				} else {
					build3dScreenMenu();
				}
			}
		} else if (frameMode != ScreenMode.FIXED) {
			int offsetY = frameHeight - 160;
			int x = 256;
			int y = offsetY - 10 - extendChatArea;
			if (changeChatArea && (super.mouseX >= x && super.mouseX <= x + 10 && super.mouseY >= y && super.mouseY <= y + 16)) {
				chatTextDrawingArea.method390(4, 0xffffff, "Resize Chatbox", loopCycle / 1000, 15);
				return;
			}
			if (getMousePositions()) {
				if (super.mouseX > (frameWidth / 2) - 356 && super.mouseY > (frameHeight / 2) - 230 && super.mouseX < ((frameWidth / 2) + 356) && super.mouseY < (frameHeight / 2) + 230
						&& openInterfaceID != -1) {
					buildInterfaceMenu((frameWidth / 2) - 356, RSInterface.interfaceCache[openInterfaceID], super.mouseX, (frameHeight / 2) - 230, super.mouseY, 0);
				} else {
					build3dScreenMenu();
				}
			}
		}
		if (anInt886 != anInt1026) {
			anInt1026 = anInt886;
		}
		if (anInt1315 != anInt1129) {
			anInt1129 = anInt1315;
		}
		anInt886 = 0;
		anInt1315 = 0;
		if (!changeTabArea) {
			final int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 503;
			final int xOffset = frameMode == ScreenMode.FIXED ? 0 : frameWidth - 765;
			if (super.mouseX > 548 + xOffset && super.mouseX < 740 + xOffset && super.mouseY > 207 + yOffset && super.mouseY < 468 + yOffset) {
				if (invOverlayInterfaceID != -1) {
					buildInterfaceMenu(548 + xOffset, RSInterface.interfaceCache[invOverlayInterfaceID], super.mouseX, 207 + yOffset, super.mouseY, 0);
				} else if (tabInterfaceIDs[tabID] != -1 && !com.koios.client.arpg.ArpgTabs.owns(tabID)) {
					buildInterfaceMenu(548 + xOffset, RSInterface.interfaceCache[tabInterfaceIDs[tabID]], super.mouseX, 207 + yOffset, super.mouseY, 0);
				}
			}
		} else if (changeTabArea) {
			final int yOffset = frameWidth >= 1000 ? 37 : 74;
			if (super.mouseX > frameWidth - 197 && super.mouseY > frameHeight - yOffset - 267 && super.mouseX < frameWidth - 7 && super.mouseY < frameHeight - yOffset - 7 && showTabComponents) {
				if (invOverlayInterfaceID != -1) {
					buildInterfaceMenu(frameWidth - 197, RSInterface.interfaceCache[invOverlayInterfaceID], super.mouseX, frameHeight - yOffset - 267, super.mouseY, 0);
				} else if (tabInterfaceIDs[tabID] != -1 && !com.koios.client.arpg.ArpgTabs.owns(tabID)) {
					buildInterfaceMenu(frameWidth - 197, RSInterface.interfaceCache[tabInterfaceIDs[tabID]], super.mouseX, frameHeight - yOffset - 267, super.mouseY, 0);
				}
			}
		}
		if (anInt886 != anInt1048) {
			tabAreaAltered = true;
			anInt1048 = anInt886;
		}
		if (anInt1315 != anInt1044) {
			tabAreaAltered = true;
			anInt1044 = anInt1315;
		}
		anInt886 = 0;
		anInt1315 = 0;
		if (super.mouseX > 0 && super.mouseY > (frameMode == ScreenMode.FIXED ? 338 : frameHeight - 165) && super.mouseX < 490
				&& super.mouseY < (frameMode == ScreenMode.FIXED ? 463 : frameHeight - 40) && showChatComponents) {
			if (backDialogID != -1) {
				buildInterfaceMenu(20, RSInterface.interfaceCache[backDialogID], super.mouseX, (frameMode == ScreenMode.FIXED ? 358 : frameHeight - 145), super.mouseY, 0);
			} else if (super.mouseY < (frameMode == ScreenMode.FIXED ? 463 : frameHeight - 40) && super.mouseX < 490) {
				buildChatAreaMenu(super.mouseY - (frameMode == ScreenMode.FIXED ? 338 : frameHeight - 165));
			}
		}
		if (backDialogID != -1 && anInt886 != anInt1039) {
			inputTaken = true;
			anInt1039 = anInt886;
		}
		if (backDialogID != -1 && anInt1315 != anInt1500) {
			inputTaken = true;
			anInt1500 = anInt1315;
		}
		if (super.mouseX > 4 && super.mouseY > 480 && super.mouseX < 516 && super.mouseY < frameHeight) {
			rightClickChatButtons();
		}
		processMinimapActions();
		boolean flag = false;
		while (!flag) {
			flag = true;
			for (int j = 0; j < menuActionRow - 1; j++) {
				if (menuActionID[j] < 1000 && menuActionID[j + 1] > 1000) {
					String s = menuActionName[j];
					menuActionName[j] = menuActionName[j + 1];
					menuActionName[j + 1] = s;
					int k = menuActionID[j];
					menuActionID[j] = menuActionID[j + 1];
					menuActionID[j + 1] = k;
					k = menuActionCmd2[j];
					menuActionCmd2[j] = menuActionCmd2[j + 1];
					menuActionCmd2[j + 1] = k;
					k = menuActionCmd3[j];
					menuActionCmd3[j] = menuActionCmd3[j + 1];
					menuActionCmd3[j + 1] = k;
					k = menuActionCmd1[j];
					menuActionCmd1[j] = menuActionCmd1[j + 1];
					menuActionCmd1[j + 1] = k;
					flag = false;
				}
			}
		}
	}

	private void login(String username, String password, boolean reconnecting) {
		Signlink.errorname = username;
		try {
			if (!reconnecting) {
				// loginMessage1 = "";
				loginMessage1 = "Connecting to server...";
				drawLoginScreen();
			}
			socketStream = new RSSocket(this, openSocket(clientStatus.getPort() + portOff));
			stream.currentOffset = 0;
			stream.writeLEShort(14);
			socketStream.queueBytes(1, stream.buffer);

			int responseCode = socketStream.read();
			int initialResponseCode = responseCode;
			if (responseCode == 0) {
				socketStream.flushInputStream(inStream.buffer, 8);
				inStream.currentOffset = 0;
				aLong1215 = inStream.readLong();
				int ai[] = new int[4];
				ai[0] = (int) (Math.random() * 99999999D);
				ai[1] = (int) (Math.random() * 99999999D);
				ai[2] = (int) (aLong1215 >> 32);
				ai[3] = (int) aLong1215;
				stream.currentOffset = 0;
				stream.writeLEShort(10);
				stream.writeInt(ai[0]);
				stream.writeInt(ai[1]);
				stream.writeInt(ai[2]);
				stream.writeInt(ai[3]);
				stream.writeLong(/* signlink.uid */999999);
				stream.writeString(username);
				stream.writeString(password);
				// TODO: Implement this server side
				// stream.writeString(Signlink.serialNumber);
				stream.doKeys();
				aStream_847.currentOffset = 0;
				if (reconnecting)
					aStream_847.writeLEShort(18);
				else
					aStream_847.writeLEShort(16);
				aStream_847.writeLEShort(stream.currentOffset + 1 + 1 + 2);
				aStream_847.writeLEShort(255);
				aStream_847.writeShort(Constants.CLIENT_VERSION);
				aStream_847.writeLEShort(lowMem ? 1 : 0);
				aStream_847.writeBytes(stream.buffer, stream.currentOffset, 0);
				stream.encryption = new ISAACRandomGen(ai);
				for (int j2 = 0; j2 < 4; j2++)
					ai[j2] += 50;

				encryption = new ISAACRandomGen(ai);
				socketStream.queueBytes(aStream_847.currentOffset, aStream_847.buffer);
				responseCode = socketStream.read();
			}
			if (responseCode == 1) {
				try {
					Thread.sleep(2000L);
				} catch (Exception _ex) {
				}
				login(username, password, reconnecting);
				return;
			}
			if (responseCode == 2) {
				myPrivilege = socketStream.read();
				flagged = socketStream.read() == 1;
				aLong1220 = 0L;
				anInt1022 = 0;
				mouseDetection.coordsIndex = 0;
				super.awtFocus = true;
				aBoolean954 = true;
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				pktType = -1;
				anInt841 = -1;
				anInt842 = -1;
				anInt843 = -1;
				pktSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				anInt1011 = 0;
				anInt855 = 0;
				menuActionRow = 0;
				menuOpen = false;
				super.idleTime = 0;
				for (int j1 = 0; j1 < 100; j1++)
					chatMessages[j1] = null;
				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				soundCount = 0;
				setNorth();
				anInt1021 = 0;
				anInt985 = -1;
				destX = 0;
				destY = 0;
				playerCount = 0;
				npcCount = 0;
				for (int i2 = 0; i2 < maxPlayers; i2++) {
					playerArray[i2] = null;
					aStreamArray895s[i2] = null;
				}
				for (int k2 = 0; k2 < 16384; k2++)
					npcArray[k2] = null;
				myPlayer = playerArray[myPlayerIndex] = new Player();
				aClass19_1013.removeAll();
				aClass19_1056.removeAll();
				for (int l2 = 0; l2 < 4; l2++) {
					for (int i3 = 0; i3 < 104; i3++) {
						for (int k3 = 0; k3 < 104; k3++)
							groundArray[l2][i3][k3] = null;
					}
				}
				aClass19_1179 = new NodeList();
				fullscreenInterfaceID = -1;
				privateMessageStatus = 0;
				friendsCount = 0;
				dialogID = -1;
				backDialogID = -1;
				openInterfaceID = -1;
				invOverlayInterfaceID = -1;
				anInt1018 = -1;
				aBoolean1149 = false;
				tabID = 3;
				inputDialogState = 0;
				menuOpen = false;
				messagePromptRaised = false;
				aString844 = null;
				anInt1055 = 0;
				anInt1054 = -1;
				aBoolean1047 = true;
				method45();
				for (int j3 = 0; j3 < 5; j3++)
					anIntArray990[j3] = 0;
				for (int l3 = 0; l3 < 5; l3++) {
					atPlayerActions[l3] = null;
					atPlayerArray[l3] = false;
				}
				anInt1175 = 0;
				anInt1134 = 0;
				anInt986 = 0;
				anInt1288 = 0;
				anInt924 = 0;
				anInt1188 = 0;
				anInt1155 = 0;
				anInt1226 = 0;
				loadClan();
				saveData();
				sendFrame36(429, 1);
				resetImageProducers2();
				RSInterface.prepareAtLogin();
				setBounds();
				updateGraphics(true);
				return;
			}
			if (responseCode == 3) {
				return;
			}
			if (responseCode == 4) {
				loginMessage1 = "Your account has been disabled.";
				return;
			}
			if (responseCode == 5) {
				loginMessage1 = "Your account is already logged in.";
				return;
			}
			if (responseCode == 6) {
				loginMessage1 = Constants.CLIENT_NAME + " has been updated!";
				return;
			}
			if (responseCode == 7) {
				loginMessage1 = "This world is full.";
				return;
			}
			if (responseCode == 8) {
				loginMessage1 = "Unable to connect.";
				return;
			}
			if (responseCode == 9) {
				loginMessage1 = "Login limit exceeded. Too many connections from your address.";
				// loginMessage2 = "Too many connections from your address.";
				return;
			}
			if (responseCode == 10) {
				loginMessage1 = "Unable to connect. Bad session id.";
				return;
			}
			if (responseCode == 11) {
				return;
			}
			if (responseCode == 12) {
				loginMessage1 = "You need a members account to login to this world.";
				return;
			}
			if (responseCode == 13) {
				loginMessage1 = "Could not complete login.";
				return;
			}
			if (responseCode == 14) {
				loginMessage1 = "The server is being updated. Please wait 1 minute and try again.";
				return;
			}
			if (responseCode == 15) {
				loggedIn = true;
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				pktType = -1;
				anInt841 = -1;
				anInt842 = -1;
				anInt843 = -1;
				pktSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				menuActionRow = 0;
				menuOpen = false;
				aLong824 = System.currentTimeMillis();
				return;
			}
			if (responseCode == 16) {
				loginMessage1 = "Login attempts exceeded. Please wait 1 minute and try again.";
				return;
			}
			if (responseCode == 17) {
				loginMessage1 = "You are standing in a members-only area.";
				return;
			}
			if (responseCode == 20) {
				loginMessage1 = "Invalid loginserver requested";
				return;
			}
			if (responseCode == 21) {
				for (int k1 = socketStream.read(); k1 >= 0; k1--) {
					loginMessage1 = "You have only just left another world";
					drawLoginScreen();
					try {
						Thread.sleep(1000L);
					} catch (Exception _ex) {
					}
				}
				login(username, password, reconnecting);
				return;
			}
			if (responseCode == -1) {
				if (initialResponseCode == 0) {
					if (loginFailures < 2) {
						try {
							Thread.sleep(2000L);
						} catch (Exception _ex) {
						}
						loginFailures++;
						login(username, password, reconnecting);
						return;
					} else {
						loginMessage1 = "No response from loginserver, Please wait 1 minute and try again.";
						return;
					}
				} else {
					loginMessage1 = "No response from server, Please try using a different world.";
					return;
				}
			} else {
				loginMessage1 = "Unexpected server response, Please try using a different world.";
				return;
			}
		} catch (IOException _ex) {
			loginMessage1 = "";
		}
		loginMessage1 = "Error connecting to server.";
	}

	private boolean doWalkTo(int i, int j, int k, int i1, int localY, int k1, int l1, int i2, int localX, boolean flag, int k2) {
		byte regionWidth = 104;
		byte regionHeight = 104;
		for (int l2 = 0; l2 < regionWidth; l2++) {
			for (int i3 = 0; i3 < regionHeight; i3++) {
				anIntArrayArray901[l2][i3] = 0;
				anIntArrayArray825[l2][i3] = 0x5f5e0ff;
			}
		}
		int j3 = localX;
		int k3 = localY;
		if (localY > 104 || localX > 104) {
			return false;
		}
		anIntArrayArray901[localX][localY] = 99;
		anIntArrayArray825[localX][localY] = 0;
		int l3 = 0;
		int i4 = 0;
		bigX[l3] = localX;
		bigY[l3++] = localY;
		boolean flag1 = false;
		int j4 = bigX.length;
		int ai[][] = aClass11Array1230[plane].anIntArrayArray294;
		while (i4 != l3) {
			j3 = bigX[i4];
			k3 = bigY[i4];
			i4 = (i4 + 1) % j4;
			if (j3 == k2 && k3 == i2) {
				flag1 = true;
				break;
			}
			if (i1 != 0) {
				if ((i1 < 5 || i1 == 10) && aClass11Array1230[plane].method219(k2, j3, k3, j, i1 - 1, i2)) {
					flag1 = true;
					break;
				}
				if (i1 < 10 && aClass11Array1230[plane].method220(k2, i2, k3, i1 - 1, j, j3)) {
					flag1 = true;
					break;
				}
			}
			if (k1 != 0 && k != 0 && aClass11Array1230[plane].method221(i2, k2, j3, k, l1, k1, k3)) {
				flag1 = true;
				break;
			}
			int l4 = anIntArrayArray825[j3][k3] + 1;
			if (j3 > 0 && anIntArrayArray901[j3 - 1][k3] == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0) {
				bigX[l3] = j3 - 1;
				bigY[l3] = k3;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3] = 2;
				anIntArrayArray825[j3 - 1][k3] = l4;
			}
			if (j3 < regionWidth - 1 && anIntArrayArray901[j3 + 1][k3] == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0) {
				bigX[l3] = j3 + 1;
				bigY[l3] = k3;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3] = 8;
				anIntArrayArray825[j3 + 1][k3] = l4;
			}
			if (k3 > 0 && anIntArrayArray901[j3][k3 - 1] == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0) {
				bigX[l3] = j3;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3][k3 - 1] = 1;
				anIntArrayArray825[j3][k3 - 1] = l4;
			}
			if (k3 < regionHeight - 1 && anIntArrayArray901[j3][k3 + 1] == 0 && (ai[j3][k3 + 1] & 0x1280120) == 0) {
				bigX[l3] = j3;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3][k3 + 1] = 4;
				anIntArrayArray825[j3][k3 + 1] = l4;
			}
			if (j3 > 0 && k3 > 0 && anIntArrayArray901[j3 - 1][k3 - 1] == 0 && (ai[j3 - 1][k3 - 1] & 0x128010e) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0) {
				bigX[l3] = j3 - 1;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3 - 1] = 3;
				anIntArrayArray825[j3 - 1][k3 - 1] = l4;
			}
			if (j3 < regionWidth - 1 && k3 > 0 && anIntArrayArray901[j3 + 1][k3 - 1] == 0 && (ai[j3 + 1][k3 - 1] & 0x1280183) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
					&& (ai[j3][k3 - 1] & 0x1280102) == 0) {
				bigX[l3] = j3 + 1;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3 - 1] = 9;
				anIntArrayArray825[j3 + 1][k3 - 1] = l4;
			}
			if (j3 > 0 && k3 < regionHeight - 1 && anIntArrayArray901[j3 - 1][k3 + 1] == 0 && (ai[j3 - 1][k3 + 1] & 0x1280138) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0
					&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
				bigX[l3] = j3 - 1;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3 + 1] = 6;
				anIntArrayArray825[j3 - 1][k3 + 1] = l4;
			}
			if (j3 < regionWidth - 1 && k3 < regionHeight - 1 && anIntArrayArray901[j3 + 1][k3 + 1] == 0 && (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
					&& (ai[j3][k3 + 1] & 0x1280120) == 0) {
				bigX[l3] = j3 + 1;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3 + 1] = 12;
				anIntArrayArray825[j3 + 1][k3 + 1] = l4;
			}
		}
		anInt1264 = 0;
		if (!flag1) {
			if (flag) {
				int i5 = 100;
				for (int k5 = 1; k5 < 2; k5++) {
					for (int i6 = k2 - k5; i6 <= k2 + k5; i6++) {
						for (int l6 = i2 - k5; l6 <= i2 + k5; l6++) {
							if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104 && anIntArrayArray825[i6][l6] < i5) {
								i5 = anIntArrayArray825[i6][l6];
								j3 = i6;
								k3 = l6;
								anInt1264 = 1;
								flag1 = true;
							}
						}
					}
					if (flag1)
						break;
				}
			}
			if (!flag1)
				return false;
		}
		i4 = 0;
		bigX[i4] = j3;
		bigY[i4++] = k3;
		int l5;
		for (int j5 = l5 = anIntArrayArray901[j3][k3]; j3 != localX || k3 != localY; j5 = anIntArrayArray901[j3][k3]) {
			if (j5 != l5) {
				l5 = j5;
				bigX[i4] = j3;
				bigY[i4++] = k3;
			}
			if ((j5 & 2) != 0)
				j3++;
			else if ((j5 & 8) != 0)
				j3--;
			if ((j5 & 1) != 0)
				k3++;
			else if ((j5 & 4) != 0)
				k3--;
		}
		if (i4 > 0) {
			int k4 = i4;
			if (k4 > 25)
				k4 = 25;
			i4--;
			int k6 = bigX[i4];
			int i7 = bigY[i4];
			anInt1288 += k4;
			if (anInt1288 >= 92) {
				stream.createFrame(36);
				stream.writeInt(0);
				anInt1288 = 0;
			}
			if (i == 0) {
				stream.createFrame(164);
				stream.writeLEShort(k4 + k4 + 3);
			}
			if (i == 1) {
				stream.createFrame(248);
				stream.writeLEShort(k4 + k4 + 3 + 14);
			}
			if (i == 2) {
				stream.createFrame(98);
				stream.writeLEShort(k4 + k4 + 3);
			}
			stream.method433(k6 + baseX);
			destX = bigX[0];
			destY = bigY[0];
			for (int j7 = 1; j7 < k4; j7++) {
				i4--;
				stream.writeLEShort(bigX[i4] - k6);
				stream.writeLEShort(bigY[i4] - i7);
			}
			stream.method431(i7 + baseY);
			stream.method424(super.keyArray[5] != 1 ? 0 : 1);
			return true;
		}
		return i != 1;
	}

	private void method86(Stream stream) {
		for (int j = 0; j < anInt893; j++) {
			int k = anIntArray894[j];
			NPC npc = npcArray[k];
			int updateFlag = stream.readUnsignedByte();

			if ((updateFlag & 0x10) != 0) {
				int animationId = stream.readLEShort();
				if (animationId == 65535)
					animationId = -1;
				int animationDelay = stream.readUnsignedByte();
				if (animationId == npc.anim && animationId != -1) {
					int l2 = Animation.anims[animationId].anInt365;
					if (l2 == 1) {
						npc.anInt1527 = 0;
						npc.anInt1528 = 0;
						npc.anInt1529 = animationDelay;
						npc.anInt1530 = 0;
					}
					if (l2 == 2)
						npc.anInt1530 = 0;
				} else if (animationId == -1 || npc.anim == -1 || Animation.anims[animationId].anInt359 >= Animation.anims[npc.anim].anInt359) {
					npc.anim = animationId;
					npc.anInt1527 = 0;
					npc.anInt1528 = 0;
					npc.anInt1529 = animationDelay;
					npc.anInt1530 = 0;
					npc.anInt1542 = npc.smallXYIndex;
				}
			}
			if ((updateFlag & 8) != 0) {
				int j1 = stream.readInt();
				int j2 = stream.readByteC();
				npc.updateHitData(j2, j1, loopCycle);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.readByteA();
				npc.maxHealth = stream.readUnsignedByte();
			}
			if ((updateFlag & 0x80) != 0) {
				npc.anInt1520 = stream.readUnsignedShort();
				int k1 = stream.readInt();
				npc.anInt1524 = k1 >> 16;
				npc.anInt1523 = loopCycle + (k1 & 0xffff);
				npc.anInt1521 = 0;
				npc.anInt1522 = 0;
				if (npc.anInt1523 > loopCycle)
					npc.anInt1521 = -1;
				if (npc.anInt1520 == 65535)
					npc.anInt1520 = -1;
			}
			if ((updateFlag & 0x20) != 0) {
				npc.interactingEntity = stream.readUnsignedShort();
				if (npc.interactingEntity == 65535)
					npc.interactingEntity = -1;
			}
			if ((updateFlag & 1) != 0) {
				npc.textSpoken = stream.readString();
				npc.textCycle = 100;
			}

			if ((updateFlag & 0x40) != 0) {
				int damage = stream.readInt();
				int hitMask = stream.readByteS();
				npc.updateHitData(hitMask, damage, loopCycle);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.readByteS();
				npc.maxHealth = stream.readByteC();
			}
			if ((updateFlag & 2) != 0) {
				npc.desc = NPCDefinition.forID(stream.method436());
				npc.anInt1540 = npc.desc.npcSizeInSquares;
				npc.anInt1504 = npc.desc.anInt79;
				npc.walkAnimation = npc.desc.walkAnimation;
				npc.turn180Animation = npc.desc.walkingBackwardsAnimation;
				npc.turn90CW = npc.desc.walkLeftAnimation;
				npc.turn90CCW = npc.desc.walkRightAnimation;
				npc.standAnimation = npc.desc.standAnimation;
			}
			if ((updateFlag & 4) != 0) {
				npc.anInt1538 = stream.readLEShort();
				npc.anInt1539 = stream.readLEShort();
			}
		}
	}

	private void buildAtNPCMenu(NPCDefinition entityDef, int i, int j, int k) {
        if(entityDef!=null&&entityDef.interfaceType>=15000&&entityDef.interfaceType<=15013&&i>=0&&i<npcArray.length&&npcArray[i]!=null&&npcArray[i].maxHealth>0&&npcArray[i].currentHealth<=0)return;
		if (menuActionRow >= 400)
			return;
		if (entityDef.childrenIDs != null)
			entityDef = entityDef.method161();
		if (entityDef == null)
			return;
		if (!entityDef.aBoolean84)
			return;
		String s = entityDef.name;
		if (entityDef.combatLevel != 0)
			s = s + combatDiffColor(myPlayer.combatLevel, entityDef.combatLevel) + " (level-" + entityDef.combatLevel + ")";
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName + " with @yel@" + s;
			menuActionID[menuActionRow] = 582;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
			return;
		}
		if (spellSelected == 1) {
			if ((spellUsableOn & 2) == 2) {
				menuActionName[menuActionRow] = spellTooltip + " @yel@" + s;
				menuActionID[menuActionRow] = 413;
				menuActionCmd1[menuActionRow] = i;
				menuActionCmd2[menuActionRow] = k;
				menuActionCmd3[menuActionRow] = j;
				menuActionRow++;
			}
		} else {
			if (entityDef.actions != null) {
				for (int l = 4; l >= 0; l--)
					if (entityDef.actions[l] != null && !entityDef.actions[l].equalsIgnoreCase("attack")) {
						menuActionName[menuActionRow] = entityDef.actions[l] + " @yel@" + s;
						if (l == 0)
							menuActionID[menuActionRow] = 20;
						if (l == 1)
							menuActionID[menuActionRow] = 412;
						if (l == 2)
							menuActionID[menuActionRow] = 225;
						if (l == 3)
							menuActionID[menuActionRow] = 965;
						if (l == 4)
							menuActionID[menuActionRow] = 478;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			if (entityDef.actions != null) {
				for (int i1 = 4; i1 >= 0; i1--)
					if (entityDef.actions[i1] != null && entityDef.actions[i1].equalsIgnoreCase("attack")) {
						char c = '\0';
						if (!oneClickAttack)
							if (entityDef.combatLevel > myPlayer.combatLevel)
								c = '\u07D0';
						menuActionName[menuActionRow] = entityDef.actions[i1] + " @yel@" + s;
						if (i1 == 0)
							menuActionID[menuActionRow] = 20 + c;
						if (i1 == 1)
							menuActionID[menuActionRow] = 412 + c;
						if (i1 == 2)
							menuActionID[menuActionRow] = 225 + c;
						if (i1 == 3)
							menuActionID[menuActionRow] = 965 + c;
						if (i1 == 4)
							menuActionID[menuActionRow] = 478 + c;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			menuActionName[menuActionRow] = "Examine @yel@" + s;
			menuActionID[menuActionRow] = 1025;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
		}
	}

	private void buildAtPlayerMenu(int i, int j, Player player, int k) {
		if (player == myPlayer)
			return;
		if (menuActionRow >= 400)
			return;
		String s;
		if (player.skill == 0)
			s = player.name + combatDiffColor(myPlayer.combatLevel, player.combatLevel) + " (level-" + player.combatLevel + ")";
		else
			s = player.name + " (skill-" + player.skill + ")";
		if (itemSelected == 1) {
			menuActionName[menuActionRow] = "Use " + selectedItemName + " with @whi@" + s;
			menuActionID[menuActionRow] = 491;
			menuActionCmd1[menuActionRow] = j;
			menuActionCmd2[menuActionRow] = i;
			menuActionCmd3[menuActionRow] = k;
			menuActionRow++;
		} else if (spellSelected == 1) {
			if ((spellUsableOn & 8) == 8) {
				menuActionName[menuActionRow] = spellTooltip + " @whi@" + s;
				menuActionID[menuActionRow] = 365;
				menuActionCmd1[menuActionRow] = j;
				menuActionCmd2[menuActionRow] = i;
				menuActionCmd3[menuActionRow] = k;
				menuActionRow++;
			}
		} else {
			for (int l = 4; l >= 0; l--)
				if (atPlayerActions[l] != null) {
					menuActionName[menuActionRow] = atPlayerActions[l] + " @whi@" + s;
					char c = '\0';
					if (atPlayerActions[l].equalsIgnoreCase("attack")) {
						if (!oneClickAttack)
							if (player.combatLevel > myPlayer.combatLevel)
								c = '\u07D0';
						if (myPlayer.team != 0 && player.team != 0)
							if (myPlayer.team == player.team)
								c = '\u07D0';
							else
								c = '\0';
					} else if (atPlayerArray[l])
						c = '\u07D0';
					if (l == 0)
						menuActionID[menuActionRow] = 561 + c;
					if (l == 1)
						menuActionID[menuActionRow] = 779 + c;
					if (l == 2)
						menuActionID[menuActionRow] = 27 + c;
					if (l == 3)
						menuActionID[menuActionRow] = 577 + c;
					if (l == 4)
						menuActionID[menuActionRow] = 729 + c;
					menuActionCmd1[menuActionRow] = j;
					menuActionCmd2[menuActionRow] = i;
					menuActionCmd3[menuActionRow] = k;
					menuActionRow++;
				}

		}
		for (int i1 = 0; i1 < menuActionRow; i1++) {
			if (menuActionID[i1] == 519) {
				menuActionName[i1] = "Walk here @whi@" + s;
				return;
			}
		}
	}

	private void method89(Class30_Sub1 class30_sub1) {
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if (class30_sub1.anInt1296 == 0)
			i = worldController.method300(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 1)
			i = worldController.method301(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 2)
			i = worldController.method302(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (class30_sub1.anInt1296 == 3)
			i = worldController.method303(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298);
		if (i != 0) {
			int i1 = worldController.method304(class30_sub1.anInt1295, class30_sub1.anInt1297, class30_sub1.anInt1298, i);
			j = i >> 14 & 0x7fff;
			k = i1 & 0x1f;
			l = i1 >> 6;
		}
		class30_sub1.anInt1299 = j;
		class30_sub1.anInt1301 = k;
		class30_sub1.anInt1300 = l;
	}

	private void sendStartupText() {
		sendFrame126("Monster Teleport", 19641); // Varrock Teleport
		sendFrame126("Teleports to monsters", 19642);

		sendFrame126("Minigames Teleport", 19722); // Lumbridge Teleport
		sendFrame126("Teleports to minigames", 19723);

		sendFrame126("Boss Teleport", 19803); // Falador Teleport
		sendFrame126("Teleports to bosses", 19804);

		sendFrame126("Skills Teleport", 19960); // Camelot Teleport
		sendFrame126("Teleports to skills", 19961);

		sendFrame126("PvP Teleport", 20195); // Ardougne Teleport
		sendFrame126("Teleports to PvP", 20196);

		sendFrame126("City Teleport", 20354); // Watchtower Teleport
		sendFrame126("Teleports to cities", 20355);

		// sendFrame126("Unused Teleport", 20570); // Trollheim Teleport
		// sendFrame126("Not used", 19961);

		sendFrame126("Monster Teleport", 21833); // Paddewwa Teleport
		sendFrame126("Teleports to monsters", 21834);

		sendFrame126("Minigames Teleport", 21933); // Senntisten Teleport
		sendFrame126("Teleports to minigames", 21934);

		sendFrame126("Boss Teleport", 22052); // Kharyll Teleport
		sendFrame126("Teleports to bosses", 22053);

		sendFrame126("Skills Teleport", 22123); // Lassar Teleport
		sendFrame126("Teleports to skills", 22124);

		sendFrame126("PvP Teleport", 22232); // Dareeyak Teleport
		sendFrame126("Teleports to PvP", 22233);

		sendFrame126("City Teleport", 22307); // Carrallanger Teleport
		sendFrame126("Teleports to cities", 22308);

		// sendFrame126("Unused Teleport", 22415); // 22415 - Annakarl Teleport
		// sendFrame126("Not used", 22416);

		sendFrame126("Ape Atoll Teleport", 22490); // 22415 - Annakarl Teleport

		sendFrame126("Monster Teleport", 30067); // lunar monster Teleport
		sendFrame126("Teleports to monsters", 30068);

		sendFrame126("Minigames Teleport", 30078); // lunar minigames Teleport
		sendFrame126("Teleports to minigames", 30079);

		sendFrame126("Boss Teleport", 30086); // lunar boss Teleport
		sendFrame126("Teleports to bosses", 30087);

		sendFrame126("PvP Teleport", 30109); // lunar pvp Teleport
		sendFrame126("Teleports to PvP", 30110);

		sendFrame126("City Teleport", 30117); // lunar city Teleport
		sendFrame126("Teleports to cities", 30118);

		sendFrame126("Unused Teleport", 30165); // unused Teleport
		sendFrame126("Teleports nowhere", 30166);

		sendFrame126("Ape Atoll Teleport", 30173); // lunar ape atoll Teleport
		sendFrame126("Teleports to Ape Atoll", 30174);
	}

	@Override
	public void startUp() {

		drawLoadingText(0, "Starting up");

		CacheDownloader.start(this, FileType.CACHE);
		CacheDownloader.start(this, FileType.FILEDATA);

		if (Signlink.cache_dat != null) {
			for (int i = 0; i < 5; i++)
				decompressors[i] = new Decompressor(Signlink.cache_dat, Signlink.cache_idx[i], i + 1);
		}
		try {
			titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25);
			smallText = new TextDrawingArea(false, "p11_full", titleStreamLoader);
			aTextDrawingArea_1271 = new TextDrawingArea(false, "p12_full", titleStreamLoader);
			chatTextDrawingArea = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);
			newSmallFont = RSFont.exileSmall();
			newRegularFont = RSFont.exileBody();
			newBoldFont = RSFont.exileBody();
			newFancyFont = new RSFont(true, "q8_full", titleStreamLoader);
			drawLogo();
			loadTitleScreen();

			drawLoadingText(10, "Connecting to fileserver");
			// connect to fs here

			aClass3_Sub7_Sub1_1493 = method407(instance);
			aClass25_1948 = new Class25(22050, anInt197);
			StreamLoader streamLoader = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30);
			StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35);
			StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40);
			this.mediaStreamLoader = streamLoader_2;
			StreamLoader streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45);
			StreamLoader streamLoader_4 = streamLoaderForName(7, "chat system", "wordenc", expectedCRCs[7], 50);
			StreamLoader soundArchive = streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55);
			streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55);
			byteGroundArray = new byte[4][104][104];
			intGroundArray = new int[4][105][105];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				aClass11Array1230[j] = new CollisionMap();

			minimapImage = new Sprite(512, 512);
			StreamLoader streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60);
			drawLoadingText(40, "Connecting to update server");
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, this);
			Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);
			constructMusic();
			//repackCacheIndex(1); // repack models
			//repackCacheIndex(4); // repack maps
			drawLoadingText(50, "Unpacking media");
			loadCustomSprites();

			multiOverlay = new Sprite(streamLoader_2, "overlay_multiway", 0);
			mapBack = new Background(streamLoader_2, "mapback", 0);
			for (int j3 = 0; j3 <= 14; j3++)
				sideIcons[j3] = new Sprite(streamLoader_2, "sideicons", j3);
			compass = new Sprite(streamLoader_2, "compass", 0);
			try {
				for (int k3 = 0; k3 < 100; k3++)
					mapScenes[k3] = new Background(streamLoader_2, "mapscene", k3);
			} catch (Exception _ex) {
			}
			try {
				for (int l3 = 0; l3 < 100; l3++)
					mapFunctions[l3] = new Sprite(streamLoader_2, "mapfunction", l3);
			} catch (Exception _ex) {
			}
			try {
				for (int i4 = 0; i4 < 20; i4++)
					hitMarks[i4] = new Sprite(streamLoader_2, "hitmarks", i4);
			} catch (Exception _ex) {
			}
			try {
				for (int h1 = 0; h1 < 6; h1++)
					headIconsHint[h1] = new Sprite(streamLoader_2, "headicons_hint", h1);
			} catch (Exception _ex) {
			}
			try {
				for (int j4 = 0; j4 < 18; j4++)
					headIcons[j4] = new Sprite(streamLoader_2, "headicons_prayer", j4);
				for (int j45 = 0; j45 < 3; j45++)
					skullIcons[j45] = new Sprite(streamLoader_2, "headicons_pk", j45);
			} catch (Exception _ex) {
			}
			mapFlag = new Sprite(streamLoader_2, "mapmarker", 0);
			mapMarker = new Sprite(streamLoader_2, "mapmarker", 1);
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = new Sprite(streamLoader_2, "cross", k4);
			mapDotItem = new Sprite(streamLoader_2, "mapdots", 0);
			mapDotNPC = new Sprite(streamLoader_2, "mapdots", 1);
			mapDotPlayer = new Sprite(streamLoader_2, "mapdots", 2);
			mapDotFriend = new Sprite(streamLoader_2, "mapdots", 3);
			mapDotTeam = new Sprite(streamLoader_2, "mapdots", 4);
			mapDotClan = new Sprite(streamLoader_2, "mapdots", 5);
			scrollBar1 = new Sprite(streamLoader_2, "scrollbar", 0);
			scrollBar2 = new Sprite(streamLoader_2, "scrollbar", 1);
			for (int l4 = 0; l4 < modIcons.length; l4++)
				modIcons[l4] = new Background(streamLoader_2, "mod_icons", l4);
			Sprite sprite = new Sprite(streamLoader_2, "screenframe", 0);
			leftFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.method346(0, 0);
			sprite = new Sprite(streamLoader_2, "screenframe", 1);
			topFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.method346(0, 0);
			drawLogo();

			drawLoadingText(60, "Unpacking textures");

			Rasterizer.method368(streamLoader_3);
			Rasterizer.method372(0.80000000000000004D);
			Rasterizer.method367();

			drawLoadingText(70, "Unpacking config");

			Animation.unpackConfig(streamLoader);
			ObjectDefinition.unpackConfig(streamLoader);
			FloorDefinition.unpackConfig(streamLoader);
			ItemDefinition.unpackConfig(streamLoader);
			NPCDefinition.unpackConfig(streamLoader);
			IdentitiKit.unpackConfig(streamLoader);
			SpotAnim.unpackConfig(streamLoader);
			Varp.unpackConfig(streamLoader);
			VarBit.unpackConfig(streamLoader);
			ItemDefinition.isMembers = isMembers;

			drawLoadingText(80, "Unpacking sounds");

			byte[] data = soundArchive.getDataForName("sounds.dat");
			Stream buffer = new Stream(data);
			Sound.unpack(buffer);

			drawLoadingText(90, "Unpacking interfaces");

			TextDrawingArea aclass30_sub2_sub1_sub4s[] = { smallText, aTextDrawingArea_1271, chatTextDrawingArea, aTextDrawingArea_1273 };
			RSInterface.unpack(streamLoader_1, aclass30_sub2_sub1_sub4s, streamLoader_2);
			drawLoadingText(100, "Preparing game engine");
			for (int j6 = 0; j6 < 33; j6++) {
				int k6 = 999;
				int i7 = 0;
				for (int k7 = 0; k7 < 34; k7++) {
					if (mapBack.aByteArray1450[k7 + j6 * mapBack.anInt1452] == 0) {
						if (k6 == 999)
							k6 = k7;
						continue;
					}
					if (k6 == 999)
						continue;
					i7 = k7;
					break;
				}
				anIntArray968[j6] = k6;
				anIntArray1057[j6] = i7 - k6;
			}
			for (int l6 = 1; l6 < 153; l6++) {
				int j7 = 999;
				int l7 = 0;
				for (int j8 = 24; j8 < 177; j8++) {
					if (mapBack.aByteArray1450[j8 + l6 * mapBack.anInt1452] == 0 && (j8 > 34 || l6 > 34)) {
						if (j7 == 999) {
							j7 = j8;
						}
						continue;
					}
					if (j7 == 999) {
						continue;
					}
					l7 = j8;
					break;
				}
				anIntArray1052[l6 - 1] = j7 - 24;
				anIntArray1229[l6 - 1] = l7 - j7;
			}
			setBounds();
			Censor.loadConfig(streamLoader_4);
			mouseDetection = new MouseDetection(this);
			startRunnable(mouseDetection, 10);
			Animable_Sub5.clientInstance = this;
			ObjectDefinition.clientInstance = this;
			NPCDefinition.clientInstance = this;
			sendStartupText();
			welcomeScreenRaised = true;
			/*
			 * Draw the background again to cover up the loading text after
			 * everything is loaded
			 */
			readData();
			drawLogo();
			return;
		} catch (Exception exception) {
			exception.printStackTrace();
			Signlink.reporterror("loaderror " + loadingString + " " + loadingPercent);
		}
		loadingError = true;
	}

	/**
	 * Loads custom sprites
	 */
	private void loadCustomSprites() {

		/*
		 * Loads the images from a local directory when false, else loads from
		 * cache sprite
		 */
		if (!Constants.LOAD_SPRITE_CACHE) {
			File directory = new File(Signlink.findcachedir() + "custom_sprites/");
			File[] file = directory.listFiles();
			int size = file.length;
			cacheSprite = new Sprite[size];

			/*
			 * Loop through all of the sprites and create them
			 */
			for (int i = 0; i < size; i++) {
				cacheSprite[i] = Sprite.createCustomSprite("" + i);
			}

			System.out.println("Loaded a total of " + size + " cache sprites.");
		} else {
			SpriteLoader.loadSprites();
			cacheSprite = SpriteLoader.sprites;
		}
	}

	private void method91(Stream stream, int i) {
		while (stream.bitPosition + 10 < i * 8) {
			int j = stream.readBits(11);
			if (j == 2047)
				break;
			if (playerArray[j] == null) {
				playerArray[j] = new Player();
				if (aStreamArray895s[j] != null)
					playerArray[j].updatePlayer(aStreamArray895s[j]);
			}
			playerIndices[playerCount++] = j;
			Player player = playerArray[j];
			player.anInt1537 = loopCycle;
			int k = stream.readBits(1);
			if (k == 1)
				anIntArray894[anInt893++] = j;
			int l = stream.readBits(1);
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(5);
			if (j1 > 15)
				j1 -= 32;
			player.realPlane = stream.readBits(2);
			player.setPos(myPlayer.smallX[0] + j1, myPlayer.smallY[0] + i1, l == 1);
		}
		stream.finishBitAccess();
	}

	public String indexLocation(int cacheIndex, int index) {
		return Signlink.findcachedir() + "index" + cacheIndex + "/" + (index != -1 ? index + ".gz" : "");
	}

	public void repackCacheIndex(int cacheIndex) {
		System.out.println("Started repacking index " + cacheIndex + ".");
		int indexLength = new File(indexLocation(cacheIndex, -1)).listFiles().length;
		File[] file = new File(indexLocation(cacheIndex, -1)).listFiles();
		try {
			for (int index = 0; index < indexLength; index++) {
				int fileIndex = Integer.parseInt(getFileNameWithoutExtension(file[index].toString()));
				byte[] data = fileToByteArray(cacheIndex, fileIndex);
				if (data != null && data.length > 0) {
					decompressors[cacheIndex].method234(data.length, data, fileIndex);
					System.out.println("Repacked " + fileIndex + ".");
				} else {
					System.out.println("Unable to locate index " + fileIndex + ".");
				}
			}
		} catch (Exception e) {
			System.out.println("Error packing cache index " + cacheIndex + ".");
		}
		System.out.println("Finished repacking " + cacheIndex + ".");
	}

	public byte[] fileToByteArray(int cacheIndex, int index) {
		try {
			if (indexLocation(cacheIndex, index).length() <= 0 || indexLocation(cacheIndex, index) == null) {
				return null;
			}
			File file = new File(indexLocation(cacheIndex, index));
			byte[] fileData = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(fileData);
			fis.close();
			return fileData;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean inCircle(int circleX, int circleY, int clickX, int clickY, int radius) {
		return java.lang.Math.pow((circleX + radius - clickX), 2) + java.lang.Math.pow((circleY + radius - clickY), 2) < java.lang.Math.pow(radius, 2);
	}

	private void processMainScreenClick() {
		if (anInt1021 != 0)
			return;
		if (super.clickMode3 == 1) {
			int i = super.saveClickX - 25 - 547;
			int j = super.saveClickY - 5 - 3;
			if (frameMode != ScreenMode.FIXED) {
				i = super.saveClickX - (frameWidth - 182 + 24);
				j = super.saveClickY - 8;
			}
			if (inCircle(0, 0, i, j, 76) && mouseMapPosition() && !runHover) {
				i -= 73;
				j -= 75;
				int k = minimapInt1 + minimapInt2 & 0x7ff;
				int i1 = Rasterizer.SINE[k];
				int j1 = Rasterizer.COSINE[k];
				i1 = i1 * (minimapInt3 + 256) >> 8;
				j1 = j1 * (minimapInt3 + 256) >> 8;
				int k1 = j * i1 + i * j1 >> 11;
				int l1 = j * j1 - i * i1 >> 11;
				int i2 = myPlayer.x + k1 >> 7;
				int j2 = myPlayer.y - l1 >> 7;
				boolean flag1 = doWalkTo(1, 0, 0, 0, myPlayer.smallY[0], 0, 0, j2, myPlayer.smallX[0], true, i2);
				if (flag1) {
					stream.writeLEShort(i);
					stream.writeLEShort(j);
					stream.writeShort(minimapInt1);
					stream.writeLEShort(57);
					stream.writeLEShort(minimapInt2);
					stream.writeLEShort(minimapInt3);
					stream.writeLEShort(89);
					stream.writeShort(myPlayer.x);
					stream.writeShort(myPlayer.y);
					stream.writeLEShort(anInt1264);
					stream.writeLEShort(63);
				}
			}
		}
	}

	private String interfaceIntToString(int j) {
		if (j < 0x3b9ac9ff)
			return String.valueOf(j);
		else
			return "*";
	}

	private void showErrorScreen() {
		Graphics g = getGameComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 765, 503);
		method4(1);
		if (loadingError) {
			aBoolean831 = false;
			g.setFont(new Font("Helvetica", 1, 16));
			g.setColor(Color.yellow);
			int k = 35;
			g.drawString("Sorry, an error has occured whilst loading " + Constants.CLIENT_NAME, 30, k);
			k += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, k);
			k += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, k);
			k += 30;
			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, k);
			k += 30;
			g.drawString("3: Try using a different game-world", 30, k);
			k += 30;
			g.drawString("4: Try rebooting your computer", 30, k);
			k += 30;
			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, k);
		}
		if (genericLoadingError) {
			aBoolean831 = false;
			g.setFont(new Font("Helvetica", 1, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play " + Constants.CLIENT_NAME + " make sure you play from", 50, 100);
			g.drawString("the Exile launcher.", 50, 150);
		}
		if (rsAlreadyLoaded) {
			aBoolean831 = false;
			g.setColor(Color.yellow);
			int l = 35;
			g.drawString("Error a copy of " + Constants.CLIENT_NAME + " already appears to be loaded", 30, l);
			l += 50;
			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, l);
			l += 50;
			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", 1, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, l);
			l += 30;
			g.drawString("2: Try rebooting your computer, and reloading", 30, l);
			l += 30;
		}
	}

	@Override
	public URL getCodeBase() {
		try {
			return new URL(clientStatus.getAddress() + ":" + (80 + portOff));
		} catch (Exception _ex) {
		}
		return null;
	}

	private void method95() {
		for (int j = 0; j < npcCount; j++) {
			int k = npcIndices[j];
			NPC npc = npcArray[k];
			if (npc != null)
				method96(npc);
		}
	}

	private void method96(Entity entity) {
		if (entity.x < 128 || entity.y < 128 || entity.x >= 13184 || entity.y >= 13184) {
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity == myPlayer && (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776)) {
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity.anInt1547 > loopCycle)
			method97(entity);
		else if (entity.anInt1548 >= loopCycle)
			method98(entity);
		else
			method99(entity);
		method100(entity);
		method101(entity);
	}

	private void method97(Entity entity) {
		int i = entity.anInt1547 - loopCycle;
		int j = entity.anInt1543 * 128 + entity.anInt1540 * 64;
		int k = entity.anInt1545 * 128 + entity.anInt1540 * 64;
		entity.x += (j - entity.x) / i;
		entity.y += (k - entity.y) / i;
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
	}

	private void method98(Entity entity) {
		if (entity.anInt1548 == loopCycle || entity.anim == -1 || entity.anInt1529 != 0 || entity.anInt1528 + 1 > Animation.anims[entity.anim].method258(entity.anInt1527)) {
			int i = entity.anInt1548 - entity.anInt1547;
			int j = loopCycle - entity.anInt1547;
			int k = entity.anInt1543 * 128 + entity.anInt1540 * 64;
			int l = entity.anInt1545 * 128 + entity.anInt1540 * 64;
			int i1 = entity.anInt1544 * 128 + entity.anInt1540 * 64;
			int j1 = entity.anInt1546 * 128 + entity.anInt1540 * 64;
			entity.x = (k * (i - j) + i1 * j) / i;
			entity.y = (l * (i - j) + j1 * j) / i;
		}
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
		entity.anInt1552 = entity.turnDirection;
	}

	private void method99(Entity entity) {
		entity.anInt1517 = entity.standAnimation;
		if (entity.smallXYIndex == 0) {
			entity.anInt1503 = 0;
			return;
		}
		if (entity.anim != -1 && entity.anInt1529 == 0) {
			Animation animation = Animation.anims[entity.anim];
			if (entity.anInt1542 > 0 && animation.anInt363 == 0) {
				entity.anInt1503++;
				return;
			}
			if (entity.anInt1542 <= 0 && animation.anInt364 == 0) {
				entity.anInt1503++;
				return;
			}
		}
		int i = entity.x;
		int j = entity.y;
		int k = entity.smallX[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
		int l = entity.smallY[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
		if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256) {
			entity.x = k;
			entity.y = l;
			return;
		}
		if (i < k) {
			if (j < l)
				entity.turnDirection = 1280;
			else if (j > l)
				entity.turnDirection = 1792;
			else
				entity.turnDirection = 1536;
		} else if (i > k) {
			if (j < l)
				entity.turnDirection = 768;
			else if (j > l)
				entity.turnDirection = 256;
			else
				entity.turnDirection = 512;
		} else if (j < l)
			entity.turnDirection = 1024;
		else
			entity.turnDirection = 0;
		int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (i1 > 1024)
			i1 -= 2048;
		int j1 = entity.turn180Animation;
		if (i1 >= -256 && i1 <= 256)
			j1 = entity.walkAnimation;
		else if (i1 >= 256 && i1 < 768)
			j1 = entity.turn90CCW;
		else if (i1 >= -768 && i1 <= -256)
			j1 = entity.turn90CW;
		if (j1 == -1)
			j1 = entity.walkAnimation;
		entity.anInt1517 = j1;
		int k1 = 4;
		if (entity.anInt1552 != entity.turnDirection && entity.interactingEntity == -1 && entity.anInt1504 != 0)
			k1 = 2;
		if (entity.smallXYIndex > 2)
			k1 = 6;
		if (entity.smallXYIndex > 3)
			k1 = 8;
		if (entity.anInt1503 > 0 && entity.smallXYIndex > 1) {
			k1 = 8;
			entity.anInt1503--;
		}
		if (entity.aBooleanArray1553[entity.smallXYIndex - 1])
			k1 <<= 1;
		if (k1 >= 8 && entity.anInt1517 == entity.walkAnimation && entity.runAnimation != -1)
			entity.anInt1517 = entity.runAnimation;
		if (i < k) {
			entity.x += k1;
			if (entity.x > k)
				entity.x = k;
		} else if (i > k) {
			entity.x -= k1;
			if (entity.x < k)
				entity.x = k;
		}
		if (j < l) {
			entity.y += k1;
			if (entity.y > l)
				entity.y = l;
		} else if (j > l) {
			entity.y -= k1;
			if (entity.y < l)
				entity.y = l;
		}
		if (entity.x == k && entity.y == l) {
			entity.smallXYIndex--;
			if (entity.anInt1542 > 0)
				entity.anInt1542--;
		}
	}

	private void method100(Entity entity) {
		if (entity.anInt1504 == 0)
			return;
		if (entity.interactingEntity != -1 && entity.interactingEntity < 32768) {
			NPC npc = npcArray[entity.interactingEntity];
			if (npc != null) {
				int i1 = entity.x - npc.x;
				int k1 = entity.y - npc.y;
				if (i1 != 0 || k1 != 0)
					entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
			}
		}
		if (entity.interactingEntity >= 32768) {
			int j = entity.interactingEntity - 32768;
			if (j == unknownInt10)
				j = myPlayerIndex;
			Player player = playerArray[j];
			if (player != null) {
				int l1 = entity.x - player.x;
				int i2 = entity.y - player.y;
				if (l1 != 0 || i2 != 0)
					entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
			}
		}
		if ((entity.anInt1538 != 0 || entity.anInt1539 != 0) && (entity.smallXYIndex == 0 || entity.anInt1503 > 0)) {
			int k = entity.x - (entity.anInt1538 - baseX - baseX) * 64;
			int j1 = entity.y - (entity.anInt1539 - baseY - baseY) * 64;
			if (k != 0 || j1 != 0)
				entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
			entity.anInt1538 = 0;
			entity.anInt1539 = 0;
		}
		int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (l != 0) {
			if (l < entity.anInt1504 || l > 2048 - entity.anInt1504)
				entity.anInt1552 = entity.turnDirection;
			else if (l > 1024)
				entity.anInt1552 -= entity.anInt1504;
			else
				entity.anInt1552 += entity.anInt1504;
			entity.anInt1552 &= 0x7ff;
			if (entity.anInt1517 == entity.standAnimation && entity.anInt1552 != entity.turnDirection) {
				if (entity.turnAnimation != -1) {
					entity.anInt1517 = entity.turnAnimation;
					return;
				}
				entity.anInt1517 = entity.walkAnimation;
			}
		}
	}

	public void method101(Entity entity) {
		entity.aBoolean1541 = false;
		if (entity.anInt1517 != -1) {
			if (entity.anInt1517 > Animation.anims.length)
				entity.anInt1517 = 0;
			Animation animation = Animation.anims[entity.anInt1517];

			if (animation == null) {
				return;
			}
			entity.anInt1519++;
			if (entity.anInt1518 >= Animation.anims.length) {
				entity.anInt1518 = -1;
			}
			if (entity.anInt1518 < animation.anInt352 && entity.anInt1519 > animation.method258(entity.anInt1518)) {
				entity.anInt1519 = 1;
				entity.anInt1518++;
				entity.nextIdleAnimationFrame++;
			}
			entity.nextIdleAnimationFrame = entity.anInt1518 + 1;
			if (entity.nextIdleAnimationFrame >= animation.anInt352) {
				if (entity.nextIdleAnimationFrame >= animation.anInt352)
					entity.nextIdleAnimationFrame = 0;
			}
			if (entity.anInt1518 >= animation.anInt352) {
				entity.anInt1519 = 1;
				entity.anInt1518 = 0;
			}
		}
		if (entity.anInt1520 != -1 && loopCycle >= entity.anInt1523) {
			if (entity.anInt1521 < 0)
				entity.anInt1521 = 0;
			if (entity.anInt1520 > SpotAnim.cache.length) {
				entity.anInt1520 = 0;
			}
			Animation animation_1 = SpotAnim.cache[entity.anInt1520].aAnimation_407;
			if (animation_1 == null) {
				System.out.println("Null Graphic: " + entity.anInt1520);
				entity.anInt1520 = -1;
				return;
			}
			for (entity.anInt1522++; entity.anInt1521 < animation_1.anInt352 && entity.anInt1522 > animation_1.method258(entity.anInt1521); entity.anInt1521++)
				entity.anInt1522 -= animation_1.method258(entity.anInt1521);

			if (entity.anInt1521 >= animation_1.anInt352 && (entity.anInt1521 < 0 || entity.anInt1521 >= animation_1.anInt352))
				entity.anInt1520 = -1;
			entity.nextGraphicsAnimationFrame = entity.anInt1521 + 1;
			if (entity.nextGraphicsAnimationFrame >= animation_1.anInt352) {
				if (entity.nextGraphicsAnimationFrame < 0 || entity.nextGraphicsAnimationFrame >= animation_1.anInt352)
					entity.anInt1520 = -1;
			}
		}
		if (entity.anim != -1 && entity.anInt1529 <= 1) {
			if (entity.anim >= Animation.anims.length) {
				entity.anim = -1;
			}
			Animation animation_2 = Animation.anims[entity.anim];
			if (animation_2.anInt363 == 1 && entity.anInt1542 > 0 && entity.anInt1547 <= loopCycle && entity.anInt1548 < loopCycle) {
				entity.anInt1529 = 1;
				return;
			}
		}
		if (entity.anim != -1 && entity.anInt1529 == 0) {
			Animation animation_3 = Animation.anims[entity.anim];
			for (entity.anInt1528++; entity.anInt1527 < animation_3.anInt352 && entity.anInt1528 > animation_3.method258(entity.anInt1527); entity.anInt1527++)
				entity.anInt1528 -= animation_3.method258(entity.anInt1527);

			if (entity.anInt1527 >= animation_3.anInt352) {
				entity.anInt1527 -= animation_3.anInt356;
				entity.anInt1530++;
				if (entity.anInt1530 >= animation_3.anInt362)
					entity.anim = -1;
				if (entity.anInt1527 < 0 || entity.anInt1527 >= animation_3.anInt352)
					entity.anim = -1;
			}
			entity.nextAnimationFrame = entity.anInt1527 + 1;
			if (entity.nextAnimationFrame >= animation_3.anInt352) {
				if (entity.anInt1530 >= animation_3.anInt362)
					entity.nextAnimationFrame = entity.anInt1527 + 1;
				if (entity.nextAnimationFrame < 0 || entity.nextAnimationFrame >= animation_3.anInt352)
					entity.nextAnimationFrame = entity.anInt1527;
			}
			entity.aBoolean1541 = animation_3.aBoolean358;
		}
		if (entity.anInt1529 > 0)
			entity.anInt1529--;
	}

	private void drawGameScreen() {

		if (fullscreenInterfaceID != -1 && (loadingStage == 2 || super.fullGameScreen != null)) {
			if (loadingStage == 2) {
				method119(anInt945, fullscreenInterfaceID);
				if (openInterfaceID != -1) {
					method119(anInt945, openInterfaceID);
				}
				anInt945 = 0;
				resetAllImageProducers();
				super.fullGameScreen.initDrawingArea();
				Rasterizer.lineOffsets = fullScreenTextureArray;
				DrawingArea.setAllPixelsToZero();
				welcomeScreenRaised = true;
				if (openInterfaceID != -1) {
					RSInterface rsInterface_1 = RSInterface.interfaceCache[openInterfaceID];
					if (rsInterface_1.width == 512 && rsInterface_1.height == 334 && rsInterface_1.type == 0) {
						rsInterface_1.width = 765;
						rsInterface_1.height = 503;
					}
					drawInterface(0, 0, rsInterface_1, 8);
				}
				RSInterface rsInterface = RSInterface.interfaceCache[fullscreenInterfaceID];
				if (rsInterface.width == 512 && rsInterface.height == 334 && rsInterface.type == 0) {
					rsInterface.width = 765;
					rsInterface.height = 503;
				}
				drawInterface(0, 0, rsInterface, 8);
				if (!menuOpen) {
					processRightClick();
					drawTooltip();
				} else {
					drawMenu(frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
				}
			}
			drawCount++;
			super.fullGameScreen.drawGraphics(0, super.graphics, 0);
			return;
		} else {
			if (drawCount != 0) {
				resetImageProducers2();
			}
		}
		if (welcomeScreenRaised) {
			welcomeScreenRaised = false;
			if (frameMode == ScreenMode.FIXED) {
				topFrame.drawGraphics(0, super.graphics, 0);
				leftFrame.drawGraphics(4, super.graphics, 0);
			}
			inputTaken = true;
			tabAreaAltered = true;
			if (loadingStage != 2) {
				if (frameMode == ScreenMode.FIXED) {
					aRSImageProducer_1165.drawGraphics(frameMode == ScreenMode.FIXED ? 4 : 0, super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0);
					aRSImageProducer_1164.drawGraphics(0, super.graphics, 516);
				}
			}
		}
		if (invOverlayInterfaceID != -1) {
			method119(anInt945, invOverlayInterfaceID);
		}
		drawTabArea();
		extendChatArea();
		if (backDialogID == -1) {
			aClass9_1059.scrollPosition = anInt1211 - anInt1089 - 110;
			if (super.mouseX >= 496 && super.mouseX <= 511 && super.mouseY > (frameMode == ScreenMode.FIXED ? 345 : frameHeight - 158 - extendChatArea))
				method65(494, 110, super.mouseX, super.mouseY - (frameMode == ScreenMode.FIXED ? 345 : frameHeight - 158 - extendChatArea), aClass9_1059, 0, false, anInt1211);
			int i = anInt1211 - 110 - aClass9_1059.scrollPosition;
			if (i < 0) {
				i = 0;
			}
			if (i > anInt1211 - 110) {
				i = anInt1211 - 110;
			}
			if (anInt1089 != i) {
				anInt1089 = i;
				inputTaken = true;
			}
		}
		if (backDialogID != -1) {
			boolean flag2 = method119(anInt945, backDialogID);
			if (flag2)
				inputTaken = true;
		}
		if (atInventoryInterfaceType == 3)
			inputTaken = true;
		if (activeInterfaceType == 3)
			inputTaken = true;
		if (aString844 != null)
			inputTaken = true;
		if (menuOpen && menuScreenArea == 2)
			inputTaken = true;
		if (inputTaken || prototypeOpen || arpgSkillsOpen) {
			drawChatArea();
			inputTaken = false;
		}
		if (loadingStage == 2)
			method146();
		if (loadingStage == 2) {
			if (frameMode == ScreenMode.FIXED) {
				drawMinimap();
				aRSImageProducer_1164.drawGraphics(0, super.graphics, 516);
			}
		}
		if (anInt1054 != -1)
			tabAreaAltered = true;
		if (tabAreaAltered) {
			if (anInt1054 != -1 && anInt1054 == tabID) {
				anInt1054 = -1;
				stream.createFrame(120);
				stream.writeLEShort(tabID);
			}
			tabAreaAltered = false;
			aRSImageProducer_1125.initDrawingArea();
			aRSImageProducer_1165.initDrawingArea();
		}
		anInt945 = 0;
	}

	private boolean buildFriendsListMenu(RSInterface class9) {
		int i = class9.contentType;
		if (i >= 1 && i <= 200 || i >= 701 && i <= 900) {
			if (i >= 801)
				i -= 701;
			else if (i >= 701)
				i -= 601;
			else if (i >= 101)
				i -= 101;
			else
				i--;
			menuActionName[menuActionRow] = "Remove @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 792;
			menuActionRow++;
			menuActionName[menuActionRow] = "Message @whi@" + friendsList[i];
			menuActionID[menuActionRow] = 639;
			menuActionRow++;
			return true;
		}
		if (i >= 401 && i <= 500) {
			menuActionName[menuActionRow] = "Remove @whi@" + class9.message;
			menuActionID[menuActionRow] = 322;
			menuActionRow++;
			return true;
		} else {
			return false;
		}
	}

	private void method104() {
		Animable_Sub3 class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056.reverseGetFirst();
		for (; class30_sub2_sub4_sub3 != null; class30_sub2_sub4_sub3 = (Animable_Sub3) aClass19_1056.reverseGetNext())
			if (class30_sub2_sub4_sub3.anInt1560 != plane || class30_sub2_sub4_sub3.aBoolean1567)
				class30_sub2_sub4_sub3.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub3.anInt1564) {
				class30_sub2_sub4_sub3.method454(anInt945);
				if (class30_sub2_sub4_sub3.aBoolean1567)
					class30_sub2_sub4_sub3.unlink();
				else
					worldController.method285(class30_sub2_sub4_sub3.anInt1560, 0, class30_sub2_sub4_sub3.anInt1563, -1, class30_sub2_sub4_sub3.anInt1562, 60, class30_sub2_sub4_sub3.anInt1561,
							class30_sub2_sub4_sub3, false);
			}

	}

	public void drawBlackBox(int xPos, int yPos) {
		DrawingArea.drawPixels(71, yPos - 1, xPos - 2, 0x726451, 1);
		DrawingArea.drawPixels(69, yPos, xPos + 174, 0x726451, 1);
		DrawingArea.drawPixels(1, yPos - 2, xPos - 2, 0x726451, 178);
		DrawingArea.drawPixels(1, yPos + 68, xPos, 0x726451, 174);
		DrawingArea.drawPixels(71, yPos - 1, xPos - 1, 0x2E2B23, 1);
		DrawingArea.drawPixels(71, yPos - 1, xPos + 175, 0x2E2B23, 1);
		DrawingArea.drawPixels(1, yPos - 1, xPos, 0x2E2B23, 175);
		DrawingArea.drawPixels(1, yPos + 69, xPos, 0x2E2B23, 175);
		DrawingArea.method335(0, yPos, 174, 68, 220, xPos);
	}

	private void drawInterface(int scrollPosition, int xPosition, RSInterface rsInterface, int yPosition) {
        if(rsInterface.id==3917){com.koios.client.arpg.CharacterPanel.draw(com.koios.client.arpg.ExileTheme.body(),xPosition,yPosition,arpgCharacterLines);return;}
		if (rsInterface == null)
			rsInterface = RSInterface.interfaceCache[21356];
		if (rsInterface.type != 0 || rsInterface.children == null)
			return;
		if (rsInterface.isMouseoverTriggered && anInt1026 != rsInterface.id && anInt1048 != rsInterface.id && anInt1039 != rsInterface.id)
			return;
		int topX = DrawingArea.topX;
		int topY = DrawingArea.topY;
		int bottomX = DrawingArea.bottomX;
		int bottomY = DrawingArea.bottomY;
		DrawingArea.setDrawingArea(yPosition + rsInterface.height, xPosition, xPosition + rsInterface.width, yPosition);
		int totalChildren = rsInterface.children.length;
		int alpha = rsInterface.transparency;
		for (int childId = 0; childId < totalChildren; childId++) {
			int drawX = rsInterface.childX[childId] + xPosition;
			int drawY = (rsInterface.childY[childId] + yPosition) - scrollPosition;
			RSInterface child = RSInterface.interfaceCache[rsInterface.children[childId]];
			drawX += child.xOffset;
			drawY += child.yOffset;
			if (child.contentType > 0)
				drawFriendsListOrWelcomeScreen(child);
			// here
			int[] IDs = { 1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258, 1267, 1274, 1283, 1573, 1290, 1299, 1308, 1315, 1324, 1333, 1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583,
					12038, 1414, 1421, 1430, 1437, 1446, 1453, 1460, 1469, 15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503, 1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426, 12436, 12446,
					12456, 6004, 18471,
					/* Ancients */
					12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012, 13054, 12920, 12882, 13062, 12952, 13000, 13070, 12912, 12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096 };
			for (int m5 = 0; m5 < IDs.length; m5++) {
				if (child.id == IDs[m5] + 1) {
					if (m5 > 61)
						drawBlackBox(drawX + 1, drawY);
					else
						drawBlackBox(drawX, drawY + 1);
				}
			}
			int[] runeChildren = { 1202, 1203, 1209, 1210, 1211, 1218, 1219, 1220, 1227, 1228, 1234, 1235, 1236, 1243, 1244, 1245, 1252, 1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279,
					1286, 1287, 1293, 1294, 1295, 1302, 1303, 1304, 1311, 1312, 1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343, 1344, 1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377,
					1378, 1384, 1385, 1391, 1392, 1393, 1400, 1401, 1407, 1408, 1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441, 1442, 1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473,
					1474, 1481, 1482, 1488, 1489, 1490, 1497, 1498, 1499, 1506, 1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534, 1535, 1547, 1548, 1549, 1556, 1557, 1558, 1566, 1567, 1568,
					1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598, 1605, 1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639, 1640, 6007, 6008, 6011, 8673, 8674, 12041, 12042, 12429,
					12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451, 12459, 12460, 15881, 15882, 15885, 18474, 18475, 18478 };
			for (int r = 0; r < runeChildren.length; r++)
				if (child.id == runeChildren[r])
					child.modelZoom = 775;

			if (child.type == 0) {
				if (child.scrollPosition > child.scrollMax - child.height)
					child.scrollPosition = child.scrollMax - child.height;
				if (child.scrollPosition < 0)
					child.scrollPosition = 0;
				drawInterface(child.scrollPosition, drawX, child, drawY);
				if (child.scrollMax > child.height)
					drawScrollbar(child.height, child.scrollPosition, drawY, drawX + child.width, child.scrollMax, false);
			} else if (child.type != 1)
				if (child.type == 2) {
					int i3 = 0;
					for (int h = 0; h < child.height; h++) {
						for (int w = 0; w < child.width; w++) {
							int k5 = drawX + w * (32 + child.invSpritePadX);
							int j6 = drawY + h * (32 + child.invSpritePadY);
							if (i3 < 20) {
								k5 += child.spritesX[i3];
								j6 += child.spritesY[i3];
							}
                            if(child.id==3214){com.koios.client.arpg.ItemTooltipCache.Entry entry=arpgTooltips.get(child.id,i3,child.container[i3]-1);com.koios.client.arpg.SidebarChrome.slot(k5,j6,arpgHoverWidget==child.id&&arpgHoverSlot==i3,itemSelected==1&&anInt1283==i3&&anInt1284==child.id,entry==null?0:entry.color);}
							if (child.container[i3] > 0) {
								int k6 = 0;
								int j7 = 0;
								int j9 = child.container[i3] - 1;
								if (k5 > DrawingArea.topX - 32 && k5 < DrawingArea.bottomX && j6 > DrawingArea.topY - 32 && j6 < DrawingArea.bottomY || activeInterfaceType != 0 && itemFrom == i3) {
									int l9 = 0;
									if (itemSelected == 1 && anInt1283 == i3 && anInt1284 == child.id)
										l9 = 0xffffff;
									Sprite selectedItem = ItemDefinition.getSprite(j9, child.containerSizes[i3], l9);
									if (selectedItem != null) {
										if (activeInterfaceType != 0 && itemFrom == i3 && anInt1084 == child.id) {
											k6 = super.mouseX - anInt1087;
											j7 = super.mouseY - anInt1088;
											if (k6 < 5 && k6 > -5)
												k6 = 0;
											if (j7 < 5 && j7 > -5)
												j7 = 0;
											if (anInt989 < 10) {
												k6 = 0;
												j7 = 0;
											}
											selectedItem.drawSprite1(k5 + k6, j6 + j7);
											if (j6 + j7 < DrawingArea.topY && rsInterface.scrollPosition > 0) {
												int i10 = (anInt945 * (DrawingArea.topY - j6 - j7)) / 3;
												if (i10 > anInt945 * 10)
													i10 = anInt945 * 10;
												if (i10 > rsInterface.scrollPosition)
													i10 = rsInterface.scrollPosition;
												rsInterface.scrollPosition -= i10;
												anInt1088 += i10;
											}
											if (j6 + j7 + 32 > DrawingArea.bottomY && rsInterface.scrollPosition < rsInterface.scrollMax - rsInterface.height) {
												int j10 = (anInt945 * ((j6 + j7 + 32) - DrawingArea.bottomY)) / 3;
												if (j10 > anInt945 * 10)
													j10 = anInt945 * 10;
												if (j10 > rsInterface.scrollMax - rsInterface.height - rsInterface.scrollPosition)
													j10 = rsInterface.scrollMax - rsInterface.height - rsInterface.scrollPosition;
												rsInterface.scrollPosition += j10;
												anInt1088 -= j10;
											}
										} else if (atInventoryInterfaceType != 0 && atInventoryIndex == i3 && atInventoryInterface == child.id)
											selectedItem.drawSprite1(k5, j6);
										else
											selectedItem.drawSprite(k5, j6);
										if (!((child.id >= (50051)) && (child.id <= (50059))) && (selectedItem.maxWidth == 33 || child.containerSizes[i3] != 1)) {
											int k10 = child.containerSizes[i3];

											smallText.method385(0, intToKOrMil(k10), j6 + 10 + j7, k5 + 1 + k6);
											if (k10 >= 1)
												smallText.method385(child.id==3214?0xD7C69B:0xFFFF00, intToKOrMil(k10), j6 + 9 + j7, k5 + k6);
											if (k10 >= 100000)
												smallText.method385(0xFFFFFF, intToKOrMil(k10), j6 + 9 + j7, k5 + k6);
											if (k10 >= 10000000)
												smallText.method385(0x00FF80, intToKOrMil(k10), j6 + 9 + j7, k5 + k6);
										}
									}
								}
							} else if (child.sprites != null && i3 < 20) {
								Sprite class30_sub2_sub1_sub1_1 = child.sprites[i3];
								if (class30_sub2_sub1_sub1_1 != null)
									class30_sub2_sub1_sub1_1.drawSprite(k5, j6);
							}
							i3++;
						}
					}
				} else if (child.type == 3) {
					boolean flag = false;
					if (anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id)
						flag = true;
					int j3;
					if (interfaceIsSelected(child)) {
						j3 = child.anInt219;
						if (flag && child.anInt239 != 0)
							j3 = child.anInt239;
					} else {
						j3 = child.textColor;
						if (flag && child.textColorSelected != 0)
							j3 = child.textColorSelected;
					}
					if (child.opacity == 0) {
						if (child.aBoolean227)
							DrawingArea.drawPixels(child.height, drawY, drawX, j3, child.width);
						else
							DrawingArea.fillPixels(drawX, child.width, child.height, j3, drawY);
					} else if (child.aBoolean227)
						DrawingArea.method335(j3, drawY, child.width, child.height, 256 - (child.opacity & 0xff), drawX);
					else
						DrawingArea.method338(drawY, child.height, 256 - (child.opacity & 0xff), j3, child.width, drawX);
				} else if (child.type == 4) {
					TextDrawingArea textDrawingArea = child.textDrawingAreas;
					String s = child.message;
					boolean flag1 = false;
					if (anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id)
						flag1 = true;
					int fontColor;
					if (interfaceIsSelected(child)) {
						fontColor = child.anInt219;
						if (flag1 && child.anInt239 != 0)
							fontColor = child.anInt239;
						if (child.aString228.length() > 0)
							s = child.aString228;
					} else {
						fontColor = child.textColor;
						if (flag1 && child.textColorSelected != 0)
							fontColor = child.textColorSelected;
					}
					if (child.atActionType == 6 && aBoolean1149) {
						s = "Please wait...";
						fontColor = child.textColor;
					}
					if (DrawingArea.width == 516) {
						if (fontColor == 0xffff00)
							fontColor = 255;
						if (fontColor == 49152)
							fontColor = 0xffffff;
					}
					if (frameMode != ScreenMode.FIXED) {
						if ((backDialogID != -1 || dialogID != -1 || child.message.contains("Click here to continue")) && (rsInterface.id == backDialogID || rsInterface.id == dialogID)) {
							if (fontColor == 0xffff00) {
								fontColor = 255;
							}
							if (fontColor == 49152) {
								fontColor = 0xffffff;
							}
						}
					}
					if ((child.parentID == 1151) || (child.parentID == 12855)) {
						switch (fontColor) {
						case 16773120:
							fontColor = 0xFE981F;
							break;
						case 7040819:
							fontColor = 0xAF6A1A;
							break;
						}
					}
					for (int currentPosition = drawY + textDrawingArea.anInt1497; s.length() > 0; currentPosition += textDrawingArea.anInt1497) {
						if (s.indexOf("%") != -1) {
							do {
								int k7 = s.indexOf("%1");
								if (k7 == -1)
									break;
								if (child.id < 4000 || child.id > 5000 && child.id != 13921 && child.id != 13922 && child.id != 12171 && child.id != 12172)
									s = s.substring(0, k7) + methodR(extractInterfaceValues(child, 0)) + s.substring(k7 + 2);
								else
									s = s.substring(0, k7) + interfaceIntToString(extractInterfaceValues(child, 0)) + s.substring(k7 + 2);
							} while (true);
							do {
								int l7 = s.indexOf("%2");
								if (l7 == -1)
									break;
								s = s.substring(0, l7) + interfaceIntToString(extractInterfaceValues(child, 1)) + s.substring(l7 + 2);
							} while (true);
							do {
								int i8 = s.indexOf("%3");
								if (i8 == -1)
									break;
								s = s.substring(0, i8) + interfaceIntToString(extractInterfaceValues(child, 2)) + s.substring(i8 + 2);
							} while (true);
							do {
								int j8 = s.indexOf("%4");
								if (j8 == -1)
									break;
								s = s.substring(0, j8) + interfaceIntToString(extractInterfaceValues(child, 3)) + s.substring(j8 + 2);
							} while (true);
							do {
								int k8 = s.indexOf("%5");
								if (k8 == -1)
									break;
								s = s.substring(0, k8) + interfaceIntToString(extractInterfaceValues(child, 4)) + s.substring(k8 + 2);
							} while (true);
						}
						int l8 = s.indexOf("\\n");
						String addText;
						if (l8 != -1) {
							addText = s.substring(0, l8);
							s = s.substring(l8 + 2);
						} else {
							addText = s;
							s = "";
						}

						switch (child.textAlign) {
						case 0:
							textDrawingArea.method389(child.textShadow, drawX, fontColor, addText, currentPosition);
							break;

						case 1:
							textDrawingArea.drawText(fontColor, drawX + (child.width / 2), addText, currentPosition, child.textShadow);
							break;

						case 2:
							textDrawingArea.method389(child.textShadow, drawX - textDrawingArea.getTextWidth(addText), fontColor, addText, currentPosition);
							break;

						case 3:
							textDrawingArea.drawCenteredString(drawX, currentPosition, addText, child.textColor, child.textShadow);
							break;
						}

					}
				} else if (child.type == 5) {
					Sprite sprite;
					if (interfaceIsSelected(child))
						sprite = child.sprite2;
					else
						sprite = child.sprite1;

					if (child.summonReq > 0) {
						if (child.summonReq <= statsMaximumLevel[SkillConstants.SUMMONING]) {
							sprite = child.sprite2;
						} else {
							sprite = child.sprite1;
						}
					}

					if ((child.id == 1164) || (child.id == 1167) || (child.id == 1170) || (child.id == 1174) || (child.id == 1540) || (child.id == 1541) || (child.id == 7455) || (child.id == 18470)
							|| (child.id == 13035) || (child.id == 13045) || (child.id == 13053) || (child.id == 13061) || (child.id == 13069) || (child.id == 13079) || (child.id == 30064)
							|| (child.id == 30075) || (child.id == 30083) || (child.id == 30106) || (child.id == 30114) || (child.id == 30106) || (child.id == 30170) || (child.id == 13087)
							|| (child.id == 30162) || (child.id == 13095)) {
						sprite = child.sprite2;
					}

					if (spellSelected == 1 && child.id == spellID && spellID != 0 && sprite != null) {
						sprite.drawSprite(drawX, drawY, 0xffffff);
					} else {
						if (sprite != null)
							if (child.drawsTransparent) {
								sprite.drawTransparentSprite(drawX, drawY, alpha);
							} else {
								sprite.drawSprite(drawX, drawY);
							}
					}
					if (autocast && child.id == autoCastId)
						cacheSprite[43].drawSprite(drawX - 3, drawY - 3);
					if (sprite != null)
						if (child.drawsTransparent) {
							sprite.drawSprite1(drawX, drawY);
						} else {
							sprite.drawSprite(drawX, drawY);
						}
				} else if (child.type == 6) {
					int k3 = Rasterizer.centerX;
					int j4 = Rasterizer.centerY;
					Rasterizer.centerX = drawX + child.width / 2;
					Rasterizer.centerY = drawY + child.height / 2;
					int i5 = Rasterizer.SINE[child.modelRotation1] * child.modelZoom >> 16;
					int l5 = Rasterizer.COSINE[child.modelRotation1] * child.modelZoom >> 16;
					boolean flag2 = interfaceIsSelected(child);
					int i7;
					if (flag2)
						i7 = child.anInt258;
					else
						i7 = child.anInt257;
					Model model;
					if (i7 == -1) {
						model = child.method209(-1, -1, flag2);
					} else {
						Animation animation = Animation.anims[i7];
						model = child.method209(animation.anIntArray354[child.anInt246], animation.anIntArray353[child.anInt246], flag2);
					}
					if (model != null)
						model.method482(child.modelRotation2, 0, child.modelRotation1, 0, i5, l5);
					Rasterizer.centerX = k3;
					Rasterizer.centerY = j4;
				} else if (child.type == 7) {
					TextDrawingArea textDrawingArea_1 = child.textDrawingAreas;
					int k4 = 0;
					for (int j5 = 0; j5 < child.height; j5++) {
						for (int i6 = 0; i6 < child.width; i6++) {
							if (child.container[k4] > 0) {
								ItemDefinition itemDef = ItemDefinition.forID(child.container[k4] - 1);
								String s2 = itemDef.name;
								if (itemDef.stackable || child.containerSizes[k4] != 1)
									s2 = s2 + " x" + intToKOrMilLongName(child.containerSizes[k4]);
								int i9 = drawX + i6 * (115 + child.invSpritePadX);
								int k9 = drawY + j5 * (12 + child.invSpritePadY);
								switch (child.textAlign) {
								case 0:
									textDrawingArea_1.method389(child.textShadow, i9, child.textColor, s2, k9);
									break;

								case 1:
									textDrawingArea_1.drawText(child.textColor, i9 + (child.width / 2), s2, k9, child.textShadow);
									break;

								case 2:
									textDrawingArea_1.method389(child.textShadow, i9 - textDrawingArea_1.getTextWidth(s2), child.textColor, s2, k9);
								}
							}
							k4++;
						}
					}
				} else if (child.type == 8 && (anInt1500 == child.id || anInt1044 == child.id || anInt1129 == child.id) && anInt1501 == 0 && !menuOpen) {
					int boxWidth = 0;
					int boxHeight = 0;
					TextDrawingArea textDrawingArea_2 = chatTextDrawingArea;

					for (String s1 = child.message; s1.length() > 0;) {
						if (s1.indexOf("%") != -1) {
							do {
								int k7 = s1.indexOf("%1");

								if (k7 == -1) {
									break;
								}

								s1 = s1.substring(0, k7) + interfaceIntToString(extractInterfaceValues(child, 0)) + s1.substring(k7 + 2);
							} while (true);

							do {
								int l7 = s1.indexOf("%2");

								if (l7 == -1) {
									break;
								}

								s1 = s1.substring(0, l7) + interfaceIntToString(extractInterfaceValues(child, 1)) + s1.substring(l7 + 2);
							} while (true);

							do {
								int i8 = s1.indexOf("%3");

								if (i8 == -1) {
									break;
								}

								s1 = s1.substring(0, i8) + interfaceIntToString(extractInterfaceValues(child, 2)) + s1.substring(i8 + 2);
							} while (true);

							do {
								int j8 = s1.indexOf("%4");

								if (j8 == -1) {
									break;
								}

								s1 = s1.substring(0, j8) + interfaceIntToString(extractInterfaceValues(child, 3)) + s1.substring(j8 + 2);
							} while (true);

							do {
								int k8 = s1.indexOf("%5");

								if (k8 == -1) {
									break;
								}

								s1 = s1.substring(0, k8) + interfaceIntToString(extractInterfaceValues(child, 4)) + s1.substring(k8 + 2);
							} while (true);
						}

						int l7 = s1.indexOf("\\n");
						String s4;

						if (l7 != -1) {
							s4 = s1.substring(0, l7);
							s1 = s1.substring(l7 + 2);
						} else {
							s4 = s1;
							s1 = "";
						}

						int j10 = textDrawingArea_2.getTextWidth(s4);

						if (j10 > boxWidth) {
							boxWidth = j10;
						}

						boxHeight += textDrawingArea_2.anInt1497 + 1;
					}

					boxWidth += 6;
					boxHeight += 7;
					int xPos = (drawX + child.width) - 5 - boxWidth;
					int yPos = drawY + child.height + 5;

					if (xPos < (drawX + 5)) {
						xPos = drawX + 5;
					}

					if ((xPos + boxWidth) > (xPos + rsInterface.width)) {
						xPos = (xPos + rsInterface.width) - boxWidth;
					}

					if ((yPos + boxHeight) > (yPos + rsInterface.height)) {
						yPos = (drawY - boxHeight);
					}

					DrawingArea.drawPixels(boxHeight, yPos, xPos, 0xFFFFA0, boxWidth);
					DrawingArea.fillPixels(xPos, boxWidth, boxHeight, 0, yPos);
					String s2 = child.message;

					for (int j11 = yPos + textDrawingArea_2.anInt1497 + 2; s2.length() > 0; j11 += textDrawingArea_2.anInt1497 + 1) {
						// anInt1497
						if (s2.indexOf("%") != -1) {
							do {
								int k7 = s2.indexOf("%1");

								if (k7 == -1) {
									break;
								}

								s2 = s2.substring(0, k7) + interfaceIntToString(extractInterfaceValues(child, 0)) + s2.substring(k7 + 2);
							} while (true);

							do {
								int l7 = s2.indexOf("%2");

								if (l7 == -1) {
									break;
								}

								s2 = s2.substring(0, l7) + interfaceIntToString(extractInterfaceValues(child, 1)) + s2.substring(l7 + 2);
							} while (true);

							do {
								int i8 = s2.indexOf("%3");

								if (i8 == -1) {
									break;
								}

								s2 = s2.substring(0, i8) + interfaceIntToString(extractInterfaceValues(child, 2)) + s2.substring(i8 + 2);
							} while (true);

							do {
								int j8 = s2.indexOf("%4");

								if (j8 == -1) {
									break;
								}

								s2 = s2.substring(0, j8) + interfaceIntToString(extractInterfaceValues(child, 3)) + s2.substring(j8 + 2);
							} while (true);

							do {
								int k8 = s2.indexOf("%5");

								if (k8 == -1) {
									break;
								}

								s2 = s2.substring(0, k8) + interfaceIntToString(extractInterfaceValues(child, 4)) + s2.substring(k8 + 2);
							} while (true);
						}

						int l11 = s2.indexOf("\\n");
						String s5;

						if (l11 != -1) {
							s5 = s2.substring(0, l11);
							s2 = s2.substring(l11 + 2);
						} else {
							s5 = s2;
							s2 = "";
						}

						switch (child.textAlign) {
						case 0:
							newRegularFont.drawBasicString(s5, xPos + 3, j11, 0, -1);
							// textDrawingArea_1.method389(false, xPos + 3, 0,
							// s5, j11);
							break;

						case 1:
							textDrawingArea_2.drawText(yPos, xPos + (child.width / 2), s5, j11, false);
							break;

						case 2:
							textDrawingArea_2.method389(false, (xPos + 3) - textDrawingArea_2.getTextWidth(s5), 0, s5, j11);
						}
					}
				} else if (child.type == 9) {
					drawHoverBox(child, drawX, drawY, child.message);
				} else if (child.type == 10) {
					Sprite sprite;

					if (interfaceIsSelected(child)) {
						sprite = child.sprite2;
					} else {
						sprite = child.sprite1;
					}

					if (sprite != null) {
						sprite.drawARGBSprite(drawX, drawY, child.opacity);
					}
				}
		}
		DrawingArea.setDrawingArea(bottomY, topX, bottomX, topY);
	}

	private void randomizeBackground(Background background) {
		int j = 256;
		for (int k = 0; k < anIntArray1190.length; k++)
			anIntArray1190[k] = 0;

		for (int l = 0; l < 5000; l++) {
			int i1 = (int) (Math.random() * 128D * j);
			anIntArray1190[i1] = (int) (Math.random() * 256D);
		}
		for (int j1 = 0; j1 < 20; j1++) {
			for (int k1 = 1; k1 < j - 1; k1++) {
				for (int i2 = 1; i2 < 127; i2++) {
					int k2 = i2 + (k1 << 7);
					anIntArray1191[k2] = (anIntArray1190[k2 - 1] + anIntArray1190[k2 + 1] + anIntArray1190[k2 - 128] + anIntArray1190[k2 + 128]) / 4;
				}

			}
			int ai[] = anIntArray1190;
			anIntArray1190 = anIntArray1191;
			anIntArray1191 = ai;
		}
		if (background != null) {
			int l1 = 0;
			for (int j2 = 0; j2 < background.anInt1453; j2++) {
				for (int l2 = 0; l2 < background.anInt1452; l2++)
					if (background.aByteArray1450[l1++] != 0) {
						int i3 = l2 + 16 + background.anInt1454;
						int j3 = j2 + 16 + background.anInt1455;
						int k3 = i3 + (j3 << 7);
						anIntArray1190[k3] = 0;
					}
			}
		}
	}

	private void method107(int i, int j, Stream stream, Player player) {

		if ((i & 0x400) != 0) {
			player.anInt1543 = stream.readByteS();
			player.anInt1545 = stream.readByteS();
			player.anInt1544 = stream.readByteS();
			player.anInt1546 = stream.readByteS();
			player.anInt1547 = stream.method436() + loopCycle;
			player.anInt1548 = stream.method435() + loopCycle;
			player.anInt1549 = stream.readByteS();
			player.method446();
		}
		if ((i & 0x100) != 0) {
			player.anInt1520 = stream.readLEShort();
			int graphicData = stream.readInt();
			player.anInt1524 = graphicData >> 16;
			player.anInt1523 = loopCycle + (graphicData & 0xffff);
			player.anInt1521 = 0;
			player.anInt1522 = 0;
			if (player.anInt1523 > loopCycle)
				player.anInt1521 = -1;
			if (player.anInt1520 == 65535)
				player.anInt1520 = -1;

		}
		if ((i & 8) != 0) {
			int l = stream.readLEShort();
			if (l == 65535)
				l = -1;
			int i2 = stream.readByteC();
			if (l == player.anim && l != -1) {
				int i3 = Animation.anims[l].anInt365;
				if (i3 == 1) {
					player.anInt1527 = 0;
					player.anInt1528 = 0;
					player.anInt1529 = i2;
					player.anInt1530 = 0;
				}
				if (i3 == 2)
					player.anInt1530 = 0;
			} else if (l == -1 || player.anim == -1 || Animation.anims[l].anInt359 >= Animation.anims[player.anim].anInt359) {
				player.anim = l;
				player.anInt1527 = 0;
				player.anInt1528 = 0;
				player.anInt1529 = i2;
				player.anInt1530 = 0;
				player.anInt1542 = player.smallXYIndex;
			}
		}
		if ((i & 4) != 0) {
			player.textSpoken = stream.readString();
			if (player.textSpoken.charAt(0) == '~') {
				player.textSpoken = player.textSpoken.substring(1);
				pushMessage(player.textSpoken, 2, player.name);
			} else if (player == myPlayer)
				pushMessage(player.textSpoken, 2, player.name);
			player.anInt1513 = 0;
			player.anInt1531 = 0;
			player.textCycle = 150;
		}
		if ((i & 0x80) != 0) {
			// right fucking here
			int i1 = stream.readLEShort();
			int j2 = stream.readUnsignedByte();
			int j3 = stream.readByteC();
			int k3 = stream.currentOffset;
			if (player.name != null && player.visible) {
				long l3 = UsernameEncoding.encode(player.name);
				boolean flag = false;
				if (j2 <= 1) {
					for (int i4 = 0; i4 < ignoreCount; i4++) {
						if (ignoreListAsLongs[i4] != l3)
							continue;
						flag = true;
						break;
					}

				}
				if (!flag && anInt1251 == 0)
					try {
						aStream_834.currentOffset = 0;
						stream.method442(j3, 0, aStream_834.buffer);
						aStream_834.currentOffset = 0;
						String s = TextInput.method525(j3, aStream_834);
						// s = Censor.doCensor(s);
						player.textSpoken = s;
						player.anInt1513 = i1 >> 8;
						player.privelage = j2;
						player.anInt1531 = i1 & 0xff;
						player.textCycle = 150;
						if (j2 > 0)
							pushMessage(s, 1, "@cr" + j2 + "@" + player.name);
						else
							pushMessage(s, 2, player.name);
					} catch (Exception exception) {
						Signlink.reporterror("cde2");
					}
			}
			stream.currentOffset = k3 + j3;
		}
		if ((i & 1) != 0) {
			player.interactingEntity = stream.readLEShort();
			if (player.interactingEntity == 65535)
				player.interactingEntity = -1;
		}
		if ((i & 0x10) != 0) {
			int j1 = stream.readByteC();
			byte abyte0[] = new byte[j1];
			Stream stream_1 = new Stream(abyte0);
			stream.readBytes(j1, 0, abyte0);
			aStreamArray895s[j] = stream_1;
			player.updatePlayer(stream_1);
		}
		if ((i & 2) != 0) {
			player.anInt1538 = stream.method436();
			player.anInt1539 = stream.readLEShort();
		}
		if ((i & 0x20) != 0) {
			int damage = stream.readInt();
			int mask = stream.readByteA();
			player.updateHitData(mask, damage, loopCycle);
			player.loopCycleStatus = loopCycle + 300;
			player.currentHealth = stream.readInt();
			player.maxHealth = stream.readInt();
		}
		if ((i & 0x200) != 0) {
			int l1 = stream.readInt();
			int l2 = stream.readByteS();
			player.updateHitData(l2, l1, loopCycle);
			player.loopCycleStatus = loopCycle + 300;
			player.currentHealth = stream.readInt();
			player.maxHealth = stream.readInt();
		}
	}

	private void method108() {
		try {
			int j = myPlayer.x + anInt1278;
			int k = myPlayer.y + anInt1131;
			if (anInt1014 - j < -500 || anInt1014 - j > 500 || anInt1015 - k < -500 || anInt1015 - k > 500) {
				anInt1014 = j;
				anInt1015 = k;
			}
			if (anInt1014 != j)
				anInt1014 += (j - anInt1014) / 16;
			if (anInt1015 != k)
				anInt1015 += (k - anInt1015) / 16;
			if (super.keyArray[1] == 1)
				anInt1186 += (-24 - anInt1186) / 2;
			else if (super.keyArray[2] == 1)
				anInt1186 += (24 - anInt1186) / 2;
			else
				anInt1186 /= 2;
			if (super.keyArray[3] == 1)
				anInt1187 += (12 - anInt1187) / 2;
			else if (super.keyArray[4] == 1)
				anInt1187 += (-12 - anInt1187) / 2;
			else
				anInt1187 /= 2;
			minimapInt1 = minimapInt1 + anInt1186 / 2 & 0x7ff;
			anInt1184 += anInt1187 / 2;
			if (anInt1184 < 128)
				anInt1184 = 128;
			if (anInt1184 > 383)
				anInt1184 = 383;
			int l = anInt1014 >> 7;
			int i1 = anInt1015 >> 7;
			int j1 = method42(plane, anInt1015, anInt1014);
			int k1 = 0;
			if (l > 3 && i1 > 3 && l < 100 && i1 < 100) {
				for (int l1 = l - 4; l1 <= l + 4; l1++) {
					for (int k2 = i1 - 4; k2 <= i1 + 4; k2++) {
						int l2 = plane;
						if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
							l2++;
						int i3 = j1 - intGroundArray[l2][l1][k2];
						if (i3 > k1)
							k1 = i3;
					}

				}

			}
			int j2 = k1 * 192;
			if (j2 > 0x17f00)
				j2 = 0x17f00;
			if (j2 < 32768)
				j2 = 32768;
			if (j2 > anInt984) {
				anInt984 += (j2 - anInt984) / 24;
				return;
			}
			if (j2 < anInt984) {
				anInt984 += (j2 - anInt984) / 80;
			}
		} catch (Exception _ex) {
			Signlink.reporterror("glfc_ex " + myPlayer.x + "," + myPlayer.y + "," + anInt1014 + "," + anInt1015 + "," + anInt1069 + "," + anInt1070 + "," + baseX + "," + baseY);
			throw new RuntimeException("eek");
		}
	}

	@Override
	public void processDrawing() {
		if (rsAlreadyLoaded || loadingError || genericLoadingError) {
			showErrorScreen();
			return;
		}
		if (!loggedIn)
			drawLoginScreen();
		else
			drawGameScreen();
		anInt1213 = 0;
	}

	private boolean isFriendOrSelf(String s) {
		if (s == null)
			return false;
		for (int i = 0; i < friendsCount; i++)
			if (s.equalsIgnoreCase(friendsList[i]))
				return true;
		return s.equalsIgnoreCase(myPlayer.name);
	}

	private static String combatDiffColor(int i, int j) {
		int k = i - j;
		if (k < -9)
			return "@red@";
		if (k < -6)
			return "@or3@";
		if (k < -3)
			return "@or2@";
		if (k < 0)
			return "@or1@";
		if (k > 9)
			return "@gre@";
		if (k > 6)
			return "@gr3@";
		if (k > 3)
			return "@gr2@";
		if (k > 0)
			return "@gr1@";
		else
			return "@yel@";
	}

	private void draw3dScreen() {
		if (showChatComponents) {
			drawSplitPrivateChat();
		}
		if (crossType == 1) {
			int offSet = frameMode == ScreenMode.FIXED ? 4 : 0;
			crosses[crossIndex / 100].drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
			anInt1142++;
			if (anInt1142 > 67) {
				anInt1142 = 0;
				stream.createFrame(78);
			}
		}
		if (crossType == 2) {
			int offSet = frameMode == ScreenMode.FIXED ? 4 : 0;
			crosses[4 + crossIndex / 100].drawSprite(crossX - 8 - offSet, crossY - 8 - offSet);
		}
		if (anInt1018 != -1) {
			method119(anInt945, anInt1018);
			if (anInt1018 == 23300) {
				RSInterface rsInterface = RSInterface.interfaceCache[anInt1018];
				rsInterface.childX[20] = frameMode.isFixed() ? 2 : 145;
				rsInterface.childY[20] = frameMode.isFixed() ? 2 : -85;
				drawInterface(0, frameWidth - rsInterface.width - 275, rsInterface, 0);
			} else if (anInt1018 == 197 && frameMode != ScreenMode.FIXED) {
				skullIcons[0].drawSprite(frameWidth - 157, 168);
				String text = RSInterface.interfaceCache[199].message.replace("@yel@", "");
				aTextDrawingArea_1271.drawChatInput(0xE1981D, frameWidth - 165, text, 207, true);
			} else if (anInt1018 == 201 && frameMode != ScreenMode.FIXED) {
				drawInterface(0, frameWidth - 560, RSInterface.interfaceCache[anInt1018], -109);
			} else {
				drawInterface(0, frameMode == ScreenMode.FIXED ? 0 : (frameWidth / 2) - 356, RSInterface.interfaceCache[anInt1018], frameMode == ScreenMode.FIXED ? 0 : (frameHeight / 2) - 230);
			}
		}
		if (openInterfaceID != -1) {
			method119(anInt945, openInterfaceID);
			drawInterface(0, frameMode == ScreenMode.FIXED ? 0 : (frameWidth / 2) - 356, RSInterface.interfaceCache[openInterfaceID], frameMode == ScreenMode.FIXED ? 0 : (frameHeight / 2) - 230);
		}
        drawMapHazard();
        battleEffects.draw((wx,wy,h)->{calcEntityScreenPos(wx-(baseX<<7),h,wy-(baseY<<7));return new int[]{spriteDrawX,spriteDrawY};},System.currentTimeMillis());
        if(!prototypeOpen&&!arpgSkillsOpen&&controlsPanel==null&&openInterfaceID==-1&&!menuOpen&&myPlayer!=null){int offset=frameMode==ScreenMode.FIXED?4:0;java.util.List<java.awt.Rectangle> reserved=new java.util.ArrayList<>();if(mapObjective.isVisible())reserved.add(mapObjective.bounds(com.koios.client.arpg.ExileTheme.body()));reserved.add(new java.awt.Rectangle(com.koios.client.arpg.SkillPanel.barOffset(DrawingArea.width)+12,com.koios.client.arpg.SkillPanel.barTop(DrawingArea.width,DrawingArea.height),392,78));if(frameMode!=ScreenMode.FIXED){reserved.add(new java.awt.Rectangle(frameWidth-250,0,250,184));reserved.add(new java.awt.Rectangle(0,frameHeight-175-extendChatArea,519,175+extendChatArea));reserved.add(new java.awt.Rectangle(frameWidth-247,frameHeight-336,247,336));}groundLoot.setExclusions(reserved);groundLoot.draw(com.koios.client.arpg.ExileTheme.body(),(wx,wy,h)->{calcEntityScreenPos(wx-(baseX<<7),h,wy-(baseY<<7));return new int[]{spriteDrawX,spriteDrawY};},plane,baseX+(myPlayer.x>>7),baseY+(myPlayer.y>>7),arpgAltDown,mouseX-offset,mouseY-offset);}else groundLoot.clearHits();
		if (!menuOpen) {
			processRightClick();
			drawTooltip();
		} else if (menuScreenArea == 0) {
			drawMenu(frameMode == ScreenMode.FIXED ? 4 : 0, frameMode == ScreenMode.FIXED ? 4 : 0);
		}
		if (anInt1055 == 1 && !mapObjective.isVisible()) {
			multiOverlay.drawSprite(frameMode == ScreenMode.FIXED ? 472 : frameWidth - 85, frameMode == ScreenMode.FIXED ? 296 : 186);
		}
		if (fpsOn) {
			int textColour = 0xffff00;
			if (super.fps < 15) {
				textColour = 0xff0000;
			}
			aTextDrawingArea_1271.method385(textColour, "Fps: " + super.fps, 15, frameMode == ScreenMode.FIXED ? 470 : frameWidth - 265);
			Runtime runtime = Runtime.getRuntime();
			int memUsage = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			textColour = 0xffff00;
			if (memUsage > 0x2000000 && lowMem) {
				textColour = 0xff0000;
			}
			aTextDrawingArea_1271.method385(textColour, "Mem: " + memUsage + "k", 30, frameMode == ScreenMode.FIXED ? 428 : frameWidth - 265);
		}
		int x = baseX + (myPlayer.x - 6 >> 7);
		int y = baseY + (myPlayer.y - 6 >> 7);
		final String screenMode = frameMode == ScreenMode.FIXED ? "Fixed" : "Resizable";
		if (clientData) {
			int textColour = 0xffff00;
			int fpsColour = 0xffff00;
			if (super.fps < 15) {
				fpsColour = 0xff0000;
			}
			aTextDrawingArea_1271.method385(textColour, "Floor Maps: " + floorMaps, frameHeight - 301, 5);
			aTextDrawingArea_1271.method385(textColour, "Object Maps: " + objectMaps, frameHeight - 286, 5);
			aTextDrawingArea_1271.method385(textColour, "clientWidth: " + (mouseX - frameWidth) + ", clientHeight: " + (mouseY - frameHeight), frameHeight - 271, 5);
			aTextDrawingArea_1271.method385(textColour, "Client Zoom: " + cameraZoom, frameHeight - 257, 5);
			aTextDrawingArea_1271.method385(fpsColour, "Fps: " + super.fps, frameHeight - 243, 5);
			Runtime runtime = Runtime.getRuntime();
			int clientMemory = (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
			aTextDrawingArea_1271.method385(textColour, "Memory Usage: " + clientMemory + "k", frameHeight - 229, 5);
			aTextDrawingArea_1271.method385(textColour, "Mouse X: " + mouseX + ", Mouse Y: " + mouseY, frameHeight - 215, 5);
			aTextDrawingArea_1271.method385(textColour, "Coords: " + x + ", " + y, frameHeight - 201, 5);
			aTextDrawingArea_1271.method385(textColour, "Client Mode: " + screenMode + "", frameHeight - 187, 5);
			aTextDrawingArea_1271.method385(textColour, "Client Resolution: " + frameWidth + "x" + frameHeight, frameHeight - 173, 5);
		}
		if (anInt1104 != 0) {
			int j = anInt1104 / 50;
			int l = j / 60;
			int yOffset = frameMode == ScreenMode.FIXED ? 0 : frameHeight - 498;
			j %= 60;
			if (j < 10)
				aTextDrawingArea_1271.method385(0xffff00, "System update in: " + l + ":0" + j, 329 + yOffset, 4);
			else
				aTextDrawingArea_1271.method385(0xffff00, "System update in: " + l + ":" + j, 329 + yOffset, 4);
			anInt849++;
			if (anInt849 > 75) {
				anInt849 = 0;
				stream.createFrame(148);
			}
		}
	}

	private void updateTabPreviews() {
		int usedTabs = getUsedTabs();
		for (int i = 51; i <= 59; i++) {
			if ((i - 51) > 0) {
				int preview = RSInterface.interfaceCache[RSInterface.BANK_CONTAINER + (i - 51)].container[0];
				int amount = RSInterface.interfaceCache[RSInterface.BANK_CONTAINER + (i - 51)].containerSizes[0];
				RSInterface.interfaceCache[50000 + i].container[0] = preview;
				RSInterface.interfaceCache[50000 + i].containerSizes[0] = amount;
				RSInterface.interfaceCache[50035 + ((i - 52) * 2)].isMouseoverTriggered = ((i - 52) != 0) && (preview <= 0) && ((i - 52) != (usedTabs - 1));
			}
			variousSettings[(1000 + i) - 51] = (bankTab == (i - 51)) && (variousSettings[1010] != 1) ? 1 : 0;
		}

		if ((RSInterface.interfaceCache[RSInterface.BANK_CONTAINER + bankTab].container[0] <= 0) && (RSInterface.interfaceCache[RSInterface.BANK_CONTAINER + bankTab].container[1] <= 0)) {
			bankTab = 0;
		}

		RSInterface.interfaceCache[50062].isMouseoverTriggered = usedTabs > 8;
		RSInterface.interfaceCache[50062].xOffset = (usedTabs == 0) ? 0 : 48 * (usedTabs - 1);
	}

	private void updateBankScrollPosition() {
		int height = 0;
		int tabSize = getTabSize(bankTab);

		if ((bankTab > 0) || (variousSettings[1010] == 1)) {
			height = (((tabSize / 10) + ((tabSize % 10) > 0 ? 1 : 0)) * 44) + 22;
		} else {
			height = getTabsHeight(getUsedTabs());
		}

		int bankSize = 0;

		for (int index = 0; index < 9; index++) {
			RSInterface tab = RSInterface.interfaceCache[50088 + index];

			if ((tab == null) || (tab.container == null)) {
				continue;
			}

			for (int containerItem : tab.container) {
				if (containerItem <= 0) {
					break;
				}

				bankSize++;
			}
		}

		RSInterface.interfaceCache[50017].setText(Integer.toString(tabSize));
		RSInterface.interfaceCache[50019].setText(Integer.toString(bankSize));
		RSInterface.interfaceCache[5385].scrollMax = height < 209 ? 209 : height;
	}

	private int bankTab = 0;

	private void bankUpdate() {
		updateTabPreviews();

		for (int child = 0; child <= 32; child++) {
			boolean isBankSearching = variousSettings[1010] == 1;
			boolean bankContainer = (child <= 32) && (child >= 24);
			boolean tabDivider = (child <= 23) && (child >= 16);
			boolean tabHighlight = (child <= 15) && (child >= 8);
			boolean tabText = (child <= 7) && (child >= 0);
			int tab = bankContainer ? (child - 24) : tabDivider ? (child - 16) : tabHighlight ? (child - 8) : child;
			boolean mainTabSprite = (bankTab > 0) && (((tab != bankTab) && bankContainer) || tabDivider || tabText);
			boolean emptyTab = getTabSize(tab + (bankContainer ? 0 : 1)) == 0;
			int x = bankContainer ? 38 : tabHighlight ? 36 : 30;

			if (mainTabSprite || emptyTab || isBankSearching) {
				RSInterface.interfaceCache[5385].childX[child] = -1000;
			} else {
				RSInterface.interfaceCache[5385].childX[child] = x;
			}

			if (isBankSearching) {
				RSInterface.interfaceCache[5385].childX[24] = x;
			}

			int y = 0;

			if (bankTab == 0) {
				y = getTabsHeight(tab + (bankContainer ? 0 : 1));

				if (bankContainer) {
					y += 3;
				}

				if (tabDivider) {
					y -= 3;
				}

				if (tabText) {
					y -= 14;
				}
			} else {
				y = 7 - (tabHighlight ? 3 : 0);
			}

			RSInterface.interfaceCache[5385].childY[child] = y;
		}

		updateBankScrollPosition();
	}

	private int getTabsHeight(int tab) {
		int height = 0;

		for (int index = 0; index < tab; index++) {
			int tabSize = getTabSize(index);
			height += tabSize == 0 ? 0 : ((((tabSize / 10) + ((tabSize % 10) > 0 ? 1 : 0)) * 44) + 32);
		}

		return height;
	}

	private int getTabSize(int index) {
		RSInterface tab = RSInterface.interfaceCache[50088 + index];

		if ((tab == null) || (tab.container == null)) {
			return 0;
		}

		int itemCount = 0;

		for (int containerItem : tab.container) {
			if (containerItem > 0) {
				itemCount++;
			}
		}

		return itemCount;
	}

	private int getUsedTabs() {
		int height = 0;

		for (int index = 0; index < 9; index++) {
			if ((getTabSize(index) > 0) || ((index == 0) && (variousSettings[1010] == 1))) {
				height++;
			}
		}

		return height;
	}

	private void addIgnore(long l) {
		try {
			if (l == 0L)
				return;
			if (ignoreCount >= 100) {
				pushMessage("Your ignore list is full. Max of 100 hit", 0, "");
				return;
			}
			String s = UsernameEncoding.formalize(UsernameEncoding.decode(l));
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					pushMessage(s + " is already on your ignore list", 0, "");
					return;
				}
			for (int k = 0; k < friendsCount; k++)
				if (friendsListAsLongs[k] == l) {
					pushMessage("Please remove " + s + " from your friend list first", 0, "");
					return;
				}

			ignoreListAsLongs[ignoreCount++] = l;
			stream.createFrame(133);
			stream.writeLong(l);
			return;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("45688, " + l + ", " + 4 + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void method114() {
		for (int i = -1; i < playerCount; i++) {
			int j;
			if (i == -1)
				j = myPlayerIndex;
			else
				j = playerIndices[i];
			Player player = playerArray[j];
			if (player != null)
				method96(player);
		}

	}

	private void method115() {
		if (loadingStage == 2) {
			for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetNext()) {
				if (class30_sub1.anInt1294 > 0)
					class30_sub1.anInt1294--;
				if (class30_sub1.anInt1294 == 0) {
					if (class30_sub1.anInt1299 < 0 || ObjectManager.method178(class30_sub1.anInt1299, class30_sub1.anInt1301)) {
						method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1300, class30_sub1.anInt1301, class30_sub1.anInt1297, class30_sub1.anInt1296,
								class30_sub1.anInt1299);
						class30_sub1.unlink();
					}
				} else {
					if (class30_sub1.anInt1302 > 0)
						class30_sub1.anInt1302--;
					if (class30_sub1.anInt1302 == 0 && class30_sub1.anInt1297 >= 1 && class30_sub1.anInt1298 >= 1 && class30_sub1.anInt1297 <= 102 && class30_sub1.anInt1298 <= 102
							&& (class30_sub1.anInt1291 < 0 || ObjectManager.method178(class30_sub1.anInt1291, class30_sub1.anInt1293))) {
						method142(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1292, class30_sub1.anInt1293, class30_sub1.anInt1297, class30_sub1.anInt1296,
								class30_sub1.anInt1291);
						class30_sub1.anInt1302 = -1;
						if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1299 == -1)
							class30_sub1.unlink();
						else if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1292 == class30_sub1.anInt1300 && class30_sub1.anInt1293 == class30_sub1.anInt1301)
							class30_sub1.unlink();
					}
				}
			}

		}
	}

	private void determineMenuSize() {
		int boxLength = com.koios.client.arpg.ExileTheme.body().getTextWidth("Choose option")+4;
		for (int row = 0; row < menuActionRow; row++) {
			int actionLength = com.koios.client.arpg.ExileTheme.body().getTextWidth(menuActionName[row])+4;
			if (actionLength > boxLength)
				boxLength = actionLength;
		}
		boxLength += 8;
		int offset = 15 * menuActionRow + 21;
		if (super.saveClickX > 0 && super.saveClickY > 0 && super.saveClickX < frameWidth && super.saveClickY < frameHeight) {
			int xClick = super.saveClickX - boxLength / 2;
			if (xClick + boxLength > frameWidth - 4) {
				xClick = frameWidth - 4 - boxLength;
			}
			if (xClick < 0) {
				xClick = 0;
			}
			int yClick = super.saveClickY - 0;
			if (yClick + offset > frameHeight - 6) {
				yClick = frameHeight - 6 - offset;
			}
			if (yClick < 0) {
				yClick = 0;
			}
			menuOpen = true;
			menuOffsetX = xClick;
			menuOffsetY = yClick;
			menuWidth = boxLength;
			menuHeight = 15 * menuActionRow + 22;
		}
	}

	private void method117(Stream stream) {
		stream.initBitAccess();
		int j = stream.readBits(1);
		if (j == 0)
			return;
		int k = stream.readBits(2);
		if (k == 0) {
			anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 1) {
			int l = stream.readBits(3);
			myPlayer.moveInDir(false, l);
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 2) {
			int i1 = stream.readBits(3);
			myPlayer.moveInDir(true, i1);
			int l1 = stream.readBits(3);
			myPlayer.moveInDir(true, l1);
			int j2 = stream.readBits(1);
			if (j2 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			return;
		}
		if (k == 3) {
			plane = myPlayer.realPlane = stream.readBits(2);
			int j1 = stream.readBits(1);
			int i2 = stream.readBits(1);
			if (i2 == 1)
				anIntArray894[anInt893++] = myPlayerIndex;
			int k2 = stream.readBits(7);
			int l2 = stream.readBits(7);
			myPlayer.setPos(l2, k2, j1 == 1);
		}
	}

	private void nullLoader() {
		aBoolean831 = false;
		while (drawingFlames) {
			aBoolean831 = false;
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
		}
		aBackgroundArray1152s = null;
		anIntArray851 = null;
		anIntArray852 = null;
		anIntArray853 = null;
		anIntArray1190 = null;
		anIntArray1191 = null;
	}

	private boolean method119(int i, int j) {
		boolean flag1 = false;
		RSInterface class9 = RSInterface.interfaceCache[j];
		for (int k = 0; k < class9.children.length; k++) {
			if (class9.children[k] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[k]];
			if (class9_1.type == 1)
				flag1 |= method119(i, class9_1.id);
			if (class9_1.type == 6 && (class9_1.anInt257 != -1 || class9_1.anInt258 != -1)) {
				boolean flag2 = interfaceIsSelected(class9_1);
				int l;
				if (flag2)
					l = class9_1.anInt258;
				else
					l = class9_1.anInt257;
				if (l != -1) {
					Animation animation = Animation.anims[l];
					for (class9_1.anInt208 += i; class9_1.anInt208 > animation.method258(class9_1.anInt246);) {
						class9_1.anInt208 -= animation.method258(class9_1.anInt246) + 1;
						class9_1.anInt246++;
						if (class9_1.anInt246 >= animation.anInt352) {
							class9_1.anInt246 -= animation.anInt356;
							if (class9_1.anInt246 < 0 || class9_1.anInt246 >= animation.anInt352)
								class9_1.anInt246 = 0;
						}
						flag1 = true;
					}

				}
			}
		}

		return flag1;
	}

	private int method120() {
		if (roofsOff) {
			return plane;
		}

		int j = 3;
		if (yCameraCurve < 310) {
			int k = xCameraPos >> 7;
			int l = yCameraPos >> 7;
			int i1 = myPlayer.x >> 7;
			int j1 = myPlayer.y >> 7;
			if ((byteGroundArray[plane][k][l] & 4) != 0)
				j = plane;
			int k1;
			if (i1 > k)
				k1 = i1 - k;
			else
				k1 = k - i1;
			int l1;
			if (j1 > l)
				l1 = j1 - l;
			else
				l1 = l - j1;
			if (k1 > l1) {
				int i2 = (l1 * 0x10000) / k1;
				int k2 = 32768;
				while (k != i1) {
					if (k < i1)
						k++;
					else if (k > i1)
						k--;
					if ((byteGroundArray[plane][k][l] & 4) != 0)
						j = plane;
					k2 += i2;
					if (k2 >= 0x10000) {
						k2 -= 0x10000;
						if (l < j1)
							l++;
						else if (l > j1)
							l--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
					}
				}
			} else {
				int j2 = (k1 * 0x10000) / l1;
				int l2 = 32768;
				while (l != j1) {
					if (l < j1)
						l++;
					else if (l > j1)
						l--;
					if ((byteGroundArray[plane][k][l] & 4) != 0)
						j = plane;
					l2 += j2;
					if (l2 >= 0x10000) {
						l2 -= 0x10000;
						if (k < i1)
							k++;
						else if (k > i1)
							k--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
					}
				}
			}
		}
		if ((byteGroundArray[plane][myPlayer.x >> 7][myPlayer.y >> 7] & 4) != 0)
			j = plane;
		return j;
	}

	private int method121() {
		if (roofsOff) {
			return plane;
		}
		int j = method42(plane, yCameraPos, xCameraPos);
		if (j - zCameraPos < 800 && (byteGroundArray[plane][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
			return plane;
		else
			return 3;
	}

	private void delIgnore(long l) {
		try {
			if (l == 0L)
				return;
			for (int j = 0; j < ignoreCount; j++)
				if (ignoreListAsLongs[j] == l) {
					ignoreCount--;
					System.arraycopy(ignoreListAsLongs, j + 1, ignoreListAsLongs, j, ignoreCount - j);

					stream.createFrame(74);
					stream.writeLong(l);
					return;
				}

			return;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	private void chatJoin(long l) {
		try {
			if (l == 0L)
				return;
			stream.createFrame(60);
			stream.writeLong(l);
			return;
		} catch (RuntimeException runtimeexception) {
			Signlink.reporterror("47229, " + 3 + ", " + l + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();

	}

	@Override
	public String getParameter(String s) {
		if (Signlink.mainapp != null)
			return Signlink.mainapp.getParameter(s);
		else
			return super.getParameter(s);
	}

	private int extractInterfaceValues(RSInterface component, int instructionIndex) {
		if (component.instructions == null || instructionIndex >= component.instructions.length)
			return -2;
		try {
			int instructions[] = component.instructions[instructionIndex];

			int totalValue = 0;
			int caret = 0;
			int operationMode = 0;
			do {
				int opcode = instructions[caret++];
				int value = 0;
				byte nextOperationMode = 0;

				if (opcode == -1) {
					for (int i = 0; i < SkillConstants.SKILL_COUNT; i++) {
						if (SkillConstants.SKILL_ENABLED[i]) {
							value += statsExperience[i];
						}
					}
				}

				if (opcode == 0)
					return totalValue;

				if (opcode == 1) {
					value = statsCurrentLevel[instructions[caret++]];
				}

				if (opcode == 2) {
					value = statsMaximumLevel[instructions[caret++]];
				}

				if (opcode == 3) {
					value = statsExperience[instructions[caret++]];
				}

				if (opcode == 4) {
					RSInterface class9_1 = RSInterface.interfaceCache[instructions[caret++]];
					int targetItemId = instructions[caret++];
					if (targetItemId >= 0 && targetItemId < ItemDefinition.totalItems && (!ItemDefinition.forID(targetItemId).membersObject || isMembers)) {
						for (int j3 = 0; j3 < class9_1.container.length; j3++)
							if (class9_1.container[j3] == targetItemId + 1)
								value += class9_1.containerSizes[j3];

					}
				}

				if (opcode == 5) {
					value = variousSettings[instructions[caret++]];
				}

				if (opcode == 6) {
					value = EXPERIENCE_BY_LEVEL[statsMaximumLevel[instructions[caret++]] - 1];
				}

				if (opcode == 7) {
					value = (variousSettings[instructions[caret++]] * 100) / 46875;
				}

				if (opcode == 8) {
					value = myPlayer.combatLevel;
				}

				if (opcode == 9) {
					for (int i = 0; i < SkillConstants.SKILL_COUNT; i++) {
						if (SkillConstants.SKILL_ENABLED[i]) {
							value += statsMaximumLevel[i];
						}
					}
				}
				if (opcode == 10) {
					RSInterface class9_2 = RSInterface.interfaceCache[instructions[caret++]];
					int l2 = instructions[caret++] + 1;
					if (l2 >= 0 && l2 < ItemDefinition.totalItems && isMembers) {
						for (int k3 = 0; k3 < class9_2.container.length; k3++) {
							if (class9_2.container[k3] != l2)
								continue;
							value = 0x3b9ac9ff;
							break;
						}

					}
				}
				if (opcode == 11)
					value = energy;
				if (opcode == 12)
					value = weight;
				if (opcode == 13) {
					int i2 = variousSettings[instructions[caret++]];
					int i3 = instructions[caret++];
					value = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if (opcode == 14) {
					int j2 = instructions[caret++];
					VarBit varBit = VarBit.cache[j2];
					int l3 = varBit.anInt648;
					int i4 = varBit.anInt649;
					int j4 = varBit.anInt650;
					int k4 = anIntArray1232[j4 - i4];
					value = variousSettings[l3] >> i4 & k4;
				}
				if (opcode == 15)
					nextOperationMode = 1;
				if (opcode == 16)
					nextOperationMode = 2;
				if (opcode == 17)
					nextOperationMode = 3;
				if (opcode == 18)
					value = (myPlayer.x >> 7) + baseX;
				if (opcode == 19)
					value = (myPlayer.y >> 7) + baseY;
				if (opcode == 20)
					value = instructions[caret++];

				if (opcode == 21) {
					value = statsSkillGoal[instructions[caret++]][0];
				}

				if (opcode == 22) {
					int skill = instructions[caret++];
					value = statsSkillGoal[skill][1] == 1 ? statsSkillGoal[skill][0] : EXPERIENCE_BY_LEVEL[statsSkillGoal[skill][0] - 2];
				}

				if (nextOperationMode == 0) {
					if (operationMode == 0) {
						totalValue += value;
					}

					if (operationMode == 1) {
						totalValue -= value;
					}

					if ((operationMode == 2) && (value != 0)) {
						totalValue /= value;
					}

					if (operationMode == 3) {
						totalValue *= value;
					}

					operationMode = 0;
				} else {
					operationMode = nextOperationMode;
				}
			} while (true);
		} catch (Exception _ex) {
			return -1;
		}
	}

	private void sendPrototype(String action){if(!loggedIn||openInterfaceID!=-1)return;String command=action.startsWith("hub ")?action:"arpgproto "+action;stream.createFrame(103);stream.writeLEShort(command.length()+1);stream.writeString(command);}
    private boolean overSidebar(int x,int y){return com.koios.client.arpg.SidebarChrome.contains(x,y,frameWidth,frameHeight,frameMode==ScreenMode.FIXED,changeTabArea);}
    private int overlayMouseX(int x){return x-(frameMode==ScreenMode.FIXED?4:com.koios.client.arpg.OverlayViewport.originX(frameWidth,frameHeight,false));}
    private int overlayMouseY(int y){return y-(frameMode==ScreenMode.FIXED?4:com.koios.client.arpg.OverlayViewport.originY(frameWidth,frameHeight,false));}
    private void drawArpgOverlay(Runnable painter){com.koios.client.arpg.OverlayViewport.draw(frameWidth,frameHeight,frameMode==ScreenMode.FIXED,painter);}
    private void sendArpgSkill(String action){
        if(!loggedIn || openInterfaceID!=-1 || messagePromptRaised || inputDialogState!=0 || backDialogID!=-1)return;
        String command="arpgskill "+action;stream.createFrame(103);stream.writeLEShort(command.length()+1);stream.writeString(command);
    }
    private void drawArpgSkills(){
        drawArpgOverlay(()->com.koios.client.arpg.SkillPanel.draw(com.koios.client.arpg.ExileTheme.body(),skillDrag,arpgSkillState,overlayMouseX(mouseX),overlayMouseY(mouseY)));
    }
    private boolean drawArpgTooltip(){
        if(menuOpen || activeInterfaceType!=0 || arpgHoverWidget<0 || arpgHoverSlot<0)return false;
        RSInterface widget=RSInterface.interfaceCache[arpgHoverWidget];
        if(widget==null || widget.container==null || arpgHoverSlot>=widget.container.length)return false;
        com.koios.client.arpg.ItemTooltipCache.Entry entry=arpgTooltips.get(arpgHoverWidget,arpgHoverSlot,widget.container[arpgHoverSlot]-1);
        if(entry==null)return false;
        com.koios.client.arpg.ItemTooltipRenderer.draw(com.koios.client.arpg.ExileTheme.body(),entry,arpgControlDown,mouseX,mouseY);
        return true;
    }

    private void drawTooltip() {
        if(prototypeOpen){drawArpgOverlay(()->prototypePanel.draw(com.koios.client.arpg.ExileTheme.body(),overlayMouseX(mouseX),overlayMouseY(mouseY)));if(overSidebar(mouseX,mouseY))drawArpgTooltip();else{int slot=prototypePanel.hoverInventorySlot(overlayMouseX(mouseX),overlayMouseY(mouseY));RSInterface bag=RSInterface.interfaceCache[3214];if(slot>=0&&bag!=null&&slot<bag.container.length){com.koios.client.arpg.ItemTooltipCache.Entry entry=arpgTooltips.get(3214,slot,bag.container[slot]-1);if(entry!=null)com.koios.client.arpg.ItemTooltipRenderer.draw(com.koios.client.arpg.ExileTheme.body(),entry,arpgControlDown,mouseX,mouseY);}}return;}
        if(controlsPanel!=null){drawArpgOverlay(()->controlsPanel.draw(com.koios.client.arpg.ExileTheme.body()));return;}
        if(arpgSkillsOpen){drawArpgSkills();if(overSidebar(mouseX,mouseY))drawArpgTooltip();return;}
        if(openInterfaceID==-1){com.koios.client.arpg.SkillPanel.drawBar(com.koios.client.arpg.ExileTheme.body(),arpgSkillState,arpgCharacterLines);mapObjective.draw(com.koios.client.arpg.ExileTheme.body());}
        if(drawArpgTooltip())return;
		if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0)
			return;
		String s;
		if (itemSelected == 1 && menuActionRow < 2)
			s = "Use " + selectedItemName + " with...";
		else if (spellSelected == 1 && menuActionRow < 2)
			s = spellTooltip + "...";
		else
			s = menuActionName[menuActionRow - 1];
		if (menuActionRow > 2)
			s = s + "@whi@ / " + (menuActionRow - 2) + " more options";
		com.koios.client.arpg.ExileTheme.label(com.koios.client.arpg.ExileTheme.body(),4,15,s,com.koios.client.arpg.ExileTheme.TEXT);
	}

	private void markMinimap(Sprite sprite, int x, int y) {
		if (sprite == null)
			return;
		try {
			int k = minimapInt1 + minimapInt2 & 0x7ff;
			int l = x * x + y * y;
			if (l > 6400) {
				return;
			}
			int i1 = Model.modelIntArray1[k];
			int j1 = Model.modelIntArray2[k];
			i1 = (i1 * 256) / (minimapInt3 + 256);
			j1 = (j1 * 256) / (minimapInt3 + 256);
			int k1 = y * i1 + x * j1 >> 16;
			int l1 = y * j1 - x * i1 >> 16;
			if (frameMode == ScreenMode.FIXED) {
				sprite.drawSprite(((94 + k1) - sprite.maxWidth / 2) + 4 + 30, 83 - l1 - sprite.maxHeight / 2 - 4 + 5);
			} else {
				sprite.drawSprite(((77 + k1) - sprite.maxWidth / 2) + 4 + (frameWidth - 167), 85 - l1 - sprite.maxHeight / 2 - 4);
			}
		} catch (Exception e) {

		}
	}

	private void drawMinimap() {
		if (frameMode == ScreenMode.FIXED) {
			aRSImageProducer_1164.initDrawingArea();
		}
		if (anInt1021 == 2) {
			if (frameMode == ScreenMode.FIXED) {
				cacheSprite[19].drawSprite(0, 0);
			} else {
				cacheSprite[44].drawSprite(frameWidth - 181, 0);
				cacheSprite[45].drawSprite(frameWidth - 158, 7);
			}
			if (frameMode != ScreenMode.FIXED && changeTabArea) {
				if (super.mouseX >= frameWidth - 26 && super.mouseX <= frameWidth - 1 && super.mouseY >= 2 && super.mouseY <= 24 || tabID == 15) {
					cacheSprite[27].drawSprite(frameWidth - 25, 2);
				} else {
					cacheSprite[27].drawARGBSprite(frameWidth - 25, 2, 165);
				}
			}
			loadAllOrbs(frameMode == ScreenMode.FIXED ? 0 : frameWidth - 217);
			compass.method352(33, minimapInt1, anIntArray1057, 256, anIntArray968, (frameMode == ScreenMode.FIXED ? 25 : 24), 4, (frameMode == ScreenMode.FIXED ? 29 : frameWidth - 176), 33, 25);
			if (menuOpen) {
				drawMenu(frameMode == ScreenMode.FIXED ? 516 : 0, 0);
			}
			if (frameMode == ScreenMode.FIXED) {
				aRSImageProducer_1164.initDrawingArea();
			}
			return;
		}
		
		int i = minimapInt1 + minimapInt2 & 0x7ff;
		int j = 48 + myPlayer.x / 32;
		int l2 = 464 - myPlayer.y / 32;
		minimapImage.method352(151, i, anIntArray1229, 256 + minimapInt3, anIntArray1052, l2, (frameMode == ScreenMode.FIXED ? 9 : 7), (frameMode == ScreenMode.FIXED ? 54 : frameWidth - 158), 146, j);
		for (int j5 = 0; j5 < anInt1071; j5++) {
			int k = (anIntArray1072[j5] * 4 + 2) - myPlayer.x / 32;
			int i3 = (anIntArray1073[j5] * 4 + 2) - myPlayer.y / 32;
			markMinimap(aClass30_Sub2_Sub1_Sub1Array1140[j5], k, i3);
		}
		for (int k5 = 0; k5 < 104; k5++) {
			for (int l5 = 0; l5 < 104; l5++) {
				NodeList class19 = groundArray[plane][k5][l5];
				if (class19 != null) {
					int l = (k5 * 4 + 2) - myPlayer.x / 32;
					int j3 = (l5 * 4 + 2) - myPlayer.y / 32;
					markMinimap(mapDotItem, l, j3);
				}
			}
		}
		for (int i6 = 0; i6 < npcCount; i6++) {
			NPC npc = npcArray[npcIndices[i6]];
			if (npc != null && npc.isVisible()) {
				NPCDefinition entityDef = npc.desc;
				if (entityDef.childrenIDs != null) {
					entityDef = entityDef.method161();
				}
				if (entityDef != null && entityDef.aBoolean87 && entityDef.aBoolean84) {
					int i1 = npc.x / 32 - myPlayer.x / 32;
					int k3 = npc.y / 32 - myPlayer.y / 32;
					markMinimap(mapDotNPC, i1, k3);
				}
			}
		}
		for (int j6 = 0; j6 < playerCount; j6++) {
			Player player = playerArray[playerIndices[j6]];
			if (player != null && player.isVisible()) {
				int j1 = player.x / 32 - myPlayer.x / 32;
				int l3 = player.y / 32 - myPlayer.y / 32;
				boolean flag1 = false;
				boolean flag3 = false;
				
				if (lowMem && player.realPlane != plane) {
					continue;
				}
				
				for (int j3 = 0; j3 < clanList.length; j3++) {
					if (clanList[j3] == null) {
						continue;
					}
					if (!clanList[j3].equalsIgnoreCase(player.name)) {
						continue;
					}
					flag3 = true;
					break;
				}
				long l6 = UsernameEncoding.encode(player.name);
				for (int k6 = 0; k6 < friendsCount; k6++) {
					if (l6 != friendsListAsLongs[k6] || friendsNodeIDs[k6] == 0) {
						continue;
					}
					flag1 = true;
					break;
				}
				boolean flag2 = false;
				if (myPlayer.team != 0 && player.team != 0 && myPlayer.team == player.team) {
					flag2 = true;
				}
				if (flag1) {
					markMinimap(mapDotFriend, j1, l3);
				} else if (flag3) {
					markMinimap(mapDotClan, j1, l3);
				} else if (flag2) {
					markMinimap(mapDotTeam, j1, l3);
				} else {
					markMinimap(mapDotPlayer, j1, l3);
				}
			}
		}
		if (anInt855 != 0 && loopCycle % 20 < 10) {
			if (anInt855 == 1 && anInt1222 >= 0 && anInt1222 < npcArray.length) {
				NPC class30_sub2_sub4_sub1_sub1_1 = npcArray[anInt1222];
				if (class30_sub2_sub4_sub1_sub1_1 != null) {
					int k1 = class30_sub2_sub4_sub1_sub1_1.x / 32 - myPlayer.x / 32;
					int i4 = class30_sub2_sub4_sub1_sub1_1.y / 32 - myPlayer.y / 32;
					method81(mapMarker, i4, k1);
				}
			}
			if (anInt855 == 2) {
				int l1 = ((anInt934 - baseX) * 4 + 2) - myPlayer.x / 32;
				int j4 = ((anInt935 - baseY) * 4 + 2) - myPlayer.y / 32;
				method81(mapMarker, j4, l1);
			}
			if (anInt855 == 10 && anInt933 >= 0 && anInt933 < playerArray.length) {
				Player class30_sub2_sub4_sub1_sub2_1 = playerArray[anInt933];
				if (class30_sub2_sub4_sub1_sub2_1 != null) {
					int i2 = class30_sub2_sub4_sub1_sub2_1.x / 32 - myPlayer.x / 32;
					int k4 = class30_sub2_sub4_sub1_sub2_1.y / 32 - myPlayer.y / 32;
					method81(mapMarker, k4, i2);
				}
			}
		}
		if (destX != 0) {
			int j2 = (destX * 4 + 2) - myPlayer.x / 32;
			int l4 = (destY * 4 + 2) - myPlayer.y / 32;
			markMinimap(mapFlag, j2, l4);
		}
		DrawingArea.drawPixels(3, (frameMode == ScreenMode.FIXED ? 83 : 80), (frameMode == ScreenMode.FIXED ? 127 : frameWidth - 88), 0xffffff, 3);
		if (frameMode == ScreenMode.FIXED) {
			cacheSprite[19].drawSprite(0, 0);
		} else {
			cacheSprite[44].drawSprite(frameWidth - 181, 0);
		}
		compass.method352(33, minimapInt1, anIntArray1057, 256, anIntArray968, (frameMode == ScreenMode.FIXED ? 25 : 24), 4, (frameMode == ScreenMode.FIXED ? 29 : frameWidth - 176), 33, 25);
		if (frameMode != ScreenMode.FIXED && changeTabArea) {
			if (super.mouseX >= frameWidth - 26 && super.mouseX <= frameWidth - 1 && super.mouseY >= 2 && super.mouseY <= 24 || tabID == 15) {
				cacheSprite[27].drawSprite(frameWidth - 25, 2);
			} else {
				cacheSprite[27].drawARGBSprite(frameWidth - 25, 2, 165);
			}
		}
		loadAllOrbs(frameMode == ScreenMode.FIXED ? 0 : frameWidth - 217);
		if (menuOpen) {
			drawMenu(frameMode == ScreenMode.FIXED ? 516 : 0, 0);
		}
		if (frameMode == ScreenMode.FIXED) {
			aRSImageProducer_1165.initDrawingArea();
		}
	}

	private void npcScreenPos(Entity entity, int i) {
		calcEntityScreenPos(entity.x, i, entity.y);
	}

	private void calcEntityScreenPos(int i, int j, int l) {
		if (i < 128 || l < 128 || i > 13056 || l > 13056) {
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}
		int i1 = method42(plane, l, i) - j;
		i -= xCameraPos;
		i1 -= zCameraPos;
		l -= yCameraPos;
		int j1 = Model.modelIntArray1[yCameraCurve];
		int k1 = Model.modelIntArray2[yCameraCurve];
		int l1 = Model.modelIntArray1[xCameraCurve];
		int i2 = Model.modelIntArray2[xCameraCurve];
		int j2 = l * l1 + i * i2 >> 16;
		l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;
		if (l >= 50) {
			spriteDrawX = Rasterizer.centerX + (i << WorldController.viewDistance) / l;
			spriteDrawY = Rasterizer.centerY + (i1 << WorldController.viewDistance) / l;
		} else {
			spriteDrawX = -1;
			spriteDrawY = -1;
		}
	}

	private void buildSplitPrivateChatMenu() {
		if (splitPrivateChat == 0)
			return;
		int i = 0;
		if (anInt1104 != 0)
			i = 1;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null) {
				int k = chatTypes[j];
				String s = chatNames[j];
				for (int z = 1; z <= modIcons.length; z++) {
					if (s != null && s.startsWith("@cr" + z + "@")) {
						s = s.substring(z > 9 ? 6 : 5);
					}
				}
				if ((k == 3 || k == 7) && (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isFriendOrSelf(s))) {
					int l = 329 - i * 13;
					int offSet = frameMode == ScreenMode.FIXED ? 4 : 0;
					if (frameMode != ScreenMode.FIXED) {
						l = frameHeight - 170 - i * 13;
					}
					if (super.mouseX > 4 && super.mouseY - offSet > l - 10 && super.mouseY - offSet <= l + 3) {
						int i1 = aTextDrawingArea_1271.getTextWidth("From:  " + s + chatMessages[j]) + 25;
						if (i1 > 450)
							i1 = 450;
						if (super.mouseX < 4 + i1) {
							if (myPrivilege >= 1) {
								menuActionName[menuActionRow] = "Report abuse @whi@" + s;
								menuActionID[menuActionRow] = 2606;
								menuActionRow++;
							}
							menuActionName[menuActionRow] = "Add ignore @whi@" + s;
							menuActionID[menuActionRow] = 2042;
							menuActionRow++;
							menuActionName[menuActionRow] = "Add friend @whi@" + s;
							menuActionID[menuActionRow] = 2337;
							menuActionRow++;
						}
					}
					if (++i >= 5)
						return;
				}
				if ((k == 5 || k == 6) && privateChatMode < 2 && ++i >= 5)
					return;
			}

	}

	private void method130(int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2) {
		Class30_Sub1 class30_sub1 = null;
		for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179.reverseGetNext()) {
			if (class30_sub1_1.anInt1295 != l1 || class30_sub1_1.anInt1297 != i2 || class30_sub1_1.anInt1298 != j1 || class30_sub1_1.anInt1296 != i1)
				continue;
			class30_sub1 = class30_sub1_1;
			break;
		}

		if (class30_sub1 == null) {
			class30_sub1 = new Class30_Sub1();
			class30_sub1.anInt1295 = l1;
			class30_sub1.anInt1296 = i1;
			class30_sub1.anInt1297 = i2;
			class30_sub1.anInt1298 = j1;
			method89(class30_sub1);
			aClass19_1179.insertHead(class30_sub1);
		}
		class30_sub1.anInt1291 = k;
		class30_sub1.anInt1293 = k1;
		class30_sub1.anInt1292 = l;
		class30_sub1.anInt1302 = j2;
		class30_sub1.anInt1294 = j;
	}

	private boolean interfaceIsSelected(RSInterface class9) {
		if (class9.valueCompareType == null)
			return false;
		for (int i = 0; i < class9.valueCompareType.length; i++) {
			int j = extractInterfaceValues(class9, i);
			int k = class9.requiredValues[i];
			if (class9.valueCompareType[i] == 2) {
				if (j >= k)
					return false;
			} else if (class9.valueCompareType[i] == 3) {
				if (j <= k)
					return false;
			} else if (class9.valueCompareType[i] == 4) {
				if (j == k)
					return false;
			} else if (j != k)
				return false;
		}

		return true;
	}

	private DataInputStream openJagGrabInputStream(String s) throws IOException {
		// if(!aBoolean872)
		// if(signlink.mainapp != null)
		// return signlink.openurl(s);
		// else
		// return new DataInputStream((new URL(getCodeBase(), s)).openStream());
		if (aSocket832 != null) {
			try {
				aSocket832.close();
			} catch (Exception _ex) {
			}
			aSocket832 = null;
		}
		aSocket832 = openSocket(43595);
		aSocket832.setSoTimeout(10000);
		java.io.InputStream inputstream = aSocket832.getInputStream();
		OutputStream outputstream = aSocket832.getOutputStream();
		outputstream.write(("JAGGRAB /" + s + "\n\n").getBytes());
		return new DataInputStream(inputstream);
	}

	private void method134(Stream stream) {
		int j = stream.readBits(8);
		if (j < playerCount) {
			for (int k = j; k < playerCount; k++)
				anIntArray840[anInt839++] = playerIndices[k];

		}
		if (j > playerCount) {
			Signlink.reporterror(myUsername + " Too many players");
			throw new RuntimeException("eek");
		}
		playerCount = 0;
		for (int l = 0; l < j; l++) {
			int i1 = playerIndices[l];
			Player player = playerArray[i1];
			int j1 = stream.readBits(1);
			if (j1 == 0) {
				playerIndices[playerCount++] = i1;
				player.anInt1537 = loopCycle;
			} else {
				int k1 = stream.readBits(2);
				if (k1 == 0) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = i1;
				} else if (k1 == 1) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					int l1 = stream.readBits(3);
					player.moveInDir(false, l1);
					int j2 = stream.readBits(1);
					if (j2 == 1)
						anIntArray894[anInt893++] = i1;
				} else if (k1 == 2) {
					playerIndices[playerCount++] = i1;
					player.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					player.moveInDir(true, i2);
					int k2 = stream.readBits(3);
					player.moveInDir(true, k2);
					int l2 = stream.readBits(1);
					if (l2 == 1)
						anIntArray894[anInt893++] = i1;
				} else if (k1 == 3)
					anIntArray840[anInt839++] = i1;
			}
		}
	}

	private boolean drawnLogo = false;
	private int opacity;
	private int direction;
	private boolean rememberMe;

	private void drawLoginScreen() {
		resetImageProducers();
		loginScreenProducer.initDrawingArea();

		/*
		 * Get our center point
		 */
		final int centerX = frameWidth / 2, centerY = frameHeight / 2;

		com.koios.client.arpg.ExileLogin.Layout exileLayout=com.koios.client.arpg.ExileLogin.layout(frameWidth,frameHeight);
        final int boxX=exileLayout.x,boxY=exileLayout.y;
        com.koios.client.arpg.ExileLogin.background();
        com.koios.client.arpg.ExileLogin.chrome(com.koios.client.arpg.ExileTheme.body(),exileLayout,mouseX,mouseY,rememberMe,loginScreenCursorPos);

		/*
		 * Handles the cursor flashing
		 */
		opacity += direction == 0 ? 1 : -1;

		if (direction == 0 && opacity >= 30) {
			direction = 1;
		} else if (direction == 1 && opacity == 0) {
			direction = 0;
		}

		// Obfuscate the password so we dont need to do it multiple times
		String obfuscatedPassword = UsernameEncoding.obfuscateString(myPassword);

		com.koios.client.arpg.ExileLogin.credentials(exileLayout,UsernameEncoding.formalize(myUsername),obfuscatedPassword,loginScreenCursorPos,opacity < 10 || opacity > 20);
		com.koios.client.arpg.ExileLogin.feedback(exileLayout,loginMessage1);
		// newRegularFont.drawCenteredString(loginMessage2, centerX, boxY + 240,
		// 0xFFFFFF, 0);

		positionLoginWorld();
		DrawingArea.drawRoundedRectangle(worldX, worldY, worldWidth, worldHeight, com.koios.client.arpg.ExileTheme.OXBLOOD, 125, true, false);

		newFancyFont.drawCenteredString(clientStatus.toString(), worldX + 35, worldY + 24, com.koios.client.arpg.ExileTheme.MUTED, -1);

		newRegularFont.drawBasicString("EXILE / PROTOTYPE", 10, frameHeight - 10);

		/*
		 * Draw our image producer for the login screen
		 */
		loginScreenProducer.drawGraphics(0, super.graphics, 0);
		if (welcomeScreenRaised) {
			welcomeScreenRaised = false;
		}
	}

	private int worldX;
	private int worldY;
	private final int worldWidth = 70;
	private final int worldHeight = 35;
	private final int worldColor = 0x009c87;
    private void positionLoginWorld(){java.awt.Rectangle area=com.koios.client.arpg.ExileLogin.layout(frameWidth,frameHeight).world;worldX=area.x;worldY=area.y;}

	public static int realPlane;

	private void drawFlames() {
	}

	@Override
	public void raiseWelcomeScreen() {
		welcomeScreenRaised = true;
	}

	private void method137(Stream stream, int j) {
		if (j == 84) {
			int k = stream.readUnsignedByte();
			int j3 = anInt1268 + (k >> 4 & 7);
			int i6 = anInt1269 + (k & 7);
			int l8 = stream.readUnsignedShort();
			int k11 = stream.readUnsignedShort();
			int l13 = stream.readUnsignedShort();
			if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104) {
				NodeList class19_1 = groundArray[plane][j3][i6];
				if (class19_1 != null) {
					for (Item class30_sub2_sub4_sub2_3 = (Item) class19_1.reverseGetFirst(); class30_sub2_sub4_sub2_3 != null; class30_sub2_sub4_sub2_3 = (Item) class19_1.reverseGetNext()) {
						if (class30_sub2_sub4_sub2_3.ID != (l8 & 0x7fff) || class30_sub2_sub4_sub2_3.anInt1559 != k11)
							continue;
						class30_sub2_sub4_sub2_3.anInt1559 = l13;
						break;
					}

					spawnGroundItem(j3, i6);
				}
			}
			return;
		}
		if (j == 105) {
			int l = stream.readUnsignedByte();
			int k3 = anInt1268 + (l >> 4 & 7);
			int j6 = anInt1269 + (l & 7);
			int i9 = stream.readUnsignedShort();
			int l11 = stream.readUnsignedByte();
			int i14 = l11 >> 4 & 0xf;
			int i16 = l11 & 7;
			if (myPlayer.smallX[0] >= k3 - i14 && myPlayer.smallX[0] <= k3 + i14 && myPlayer.smallY[0] >= j6 - i14 && myPlayer.smallY[0] <= j6 + i14 && soundEffectVolume != 0 && aBoolean848 && !lowMem
					&& soundCount < 50) {
				sound[soundCount] = i9;
				soundType[soundCount] = i16;
				soundDelay[soundCount] = Sounds.anIntArray326[i9];
				aClass26Array1468[soundCount] = null;
				soundCount++;
			}

		}
		if (j == 215) {
			int i1 = stream.method435();
			int l3 = stream.readByteS();
			int k6 = anInt1268 + (l3 >> 4 & 7);
			int j9 = anInt1269 + (l3 & 7);
			int i12 = stream.method435();
			int j14 = stream.readUnsignedShort();
			if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != unknownInt10) {
				Item class30_sub2_sub4_sub2_2 = new Item();
				class30_sub2_sub4_sub2_2.ID = i1;
				class30_sub2_sub4_sub2_2.anInt1559 = j14;
				if (groundArray[plane][k6][j9] == null)
					groundArray[plane][k6][j9] = new NodeList();
				groundArray[plane][k6][j9].insertHead(class30_sub2_sub4_sub2_2);
				spawnGroundItem(k6, j9);
			}
			return;
		}
		if (j == 156) {
			int j1 = stream.readByteA();
			int i4 = anInt1268 + (j1 >> 4 & 7);
			int l6 = anInt1269 + (j1 & 7);
			int k9 = stream.readUnsignedShort();
			if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104) {
				NodeList class19 = groundArray[plane][i4][l6];
				if (class19 != null) {
					for (Item item = (Item) class19.reverseGetFirst(); item != null; item = (Item) class19.reverseGetNext()) {
						if (item.ID != (k9 & 0x7fff))
							continue;
						item.unlink();
						break;
					}

					if (class19.reverseGetFirst() == null)
						groundArray[plane][i4][l6] = null;
					spawnGroundItem(i4, l6);
				}
			}
			return;
		}
		if (j == 160) {
			int k1 = stream.readByteS();
			int j4 = anInt1268 + (k1 >> 4 & 7);
			int i7 = anInt1269 + (k1 & 7);
			int l9 = stream.readByteS();
			int j12 = l9 >> 2;
			int k14 = l9 & 3;
			int j16 = anIntArray1177[j12];
			int j17 = stream.method435();
			if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103) {
				int j18 = intGroundArray[plane][j4][i7];
				int i19 = intGroundArray[plane][j4 + 1][i7];
				int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
				int k20 = intGroundArray[plane][j4][i7 + 1];
				if (j16 == 0) {
					Object1 class10 = worldController.method296(plane, j4, i7);
					if (class10 != null) {
						int k21 = class10.uid >> 14 & 0x7fff;
						if (j12 == 2) {
							class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(k21, 4 + k14, 2, i19, l19, j18, k20, j17, false);
							class10.aClass30_Sub2_Sub4_279 = new Animable_Sub5(k21, k14 + 1 & 3, 2, i19, l19, j18, k20, j17, false);
						} else {
							class10.aClass30_Sub2_Sub4_278 = new Animable_Sub5(k21, k14, j12, i19, l19, j18, k20, j17, false);
						}
					}
				}
				if (j16 == 1) {
					Object2 class26 = worldController.method297(j4, i7, plane);
					if (class26 != null)
						class26.aClass30_Sub2_Sub4_504 = new Animable_Sub5(class26.uid >> 14 & 0x7fff, 0, 4, i19, l19, j18, k20, j17, false);
				}
				if (j16 == 2) {
					Object5 class28 = worldController.method298(j4, i7, plane);
					if (j12 == 11)
						j12 = 10;
					if (class28 != null)
						class28.aClass30_Sub2_Sub4_521 = new Animable_Sub5(class28.uid >> 14 & 0x7fff, k14, j12, i19, l19, j18, k20, j17, false);
				}
				if (j16 == 3) {
					Object3 class49 = worldController.method299(i7, j4, plane);
					if (class49 != null)
						class49.aClass30_Sub2_Sub4_814 = new Animable_Sub5(class49.uid >> 14 & 0x7fff, k14, 22, i19, l19, j18, k20, j17, false);
				}
			}
			return;
		}
		if (j == 147) {
			int l1 = stream.readByteS();
			int k4 = anInt1268 + (l1 >> 4 & 7);
			int j7 = anInt1269 + (l1 & 7);
			int i10 = stream.readUnsignedShort();
			byte byte0 = stream.method430();
			int l14 = stream.readLEShort();
			byte byte1 = stream.method429();
			int k17 = stream.readUnsignedShort();
			int k18 = stream.readByteS();
			int j19 = k18 >> 2;
			int i20 = k18 & 3;
			int l20 = anIntArray1177[j19];
			byte byte2 = stream.readSignedByte();
			int l21 = stream.readUnsignedShort();
			byte byte3 = stream.method429();
			Player player;
			if (i10 == unknownInt10)
				player = myPlayer;
			else
				player = playerArray[i10];
			if (player != null) {
				ObjectDefinition class46 = ObjectDefinition.forID(l21);
				int i22 = intGroundArray[plane][k4][j7];
				int j22 = intGroundArray[plane][k4 + 1][j7];
				int k22 = intGroundArray[plane][k4 + 1][j7 + 1];
				int l22 = intGroundArray[plane][k4][j7 + 1];
				Model model = class46.method578(j19, i20, i22, j22, k22, l22, -1);
				if (model != null) {
					method130(k17 + 1, -1, 0, l20, j7, 0, plane, k4, l14 + 1);
					player.anInt1707 = l14 + loopCycle;
					player.anInt1708 = k17 + loopCycle;
					player.aModel_1714 = model;
					int i23 = class46.sizeX;
					int j23 = class46.sizeY;
					if (i20 == 1 || i20 == 3) {
						i23 = class46.sizeY;
						j23 = class46.sizeX;
					}
					player.anInt1711 = k4 * 128 + i23 * 64;
					player.anInt1713 = j7 * 128 + j23 * 64;
					player.anInt1712 = method42(plane, player.anInt1713, player.anInt1711);
					if (byte2 > byte0) {
						byte byte4 = byte2;
						byte2 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte1) {
						byte byte5 = byte3;
						byte3 = byte1;
						byte1 = byte5;
					}
					player.anInt1719 = k4 + byte2;
					player.anInt1721 = k4 + byte0;
					player.anInt1720 = j7 + byte3;
					player.anInt1722 = j7 + byte1;
				}
			}
		}
		if (j == 151) {
			int i2 = stream.readByteA();
			int l4 = anInt1268 + (i2 >> 4 & 7);
			int k7 = anInt1269 + (i2 & 7);
			int j10 = stream.readLEShort();
			int k12 = stream.readByteS();
			int i15 = k12 >> 2;
			int k16 = k12 & 3;
			int l17 = anIntArray1177[i15];
			if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
				method130(-1, j10, k16, l17, k7, i15, plane, l4, 0);
			return;
		}
		if (j == 4) {
			int j2 = stream.readUnsignedByte();
			int i5 = anInt1268 + (j2 >> 4 & 7);
			int l7 = anInt1269 + (j2 & 7);
			int k10 = stream.readUnsignedShort();
			int l12 = stream.readUnsignedByte();
			int j15 = stream.readUnsignedShort();
			if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104) {
				i5 = i5 * 128 + 64;
				l7 = l7 * 128 + 64;
				Animable_Sub3 class30_sub2_sub4_sub3 = new Animable_Sub3(plane, loopCycle, j15, k10, method42(plane, l7, i5) - l12, l7, i5);
				aClass19_1056.insertHead(class30_sub2_sub4_sub3);
			}
			return;
		}
		if (j == 44) {
			int k2 = stream.method436();
			int j5 = stream.readUnsignedShort();
			int i8 = stream.readUnsignedByte();
			int l10 = anInt1268 + (i8 >> 4 & 7);
			int i13 = anInt1269 + (i8 & 7);
			if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104) {
				Item class30_sub2_sub4_sub2_1 = new Item();
				class30_sub2_sub4_sub2_1.ID = k2;
				class30_sub2_sub4_sub2_1.anInt1559 = j5;
				if (groundArray[plane][l10][i13] == null)
					groundArray[plane][l10][i13] = new NodeList();
				groundArray[plane][l10][i13].insertHead(class30_sub2_sub4_sub2_1);
				spawnGroundItem(l10, i13);
			}
			return;
		}
		if (j == 101) {
			int l2 = stream.readByteC();
			int k5 = l2 >> 2;
			int j8 = l2 & 3;
			int i11 = anIntArray1177[k5];
			int j13 = stream.readUnsignedByte();
			int k15 = anInt1268 + (j13 >> 4 & 7);
			int l16 = anInt1269 + (j13 & 7);
			if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
				method130(-1, -1, j8, i11, l16, k5, plane, k15, 0);
			return;
		}
		if (j == 117) {
			int i3 = stream.readUnsignedByte();
			int l5 = anInt1268 + (i3 >> 4 & 7);
			int k8 = anInt1269 + (i3 & 7);
			int j11 = l5 + stream.readSignedByte();
			int k13 = k8 + stream.readSignedByte();
			int l15 = stream.readSignedWord();
			int i17 = stream.readUnsignedShort();
			int i18 = stream.readUnsignedByte() * 4;
			int l18 = stream.readUnsignedByte() * 4;
			int k19 = stream.readUnsignedShort();
			int j20 = stream.readUnsignedShort();
			int i21 = stream.readUnsignedByte();
			int j21 = stream.readUnsignedByte();
			if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0 && k13 >= 0 && j11 < 104 && k13 < 104 && i17 != 65535) {
				l5 = l5 * 128 + 64;
				k8 = k8 * 128 + 64;
				j11 = j11 * 128 + 64;
				k13 = k13 * 128 + 64;
				Animable_Sub4 class30_sub2_sub4_sub4 = new Animable_Sub4(i21, l18, k19 + loopCycle, j20 + loopCycle, j21, plane, method42(plane, k8, l5) - i18, k8, l5, l15, i17);
				class30_sub2_sub4_sub4.method455(k19 + loopCycle, k13, method42(plane, k13, j11) - l18, j11);
				aClass19_1013.insertHead(class30_sub2_sub4_sub4);
			}
		}
	}

	private void updateNpcMovement(Stream stream) {
		stream.initBitAccess();
		int k = stream.readBits(8);

		if (k < npcCount) {
			for (int l = k; l < npcCount; l++)
				anIntArray840[anInt839++] = npcIndices[l];

		}
		if (k > npcCount) {
			Signlink.reporterror(myUsername + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;

		for (int count = 0; count < k; count++) {
			int index = npcIndices[count];
			NPC npc = npcArray[index];
			int flag = stream.readBits(1);
			if (flag == 0) {
				npcIndices[npcCount++] = index;
				npc.anInt1537 = loopCycle;
			} else {
				int direction = stream.readBits(2);
				if (direction == 0) {
					npcIndices[npcCount++] = index;
					npc.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = index;
				} else if (direction == 1) {
					npcIndices[npcCount++] = index;
					npc.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					npc.moveInDir(false, i2);
					int k2 = stream.readBits(1);
					if (k2 == 1)
						anIntArray894[anInt893++] = index;
				} else if (direction == 2) {
					npcIndices[npcCount++] = index;
					npc.anInt1537 = loopCycle;
					int j2 = stream.readBits(3);
					npc.moveInDir(true, j2);
					int l2 = stream.readBits(3);
					npc.moveInDir(true, l2);
					int i3 = stream.readBits(1);
					if (i3 == 1)
						anIntArray894[anInt893++] = index;
				} else if (direction == 3)
					anIntArray840[anInt839++] = index;
			}
		}

	}

	private boolean qaLoginAttempted;
 private void processLoginScreenInput() {
        String qaCharacter=System.getProperty("arpg.qaCharacter","");
        if(!qaLoginAttempted&&clientStatus==ClientStatus.LOCAL&&qaCharacter.matches("qa[a-z0-9]{3,9}")){qaLoginAttempted=true;myUsername=qaCharacter;myPassword="qatest123";login(myUsername,myPassword,false);return;}

        positionLoginWorld();
		if (mouseInRegion(worldX, worldY, worldX + worldWidth, worldY + worldHeight) && super.clickMode2 > 0) {
			clientStatus = clientStatus.getNext();
			super.clickMode2 = 0;
		}

		final int centerX = frameWidth / 2, centerY = frameHeight / 2;

		com.koios.client.arpg.ExileLogin.Layout exileLayout=com.koios.client.arpg.ExileLogin.layout(frameWidth,frameHeight);
        final int boxX=exileLayout.x,boxY=exileLayout.y;

		boolean inLoginButton = clickMode3==1 && exileLayout.submit.contains(saveClickX,saveClickY);

		if (inLoginButton) {

			if (myUsername.length() < 3) {
				loginMessage1 = "Please enter a valid username...";
			} else if (myPassword.length() < 5) {
				loginMessage1 = "Please enter a valid password...";
			} else {
				login(myUsername, myPassword, false);
			}
		}
		boolean inUsernameBox = clickMode3==1 && exileLayout.account.contains(saveClickX,saveClickY);
		boolean inPasswordBox = clickMode3==1 && exileLayout.password.contains(saveClickX,saveClickY);
		if (inUsernameBox) {
			loginScreenCursorPos = 0;
		} else if (inPasswordBox) {
			loginScreenCursorPos = 1;
		}

		if (clickMode3==1 && exileLayout.remember.contains(saveClickX,saveClickY)) {
			rememberMe = !rememberMe;
			saveData();
		}

		do {
			int l1 = readChar(-796);
			if (l1 == -1)
				break;
			boolean flag1 = false;
			for (int i2 = 0; i2 < validUserPassChars.length(); i2++) {
				if (l1 != validUserPassChars.charAt(i2))
					continue;
				flag1 = true;
				break;
			}

			if (loginScreenCursorPos == 0) {
				if (l1 == 8 && myUsername.length() > 0)
					myUsername = myUsername.substring(0, myUsername.length() - 1);
				if (l1 == 9 || l1 == 10 || l1 == 13)
					loginScreenCursorPos = 1;
				if (flag1)
					myUsername += (char) l1;
				if (myUsername.length() > 12)
					myUsername = myUsername.substring(0, 12);
			} else if (loginScreenCursorPos == 1) {
				if (l1 == 8 && myPassword.length() > 0)
					myPassword = myPassword.substring(0, myPassword.length() - 1);
				if (l1 == 9 || l1 == 10 || l1 == 13)
					if (myUsername.length() > 2 && myPassword.length() > 4)
						loginScreenCursorPos = 0;
					else
						login(myUsername, myPassword, false);
				if (flag1)
					myPassword += (char) l1;
				if (myPassword.length() > 20)
					myPassword = myPassword.substring(0, 20);
			}
		} while (true);
		return;
	}

	private void method142(int i, int j, int k, int l, int i1, int j1, int k1) {
		if (i1 >= 1 && i >= 1 && i1 <= 102 && i <= 102) {
			if (lowMem && j != plane)
				return;
			int i2 = 0;
			if (j1 == 0)
				i2 = worldController.method300(j, i1, i);
			if (j1 == 1)
				i2 = worldController.method301(j, i1, i);
			if (j1 == 2)
				i2 = worldController.method302(j, i1, i);
			if (j1 == 3)
				i2 = worldController.method303(j, i1, i);
			if (i2 != 0) {
				int i3 = worldController.method304(j, i1, i, i2);
				int j2 = i2 >> 14 & 0x7fff;
				int k2 = i3 & 0x1f;
				int l2 = i3 >> 6;
				if (j1 == 0) {
					worldController.method291(i1, j, i, (byte) -119);
					ObjectDefinition class46 = ObjectDefinition.forID(j2);
					if (class46.aBoolean767)
						aClass11Array1230[j].method215(l2, k2, class46.aBoolean757, i1, i);
				}
				if (j1 == 1)
					worldController.method292(i, j, i1);
				if (j1 == 2) {
					worldController.method293(j, i1, i);
					ObjectDefinition class46_1 = ObjectDefinition.forID(j2);
					if (i1 + class46_1.sizeX > 103 || i + class46_1.sizeX > 103 || i1 + class46_1.sizeY > 103 || i + class46_1.sizeY > 103)
						return;
					if (class46_1.aBoolean767)
						aClass11Array1230[j].method216(l2, class46_1.sizeX, i1, i, class46_1.sizeY, class46_1.aBoolean757);
				}
				if (j1 == 3) {
					worldController.method294(j, i, i1);
					ObjectDefinition class46_2 = ObjectDefinition.forID(j2);
					if (class46_2.aBoolean767 && class46_2.hasActions)
						aClass11Array1230[j].method218(i, i1);
				}
			}
			if (k1 >= 0) {
				int j3 = j;
				if (j3 < 3 && (byteGroundArray[1][i1][i] & 2) == 2)
					j3++;
				ObjectManager.method188(worldController, k, i, l, j3, aClass11Array1230[j], intGroundArray, i1, k1, j);
			}
		}
	}

	private void updatePlayers(int i, Stream stream) {
		anInt839 = 0;
		anInt893 = 0;
		method117(stream);
		method134(stream);
		method91(stream, i);
		method49(stream);
		for (int k = 0; k < anInt839; k++) {
			int l = anIntArray840[k];
			if (playerArray[l].anInt1537 != loopCycle)
				playerArray[l] = null;
		}

		if (stream.currentOffset != i) {
			Signlink.reporterror("Error packet size mismatch in getplayer pos:" + stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < playerCount; i1++)
			if (playerArray[playerIndices[i1]] == null) {
				Signlink.reporterror(myUsername + " null entry in pl list - pos:" + i1 + " size:" + playerCount);
				throw new RuntimeException("eek");
			}

	}

	private void setCameraPos(int j, int k, int l, int i1, int j1, int k1) {
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		int l2 = j;
		if (l1 != 0) {
			int i3 = Model.modelIntArray1[l1];
			int k3 = Model.modelIntArray2[l1];
			int i4 = k2 * k3 - l2 * i3 >> 16;
			l2 = k2 * i3 + l2 * k3 >> 16;
			k2 = i4;
		}
		if (i2 != 0) {
			int j3 = Model.modelIntArray1[i2];
			int l3 = Model.modelIntArray2[i2];
			int j4 = l2 * j3 + j2 * l3 >> 16;
			l2 = l2 * l3 - j2 * j3 >> 16;
			j2 = j4;
		}
		xCameraPos = l - j2;
		zCameraPos = i1 - k2;
		yCameraPos = k1 - l2;
		yCameraCurve = k;
		xCameraCurve = j1;
	}

	public void updateStrings(String str, int i) {
		switch (i) {
		case 1675:
			sendFrame126(str, 17508);
			break;// Stab
		case 1676:
			sendFrame126(str, 17509);
			break;// Slash
		case 1677:
			sendFrame126(str, 17510);
			break;// Cursh
		case 1678:
			sendFrame126(str, 17511);
			break;// Magic
		case 1679:
			sendFrame126(str, 17512);
			break;// Range
		case 1680:
			sendFrame126(str, 17513);
			break;// Stab
		case 1681:
			sendFrame126(str, 17514);
			break;// Slash
		case 1682:
			sendFrame126(str, 17515);
			break;// Crush
		case 1683:
			sendFrame126(str, 17516);
			break;// Magic
		case 1684:
			sendFrame126(str, 17517);
			break;// Range
		case 1686:
			sendFrame126(str, 17518);
			break;// Strength
		case 1687:
			sendFrame126(str, 17519);
			break;// Prayer
		}
	}

	public void sendFrame126(String str, int i) {
		RSInterface.interfaceCache[i].message = str;
		if (RSInterface.interfaceCache[i].parentID == tabInterfaceIDs[tabID]) {
		}
	}

	public void sendPacket185(int button, int toggle, int type) {
		switch (type) {
		case 135:
			RSInterface class9 = RSInterface.interfaceCache[button];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8) {
				stream.createFrame(185);
				stream.writeShort(button);
			}
			break;
		case 646:
			stream.createFrame(185);
			stream.writeShort(button);
			RSInterface class9_2 = RSInterface.interfaceCache[button];
			if (class9_2.instructions != null && class9_2.instructions[0][0] == 5) {
				if (variousSettings[toggle] != class9_2.requiredValues[0]) {
					variousSettings[toggle] = class9_2.requiredValues[0];
					method33(toggle);
				}
			}
			break;
		case 169:
			stream.createFrame(185);
			stream.writeShort(button);
			RSInterface class9_3 = RSInterface.interfaceCache[button];
			if (class9_3.instructions != null && class9_3.instructions[0][0] == 5) {
				variousSettings[toggle] = 1 - variousSettings[toggle];
				method33(toggle);
			}
			switch (button) {
			case 74214:
				if (toggle == 0)
					sendFrame36(173, toggle);
				if (toggle == 1)
					sendPacket185(153, 173, 646);
				break;
			}
			break;
		}
	}

	public void sendFrame36(int id, int state) {
		anIntArray1045[id] = state;
		if (variousSettings[id] != state) {
			variousSettings[id] = state;
			method33(id);
			if (dialogID != -1)
				inputTaken = true;
		}
	}

	public void sendFrame219() {
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = -1;
		aBoolean1149 = false;
	}

	public void sendFrame248(int interfaceID, int sideInterfaceID) {
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0) {
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = interfaceID;
		invOverlayInterfaceID = sideInterfaceID;
		tabAreaAltered = true;
		aBoolean1149 = false;
	}

	private boolean parsePacket() {
		if (socketStream == null)
			return false;
		try {
			int available = socketStream.available();
			if (available == 0)
				return false;
			if (pktType == -1) {
				socketStream.flushInputStream(inStream.buffer, 1);
				pktType = inStream.buffer[0] & 0xff;
				if (encryption != null)
					pktType = pktType - encryption.getNextKey() & 0xff;
				pktSize = SizeConstants.packetSizes[pktType];
				available--;
			}
			if (pktSize == -1)
				if (available > 0) {
					socketStream.flushInputStream(inStream.buffer, 1);
					pktSize = inStream.buffer[0] & 0xff;
					available--;
				} else {
					return false;
				}
			if (pktSize == -2)
				if (available > 1) {
					socketStream.flushInputStream(inStream.buffer, 2);
					inStream.currentOffset = 0;
					pktSize = inStream.readUnsignedShort();
					available -= 2;
				} else {
					return false;
				}
			if (available < pktSize)
				return false;
			inStream.currentOffset = 0;
			socketStream.flushInputStream(inStream.buffer, pktSize);
			anInt1009 = 0;
			anInt843 = anInt842;
			anInt842 = anInt841;
			anInt841 = pktType;
			switch (pktType) {
			case 81:
				updatePlayers(pktSize, inStream);
				aBoolean1080 = false;
				pktType = -1;
				return true;

			case 176:
				daysSinceRecovChange = inStream.readByteC();
				unreadMessages = inStream.method435();
				membersInt = inStream.readUnsignedByte();
				anInt1193 = inStream.method440();
				daysSinceLastLogin = inStream.readUnsignedShort();
				if (anInt1193 != 0 && openInterfaceID == -1) {
					Signlink.dnslookup(TextClass.method586(anInt1193));
					clearTopInterfaces();
					char c = '\u028A';
					if (daysSinceRecovChange != 201 || membersInt == 1)
						c = '\u028F';
					reportAbuseInput = "";
					canMute = false;
					for (int k9 = 0; k9 < RSInterface.interfaceCache.length; k9++) {
						if (RSInterface.interfaceCache[k9] == null || RSInterface.interfaceCache[k9].contentType != c)
							continue;
						openInterfaceID = RSInterface.interfaceCache[k9].parentID;

					}
				}
				pktType = -1;
				return true;

			case 190: // reset clanchat interface
				try {
					for (int z = 0; z < clanList.length; z++) {
						clanList[z] = null;
					}
					RSInterface.interfaceCache[18139].setText("Talking in:");
					RSInterface.interfaceCache[18140].setText("Owner:");

					// reset name list
					for (int z = 18144; z < 18244; z++) {
						RSInterface.interfaceCache[z].setText("");
					}

					// reset icon list
					for (int z = 38144; z < 38244; z++) {
						if (RSInterface.interfaceCache[z] != null) {
							RSInterface.interfaceCache[z].sprite1 = Client.cacheSprite[491];
							RSInterface.interfaceCache[z].sprite2 = Client.cacheSprite[491];
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				pktType = -1;
				return true;

			case 180:
				int rankId = inStream.readUnsignedShort();
				int frameId = inStream.readUnsignedShort();

				if (RSInterface.interfaceCache[frameId] != null) {
					RSInterface.interfaceCache[frameId].sprite1 = cacheSprite[CLANCHAT_RANK_SPRITE[rankId]];
					RSInterface.interfaceCache[frameId].sprite2 = cacheSprite[CLANCHAT_RANK_SPRITE[rankId]];
				}

				pktType = -1;
				return true;

			case 181:
				String playerName = capitalize(inStream.readString());
				int rank = inStream.readUnsignedByte();
				final ClanMember member = new ClanMember(playerName, rank);
				boolean done = false;

				for (int a = 0; a < rankedNames.size(); a++) {
					if (rankedNames.get(a).getPlayerName().equalsIgnoreCase(member.getPlayerName())) {
						rankedNames.get(a).setRank(rank);
						done = true;
					}
				}

				if (!done) {
					rankedNames.add(member);
				}

				rank = -1;
				pktType = -1;
				return true;

			case 182:
				
				try {
					int slot = 44001;

					for (int i = 0; i < rankedNames.size(); i++) {
						setInterfaceText(rankedNames.get(i).getPlayerName(), slot);
						setInterfaceText(rankedNames.get(i).getRank().toStr(), slot + 800);
						slot++;
					}

					for (int i = slot; i <= 44200; i++) {
						setInterfaceText("", i);
						setInterfaceText("", i + 800);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				pktType = -1;
				return true;

			case 64:
				anInt1268 = inStream.readByteC();
				anInt1269 = inStream.readByteS();
				for (int j = anInt1268; j < anInt1268 + 8; j++) {
					for (int l9 = anInt1269; l9 < anInt1269 + 8; l9++)
						if (groundArray[plane][j][l9] != null) {
							groundArray[plane][j][l9] = null;
							spawnGroundItem(j, l9);
						}
				}
				for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetNext())
					if (class30_sub1.anInt1297 >= anInt1268 && class30_sub1.anInt1297 < anInt1268 + 8 && class30_sub1.anInt1298 >= anInt1269 && class30_sub1.anInt1298 < anInt1269 + 8
							&& class30_sub1.anInt1295 == plane)
						class30_sub1.anInt1294 = 0;
				pktType = -1;
				return true;

			case 185:
				int buttonId = inStream.method436();
				RSInterface.interfaceCache[buttonId].anInt233 = 3;
				if (myPlayer.desc == null)
					RSInterface.interfaceCache[buttonId].mediaID = (myPlayer.anIntArray1700[0] << 25) + (myPlayer.anIntArray1700[4] << 20) + (myPlayer.equipment[0] << 15)
							+ (myPlayer.equipment[8] << 10) + (myPlayer.equipment[11] << 5) + myPlayer.equipment[1];
				else
					RSInterface.interfaceCache[buttonId].mediaID = (int) (0x12345678L + myPlayer.desc.interfaceType);
				pktType = -1;
				return true;

			/* Clan chat packet */
			case 217:
				try {
					name = inStream.readString();
					message = inStream.readString();
					clanname = inStream.readString();
					rights = inStream.readUnsignedShort();
					// message = TextInput.processText(message);
					// message = Censor.doCensor(message);
					pushMessage(message, 16, name);
				} catch (Exception e) {
					e.printStackTrace();
				}
				pktType = -1;
				return true;

			case 107:
				aBoolean1160 = false;
				for (int l = 0; l < 5; l++)
					aBooleanArray876[l] = false;
				pktType = -1;
				return true;

			case 72:
				int i1 = inStream.readLEShort();
				RSInterface class9 = RSInterface.interfaceCache[i1];
				for (int k15 = 0; k15 < class9.container.length; k15++) {
					class9.container[k15] = -1;
					class9.container[k15] = 0;
				}
				pktType = -1;
				return true;

			case 214:
				ignoreCount = pktSize / 8;
				for (int j1 = 0; j1 < ignoreCount; j1++)
					ignoreListAsLongs[j1] = inStream.readLong();
				pktType = -1;
				return true;

			case 166:
				aBoolean1160 = true;
				anInt1098 = inStream.readUnsignedByte();
				anInt1099 = inStream.readUnsignedByte();
				anInt1100 = inStream.readUnsignedShort();
				anInt1101 = inStream.readUnsignedByte();
				anInt1102 = inStream.readUnsignedByte();
				if (anInt1102 >= 100) {
					xCameraPos = anInt1098 * 128 + 64;
					yCameraPos = anInt1099 * 128 + 64;
					zCameraPos = method42(plane, yCameraPos, xCameraPos) - anInt1100;
				}
				pktType = -1;
				return true;

			case 25:
				int goalSkill = inStream.readUnsignedByte();
				int goal = inStream.method439();
				int goalType = inStream.readUnsignedByte();
				int initExp = inStream.method439();

				statsSkillGoal[goalSkill][0] = goal;
				statsSkillGoal[goalSkill][1] = goalType;
				statsSkillGoal[goalSkill][2] = initExp;
				pktType = -1;
				return true;

			case 134:
				int skill = inStream.readUnsignedByte();
				int experience = inStream.method439();
				int level = inStream.readUnsignedByte();
				statsExperience[skill] = experience;
				statsCurrentLevel[skill] = level;
				statsMaximumLevel[skill] = 1;

				for (int z = 0; z < 98; z++) {
					if (experience >= EXPERIENCE_BY_LEVEL[z]) {
						statsMaximumLevel[skill] = z + 2;
					}
				}

				if(skill==3){statsCurrentLevel[3]=arpgLife;statsMaximumLevel[3]=arpgMaxLife;}
                if (skill < 24) {
					String text = SkillConstants.SKILL_NAMES_ORDERED[skill] + ": " + statsCurrentLevel[skill] + "/" + statsMaximumLevel[skill] + "\nCurrent Xp: %2";

					if (statsMaximumLevel[skill] < 99) {
						text += "\nNext level: %3";
						text += "\nRemainder: %4";
					}

					if (statsSkillGoal[skill][0] != -1) {
						text += "\nTarget " + (statsSkillGoal[skill][1] == 0 ? "lvl" : "Xp") + ": %5";
						text += "\nRemainder: %6";
					}

					RSInterface.interfaceCache[SkillConstants.MORE_DETAILS_PANEL_ID[skill]].setText(text);
				}
				pktType = -1;
				return true;

			case 71:
				int l1 = inStream.readUnsignedShort();
				int j10 = inStream.readByteA();
				if (l1 == 65535)
					l1 = -1;
				tabInterfaceIDs[j10] = l1;
				tabAreaAltered = true;
				pktType = -1;
				return true;

			case 74:
				int songId = inStream.readLEShort();
				if (songId == 65535) {
					songId = -1;
				}
				if (songId != -1 || prevSong != 0) {
					if (songId != -1 && currentSong != songId && musicVolume != 0 && prevSong == 0) {
						method58(10, musicVolume, false, songId);
					}
				} else {
					method55(false);
				}
				currentSong = songId;
				pktType = -1;
				return true;

			case 121:
				int tempSongId = inStream.method436();
				int tempSongDelay = inStream.method435();
				if (tempSongId == 65535) {
					tempSongId = -1;
				}
				if (musicVolume != 0 && tempSongDelay != -1) {
					method56(musicVolume, false, tempSongId);
					prevSong = tempSongDelay * 20;
				}
				pktType = -1;
				return true;

			case 109:
				resetLogout();
				pktType = -1;
				return false;

			case 70:
				int k2 = inStream.readSignedWord();
				int l10 = inStream.method437();
				int i16 = inStream.readLEShort();
				RSInterface class9_5 = RSInterface.interfaceCache[i16];
				class9_5.xOffset = k2;
				class9_5.yOffset = l10;
				pktType = -1;
				return true;

			case 73:
			case 241:
				int l2 = anInt1069;
				int i11 = anInt1070;
				if (pktType == 73) {
					l2 = mapX = inStream.method435();
					i11 = mapY = inStream.readUnsignedShort();
					aBoolean1159 = false;
				}

				if (pktType == 241) {
					i11 = inStream.method435();
					inStream.initBitAccess();
					for (int j16 = 0; j16 < 4; j16++) {
						for (int l20 = 0; l20 < 13; l20++) {
							for (int j23 = 0; j23 < 13; j23++) {
								int i26 = inStream.readBits(1);
								if (i26 == 1)
									anIntArrayArrayArray1129[j16][l20][j23] = inStream.readBits(26);
								else
									anIntArrayArrayArray1129[j16][l20][j23] = -1;
							}
						}
					}
					inStream.finishBitAccess();
					l2 = inStream.readUnsignedShort();
					aBoolean1159 = true;
				}
				if (anInt1069 == l2 && anInt1070 == i11 && loadingStage == 2 && !arpgTerrainReload) {
					pktType = -1;
					return true;
				}
                arpgTerrainReload=false;
				anInt1069 = l2;
				anInt1070 = i11;
				baseX = (anInt1069 - 6) * 8;
				baseY = (anInt1070 - 6) * 8;
				aBoolean1141 = (anInt1069 / 8 == 48 || anInt1069 / 8 == 49) && anInt1070 / 8 == 48;
				if (anInt1069 / 8 == 48 && anInt1070 / 8 == 148)
					aBoolean1141 = true;
				loadingStage = 1;
				aLong824 = System.currentTimeMillis();
				aRSImageProducer_1165.initDrawingArea();
				drawLoadingMessages(1, "Loading - please wait.", null);
				aRSImageProducer_1165.drawGraphics(frameMode == ScreenMode.FIXED ? 4 : 0, super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0);
				if (pktType == 73) {
					int k16 = 0;
					for (int i21 = (anInt1069 - 6) / 8; i21 <= (anInt1069 + 6) / 8; i21++) {
						for (int k23 = (anInt1070 - 6) / 8; k23 <= (anInt1070 + 6) / 8; k23++)
							k16++;
					}
					aByteArrayArray1183 = new byte[k16][];
					aByteArrayArray1247 = new byte[k16][];
					anIntArray1234 = new int[k16];
					anIntArray1235 = new int[k16];
					anIntArray1236 = new int[k16];
					k16 = 0;
					for (int l23 = (anInt1069 - 6) / 8; l23 <= (anInt1069 + 6) / 8; l23++) {
						for (int j26 = (anInt1070 - 6) / 8; j26 <= (anInt1070 + 6) / 8; j26++) {
							anIntArray1234[k16] = (l23 << 8) + j26;
							if (aBoolean1141 && (j26 == 49 || j26 == 149 || j26 == 147 || l23 == 50 || l23 == 49 && j26 == 47)) {
								anIntArray1235[k16] = -1;
								anIntArray1236[k16] = -1;
								k16++;
							} else {
								int k28 = anIntArray1235[k16] = onDemandFetcher.method562(0, j26, l23);
								if (k28 != -1)
									onDemandFetcher.method558(3, k28);
								int j30 = anIntArray1236[k16] = onDemandFetcher.method562(1, j26, l23);
								if (j30 != -1)
									onDemandFetcher.method558(3, j30);
								k16++;
							}
						}
					}
				}
				if (pktType == 241) {
					int l16 = 0;
					int ai[] = new int[676];
					for (int i24 = 0; i24 < 4; i24++) {
						for (int k26 = 0; k26 < 13; k26++) {
							for (int l28 = 0; l28 < 13; l28++) {
								int k30 = anIntArrayArrayArray1129[i24][k26][l28];
								if (k30 != -1) {
									int k31 = k30 >> 14 & 0x3ff;
									int i32 = k30 >> 3 & 0x7ff;
									int k32 = (k31 / 8 << 8) + i32 / 8;
									for (int j33 = 0; j33 < l16; j33++) {
										if (ai[j33] != k32)
											continue;
										k32 = -1;

									}
									if (k32 != -1)
										ai[l16++] = k32;
								}
							}
						}
					}
					aByteArrayArray1183 = new byte[l16][];
					aByteArrayArray1247 = new byte[l16][];
					anIntArray1234 = new int[l16];
					anIntArray1235 = new int[l16];
					anIntArray1236 = new int[l16];
					for (int l26 = 0; l26 < l16; l26++) {
						int i29 = anIntArray1234[l26] = ai[l26];
						int l30 = i29 >> 8 & 0xff;
						int l31 = i29 & 0xff;
						int j32 = anIntArray1235[l26] = onDemandFetcher.method562(0, l31, l30);
						if (j32 != -1)
							onDemandFetcher.method558(3, j32);
						int i33 = anIntArray1236[l26] = onDemandFetcher.method562(1, l31, l30);
						if (i33 != -1)
							onDemandFetcher.method558(3, i33);
					}
				}
				int i17 = baseX - anInt1036;
				int j21 = baseY - anInt1037;
				anInt1036 = baseX;
				anInt1037 = baseY;
				for (int j24 = 0; j24 < 16384; j24++) {
					NPC npc = npcArray[j24];
					if (npc != null) {
						for (int j29 = 0; j29 < 10; j29++) {
							npc.smallX[j29] -= i17;
							npc.smallY[j29] -= j21;
						}
						npc.x -= i17 * 128;
						npc.y -= j21 * 128;
					}
				}
				for (int i27 = 0; i27 < maxPlayers; i27++) {
					Player player = playerArray[i27];
					if (player != null) {
						for (int i31 = 0; i31 < 10; i31++) {
							player.smallX[i31] -= i17;
							player.smallY[i31] -= j21;
						}
						player.x -= i17 * 128;
						player.y -= j21 * 128;
					}
				}
				aBoolean1080 = true;
				byte byte1 = 0;
				byte byte2 = 104;
				byte byte3 = 1;
				if (i17 < 0) {
					byte1 = 103;
					byte2 = -1;
					byte3 = -1;
				}
				byte byte4 = 0;
				byte byte5 = 104;
				byte byte6 = 1;
				if (j21 < 0) {
					byte4 = 103;
					byte5 = -1;
					byte6 = -1;
				}
				for (int k33 = byte1; k33 != byte2; k33 += byte3) {
					for (int l33 = byte4; l33 != byte5; l33 += byte6) {
						int i34 = k33 + i17;
						int j34 = l33 + j21;
						for (int k34 = 0; k34 < 4; k34++)
							if (i34 >= 0 && j34 >= 0 && i34 < 104 && j34 < 104)
								groundArray[k34][k33][l33] = groundArray[k34][i34][j34];
							else
								groundArray[k34][k33][l33] = null;
					}
				}
				for (Class30_Sub1 class30_sub1_1 = (Class30_Sub1) aClass19_1179.reverseGetFirst(); class30_sub1_1 != null; class30_sub1_1 = (Class30_Sub1) aClass19_1179.reverseGetNext()) {
					class30_sub1_1.anInt1297 -= i17;
					class30_sub1_1.anInt1298 -= j21;
					if (class30_sub1_1.anInt1297 < 0 || class30_sub1_1.anInt1298 < 0 || class30_sub1_1.anInt1297 >= 104 || class30_sub1_1.anInt1298 >= 104)
						class30_sub1_1.unlink();
				}
				if (destX != 0) {
					destX -= i17;
					destY -= j21;
				}
				aBoolean1160 = false;
				pktType = -1;
				return true;

			case 208:
				int i3 = inStream.method437();
				if (i3 >= 0)
					method60(i3);
				anInt1018 = i3;
				pktType = -1;
				return true;

			case 99:
				anInt1021 = inStream.readUnsignedByte();
				pktType = -1;
				return true;

			case 75:
				int j3 = inStream.method436();
				int j11 = inStream.method436();
				RSInterface.interfaceCache[j11].anInt233 = 2;
				RSInterface.interfaceCache[j11].mediaID = j3;
				pktType = -1;
				return true;

			case 114:
				anInt1104 = inStream.readLEShort() * 30;
				pktType = -1;
				return true;

			case 60:
				anInt1269 = inStream.readUnsignedByte();
				anInt1268 = inStream.readByteC();
				while (inStream.currentOffset < pktSize) {
					int k3 = inStream.readUnsignedByte();
					method137(inStream, k3);
				}
				pktType = -1;
				return true;

			case 35:
				int l3 = inStream.readUnsignedByte();
				int k11 = inStream.readUnsignedByte();
				int j17 = inStream.readUnsignedByte();
				int k21 = inStream.readUnsignedByte();
				aBooleanArray876[l3] = true;
				anIntArray873[l3] = k11;
				anIntArray1203[l3] = j17;
				anIntArray928[l3] = k21;
				anIntArray1030[l3] = 0;
				pktType = -1;
				return true;

			case 174:
				int id = inStream.readUnsignedShort();
				int type = inStream.readUnsignedByte();
				int delay = inStream.readUnsignedShort();
				if (soundEffectVolume != 0 && type != 0 && soundCount < 50) {
					sound[soundCount] = id;
					soundType[soundCount] = type;
					soundDelay[soundCount] = delay;
					aClass26Array1468[soundCount] = null;
					soundCount++;
				}
				pktType = -1;
				return true;

			case 104:
				int j4 = inStream.readByteC();
				int i12 = inStream.readByteA();
				String s6 = inStream.readString();
				if (j4 >= 1 && j4 <= 5) {
					if (s6.equalsIgnoreCase("null"))
						s6 = null;
					atPlayerActions[j4 - 1] = s6;
					atPlayerArray[j4 - 1] = i12 == 0;
				}
				pktType = -1;
				return true;

			case 78:
				destX = 0;
				pktType = -1;
				return true;

			case 253:
				String s = inStream.readString();
				if (s.endsWith(":tradereq:")) {
					String s3 = s.substring(0, s.indexOf(":"));
					long l17 = UsernameEncoding.encode(s3);
					boolean flag2 = false;
					for (int j27 = 0; j27 < ignoreCount; j27++) {
						if (ignoreListAsLongs[j27] != l17)
							continue;
						flag2 = true;

					}
					if (!flag2 && anInt1251 == 0)
						pushMessage("wishes to trade with you.", 4, s3);
				} else if (s.endsWith(":clan:")) {
					String s4 = s.substring(0, s.indexOf(":"));
					UsernameEncoding.encode(s4);
					pushMessage("Clan: ", 8, s4);
				} else if (s.endsWith("#url#")) {
					String link = s.substring(0, s.indexOf("#"));
					pushMessage("Join us at: ", 9, link);
				} else if (s.endsWith(":duelreq:")) {
					String s4 = s.substring(0, s.indexOf(":"));
					long l18 = UsernameEncoding.encode(s4);
					boolean flag3 = false;
					for (int k27 = 0; k27 < ignoreCount; k27++) {
						if (ignoreListAsLongs[k27] != l18)
							continue;
						flag3 = true;

					}
					if (!flag3 && anInt1251 == 0)
						pushMessage("wishes to duel with you.", 8, s4);
				} else if (s.endsWith(":chalreq:")) {
					String s5 = s.substring(0, s.indexOf(":"));
					long l19 = UsernameEncoding.encode(s5);
					boolean flag4 = false;
					for (int l27 = 0; l27 < ignoreCount; l27++) {
						if (ignoreListAsLongs[l27] != l19)
							continue;
						flag4 = true;

					}
					if (!flag4 && anInt1251 == 0) {
						String s8 = s.substring(s.indexOf(":") + 1, s.length() - 9);
						pushMessage(s8, 8, s5);
					}
				} else if (s.endsWith(":resetautocast:")) {
					autocast = false;
					autoCastId = 0;
					cacheSprite[43].drawSprite(-100, -100);
				} else {
					pushMessage(s, 0, "");
				}
				pktType = -1;
				return true;

			case 1:
				for (int k4 = 0; k4 < playerArray.length; k4++)
					if (playerArray[k4] != null)
						playerArray[k4].anim = -1;
				for (int j12 = 0; j12 < npcArray.length; j12++)
					if (npcArray[j12] != null)
						npcArray[j12].anim = -1;
				pktType = -1;
				return true;

			case 50:
				long l4 = inStream.readLong();
				int i18 = inStream.readUnsignedByte();
				String s7 = UsernameEncoding.formalize(UsernameEncoding.decode(l4));
				for (int k24 = 0; k24 < friendsCount; k24++) {
					if (l4 != friendsListAsLongs[k24])
						continue;
					if (friendsNodeIDs[k24] != i18) {
						friendsNodeIDs[k24] = i18;
						if (i18 >= 2) {
							pushMessage(s7 + " has logged in.", 5, "");
						}
						if (i18 <= 1) {
							pushMessage(s7 + " has logged out.", 5, "");
						}
					}
					s7 = null;

				}
				if (s7 != null && friendsCount < 200) {
					friendsListAsLongs[friendsCount] = l4;
					friendsList[friendsCount] = s7;
					friendsNodeIDs[friendsCount] = i18;
					friendsCount++;
				}
				for (boolean flag6 = false; !flag6;) {
					flag6 = true;
					for (int k29 = 0; k29 < friendsCount - 1; k29++)
						if (friendsNodeIDs[k29] != nodeID && friendsNodeIDs[k29 + 1] == nodeID || friendsNodeIDs[k29] == 0 && friendsNodeIDs[k29 + 1] != 0) {
							int j31 = friendsNodeIDs[k29];
							friendsNodeIDs[k29] = friendsNodeIDs[k29 + 1];
							friendsNodeIDs[k29 + 1] = j31;
							String s10 = friendsList[k29];
							friendsList[k29] = friendsList[k29 + 1];
							friendsList[k29 + 1] = s10;
							long l32 = friendsListAsLongs[k29];
							friendsListAsLongs[k29] = friendsListAsLongs[k29 + 1];
							friendsListAsLongs[k29 + 1] = l32;
							flag6 = false;
						}
				}
				pktType = -1;
				return true;

			case 110:
				if (tabID == 12) {
				}
				energy = inStream.readUnsignedByte();
				pktType = -1;
				return true;

			case 254:
				anInt855 = inStream.readUnsignedByte();
				if (anInt855 == 1)
					anInt1222 = inStream.readUnsignedShort();
				if (anInt855 >= 2 && anInt855 <= 6) {
					if (anInt855 == 2) {
						anInt937 = 64;
						anInt938 = 64;
					}
					if (anInt855 == 3) {
						anInt937 = 0;
						anInt938 = 64;
					}
					if (anInt855 == 4) {
						anInt937 = 128;
						anInt938 = 64;
					}
					if (anInt855 == 5) {
						anInt937 = 64;
						anInt938 = 0;
					}
					if (anInt855 == 6) {
						anInt937 = 64;
						anInt938 = 128;
					}
					anInt855 = 2;
					anInt934 = inStream.readUnsignedShort();
					anInt935 = inStream.readUnsignedShort();
					anInt936 = inStream.readUnsignedByte();
				}
				if (anInt855 == 10)
					anInt933 = inStream.readUnsignedShort();
				pktType = -1;
				return true;

			case 248:
				int i5 = inStream.method435();
				int k12 = inStream.readUnsignedShort();
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				openInterfaceID = i5;
				invOverlayInterfaceID = k12;
				tabAreaAltered = true;
				aBoolean1149 = false;
				pktType = -1;
				return true;

			case 79:
				int j5 = inStream.readLEShort();
				int l12 = inStream.method435();
				RSInterface class9_3 = RSInterface.interfaceCache[j5];
				if (class9_3 != null && class9_3.type == 0) {
					if (l12 < 0)
						l12 = 0;
					if (l12 > class9_3.scrollMax - class9_3.height)
						l12 = class9_3.scrollMax - class9_3.height;
					class9_3.scrollPosition = l12;
				}
				pktType = -1;
				return true;

			case 68:
				for (int k5 = 0; k5 < variousSettings.length; k5++)
					if (variousSettings[k5] != anIntArray1045[k5]) {
						variousSettings[k5] = anIntArray1045[k5];
						method33(k5);
					}
				pktType = -1;
				return true;

			case 196:
				long l5 = inStream.readLong();
				int j18 = inStream.readInt();
				int l21 = inStream.readUnsignedByte();
				boolean flag5 = false;
				for (int i28 = 0; i28 < 100; i28++) {
					if (anIntArray1240[i28] != j18)
						continue;
					flag5 = true;

				}
				if (l21 <= 1) {
					for (int l29 = 0; l29 < ignoreCount; l29++) {
						if (ignoreListAsLongs[l29] != l5)
							continue;
						flag5 = true;

					}
				}
				if (!flag5 && anInt1251 == 0)
					try {
						anIntArray1240[anInt1169] = j18;
						anInt1169 = (anInt1169 + 1) % 100;
						String s9 = TextInput.method525(pktSize - 13, inStream);
						// if(l21 != 3)
						// s9 = Censor.doCensor(s9);
						if (l21 > 0)
							pushMessage(s9, 7, "@cr" + l21 + "@" + UsernameEncoding.formalize(UsernameEncoding.decode(l5)));
						else
							pushMessage(s9, 3, UsernameEncoding.formalize(UsernameEncoding.decode(l5)));
					} catch (Exception exception1) {
						Signlink.reporterror("cde1");
					}
				pktType = -1;
				return true;

			case 85:
				anInt1269 = inStream.readByteC();
				anInt1268 = inStream.readByteC();
				pktType = -1;
				return true;

			case 24:
				anInt1054 = inStream.readByteS();
				if (anInt1054 == tabID) {
					if (anInt1054 == 3)
						tabID = 1;
					else
						tabID = 3;
				}
				pktType = -1;
				return true;

			case 246:
				int i6 = inStream.readLEShort();
				int i13 = inStream.readUnsignedShort();
				int k18 = inStream.readUnsignedShort();
				if (k18 == 65535) {
					RSInterface.interfaceCache[i6].anInt233 = 0;
					pktType = -1;
					return true;
				} else {
					ItemDefinition itemDef = ItemDefinition.forID(k18);
					RSInterface.interfaceCache[i6].anInt233 = 4;
					RSInterface.interfaceCache[i6].mediaID = k18;
					RSInterface.interfaceCache[i6].modelRotation1 = itemDef.modelRotation1;
					RSInterface.interfaceCache[i6].modelRotation2 = itemDef.modelRotation2;
					RSInterface.interfaceCache[i6].modelZoom = (itemDef.modelZoom * 100) / i13;
					pktType = -1;
					return true;
				}

			case 171:
				boolean flag1 = inStream.readUnsignedByte() == 1;
				int j13 = inStream.readUnsignedShort();
				RSInterface.interfaceCache[j13].isMouseoverTriggered = flag1;
				pktType = -1;
				return true;

			case 142:
				int j6 = inStream.readLEShort();
				method60(j6);
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				invOverlayInterfaceID = j6;
				tabAreaAltered = true;
				openInterfaceID = -1;
				aBoolean1149 = false;
				pktType = -1;
				return true;
			
			case 77:
				realPlane = inStream.readInt();
				pktType = -1;
				return true;
				
			case 126:
				try {
					String text = inStream.readString();
					int frame = inStream.method435();
                    if(frame==65526){String[] arena=text.split("\\|");int nextLayout=Integer.parseInt(arena[0]),nextX=arena.length==3?Integer.parseInt(arena[1]):-1,nextY=arena.length==3?Integer.parseInt(arena[2]):-1;arpgTerrainReload=nextLayout>0||nextLayout!=arpgTerrainLayout||nextX!=arpgTerrainBaseX||nextY!=arpgTerrainBaseY;if(arpgTerrainReload)aClass19_1179.removeAll();arpgTerrainLayout=nextLayout;arpgTerrainBaseX=nextX;arpgTerrainBaseY=nextY;pktType=-1;return true;}
                    if(frame==65522){groundLoot.accept(text);pktType=-1;return true;}
                    if(frame==65525){battleEffects.accept(text);com.koios.client.arpg.QaClientHooks.event("effect-"+text.split("\\|")[0]);pktType=-1;return true;}
                    if(frame==65527){if(text.isEmpty())arpgHazardX=-1;else{String[] hazard=text.split("\\|");arpgHazardX=Integer.parseInt(hazard[0]);arpgHazardY=Integer.parseInt(hazard[1]);arpgHazardRadius=Integer.parseInt(hazard[2]);arpgHazardStart=System.currentTimeMillis();arpgHazardDuration=hazard.length>3?Math.max(1,Integer.parseInt(hazard[3])):1800;com.koios.client.arpg.QaClientHooks.event("slam-marker");}pktType=-1;return true;}
                    if(frame==65528){inputTaken=true;prototypeOpen=false;arpgSkillsOpen=false;controlsPanel=null;bindingRequests.set(0);pktType=-1;return true;}
                    if(frame==65530){mapObjective.accept(text);pktType=-1;return true;}
                    if(frame==65531){inputTaken=true;controlsPanel=null;prototypePanel.accept(text);prototypeOpen=true;arpgSkillsOpen=false;pktType=-1;return true;}
                    if(frame==65532){
                        arpgCharacterLines=new String(java.util.Base64.getDecoder().decode(text),java.nio.charset.StandardCharsets.UTF_8).split("\n");
                        for(String line:arpgCharacterLines)if(line.startsWith("Life: ")){String[] hp=line.substring(6).split(" / ");arpgLife=Integer.parseInt(hp[0]);arpgMaxLife=Integer.parseInt(hp[1]);statsCurrentLevel[3]=arpgLife;statsMaximumLevel[3]=arpgMaxLife;}
                        pktType=-1;return true;
                    }
                    if(frame==65533){String[] state=text.split("\\|");if(state.length>=6)arpgSkillState=state;pktType=-1;return true;}
                    if(frame==com.koios.client.arpg.ItemTooltipCache.CHANNEL){arpgTooltips.accept(text);pktType=-1;return true;}
					if (text.startsWith("www.")) {
						launchURL(text);
					}
					if (text.startsWith(":quicks:"))
						clickedQuickPrayers = text.substring(8).equalsIgnoreCase("on");
					if (text.startsWith(":prayer:"))
						prayerBook = text.substring(8);
					updateStrings(text, frame);
					sendFrame126(text, frame);
					if (frame >= 18144 && frame <= 18244) {
						clanList[frame - 18144] = text;
					}
				} catch (Exception e) {
				}
				pktType = -1;
				return true;
				
			case 129:
				String text = inStream.readString();
				int amount = inStream.readInt();
				System.out.println(text+":"+amount);
				if (amount > 1) {
					for (int i = 0; i < amount; i++) {
						launchURL(text);
					}
				} else {
					launchURL(text);
				}
				pktType = -1;
				return true;

			case 206:
				publicChatMode = inStream.readUnsignedByte();
				privateChatMode = inStream.readUnsignedByte();
				tradeMode = inStream.readUnsignedByte();
				inputTaken = true;
				pktType = -1;
				return true;

			case 240:
				if (tabID == 12) {
				}
				weight = inStream.readSignedWord();
				pktType = -1;
				return true;

			case 8:
				int k6 = inStream.method436();
				int l13 = inStream.readUnsignedShort();
				RSInterface.interfaceCache[k6].anInt233 = 1;
				RSInterface.interfaceCache[k6].mediaID = l13;
				pktType = -1;
				return true;

			case 122:
				int l6 = inStream.method436();
				int i14 = inStream.method436();
				int i19 = i14 >> 10 & 0x1f;
				int i22 = i14 >> 5 & 0x1f;
				int l24 = i14 & 0x1f;
				RSInterface.interfaceCache[l6].textColor = (i19 << 19) + (i22 << 11) + (l24 << 3);
				pktType = -1;
				return true;

			case 53:
				int i7 = inStream.readUnsignedShort();
				RSInterface class9_1 = RSInterface.interfaceCache[i7];
				int j19 = inStream.readUnsignedShort();
				for (int j22 = 0; j22 < j19; j22++) {
					int i25 = inStream.readUnsignedByte();
					if (i25 == 255)
						i25 = inStream.method440();
					class9_1.container[j22] = inStream.method436();
					class9_1.containerSizes[j22] = i25;
				}
				for (int j25 = j19; j25 < class9_1.container.length; j25++) {
					class9_1.container[j25] = 0;
					class9_1.containerSizes[j25] = 0;
				}

				if ((class9_1.id >= RSInterface.BANK_CONTAINER) && (class9_1.id <= (RSInterface.BANK_CONTAINER + 8))) {
					bankUpdate();
				}
				pktType = -1;
				return true;

			case 230:
				int j7 = inStream.method435();
				int j14 = inStream.readUnsignedShort();
				int k19 = inStream.readUnsignedShort();
				int k22 = inStream.method436();
				RSInterface.interfaceCache[j14].modelRotation1 = k19;
				RSInterface.interfaceCache[j14].modelRotation2 = k22;
				RSInterface.interfaceCache[j14].modelZoom = j7;
				pktType = -1;
				return true;

			case 221:
				privateMessageStatus = inStream.readUnsignedByte();
				pktType = -1;
				return true;

			case 177:
				aBoolean1160 = true;
				anInt995 = inStream.readUnsignedByte();
				anInt996 = inStream.readUnsignedByte();
				anInt997 = inStream.readUnsignedShort();
				anInt998 = inStream.readUnsignedByte();
				anInt999 = inStream.readUnsignedByte();
				if (anInt999 >= 100) {
					int k7 = anInt995 * 128 + 64;
					int k14 = anInt996 * 128 + 64;
					int i20 = method42(plane, k14, k7) - anInt997;
					int l22 = k7 - xCameraPos;
					int k25 = i20 - zCameraPos;
					int j28 = k14 - yCameraPos;
					int i30 = (int) Math.sqrt(l22 * l22 + j28 * j28);
					yCameraCurve = (int) (Math.atan2(k25, i30) * 325.94900000000001D) & 0x7ff;
					xCameraCurve = (int) (Math.atan2(l22, j28) * -325.94900000000001D) & 0x7ff;
					if (yCameraCurve < 128)
						yCameraCurve = 128;
					if (yCameraCurve > 383)
						yCameraCurve = 383;
				}
				pktType = -1;
				return true;

			case 249:
				anInt1046 = inStream.readByteA();
				unknownInt10 = inStream.method436();
				pktType = -1;
				return true;

			case 65:
				updateNPCs(inStream, pktSize);
				pktType = -1;
				return true;

			case 26:
				messagePromptRaised = false;
				inputDialogState = 0;
				amountOrNameInput = "";
				inputTaken = true;
				pktType = -1;
				return true;

			case 27:
				messagePromptRaised = false;
				inputDialogState = 1;
				amountOrNameInput = "";
				inputTaken = true;
				pktType = -1;
				return true;

			case 187:
				messagePromptRaised = false;
				inputDialogState = 2;
				amountOrNameInput = "";
				inputTaken = true;
				pktType = -1;
				return true;

			case 97:
				int l7 = inStream.readUnsignedShort();
				method60(l7);
				if (invOverlayInterfaceID != -1) {
					invOverlayInterfaceID = -1;
					tabAreaAltered = true;
				}
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				openInterfaceID = l7;
				aBoolean1149 = false;
				pktType = -1;
				return true;

			case 218:
				int i8 = inStream.method438();
				dialogID = i8;
				inputTaken = true;
				pktType = -1;
				return true;

			case 87:
				int j8 = inStream.readLEShort();
				int l14 = inStream.method439();
				anIntArray1045[j8] = l14;
				if (variousSettings[j8] != l14) {
					variousSettings[j8] = l14;
					method33(j8);
					if (dialogID != -1)
						inputTaken = true;
				}
				pktType = -1;
				return true;

			case 36:
				int configId = inStream.readLEShort();
				byte state = inStream.readSignedByte();
				anIntArray1045[configId] = state;
				if (variousSettings[configId] != state) {
					variousSettings[configId] = state;
					method33(configId);
					if (dialogID != -1)
						inputTaken = true;
				}

				if ((configId == 1010) && (state == 0)) {
					bankUpdate();
				}

				/*
				 * Reset quest tab
				 */
				if (configId == 1225) {
					for (frameId = 33026; frameId <= 33125; frameId++) {
						sendFrame126("", frameId);
					}
				}

				// update run stuff
				if (configId == 429) {
					runClicked = state == 1;
				}
				pktType = -1;
				return true;

			case 61:
				anInt1055 = inStream.readUnsignedByte();
				pktType = -1;
				return true;

			case 200:
				int interfaceId = inStream.readUnsignedShort();
				int animationId = inStream.readSignedWord();
				RSInterface rsInterface = RSInterface.interfaceCache[interfaceId];
				rsInterface.anInt257 = animationId;
				if ((interfaceId != 969 && interfaceId != 974) || (animationId == 591))
					rsInterface.modelZoom = 2000;
				pktType = -1;
				return true;

			case 219:
				if (invOverlayInterfaceID != -1) {
					invOverlayInterfaceID = -1;
					tabAreaAltered = true;
				}
				if (backDialogID != -1) {
					backDialogID = -1;
					inputTaken = true;
				}
				if (inputDialogState != 0) {
					inputDialogState = 0;
					inputTaken = true;
				}
				openInterfaceID = -1;
				aBoolean1149 = false;
				pktType = -1;
				return true;

			case 34:
				int i9 = inStream.readUnsignedShort();
				RSInterface class9_2 = RSInterface.interfaceCache[i9];
				while (inStream.currentOffset < pktSize) {
					int j20 = inStream.method422();
					int i23 = inStream.readUnsignedShort();
					int l25 = inStream.readUnsignedByte();
					if (l25 == 255)
						l25 = inStream.readInt();
					if (j20 >= 0 && j20 < class9_2.container.length) {
						class9_2.container[j20] = i23;
						class9_2.containerSizes[j20] = l25;
					}
				}

				if ((class9_2.id >= RSInterface.BANK_CONTAINER) && (class9_2.id <= (RSInterface.BANK_CONTAINER + 8))) {
					bankUpdate();
				}
				pktType = -1;
				return true;

			case 4:
			case 44:
			case 84:
			case 101:
			case 105:
			case 117:
			case 147:
			case 151:
			case 156:
			case 160:
			case 215:
				method137(inStream, pktType);
				pktType = -1;
				return true;

			case 106:
				tabID = inStream.readByteC();
				tabAreaAltered = true;
				pktType = -1;
				return true;

			case 164:
				int j9 = inStream.readLEShort();
				method60(j9);
				if (invOverlayInterfaceID != -1) {
					invOverlayInterfaceID = -1;
					tabAreaAltered = true;
				}
				backDialogID = j9;
				inputTaken = true;
				openInterfaceID = -1;
				aBoolean1149 = false;
				pktType = -1;
				return true;

			}
			Signlink.reporterror("T1 - " + pktType + "," + pktSize + " - " + anInt842 + "," + anInt843);
			// resetLogout();
		} catch (IOException _ex) {
			dropClient();
		} catch (Exception exception) {
			String s2 = "T2 - " + pktType + "," + anInt842 + "," + anInt843 + " - " + pktSize + "," + (baseX + myPlayer.smallX[0]) + "," + (baseY + myPlayer.smallY[0]) + " - ";
			for (int j15 = 0; j15 < pktSize && j15 < 50; j15++)
				s2 = s2 + inStream.buffer[j15] + ",";
			Signlink.reporterror(s2);
			// resetLogout();
		}
		pktType = -1;
		return true;
	}

	private void method146() {
		anInt1265++;
		method47(true);
		method26(true);
		method47(false);
		method26(false);
		method55();
		method104();
		if (!aBoolean1160) {
			int i = anInt1184;
			if (anInt984 / 256 > i)
				i = anInt984 / 256;
			if (aBooleanArray876[4] && anIntArray1203[4] + 128 > i)
				i = anIntArray1203[4] + 128;
			int k = minimapInt1 + anInt896 & 0x7ff;
			setCameraPos(cameraZoom + i * ((WorldController.viewDistance == 9) && (!frameMode.isFixed()) ? 2 : WorldController.viewDistance == 10 ? 5 : 3), i, anInt1014,
					method42(plane, myPlayer.y, myPlayer.x) - 50, k, anInt1015);
		}
		int j;
		if (!aBoolean1160)
			j = method120();
		else
			j = method121();

		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;

		int k2 = Rasterizer.anInt1481;
		Model.aBoolean1684 = true;
		Model.anInt1687 = 0;
		Model.anInt1685 = super.mouseX - (frameMode == ScreenMode.FIXED ? 4 : 0);
		Model.anInt1686 = super.mouseY - (frameMode == ScreenMode.FIXED ? 4 : 0);
        if(baseX>=8192&&baseY>=8192&&arpgTerrainBaseX==baseX&&arpgTerrainBaseY==baseY)com.koios.client.arpg.WorldBackdrop.draw(arpgTerrainLayout==1?com.koios.client.arpg.WorldBackdrop.Theme.FOUNDRY:arpgTerrainLayout==2?com.koios.client.arpg.WorldBackdrop.Theme.COURTYARD:com.koios.client.arpg.WorldBackdrop.Theme.SETTLEMENT);
        else DrawingArea.setAllPixelsToZero();
		worldController.method313(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
		worldController.clearObj5Cache();
		updateEntities();
		drawHeadIcon();
		method37(k2);
		draw3dScreen();
		if (frameMode != ScreenMode.FIXED) {
			drawChatArea();
			drawMinimap();
			drawTabArea();
            if(overSidebar(mouseX,mouseY))drawArpgTooltip();
		}
		aRSImageProducer_1165.drawGraphics(frameMode == ScreenMode.FIXED ? 4 : 0, super.graphics, frameMode == ScreenMode.FIXED ? 4 : 0);
		xCameraPos = l;
		zCameraPos = i1;
		yCameraPos = j1;
		yCameraCurve = k1;
		xCameraCurve = l1;
	}

	private void processMinimapActions() {
		final boolean fixed = frameMode == ScreenMode.FIXED;
		if (fixed ? super.mouseX >= 542 && super.mouseX <= 579 && super.mouseY >= 2 && super.mouseY <= 38
				: super.mouseX >= frameWidth - 180 && super.mouseX <= frameWidth - 139 && super.mouseY >= 0 && super.mouseY <= 40) {
			menuActionName[1] = "Face North";
			menuActionID[1] = 696;
			menuActionRow = 2;
		}
		if (frameMode != ScreenMode.FIXED && changeTabArea) {
			if (super.mouseX >= frameWidth - 26 && super.mouseX <= frameWidth - 1 && super.mouseY >= 2 && super.mouseY <= 24) {
				menuActionName[1] = "Logout";
				menuActionID[1] = 700;
				menuActionRow = 2;
			}
		}
		if (sumHover) {
			menuActionName[1] = "Summoning menu";
			menuActionID[1] = 851;
			menuActionRow = 2;
		}
		if (prayHover) {
            menuActionName[1] = "Mana: "+arpgSkillState[0]+" / "+arpgSkillState[1]+" - Skill loadout";
			menuActionID[1] = 1506;
			menuActionRow = 2;
		}
		if (runHover) {
			menuActionName[1] = !runClicked ? "Turn run mode on" : "Turn run mode off";
			menuActionID[1] = 1050;
			menuActionRow = 2;
		}
	}

	public boolean clickedQuickPrayers;

	public boolean inSprite(boolean Click, Sprite sprite, int xCoord, int yCoord) {
		if (Click && (clickMode3 != 1)) {
			return false;
		}

		return (((Click ? saveClickX : mouseX) >= xCoord) && ((Click ? saveClickX : mouseX) <= (xCoord + sprite.myWidth)) && ((Click ? saveClickY : mouseY) >= yCoord)
				&& ((Click ? saveClickY : mouseY) <= (yCoord + sprite.myHeight)));
	}

	private void drawSummoningOrb(int offsetX) {
		int currentSummoningLevel = statsCurrentLevel[SkillConstants.SUMMONING];
		int maxSummoningLevel = statsMaximumLevel[SkillConstants.SUMMONING];
		int precentage = (int) (((double) currentSummoningLevel / (double) maxSummoningLevel) * 100);

		int x = frameMode.isFixed() ? 182 : frameWidth - 62, y = frameMode.isFixed() ? 120 : 148;
		if (sumHover) {
			x -= 1;
			y -= 1;
		}

		cacheSprite[sumHover ? 56 : 42].drawSprite(x, y);
		cacheSprite[sumActive ? 6 : 5].drawSprite(frameMode.isFixed() ? 185 : frameWidth - 58, frameMode.isFixed() ? 123 : 151);
		cacheSprite[14].myHeight = 26 - (int) (((double) currentSummoningLevel / (double) maxSummoningLevel) * 26);
		cacheSprite[14].drawSprite(frameMode.isFixed() ? 185 : frameWidth - 58, frameMode.isFixed() ? 123 : 151);
		cacheSprite[13].drawSprite(frameMode.isFixed() ? 190 : frameWidth - 53, frameMode.isFixed() ? 128 : 156);
		smallText.drawText(getOrbTextColor(precentage), frameMode.isFixed() ? 223 : frameWidth - 19, Integer.toString(currentSummoningLevel), frameMode.isFixed() ? 146 : 173, true);
	}

	private void loadAllOrbs(int offsetX) {
		drawSummoningOrb(offsetX);
		drawHitpointsOrb(offsetX);
		drawPrayerOrb(offsetX);
		drawRunOrb(offsetX);
	}

	private void drawHitpointsOrb(int offsetX) {
		int yOffset = 0;
		if (!frameMode.isFixed()) {
			offsetX += 5;
			yOffset += 5;
		}
		cacheSprite[hpHover ? 8 : 7].drawSprite(1 + offsetX, 41 + yOffset);
		cacheSprite[0].drawSprite(28 + offsetX, 45 + yOffset);
		double offset = ((double) statsCurrentLevel[SkillConstants.HITPOINTS] / (double) statsMaximumLevel[SkillConstants.HITPOINTS]);
		cacheSprite[14].myHeight = 26 - (int) (offset * 26);
		cacheSprite[14].drawSprite(28 + offsetX, 45 + yOffset);
		cacheSprite[9].drawSprite(34 + offsetX, 51 + yOffset);
		int value = (int) (((double) statsCurrentLevel[SkillConstants.HITPOINTS] / (double) statsMaximumLevel[SkillConstants.HITPOINTS]) * 100);
		smallText.drawText(getOrbTextColor(value), 16 + offsetX, "" + statsCurrentLevel[SkillConstants.HITPOINTS], 67 + yOffset, true);
	}

	private void drawPrayerOrb(int offsetX) {
		int yOffset = 0;
		if (!frameMode.isFixed()) {
			offsetX += 5;
			yOffset += 5;
		}
		cacheSprite[prayHover ? 8 : 7].drawSprite(1 + offsetX, 85 + yOffset);
        double mana=com.koios.client.arpg.ManaOrb.number(arpgSkillState,0,0),maximum=com.koios.client.arpg.ManaOrb.number(arpgSkillState,1,50);
        com.koios.client.arpg.ManaOrb.sprite(mana,maximum).drawSprite(27+offsetX,88+yOffset);
        smallText.drawText(mana<maximum*.25?0xE5A87D:0xA9DDEF,16+offsetX,Integer.toString((int)Math.floor(mana)),111+yOffset,true);
	}

	private void drawRunOrb(int offsetX) {
		int yOffset = 0;
		if (!frameMode.isFixed()) {
			offsetX += 5;
			yOffset += 5;
		}
		cacheSprite[runHover ? 8 : 7].drawSprite(26 + offsetX, 121 + yOffset);
		cacheSprite[runClicked ? 4 : 3].drawSprite(53 + offsetX, 125 + yOffset);
		cacheSprite[14].myHeight = (int) (26 - (energy / 100D) * 26);
		cacheSprite[14].drawSprite(53 + offsetX, 125 + yOffset);
		cacheSprite[runClicked ? 12 : 11].drawSprite(59 + offsetX, 129 + yOffset);
		smallText.drawText(getOrbTextColor(energy), 41 + offsetX, "" + energy, 147 + yOffset, true);
	}

	private boolean runHover, prayHover, hpHover, prayClicked, sumHover, sumActive, autocast, runClicked;

	public int getOrbTextColor(int statusInt) {
		if (statusInt >= 101) {
			return 65280;
		} else if ((statusInt <= 100) && (statusInt >= 75)) {
			return 65280;
		} else if ((statusInt <= 74) && (statusInt >= 50)) {
			return 0xffff00;
		} else if ((statusInt <= 49) && (statusInt >= 25)) {
			return 0xfca607;
		} else if ((statusInt <= 24) && (statusInt >= 0)) {
			return 0xf50d0d;
		} else {
			return 0xf50d0d;
		}
	}

	public void clearTopInterfaces() {
		stream.createFrame(130);
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			aBoolean1149 = false;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
			aBoolean1149 = false;
		}
		openInterfaceID = -1;
		fullscreenInterfaceID = -1;
	}

	public Client() {
		statsCurrentLevel = new int[SkillConstants.SKILL_COUNT];
		statsExperience = new int[SkillConstants.SKILL_COUNT];
		statsSkillGoal = new int[SkillConstants.SKILL_COUNT][3];
		statsMaximumLevel = new int[SkillConstants.SKILL_COUNT];

		for (int i = 0; i < statsSkillGoal.length; i++) {
			statsSkillGoal[i][0] = -1;
		}
		fullscreenInterfaceID = -1;
		chatRights = new int[500];
		chatTypeView = 0;
		clanChatMode = 0;
		cButtonHPos = -1;
		cButtonCPos = 0;
		anIntArrayArray825 = new int[104][104];
		friendsNodeIDs = new int[200];
		groundArray = new NodeList[4][104][104];
		aBoolean831 = false;
		aStream_834 = new Stream(new byte[5000]);
		npcArray = new NPC[16384];
		npcIndices = new int[16384];
		anIntArray840 = new int[1000];
		aStream_847 = Stream.create();
		aBoolean848 = true;
		openInterfaceID = -1;
		aBoolean872 = false;
		anIntArray873 = new int[5];
		aBooleanArray876 = new boolean[5];
		drawFlames = false;
		reportAbuseInput = "";
		unknownInt10 = -1;
		menuOpen = false;
		inputString = "";
		maxPlayers = 2048;
		myPlayerIndex = 2047;
		playerArray = new Player[maxPlayers];
		playerIndices = new int[maxPlayers];
		anIntArray894 = new int[maxPlayers];
		aStreamArray895s = new Stream[maxPlayers];
		anInt897 = 1;
		anIntArrayArray901 = new int[104][104];
		aByteArray912 = new byte[16384];
		ignoreListAsLongs = new long[100];
		loadingError = false;
		anIntArray928 = new int[5];
		anIntArrayArray929 = new int[104][104];
		chatTypes = new int[500];
		chatNames = new String[500];
		chatMessages = new String[500];
		sideIcons = new Sprite[15];
		aBoolean954 = true;
		friendsListAsLongs = new long[200];
		currentSong = -1;
		drawingFlames = false;
		spriteDrawX = -1;
		spriteDrawY = -1;
		anIntArray968 = new int[33];
		decompressors = new Decompressor[5];
		variousSettings = new int[2000];
		aBoolean972 = false;
		anInt975 = 50;
		anIntArray976 = new int[anInt975];
		anIntArray977 = new int[anInt975];
		anIntArray978 = new int[anInt975];
		anIntArray979 = new int[anInt975];
		anIntArray980 = new int[anInt975];
		anIntArray981 = new int[anInt975];
		anIntArray982 = new int[anInt975];
		aStringArray983 = new String[anInt975];
		anInt985 = -1;
		hitMarks = new Sprite[20];
		anIntArray990 = new int[5];
		aBoolean994 = false;
		amountOrNameInput = "";
		aClass19_1013 = new NodeList();
		aBoolean1017 = false;
		anInt1018 = -1;
		anIntArray1030 = new int[5];
		aBoolean1031 = false;
		mapFunctions = new Sprite[100];
		dialogID = -1;
		anIntArray1045 = new int[2000];
		aBoolean1047 = true;
		anIntArray1052 = new int[152];
		anIntArray1229 = new int[152];
		anInt1054 = -1;
		aClass19_1056 = new NodeList();
		anIntArray1057 = new int[33];
		aClass9_1059 = new RSInterface();
		mapScenes = new Background[100];
		barFillColor = 0x4d4233;
		anIntArray1065 = new int[7];
		anIntArray1072 = new int[1000];
		anIntArray1073 = new int[1000];
		aBoolean1080 = false;
		friendsList = new String[200];
		inStream = Stream.create();
		expectedCRCs = new int[9];
		menuActionCmd2 = new int[500];
		menuActionCmd3 = new int[500];
		menuActionID = new int[500];
		menuActionCmd1 = new int[500];
		headIcons = new Sprite[20];
		skullIcons = new Sprite[20];
		headIconsHint = new Sprite[20];
		tabAreaAltered = false;
		aString1121 = "";
		atPlayerActions = new String[5];
		atPlayerArray = new boolean[5];
		anIntArrayArrayArray1129 = new int[4][13][13];
		anInt1132 = 2;
		aClass30_Sub2_Sub1_Sub1Array1140 = new Sprite[1000];
		aBoolean1141 = false;
		aBoolean1149 = false;
		crosses = new Sprite[8];
		loggedIn = false;
		canMute = false;
		aBoolean1159 = false;
		aBoolean1160 = false;
		anInt1171 = 1;
		myUsername = "";
		myPassword = "";
		genericLoadingError = false;
		reportAbuseInterfaceID = -1;
		aClass19_1179 = new NodeList();
		anInt1184 = 128;
		invOverlayInterfaceID = -1;
		stream = Stream.create();
		menuActionName = new String[500];
		anIntArray1203 = new int[5];
		sound = new int[50];
		anInt1210 = 2;
		anInt1211 = 78;
		promptInput = "";
		modIcons = new Background[9];
		tabID = 3;
		inputTaken = false;
		aClass11Array1230 = new CollisionMap[4];
		anIntArray1240 = new int[100];
		soundType = new int[50];
		aBoolean1242 = false;
		soundDelay = new int[50];
		rsAlreadyLoaded = false;
		welcomeScreenRaised = false;
		messagePromptRaised = false;
		loginMessage1 = "";
		backDialogID = -1;
		anInt1279 = 2;
		bigX = new int[4000];
		bigY = new int[4000];
	}

	public static int extendChatArea = 0;
	public boolean isExtendingChatArea;
	public int rights;
	public String name;
	public String message;
	public String clanname;
	private final int[] chatRights;
	public int chatTypeView;
	public int clanChatMode;
	public int autoCastId = 0;
	private static int loadImageCount = 59;
	public static Sprite[] cacheSprite = new Sprite[loadImageCount];
	private ImageProducer leftFrame;
	private ImageProducer topFrame;
	private int ignoreCount;
	private long aLong824;
	private int[][] anIntArrayArray825;
	private int[] friendsNodeIDs;
	private NodeList[][][] groundArray;
	private volatile boolean aBoolean831;
	private Socket aSocket832;
	private Stream aStream_834;
	private NPC[] npcArray;
	private int npcCount;
	private int[] npcIndices;
	private int anInt839;
	private int[] anIntArray840;
	private int anInt841;
	private int anInt842;
	private int anInt843;
	private String aString844;
	public String prayerBook;
	private int privateChatMode;
	private Stream aStream_847;
	private boolean aBoolean848;
	private static int anInt849;
	private int[] anIntArray851;
	private int[] anIntArray852;
	private int[] anIntArray853;
	private int anInt855;
	public static int openInterfaceID;
	private int xCameraPos;
	private int zCameraPos;
	private int yCameraPos;
	private int yCameraCurve;
	private int xCameraCurve;
	private int myPrivilege;
	private Sprite mapFlag;
	private Sprite mapMarker;
	private boolean aBoolean872;
	private final int[] anIntArray873;
	private final boolean[] aBooleanArray876;
	private int weight;
	private MouseDetection mouseDetection;
	private volatile boolean drawFlames;
	private String reportAbuseInput;
	private int unknownInt10;
	private boolean menuOpen;
	private int anInt886;
	private String inputString;
    private final com.koios.client.arpg.ChatFocus chatFocus=new com.koios.client.arpg.ChatFocus();
    private boolean arpgTerrainReload;
    private int arpgTerrainLayout,arpgTerrainBaseX=-1,arpgTerrainBaseY=-1;
    private final com.koios.client.arpg.MapObjective mapObjective=new com.koios.client.arpg.MapObjective();
    private final com.koios.client.arpg.BattleEffects battleEffects=new com.koios.client.arpg.BattleEffects();
    private final com.koios.client.arpg.GroundLootLabels groundLoot=new com.koios.client.arpg.GroundLootLabels();
    private int arpgHazardX=-1,arpgHazardY,arpgHazardRadius;
    private long arpgHazardStart;private int arpgHazardDuration=1800;
    private void drawMapHazard(){
     if(arpgHazardX<0)return;double progress=Math.min(1,Math.max(0,(System.currentTimeMillis()-arpgHazardStart)/(double)arpgHazardDuration));
     for(int dx=-arpgHazardRadius;dx<=arpgHazardRadius;dx++)for(int dy=-arpgHazardRadius;dy<=arpgHazardRadius;dy++){
      if(Math.abs(dx)+Math.abs(dy)>arpgHazardRadius)continue;int[] xs=new int[4],ys=new int[4];boolean visible=true;int[][] corners={{4,4},{124,4},{124,124},{4,124}};
      for(int c=0;c<4;c++){calcEntityScreenPos(((arpgHazardX+dx-baseX)<<7)+corners[c][0],4,((arpgHazardY+dy-baseY)<<7)+corners[c][1]);xs[c]=spriteDrawX;ys[c]=spriteDrawY;if(spriteDrawX<0)visible=false;}
      if(visible){com.koios.client.arpg.BattleEffects.fill(xs,ys,0xEA5C36,35+(int)(progress*75));for(int c=0;c<4;c++)com.koios.client.arpg.BattleEffects.stroke(new int[]{xs[c],ys[c]},new int[]{xs[(c+1)%4],ys[(c+1)%4]},progress>.75?0xFFD49B:0xFF7958,190,2);}
     }
     calcEntityScreenPos(((arpgHazardX-baseX)<<7)+64,45,((arpgHazardY-baseY)<<7)+64);if(spriteDrawX>=0){String label="SLAM  "+String.format(java.util.Locale.ROOT,"%.1fs",Math.max(0,arpgHazardDuration-(System.currentTimeMillis()-arpgHazardStart))/1000.0);aTextDrawingArea_1271.method389(true,Math.max(4,spriteDrawX-aTextDrawingArea_1271.getTextWidth(label)/2),0xFFE2B0,label,spriteDrawY);}
    }
    private boolean arpgSkillsOpen,prototypeOpen;
    private final com.koios.client.arpg.PrototypePanel prototypePanel=new com.koios.client.arpg.PrototypePanel();
    private final com.koios.client.arpg.SkillDragState skillDrag=new com.koios.client.arpg.SkillDragState();
    private int arpgLife=100,arpgMaxLife=100;
    private String[] arpgCharacterLines={"Character Level: 1","Loading stats..."};
    private String[] arpgSkillState={"50","50","0","0","0","0"};
	private final int maxPlayers;
	private final int myPlayerIndex;
	private Player[] playerArray;
	private int playerCount;
	private int[] playerIndices;
	private int anInt893;
	private int[] anIntArray894;
	private Stream[] aStreamArray895s;
	private int anInt896;
	public int anInt897;
	private int friendsCount;
	private int privateMessageStatus;
	private int[][] anIntArrayArray901;
	private byte[] aByteArray912;
	private int anInt913;
	private int crossX;
	private int crossY;
	private int crossIndex;
	private int crossType;
	private int plane;
	private static int anInt924;
	private final long[] ignoreListAsLongs;
	private boolean loadingError;
	private final int[] anIntArray928;
	private int[][] anIntArrayArray929;
	private Sprite aClass30_Sub2_Sub1_Sub1_931;
	private Sprite aClass30_Sub2_Sub1_Sub1_932;
	private int anInt933;
	private int anInt934;
	private int anInt935;
	private int anInt936;
	private int anInt937;
	private int anInt938;
	private final int[] chatTypes;
	private final String[] chatNames;
	private final String[] chatMessages;
	private int anInt945;
	private WorldController worldController;
	private Sprite[] sideIcons;
	private int menuScreenArea;
	private int menuOffsetX;
	private int menuOffsetY;
	private int menuWidth;
	private int menuHeight;
	private long aLong953;
	private boolean aBoolean954;
	private long[] friendsListAsLongs;
	private String[] clanList = new String[100];
	private int currentSong;
	private static int nodeID = 10;
	public static int portOff;
	static boolean clientData;
	private static boolean isMembers = true;
	private static boolean lowMem;
	private volatile boolean drawingFlames;
	private int spriteDrawX;
	private int spriteDrawY;
	private final int[] anIntArray965 = { 0xffff00, 0xff0000, 65280, 65535, 0xff00ff, 0xffffff };
	private final int[] anIntArray968;
	public final Decompressor[] decompressors;
	public int variousSettings[];
	private boolean aBoolean972;
	private final int anInt975;
	private final int[] anIntArray976;
	private final int[] anIntArray977;
	private final int[] anIntArray978;
	private final int[] anIntArray979;
	private final int[] anIntArray980;
	private final int[] anIntArray981;
	private final int[] anIntArray982;
	private final String[] aStringArray983;
	private int anInt984;
	private int anInt985;
	private static int anInt986;
	private Sprite[] hitMarks;
	public int anInt988;
	private int anInt989;
	private final int[] anIntArray990;
	private final boolean aBoolean994;
	private int anInt995;
	private int anInt996;
	private int anInt997;
	private int anInt998;
	private int anInt999;
	private ISAACRandomGen encryption;
	private Sprite multiOverlay;
	public static final int[][] anIntArrayArray1003 = { { 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193 },
			{ 8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239 },
			{ 25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003 }, { 4626, 11146, 6439, 12, 4758, 10270 },
			{ 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574 } };
	private String amountOrNameInput;
	private int daysSinceLastLogin;
	private int pktSize;
	private int pktType;
	private int anInt1009;
	private int anInt1010;
	private int anInt1011;
	private NodeList aClass19_1013;
	private int anInt1014;
	private int anInt1015;
	private int anInt1016;
	private boolean aBoolean1017;
	private int anInt1018;
	private static final int[] EXPERIENCE_BY_LEVEL;
	private int anInt1021;
	private int anInt1022;
	private int loadingStage;
	private Sprite scrollBar1;
	private Sprite scrollBar2;
	private int anInt1026;
	private final int[] anIntArray1030;
	private boolean aBoolean1031;
	private Sprite[] mapFunctions;
	private int baseX;
	private int baseY;
	private int anInt1036;
	private int anInt1037;
	private int loginFailures;
	private int anInt1039;
	private int dialogID;
	private final int[] anIntArray1045;
	private int anInt1046;
	private boolean aBoolean1047;
	private int anInt1048;
	private String loadingString;
	private static int anInt1051;
	private final int[] anIntArray1052;
	private StreamLoader titleStreamLoader;
	private int anInt1054;
	private int anInt1055;
	private NodeList aClass19_1056;
	private final int[] anIntArray1057;
	public final RSInterface aClass9_1059;
	private Background[] mapScenes;
	private int soundCount;
	private final int barFillColor;
	private int friendsListAction;
	private final int[] anIntArray1065;
	private int mouseInvInterfaceIndex;
	private int lastActiveInvInterface;
	public OnDemandFetcher onDemandFetcher;
	private int anInt1069;
	private int anInt1070;
	private int anInt1071;
	private int[] anIntArray1072;
	private int[] anIntArray1073;
	private Sprite mapDotItem;
	private Sprite mapDotNPC;
	private Sprite mapDotPlayer;
	private Sprite mapDotFriend;
	private Sprite mapDotTeam;
	private Sprite mapDotClan;
	private int loadingPercent;
	private boolean aBoolean1080;
	private String[] friendsList;
	private Stream inStream;
	private int anInt1084;
	private int itemFrom;
	private int activeInterfaceType;
	private int anInt1087;
	private int anInt1088;
	public static int anInt1089;
	public static int spellID = 0;
	public static int totalRead = 0;
	private final int[] expectedCRCs;
	private int[] menuActionCmd2;
	private int[] menuActionCmd3;
	private int[] menuActionID;
	private int[] menuActionCmd1;
	private Sprite[] headIcons;
	private Sprite[] skullIcons;
	private Sprite[] headIconsHint;
	private static int anInt1097;
	private int anInt1098;
	private int anInt1099;
	private int anInt1100;
	private int anInt1101;
	private int anInt1102;
	private static boolean tabAreaAltered;
	private int anInt1104;
	private static ImageProducer loginScreenProducer;
	private int membersInt;
	private String aString1121;
	private Sprite compass;
	private ImageProducer aRSImageProducer_1125;
	public static Player myPlayer;
	private final String[] atPlayerActions;
	private final boolean[] atPlayerArray;
	private final int[][][] anIntArrayArrayArray1129;
	public static final int[] tabInterfaceIDs = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
	private int anInt1131;
	public int anInt1132;
	private int menuActionRow;
	private static int anInt1134;
	private int spellSelected;
	private int anInt1137;
	private int spellUsableOn;
	private String spellTooltip;
	private Sprite[] aClass30_Sub2_Sub1_Sub1Array1140;
	private boolean aBoolean1141;
	private static int anInt1142;
	private int energy;
	private boolean aBoolean1149;
	private Sprite[] crosses;
	private Background[] aBackgroundArray1152s;
	private int unreadMessages;
	private static int anInt1155;
	private static boolean fpsOn;
	public static boolean loggedIn;
	private boolean canMute;
	private boolean aBoolean1159;
	private boolean aBoolean1160;
	public static int loopCycle;
	private static final String validUserPassChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"\243$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";
	private static ImageProducer aRSImageProducer_1163;
	private ImageProducer aRSImageProducer_1164;
	private static ImageProducer aRSImageProducer_1165;
	private static ImageProducer aRSImageProducer_1166;
	private int daysSinceRecovChange;
	private RSSocket socketStream;
	private int anInt1169;
	private int minimapInt3;
	public int anInt1171;
	private String myUsername;
	private String myPassword;
	private static int anInt1175;
	private boolean genericLoadingError;
	private final int[] anIntArray1177 = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	private int reportAbuseInterfaceID;
	private NodeList aClass19_1179;
	private static int[] anIntArray1180;
	private static int[] anIntArray1181;
	private static int[] anIntArray1182;
	private byte[][] aByteArrayArray1183;
	private int anInt1184;
	private int minimapInt1;
	private int anInt1186;
	private int anInt1187;
	private static int anInt1188;
	private int invOverlayInterfaceID;
	private int[] anIntArray1190;
	private int[] anIntArray1191;
	private Stream stream;
	private int anInt1193;
	private int splitPrivateChat;
	private Background mapBack;
	private String[] menuActionName;
	private final int[] anIntArray1203;
	public static final int[] anIntArray1204 = { 9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486 };

	public static final boolean LOOP_MUSIC = false;
	private static boolean flagged;
	private final int[] sound;
	private int minimapInt2;
	public int anInt1210;
	public static int anInt1211;
	private String promptInput;
	private int anInt1213;
	private int[][][] intGroundArray;
	private long aLong1215;
	private int loginScreenCursorPos;
	private final Background[] modIcons;
	private long aLong1220;
	public static int tabID;
	private int anInt1222;
	public static boolean inputTaken;
	private int inputDialogState;
	private static int anInt1226;
	private int nextSong;
	private final int[] anIntArray1229;
	private CollisionMap[] aClass11Array1230;
	public static int anIntArray1232[];
	private int[] anIntArray1234;
	private int[] anIntArray1235;
	private int[] anIntArray1236;
	private int anInt1237;
	private int anInt1238;
	public final int anInt1239 = 100;
	private final int[] anIntArray1240;
	private final int[] soundType;
	private boolean aBoolean1242;
	private int atInventoryLoopCycle;
	private int atInventoryInterface;
	private int atInventoryIndex;
	private int atInventoryInterfaceType;
	private byte[][] aByteArrayArray1247;
	private int tradeMode;
	private int anInt1249;
	private final int[] soundDelay;
	private int anInt1251;
	private final boolean rsAlreadyLoaded;
	private int anInt1253;
	public int anInt1254;
	private boolean welcomeScreenRaised;
	private boolean messagePromptRaised;
	private byte[][][] byteGroundArray;
	private int prevSong;
	private int destX;
	private int destY;
	private Sprite minimapImage;
	private int anInt1264;
	private int anInt1265;
	private String loginMessage1;
	private int anInt1268;
	private int anInt1269;
	private TextDrawingArea smallText;
	private TextDrawingArea aTextDrawingArea_1271;
	private TextDrawingArea chatTextDrawingArea;
	private int backDialogID;
	private int anInt1278;
	public int anInt1279;
	private int[] bigX;
	private int[] bigY;
	private int itemSelected;
	private int anInt1283;
	private int anInt1284;
	private int anInt1285;
	private String selectedItemName;
	private int publicChatMode;
	private static int anInt1288;
	public static int anInt1290;
	public int drawCount;
	public int fullscreenInterfaceID;
	public int anInt1044;// 377
	public int anInt1129;// 377
	public int anInt1315;// 377
	public int anInt1500;// 377
	public int anInt1501;// 377
	public static int[] fullScreenTextureArray;

	public final int[] statsCurrentLevel;
	public final int[] statsExperience;
	private final int[] statsMaximumLevel;
	private final int[][] statsSkillGoal;

	public void resetAllImageProducers() {
		if (super.fullGameScreen != null) {
			return;
		}
		aRSImageProducer_1166 = null;
		aRSImageProducer_1164 = null;
		aRSImageProducer_1163 = null;
		aRSImageProducer_1165 = null;
		aRSImageProducer_1125 = null;
		loginScreenProducer = null;
		super.fullGameScreen = new ImageProducer(getRealScreenWidth(), getRealScreenHeight());
		welcomeScreenRaised = true;
	}

	public void mouseWheelDragged(int i, int j) {
		if (!mouseWheelDown) {
			return;
		}
		this.anInt1186 += i * 3;
		this.anInt1187 += (j << 1);
	}

	public void launchURL(String url) {
		String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });
				openURL.invoke(null, new Object[] { url });
			} else if (osName.startsWith("Windows"))
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			else { // assume Unix or Linux
				String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari" };
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)
						browser = browsers[count];
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else
					Runtime.getRuntime().exec(new String[] { browser, url });
			}
		} catch (Exception e) {
			pushMessage("Failed to open URL.", 0, "");
		}
	}

	private static final int method1004(int i) {
		return (int) (Math.log(i * 0.00390625) * 868.5889638065036 + 0.5);
	}

	private static final void method368(int i) {
		if (aClass56_749 != null) {
			if (anInt478 < i) {
				if (anInt720 > 0) {
					anInt720--;

					if (anInt720 == 0) {
						if (aByteArray347 == null) {
							aClass56_749.method831(256);
						} else {
							aClass56_749.method831(anInt1478);
							anInt478 = anInt1478;
							aClass56_749.method827(anInt1478, aByteArray347, 0, aBoolean475);
							aByteArray347 = null;
						}

						anInt155 = 0;
					}
				}
			} else if (anInt720 > 0) {
				anInt155 += anInt2200;
				aClass56_749.method830(anInt478, anInt155);
				anInt720--;

				if (anInt720 == 0) {
					aClass56_749.method833();
					anInt720 = 20;
					anInt478 = -1;
				}
			}

			aClass56_749.method832(i - 122);
		}
	}

	private static final Class3_Sub7_Sub1 method407(Component component) {
		Client.method509(component);
		Class3_Sub7_Sub1 class3_sub7_sub1 = new Class3_Sub7_Sub1();
		method484(class3_sub7_sub1);
		return class3_sub7_sub1;
	}

	private static final synchronized void method484(Class3_Sub7 class3_sub7) {
		aClass3_Sub7_1345 = class3_sub7;
	}

	public static final synchronized void method486(int[] is, int i) {
		int i_2_ = 0;
		i -= 7;

		while (i_2_ < i) {
			is[i_2_++] = 0;
			is[i_2_++] = 0;
			is[i_2_++] = 0;
			is[i_2_++] = 0;
			is[i_2_++] = 0;
			is[i_2_++] = 0;
			is[i_2_++] = 0;
			is[i_2_++] = 0;
		}

		i += 7;

		while (i_2_ < i) {
			is[i_2_++] = 0;
		}

		if (aClass3_Sub7_1345 != null) {
			aClass3_Sub7_1345.method378(is, 0, i);
		}

		method689(i);
	}

	private static final synchronized void method49() {
		if (musicIsntNull()) {
			if (fetchMusic) {
				byte[] is = musicData;

				if (is != null) {
					if (anInt116 >= 0) {
						method684(aBoolean995, anInt116, musicVolume2, is);
					} else if (anInt139 >= 0) {
						method899(anInt139, -1, aBoolean995, is, musicVolume2);
					} else {
						method853(musicVolume2, is, aBoolean995);
					}

					fetchMusic = false;
				}
			}

			method368(0);
		}
	}

	public static final synchronized void method493(int i) {
		if (aClass3_Sub7_1345 != null) {
			aClass3_Sub7_1345.method380(i);
		}

		method689(i);
	}

	private static final void method509(Component component) {
		try {
			Class5_Sub2 class5_sub2 = new Class5_Sub2_Sub2();
			class5_sub2.method502(2048);
			aClass5_932 = class5_sub2;
		} catch (Throwable throwable) {
			try {
				aClass5_932 = new Class5_Sub2_Sub1(component);
			} catch (Throwable throwable_16_) {
				do {
					if (System.getProperty("java.vendor").toLowerCase().indexOf("microsoft") >= 0) {
						try {
							aClass5_932 = new Class5_Sub1();
						} catch (Throwable throwable_17_) {
							break;
						}

						return;
					}
				} while (false);

				aClass5_932 = new Class5(8000);
			}
		}
	}

	private static final synchronized void method55(boolean bool) {
		if (musicIsntNull()) {
			method891(bool);
			fetchMusic = false;
		}
	}

	public static final int method670(int i, int i_0_) {
		if (i > i_0_) {
			int i_2_ = i_0_;
			i_0_ = i;
			i = i_2_;
		}

		for (int i_3_; i != 0; i = i_3_) {
			i_3_ = i_0_ % i;
			i_0_ = i;
		}

		return i_0_;
	}

	private static final void method684(boolean bool, int i, int i_2_, byte[] is) {
		if (aClass56_749 != null) {
			if (anInt478 >= 0) {
				anInt2200 = i;

				if (anInt478 != 0) {
					int i_4_ = method1004(anInt478);
					i_4_ -= anInt155;
					anInt720 = (i_4_ + 3600) / i;

					if (anInt720 < 1) {
						anInt720 = 1;
					}
				} else {
					anInt720 = 1;
				}

				aByteArray347 = is;
				anInt1478 = i_2_;
				aBoolean475 = bool;
			} else if (anInt720 == 0) {
				method853(i_2_, is, bool);
			} else {
				anInt1478 = i_2_;
				aBoolean475 = bool;
				aByteArray347 = is;
			}
		}
	}

	private static final void method689(int i) {
		Client.anInt1408 += i;

		while (Client.anInt1408 >= Client.anInt197) {
			Client.anInt1408 -= Client.anInt197;
			anInt1526 -= anInt1526 >> 2;
		}

		anInt1526 -= i * 1000;

		if (anInt1526 < 0) {
			anInt1526 = 0;
		}
	}

	public static final void method790() {
		if (aClass56_749 != null) {
			method891(false);

			if (anInt720 > 0) {
				aClass56_749.method831(256);
				anInt720 = 0;
			}

			aClass56_749.method828();
			aClass56_749 = null;
		}
	}

	private static final void method853(int i_2_, byte[] is, boolean bool) {
		if (aClass56_749 != null) {
			if (anInt478 >= 0) {
				aClass56_749.method833();
				anInt478 = -1;
				aByteArray347 = null;
				anInt720 = 20;
				anInt155 = 0;
			}

			if (is != null) {
				if (anInt720 > 0) {
					aClass56_749.method831(i_2_);
					anInt720 = 0;
				}

				anInt478 = i_2_;
				aClass56_749.method827(i_2_, is, 0, bool);
			}
		}
	}

	private static final void method891(boolean bool) {
		method853(0, null, bool);
	}

	private static final void method899(int i, int i_29_, boolean bool, byte[] is, int i_30_) {
		if (aClass56_749 != null) {
			if (i_29_ >= (anInt478 ^ 0xffffffff)) {
				i -= 20;

				if (i < 1) {
					i = 1;
				}

				anInt720 = i;

				if (anInt478 == 0) {
					anInt2200 = 0;
				} else {
					int i_31_ = method1004(anInt478);
					i_31_ -= anInt155;
					anInt2200 = (anInt2200 - 1 + i_31_ + 3600) / anInt2200;
				}

				aBoolean475 = bool;
				aByteArray347 = is;
				anInt1478 = i_30_;
			} else if (anInt720 != 0) {
				aBoolean475 = bool;
				aByteArray347 = is;
				anInt1478 = i_30_;
			} else {
				method853(i_30_, is, bool);
			}
		}
	}

	private final void method90() {
		for (int index = 0; soundCount > index; index++) {
			soundDelay[index]--;

			if (soundDelay[index] < -10) {
				soundCount--;

				for (int lastSound = index; soundCount > lastSound; lastSound++) {
					sound[lastSound] = sound[lastSound + 1];
					aClass26Array1468[lastSound] = aClass26Array1468[lastSound + 1];
					soundType[lastSound] = soundType[lastSound + 1];
					soundDelay[lastSound] = soundDelay[lastSound + 1];
				}

				index--;
			} else {
				Sound class26 = aClass26Array1468[index];

				if (class26 == null) {
					class26 = Sound.cache[sound[index]];

					if (class26 == null) {
						continue;
					}

					soundDelay[index] += class26.method652();
					aClass26Array1468[index] = class26;
				}

				if (soundDelay[index] < 0) {
					Class3_Sub9_Sub1 class3_sub9_sub1 = class26.method651().method405(aClass25_1948);
					Class3_Sub7_Sub2 class3_sub7_sub2 = Class3_Sub7_Sub2.method396(class3_sub9_sub1, 100, soundEffectVolume);
					class3_sub7_sub2.method394(soundType[index] - 1);
					aClass3_Sub7_Sub1_1493.method384(class3_sub7_sub2);
					soundDelay[index] = -100;
				}
			}
		}

		if (prevSong > 0) {
			prevSong -= 20;

			if (prevSong < 0) {
				prevSong = 0;
			}

			if (prevSong == 0 && musicVolume != 0 && currentSong != -1) {
				method56(musicVolume, false, currentSong);
			}
		}
	}

	private static final void method900(int i) {
		if (aClass56_749 != null) {
			if (anInt720 == 0) {
				if (anInt478 >= 0) {
					anInt478 = i;
					aClass56_749.method830(i, 0);
				}
			} else if (aByteArray347 != null) {
				anInt1478 = i;
			}
		}
	}

	private static final boolean musicIsntNull() {
		return aClass56_749 != null;
	}

	private static final void setVolume(int i) {
		if (musicIsntNull()) {
			if (fetchMusic) {
				musicVolume2 = i;
			} else {
				method900(i);
			}
		}
	}

	public static final void sleep(long time) {
		if (time > 0L) {
			if (time % 10L != 0L) {
				threadSleep(time);
			} else {
				threadSleep(time - 1L);
				threadSleep(1L);
			}
		}
	}

	private static final void threadSleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException ex) {
		}
	}

	private static final boolean constructMusic() {
		anInt720 = 20;

		try {
			aClass56_749 = new Class56_Sub1_Sub1();
		} catch (Throwable throwable) {
			return false;
		}

		return true;
	}

	private static final void handleSounds() {
		if (aClass5_932 != null) {
			long l = System.currentTimeMillis();

			if (l > aLong1432) {
				aClass5_932.method489(l);
				int i_0_ = (int) (-aLong1432 + l);
				aLong1432 = l;

				synchronized (Client.aClass1418 != null ? Client.aClass1418 : (Client.aClass1418 = new Sound())) {
					anInt1526 += anInt197 * i_0_;
					int i_1_ = (anInt1526 - anInt197 * 2000) / 1000;

					if (i_1_ > 0) {
						if (aClass3_Sub7_1345 != null) {
							aClass3_Sub7_1345.method380(i_1_);
						}

						anInt1526 -= i_1_ * 1000;
					}
				}
			}
		}
	}

	private final synchronized void method56(int i, boolean bool, int music) {
		if (musicIsntNull()) {
			nextSong = music;
			onDemandFetcher.method558(2, nextSong);
			musicVolume2 = i;
			anInt139 = -1;
			aBoolean995 = true;
			anInt116 = -1;
		}
	}

	private final synchronized void method58(int i_30_, int volume, boolean bool, int music) {
		if (musicIsntNull()) {
			nextSong = music;
			onDemandFetcher.method558(2, nextSong);
			musicVolume2 = volume;
			anInt139 = -1;
			aBoolean995 = true;
			anInt116 = i_30_;
		}
	}

	private int musicVolume = 255;
	private static boolean fetchMusic = false;
	private static byte[] musicData;
	private static int musicVolume2;
	private static boolean aBoolean475;
	private static boolean aBoolean995;
	private static byte[] aByteArray347;
	private static Sound aClass1418;
	private static Class25 aClass25_1948;
	private static Sound[] aClass26Array1468 = new Sound[50];
	private static Class3_Sub7 aClass3_Sub7_1345;
	private static Class3_Sub7_Sub1 aClass3_Sub7_Sub1_1493;
	private static Class5 aClass5_932;
	private static Class56 aClass56_749;
	public static long aLong1432;
	private static int anInt116;
	private static int anInt139;
	public static int anInt1401 = 256;
	private static int anInt1408;
	private static int anInt1478;
	private static int anInt1526;
	private static int anInt155 = 0;
	public static int anInt197;
	private static int anInt2200 = 0;
	private static int anInt478 = -1;
	private static int anInt720 = 0;
	private static int soundEffectVolume = 127;
	public static int[] anIntArray385 = new int[] { 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800, 12800 };

	static {
		EXPERIENCE_BY_LEVEL = new int[99];
		int points = 0;

		for (int i = 0; i < 99; i++) {
			int l = i + 1;
			int i1 = (int) (l + (300D * Math.pow(2D, l / 7D)));
			points += i1;
			EXPERIENCE_BY_LEVEL[i] = points >> 2;
		}

		anIntArray1232 = new int[32];
		points = 2;
		for (int k = 0; k < 32; k++) {
			anIntArray1232[k] = points - 1;
			points += points;
		}

	}
}


