package com.asp.android.rdp.interfaces;

import android.os.IBinder;

public interface IRDPConnectionListener {

	public void connectionFailed(int error_code);
	
	public void connectionSucceeded(int id);
	
	
}
