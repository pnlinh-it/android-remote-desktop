package com.asp.android.rdp.server;

import com.asp.android.rdp.core.BackstoreManager;
import com.asp.android.rdp.core.Cache;
import com.asp.android.rdp.core.RasterOp;
import com.asp.android.rdp.core.RdesktopException;
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
import com.asp.android.rdp.core.view.Bitmap;
import com.asp.android.rdp.localised.model.RDPConnection;
import com.asp.android.rdp.localised.view.AndroidWrappedImage;
import com.asp.android.rdp.utils.RDPLogger;

public class DroidRDPBackstoreManager extends BackstoreManager{

	
	
	private RDPConnection rdpConnection;
	public DroidRDPBackstoreManager(RDPConnection rdpConnection) {
		super(new AndroidWrappedImage(rdpConnection.getOptions().width,rdpConnection.getOptions().height));
		this.rdpConnection=rdpConnection;
		rop=new RasterOp();
		
	}
	
	

	
	@Override
	public void displayImage(int[] data, int width, int height, int x, int y,
			int cx, int cy) {
		backstore.setRGB(x, y, cx, cy, data, 0, width);
	}
	/**
	 * Perform a dest blt
	 * 
	 * @param destblt
	 *            DestBltOrder describing the blit to be performed
	 */
	public void drawDestBltOrder(DestBltOrder destblt) {
		int x = destblt.getX();
		int y = destblt.getY();

		if (x > this.right || y > this.bottom)
			return; // off screen

		int cx = destblt.getCX();
		int cy = destblt.getCY();

		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;

		rop.do_array(destblt.getOpcode(), backstore, this.width, x, y, cx, cy,
				null, 0, 0, 0, rdpConnection.getOptions().bpp_mask);
		if(canvas!=null)
		canvas.repaint(x, y, cx, cy);

	}

	public void drawGlyph(int mixmode, int x, int y, int cx, int cy,
			byte[] data, int bgcolor, int fgcolor) {

		int pdata = 0;
		int index = 0x80;

		int bytes_per_row = (cx - 1) / 8 + 1;
		int newx, newy, newcx, newcy;

		int Bpp = rdpConnection.getOptions().Bpp;

		// convert to 24-bit colour
		fgcolor = Bitmap.convertTo24(fgcolor,
				rdpConnection.getOptions().server_bpp);
		bgcolor = Bitmap.convertTo24(bgcolor,
				rdpConnection.getOptions().server_bpp);

		// correction for 24-bit colour
		if (Bpp == 3) {
			fgcolor = ((fgcolor & 0xFF) << 16) | (fgcolor & 0xFF00)
					| ((fgcolor & 0xFF0000) >> 16);
			bgcolor = ((bgcolor & 0xFF) << 16) | (bgcolor & 0xFF00)
					| ((bgcolor & 0xFF0000) >> 16);
		}

		// clip here instead

		if (x > this.right || y > this.bottom)
			return; // off screen

		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			newx = this.left;
		else
			newx = x;
		newcx = clipright - x + 1; // not clipright - newx - 1

		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			newy = this.top;
		else
			newy = y;

		newcy = clipbottom - newy + 1;

		int pbackstore = (newy * this.width) + x;
		pdata = bytes_per_row * (newy - y); // offset y, but not x

		if (mixmode == MIX_TRANSPARENT) { // FillStippled
			for (int i = 0; i < newcy; i++) {
				for (int j = 0; j < newcx; j++) {
					if (index == 0) { // next row
						pdata++;
						index = 0x80;
					}

					if ((data[pdata] & index) != 0) {
						if ((x + j >= newx) && (newx + j > 0) && (newy + i > 0))
							// since haven't offset x
							backstore.setRGB(newx + j, newy + i, fgcolor);
					}
					index >>= 1;
				}
				pdata++;
				index = 0x80;
				pbackstore += this.width;
				if (pdata == data.length) {
					pdata = 0;
				}
			}
		} else { // FillOpaqueStippled
			for (int i = 0; i < newcy; i++) {
				for (int j = 0; j < newcx; j++) {
					if (index == 0) { // next row
						pdata++;
						index = 0x80;
					}

					if (x + j >= newx) {
						if ((x + j > 0) && (y + i > 0)) {
							if ((data[pdata] & index) != 0)
								backstore.setRGB(x + j, y + i, fgcolor);
							else
								backstore.setRGB(x + j, y + i, bgcolor);
						}
					}
					index >>= 1;
				}
				pdata++;
				index = 0x80;
				pbackstore += this.width;
				if (pdata == data.length) {
					pdata = 0;
				}
			}
		}
		// this.repaint(newx, newy, newcx, newcy);
	}

	public void drawLineOrder(LineOrder line) {
		int x1 = line.getStartX();
		int y1 = line.getStartY();
		int x2 = line.getEndX();
		int y2 = line.getEndY();

		int fgcolor = line.getPen().getColor();

		int opcode = line.getOpcode() - 1;
		drawLine(x1, y1, x2, y2, fgcolor, opcode);
	}

	public void drawLine(int x1, int y1, int x2, int y2, int color, int opcode) {
		// convert to 24-bit colour
		color = Bitmap
				.convertTo24(color, rdpConnection.getOptions().server_bpp);

		if (x1 == x2 || y1 == y2) {
			drawLineVerticalHorizontal(x1, y1, x2, y2, color, opcode);
			return;
		}
		int deltax = Math.abs(x2 - x1); // The difference between the x's
		int deltay = Math.abs(y2 - y1); // The difference between the y's
		int x = x1; // Start x off at the first pixel
		int y = y1; // Start y off at the first pixel
		int xinc1, xinc2, yinc1, yinc2;
		int num, den, numadd, numpixels;

		if (x2 >= x1) { // The x-values are increasing
			xinc1 = 1;
			xinc2 = 1;
		} else { // The x-values are decreasing
			xinc1 = -1;
			xinc2 = -1;
		}

		if (y2 >= y1) { // The y-values are increasing
			yinc1 = 1;
			yinc2 = 1;
		} else { // The y-values are decreasing
			yinc1 = -1;
			yinc2 = -1;
		}

		if (deltax >= deltay) { // There is at least one x-value for every
			// y-value
			xinc1 = 0; // Don't change the x when numerator >= denominator
			yinc2 = 0; // Don't change the y for every iteration
			den = deltax;
			num = deltax / 2;
			numadd = deltay;
			numpixels = deltax; // There are more x-values than y-values
		} else { // There is at least one y-value for every x-value
			xinc2 = 0; // Don't change the x for every iteration
			yinc1 = 0; // Don't change the y when numerator >= denominator
			den = deltay;
			num = deltay / 2;
			numadd = deltax;
			numpixels = deltay; // There are more y-values than x-values
		}

		for (int curpixel = 0; curpixel <= numpixels; curpixel++) {
			setPixel(opcode, x, y, color); // Draw the current pixel
			num += numadd; // Increase the numerator by the top of the fraction
			if (num >= den) { // Check if numerator >= denominator
				num -= den; // Calculate the new numerator value
				x += xinc1; // Change the x as appropriate
				y += yinc1; // Change the y as appropriate
			}
			x += xinc2; // Change the x as appropriate
			y += yinc2; // Change the y as appropriate
		}

		int x_min = x1 < x2 ? x1 : x2;
		int x_max = x1 > x2 ? x1 : x2;
		int y_min = y1 < y2 ? y1 : y2;
		int y_max = y1 > y2 ? y1 : y2;
		if(canvas!=null)
		canvas.repaint(x_min, y_min, x_max - x_min + 1, y_max - y_min + 1);
	}

	public void setPixel(int opcode, int x, int y, int color) {
		int Bpp = rdpConnection.getOptions().Bpp;

		// correction for 24-bit colour
		if (Bpp == 3)
			color = ((color & 0xFF) << 16) | (color & 0xFF00)
					| ((color & 0xFF0000) >> 16);

		if ((x < this.left) || (x > this.right) || (y < this.top)
				|| (y > this.bottom)) { // Clip
		} else {
			rop.do_pixel(opcode, backstore, x, y, color, rdpConnection
					.getOptions().bpp_mask);
		}
	}

	public void drawLineVerticalHorizontal(int x1, int y1, int x2, int y2,
			int color, int opcode) {
		int pbackstore;
		int i;
		// only vertical or horizontal lines
		if (y1 == y2) { // HORIZONTAL
			if (y1 >= this.top && y1 <= this.bottom) { // visible
				if (x2 > x1) { // x inc, y1=y2
					if (x1 < this.left)
						x1 = this.left;
					if (x2 > this.right)
						x2 = this.right;
					pbackstore = y1 * this.width + x1;
					for (i = 0; i < x2 - x1; i++) {
						rop.do_pixel(opcode, backstore, x1 + i, y1, color,
								rdpConnection.getOptions().bpp_mask);
						pbackstore++;
					}
					if(canvas!=null)
					canvas.repaint(x1, y1, x2 - x1 + 1, 1);
				} else { // x dec, y1=y2
					if (x2 < this.left)
						x2 = this.left;
					if (x1 > this.right)
						x1 = this.right;
					pbackstore = y1 * this.width + x1;
					for (i = 0; i < x1 - x2; i++) {
						rop.do_pixel(opcode, backstore, x2 + i, y1, color,
								rdpConnection.getOptions().bpp_mask);
						pbackstore--;
					}
					if(canvas!=null)
					canvas.repaint(x2, y1, x1 - x2 + 1, 1);
				}
			}
		} else { // x1==x2 VERTICAL
			if (x1 >= this.left && x1 <= this.right) { // visible
				if (y2 > y1) { // x1=x2, y inc
					if (y1 < this.top)
						y1 = this.top;
					if (y2 > this.bottom)
						y2 = this.bottom;
					pbackstore = y1 * this.width + x1;
					for (i = 0; i < y2 - y1; i++) {
						rop.do_pixel(opcode, backstore, x1, y1 + i, color,
								rdpConnection.getOptions().bpp_mask);
						pbackstore += this.width;
					}
					if(canvas!=null)
					canvas.repaint(x1, y1, 1, y2 - y1 + 1);
				} else { // x1=x2, y dec
					if (y2 < this.top)
						y2 = this.top;
					if (y1 > this.bottom)
						y1 = this.bottom;
					pbackstore = y1 * this.width + x1;
					for (i = 0; i < y1 - y2; i++) {
						rop.do_pixel(opcode, backstore, x1, y2 + i, color,
								rdpConnection.getOptions().bpp_mask);
						pbackstore -= this.width;
					}
					if(canvas!=null)
					canvas.repaint(x1, y2, 1, y1 - y2 + 1);
				}
			}
		}

	}

	public void drawRectangleOrder(RectangleOrder rect) {
		fillRectangle(rect.getX(), rect.getY(), rect.getCX(), rect.getCY(),
				rect.getColor());
	}

	public void drawScreenBltOrder(ScreenBltOrder screenblt) {
		int x = screenblt.getX();
		int y = screenblt.getY();

		if (x > this.right || y > this.bottom)
			return; // off screen

		int cx = screenblt.getCX();
		int cy = screenblt.getCY();
		int srcx = screenblt.getSrcX();
		int srcy = screenblt.getSrcY();

		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;

		srcx += x - screenblt.getX();
		srcy += y - screenblt.getY();

		rop.do_array(screenblt.getOpcode(), backstore, this.width, x, y, cx,
				cy, null, this.width, srcx, srcy,
				rdpConnection.getOptions().bpp_mask);
		if(canvas!=null)
		canvas.repaint(x, y, cx, cy);
	}

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
	public void patBltOrder(int opcode, int x, int y, int cx, int cy,
			int fgcolor, int bgcolor, Brush brush) {

		// convert to 24-bit colour
		fgcolor = Bitmap.convertTo24(fgcolor,
				rdpConnection.getOptions().server_bpp);
		bgcolor = Bitmap.convertTo24(bgcolor,
				rdpConnection.getOptions().server_bpp);

		// Perform standard clipping checks, x-axis
		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		// Perform standard clipping checks, y-axis
		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;

		int i;
		int[] src = null;
		switch (brush.getStyle()) {
		case 0: // solid
			// make efficient version of rop later with int fgcolor and boolean
			// usearray set to false for single colour
			src = new int[cx * cy];
			for (i = 0; i < src.length; i++)
				src[i] = fgcolor;
			rop.do_array(opcode, backstore, this.width, x, y, cx, cy, src, cx,
					0, 0, rdpConnection.getOptions().bpp_mask);
			if(canvas!=null)
			canvas.repaint(x, y, cx, cy);

			break;

		case 2: // hatch

			break;

		case 3: // pattern
			int brushx = brush.getXOrigin();
			int brushy = brush.getYOrigin();
			byte[] pattern = brush.getPattern();
			byte[] ipattern = pattern;

			/*
			 * // not sure if this inversion is needed byte[] ipattern = new
			 * byte[8]; for(i=0;i<ipattern.length;i++) {
			 * ipattern[ipattern.length-1-i] = pattern[i]; }
			 */

			src = new int[cx * cy];
			int psrc = 0;
			for (i = 0; i < cy; i++) {
				for (int j = 0; j < cx; j++) {
					if ((ipattern[(i + brushy) % 8] & (0x01 << ((j + brushx) % 8))) == 0)
						src[psrc] = fgcolor;
					else
						src[psrc] = bgcolor;
					psrc++;
				}
			}
			rop.do_array(opcode, backstore, this.width, x, y, cx, cy, src, cx,
					0, 0, rdpConnection.getOptions().bpp_mask);
			if(canvas!=null)
			canvas.repaint(x, y, cx, cy);
			break;
		default:

		}
	}

	/**
	 * Perform a pattern blit on the screen
	 * 
	 * @param patblt
	 *            PatBltOrder describing the blit to be performed
	 */
	public void drawPatBltOrder(PatBltOrder patblt) {
		Brush brush = patblt.getBrush();
		int x = patblt.getX();
		int y = patblt.getY();

		if (x > this.right || y > this.bottom)
			return; // off screen

		int cx = patblt.getCX();
		int cy = patblt.getCY();
		int fgcolor = patblt.getForegroundColor();
		int bgcolor = patblt.getBackgroundColor();
		int opcode = patblt.getOpcode();

		patBltOrder(opcode, x, y, cx, cy, fgcolor, bgcolor, brush);
	}

	/**
	 * Parse a delta co-ordinate in polyline order form
	 * 
	 * @param buffer
	 * @param offset
	 * @return
	 */
	static int parse_delta(byte[] buffer, int[] offset) {
		int value = buffer[offset[0]++] & 0xff;
		int two_byte = value & 0x80;

		if ((value & 0x40) != 0) /* sign bit */
			value |= ~0x3f;
		else
			value &= 0x3f;

		if (two_byte != 0)
			value = (value << 8) | (buffer[offset[0]++] & 0xff);

		return value;
	}

	/**
	 * Draw a multi-point set of lines to the screen
	 * 
	 * @param polyline
	 *            PolyLineOrder describing the set of lines to draw
	 */
	public void drawPolyLineOrder(PolyLineOrder polyline) {
		int x = polyline.getX();
		int y = polyline.getY();
		int fgcolor = polyline.getForegroundColor();
		int datasize = polyline.getDataSize();
		byte[] databytes = polyline.getData();
		int lines = polyline.getLines();

		// convert to 24-bit colour
		fgcolor = Bitmap.convertTo24(fgcolor,
				rdpConnection.getOptions().server_bpp);

		int[] data = new int[1];
		data[0] = ((lines - 1) / 4) + 1;
		int flags = 0;
		int index = 0;

		int opcode = polyline.getOpcode() - 1;

		for (int line = 0; (line < lines) && (data[0] < datasize); line++) {
			int xfrom = x;
			int yfrom = y;

			if (line % 4 == 0)
				flags = databytes[index++];

			if ((flags & 0xc0) == 0)
				flags |= 0xc0; /* none = both */

			if ((flags & 0x40) != 0)
				x += parse_delta(databytes, data);

			if ((flags & 0x80) != 0)
				y += parse_delta(databytes, data);

			drawLine(xfrom, yfrom, x, y, fgcolor, opcode);
			flags <<= 2;
		}
	}

	/**
	 * Perform a memory blit
	 * 
	 * @param memblt
	 *            MemBltOrder describing the blit to be performed
	 */
	public void drawMemBltOrder(MemBltOrder memblt) {
		int x = memblt.getX();
		int y = memblt.getY();

		if (x > this.right || y > this.bottom)
			return; // off screen

		int cx = memblt.getCX();
		int cy = memblt.getCY();
		int srcx = memblt.getSrcX();
		int srcy = memblt.getSrcY();

		// Perform standard clipping checks, x-axis
		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		// Perform standard clipping checks, y-axis
		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;

		srcx += x - memblt.getX();
		srcy += y - memblt.getY();

		try {
			Bitmap bitmap = cache.getBitmap(memblt.getCacheID(), memblt
					.getCacheIDX());

			rop.do_array(memblt.getOpcode(), backstore, this.width, x, y, cx,
					cy, bitmap.getBitmapData(), bitmap.getWidth(), srcx, srcy,
					rdpConnection.getOptions().bpp_mask);
			if(canvas!=null)
			canvas.repaint(x, y, cx, cy);
		} catch (RdesktopException e) {
		}
	}

	/**
	 * Perform a tri blit on the screen
	 * 
	 * @param triblt
	 *            TriBltOrder describing the blit
	 */
	public void drawTriBltOrder(TriBltOrder triblt) {
		int x = triblt.getX();
		int y = triblt.getY();

		if (x > this.right || y > this.bottom)
			return; // off screen

		int cx = triblt.getCX();
		int cy = triblt.getCY();
		int srcx = triblt.getSrcX();
		int srcy = triblt.getSrcY();
		int fgcolor = triblt.getForegroundColor();
		int bgcolor = triblt.getBackgroundColor();
		Brush brush = triblt.getBrush();

		// convert to 24-bit colour
		fgcolor = Bitmap.convertTo24(fgcolor,
				rdpConnection.getOptions().server_bpp);
		bgcolor = Bitmap.convertTo24(bgcolor,
				rdpConnection.getOptions().server_bpp);

		// Perform standard clipping checks, x-axis
		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		// Perform standard clipping checks, y-axis
		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;

		try {
			Bitmap bitmap = cache.getBitmap(triblt.getCacheID(), triblt
					.getCacheIDX());
			switch (triblt.getOpcode()) {
			case 0x69: // PDSxxn
				rop.do_array(ROP2_XOR, backstore, this.width, x, y, cx, cy,
						bitmap.getBitmapData(), bitmap.getWidth(), srcx, srcy,
						rdpConnection.getOptions().bpp_mask);
				patBltOrder(ROP2_NXOR, x, y, cx, cy, fgcolor, bgcolor, brush);
				break;
			case 0xb8: // PSDPxax
				patBltOrder(ROP2_XOR, x, y, cx, cy, fgcolor, bgcolor, brush);
				rop.do_array(ROP2_AND, backstore, this.width, x, y, cx, cy,
						bitmap.getBitmapData(), bitmap.getWidth(), srcx, srcy,
						rdpConnection.getOptions().bpp_mask);
				patBltOrder(ROP2_XOR, x, y, cx, cy, fgcolor, bgcolor, brush);
				break;
			case 0xc0: // PSa
				rop.do_array(ROP2_COPY, backstore, this.width, x, y, cx, cy,
						bitmap.getBitmapData(), bitmap.getWidth(), srcx, srcy,
						rdpConnection.getOptions().bpp_mask);
				patBltOrder(ROP2_AND, x, y, cx, cy, fgcolor, bgcolor, brush);
				break;

			default:

				rop.do_array(ROP2_COPY, backstore, this.width, x, y, cx, cy,
						bitmap.getBitmapData(), bitmap.getWidth(), srcx, srcy,
						rdpConnection.getOptions().bpp_mask);
			}
		} catch (RdesktopException e) {
		}
	}

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
	public int[] getImage(int x, int y, int cx, int cy) {

		int[] data = new int[cx * cy];

		data = backstore.getRGB(x, y, cx, cy, null, // no existing image data to
				// add to
				0, // retrieving as complete image, no offset needed
				cx);

		return data;
	}

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
	public void putImage(int x, int y, int cx, int cy, int[] data) {

		backstore.setRGBNoConversion(x, y, cx, cy, data, 0, // drawing entire
				// image, no
				// offset needed
				cx);
		if(canvas!=null)
		canvas.repaint(x, y, cx, cy);
	}
	public void fillRectangle(int x, int y, int cx, int cy, int color) {
		
		// clip here instead
		if (x > this.right || y > this.bottom)
			return; // off screen

		int Bpp = rdpConnection.getOptions().Bpp;

		// convert to 24-bit colour
		color = Bitmap
				.convertTo24(color, rdpConnection.getOptions().server_bpp);

		// correction for 24-bit colour
		if (Bpp == 3)
			color = ((color & 0xFF) << 16) | (color & 0xFF00)
					| ((color & 0xFF0000) >> 16);

		// Perform standard clipping checks, x-axis
		int clipright = x + cx - 1;
		if (clipright > this.right)
			clipright = this.right;
		if (x < this.left)
			x = this.left;
		cx = clipright - x + 1;

		// Perform standard clipping checks, y-axis
		int clipbottom = y + cy - 1;
		if (clipbottom > this.bottom)
			clipbottom = this.bottom;
		if (y < this.top)
			y = this.top;
		cy = clipbottom - y + 1;
		
		RDPLogger.d("Array size :"+cx+"*"+cy);

		// construct rectangle as integer array, filled with color
		int[] rect = new int[cx * cy];
		for (int i = 0; i < rect.length; i++)
			rect[i] = color;
		// draw rectangle to backstore
		backstore.setRGB(x, y, cx, cy, rect, 0, cx);
		if(canvas!=null){
		canvas.repaint(x, y, cx, cy); // seems to be faster than Graphics.fillRect
		}
		// according to JProbe
	}
	public void resetClip() {

		this.top = 0;
		this.left = 0;
		this.right = this.width - 1; // changed
		this.bottom = this.height - 1; // changed
	}
	public void setClip(BoundsOrder bounds) {
		this.top = bounds.getTop();
		this.left = bounds.getLeft();
		this.right = bounds.getRight();
		this.bottom = bounds.getBottom();
	}




	@Override
	public void repaint(int x, int y, int width, int height) {
		if(canvas!=null){
			canvas.repaint(x, y, width, height);
		}
	}
}
