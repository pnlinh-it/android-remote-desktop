package com.asp.android.rdp.interfaces;

import com.asp.android.rdp.localised.model.RDPConnection;

import android.os.IBinder;

public interface IRDPConnectionManager {
	
	public void connect(String host,String domain,String directory,int flags,int performanceFlags,String userName,String password,String screenResolution,int colorDepth,IRDPConnectionListener connectionListener);
	
	public void endRDPSession(int id);
	
	public RDPConnection getRDPConnection(int id);

}
