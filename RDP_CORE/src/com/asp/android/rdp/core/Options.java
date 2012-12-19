/* Options.java
 * Component: ProperJavaRDP
 * 
 * Revision: $Revision: 25 $
 * Author: $Author: miha_vitorovic $
 * Date: $Date: 2008-01-25 16:56:00 +0530 (Fri, 25 Jan 2008) $
 *
 * Copyright (c) 2005 Propero Limited
 *
 * Purpose: Global static storage of user-definable options
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


public class Options {

	public static final int DIRECT_BITMAP_DECOMPRESSION = 0;

	public static final int BUFFEREDIMAGE_BITMAP_DECOMPRESSION = 1;

	public static final int INTEGER_BITMAP_DECOMPRESSION = 2;

	public static int bitmap_decompression_store = INTEGER_BITMAP_DECOMPRESSION;

	// disables bandwidth saving tcp packets
	public  boolean low_latency = true;

	public  int keylayout = 0x809; // UK by default

	public  String username = "Administrator"; // -u username

	public  String domain = ""; // -d domain

	public  String password = ""; // -p password

	public  String hostname = ""; // -n hostname

	public  String command = ""; // -s command

	public  String directory = ""; // -d directory

	public  String windowTitle = "properJavaRDP"; // -T windowTitle
	
	public static final int WIDTH_CONSTANT=800;
	
	public static final int HEIGHT_CONSTANT=600;

	public  int width = 400; // -g widthxheight

	public  int height = 240; // -g widthxheight

	public  int port = 3389; // -t port

	public  boolean fullscreen = false;

	public  boolean built_in_licence = false;

	public  boolean load_licence = false;

	public  boolean save_licence = false;

	public  String licence_path = "./";

	public  boolean debug_keyboard = false;

	public  boolean debug_hexdump = false;

	public  boolean enable_menu = false;

	// public static boolean paste_hack = true;

	public  boolean altkey_quiet = false;

	public  boolean caps_sends_up_and_down = true;

	public  boolean remap_hash = true;

	public  boolean useLockingKeyState = true;

	public  boolean use_rdp5 = true;

	public  int server_bpp = 24; // Bits per pixel

	public  int Bpp = (server_bpp + 7) / 8; // Bytes per pixel

	// Correction value to ensure only the relevant number of bytes are used for
	// a pixel
	public  int bpp_mask = 0xFFFFFF >> 8 * (3 - Bpp);

	public  int imgCount = 0;

	

	/**
	 * Set a new value for the server's bits per pixel
	 * 
	 * @param server_bpp
	 *            New bpp value
	 */
	public  void set_bpp(int server_bpp) {
		this.server_bpp = server_bpp;
		Bpp = (server_bpp + 7) / 8;
		if (server_bpp == 8)
			bpp_mask = 0xFF;
		else
			bpp_mask = 0xFFFFFF;

	}

	public  int server_rdp_version;

	public  int win_button_size = 0; /* If zero, disable single app mode */

	public  boolean bitmap_compression = true;

	public  boolean persistent_bitmap_caching = false;

	public  boolean bitmap_caching = false;

	public  boolean precache_bitmaps = false;

	public  boolean polygon_ellipse_orders = false;

	public  boolean sendmotion = true;

	public  boolean orders = true;

	public  boolean encryption = true;

	public  boolean packet_encryption = true;

	public  boolean desktop_save = true;

	public  boolean grab_keyboard = true;

	public  boolean hide_decorations = false;

	public  boolean console_session = false;

	public  boolean owncolmap;

	public  boolean use_ssl = false;

	public  boolean map_clipboard = true;

	public  int rdp5_performanceflags = Rdp.RDP5_NO_CURSOR_SHADOW
			| Rdp.RDP5_NO_CURSORSETTINGS | Rdp.RDP5_NO_FULLWINDOWDRAG
			| Rdp.RDP5_NO_MENUANIMATIONS;

	public  boolean save_graphics = false;



	public boolean isLow_latency() {
		return low_latency;
	}

	public void setLow_latency(boolean lowLatency) {
		low_latency = lowLatency;
	}

	public int getKeylayout() {
		return keylayout;
	}

	public void setKeylayout(int keylayout) {
		this.keylayout = keylayout;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	public boolean isBuilt_in_licence() {
		return built_in_licence;
	}

	public void setBuilt_in_licence(boolean builtInLicence) {
		built_in_licence = builtInLicence;
	}

	public boolean isLoad_licence() {
		return load_licence;
	}

	public void setLoad_licence(boolean loadLicence) {
		load_licence = loadLicence;
	}

	public boolean isSave_licence() {
		return save_licence;
	}

	public void setSave_licence(boolean saveLicence) {
		save_licence = saveLicence;
	}

	public String getLicence_path() {
		return licence_path;
	}

	public void setLicence_path(String licencePath) {
		licence_path = licencePath;
	}

	public boolean isDebug_keyboard() {
		return debug_keyboard;
	}

	public void setDebug_keyboard(boolean debugKeyboard) {
		debug_keyboard = debugKeyboard;
	}

	public boolean isDebug_hexdump() {
		return debug_hexdump;
	}

	public void setDebug_hexdump(boolean debugHexdump) {
		debug_hexdump = debugHexdump;
	}

	public boolean isEnable_menu() {
		return enable_menu;
	}

	public void setEnable_menu(boolean enableMenu) {
		enable_menu = enableMenu;
	}

	public boolean isAltkey_quiet() {
		return altkey_quiet;
	}

	public void setAltkey_quiet(boolean altkeyQuiet) {
		altkey_quiet = altkeyQuiet;
	}

	public boolean isCaps_sends_up_and_down() {
		return caps_sends_up_and_down;
	}

	public void setCaps_sends_up_and_down(boolean capsSendsUpAndDown) {
		caps_sends_up_and_down = capsSendsUpAndDown;
	}

	public boolean isRemap_hash() {
		return remap_hash;
	}

	public void setRemap_hash(boolean remapHash) {
		remap_hash = remapHash;
	}

	public boolean isUseLockingKeyState() {
		return useLockingKeyState;
	}

	public void setUseLockingKeyState(boolean useLockingKeyState) {
		this.useLockingKeyState = useLockingKeyState;
	}

	public boolean isUse_rdp5() {
		return use_rdp5;
	}

	public void setUse_rdp5(boolean useRdp5) {
		use_rdp5 = useRdp5;
	}

	public int getServer_bpp() {
		return server_bpp;
	}

	public void setServer_bpp(int serverBpp) {
		server_bpp = serverBpp;
	}

	public int getBpp() {
		return Bpp;
	}

	public void setBpp(int bpp) {
		Bpp = bpp;
	}

	public int getBpp_mask() {
		return bpp_mask;
	}

	public void setBpp_mask(int bppMask) {
		bpp_mask = bppMask;
	}

	public int getImgCount() {
		return imgCount;
	}

	public void setImgCount(int imgCount) {
		this.imgCount = imgCount;
	}

	public int getServer_rdp_version() {
		return server_rdp_version;
	}

	public void setServer_rdp_version(int serverRdpVersion) {
		server_rdp_version = serverRdpVersion;
	}

	public int getWin_button_size() {
		return win_button_size;
	}

	public void setWin_button_size(int winButtonSize) {
		win_button_size = winButtonSize;
	}

	public boolean isBitmap_compression() {
		return bitmap_compression;
	}

	public void setBitmap_compression(boolean bitmapCompression) {
		bitmap_compression = bitmapCompression;
	}

	public boolean isPersistent_bitmap_caching() {
		return persistent_bitmap_caching;
	}

	public void setPersistent_bitmap_caching(boolean persistentBitmapCaching) {
		persistent_bitmap_caching = persistentBitmapCaching;
	}

	public boolean isBitmap_caching() {
		return bitmap_caching;
	}

	public void setBitmap_caching(boolean bitmapCaching) {
		bitmap_caching = bitmapCaching;
	}

	public boolean isPrecache_bitmaps() {
		return precache_bitmaps;
	}

	public void setPrecache_bitmaps(boolean precacheBitmaps) {
		precache_bitmaps = precacheBitmaps;
	}

	public boolean isPolygon_ellipse_orders() {
		return polygon_ellipse_orders;
	}

	public void setPolygon_ellipse_orders(boolean polygonEllipseOrders) {
		polygon_ellipse_orders = polygonEllipseOrders;
	}

	public boolean isSendmotion() {
		return sendmotion;
	}

	public void setSendmotion(boolean sendmotion) {
		this.sendmotion = sendmotion;
	}

	public boolean isOrders() {
		return orders;
	}

	public void setOrders(boolean orders) {
		this.orders = orders;
	}

	public boolean isEncryption() {
		return encryption;
	}

	public void setEncryption(boolean encryption) {
		this.encryption = encryption;
	}

	public boolean isPacket_encryption() {
		return packet_encryption;
	}

	public void setPacket_encryption(boolean packetEncryption) {
		packet_encryption = packetEncryption;
	}

	public boolean isDesktop_save() {
		return desktop_save;
	}

	public void setDesktop_save(boolean desktopSave) {
		desktop_save = desktopSave;
	}

	public boolean isGrab_keyboard() {
		return grab_keyboard;
	}

	public void setGrab_keyboard(boolean grabKeyboard) {
		grab_keyboard = grabKeyboard;
	}

	public boolean isHide_decorations() {
		return hide_decorations;
	}

	public void setHide_decorations(boolean hideDecorations) {
		hide_decorations = hideDecorations;
	}

	public boolean isConsole_session() {
		return console_session;
	}

	public void setConsole_session(boolean consoleSession) {
		console_session = consoleSession;
	}

	public boolean isOwncolmap() {
		return owncolmap;
	}

	public void setOwncolmap(boolean owncolmap) {
		this.owncolmap = owncolmap;
	}

	public boolean isUse_ssl() {
		return use_ssl;
	}

	public void setUse_ssl(boolean useSsl) {
		use_ssl = useSsl;
	}

	public boolean isMap_clipboard() {
		return map_clipboard;
	}

	public void setMap_clipboard(boolean mapClipboard) {
		map_clipboard = mapClipboard;
	}

	public  int getRdp5_performanceflags() {
		return rdp5_performanceflags;
	}

	public  void setRdp5_performanceflags(int rdp5Performanceflags) {
		rdp5_performanceflags = rdp5Performanceflags;
	}

	public boolean isSave_graphics() {
		return save_graphics;
	}

	public void setSave_graphics(boolean saveGraphics) {
		save_graphics = saveGraphics;
	}

}
