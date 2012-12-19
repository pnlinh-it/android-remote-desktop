package com.asp.android.rdp.ui;

import com.asp.android.rdp.R;
import com.asp.android.rdp.core.Options;
import com.asp.android.rdp.core.Rdp;
import com.asp.android.rdp.interfaces.IRDPConnectionListener;
import com.asp.android.rdp.localised.model.RDPConnection;
import com.asp.android.rdp.server.RDPConnectionManager;
import com.asp.android.rdp.service.RDPService;
import com.asp.android.rdp.utils.RDPLogger;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private EditText editTxtIpAddress;
	private EditText editTxtUsername;
	private EditText editTxtPassword;
	private EditText editTxtDomain;
	private EditText editTxtDirectory;

	private CheckBox checkBxAutomaticLogin;
	private CheckBox showWallPaper;
	private CheckBox showThemes;

	private Spinner spinnerScreenResolution;
	private Spinner spinnerColorDepth;

	private Handler handler;

	private RDPConnectionManager connectionmanager;

	private Button btnConnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		startRDPService();
		setContentView(R.layout.settings_activity_layout);
		editTxtIpAddress = (EditText) findViewById(R.id.edit_txt_ip_address);
		checkBxAutomaticLogin = (CheckBox) findViewById(R.id.check_bx_login_automatically);
		showThemes = (CheckBox) findViewById(R.id.check_bx_show_themes);
		showWallPaper = (CheckBox) findViewById(R.id.check_bx_show_background);
		btnConnect = (Button) findViewById(R.id.btn_connect);
		checkBxAutomaticLogin
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							ViewGroup automaticLoginLyt = (ViewGroup) findViewById(R.id.lyt_automatic_login_layout);
							LayoutInflater.from(SettingsActivity.this).inflate(
									R.layout.username_password_layout,
									automaticLoginLyt, true);
							editTxtUsername = (EditText) findViewById(R.id.edit_txt_user_name);
							editTxtPassword = (EditText) findViewById(R.id.edit_txt_password);
						} else {
							ViewGroup automaticLoginLyt = (ViewGroup) findViewById(R.id.lyt_automatic_login_layout);
							View lytUsernamePassword = automaticLoginLyt
									.findViewById(R.id.lyt_user_name_password);
							if (lytUsernamePassword != null) {
								automaticLoginLyt
										.removeView(lytUsernamePassword);
							}
						}
					}
				});

		btnConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editTxtIpAddress.getText().toString().equals("")) {
					Bundle extras = new Bundle();
					extras.putString(EXTRA_DIALOG_MESSAGE,
							getString(R.string.enter_ip_address));
					showDialog(INFO, extras);
				} else {
					showDialog(CONNECTING);
					String userName = "";
					String password = "";
					int logonflags = com.asp.android.rdp.core.Rdp.RDP_LOGON_NORMAL;
					if (checkBxAutomaticLogin.isChecked()) {
						logonflags |= Rdp.RDP_LOGON_AUTO;
						logonflags|=Rdp.INFO_REMOTECONSOLEAUDIO;
						userName = editTxtUsername.getText().toString();
						password = editTxtPassword.getText().toString();
					}
					if (connectionmanager != null) {
						String selectedResolution = (String) spinnerScreenResolution
								.getSelectedItem();
						RDPLogger
								.d("Selected Resolution:" + selectedResolution);
						int selectedDepth = spinnerColorDepth
								.getSelectedItemPosition();
						int performanceFlags = 0;

						if (!showThemes.isChecked()) {
							performanceFlags = Rdp.RDP5_NO_THEMING;
						}
						if (!showWallPaper.isChecked()) {
							if (performanceFlags == Rdp.RDP5_NO_THEMING) {
								performanceFlags |= Rdp.RDP5_NO_WALLPAPER;
							} else {
								performanceFlags = Rdp.RDP5_NO_WALLPAPER;
							}
						}
						connectionmanager.connect(editTxtIpAddress.getText()
								.toString(), "", "", logonflags,
								performanceFlags, userName, password,
								selectedResolution, selectedDepth,
								new IRDPConnectionListener() {

									public void connectionSucceeded(int id) {
										dismissDialog(CONNECTING);
										removeDialog(CONNECTING);
										try {
											unbindService(rdpServiceConnection);
										} catch (Exception e) {

										}
										Intent intent = new Intent(
												SettingsActivity.this,
												RDesktopActivity.class);
										intent.putExtra(
												RDesktopActivity.EXTRA_RDP_ID,
												id);
										startActivity(intent);

									}

									public void connectionFailed(int errorCode) {
										dismissDialog(CONNECTING);
										removeDialog(CONNECTING);
										final Bundle extras = new Bundle();
										switch (errorCode) {
										case RDPConnection.ERROR_CODE_UNKNOWN_HOST:
											extras.putString(
													EXTRA_DIALOG_MESSAGE,
													getString(R.string.error_connecting_unknown_host));
											break;
										case RDPConnection.ERROR_CODE_CONNECTION_ERROR:
											extras.putString(
													EXTRA_DIALOG_MESSAGE,
													getString(R.string.error_connecting_connection_error));
											break;
										}
										handler.post(new Runnable() {

											public void run() {
												showDialog(ERROR, extras);
											}
										});

									}
								});
					}
				}
			}
		});
		spinnerScreenResolution = (Spinner) findViewById(R.id.spinner_screen_resolution);
		spinnerColorDepth = (Spinner) findViewById(R.id.spinner_color_depth);

		ArrayAdapter<CharSequence> resolutionAdapter = ArrayAdapter
				.createFromResource(this, R.array.resolution,
						android.R.layout.simple_spinner_item);
		resolutionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerScreenResolution.setAdapter(resolutionAdapter);

		ArrayAdapter<CharSequence> colorDepthAdapter = ArrayAdapter
				.createFromResource(this, R.array.color_depth,
						android.R.layout.simple_spinner_item);
		colorDepthAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerColorDepth.setAdapter(colorDepthAdapter);
	}

	private static final int INFO = 1;

	private static final int ERROR = 2;

	private static final int CONNECTING = 3;

	private static final String EXTRA_DIALOG_MESSAGE = "dialog_message";

	@Override
	protected Dialog onCreateDialog(final int id, Bundle args) {
		String message = null;
		if (args != null) {
			message = args.getString(EXTRA_DIALOG_MESSAGE);
		}
		Builder builder = new Builder(this);
		switch (id) {
		case INFO:
			builder.setPositiveButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							removeDialog(id);
						}
					});
			builder.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					removeDialog(id);
				}
			});
			builder.setTitle(R.string.info);
			builder.setMessage(message);
			return builder.create();
		case ERROR:
			builder.setPositiveButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							removeDialog(id);
						}
					});
			builder.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					removeDialog(id);
				}
			});
			builder.setTitle(R.string.error);
			builder.setMessage(message);
			return builder.create();
		case CONNECTING:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setMessage(getString(R.string.connecting));
			return progressDialog;
		}
		return super.onCreateDialog(id, args);
	}

	ServiceConnection rdpServiceConnection;

	private void startRDPService() {

		Intent serviceIntent = new Intent(this, RDPService.class);
		startService(serviceIntent);

		rdpServiceConnection = new ServiceConnection() {

			public void onServiceDisconnected(ComponentName name) {
				connectionmanager = null;
			}

			public void onServiceConnected(ComponentName name, IBinder service) {
				connectionmanager = (RDPConnectionManager) service;
			}
		};

		Intent intent = new Intent(this, RDPService.class);
		intent.putExtra(RDPService.INTENT_EXTRA_BINDER,
				RDPService.BINDER_RDP_CONNECTION_MANAGER);
		bindService(intent, rdpServiceConnection, BIND_AUTO_CREATE);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return super.onCreateOptionsMenu(menu);
	}
	
	
}
