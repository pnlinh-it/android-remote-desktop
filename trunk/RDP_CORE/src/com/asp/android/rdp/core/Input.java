package com.asp.android.rdp.core;

import java.util.Vector;

import com.asp.android.rdp.core.model.RDPConnection;





public class Input {

	float touchPositionX = 0;
	float touchpositionY = 0;

	protected static boolean capsLockOn = false;

	protected static boolean numLockOn = false;

	protected static boolean scrollLockOn = false;

	protected static boolean serverAltDown = false;

	protected static boolean altDown = false;

	protected static boolean ctrlDown = false;

	protected static long last_mousemove = 0;

	// Using this flag value (0x0001) seems to do nothing, and after running
	// through other possible values, the RIGHT flag does not appear to be
	// implemented
	protected static final int KBD_FLAG_RIGHT = 0x0001;

	protected static final int KBD_FLAG_EXT = 0x0100;

	// QUIET flag is actually as below (not 0x1000 as in rdesktop)
	protected static final int KBD_FLAG_QUIET = 0x200;

	protected static final int KBD_FLAG_DOWN = 0x4000;

	protected static final int KBD_FLAG_UP = 0x8000;

	protected static final int RDP_KEYPRESS = 0;

	protected static final int RDP_KEYRELEASE = KBD_FLAG_DOWN | KBD_FLAG_UP;

	protected static final int MOUSE_FLAG_MOVE = 0x0800;

	public static final int MOUSE_LEFT_BUTTON = 0x1000;

	public static final int MOUSE_RIGHT_BUTTON = 0x2000;

	public static final int MOUSE_MIDDLE_BUTTON = 0x4000;

	protected static final int MOUSE_FLAG_BUTTON4 = 0x0280; // wheel up -

	// rdesktop 1.2.0
	protected static final int MOUSE_FLAG_BUTTON5 = 0x0380; // wheel down -

	// rdesktop 1.2.0
	protected static final int MOUSE_FLAG_DOWN = 0x8000;

	protected static final int RDP_INPUT_SYNCHRONIZE = 0;

	protected static final int RDP_INPUT_CODEPOINT = 1;

	protected static final int RDP_INPUT_VIRTKEY = 2;

	protected static final int RDP_INPUT_SCANCODE = 4;

	protected static final int RDP_INPUT_MOUSE = 0x8001;

	public boolean modifiersValid = false;

	public boolean keyDownWindows = false;

	protected RdesktopCanvas canvas = null;
	private RDPConnection rdpConnection;

	boolean leftClick = true;
	boolean rightClick = false;

	public Input(RDPConnection rdpConnection) {
		this.rdpConnection = rdpConnection;
	}

	protected static int time = 0;

	private static int getTime() {
		time++;
		if (time == Integer.MAX_VALUE)
			time = 1;
		return time;
	}
	
	private int lastKnownX=-1;
	
	public int getLastKnownX() {
		return lastKnownX;
	}

	
	public int getLastKnownY() {
		return lastKnownY;
	}

	

	private int lastKnownY=-1;

	public void mousePressed(int x, int y, int button) {
		int time = getTime();
		rdpConnection.getRdp().sendInput(time, RDP_INPUT_MOUSE,
				button | MOUSE_FLAG_DOWN, x, y);
	}

	public void mouseReleased(int x, int y, int button) {
		int time = getTime();
		lastKnownX=x;
		lastKnownY=y;
		rdpConnection.getRdp().sendInput(time, RDP_INPUT_MOUSE, button, x, y);
	}

	public void mousedMoved(int x, int y) {
		int time = getTime();
		rdpConnection.getRdp().sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE,
				x, y);
	}

	public void mouseDragged(int x, int y) {
		int time = getTime();
		rdpConnection.getRdp().sendInput(time, RDP_INPUT_MOUSE, MOUSE_FLAG_MOVE,
				x, y);
	}

}
