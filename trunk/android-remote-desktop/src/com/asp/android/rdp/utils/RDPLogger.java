package com.asp.android.rdp.utils;

import android.util.Log;

public class RDPLogger {
	
	public static final String RDP_LOG_TAG="ANDROID_RDP";

	public static void d(String message){
		Log.d(RDP_LOG_TAG,message);
	}
	
	public static void e(String message){
		Log.e(RDP_LOG_TAG,message);
	}
	
	public static void e(String message,Exception e){
		Log.e(RDP_LOG_TAG,message,e);
	}
}
