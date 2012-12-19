package com.asp.android.rdp.localised.view;

import com.asp.android.rdp.core.RdesktopCanvas;
import com.asp.android.rdp.utils.RDPLogger;



public class RDPScaleGestureListener implements OnScaleGestureListener {

	private RdesktopCanvas canvas;

	public RDPScaleGestureListener(RdesktopCanvas canvas) {
		this.canvas = canvas;
	}

	float mScaleFactor = 1.0f;

	public boolean onScale(IBCScaleGestureDetector detector) {
		mScaleFactor *= detector.getScaleFactor();
		mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 5.0f));
		RDPLogger.d("Scaling factor :"+mScaleFactor);
		canvas.setScalingFactor(mScaleFactor);
		return true;

	}

	public boolean onScaleBegin(IBCScaleGestureDetector detector) {
		canvas.scalingInProgress(true);
		return true;
	}

	public void onScaleEnd(IBCScaleGestureDetector detector) {
		canvas.scalingInProgress(false);
	}

}
