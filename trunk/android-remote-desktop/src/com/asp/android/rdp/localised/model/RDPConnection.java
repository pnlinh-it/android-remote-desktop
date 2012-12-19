package com.asp.android.rdp.localised.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.asp.android.rdp.core.BackstoreManager;
import com.asp.android.rdp.core.ConnectionException;
import com.asp.android.rdp.core.MCS;
import com.asp.android.rdp.core.Options;
import com.asp.android.rdp.core.OrderException;
import com.asp.android.rdp.core.RdesktopException;
import com.asp.android.rdp.core.Rdp5;
import com.asp.android.rdp.core.Secure;
import com.asp.android.rdp.core.VChannels;
import com.asp.android.rdp.crypto.CryptoException;
import com.asp.android.rdp.interfaces.IRDPConnectionListener;
import com.asp.android.rdp.interfaces.IRDPConnectionManager;
import com.asp.android.rdp.interfaces.IRDPConnectionMonitor;
import com.asp.android.rdp.server.DroidRDPBackstoreManager;
import com.asp.android.rdp.utils.RDPLogger;

/**
 * This class represents a RDP connection to a server.
 * @author Ajay
 *
 */
public class RDPConnection {

	private Rdp5 rdp;
	private Secure secure;
	private Options options;
	private BackstoreManager backstoreManager;
	private IRDPConnectionMonitor connectionMonitor;

	public BackstoreManager getBackstoreManager() {
		return backstoreManager;
	}

	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	private MCS mcs;
	private RDPCommunicationThread communicationThread;

	public static final int ERROR_CODE_UNKNOWN_HOST = 1;

	public static final int ERROR_CODE_CONNECTION_ERROR = 2;
	
	
	
	public static final int RDP_CONNECTION_STATE_NOT_CONNECTED=0;
	
	public static final int RDP_CONNECTION_STATE_CONNECTED=1;
	
	public static final int RDP_CONNECTION_STATE_RUNNING=2;
	
	public static final int RDP_CONNECTION_STATE_PAUSED=3;
	
	public static final int RDP_CONNECTION_STATE_STOPPED=4;
	
	
	private int rdpConnectionState=RDP_CONNECTION_STATE_NOT_CONNECTED;

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

	private Context context;

	private IRDPConnectionManager connectionManager;
	
	/**
	 * Creating a RdpConnection.
	 * @param context
	 * @param connectionManager
	 */
	public RDPConnection(Context context,IRDPConnectionManager connectionManager) {
		this.context = context;
		this.connectionManager=connectionManager;
	}

	

	/**
	 * Initialize the options.
	 * @param screenResolution
	 * @param colorDepth
	 * @param performanceFlags
	 */
	private void initializeOptions(String screenResolution, int colorDepth,int performanceFlags) {
		options = new Options();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		if(screenResolution!=null){
			if(screenResolution.startsWith("Fit to the screen")){
				options.width =displayMetrics.widthPixels;
				options.height =displayMetrics.heightPixels;
			}else{
				String[] parts=screenResolution.split("X");
				
				int width=Integer.parseInt(parts[0]);
				int height=Integer.parseInt(parts[1]);
			
				options.width=width;
				options.height=height;
			}
			options.rdp5_performanceflags|=performanceFlags;
		}
		
	}

	public boolean connect(String host, String domain, String directory,
			int flags,int performanceFlags, String userName, String password, String resolution,
			int colorDepth, IRDPConnectionListener connectionListener) {
		initializeOptions(resolution, colorDepth,performanceFlags);
		VChannels channels = new VChannels(this);
		RDPLogger.d("Connecting to host"+host);
		try {
			RDPLogger.d("HOST :"+InetAddress.getAllByName(host)[0].toString());
		} catch (UnknownHostException e1) {
			RDPLogger.e("Unknown host",e1);
		}
		rdp = new Rdp5(this, channels);
		try {
			rdp.connect(userName, InetAddress.getByName(host), flags, domain,
					password, "", directory);
		} catch (UnknownHostException e) {
			connectionListener.connectionFailed(ERROR_CODE_UNKNOWN_HOST);
			return false;
		} catch (ConnectionException e) {
			connectionListener.connectionFailed(ERROR_CODE_CONNECTION_ERROR);
			return false;
		}
		backstoreManager = new DroidRDPBackstoreManager(this);
		rdp.registerBackstoreManager(backstoreManager);
		rdpConnectionState=RDP_CONNECTION_STATE_CONNECTED;
		return true;
	}

	public void start() {
        if(rdpConnectionState==RDP_CONNECTION_STATE_CONNECTED){
		communicationThread = new RDPCommunicationThread();
		communicationThread.start();
		rdpConnectionState=RDP_CONNECTION_STATE_RUNNING;
        }else if(rdpConnectionState==RDP_CONNECTION_STATE_PAUSED){
        	resume();
        }
	}
 
	boolean forceDisconnect=false;
	public void disConnect() {
		forceDisconnect=true;
		if (rdp != null) {
			rdp.disconnect();
		}
		
		
	}
	
	public void pause(){
		rdp.pause();
		rdpConnectionState=RDP_CONNECTION_STATE_PAUSED;
	}
	
	public void resume(){
		if(rdpConnectionState==RDP_CONNECTION_STATE_PAUSED){
		rdp.resume();
		rdpConnectionState=RDP_CONNECTION_STATE_RUNNING;
		}		
	}

	class RDPCommunicationThread extends Thread {

		boolean stopped=false;
		@Override
		public void run() {
			this.setName("MAINLOOPTHREAD");
			if (rdp != null) {
				boolean[] deactivated = new boolean[1];
				int[] ext_disc_reason = new int[1];
				try {
					rdp.mainLoop(deactivated, ext_disc_reason);
					exit();
				} catch (IOException e) {

				} catch (RdesktopException e) {

				} catch (OrderException e) {

				} catch (CryptoException e) {

				} catch (ConnectionException e) {

				}
			}
		}

	}
	
	public void setRDPConnectionMonitor(IRDPConnectionMonitor connectionMonitor){
		this.connectionMonitor=connectionMonitor;
	}
	
	private void exit(){
		if(connectionMonitor!=null && !forceDisconnect){
			connectionMonitor.diconnected(0,"");
		}
		
		if(connectionManager!=null){
			
		}
	}
	
}
