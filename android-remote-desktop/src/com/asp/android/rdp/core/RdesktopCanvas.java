package com.asp.android.rdp.core;

import com.asp.android.rdp.core.model.BoundsOrder;
import com.asp.android.rdp.core.model.Brush;
import com.asp.android.rdp.core.model.DestBltOrder;
import com.asp.android.rdp.core.model.LineOrder;
import com.asp.android.rdp.core.model.MemBltOrder;
import com.asp.android.rdp.core.model.PatBltOrder;
import com.asp.android.rdp.core.model.PolyLineOrder;
import com.asp.android.rdp.core.model.RectangleOrder;
import com.asp.android.rdp.core.model.ScreenBltOrder;
import com.asp.android.rdp.core.model.TriBltOrder;

public interface RdesktopCanvas {

	public abstract void repaint(int x, int y, int width, int height);

	public abstract void scalingInProgress(boolean scalingInProgress);

	public abstract void setScalingFactor(float scalingFactor);

	public abstract float getScalingFactor();
	
	public abstract void setOffsets(float x,float y);

}
