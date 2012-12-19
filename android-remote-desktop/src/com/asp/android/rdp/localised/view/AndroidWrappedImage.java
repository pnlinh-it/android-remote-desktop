package com.asp.android.rdp.localised.view;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

import com.asp.android.rdp.core.view.WrappedImage;
import com.asp.android.rdp.utils.RDPLogger;


public class AndroidWrappedImage extends WrappedImage{
	private android.graphics.Bitmap bitmap;

	public AndroidWrappedImage(int width, int height) {
		bitmap = android.graphics.Bitmap.createBitmap(width, height,
				Config.RGB_565);
	}

	public int getHeight() {
		if (bitmap != null) {
			return bitmap.getHeight();
		}
		return 0;
	}

	public int getWidth() {
		if (bitmap != null) {
			return bitmap.getWidth();
		}
		return 0;
	}

	public Bitmap getBitmap() {

		return bitmap;
	}

	public int[] getSubImage(int x, int y, int width, int height) {
		if (bitmap != null) {
			int[] pixels=new int[width*height];
			
			bitmap.getPixels(pixels,0,width, x, y, width, height);
			
			return  pixels;
			
		}
		return null;
	}

	public void setRGB(int x, int y, int color) {
		if (bitmap != null) {
			bitmap.setPixel(x, y, color);
		}
	}

	/**
	 * Apply a given array of colour values to an area of pixels in the image,
	 * do not convert for colour model
	 * 
	 * @param x
	 *            x-coordinate for left of area to set
	 * @param y
	 *            y-coordinate for top of area to set
	 * @param cx
	 *            width of area to set
	 * @param cy
	 *            height of area to set
	 * @param data
	 *            array of pixel colour values to apply to area
	 * @param offset
	 *            offset to pixel data in data
	 * @param w
	 *            width of a line in data (measured in pixels)
	 */
	public void setRGBNoConversion(int x, int y, int width, int height,
			int[] data, int offset, int w) {
		if (bitmap != null) {
			bitmap.setPixels(data, offset, w, x, y, width, height);
		}
	}

	public void setRGB(int x, int y, int width, int height, int[] data,
			int offset, int w) {
		if (bitmap != null) {
			bitmap.setPixels(data, offset, w, x, y, width, height);
		}
	}

	public int[] getRGB(int x, int y, int width, int height, int[] data,
			int offset, int w) {
		if (bitmap != null) {
			int[] pixels = new int[width * height];
			bitmap.getPixels(pixels, offset, w, x, y, width, height);
			return pixels;
		}
		return null;
	}

	public int getRGB(int x, int y) {
		if (bitmap != null) {
			return bitmap.getPixel(x, y);
		}
		return 0;
	}
	
	public Bitmap getBackStoreBitmap(){
		return bitmap;
	}
}
