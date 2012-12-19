package com.asp.android.rdp.ui;

import com.asp.android.rdp.R;
import com.asp.android.rdp.core.RdesktopCanvas;
import com.asp.android.rdp.interfaces.IRDPConnectionMonitor;
import com.asp.android.rdp.localised.model.RDPConnection;
import com.asp.android.rdp.localised.view.DroidRDesktopCanvas;
import com.asp.android.rdp.server.RDPConnectionManager;
import com.asp.android.rdp.service.RDPService;
import com.asp.android.rdp.utils.RDPLogger;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class RDesktopActivity extends Activity implements IRDPConnectionMonitor{

	public static final String EXTRA_RDP_ID = "rdp_id";

	private RDPConnectionManager rdpConnectionManager;
	private RDPConnection rdpConnection;
	private DroidRDesktopCanvas canvas;
	private int rdpConnectionId=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.rdp_activity_layout);
		rdpConnectionId = getIntent().getIntExtra(EXTRA_RDP_ID, -1);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		connectToService(rdpConnectionId);
	}
	
	

	@Override
	protected void onPause() {
		super.onPause();
		rdpConnection.pause();
		disconnectService();
	}



	private static final int RDP_CONNECTION_ERROR = 1;

	

	RDPServiceConnection rdpServiceConnection;
	private void connectToService(int rdpId) {
		Intent intent = new Intent(this, RDPService.class);
		rdpServiceConnection=new RDPServiceConnection(rdpId);
		bindService(intent,rdpServiceConnection, BIND_AUTO_CREATE);
	}
	
	private void disconnectService(){
		if(rdpServiceConnection!=null){
			unbindService(rdpServiceConnection);
		}
	}

	class RDPServiceConnection implements ServiceConnection {

		private int id = -1;

		public RDPServiceConnection(int id) {
			this.id = id;
		}

		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			rdpConnectionManager = (RDPConnectionManager) arg1;
			rdpConnection = rdpConnectionManager.getRDPConnection(id);
			if(rdpConnection!=null){
				rdpConnection.setRDPConnectionMonitor(RDesktopActivity.this);
			canvas = (DroidRDesktopCanvas) findViewById(R.id.surface_rdp);
			canvas.initialize(rdpConnection, RDesktopActivity.this,
					getApplicationContext());
			}
		}

		public void onServiceDisconnected(ComponentName arg0) {
			RDPLogger.d("Service disconnected");
			rdpConnectionManager = null;
		}

	}

	private Handler handler;

	public Handler getUIHandler() {
		return this.handler;
	}

	@Override
	public void onBackPressed() {
		if(rdpConnectionId!=-1){
			showDialog(END_SESSION_DIALOG);
			return;
		}
		super.onBackPressed();
	}

	private static final int END_SESSION_DIALOG=1;
	private static final int DIALOG_DISCONNECTED=2;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case END_SESSION_DIALOG:{
			Builder builder=new Builder(this);
			builder.setTitle(R.string.end_session_title);
			builder.setMessage(R.string.end_session_message);
			builder.setCancelable(true);
			builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface arg0, int arg1) {
					if(rdpConnectionManager!=null){
						RDPConnection rdpConnection=rdpConnectionManager.getRDPConnection(rdpConnectionId);
						rdpConnection.disConnect();
						finish();
					}
				}
			});
			builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return builder.create();
		}
		case DIALOG_DISCONNECTED:{
			Builder builder=new Builder(this);
			builder.setTitle(R.string.disconnected_title);
			builder.setMessage(R.string.disconnected_message);
			builder.setCancelable(false);
			builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int arg1) {
					dialog.dismiss();
					finish();
				}
			});
			return builder.create();
		}

		default:
			break;
		}
		return super.onCreateDialog(id);
	}
	
	
    
	public void diconnected(int error_code, String summary) {
		handler.post(new Runnable() {
			
			public void run() {
				showDialog(DIALOG_DISCONNECTED);
			}
		});
		
	}
	
	
	// Callback for key presses on the soft keyboard.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getUnicodeChar()==0){
			canvas.sendUnicode((char)8);
		}else{
		canvas.sendUnicode((char) event.getUnicodeChar());
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(canvas,InputMethodManager.SHOW_IMPLICIT);
		return super.onPrepareOptionsMenu(menu);
	}

}
