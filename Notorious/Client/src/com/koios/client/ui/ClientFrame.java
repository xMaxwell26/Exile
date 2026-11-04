package com.koios.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;

import com.koios.client.constants.Constants;

public final class ClientFrame extends Frame {

	private final RSApplet applet;
	public Toolkit toolkit = Toolkit.getDefaultToolkit();
	public Dimension screenSize = toolkit.getScreenSize();
	public int screenWidth = (int) screenSize.getWidth();
	public int screenHeight = (int) screenSize.getHeight();
	protected final Insets insets;
	private static final long serialVersionUID = 1L;

	public ClientFrame(RSApplet applet,int width,int height,boolean resizable,boolean fullscreen){this(applet,width,height,resizable,fullscreen,null);}
 public ClientFrame(RSApplet applet, int width, int height, boolean resizable, boolean fullscreen,java.awt.Rectangle previousDisplay) {
		this.applet = applet;
		String qa=System.getProperty("arpg.qaCharacter","");
        setTitle(Constants.CLIENT_NAME+(qa.isEmpty()?"":" - QA "+qa));
		setResizable(resizable);
		setUndecorated(fullscreen);
		setVisible(true); 
		insets = getInsets();
		if (resizable) {
			setMinimumSize(new Dimension(width + insets.left + insets.right, height + insets.top + insets.bottom));
		}
		setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		setLocationRelativeTo(null);
        java.awt.Rectangle bounds=previousDisplay;
        if(!qa.isEmpty()){java.awt.GraphicsDevice primary=java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();java.awt.GraphicsDevice target=primary;for(java.awt.GraphicsDevice device:java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())if(device!=primary){target=device;break;}bounds=target.getDefaultConfiguration().getBounds();}
        if(bounds!=null){int offset=qa.isEmpty()?(bounds.width-getWidth())/2:Integer.getInteger("arpg.qaOffset",0);setLocation(bounds.x+(fullscreen?0:Math.max(0,Math.min(offset,bounds.width-getWidth()))),bounds.y+(fullscreen?0:Math.max(0,(bounds.height-getHeight())/2)));}
		setBackground(Color.BLACK);
		requestFocus();
		toFront();
	}

	public Graphics getGraphics() {
		final Graphics graphics = super.getGraphics();
		Insets insets = this.getInsets();
		//graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.translate(insets.left, insets.top);
		return graphics;
	}

	public int getFrameWidth() {
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}

	public int getFrameHeight() {
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}

	public void update(Graphics graphics) {
		applet.update(graphics);
	}

	public void paint(Graphics graphics) {
		applet.paint(graphics);
	}
}
