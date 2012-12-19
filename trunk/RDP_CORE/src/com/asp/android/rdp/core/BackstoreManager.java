package com.asp.android.rdp.core;

import com.asp.android.rdp.core.model.BoundsOrder;
import com.asp.android.rdp.core.model.Brush;
import com.asp.android.rdp.core.model.DestBltOrder;
import com.asp.android.rdp.core.model.LineOrder;
import com.asp.android.rdp.core.model.MemBltOrder;
import com.asp.android.rdp.core.model.PatBltOrder;
import com.asp.android.rdp.core.model.PolyLineOrder;
import com.asp.android.rdp.core.model.RectangleOrder;
import com.asp.android.rdp.core.model.ScreenBltOrder;
import com.asp.android.rdp.core.model.TriBltOrder;
import com.asp.android.rdp.core.view.WrappedImage;

public abstract class BackstoreManager {

	protected int width = 0;

	protected int height = 0;

	protected int top = 0;

	protected int left = 0;

	protected int right = 0;

	protected int bottom = 0;

	public static final int ROP2_COPY = 0xc;

	protected static final int ROP2_XOR = 0x6;

	protected static final int ROP2_AND = 0x8;

	protected static final int ROP2_NXOR = 0x9;

	protected static final int ROP2_OR = 0xe;

	protected static final int MIX_TRANSPARENT = 0;

	protected static final int MIX_OPAQUE = 1;

	protected static final int TEXT2_VERTICAL = 0x04;

	protected static final int TEXT2_IMPLICIT_X = 0x20;

	protected RasterOp rop;

	protected Cache cache;

	protected WrappedImage backstore;
	protected RdesktopCanvas canvas;

	public WrappedImage getWrappedImage() {
		return backstore;
	}

	public BackstoreManager(WrappedImage wrappedImage) {
		this.backstore = wrappedImage;
		this.width = backstore.getWidth();
		this.height = backstore.getHeight();
		this.right = this.width - 1; // changed
		this.bottom = this.height - 1; // changed
	}

	public void setRdesktopCanvas(RdesktopCanvas rdesktopCanvas) {
		this.canvas = rdesktopCanvas;
	}

	public abstract void displayImage(int[] data, int width, int height, int x,
			int y, int cx, int cy);

	public abstract void fillRectangle(int x, int y, int cx, int cy, int color);

	/**
	 * Perform a dest blt
	 * 
	 * @param destblt
	 *            DestBltOrder describing the blit to be performed
	 */
	public abstract void drawDestBltOrder(DestBltOrder destblt);

	public abstract void drawGlyph(int mixmode, int x, int y, int cx, int cy,
			byte[] data, int bgcolor, int fgcolor);

	public abstract void drawLineOrder(LineOrder line);

	public abstract void drawLine(int x1, int y1, int x2, int y2, int color,
			int opcode);

	public abstract void setPixel(int opcode, int x, int y, int color);

	public abstract void drawLineVerticalHorizontal(int x1, int y1, int x2,
			int y2, int color, int opcode);

	public abstract void drawRectangleOrder(RectangleOrder rect);

	public abstract void drawScreenBltOrder(ScreenBltOrder screenblt);

	/**
	 * Draw a pattern to the screen (pattern blit)
	 * 
	 * @param opcode
	 *            Code defining operation to be performed
	 * @param x
	 *            x coordinate for left of blit area
	 * @param y
	 *            y coordinate for top of blit area
	 * @param cx
	 *            Width of blit area
	 * @param cy
	 *            Height of blit area
	 * @param fgcolor
	 *            Foreground colour for pattern
	 * @param bgcolor
	 *            Background colour for pattern
	 * @param brush
	 *            Brush object defining pattern to be drawn
	 */
	public abstract void patBltOrder(int opcode, int x, int y, int cx, int cy,
			int fgcolor, int bgcolor, Brush brush);

	/**
	 * Perform a pattern blit on the screen
	 * 
	 * @param patblt
	 *            PatBltOrder describing the blit to be performed
	 */
	public abstract void drawPatBltOrder(PatBltOrder patblt);

	/**
	 * Draw a multi-point set of lines to the screen
	 * 
	 * @param polyline
	 *            PolyLineOrder describing the set of lines to draw
	 */
	public abstract void drawPolyLineOrder(PolyLineOrder polyline);

	/**
	 * Perform a memory blit
	 * 
	 * @param memblt
	 *            MemBltOrder describing the blit to be performed
	 */
	public abstract void drawMemBltOrder(MemBltOrder memblt);

	/**
	 * Perform a tri blit on the screen
	 * 
	 * @param triblt
	 *            TriBltOrder describing the blit
	 */
	public abstract void drawTriBltOrder(TriBltOrder triblt);

	/**
	 * Retrieve an image from the backstore, as integer pixel information
	 * 
	 * @param x
	 *            x coordinate of image to retrieve
	 * @param y
	 *            y coordinage of image to retrieve
	 * @param cx
	 *            width of image to retrieve
	 * @param cy
	 *            height of image to retrieve
	 * @return Requested area of backstore, as an array of integer pixel colours
	 */
	public abstract int[] getImage(int x, int y, int cx, int cy);

	/**
	 * Draw an image (from an integer array of colour data) to the backstore,
	 * also calls repaint to draw image to canvas
	 * 
	 * @param x
	 *            x coordinate at which to draw image
	 * @param y
	 *            y coordinate at which to draw image
	 * @param cx
	 *            Width of drawn image (clips, does not scale)
	 * @param cy
	 *            Height of drawn image (clips, does not scale)
	 * @param data
	 *            Image to draw, represented as an array of integer pixel
	 *            colours
	 */
	public abstract void putImage(int x, int y, int cx, int cy, int[] data);

	public abstract void resetClip();

	/**
	 * Set clipping boundaries for canvas, based on a bounds order
	 * 
	 * @param bounds
	 *            Order defining new boundaries
	 */
	public abstract void setClip(BoundsOrder bounds);
	
	public abstract void repaint(int x,int y,int width,int height);
	
	public  void registerCache(Cache cache){
		this.cache=cache;
	}

}
