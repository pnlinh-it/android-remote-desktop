package com.asp.android.rdp.core.model;

import com.asp.android.rdp.core.BackstoreManager;
import com.asp.android.rdp.core.MCS;
import com.asp.android.rdp.core.Options;
import com.asp.android.rdp.core.Rdp5;
import com.asp.android.rdp.core.Secure;




public abstract class RDPConnection {

	protected Rdp5 rdp;
	protected Secure secure;
	protected Options options;
	protected BackstoreManager backstoreManager;
	

	public BackstoreManager getBackstoreManager() {
		return backstoreManager;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	protected MCS mcs;

	public static final int ERROR_CODE_UNKNOWN_HOST = 1;

	public static final int ERROR_CODE_CONNECTION_ERROR = 2;
	
	
	
	public static final int RDP_CONNECTION_STATE_NOT_CONNECTED=0;
	
	public static final int RDP_CONNECTION_STATE_CONNECTED=1;
	
	public static final int RDP_CONNECTION_STATE_RUNNING=2;
	
	public static final int RDP_CONNECTION_STATE_PAUSED=3;
	
	public static final int RDP_CONNECTION_STATE_STOPPED=4;
	
	
	protected int rdpConnectionState=RDP_CONNECTION_STATE_NOT_CONNECTED;

	public MCS getMcs() {
		return mcs;
	}

	public void setMcs(MCS mcs) {
		this.mcs = mcs;
	}

	public Secure getSecure() {
		return secure;
	}

	public void setSecure(Secure secure) {
		this.secure = secure;
	}

	public Rdp5 getRdp() {
		return rdp;
	}
	
}
