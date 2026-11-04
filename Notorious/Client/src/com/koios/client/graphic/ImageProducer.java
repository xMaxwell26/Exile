package com.koios.client.graphic;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class ImageProducer {

	public final int[] canvasRaster;
	public final int canvasWidth;
	public final int canvasHeight;
	private final BufferedImage bufferedImage;

	public ImageProducer(int canvasWidth, int canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
		initDrawingArea();
	}

	public void drawGraphics(int x, Graphics graphics, int y) {
		graphics.drawImage(bufferedImage, y, x, null);
        com.koios.client.arpg.HighDpiArt.draw(graphics,canvasRaster,y,x);
        com.koios.client.arpg.QaClientHooks.present(bufferedImage,canvasRaster,y,x,graphics);
	}

	public void initDrawingArea() {
		DrawingArea.initDrawingArea(canvasHeight, canvasWidth, canvasRaster);
	}
}