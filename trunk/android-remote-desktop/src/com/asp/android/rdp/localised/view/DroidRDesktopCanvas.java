package com.asp.android.rdp.localised.view;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.asp.android.rdp.R;
import com.asp.android.rdp.core.Cache;
import com.asp.android.rdp.core.Input;
import com.asp.android.rdp.core.RasterOp;
import com.asp.android.rdp.core.RdesktopCanvas;
import com.asp.android.rdp.core.RdesktopException;
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
import com.asp.android.rdp.core.view.Bitmap;
import com.asp.android.rdp.core.view.WrappedImage;
import com.asp.android.rdp.localised.model.RDPConnection;
import com.asp.android.rdp.ui.RDesktopActivity;
import com.asp.android.rdp.utils.RDPLogger;

public class DroidRDesktopCanvas extends SurfaceView implements RdesktopCanvas,
		SurfaceHolder.Callback, OnTouchListener {

	WrappedImage backstore;

	DisplayMetrics dispMetrics;

	private int displayWidth = 0;
	private int displayHeight = 0;
	private float density;
	private RDPScaleGestureListener scaleListener;

	private RDPConnection rdpConnection;

	private Input input;

	public DroidRDesktopCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	private void intializeDisplay(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dispMetric = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dispMetric);
		displayWidth = dispMetric.widthPixels;
		RDPLogger.d("Resolution :" + displayWidth + "X" + displayHeight);
		displayHeight = dispMetric.heightPixels;
		density = dispMetric.density;// > 1 ? 1 : dispMetric.density;
	}

	private RDesktopActivity rdesktopActivity;
	private FrameLayout menuLayOut;

	private boolean initialized = false;

	public void initialize(RDPConnection rdpConnection,
			RDesktopActivity rdesktopActivity, Context applicationContext) {
		if (!initialized) {
			this.rdpConnection = rdpConnection;
			this.rdesktopActivity = rdesktopActivity;
			this.menuLayOut = (FrameLayout) rdesktopActivity
					.findViewById(R.id.lyt_menu);
			menuLayOut.setVisibility(View.INVISIBLE);
			this.setFocusableInTouchMode(true);
			input = new Input(rdpConnection);
			intializeDisplay(applicationContext);
			scaleListener = new RDPScaleGestureListener(this);
			this.setOnTouchListener(this);
			scaleDetector = new ScaleGestureDetector(applicationContext,
					scaleListener);
			getHolder().addCallback(this);
			initialized = true;
		}

	}

	Canvas canvas;

	public DroidRDesktopCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DroidRDesktopCanvas(Context context) {
		super(context);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	private float scrollX = 0;
	private float scrolY = 0;

	private float xOffset = 0;
	private float yOffset = 0;

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			rdpConnection.start();
		} catch (Exception e) {
		}

		isRendering = true;
		rdpConnection.getBackstoreManager().setRdesktopCanvas(this);
		backstore = rdpConnection.getBackstoreManager().getWrappedImage();
		repaint(0, 0, rdpConnection.getOptions().width,
				rdpConnection.getOptions().height);
	}

	private boolean isRendering = false;

	public void surfaceDestroyed(SurfaceHolder holder) {
		isRendering = false;
	}

	private float scalingFactor = 1.0f;

	public void repaint(int x, int y, int width, int height) {
		try {
			if (!isRendering || scalingInProgress || backstore == null) {
				return;
			}

			int drawingWidth = (int) (displayWidth / scalingFactor);
			int drawingHeight = (int) (displayHeight / scalingFactor);

			drawingWidth = (int) (backstore.getWidth() < ((xOffset + scrollX) + drawingWidth) ? (backstore
					.getWidth() - (xOffset + scrollX)) : drawingWidth);
			drawingHeight = (int) (backstore.getHeight() < drawingHeight
					+ (yOffset + scrolY) ? backstore.getHeight()
					- (yOffset + scrolY) : drawingHeight);

			if ((x + width) < (xOffset + scrollX)
					|| x > (xOffset + scrollX + drawingWidth)
					|| (y + height) < (yOffset + scrolY)
					|| y > (yOffset + scrolY + drawingHeight)) {
				RDPLogger.d("Out of display area");
				return;
			}

			int[] subImage = backstore.getSubImage((int) (xOffset + scrollX),
					(int) (yOffset + scrolY), drawingWidth, drawingHeight);
			canvas = getHolder().lockCanvas();
			if (canvas == null) { // Unable to acquire the Canvas.
				return;
			}
			Matrix matrix = new Matrix();
			matrix.postScale((float) (scalingFactor / density),
					(float) (scalingFactor / density));
			canvas.setMatrix(matrix);
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(subImage, 0, drawingWidth, 0, 0, drawingWidth,
					drawingHeight, false, new Paint());
			getHolder().unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			RDPLogger.e("Canvas", e);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();

	}

	private float startX = 0;
	private float startY = 0;

	private boolean multiTouch = false;

	public boolean onTouch(View v, MotionEvent event) {

		if (event.getPointerCount() == 1) {
			return onSingleTouch(event);
		} else {
			scaleDetector.onTouchEvent(event);
		}
		return true;
	}

	private int currentState = 0;

	private static final int CURRENT_STATE_TOUCH = 1;
	private static final int CURRENT_STATE_SCROLLING = 2;
	private static final int CURRENT_STATE_DRAG = 3;

	private Timer stateTimer;
	private Timer rightBtnClickTimer;
	private boolean rightClicked = false;
	int lastX = -1;
	int lastY = -1;
	private static final int TOUCH_EVENT_THRESHOLD = 10;

	private boolean onSingleTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			RDPLogger.d("Single touch down");
			rightClicked = false;
			currentState = CURRENT_STATE_TOUCH;
			stateTimer = new Timer();
			rightBtnClickTimer = new Timer();
			stateTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					currentState = CURRENT_STATE_DRAG;
					input.mousePressed(
							(int) (xOffset + (startX * density) / scalingFactor),
							(int) (yOffset + (startY * density) / scalingFactor),
							Input.MOUSE_LEFT_BUTTON);

				}
			}, 600);
			rightBtnClickTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					currentState = CURRENT_STATE_TOUCH;
					input.mousePressed(
							(int) (xOffset + (startX * density) / scalingFactor),
							(int) (yOffset + (startY * density) / scalingFactor),
							Input.MOUSE_RIGHT_BUTTON);
					input.mouseReleased(
							(int) (xOffset + (startX * density) / scalingFactor),
							(int) (yOffset + (startY * density) / scalingFactor),
							Input.MOUSE_RIGHT_BUTTON);
					rightClicked = true;
				}
			}, 1500);
			startX = event.getX();
			startY = event.getY();
			scrollX = 0;
			scrolY = 0;
		}

		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (currentState == CURRENT_STATE_TOUCH
					|| currentState == CURRENT_STATE_SCROLLING) {
				

				if ((xOffset + (startX - event.getX())) > 0
						&& !((backstore.getWidth() - (xOffset + (startX - event
								.getX()))) < (displayWidth / scalingFactor))) {
					scrollX = startX - event.getX();
				}

				if ((yOffset + (startY - event.getY())) > 0
						&& !((backstore.getHeight() - (yOffset + (startY - event
								.getY()))) < (displayHeight / scalingFactor))) {
					scrolY = startY - event.getY();
				}
				RDPLogger.d("Event move" + scrollX + "x" + scrolY);
				float xnormalizationFactor = (displayWidth/ 480) * 4;
				float ynormalizationFactor = (displayHeight / 800) *6 ;
				RDPLogger.d("Normalization factors"+xnormalizationFactor+"x"+ynormalizationFactor);

				if (Math.abs(scrollX) <= xnormalizationFactor
						&& Math.abs(scrolY) <= ynormalizationFactor) {
					currentState = CURRENT_STATE_TOUCH;
					return true; // If scroll is very less, ignore it.
				}
				currentState = CURRENT_STATE_SCROLLING;
				if (stateTimer != null) {
					stateTimer.cancel();
				}
				if (rightBtnClickTimer != null) {
					rightBtnClickTimer.cancel();
				}

				repaint(0, 0, rdpConnection.getOptions().width,
						rdpConnection.getOptions().height);
			} else if (currentState == CURRENT_STATE_DRAG) {
				
				float xnormalizationFactor = (displayWidth/ 480) * 4;
				float ynormalizationFactor = (displayHeight / 800) *6 ;
				
				float diffX=Math.abs(startX-event.getX());
				float diffY=Math.abs(startY-event.getY());
				
				if(diffX<=xnormalizationFactor && diffY<=ynormalizationFactor){
					return true;
				}
               
				if (rightBtnClickTimer != null) {
					rightBtnClickTimer.cancel();
				}
				input.mouseDragged((int) (xOffset + (event.getX() * density)
						/ scalingFactor),
						(int) (yOffset + (event.getY() * density)
								/ scalingFactor));
			}

		}

		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (stateTimer != null) {
				stateTimer.cancel();
			}
			if (rightBtnClickTimer != null) {
				rightBtnClickTimer.cancel();
			}

			if (currentState == CURRENT_STATE_SCROLLING) {
				xOffset += scrollX;
				yOffset += scrolY;

				if (scrollX <= 2 && scrolY <= 2) {
					currentState = CURRENT_STATE_TOUCH; // Since scrolling is
														// very less we consider
														// this event as touch
														// event.
				}
				scrollX = 0;
				scrolY = 0;

			}
			if (currentState == CURRENT_STATE_TOUCH) {
				if (!rightClicked) {
					int currentX = (int) (xOffset + (startX * density)
							/ scalingFactor);
					;
					int currentY = (int) (yOffset + (startY * density)
							/ scalingFactor);

					if (lastX != -1
							&& Math.abs((lastX - currentX)) < ((TOUCH_EVENT_THRESHOLD * density) / scalingFactor)
							&& Math.abs((lastY - currentY)) < ((TOUCH_EVENT_THRESHOLD * density) / scalingFactor)) {
						RDPLogger.d("Within treshold");
						currentX = lastX;
						currentY = lastY;
					} else {
						lastX = currentX;
						lastY = currentY;
					}
					RDPLogger.d("Sending input events");
					input.mousePressed(currentX, currentY,
							Input.MOUSE_LEFT_BUTTON);

					input.mouseReleased(currentX, currentY,
							Input.MOUSE_LEFT_BUTTON);
				} else {
					rightClicked = false;
				}

			} else if (currentState == CURRENT_STATE_DRAG) {
				input.mouseReleased((int) (xOffset + (event.getX() * density)
						/ scalingFactor),
						(int) (yOffset + (event.getY() * density)
								/ scalingFactor), Input.MOUSE_LEFT_BUTTON);
			}
		}

		return true;
	}

	private ScaleGestureDetector scaleDetector;

	public float getXOffset() {
		return (xOffset);
	}

	public float getYOffset() {
		return (yOffset);
	}

	private boolean scalingInProgress = false;

	public void scalingInProgress(boolean scalingInProgress) {
		this.scalingInProgress = scalingInProgress;
	}

	public void setScalingFactor(float scalingFactor) {

		// Setting the scaling factor based on users pinch zoom input. Scaling factor =1 (Regular image).
		this.scalingFactor = scalingFactor;
		int drawingWidth = (int) (displayWidth / scalingFactor);
		int drawingHeight = (int) (displayHeight / scalingFactor);

		drawingWidth = (int) (backstore.getWidth() < ((xOffset + scrollX) + drawingWidth) ? (backstore
				.getWidth() - (xOffset + scrollX)) : drawingWidth);
		drawingHeight = (int) (backstore.getHeight() < drawingHeight
				+ (yOffset + scrolY) ? backstore.getHeight()
				- (yOffset + scrolY) : drawingHeight);

		int[] subImage = backstore.getSubImage((int) (xOffset + scrollX),
				(int) (yOffset + scrolY), drawingWidth, drawingHeight);
		canvas = getHolder().lockCanvas();
		Matrix matrix = new Matrix();
		matrix.postScale((float) (scalingFactor / density),
				(float) (scalingFactor / density));
		canvas.setMatrix(matrix);
		canvas.drawColor(Color.BLACK);
		canvas.drawBitmap(subImage, 0, drawingWidth, 0, 0, drawingWidth,
				drawingHeight, false, new Paint());
		getHolder().unlockCanvasAndPost(canvas);
	}

	public float getScalingFactor() {
		return scalingFactor;
	}

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {

		return null;
	}

	@Override
	public boolean onCheckIsTextEditor() {
		return true;
	}

	public android.graphics.Bitmap getBackstoreImage() {
		if (backstore != null) {
			AndroidWrappedImage androidWrappedImage = (AndroidWrappedImage) backstore;
			return androidWrappedImage.getBackStoreBitmap();
		}
		return null;
	}

	public void setOffsets(float x, float y) {

	}
	
	public void sendUnicode(char data){
		this.input.sendUnicode(data);
	}

}
