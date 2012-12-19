package com.asp.android.rdp.service;

import com.asp.android.rdp.core.Rdp;
import com.asp.android.rdp.server.RDPConnectionManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RDPService extends Service{
	
	private RDPConnectionManager rdpConnectionManager;
	
	public static final String INTENT_EXTRA_BINDER="binder";
	
	public static final String BINDER_RDP_CONNECTION_MANAGER="rdpconnectionmanager";
	@Override
	public void onCreate() {
		super.onCreate();
		rdpConnectionManager=new RDPConnectionManager(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return super.onStartCommand(intent, flags, startId);
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return rdpConnectionManager;
	}

}
