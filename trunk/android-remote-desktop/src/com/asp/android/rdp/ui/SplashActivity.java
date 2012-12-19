package com.asp.android.rdp.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.asp.android.rdp.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashActivity extends Activity {
	
	Timer timer;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        timer=new Timer();
        timer.schedule(new TimerTask() {
			@Override
			public void run() {
				startApplication();
			}
		},2000);
    }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(timer!=null){
			timer.cancel();
			startApplication();
		}
		return super.onTouchEvent(event);
	}
	
	private void startApplication(){
		Intent intent=new Intent(this,SettingsActivity.class);
		startActivity(intent);
		this.finish();
    }
    
    
}