/* Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * MODIFIED FOR ANTLERSOFT
 *
 * Changes for antlersoft/ vnc viewer for android
 *
 * Copyright (C) 2010 Michael A. MacDonald
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */
package com.asp.android.rdp.localised.view;

import android.view.MotionEvent;

/**
 * Backwards-compatibility interface to the android.view.ScaleGestureDetector
 * introduced in Android SDK 8.
 * 
 * This will be a working implementation of devices with SDK >= 5 (since I
 * backported ScaleGestureDetector to 5) and a dummy implementation for older
 * devices.
 * 
 * @author Michael A. MacDonald
 * 
 */
public interface IBCScaleGestureDetector {

	public abstract boolean onTouchEvent(MotionEvent event);

	public abstract boolean isInProgress();

	public abstract float getFocusX();

	public abstract float getFocusY();

	public abstract float getCurrentSpan();

	public abstract float getPreviousSpan();

	public abstract float getScaleFactor();

	public abstract long getTimeDelta();

	public abstract long getEventTime();

}