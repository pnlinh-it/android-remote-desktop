package com.asp.android.rdp.core.view;





public abstract class WrappedImage {


	public abstract int getHeight();

	public abstract int getWidth();

	

	public abstract int[] getSubImage(int x, int y, int width, int height);

	public abstract void setRGB(int x, int y, int color);

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
	public abstract void setRGBNoConversion(int x, int y, int width, int height,
			int[] data, int offset, int w);

	public abstract void setRGB(int x, int y, int width, int height, int[] data,
			int offset, int w);

	public abstract int[] getRGB(int x, int y, int width, int height, int[] data,
			int offset, int w);

	public abstract int getRGB(int x, int y);

}
