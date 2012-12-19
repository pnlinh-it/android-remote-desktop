/* Cache.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 12 $
 * Author: $Author: miha_vitorovic $
 * Date: $Date: 2007-05-11 17:19:09 +0530 (Fri, 11 May 2007) $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Handle caching of bitmaps, cursors, colour maps, text and fonts
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 * 
 * (See gpl.txt for details of the GNU General Public License.)
 *          
 */
package com.asp.android.rdp.core;

import com.asp.android.rdp.core.view.Bitmap;







public class Cache {

	

	private static final int RDPCACHE_COLOURMAPSIZE = 0x06; // unified patch

	private Bitmap[][] bitmapcache = new Bitmap[3][600];

	

	private Glyph[][] fontcache = new Glyph[12][256];

	private DataBlob[] textcache = new DataBlob[256];

	private int[] highdeskcache = new int[921600];

	private int num_bitmaps_in_memory[] = new int[3];

	
	public Cache() {
	}

	void TOUCH(int id, int idx) {
		//TODO:persistent
//		bitmapcache[id][idx].usage = ++PstCache.g_stamp;
	}

	/**
	 * Remove the least-recently-used bitmap from the specified cache
	 * 
	 * @param cache_id
	 *            Number of cache from which to remove bitmap
	 */
	void removeLRUBitmap(int cache_id) {
		int i;
		int cache_idx = 0;
		int m = 0xffffffff;

		for (i = 0; i < bitmapcache[cache_id].length; i++) {
			if ((bitmapcache[cache_id][i] != null)
					&& (bitmapcache[cache_id][i].getBitmapData() != null)
					&& (bitmapcache[cache_id][i].usage < m)) {
				cache_idx = i;
				m = bitmapcache[cache_id][i].usage;
			}
		}

		bitmapcache[cache_id][cache_idx] = null;
		--num_bitmaps_in_memory[cache_id];
	}

	/**
	 * Retrieve the indexed colour model from the specified cache
	 * 
	 * @param cache_id
	 *            ID of cache from which to retrieve colour model
	 * @return Indexed colour model for specified cache
	 * @throws RdesktopException
	 */
	
	/**
	 * Assign a colour model to a specified cache
	 * 
	 * @param cache_id
	 *            ID of cache to which the colour map should be added
	 * @param map
	 *            Indexed colour model to assign to the cache
	 * @throws RdesktopException
	 */
	

	/**
	 * Retrieve a bitmap from the cache
	 * 
	 * @param cache_id
	 *            ID of cache from which to retrieve bitmap
	 * @param cache_idx
	 *            ID of bitmap to return
	 * @return Bitmap stored in specified location within the cache
	 * @throws RdesktopException
	 */
	public Bitmap getBitmap(int cache_id, int cache_idx)
			throws RdesktopException {
		Bitmap bitmap = null;

		if ((cache_id < bitmapcache.length)
				&& (cache_idx < bitmapcache[0].length)) {
			bitmap = bitmapcache[cache_id][cache_idx];
			
			if (bitmap != null)
				return bitmap;
		}

		throw new RdesktopException("Could not get Bitmap!");
	}

	/**
	 * Add a bitmap to the cache
	 * 
	 * @param cache_id
	 *            ID of cache to which the Bitmap should be added
	 * @param cache_idx
	 *            ID of location in specified cache in which to store the Bitmap
	 * @param bitmap
	 *            Bitmap object to store in cache
	 * @param stamp
	 *            Timestamp for storage of bitmap
	 * @throws RdesktopException
	 */
	public void putBitmap(int cache_id, int cache_idx, Bitmap bitmap, int stamp)
			throws RdesktopException {

		// Bitmap old;

		if ((cache_id < bitmapcache.length)
				&& (cache_idx < bitmapcache[0].length)) {
			bitmapcache[cache_id][cache_idx] = bitmap;
			/*
			 * if (Options.use_rdp5) { if (++num_bitmaps_in_memory[cache_id] >
			 * Rdp.BMPCACHE2_C2_CELLS) removeLRUBitmap(cache_id); }
			 * 
			 * bitmapcache[cache_id][cache_idx] = bitmap;
			 * bitmapcache[cache_id][cache_idx].usage = stamp;
			 */
		} else {
			throw new RdesktopException("Could not put Bitmap!");
		}
	}

	/**
	 * Retrieve a Cursor object from the cache
	 * 
	 * @param cache_idx
	 *            ID of cache in which the Cursor is stored
	 * @return Cursor stored in specified cache
	 * @throws RdesktopException
	 */
	

	/**
	 * Assign a Cursor object to a specific cache
	 * 
	 * @param cache_idx
	 *            ID of cache to store Cursor in
	 * @param cursor
	 *            Cursor object to assign to cache
	 * @throws RdesktopException
	 */
	 

	/**
	 * Add a font to the cache
	 * 
	 * @param glyph
	 *            Glyph containing references to relevant font
	 * @throws RdesktopException
	 */
	public void putFont(Glyph glyph) throws RdesktopException {
		if ((glyph.getFont() < fontcache.length)
				&& (glyph.getCharacter() < fontcache[0].length)) {
			fontcache[glyph.getFont()][glyph.getCharacter()] = glyph;
		} else {
			throw new RdesktopException("Could not put font");
		}
	}

	/**
	 * Update the persistent bitmap cache MRU information on exit
	 */
	public void saveState() {
//		int id, idx;
//
//		for (id = 0; id < bitmapcache.length; id++)
//			if (PstCache.IS_PERSISTENT(id))
//				for (idx = 0; idx < bitmapcache[id].length; idx++)
//					PstCache.touchBitmap(id, idx, bitmapcache[id][idx].usage);
	}

	/**
	 * Retrieve a Glyph for a specified character in a specified font
	 * 
	 * @param font
	 *            ID of desired font for Glyph
	 * @param character
	 *            ID of desired character
	 * @return Glyph representing character in font
	 * @throws RdesktopException
	 */
	public Glyph getFont(int font, int character) throws RdesktopException {
       
		if ((font < fontcache.length) && (character < fontcache[0].length)) {
			Glyph glyph = fontcache[font][character];
			if (glyph != null) {
				
				return glyph;
			}
		}
		throw new RdesktopException("Could not get Font:" + font + ", "
				+ character);
	}

	/**
	 * Retrieve text stored in the cache
	 * 
	 * @param cache_id
	 *            ID of cache containing text
	 * @return Text stored in specified cache, represented as a DataBlob
	 * @throws RdesktopException
	 */
	public DataBlob getText(int cache_id) throws RdesktopException {
		DataBlob entry = null;
		if (cache_id < textcache.length) {
			entry = textcache[cache_id];
			if (entry != null) {
				if (entry.getData() != null) {
					return entry;
				}
			}
		}

		throw new RdesktopException("Could not get Text:" + cache_id);
	}

	/**
	 * Store text in the cache
	 * 
	 * @param cache_id
	 *            ID of cache in which to store the text
	 * @param entry
	 *            DataBlob representing the text to be stored
	 * @throws RdesktopException
	 */
	public void putText(int cache_id, DataBlob entry) throws RdesktopException {
		if (cache_id < textcache.length) {
			textcache[cache_id] = entry;
		} else {
			throw new RdesktopException("Could not put Text");
		}
	}

	/**
	 * Store an image in the desktop cache
	 * 
	 * @param offset
	 *            Location in desktop cache to begin storage of supplied data
	 * @param cx
	 *            Width of image to store
	 * @param cy
	 *            Height of image to store
	 * @param data
	 *            Array of integer pixel values representing image to be stored
	 * @throws RdesktopException
	 */
	public void putDesktop(int offset, int cx, int cy, int[] data)
			throws RdesktopException {

		int length = cx * cy;
		int pdata = 0;

		if (offset > highdeskcache.length)
			offset = 0;

		if ((int) offset + length <= highdeskcache.length) {
			for (int i = 0; i < cy; i++) {
				System.arraycopy(data, pdata, highdeskcache, offset, cx);
				offset += cx;
				pdata += cx;
			}
		} else {
			throw new RdesktopException("Could not put Desktop");
		}
	}

	/**
	 * Retrieve an image from the desktop cache
	 * 
	 * @param offset
	 *            Offset of image data within desktop cache
	 * @param cx
	 *            Width of image
	 * @param cy
	 *            Height of image
	 * @return Integer pixel data for image requested
	 * @throws RdesktopException
	 */
	public int[] getDesktopInt(int offset, int cx, int cy)
			throws RdesktopException {
		int length = cx * cy;
		int pdata = 0;
		int[] data = new int[length];

		if (offset > highdeskcache.length)
			offset = 0;

		if ((int) offset + length <= highdeskcache.length) {
			for (int i = 0; i < cy; i++) {
				System.arraycopy(highdeskcache, offset, data, pdata, cx);
				offset += cx;
				pdata += cx;
			}
			return data;
		}
		throw new RdesktopException("Could not get Bitmap");
	}

}
