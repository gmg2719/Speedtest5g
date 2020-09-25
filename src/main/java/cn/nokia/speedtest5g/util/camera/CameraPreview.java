package cn.nokia.speedtest5g.util.camera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import com.android.volley.util.MyToast;
import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.util.camera.CameraInstance.TakePictureCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private static final String TAG = "CameraPreview";
//	private static final String TAG_GPS_IMG_DIRECTION = "GPSImgDirection";
//	private static final String TAG_GPS_IMG_DIRECTION_REF = "GPSImgDirectionRef";

	private Paint p = new Paint();
	private Camera.CameraInfo camera_info = new Camera.CameraInfo();
	private Matrix camera_to_preview_matrix = new Matrix();
	private Matrix preview_to_camera_matrix = new Matrix();
	private RectF face_rect = new RectF();
//	private Rect text_bounds = new Rect();
	private int display_orientation = 0;

	private boolean ui_placement_right = true;

	private boolean app_is_paused = false;
	private SurfaceHolder mHolder = null;
	private boolean has_surface = false;
	private boolean has_aspect_ratio = false;
	private double aspect_ratio = 0.0f;
	private Camera camera = null;
	private int cameraId = 0;

	private final int PHASE_NORMAL = 0;
	private final int PHASE_TIMER = 1;
	private final int PHASE_TAKING_PHOTO = 2;
	private final int PHASE_PREVIEW_PAUSED = 3; // the paused state after taking
												// a photo
	private int phase = PHASE_NORMAL;

	private Timer takePictureTimer = new Timer();
	private TimerTask takePictureTimerTask = null;
	private TimerTask beepTimerTask = null;
	private long take_photo_time = 0;
	private int remaining_burst_photos = 0;
	private int n_burst = 1;

	private boolean is_preview_started = false;
//	private String preview_image_name = null;
	private Bitmap thumbnail = null; // thumbnail of last picture taken
	private boolean thumbnail_anim = false; // whether we are displaying the
											// thumbnail animation
	private long thumbnail_anim_start_ms = -1; // time that the thumbnail
												// animation started
	private RectF thumbnail_anim_src_rect = new RectF();
	private RectF thumbnail_anim_dst_rect = new RectF();
	private Matrix thumbnail_anim_matrix = new Matrix();
	private int[] gui_location = new int[2];

//	private int current_orientation = 0; // orientation received by
//											// onOrientationChanged
	private int current_rotation = 0; // orientation relative to camera's
										// orientation (used for
										// parameters.setOrientation())
//	private boolean has_level_angle = false;
//	private double level_angle = 0.0f;
//	private double orig_level_angle = 0.0f;

//	private float free_memory_gb = -1.0f;
//	private long last_free_memory_time = 0;

	private boolean has_zoom = false;
	private int zoom_factor = 0;
	private int max_zoom_factor = 0;
	private ScaleGestureDetector scaleGestureDetector;
	private List<Integer> zoom_ratios = null;
	private boolean touch_was_multitouch = false;

	private List<String> supported_flash_values = null; // our "values" format
	private int current_flash_index = -1; // this is an index into the
											// supported_flash_values array, or
											// -1 if no flash modes available

	private List<String> supported_focus_values = null; // our "values" format
	private int current_focus_index = -1; // this is an index into the
											// supported_focus_values array, or
											// -1 if no focus modes available

	private boolean is_exposure_locked_supported = false;
//	private boolean is_exposure_locked = false;

	private List<String> color_effects = null;
	private List<String> scene_modes = null;
	private List<String> white_balances = null;
	private String iso_key = null;
	private List<String> isos = null;
	private List<String> exposures = null;
	private int min_exposure = 0;
	private int max_exposure = 0;

	private List<Size> supported_preview_sizes = null;

	private List<Size> sizes = null;
	private int current_size_index = -1; // this is an index into the sizes
											// array, or -1 if sizes not yet set

	private boolean has_set_location = false;
	private float location_accuracy = 0.0f;
	private Bitmap location_bitmap = null;
	private Bitmap location_off_bitmap = null;
	private Rect location_dest = new Rect();

	class ToastBoxer {
		public Toast toast = null;

		ToastBoxer() {
		}
	}

	private ToastBoxer switch_camera_toast = new ToastBoxer();
	private ToastBoxer focus_toast = new ToastBoxer();
	private ToastBoxer take_photo_toast = new ToastBoxer();
	private ToastBoxer change_exposure_toast = new ToastBoxer();

	private int ui_rotation = 0;

	private boolean supports_face_detection = false;
	private boolean using_face_detection = false;
	private Face[] faces_detected = null;
	private boolean has_focus_area = false;
	private int focus_screen_x = 0;
	private int focus_screen_y = 0;
	private long focus_complete_time = -1;
	private int focus_success = FOCUS_DONE;
	private static final int FOCUS_WAITING = 0;
	private static final int FOCUS_SUCCESS = 1;
	private static final int FOCUS_FAILED = 2;
	private static final int FOCUS_DONE = 3;
	private String set_flash_after_autofocus = "";
	private boolean successfully_focused = false;
	private long successfully_focused_time = -1;

//	private IntentFilter battery_ifilter = new IntentFilter(
//			Intent.ACTION_BATTERY_CHANGED);
//	private boolean has_battery_frac = false;
//	private float battery_frac = 0.0f;
//	private long last_battery_time = 0;

	// accelerometer and geomagnetic sensor info


	// for testing:
	public int count_cameraStartPreview = 0;
	public int count_cameraAutoFocus = 0;
	public int count_cameraTakePicture = 0;
	public boolean has_received_location = false;
	public boolean test_low_memory = false;
	public boolean test_have_angle = false;
	public float test_angle = 0.0f;
	public String test_last_saved_image = null;

	CameraPreview(Context context) {
		this(context, null);
	}

	@SuppressWarnings("deprecation")
	public CameraPreview(Context context, Bundle savedInstanceState) {
		super(context);
		 {
			WybLog.d(TAG, "new Preview");
		}

		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // deprecated

		scaleGestureDetector = new ScaleGestureDetector(context,
				new ScaleListener());

		if (savedInstanceState != null) {
				WybLog.d(TAG, "have savedInstanceState");
			cameraId = savedInstanceState.getInt("cameraId", 0);
				WybLog.d(TAG, "found cameraId: " + cameraId);
			if (cameraId < 0 || cameraId >= Camera.getNumberOfCameras()) {
					WybLog.d(TAG,
							"cameraID not valid for "
									+ Camera.getNumberOfCameras() + " cameras!");
				cameraId = 0;
			}
			zoom_factor = savedInstanceState.getInt("zoom_factor", 0);
			
				WybLog.d(TAG, "found zoom_factor: " + zoom_factor);
		}

//		location_bitmap = BitmapFactory.decodeResource(this.getResources(),
//				R.drawable.earth);
//		location_off_bitmap = BitmapFactory.decodeResource(this.getResources(),
//				R.drawable.earth_off);
	}

	private void calculateCameraToPreviewMatrix() {
		camera_to_preview_matrix.reset();
		// from
		// http://developer.android.com/reference/android/hardware/Camera.Face.html#rect
		Camera.getCameraInfo(cameraId, camera_info);
		// Need mirror for front camera.
		boolean mirror = (camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
		camera_to_preview_matrix.setScale(mirror ? -1 : 1, 1);
		// This is the value for android.hardware.Camera.setDisplayOrientation.
		camera_to_preview_matrix.postRotate(display_orientation);
		// UI coordinates range from (0, 0) to (width, height).
		camera_to_preview_matrix.postScale(this.getWidth() / 2000f,
				this.getHeight() / 2000f);
		camera_to_preview_matrix.postTranslate(this.getWidth() / 2f,
				this.getHeight() / 2f);
	}

	private void calculatePreviewToCameraMatrix() {
		calculateCameraToPreviewMatrix();
		if (!camera_to_preview_matrix.invert(preview_to_camera_matrix)) {
			
				WybLog.d(TAG,
						"calculatePreviewToCameraMatrix failed to invert matrix!?");
		}
	}

	private ArrayList<Camera.Area> getAreas(float x, float y) {
		float[] coords = { x, y };
		calculatePreviewToCameraMatrix();
		preview_to_camera_matrix.mapPoints(coords);
		float focus_x = coords[0];
		float focus_y = coords[1];

		int focus_size = 50;
		 {
			WybLog.d(TAG, "x, y: " + x + ", " + y);
			WybLog.d(TAG, "focus x, y: " + focus_x + ", " + focus_y);
		}
		Rect rect = new Rect();
		rect.left = (int) focus_x - focus_size;
		rect.right = (int) focus_x + focus_size;
		rect.top = (int) focus_y - focus_size;
		rect.bottom = (int) focus_y + focus_size;
		if (rect.left < -1000) {
			rect.left = -1000;
			rect.right = rect.left + 2 * focus_size;
		} else if (rect.right > 1000) {
			rect.right = 1000;
			rect.left = rect.right - 2 * focus_size;
		}
		if (rect.top < -1000) {
			rect.top = -1000;
			rect.bottom = rect.top + 2 * focus_size;
		} else if (rect.bottom > 1000) {
			rect.bottom = 1000;
			rect.top = rect.bottom - 2 * focus_size;
		}

		ArrayList<Camera.Area> areas = new ArrayList<Camera.Area>();
		areas.add(new Camera.Area(rect, 1000));
		return areas;
	}

//	private TextView tv;
//	//设置下倾角显示
//	public void setText(TextView t){
//		this.tv = t;
//	}
	
//	private double angle;
//	private DecimalFormat fnum = new DecimalFormat("##0.00"); 
//	private XChartCalc xc = new XChartCalc();
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		scaleGestureDetector.onTouchEvent(event);
//		MainActivity main_activity = (MainActivity) this.getContext();
//		main_activity.clearSeekBar();
		/*
		 * if( MyDebug.LOG ) { WybLog.d(TAG, "touch event: " + event.getAction());
		 * }
		 */
		
		//这里是实现指标移动
//		if (event.getAction() == MotionEvent.ACTION_MOVE && pXQJ != null) {
//			
////				xc.calcArcEndPointXY(this.mStartX, this.mStartY, 230, Math.atan((this.mStartY-event.getY())/(event.getX()-this.mStartX))/Math.PI*180);
//				
//				this.mStopX = event.getX();
//				this.mStopY = event.getY();
//				if (tv != null) {
//					if (this.mStopX <= this.mStartX) {
//						if (this.mStopY >= this.mStartY) {
//							angle = 90-Math.atan((this.mStartY-this.mStopY)/(this.mStopX-this.mStartX))/Math.PI*180;
//						}else {
//							angle = 90+Math.atan((this.mStartY-this.mStopY)/(this.mStopX-this.mStartX))/Math.PI*180;
//						}
//					}else {
//						if (this.mStopY >= this.mStartY) {
//							angle = 90+Math.atan((this.mStartY-this.mStopY)/(this.mStopX-this.mStartX))/Math.PI*180;
//						}else {
//							angle = 90-Math.atan((this.mStartY-this.mStopY)/(this.mStopX-this.mStartX))/Math.PI*180;
//						}
//					}
//					
//					tv.setText("下倾角：" + fnum.format(angle) + "°");
//				}
//			
//			invalidate();
//		}
		
		if (event.getPointerCount() != 1) {
			touch_was_multitouch = true;
			return true;
		}
		
		if (event.getAction() != MotionEvent.ACTION_UP) {
			if (event.getAction() == MotionEvent.ACTION_DOWN
					&& event.getPointerCount() == 1) {
				touch_was_multitouch = false;
			}
			return true;
		}
		if (touch_was_multitouch) {
			return true;
		}
		if (this.isTakingPhotoOrOnTimer()) {
			return true;
		}

		// note, we always try to force start the preview (in case
		// is_preview_paused has become false)
		startCameraPreview();
		cancelAutoFocus();

		if (camera != null && !this.using_face_detection) {
			Parameters parameters = camera.getParameters();
			String focus_mode = parameters.getFocusMode();
			this.has_focus_area = false;
			if (parameters.getMaxNumFocusAreas() != 0
					&& (focus_mode.equals(Parameters.FOCUS_MODE_AUTO)
							|| focus_mode
									.equals(Parameters.FOCUS_MODE_MACRO)
							|| focus_mode
									.equals(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) || focus_mode
								.equals(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))) {
				
					WybLog.d(TAG, "set focus (and metering?) area");
				this.has_focus_area = true;
				this.focus_screen_x = (int) event.getX();
				this.focus_screen_y = (int) event.getY();

				ArrayList<Camera.Area> areas = getAreas(event.getX(),
						event.getY());
				parameters.setFocusAreas(areas);

				// also set metering areas
				if (parameters.getMaxNumMeteringAreas() == 0) {
					
						WybLog.d(TAG, "metering areas not supported");
				} else {
					parameters.setMeteringAreas(areas);
				}

				try {
					
						WybLog.d(TAG, "set focus areas parameters");
					camera.setParameters(parameters);
					
						WybLog.d(TAG, "done");
				} catch (RuntimeException e) {
					// just in case something has gone wrong
					
						WybLog.d(TAG, "failed to set parameters for focus area");
					e.printStackTrace();
				}
			} else if (parameters.getMaxNumMeteringAreas() != 0) {
				
					WybLog.d(TAG, "set metering area");
				// don't set has_focus_area in this mode
				ArrayList<Camera.Area> areas = getAreas(event.getX(),
						event.getY());
				parameters.setMeteringAreas(areas);

				try {
					camera.setParameters(parameters);
				} catch (RuntimeException e) {
					// just in case something has gone wrong
					
						WybLog.d(TAG, "failed to set parameters for focus area");
					e.printStackTrace();
				}
			}
		}

		tryAutoFocus(false, true);
		return true;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			if (CameraPreview.this.camera != null
					&& CameraPreview.this.has_zoom) {
				CameraPreview.this.scaleZoom(detector.getScaleFactor());
			}
			return true;
		}
	}

	public void clearFocusAreas() {
		
			WybLog.d(TAG, "clearFocusAreas()");
		if (camera == null) {
			return;
		}
		cancelAutoFocus();
		Parameters parameters = camera.getParameters();
		boolean update_parameters = false;
		if (parameters.getMaxNumFocusAreas() > 0) {
			parameters.setFocusAreas(null);
			update_parameters = true;
		}
		if (parameters.getMaxNumMeteringAreas() > 0) {
			parameters.setMeteringAreas(null);
			update_parameters = true;
		}
		if (update_parameters) {
			camera.setParameters(parameters);
		}
		has_focus_area = false;
		focus_success = FOCUS_DONE;
		successfully_focused = false;
		// WybLog.d(TAG, "camera parameters null? " +
		// (camera.getParameters().getFocusAreas()==null));
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
			WybLog.d(TAG, "surfaceCreated()");
		// The Surface has been created, acquire the camera and tell it where
		// to draw.
		this.has_surface = true;
		this.openCamera();
		this.setWillNotDraw(false); // see
									// http://stackoverflow.com/questions/2687015/extended-surfaceviews-ondraw-method-never-called
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
			WybLog.d(TAG, "surfaceDestroyed()");
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's very
		// important to release it when the activity is paused.
		this.has_surface = false;
		this.closeCamera();
	}

	private void closeCamera() {
		 {
			WybLog.d(TAG, "closeCamera()");
		}
		has_focus_area = false;
		focus_success = FOCUS_DONE;
		successfully_focused = false;
		has_set_location = false;
		has_received_location = false;
//		MainActivity main_activity = (MainActivity) this.getContext();
//		main_activity.clearSeekBar();
		// if( is_taking_photo_on_timer ) {
		if (this.isOnTimer()) {
			takePictureTimerTask.cancel();
			if (beepTimerTask != null) {
				beepTimerTask.cancel();
			}
			/*
			 * is_taking_photo_on_timer = false; is_taking_photo = false;
			 */
			this.phase = PHASE_NORMAL;
			
				WybLog.d(TAG, "cancelled camera timer");
		}
		if (camera != null) {
			this.setPreviewPaused(false);
			camera.stopPreview();
			this.phase = PHASE_NORMAL;
			this.is_preview_started = false;
			showGUI(true);
			camera.release();
			camera = null;
		}
	}
	
	final Handler handler = new Handler();
	private void openCamera() {
		long debug_time = 0;
		 {
			WybLog.d(TAG, "openCamera()");
			WybLog.d(TAG, "cameraId: " + cameraId);
			debug_time = System.currentTimeMillis();
		}
		// need to init everything now, in case we don't open the camera (but
		// these may already be initialised from an earlier call - e.g., if we
		// are now switching to another camera)
		has_set_location = false;
		has_received_location = false;
		has_focus_area = false;
		focus_success = FOCUS_DONE;
		successfully_focused = false;
		scene_modes = null;
		has_zoom = false;
		max_zoom_factor = 0;
		zoom_ratios = null;
		faces_detected = null;
		supports_face_detection = false;
		using_face_detection = false;
		color_effects = null;
		white_balances = null;
		isos = null;
		exposures = null;
		min_exposure = 0;
		max_exposure = 0;
		sizes = null;
		current_size_index = -1;
		supported_flash_values = null;
		current_flash_index = -1;
		supported_focus_values = null;
		current_focus_index = -1;
		showGUI(true);
		if (!this.has_surface) {
			 {
				WybLog.d(TAG, "preview surface not yet available");
			}
			return;
		}
		if (this.app_is_paused) {
			 {
				WybLog.d(TAG, "don't open camera as app is paused");
			}
			return;
		}
		try {
			camera = Camera.open(cameraId);
		} catch (RuntimeException e) {
			
				WybLog.e(TAG, "Failed to open camera: " + e.getMessage());
			e.printStackTrace();
			camera = null;
		}
		 {
			// WybLog.d(TAG, "time after opening camera: " +
			// (System.currentTimeMillis() - debug_time));
		}
		if (camera != null) {
			Activity activity = (Activity) this.getContext();
//			this.setCameraDisplayOrientation(activity);
//			new OrientationEventListener(activity) {
//				@Override
//				public void onOrientationChanged(int orientation) {
//					CameraPreview.this.onOrientationChanged(orientation);
//				}
//			}.enable();
			//旋转
			camera.setDisplayOrientation(90);
			 {
				// WybLog.d(TAG, "time after setting orientation: " +
				// (System.currentTimeMillis() - debug_time));
			}

			
				WybLog.d(TAG, "call setPreviewDisplay");
			try {
				camera.setPreviewDisplay(mHolder);
			} catch (IOException e) {
				
					WybLog.e(TAG,
							"Failed to set preview display: " + e.getMessage());
				e.printStackTrace();
			}
			 {
				// WybLog.d(TAG, "time after setting preview display: " +
				// (System.currentTimeMillis() - debug_time));
			}

//			SharedPreferences sharedPreferences = PreferenceManager
//					.getDefaultSharedPreferences(this.getContext());

//			View switchCameraButton = (View) activity
//					.findViewById(R.id.switch_camera);
//			switchCameraButton
//					.setVisibility(Camera.getNumberOfCameras() > 1 ? View.VISIBLE
//							: View.GONE);

			Parameters parameters = camera.getParameters();

			// get available scene modes
			// important, from docs:
			// "Changing scene mode may override other parameters (such as flash
			// mode, focus mode, white balance).
			// For example, suppose originally flash mode is on and supported
			// flash modes are on/off. In night
			// scene mode, both flash mode and supported flash mode may be
			// changed to off. After setting scene
			// mode, applications should call getParameters to know if some
			// parameters are changed."
			scene_modes = parameters.getSupportedSceneModes();
			String scene_mode = setupValuesPref(scene_modes,
					getSceneModePreferenceKey(),
					Parameters.SCENE_MODE_AUTO);
			if (scene_mode != null
					&& !parameters.getSceneMode().equals(scene_mode)) {
				parameters.setSceneMode(scene_mode);
				// need to read back parameters, see comment above
				camera.setParameters(parameters);
				parameters = camera.getParameters();
			}

			this.has_zoom = parameters.isZoomSupported();
			
				WybLog.d(TAG, "has_zoom? " + has_zoom);
//			ZoomControls zoomControls = (ZoomControls) activity
//					.findViewById(R.id.zoom);
			SeekBar zoomSeekBar = (SeekBar) activity
					.findViewById(R.id.zoomBar);
			if (this.has_zoom) {
				this.max_zoom_factor = parameters.getMaxZoom();
				try {
					this.zoom_ratios = parameters.getZoomRatios();
				} catch (NumberFormatException e) {
					// crash java.lang.NumberFormatException: Invalid int:
					// " 500" reported in v1.4 on device "es209ra", Android 4.1,
					// 3 Jan 2014
					// this is from
					// java.lang.Integer.invalidInt(Integer.java:138) - unclear
					// if this is a bug in Open Camera, all we can do for now is
					// catch it
					
						WybLog.e(TAG, "NumberFormatException in getZoomRatios()");
					e.printStackTrace();
					this.has_zoom = false;
					this.zoom_ratios = null;
				}
			}

			if (this.has_zoom) {
//				if (sharedPreferences.getBoolean(
//						"preference_show_zoom_controls", true)) {
//					zoomControls.setIsZoomInEnabled(true);
//					zoomControls.setIsZoomOutEnabled(true);
//					zoomControls.setZoomSpeed(20);
//
//					zoomControls
//							.setOnZoomInClickListener(new OnClickListener() {
//								public void onClick(View v) {
//									zoomIn();
//								}
//							});
//					zoomControls
//							.setOnZoomOutClickListener(new OnClickListener() {
//								public void onClick(View v) {
//									zoomOut();
//								}
//							});
//					zoomControls.setVisibility(View.VISIBLE);
//				} else {
//					zoomControls.setVisibility(View.INVISIBLE); // must be
																// INVISIBLE not
																// GONE, so we
																// can still
																// position the
																// zoomSeekBar
																// relative to
																// it
//				}

				zoomSeekBar.setMax(max_zoom_factor);
				zoomSeekBar.setProgress(zoom_factor);
				zoomSeekBar
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
							@Override
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								zoomTo(progress, false);
							}

							@Override
							public void onStartTrackingTouch(SeekBar seekBar) {
							}

							@Override
							public void onStopTrackingTouch(SeekBar seekBar) {
							}
						});
				zoomSeekBar.setVisibility(View.VISIBLE);
			} else {
//				zoomControls.setVisibility(View.GONE);
				zoomSeekBar.setVisibility(View.GONE);
			}

			// get face detection supported
			this.faces_detected = null;
			this.supports_face_detection = parameters.getMaxNumDetectedFaces() > 0;
			if (this.supports_face_detection) {
//				this.using_face_detection = sharedPreferences.getBoolean(
//						"preference_face_detection", false);
				this.using_face_detection = false;//TODO �����Ƿ�����ʶ��
			} else {
				this.using_face_detection = false;
			}
			

			
			 {
				WybLog.d(TAG, "supports_face_detection?: "
						+ supports_face_detection);
				WybLog.d(TAG, "using_face_detection?: " + using_face_detection);
			}
			if (this.using_face_detection) {
				class MyFaceDetectionListener implements
						Camera.FaceDetectionListener {
					@Override
					public void onFaceDetection(Face[] faces, Camera camera) {
						faces_detected = new Face[faces.length];
						System.arraycopy(faces, 0, faces_detected, 0,
								faces.length);
					}
				}
				camera.setFaceDetectionListener(new MyFaceDetectionListener());
			}

			// get available color effects
			color_effects = parameters.getSupportedColorEffects();
			String color_effect = setupValuesPref(color_effects,
					getColorEffectPreferenceKey(),
					Parameters.EFFECT_NONE);
			if (color_effect != null) {
				parameters.setColorEffect(color_effect);
			}

			// get available white balances
			white_balances = parameters.getSupportedWhiteBalance();
			String white_balance = setupValuesPref(white_balances,
					getWhiteBalancePreferenceKey(),
					Parameters.WHITE_BALANCE_AUTO);
			if (white_balance != null) {
				parameters.setWhiteBalance(white_balance);
			}

			// get available isos - no standard value for this, see
			// http://stackoverflow.com/questions/2978095/android-camera-api-iso-setting
			{
				String iso_values = parameters.get("iso-values");
				if (iso_values == null) {
					iso_values = parameters.get("iso-mode-values"); // Galaxy
																	// Nexus
					if (iso_values == null) {
						iso_values = parameters.get("iso-speed-values"); // Micromax
																			// A101
						if (iso_values == null)
							iso_values = parameters
									.get("nv-picture-iso-values"); // LG dual
																	// P990
					}
				}
				if (iso_values != null && iso_values.length() > 0) {
					
						WybLog.d(TAG, "iso_values: " + iso_values);
					String[] isos_array = iso_values.split(",");
					if (isos_array != null && isos_array.length > 0) {
						isos = new ArrayList<String>();
						for (int i = 0; i < isos_array.length; i++) {
							isos.add(isos_array[i]);
						}
					}
				}
			}
			iso_key = "iso";
			if (parameters.get(iso_key) == null) {
				iso_key = "iso-speed"; // Micromax A101
				if (parameters.get(iso_key) == null) {
					iso_key = "nv-picture-iso"; // LG dual P990
					if (parameters.get(iso_key) == null)
						iso_key = null; // not supported
				}
			}
			if (iso_key != null) {
				if (isos == null) {
					// set a default for some devices which have an iso_key, but
					// don't give a list of supported ISOs
					isos = new ArrayList<String>();
					isos.add("auto");
					isos.add("100");
					isos.add("200");
					isos.add("400");
					isos.add("800");
					isos.add("1600");
				}
				String iso = setupValuesPref(isos, getISOPreferenceKey(),
						"auto");
				if (iso != null) {
					
						WybLog.d(TAG, "set: " + iso_key + " to: " + iso);
					parameters.set(iso_key, iso);
				}
			}

			// get min/max exposure
			exposures = null;
			min_exposure = parameters.getMinExposureCompensation();
			max_exposure = parameters.getMaxExposureCompensation();
			if (min_exposure != 0 || max_exposure != 0) {
				exposures = new Vector<String>();
				for (int i = min_exposure; i <= max_exposure; i++) {
					exposures.add("" + i);
				}
				String exposure_s = setupValuesPref(exposures,
						getExposurePreferenceKey(), "0");
				if (exposure_s != null) {
					try {
						int exposure = Integer.parseInt(exposure_s);
						
							WybLog.d(TAG, "exposure: " + exposure);
						parameters.setExposureCompensation(exposure);
					} catch (NumberFormatException exception) {
						
							WybLog.d(TAG,
									"exposure invalid format, can't parse to int");
					}
				}
			}
//			View exposureButton = (View) activity.findViewById(R.id.exposure);
//			exposureButton.setVisibility(exposures != null ? View.VISIBLE
//					: View.GONE);

			// get available sizes
			sizes = parameters.getSupportedPictureSizes();
			 {
				for (int i = 0; i < sizes.size(); i++) {
					Size size = sizes.get(i);
					WybLog.d(TAG, "supported picture size: " + size.width + " , "
							+ size.height);
				}
			}
			current_size_index = -1;
//			String resolution_value = sharedPreferences.getString(
//					getResolutionPreferenceKey(cameraId), "");
			
			String resolution_value = getResolutionPreferenceKey(cameraId);
			
				WybLog.d(TAG, "resolution_value: " + resolution_value);
			if (resolution_value.length() > 0) {
				// parse the saved size, and make sure it is still valid
				int index = resolution_value.indexOf(' ');
				if (index == -1) {
					
						WybLog.d(TAG,
								"resolution_value invalid format, can't find space");
				} else {
					String resolution_w_s = resolution_value
							.substring(0, index);
					String resolution_h_s = resolution_value
							.substring(index + 1);
					 {
						WybLog.d(TAG, "resolution_w_s: " + resolution_w_s);
						WybLog.d(TAG, "resolution_h_s: " + resolution_h_s);
					}
					try {
						int resolution_w = Integer.parseInt(resolution_w_s);
						
							WybLog.d(TAG, "resolution_w: " + resolution_w);
						int resolution_h = Integer.parseInt(resolution_h_s);
						
							WybLog.d(TAG, "resolution_h: " + resolution_h);
						// now find size in valid list
						for (int i = 0; i < sizes.size()
								&& current_size_index == -1; i++) {
							Size size = sizes.get(i);
							if (size.width == resolution_w
									&& size.height == resolution_h) {
								current_size_index = i;
								
									WybLog.d(TAG, "set current_size_index to: "
											+ current_size_index);
							}
						}
						if (current_size_index == -1) {
							
								WybLog.e(TAG, "failed to find valid size");
						}
					} catch (NumberFormatException exception) {
						
							WybLog.d(TAG,
									"resolution_value invalid format, can't parse w or h to int");
					}
				}
			}

			if (current_size_index == -1) {
				// set to largest
				Size current_size = null;
				for (int i = 0; i < sizes.size(); i++) {
					Size size = sizes.get(i);
					if (current_size == null
							|| size.width * size.height > current_size.width
									* current_size.height) {
						current_size_index = i;
						current_size = size;
					}
				}
			}
			if (current_size_index != -1) {
				Size current_size = sizes.get(current_size_index);
				
					WybLog.d(TAG, "Current size index " + current_size_index
							+ ": " + current_size.width + ", "
							+ current_size.height);

				// now save, so it's available for PreferenceActivity
				resolution_value = current_size.width + " "
						+ current_size.height;
				 {
					WybLog.d(TAG, "save new resolution_value: " + resolution_value);
				}
//				SharedPreferences.Editor editor = sharedPreferences.edit();
//				editor.putString(getResolutionPreferenceKey(cameraId),
//						resolution_value);
//				editor.apply();

				// now set the size
				parameters.setPictureSize(current_size.width,
						current_size.height);
			}

			/*
			 * if( MyDebug.LOG ) WybLog.d(TAG, "Current image quality: " +
			 * parameters.getJpegQuality());
			 */
			int image_quality = getImageQuality();
			parameters.setJpegQuality(image_quality);
			
				WybLog.d(TAG, "image quality: " + image_quality);

			camera.setParameters(parameters);

			// we do the following after setting parameters, as these are done
			// by calling separate functions, that themselves set the parameters
			// directly
			List<String> supported_flash_modes = parameters
					.getSupportedFlashModes(); // Android format
			View flashButton = (View) activity.findViewById(R.id.flash);
			current_flash_index = -1;
			if (supported_flash_modes != null
					&& supported_flash_modes.size() > 1) {
				
					WybLog.d(TAG, "flash modes: " + supported_flash_modes);
				supported_flash_values = convertFlashModesToValues(supported_flash_modes); // convert
																							// to
																							// our
																							// format
																							// (also
																							// resorts)

//				String flash_value = sharedPreferences.getString(
//						getFlashPreferenceKey(cameraId), "");
				String flash_value = getFlashPreferenceKey(cameraId);
				if (flash_value.length() > 0) {
					
						WybLog.d(TAG, "found existing flash_value: " + flash_value);
					if (!updateFlash(flash_value)) {
						
							WybLog.d(TAG, "flash value no longer supported!");
						updateFlash(0);
					}
				} else {
					
						WybLog.d(TAG, "found no existing flash_value");
					updateFlash(0);
				}
			} else {
				
					WybLog.d(TAG, "flash not supported");
				supported_flash_values = null;
			}
			flashButton
					.setVisibility(supported_flash_values != null ? View.VISIBLE
							: View.GONE);

			List<String> supported_focus_modes = parameters
					.getSupportedFocusModes(); // Android format
//			View focusModeButton = (View) activity
//					.findViewById(R.id.focus_mode);
			current_focus_index = -1;
			if (supported_focus_modes != null
					&& supported_focus_modes.size() > 1) {
				
					WybLog.d(TAG, "focus modes: " + supported_focus_modes);
				supported_focus_values = convertFocusModesToValues(supported_focus_modes); // convert
																							// to
																							// our
																							// format
																							// (also
																							// resorts)

//				String focus_value = sharedPreferences.getString(
//						getFocusPreferenceKey(cameraId), "");
				String focus_value = getFocusPreferenceKey(cameraId);
				if (focus_value.length() > 0) {
					
						WybLog.d(TAG, "found existing focus_value: " + focus_value);
					if (!updateFocus(focus_value, false, false, true)) { // don't
																			// need
																			// to
																			// save,
																			// as
																			// this
																			// is
																			// the
																			// value
																			// that's
																			// already
																			// saved
						
							WybLog.d(TAG, "focus value no longer supported!");
						updateFocus(0, false, true, true);
					}
				} else {
					
						WybLog.d(TAG, "found no existing focus_value");
					updateFocus(0, false, true, true);
				}
			} else {
				
					WybLog.d(TAG, "focus not supported");
				supported_focus_values = null;
			}
//			focusModeButton
//					.setVisibility(supported_focus_values != null ? View.VISIBLE
//							: View.GONE);

			this.is_exposure_locked_supported = parameters
					.isAutoExposureLockSupported();
//			ImageButton exposureLockButton = (ImageButton) activity
//					.findViewById(R.id.exposure_lock);
//			exposureLockButton
//					.setVisibility(is_exposure_locked_supported ? View.VISIBLE
//							: View.GONE);
//			is_exposure_locked = false;
			if (is_exposure_locked_supported) {
				// exposure lock should always default to false, as doesn't make
				// sense to save it - we can't really preserve a "lock" after
				// the camera is reopened
				// also note that it isn't safe to lock the exposure before
				// starting the preview
//				exposureLockButton
//						.setImageResource(is_exposure_locked ? R.drawable.exposure_locked
//								: R.drawable.exposure_unlocked);
			}

			// must be done after setting parameters, as this function may set
			// parameters
			if (this.has_zoom && zoom_factor != 0) {
				int new_zoom_factor = zoom_factor;
				zoom_factor = 0; // force zoomTo to actually update the zoom!
				zoomTo(new_zoom_factor, true);
			}

			// Must set preview size before starting camera preview
			// and must do it after setting photo vs video mode
			//设置显示
//			setPreviewSize(); // need to call this when we switch cameras, not
								// just when we run for the first time
			// Must call startCameraPreview after checking if face detection is
			// present - probably best to call it after setting all parameters
			// that we want
			
			//开始预览
			setParameters();
			startCameraPreview();
			 {
				// WybLog.d(TAG, "time after starting camera preview: " +
				// (System.currentTimeMillis() - debug_time));
			}

			
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					tryAutoFocus(true, false); // so we get the autofocus when
												// starting up - we do this on a
												// delay, as calling it
												// immediately means the
												// autofocus doesn't seem to
												// work properly sometimes (at
												// least on Galaxy Nexus)
				}
			}, 500);
		}

		 {
			WybLog.d(TAG, "total time: "
					+ (System.currentTimeMillis() - debug_time));
			if (camera != null) {
				WybLog.d(TAG, "camera parameters: "
						+ camera.getParameters().flatten());
			}
		}
	}

	public void setParameters(){
		float previewRate = 1.333f;
		if (camera != null) {
			Parameters mParams = camera.getParameters();
			mParams.setPictureFormat(ImageFormat.JPEG);
			
			mArrDx = mParams.getSupportedPictureSizes();
			mArrXs = mParams.getSupportedPreviewSizes();
			
			int widthDx = SharedPreHandler.getShared(getContext()).getIntShared(TypeKey.getInstance().CAMERA_DX_SIZE(), -1);
			if (widthDx <= 0) {
				widthDx = 800;
			}
			int widthXs = SharedPreHandler.getShared(getContext()).getIntShared(TypeKey.getInstance().CAMERA_XS_SIZE(), -1);
			if (widthXs <= 0) {
				widthXs = 800;
			}
			
			Size pictureSize = CameraUtil.getInstance().getPropPictureSize(
					mArrDx, previewRate, widthDx);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			SharedPreHandler.getShared(getContext()).setIntShared(TypeKey.getInstance().CAMERA_DX_SIZE(), pictureSize.width);
			Size previewSize = CameraUtil.getInstance().getPropPreviewSize(
					mArrXs, previewRate, widthXs);
			mParams.setPreviewSize(previewSize.width, previewSize.height);
			SharedPreHandler.getShared(getContext()).setIntShared(TypeKey.getInstance().CAMERA_XS_SIZE(), previewSize.width);
			SharedPreHandler.getShared(getContext()).setIntShared(TypeKey.getInstance().CAMERA_XS_HEIGHT(), previewSize.height);
			camera.setParameters(mParams);
		}
	}
	
	public void startPreview(){
//		camera.cancelAutoFocus();
		camera.startPreview();
	}
	
	public void stopPreview(){
		camera.stopPreview();
	}
	
	//像素，支持浏览的大小
	private List<Size> mArrXs,mArrDx;
	
	public List<Size> getmArrXs() {
		return mArrXs;
	}

	public List<Size> getmArrDx() {
		return mArrDx;
	}
	
	private String setupValuesPref(List<String> values, String key,
			String default_value) {
		
			WybLog.d(TAG, "setupValuesPref, key: " + key);
		if (values != null && values.size() > 0) {
			 {
				for (int i = 0; i < values.size(); i++) {
					WybLog.d(TAG, "supported value: " + values.get(i));
				}
			}
//			SharedPreferences sharedPreferences = PreferenceManager
//					.getDefaultSharedPreferences(this.getContext());
//			String value = sharedPreferences.getString(key, default_value);
			String value = default_value;
			
				WybLog.d(TAG, "value: " + value);
			// make sure result is valid
			if (!values.contains(value)) {
				
					WybLog.d(TAG, "value not valid!");
				if (values.contains(default_value))
					value = default_value;
				else
					value = values.get(0);
				
					WybLog.d(TAG, "value is now: " + value);
			}

			// now save, so it's available for PreferenceActivity
//			SharedPreferences.Editor editor = sharedPreferences.edit();
//			editor.putString(key, value);
//			editor.apply();

			return value;
		} else {
			
				WybLog.d(TAG, "values not supported");
			return null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		
			WybLog.d(TAG, "surfaceChanged " + w + ", " + h);
		// surface size is now changed to match the aspect ratio of camera
		// preview - so we shouldn't change the preview to match the surface
		// size, so no need to restart preview here

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		if (camera == null) {
			return;
		}
//注释
//		MainActivity main_activity = (MainActivity) CameraPreview.this
//				.getContext();
//		main_activity.layoutUI(); // need to force a layoutUI update (e.g., so
									// UI is oriented correctly when app goes
									// idle, device is then rotated, and app is
									// then resumed
	}

//	private void setPreviewSize() {
//		
//			WybLog.d(TAG, "setPreviewSize()");
//		if (camera == null) {
//			return;
//		}
//		if (is_preview_started) {
//			
//				WybLog.d(TAG,
//						"setPreviewSize() shouldn't be called when preview is running");
//			throw new RuntimeException();
//		}
//		// set optimal preview size
//		Camera.Parameters parameters = camera.getParameters();
//		
//			WybLog.d(TAG,
//					"current preview size: "
//							+ parameters.getPreviewSize().width + ", "
//							+ parameters.getPreviewSize().height);
//		supported_preview_sizes = parameters.getSupportedPreviewSizes();
//		if (supported_preview_sizes.size() > 0) {
//			Camera.Size best_size = getOptimalPreviewSize(supported_preview_sizes);
//			parameters.setPreviewSize(best_size.width, best_size.height);
//			
//				WybLog.d(TAG,
//						"new preview size: "
//								+ parameters.getPreviewSize().width + ", "
//								+ parameters.getPreviewSize().height);
//			this.setAspectRatio(((double) parameters.getPreviewSize().width)
//					/ (double) parameters.getPreviewSize().height);
//
//			camera.setParameters(parameters);
//		}
//	}

	private CamcorderProfile getCamcorderProfile(String quality) {
		
			WybLog.e(TAG, "getCamcorderProfile(): " + quality);
		CamcorderProfile camcorder_profile = CamcorderProfile.get(cameraId,
				CamcorderProfile.QUALITY_HIGH); // default
		try {
			String profile_string = quality;
			int index = profile_string.indexOf('_');
			if (index != -1) {
				profile_string = quality.substring(0, index);
				
					WybLog.e(TAG, "    profile_string: " + profile_string);
			}
			int profile = Integer.parseInt(profile_string);
			camcorder_profile = CamcorderProfile.get(cameraId, profile);
			if (index != -1 && index + 1 < quality.length()) {
				String override_string = quality.substring(index + 1);
				
					WybLog.e(TAG, "    override_string: " + override_string);
				if (override_string.charAt(0) == 'r'
						&& override_string.length() >= 4) {
					index = override_string.indexOf('x');
					if (index == -1) {
						
							WybLog.d(TAG,
									"override_string invalid format, can't find x");
					} else {
						String resolution_w_s = override_string.substring(1,
								index); // skip first 'r'
						String resolution_h_s = override_string
								.substring(index + 1);
						 {
							WybLog.d(TAG, "resolution_w_s: " + resolution_w_s);
							WybLog.d(TAG, "resolution_h_s: " + resolution_h_s);
						}
						// copy to local variable first, so that if we fail to
						// parse height, we don't set the width either
						int resolution_w = Integer.parseInt(resolution_w_s);
						int resolution_h = Integer.parseInt(resolution_h_s);
						camcorder_profile.videoFrameWidth = resolution_w;
						camcorder_profile.videoFrameHeight = resolution_h;
					}
				} else {
					
						WybLog.d(TAG,
								"unknown override_string initial code, or otherwise invalid format");
				}
			}
		} catch (NumberFormatException e) {
			
				WybLog.e(TAG, "failed to parse video quality: " + quality);
			e.printStackTrace();
		}
		return camcorder_profile;
	}

	private static String formatFloatToString(final float f) {
		final int i = (int) f;
		if (f == i)
			return Integer.toString(i);
		return String.format(Locale.getDefault(), "%.2f", f);
	}

	private static int greatestCommonFactor(int a, int b) {
		while (b > 0) {
			int temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	private static String getAspectRatio(int width, int height) {
		int gcf = greatestCommonFactor(width, height);
		width /= gcf;
		height /= gcf;
		return width + ":" + height;
	}

	static String getAspectRatioMPString(int width, int height) {
		float mp = (width * height) / 1000000.0f;
		return "(" + getAspectRatio(width, height) + ", "
				+ formatFloatToString(mp) + "MP)";
	}

	String getCamcorderProfileDescription(String quality) {
		CamcorderProfile profile = getCamcorderProfile(quality);
		String highest = "";
		if (profile.quality == CamcorderProfile.QUALITY_HIGH) {
			highest = "Highest: ";
		}
		String type = "";
		if (profile.videoFrameWidth == 3840 && profile.videoFrameHeight == 2160) {
			type = "4K Ultra HD ";
		} else if (profile.videoFrameWidth == 1920
				&& profile.videoFrameHeight == 1080) {
			type = "Full HD ";
		} else if (profile.videoFrameWidth == 1280
				&& profile.videoFrameHeight == 720) {
			type = "HD ";
		} else if (profile.videoFrameWidth == 720
				&& profile.videoFrameHeight == 480) {
			type = "SD ";
		} else if (profile.videoFrameWidth == 640
				&& profile.videoFrameHeight == 480) {
			type = "VGA ";
		} else if (profile.videoFrameWidth == 352
				&& profile.videoFrameHeight == 288) {
			type = "CIF ";
		} else if (profile.videoFrameWidth == 320
				&& profile.videoFrameHeight == 240) {
			type = "QVGA ";
		} else if (profile.videoFrameWidth == 176
				&& profile.videoFrameHeight == 144) {
			type = "QCIF ";
		}
		String desc = highest
				+ type
				+ profile.videoFrameWidth
				+ "x"
				+ profile.videoFrameHeight
				+ " "
				+ getAspectRatioMPString(profile.videoFrameWidth,
						profile.videoFrameHeight);
		return desc;
	}

	public double getTargetRatio(Point display_size) {
		double targetRatio = 0.0f;
//		Activity activity = (Activity) this.getContext();
//		SharedPreferences sharedPreferences = PreferenceManager
//				.getDefaultSharedPreferences(activity);
//		String preview_size = sharedPreferences.getString(
//				"preference_preview_size", "preference_preview_size_display");
		
		String preview_size = "preference_preview_size_display";
		// should always use wysiwig for video mode, otherwise we get incorrect
		// aspect ratio shown when recording video (at least on Galaxy Nexus,
		// e.g., at 640x480)
		// also not using wysiwyg mode with video caused corruption on Samsung
		// cameras (tested with Samsung S3, Android 4.3, front camera, infinity
		// focus)
		if (preview_size.equals("preference_preview_size_wysiwyg")) {
			
				WybLog.d(TAG, "set preview aspect ratio from photo size (wysiwyg)");
			Parameters parameters = camera.getParameters();
			Size picture_size = parameters.getPictureSize();
			
				WybLog.d(TAG, "picture_size: " + picture_size.width + " x "
						+ picture_size.height);
			targetRatio = ((double) picture_size.width)
					/ (double) picture_size.height;
		} else {
			
				WybLog.d(TAG, "set preview aspect ratio from display size");
			// base target ratio from display size - means preview will fill the
			// device's display as much as possible
			// but if the preview's aspect ratio differs from the actual
			// photo/video size, the preview will show a cropped version of what
			// is actually taken
			targetRatio = ((double) display_size.x) / (double) display_size.y;
		}
		
			WybLog.d(TAG, "targetRatio: " + targetRatio);
		return targetRatio;
	}

	public Size getOptimalPreviewSize(List<Size> sizes) {
		
			WybLog.d(TAG, "getOptimalPreviewSize()");
		final double ASPECT_TOLERANCE = 0.05;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		Point display_size = new Point();
		Activity activity = (Activity) this.getContext();
		{
			Display display = activity.getWindowManager().getDefaultDisplay();
			display.getSize(display_size);
			
				WybLog.d(TAG, "display_size: " + display_size.x + " x "
						+ display_size.y);
		}
		double targetRatio = getTargetRatio(display_size);
		int targetHeight = Math.min(display_size.y, display_size.x);
		if (targetHeight <= 0) {
			targetHeight = display_size.y;
		}
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			
				WybLog.d(TAG, "    supported preview size: " + size.width + ", "
						+ size.height);
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		if (optimalSize == null) {
			// can't find match for aspect ratio, so find closest one
			
				WybLog.d(TAG, "no preview size matches the aspect ratio");
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				double ratio = (double) size.width / size.height;
				if (Math.abs(ratio - targetRatio) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(ratio - targetRatio);
				}
			}
		}
		 {
			WybLog.d(TAG, "chose optimalSize: " + optimalSize.width + " x "
					+ optimalSize.height);
			WybLog.d(TAG, "optimalSize ratio: "
					+ ((double) optimalSize.width / optimalSize.height));
		}
		return optimalSize;
	}

	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		if (!this.has_aspect_ratio) {
			super.onMeasure(widthSpec, heightSpec);
			return;
		}
		int previewWidth = MeasureSpec.getSize(widthSpec);
		int previewHeight = MeasureSpec.getSize(heightSpec);

		// Get the padding of the border background.
		int hPadding = getPaddingLeft() + getPaddingRight();
		int vPadding = getPaddingTop() + getPaddingBottom();

		// Resize the preview frame with correct aspect ratio.
		previewWidth -= hPadding;
		previewHeight -= vPadding;

		boolean widthLonger = previewWidth > previewHeight;
		int longSide = (widthLonger ? previewWidth : previewHeight);
		int shortSide = (widthLonger ? previewHeight : previewWidth);
		if (longSide > shortSide * aspect_ratio) {
			longSide = (int) ((double) shortSide * aspect_ratio);
		} else {
			shortSide = (int) ((double) longSide / aspect_ratio);
		}
		if (widthLonger) {
			previewWidth = longSide;
			previewHeight = shortSide;
		} else {
			previewWidth = shortSide;
			previewHeight = longSide;
		}

		// Add the padding of the border.
		previewWidth += hPadding;
		previewHeight += vPadding;

		// Ask children to follow the new preview dimension.
		super.onMeasure(
				MeasureSpec.makeMeasureSpec(previewWidth, MeasureSpec.EXACTLY),
				MeasureSpec.makeMeasureSpec(previewHeight, MeasureSpec.EXACTLY));
	}

//	private void setAspectRatio(double ratio) {
//		if (ratio <= 0.0)
//			throw new IllegalArgumentException();
//
//		has_aspect_ratio = true;
//		if (aspect_ratio != ratio) {
//			aspect_ratio = ratio;
//			
//				WybLog.d(TAG, "new aspect ratio: " + aspect_ratio);
//			requestLayout();
//		}
//	}

	// for the Preview - from
	// http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
	// note, if orientation is locked to landscape this is only called when
	// setting up the activity, and will always have the same orientation
//	private void setCameraDisplayOrientation(Activity activity) {
//		
//			WybLog.d(TAG, "setCameraDisplayOrientation()");
//		Camera.CameraInfo info = new Camera.CameraInfo();
//		Camera.getCameraInfo(cameraId, info);
//		int rotation = activity.getWindowManager().getDefaultDisplay()
//				.getRotation();
//		int degrees = 0;
//		switch (rotation) {
//		case Surface.ROTATION_0:
//			degrees = 0;
//			break;
//		case Surface.ROTATION_90:
//			degrees = 90;
//			break;
//		case Surface.ROTATION_180:
//			degrees = 180;
//			break;
//		case Surface.ROTATION_270:
//			degrees = 270;
//			break;
//		}
//		
//			WybLog.d(TAG, "    degrees = " + degrees);
//
//		int result = 0;
//		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//			result = (info.orientation + degrees) % 360;
//			result = (360 - result) % 360; // compensate the mirror
//		} else { // back-facing
//			result = (info.orientation - degrees + 360) % 360;
//		}
//		 {
//			WybLog.d(TAG, "    info orientation is " + info.orientation);
//			WybLog.d(TAG, "    setDisplayOrientation to " + result);
//		}
//		camera.setDisplayOrientation(result);
//		this.display_orientation = result;
//	}

//	private void onOrientationChanged(int orientation) {
//		if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
//			return;
//		if (camera == null)
//			return;
//		Camera.getCameraInfo(cameraId, camera_info);
//		orientation = (orientation + 45) / 90 * 90;
////		this.current_orientation = orientation % 360;
//		int new_rotation = 0;
//		if (camera_info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//			new_rotation = (camera_info.orientation - orientation + 360) % 360;
//		} else { // back-facing camera
//			new_rotation = (camera_info.orientation + orientation) % 360;
//		}
//		if (new_rotation != current_rotation) {
//			/*
//			 * if( MyDebug.LOG ) { WybLog.d(TAG, "    current_orientation is " +
//			 * current_orientation); WybLog.d(TAG, "    info orientation is " +
//			 * camera_info.orientation); WybLog.d(TAG,
//			 * "    set Camera rotation from " + current_rotation + " to " +
//			 * new_rotation); }
//			 */
//			this.current_rotation = new_rotation;
//		}
//	}

	@Override
	public void onDraw(Canvas canvas) {
		if (this.app_is_paused || autoYes) {
			return;
		}
//		CameraActivity main_activity = (CameraActivity) this.getContext();

		final float scale = getResources().getDisplayMetrics().density;


		// note, no need to check preferences here, as we do that when setting
		// thumbnail_anim
		if (camera != null && this.thumbnail_anim && this.thumbnail != null) {
			long time = System.currentTimeMillis()
					- this.thumbnail_anim_start_ms;
			final long duration = 500;
			if (time > duration) {
				this.thumbnail_anim = false;
			} else {
				thumbnail_anim_src_rect.left = 0;
				thumbnail_anim_src_rect.top = 0;
				thumbnail_anim_src_rect.right = this.thumbnail.getWidth();
				thumbnail_anim_src_rect.bottom = this.thumbnail.getHeight();
				
//				View galleryButton = (View) main_activity
//						.findViewById(R.id.gallery);
				
//				float alpha = ((float) time) / (float) duration;
//
//				int st_x = canvas.getWidth() / 2;
//				int st_y = canvas.getHeight() / 2;
//				int nd_x = galleryButton.getLeft() + galleryButton.getWidth()
//						/ 2;
//				int nd_y = galleryButton.getTop() + galleryButton.getHeight()
//						/ 2;
//				int thumbnail_x = (int) ((1.0f - alpha) * st_x + alpha * nd_x);
//				int thumbnail_y = (int) ((1.0f - alpha) * st_y + alpha * nd_y);

//				float st_w = canvas.getWidth();
//				float st_h = canvas.getHeight();
//				float nd_w = galleryButton.getWidth();
//				float nd_h = galleryButton.getHeight();
				// int thumbnail_w = (int)( (1.0f-alpha)*st_w + alpha*nd_w );
				// int thumbnail_h = (int)( (1.0f-alpha)*st_h + alpha*nd_h );
//				float correction_w = st_w / nd_w - 1.0f;
//				float correction_h = st_h / nd_h - 1.0f;
//				int thumbnail_w = (int) (st_w / (1.0f + alpha * correction_w));
//				int thumbnail_h = (int) (st_h / (1.0f + alpha * correction_h));
//				thumbnail_anim_dst_rect.left = thumbnail_x - thumbnail_w / 2;
//				thumbnail_anim_dst_rect.top = thumbnail_y - thumbnail_h / 2;
//				thumbnail_anim_dst_rect.right = thumbnail_x + thumbnail_w / 2;
//				thumbnail_anim_dst_rect.bottom = thumbnail_y + thumbnail_h / 2;
				// canvas.drawBitmap(this.thumbnail, thumbnail_anim_src_rect,
				// thumbnail_anim_dst_rect, p);
				thumbnail_anim_matrix.setRectToRect(thumbnail_anim_src_rect,
						thumbnail_anim_dst_rect, Matrix.ScaleToFit.FILL);
				// thumbnail_anim_matrix.reset();
				if (ui_rotation == 90 || ui_rotation == 270) {
					float ratio = ((float) thumbnail.getWidth())
							/ (float) thumbnail.getHeight();
					thumbnail_anim_matrix
							.preScale(ratio, 1.0f / ratio,
									thumbnail.getWidth() / 2,
									thumbnail.getHeight() / 2);
				}
				thumbnail_anim_matrix.preRotate(ui_rotation,
						thumbnail.getWidth() / 2, thumbnail.getHeight() / 2);
				canvas.drawBitmap(this.thumbnail, thumbnail_anim_matrix, p);
			}
		}

		canvas.save();
		canvas.rotate(ui_rotation, canvas.getWidth() / 2,
				canvas.getHeight() / 2);

		int text_y = (int) (20 * scale + 0.5f); // convert dps to pixels
		// fine tuning to adjust placement of text with respect to the GUI,
		// depending on orientation
//		int text_base_y = 0;
		if (ui_rotation == 0) {
//			text_base_y = canvas.getHeight() - (int) (0.5 * text_y);
		} else if (ui_rotation == 180) {
//			text_base_y = canvas.getHeight() - (int) (2.5 * text_y);
		} else if (ui_rotation == 90 || ui_rotation == 270) {
			// text_base_y = canvas.getHeight() + (int)(0.5*text_y);
//			ImageButton view = (ImageButton) main_activity
//					.findViewById(R.id.button_camera);
			// align with "top" of the take_photo button, but remember to take
			// the rotation into account!
//			view.getLocationOnScreen(gui_location);
			int view_left = gui_location[0];
			this.getLocationOnScreen(gui_location);
			int this_left = gui_location[0];
			int diff_x = view_left - (this_left + canvas.getWidth() / 2);
			int max_x = canvas.getWidth();
			if (ui_rotation == 90) {
				// so we don't interfere with the top bar info (time, etc)
				max_x -= (int) (1.5 * text_y);
			}
			if (canvas.getWidth() / 2 + diff_x > max_x) {
				// in case goes off the size of the canvas, for "black bar"
				// cases (when preview aspect ratio != screen aspect ratio)
				diff_x = max_x - canvas.getWidth() / 2;
			}
		}
		
		//画出角度线
//		if (pXQJ != null) {
//			canvas.drawLine(mStartX, mStartY, mStopX, mStopY, pXQJ);
//		}

		if (camera != null && this.phase != PHASE_PREVIEW_PAUSED) {
			
		} else if (camera == null) {
			p.setColor(Color.WHITE);
			p.setTextSize(14 * scale + 0.5f); // convert dps to pixels
			p.setTextAlign(Paint.Align.CENTER);
			int pixels_offset = (int) (20 * scale + 0.5f); // convert dps to
															// pixels
			canvas.drawText("FAILED TO OPEN CAMERA.", canvas.getWidth() / 2,
					canvas.getHeight() / 2, p);
			canvas.drawText("CAMERA MAY BE IN USE", canvas.getWidth() / 2,
					canvas.getHeight() / 2 + pixels_offset, p);
			canvas.drawText("BY ANOTHER APPLICATION?", canvas.getWidth() / 2,
					canvas.getHeight() / 2 + 2 * pixels_offset, p);
		}


		boolean store_location = false;
		if (store_location) {
			int location_x = (int) (20 * scale + 0.5f); // convert dps to pixels
			int location_y = (int) (5 * scale + 0.5f); // convert dps to pixels
			int location_size = (int) (20 * scale + 0.5f); // convert dps to
															// pixels
			if (ui_rotation == 90 || ui_rotation == 270) {
				int diff = canvas.getWidth() - canvas.getHeight();
				location_x += diff / 2;
				location_y -= diff / 2;
			}
			if (ui_rotation == 90) {
				location_y = canvas.getHeight() - location_y - location_size;
			}
			if (ui_rotation == (ui_placement_right ? 180 : 0)) {
				location_x = canvas.getWidth() - location_x - location_size;
			}
			location_dest.set(location_x, location_y, location_x
					+ location_size, location_y + location_size);
			if (has_set_location) {
				canvas.drawBitmap(location_bitmap, null, location_dest, p);
				int indicator_x = location_x + location_size;
				int indicator_y = location_y;
				p.setStyle(Paint.Style.FILL_AND_STROKE);
				p.setColor(location_accuracy < 25.01f ? Color.GREEN
						: Color.YELLOW);
				canvas.drawCircle(indicator_x, indicator_y, location_size / 10,
						p);
			} else {
				canvas.drawBitmap(location_off_bitmap, null, location_dest, p);
			}
		}

		canvas.restore();

//
//		WybLog.e(TAG, "FOCUS_DONE============="+FOCUS_DONE);
		if (this.focus_success != FOCUS_DONE) {
			int size = (int) (50 * scale + 0.5f); // convert dps to pixels
			if (this.focus_success == FOCUS_SUCCESS){
				p.setColor(Color.GREEN);
//				WybLog.e(TAG, "Green=============");
			}
			else if (this.focus_success == FOCUS_FAILED){
				p.setColor(Color.RED);
//				WybLog.e(TAG, "Red=============");
			}
			else{
				p.setColor(Color.WHITE);
//				WybLog.e(TAG, "White=============");
			}
			p.setStyle(Paint.Style.STROKE);
			int pos_x = 0;
			int pos_y = 0;
			if (has_focus_area) {
				pos_x = focus_screen_x;
				pos_y = focus_screen_y;
			} else {
				pos_x = canvas.getWidth() / 2;
				pos_y = canvas.getHeight() / 2;
			}
			canvas.drawRect(pos_x - size, pos_y - size, pos_x + size, pos_y
					+ size, p);
			if (focus_complete_time != -1
					&& System.currentTimeMillis() > focus_complete_time + 1000) {
				focus_success = FOCUS_DONE;
			}
			p.setStyle(Paint.Style.FILL); // reset
		}
		if (this.using_face_detection && this.faces_detected != null) {
			p.setColor(Color.YELLOW);
			p.setStyle(Paint.Style.STROKE);
			for (Face face : faces_detected) {
				// Android doc recommends filtering out faces with score less
				// than 50
				if (face.score >= 50) {
					calculateCameraToPreviewMatrix();
					face_rect.set(face.rect);
					this.camera_to_preview_matrix.mapRect(face_rect);
					canvas.drawRect(face_rect, p);
				}
			}
			p.setStyle(Paint.Style.FILL); // reset
		}
	}
	
//	private Paint pXQJ = null;
//	
//	//起始坐标点X，Y，结束坐标点X,Y
//	private float mStartX,mStartY;
//	
//	public float mStopX,mStopY;
//	
//	public void setXQJ(float startX,float startY,float stopX,float stopY){
//		if (pXQJ == null) {
//			pXQJ = new Paint();
//			pXQJ.setColor(Color.YELLOW);
//			pXQJ.setStrokeWidth(3f);
//			pXQJ.setAntiAlias(true);//锯齿
//			pXQJ.setDither(true);//抖动效果 
//		}
//		this.mStartX = startX;
//		this.mStartY = startY;
//		this.mStopX = stopX;
//		this.mStopY = stopY;
//	}

//	private void drawTextWithBackground(Canvas canvas, Paint paint,
//			String text, int foreground, int background, int location_x,
//			int location_y) {
//		final float scale = getResources().getDisplayMetrics().density;
//		p.setStyle(Paint.Style.FILL);
//		paint.setColor(background);
//		paint.setAlpha(127);
//		paint.getTextBounds(text, 0, text.length(), text_bounds);
//		final int padding = (int) (2 * scale + 0.5f); // convert dps to pixels
//		if (paint.getTextAlign() == Paint.Align.RIGHT
//				|| paint.getTextAlign() == Paint.Align.CENTER) {
//			float width = paint.measureText(text); // n.b., need to use
//													// measureText rather than
//													// getTextBounds here
//			if (paint.getTextAlign() == Paint.Align.CENTER)
//				width /= 2.0f;
//			text_bounds.left -= width;
//			text_bounds.right -= width;
//		}
//		text_bounds.left += location_x - padding;
//		text_bounds.top += location_y - padding;
//		text_bounds.right += location_x + padding;
//		text_bounds.bottom += location_y + padding;
//		canvas.drawRect(text_bounds, paint);
//		paint.setColor(foreground);
//		canvas.drawText(text, location_x, location_y, paint);
//	}

	public void scaleZoom(float scale_factor) {
		
			WybLog.d(TAG, "scaleZoom() " + scale_factor);
		if (this.camera != null && this.has_zoom) {
			float zoom_ratio = this.zoom_ratios.get(zoom_factor) / 100.0f;
			zoom_ratio *= scale_factor;

			int new_zoom_factor = zoom_factor;
			if (zoom_ratio <= 1.0f) {
				new_zoom_factor = 0;
			} else if (zoom_ratio >= zoom_ratios.get(max_zoom_factor) / 100.0f) {
				new_zoom_factor = max_zoom_factor;
			} else {
				// find the closest zoom level
				if (scale_factor > 1.0f) {
					// zooming in
					for (int i = zoom_factor; i < zoom_ratios.size(); i++) {
						if (zoom_ratios.get(i) / 100.0f >= zoom_ratio) {
							
								WybLog.d(TAG,
										"zoom int, found new zoom by comparing "
												+ zoom_ratios.get(i) / 100.0f
												+ " >= " + zoom_ratio);
							new_zoom_factor = i;
							break;
						}
					}
				} else {
					// zooming out
					for (int i = zoom_factor; i >= 0; i--) {
						if (zoom_ratios.get(i) / 100.0f <= zoom_ratio) {
							
								WybLog.d(TAG,
										"zoom out, found new zoom by comparing "
												+ zoom_ratios.get(i) / 100.0f
												+ " <= " + zoom_ratio);
							new_zoom_factor = i;
							break;
						}
					}
				}
			}
			 {
				WybLog.d(TAG, "ScaleListener.onScale zoom_ratio is now "
						+ zoom_ratio);
				WybLog.d(TAG, "    old zoom_factor " + zoom_factor + " ratio "
						+ zoom_ratios.get(zoom_factor) / 100.0f);
				WybLog.d(TAG, "    chosen new zoom_factor " + new_zoom_factor
						+ " ratio " + zoom_ratios.get(new_zoom_factor) / 100.0f);
			}
			zoomTo(new_zoom_factor, true);
		}
	}

	public void zoomIn() {
		
			WybLog.d(TAG, "zoomIn()");
		if (zoom_factor < max_zoom_factor) {
			zoomTo(zoom_factor + 1, true);
		}
	}

	public void zoomOut() {
		
			WybLog.d(TAG, "zoomOut()");
		if (zoom_factor > 0) {
			zoomTo(zoom_factor - 1, true);
		}
	}

	public void zoomTo(int new_zoom_factor, boolean update_seek_bar) {
		
			WybLog.d(TAG, "ZoomTo(): " + new_zoom_factor);
		if (new_zoom_factor < 0)
			new_zoom_factor = 0;
		if (new_zoom_factor > max_zoom_factor)
			new_zoom_factor = max_zoom_factor;
		// problem where we crashed due to calling this function with null
		// camera should be fixed now, but check again just to be safe
		if (new_zoom_factor != zoom_factor && camera != null) {
			Parameters parameters = camera.getParameters();
			if (parameters.isZoomSupported()) {
				
					WybLog.d(TAG, "zoom was: " + parameters.getZoom());
				parameters.setZoom((int) new_zoom_factor);
				try {
					camera.setParameters(parameters);
					zoom_factor = new_zoom_factor;
					if (update_seek_bar) {
						Activity activity = (Activity) this.getContext();
						SeekBar zoomSeekBar = (SeekBar) activity
								.findViewById(R.id.zoomBar);
						zoomSeekBar.setProgress(zoom_factor);
					}
				} catch (RuntimeException e) {
					// crash reported in v1.3 on device
					// "PANTONE 5 SoftBank 107SH (SBM107SH)"
					
						WybLog.e(TAG, "runtime exception in ZoomTo()");
					e.printStackTrace();
				}
				clearFocusAreas();
			}
		}
	}

	public void changeExposure(int change, boolean update_seek_bar) {
		
			WybLog.d(TAG, "changeExposure(): " + change);
		if (change != 0 && camera != null
				&& (min_exposure != 0 || max_exposure != 0)) {
			Parameters parameters = camera.getParameters();
			int current_exposure = parameters.getExposureCompensation();
			int new_exposure = current_exposure + change;
			setExposure(new_exposure, update_seek_bar);
		}
	}

	public void setExposure(int new_exposure, boolean update_seek_bar) {
		
			WybLog.d(TAG, "setExposure(): " + new_exposure);
		if (camera != null && (min_exposure != 0 || max_exposure != 0)) {
			cancelAutoFocus();
			Parameters parameters = camera.getParameters();
			int current_exposure = parameters.getExposureCompensation();
			if (new_exposure < min_exposure)
				new_exposure = min_exposure;
			if (new_exposure > max_exposure)
				new_exposure = max_exposure;
			if (new_exposure != current_exposure) {
				
					WybLog.d(TAG, "change exposure from " + current_exposure
							+ " to " + new_exposure);
				parameters.setExposureCompensation(new_exposure);
				try {
					camera.setParameters(parameters);
					// now save
//					SharedPreferences sharedPreferences = PreferenceManager
//							.getDefaultSharedPreferences(this.getContext());
//					SharedPreferences.Editor editor = sharedPreferences.edit();
//					editor.putString(getExposurePreferenceKey(), ""
//							+ new_exposure);
//					editor.apply();
					showToast(change_exposure_toast, "Exposure compensation "
							+ new_exposure);
					if (update_seek_bar) {
//						MainActivity main_activity = (MainActivity) this
//								.getContext();
//						main_activity.setSeekBarExposure();
					}
				} catch (RuntimeException e) {
					// just to be safe
					
						WybLog.e(TAG, "runtime exception in changeExposure()");
					e.printStackTrace();
				}
			}
		}
	}

	void switchCamera() {
		
			WybLog.d(TAG, "switchCamera()");
		if (this.phase == PHASE_TAKING_PHOTO) {
			// just to be safe - risk of cancelling the autofocus before taking
			// a photo, or otherwise messing things up
			
				WybLog.d(TAG, "currently taking a photo");
			return;
		}
		int n_cameras = Camera.getNumberOfCameras();
		
			WybLog.d(TAG, "found " + n_cameras + " cameras");
		if (n_cameras > 1) {
			closeCamera();
			cameraId = (cameraId + 1) % n_cameras;
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(cameraId, info);
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				showToast(switch_camera_toast, "Front Camera");
			} else {
				showToast(switch_camera_toast, "Back Camera");
			}
			this.openCamera();

		}
	}

	public void cycleFlash() {
//		pXQJ = null;
		
			WybLog.d(TAG, "cycleFlash()");
		// if( is_taking_photo && !is_taking_photo_on_timer ) {
		if (this.phase == PHASE_TAKING_PHOTO) {
			// just to be safe - risk of cancelling the autofocus before taking
			// a photo, or otherwise messing things up
			
				WybLog.d(TAG, "currently taking a photo");
			return;
		}
		if (this.supported_flash_values != null
				&& this.supported_flash_values.size() > 1) {
			int new_flash_index = (current_flash_index + 1)
					% this.supported_flash_values.size();
			updateFlash(new_flash_index);

			// now save
			String flash_value = supported_flash_values
					.get(current_flash_index);
			 {
				WybLog.d(TAG, "save new flash_value: " + flash_value);
			}
//			SharedPreferences sharedPreferences = PreferenceManager
//					.getDefaultSharedPreferences(this.getContext());
//			SharedPreferences.Editor editor = sharedPreferences.edit();
//			editor.putString(getFlashPreferenceKey(cameraId), flash_value);
//			editor.apply();
		}
	}

	private boolean updateFlash(String flash_value) {
		
			WybLog.d(TAG, "updateFlash(): " + flash_value);
		if (supported_flash_values != null) {
			int new_flash_index = supported_flash_values.indexOf(flash_value);
			
				WybLog.d(TAG, "new_flash_index: " + new_flash_index);
			if (new_flash_index != -1) {
				updateFlash(new_flash_index);
				return true;
			}
		}
		return false;
	}

	private void updateFlash(int new_flash_index) {
		
			WybLog.d(TAG, "updateFlash(): " + new_flash_index);
		// updates the Flash button, and Flash camera mode
		if (supported_flash_values != null
				&& new_flash_index != current_flash_index) {
			boolean initial = current_flash_index == -1;
			current_flash_index = new_flash_index;
			
				WybLog.d(TAG, "    current_flash_index is now "
						+ current_flash_index + " (initial " + initial + ")");

			Activity activity = (Activity) this.getContext();
			ImageView flashButton = (ImageView) activity
					.findViewById(R.id.flash);
//			String[] flash_entries = getResources().getStringArray(
//					R.array.flash_entries);
//			String[] flash_icons = getResources().getStringArray(
//					R.array.flash_icons);
			//修改
			String[] flash_entries = getResources().getStringArray(
					R.array.flash_entries);
			String[] flash_icons = getResources().getStringArray(
					R.array.flash_icons);
			String flash_value = supported_flash_values
					.get(current_flash_index);
			
				WybLog.d(TAG, "    flash_value: " + flash_value);
//			String[] flash_values = getResources().getStringArray(
//					R.array.flash_values);
			String[] flash_values = getResources().getStringArray(
					R.array.flash_values);
			for (int i = 0; i < flash_values.length; i++) {
				/*
				 * if( MyDebug.LOG ) WybLog.d(TAG, "    compare to: " +
				 * flash_values[i]);
				 */
				if (flash_value.equals(flash_values[i])) {
					
						WybLog.d(TAG, "    found entry: " + i);
					// flashButton.setText(flash_entries[i]);
					int resource = getResources().getIdentifier(flash_icons[i],
							null,
							activity.getApplicationContext().getPackageName());
					flashButton.setImageResource(resource);
					if (!initial) {
//						showToast(flash_toast, flash_entries[i]);
						MyToast.getInstance(SpeedTest5g.getContext()).showCommon(flash_entries[i]);
					}
					break;
				}
			}
			this.setFlash(flash_value);
		}
	}

	private void setFlash(String flash_value) {
		
			WybLog.d(TAG, "setFlash() " + flash_value);
		set_flash_after_autofocus = ""; // this overrides any previously saved
										// setting, for during the startup
										// autofocus
		cancelAutoFocus();
		Parameters parameters = camera.getParameters();
		String flash_mode = convertFlashValueToMode(flash_value);
		if (flash_mode.length() > 0
				&& !flash_mode.equals(parameters.getFlashMode())) {
			parameters.setFlashMode(flash_mode);
			camera.setParameters(parameters);
		}
	}

	// this returns the flash mode indicated by the UI, rather than from the
	// camera parameters (may be different, e.g., in startup autofocus!)
	public String getCurrentFlashMode() {
		if (current_flash_index == -1)
			return null;
		String flash_value = supported_flash_values.get(current_flash_index);
		String flash_mode = convertFlashValueToMode(flash_value);
		return flash_mode;
	}

	private String convertFlashValueToMode(String flash_value) {
		String flash_mode = "";
		if (flash_value.equals("flash_off")) {
			flash_mode = Parameters.FLASH_MODE_OFF;
		} else if (flash_value.equals("flash_auto")) {
			flash_mode = Parameters.FLASH_MODE_AUTO;
		} else if (flash_value.equals("flash_on")) {
			flash_mode = Parameters.FLASH_MODE_ON;
		} else if (flash_value.equals("flash_torch")) {
			flash_mode = Parameters.FLASH_MODE_TORCH;
		} else if (flash_value.equals("flash_red_eye")) {
			flash_mode = Parameters.FLASH_MODE_RED_EYE;
		}
		return flash_mode;
	}

	private List<String> convertFlashModesToValues(
			List<String> supported_flash_modes) {
		
			WybLog.d(TAG, "convertFlashModesToValues()");
		List<String> output_modes = new Vector<String>();
		if (supported_flash_modes != null) {
			// also resort as well as converting
			// first one will be the default choice
			if (supported_flash_modes
					.contains(Parameters.FLASH_MODE_AUTO)) {
				output_modes.add("flash_auto");
				
					WybLog.d(TAG, " supports flash_auto");
			}
			if (supported_flash_modes
					.contains(Parameters.FLASH_MODE_OFF)) {
				output_modes.add("flash_off");
				
					WybLog.d(TAG, " supports flash_off");
			}
			if (supported_flash_modes.contains(Parameters.FLASH_MODE_ON)) {
				output_modes.add("flash_on");
				
					WybLog.d(TAG, " supports flash_on");
			}
			if (supported_flash_modes
					.contains(Parameters.FLASH_MODE_TORCH)) {
				output_modes.add("flash_torch");
				
					WybLog.d(TAG, " supports flash_torch");
			}
			if (supported_flash_modes
					.contains(Parameters.FLASH_MODE_RED_EYE)) {
				output_modes.add("flash_red_eye");
				
					WybLog.d(TAG, " supports flash_red_eye");
			}
		}
		return output_modes;
	}

	void cycleFocusMode() {
		
			WybLog.d(TAG, "cycleFocusMode()");
		// if( is_taking_photo && !is_taking_photo_on_timer ) {
		if (this.phase == PHASE_TAKING_PHOTO) {
			// just to be safe - otherwise problem that changing the focus mode
			// will cancel the autofocus before taking a photo, so we never take
			// a photo, but is_taking_photo remains true!
			
				WybLog.d(TAG, "currently taking a photo");
			return;
		}
		if (this.supported_focus_values != null
				&& this.supported_focus_values.size() > 1) {
			int new_focus_index = (current_focus_index + 1)
					% this.supported_focus_values.size();
			updateFocus(new_focus_index, false, true, true);
		}
	}

	private boolean updateFocus(String focus_value, boolean quiet,
			boolean save, boolean auto_focus) {
		
			WybLog.d(TAG, "updateFocus(): " + focus_value);
		if (this.supported_focus_values != null) {
			int new_focus_index = supported_focus_values.indexOf(focus_value);
			
				WybLog.d(TAG, "new_focus_index: " + new_focus_index);
			if (new_focus_index != -1) {
				updateFocus(new_focus_index, quiet, save, auto_focus);
				return true;
			}
		}
		return false;
	}

	private void updateFocus(int new_focus_index, boolean quiet, boolean save,
			boolean auto_focus) {
		
			WybLog.d(TAG, "updateFocus(): " + new_focus_index
					+ " current_focus_index: " + current_focus_index);
		// updates the Focus button, and Focus camera mode
		if (this.supported_focus_values != null
				&& new_focus_index != current_focus_index) {
			boolean initial = current_focus_index == -1;
			current_focus_index = new_focus_index;
			
				WybLog.d(TAG, "    current_focus_index is now "
						+ current_focus_index + " (initial " + initial + ")");

//			Activity activity = (Activity) this.getContext();
//			ImageButton focusModeButton = (ImageButton) activity
//					.findViewById(R.id.focus_mode);
//			String[] focus_entries = getResources().getStringArray(
//					R.array.focus_mode_entries);
//			String[] focus_icons = getResources().getStringArray(
//					R.array.focus_mode_icons);
			//修改
			String[] focus_entries = {"flash_off","flash_auto","flash_on","flash_torch","flash_red_eye"};
//			String[] focus_icons = {"drawable/btn_button_x","drawable/btn_button_x","drawable/btn_button_x","drawable/btn_button_x","drawable/btn_button_x","drawable/btn_button_x"};
			
			String focus_value = supported_focus_values
					.get(current_focus_index);
			
				WybLog.d(TAG, "    focus_value: " + focus_value);
//			String[] focus_values = getResources().getStringArray(
//					R.array.focus_mode_values);
			String[] focus_values = {"flash_off","flash_auto","flash_on","flash_torch","flash_red_eye"};
			for (int i = 0; i < focus_values.length; i++) {
				
					WybLog.d(TAG, "    compare to: " + focus_values[i]);
				if (focus_value.equals(focus_values[i])) {
					
						WybLog.d(TAG, "    found entry: " + i);
//					int resource = getResources().getIdentifier(focus_icons[i],
//							null,
//							activity.getApplicationContext().getPackageName());
//					focusModeButton.setImageResource(resource);
					if (!initial && !quiet) {
						showToast(focus_toast, focus_entries[i]);
					}
					break;
				}
			}
			this.setFocus(focus_value, auto_focus);

//			if (save) {
//				// now save
//				SharedPreferences sharedPreferences = PreferenceManager
//						.getDefaultSharedPreferences(this.getContext());
//				SharedPreferences.Editor editor = sharedPreferences.edit();
//				editor.putString(getFocusPreferenceKey(cameraId), focus_value);
//				editor.apply();
//			}
		}
	}

	private void setFocus(String focus_value, boolean auto_focus) {
		
			WybLog.d(TAG, "setFocus() " + focus_value);
		if (camera == null) {
			
				WybLog.d(TAG, "null camera");
			return;
		}
		cancelAutoFocus();
		Parameters parameters = camera.getParameters();
		if (focus_value.equals("focus_mode_auto")) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
		} else if (focus_value.equals("focus_mode_infinity")) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_INFINITY);
		} else if (focus_value.equals("focus_mode_macro")) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_MACRO);
		} else if (focus_value.equals("focus_mode_fixed")) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_FIXED);
		} else if (focus_value.equals("focus_mode_edof")) {
			parameters.setFocusMode(Parameters.FOCUS_MODE_EDOF);
		} else if (focus_value.equals("focus_mode_continuous_video")) {
			parameters
					.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		} else {
			
				WybLog.d(TAG, "setFocus() received unknown focus value "
						+ focus_value);
		}
		camera.setParameters(parameters);
		clearFocusAreas();
		if (auto_focus) {
			tryAutoFocus(false, false);
		}
	}

	/*void toggleExposureLock() {
		
			WybLog.d(TAG, "toggleExposureLock()");
		// n.b., need to allow when recording video, so no check on
		// PHASE_TAKING_PHOTO
		if (camera == null) {
			
				WybLog.d(TAG, "null camera");
			return;
		}
		if (is_exposure_locked_supported) {
			is_exposure_locked = !is_exposure_locked;
			setExposureLocked();
			showToast(exposure_lock_toast,
					is_exposure_locked ? "Exposure Locked"
							: "Exposure Unlocked");
		}
	}*/

	/*private void setExposureLocked() {
		if (camera == null) {
			
				WybLog.d(TAG, "null camera");
			return;
		}
		if (is_exposure_locked_supported) {
			cancelAutoFocus();
			Camera.Parameters parameters = camera.getParameters();
			parameters.setAutoExposureLock(is_exposure_locked);
			camera.setParameters(parameters);
			Activity activity = (Activity) this.getContext();
			ImageButton exposureLockButton = (ImageButton) activity
					.findViewById(R.id.exposure_lock);
			exposureLockButton
					.setImageResource(is_exposure_locked ? R.drawable.exposure_locked
							: R.drawable.exposure_unlocked);
		}
	}*/

	private List<String> convertFocusModesToValues(
			List<String> supported_focus_modes) {
		
			WybLog.d(TAG, "convertFocusModesToValues()");
		List<String> output_modes = new Vector<String>();
		if (supported_focus_modes != null) {
			// also resort as well as converting
			// first one will be the default choice
			if (supported_focus_modes
					.contains(Parameters.FOCUS_MODE_AUTO)) {
				output_modes.add("focus_mode_auto");
				
					WybLog.d(TAG, " supports focus_mode_auto");
			}
			if (supported_focus_modes
					.contains(Parameters.FOCUS_MODE_INFINITY)) {
				output_modes.add("focus_mode_infinity");
				
					WybLog.d(TAG, " supports focus_mode_infinity");
			}
			if (supported_focus_modes
					.contains(Parameters.FOCUS_MODE_MACRO)) {
				output_modes.add("focus_mode_macro");
				
					WybLog.d(TAG, " supports focus_mode_macro");
			}
			if (supported_focus_modes
					.contains(Parameters.FOCUS_MODE_FIXED)) {
				output_modes.add("focus_mode_fixed");
				
					WybLog.d(TAG, " supports focus_mode_fixed");
			}
			if (supported_focus_modes
					.contains(Parameters.FOCUS_MODE_EDOF)) {
				output_modes.add("focus_mode_edof");
				
					WybLog.d(TAG, " supports focus_mode_edof");
			}
			if (supported_focus_modes
					.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
				output_modes.add("focus_mode_continuous_video");
				
					WybLog.d(TAG, " supports focus_mode_continuous_video");
			}
		}
		return output_modes;
	}

	TakePictureCallback mTakePictureCallback;
	public void takePicturePressed(TakePictureCallback tpcb) {
		mTakePictureCallback = tpcb;
		
			WybLog.d(TAG, "takePicturePressed");
		if (camera == null) {
			
				WybLog.d(TAG, "camera not available");
			/*
			 * is_taking_photo_on_timer = false; is_taking_photo = false;
			 */
			this.phase = PHASE_NORMAL;
			return;
		}
		if (!this.has_surface) {
			
				WybLog.d(TAG, "preview surface not yet available");
			/*
			 * is_taking_photo_on_timer = false; is_taking_photo = false;
			 */
			this.phase = PHASE_NORMAL;
			return;
		}
		// if( is_taking_photo_on_timer ) {
		if (this.isOnTimer()) {
			takePictureTimerTask.cancel();
			if (beepTimerTask != null) {
				beepTimerTask.cancel();
			}
			/*
			 * is_taking_photo_on_timer = false; is_taking_photo = false;
			 */
			this.phase = PHASE_NORMAL;
			
				WybLog.d(TAG, "cancelled camera timer");
			showToast(take_photo_toast, "Cancelled timer");
			return;
		}
		// if( is_taking_photo ) {
		if (this.phase == PHASE_TAKING_PHOTO) {
			return;
		}

		// make sure that preview running (also needed to hide trash/share
		// icons)
		this.startCameraPreview();

		// is_taking_photo = true;
//		SharedPreferences sharedPreferences = PreferenceManager
//				.getDefaultSharedPreferences(this.getContext());
//		String timer_value = sharedPreferences.getString("preference_timer",
//				"0");
		//������ʱ
		String timer_value = "0";
		long timer_delay = 0;
		try {
			timer_delay = Integer.parseInt(timer_value) * 1000;
		} catch (NumberFormatException e) {
			
				WybLog.e(TAG, "failed to parse timer_value: " + timer_value);
			e.printStackTrace();
			timer_delay = 0;
		}

//		String burst_mode_value = sharedPreferences.getString(
//				"preference_burst_mode", "1");
		
		//�����ع�
		String burst_mode_value = "1";
		try {
			n_burst = Integer.parseInt(burst_mode_value);
			
				WybLog.d(TAG, "n_burst: " + n_burst);
		} catch (NumberFormatException e) {
			
				WybLog.e(TAG, "failed to parse burst_mode_value: "
						+ burst_mode_value);
			e.printStackTrace();
			n_burst = 1;
		}
		remaining_burst_photos = n_burst - 1;

		if (timer_delay == 0) {
			takePicture();
		} else {
			takePictureOnTimer(timer_delay, false);
		}
	}

	private void takePictureOnTimer(long timer_delay, boolean repeated) {
		 {
			WybLog.d(TAG, "takePictureOnTimer");
			WybLog.d(TAG, "timer_delay: " + timer_delay);
		}
		this.phase = PHASE_TIMER;
		class TakePictureTimerTask extends TimerTask {
			public void run() {
				if (beepTimerTask != null) {
					beepTimerTask.cancel();
				}
				takePicture();
			}
		}
		take_photo_time = System.currentTimeMillis() + timer_delay;
		
			WybLog.d(TAG, "take photo at: " + take_photo_time);
		if (!repeated) {
			showToast(take_photo_toast, "Started timer");
		}
		takePictureTimer.schedule(
				takePictureTimerTask = new TakePictureTimerTask(), timer_delay);

		//����������
//		SharedPreferences sharedPreferences = PreferenceManager
//				.getDefaultSharedPreferences(this.getContext());
//		if (sharedPreferences.getBoolean("preference_timer_beep", true)) {
//			class BeepTimerTask extends TimerTask {
//				public void run() {
//					try {
//						Uri notification = RingtoneManager
//								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//						Activity activity = (Activity) getContext();
//						Ringtone r = RingtoneManager.getRingtone(
//								activity.getApplicationContext(), notification);
//						r.play();
//					} catch (Exception e) {
//					}
//				}
//			}
//			beepTimer.schedule(beepTimerTask = new BeepTimerTask(), 0, 1000);
//		}
	}

	private void takePicture() {
		
			WybLog.d(TAG, "takePicture");
		this.thumbnail_anim = false;
		this.phase = PHASE_TAKING_PHOTO;
		if (camera == null) {
			
				WybLog.d(TAG, "camera not available");
			/*
			 * is_taking_photo_on_timer = false; is_taking_photo = false;
			 */
			this.phase = PHASE_NORMAL;
			showGUI(true);
			return;
		}
		if (!this.has_surface) {
			
				WybLog.d(TAG, "preview surface not yet available");
			/*
			 * is_taking_photo_on_timer = false; is_taking_photo = false;
			 */
			this.phase = PHASE_NORMAL;
			showGUI(true);
			return;
		}
		focus_success = FOCUS_DONE; // clear focus rectangle

//		updateParametersFromLocation();
		showGUI(false);
		Parameters parameters = camera.getParameters();
		String focus_mode = parameters.getFocusMode();
		
			WybLog.d(TAG, "focus_mode is " + focus_mode);

		if (this.successfully_focused
				&& System.currentTimeMillis() < this.successfully_focused_time + 5000) {
			
				WybLog.d(TAG,
						"recently focused successfully, so no need to refocus");
			isTakePictureWhenFocused();
		} else if (focus_mode.equals(Parameters.FOCUS_MODE_AUTO)
				|| focus_mode.equals(Parameters.FOCUS_MODE_MACRO)) {
			Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					
						WybLog.d(TAG, "autofocus complete: " + success);
					isTakePictureWhenFocused();
				}
			};
			
				WybLog.d(TAG, "start autofocus to take picture");
			try {
				camera.autoFocus(autoFocusCallback);
				count_cameraAutoFocus++;
			} catch (RuntimeException e) {
				// just in case? We got a RuntimeException report here from 1
				// user on Google Play:
				// 21 Dec 2013, Xperia Go, Android 4.1
				autoFocusCallback.onAutoFocus(false, camera);

				
					WybLog.e(TAG,
							"runtime exception from autoFocus when trying to take photo");
				e.printStackTrace();
			}
		} else {
			isTakePictureWhenFocused();
		}
	}

	boolean isCanTakePicture = false;

	private void isTakePictureWhenFocused() {
		if (!isCanTakePicture) {
			isCanTakePicture = true;
			takePictureWhenFocused();
		} else {
			return;
		}
	}
	
	

	private void takePictureWhenFocused() {
		// should be called when auto-focused

		
			WybLog.d(TAG, "takePictureWhenFocused");
		if (camera == null) {
			
				WybLog.d(TAG, "camera not available");
			this.phase = PHASE_NORMAL;
			showGUI(true);
			return;
		}
		if (!this.has_surface) {
			
				WybLog.d(TAG, "preview surface not yet available");
			this.phase = PHASE_NORMAL;
			showGUI(true);
			return;
		}
		successfully_focused = false; // so next photo taken will require an
										// autofocus
		
			WybLog.d(TAG, "remaining_burst_photos: " + remaining_burst_photos);

		//拍照咔嚓声音
//		Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
//			// don't do anything here, but we need to implement the callback to
//			// get the shutter sound (at least on Galaxy Nexus and Nexus 7)
//			public void onShutter() {
//				
//					WybLog.d(TAG, "shutterCallback.onShutter()");
//			}
//		};

		Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
			public void onPictureTaken(byte[] data, Camera cam) {
				// n.b., this is automatically run in a different thread
				System.gc();
				
					WybLog.d(TAG, "onPictureTaken");
				

				mTakePictureCallback.takePicture(data);

				/*--------------------------------------------------------------*/
				is_preview_started = false; // preview automatically stopped due
											// to taking photo
				phase = PHASE_NORMAL; // need to set this even if remaining
										// burst photos, so we can restart the
										// preview
				if (remaining_burst_photos > 0) {
					// we need to restart the preview; and we do this in the
					// callback, as we need to restart after saving the image
					// (otherwise this can fail, at least on Nexus 7)
					startCameraPreview();
					
						WybLog.d(TAG,
								"burst mode photos remaining: onPictureTaken started preview");
				} else {
					phase = PHASE_NORMAL;
//					boolean pause_preview = sharedPreferences.getBoolean(
//							"preference_pause_preview", false);
					
					boolean pause_preview = false;
					
						WybLog.d(TAG, "pause_preview? " + pause_preview);
					if (pause_preview) {
						setPreviewPaused(true);
//						preview_image_name = picFileName;
					} else {
						// we need to restart the preview; and we do this in the
						// callback, as we need to restart after saving the
						// image
						// (otherwise this can fail, at least on Nexus 7)
//						startCameraPreview();
//						showGUI(true);
						
							WybLog.d(TAG, "onPictureTaken started preview");
					}
				}

				
				System.gc();
				if (remaining_burst_photos > 0) {
					remaining_burst_photos--;

//					String timer_value = sharedPreferences.getString(
//							"preference_burst_interval", "0");
					String timer_value = "0";
					long timer_delay = 0;
					try {
						timer_delay = Integer.parseInt(timer_value) * 1000;
					} catch (NumberFormatException e) {
						
							WybLog.e(TAG, "failed to parse timer_value: "
									+ timer_value);
						e.printStackTrace();
						timer_delay = 0;
					}

					if (timer_delay == 0) {
						// we go straight to taking a photo rather than
						// refocusing, for speed
						// need to manually set the phase and rehide the GUI
						phase = PHASE_TAKING_PHOTO;
						showGUI(false);
						// takePictureWhenFocused();
						isTakePictureWhenFocused();
					} else {
						takePictureOnTimer(timer_delay, true);
					}
				}
				isCanTakePicture = false;
			}
		};

		{
			
				WybLog.d(TAG, "current_rotation: " + current_rotation);
			Parameters parameters = camera.getParameters();
			parameters.setRotation(current_rotation);
			camera.setParameters(parameters);
			
				WybLog.d(TAG, "about to call takePicture");
			String toast_text = "";
			if (n_burst > 1) {
				int photo = (n_burst - remaining_burst_photos);
				toast_text = "Taking photo... (" + photo + " / " + n_burst
						+ ")";
			} else {
				toast_text = "Taking photo...";
			}
			
				WybLog.d(TAG, toast_text);

			try {
				//TODO ��������
				camera.takePicture(null, null, jpegPictureCallback);
//				camera.takePicture(null, null, jpegPictureCallback);
				count_cameraTakePicture++;
//				showToast(take_photo_toast, toast_text);
			} catch (RuntimeException e) {
				// just in case? We got a RuntimeException report here from 1
				// user on Google Play; I also encountered it myself once of
				// Galaxy Nexus when starting up
				
					WybLog.e(TAG, "runtime exception from takePicture");
				e.printStackTrace();
//				showToast(null, "Failed to take picture");
			}
		}
		
			WybLog.d(TAG, "takePicture exit");
	}

	/*void clickedShare() {
		
			WybLog.d(TAG, "clickedShare");
		if (this.phase == PHASE_PREVIEW_PAUSED) {
			if (preview_image_name != null) {
				
					WybLog.d(TAG, "Share: " + preview_image_name);
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/jpeg");
				intent.putExtra(Intent.EXTRA_STREAM,
						Uri.parse("file://" + preview_image_name));
				Activity activity = (Activity) this.getContext();
				activity.startActivity(Intent.createChooser(intent, "Photo"));
			}
			startCameraPreview();
			tryAutoFocus(false, false);
		}
	}*/

	/*void clickedTrash() {
		
			WybLog.d(TAG, "clickedTrash");
		// if( is_preview_paused ) {
		if (this.phase == PHASE_PREVIEW_PAUSED) {
			if (preview_image_name != null) {
				
					WybLog.d(TAG, "Delete: " + preview_image_name);
				File file = new File(preview_image_name);
				if (!file.delete()) {
					
						WybLog.e(TAG, "failed to delete " + preview_image_name);
				} else {
					
						WybLog.d(TAG, "successfully deleted " + preview_image_name);
					showToast(null, "Photo deleted");
					PoleCameraActivity main_activity = (PoleCameraActivity) this
							.getContext();
					main_activity.broadcastFile(file);
				}
			}
			startCameraPreview();
			tryAutoFocus(false, false);
		}
	}*/

	private void tryAutoFocus(final boolean startup, final boolean manual) {
		// manual: whether user has requested autofocus (by touching screen)
		
			WybLog.d(TAG, "tryAutoFocus");
		if (camera == null) {
			
				WybLog.d(TAG, "no camera");
		} else if (!this.has_surface) {
			
				WybLog.d(TAG, "preview surface not yet available");
		} else if (!this.is_preview_started) {
			
				WybLog.d(TAG, "preview not yet started");
		}
		// else if( is_taking_photo ) {
		else if (this.isTakingPhotoOrOnTimer()) {
			
				WybLog.d(TAG, "currently taking a photo");
		} else {
			// it's only worth doing autofocus when autofocus has an effect
			// (i.e., auto or macro mode)
			Parameters parameters = null;
			try {
				parameters = camera.getParameters();
			} catch (Exception e) {
				return;
			}
			String focus_mode = parameters.getFocusMode();
			// getFocusMode() is documented as never returning null, however
			// I've had null pointer exceptions reported in Google Play from the
			// below line (v1.7),
			// on Galaxy Tab 10.1 (GT-P7500), Android 4.0.3 - 4.0.4; HTC EVO 3D
			// X515m (shooteru), Android 4.0.3 - 4.0.4
			if (focus_mode != null
					&& (focus_mode.equals(Parameters.FOCUS_MODE_AUTO) || focus_mode
							.equals(Parameters.FOCUS_MODE_MACRO))) {
				
					WybLog.d(TAG, "try to start autofocus");
				String old_flash = parameters.getFlashMode();
				
					WybLog.d(TAG, "old_flash: " + old_flash);
				set_flash_after_autofocus = "";
				// getFlashMode() may return null if flash not supported!
				if (startup && old_flash != null
						&& old_flash != Parameters.FLASH_MODE_OFF) {
					set_flash_after_autofocus = old_flash;
					parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
					camera.setParameters(parameters);
				}
				Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						
							WybLog.d(TAG, "autofocus complete: " + success);
						autoFocusCompleted(manual, success, false);
					}
				};

				this.focus_success = FOCUS_WAITING;
				this.focus_complete_time = -1;
				this.successfully_focused = false;
				try {
					camera.autoFocus(autoFocusCallback);
					count_cameraAutoFocus++;
				} catch (RuntimeException e) {
					// just in case? We got a RuntimeException report here from
					// 1 user on Google Play
					autoFocusCallback.onAutoFocus(false, camera);

					
						WybLog.e(TAG, "runtime exception from autoFocus");
					e.printStackTrace();
				}
			} else if (has_focus_area) {
				// do this so we get the focus box, for focus modes that support
				// focus area, but don't support autofocus
				focus_success = FOCUS_SUCCESS;
				focus_complete_time = System.currentTimeMillis();
			}
		}
	}

	private void cancelAutoFocus() {
		
			WybLog.d(TAG, "cancelAutoFocus");
		if (camera != null) {
			try {
				camera.cancelAutoFocus();
			} catch (RuntimeException e) {
				// had a report of crash on some devices, see comment at
				// https://sourceforge.net/p/opencamera/tickets/4/ made on
				// 20140520
				
					WybLog.d(TAG, "camera.cancelAutoFocus() failed");
				e.printStackTrace();
			}
			autoFocusCompleted(false, false, true);
		}
	}

	private boolean autoYes;
	private void autoFocusCompleted(boolean manual, boolean success,
			boolean cancelled) {
		autoYes = false;
		 {
			WybLog.d(TAG, "autoFocusCompleted");
			WybLog.d(TAG, "    manual? " + manual);
			WybLog.d(TAG, "    success? " + success);
			WybLog.d(TAG, "    cancelled? " + cancelled);
		}
		if (cancelled) {
			focus_success = FOCUS_DONE;
		} else {
			focus_success = success ? FOCUS_SUCCESS : FOCUS_FAILED;
			focus_complete_time = System.currentTimeMillis();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					autoYes = true;
					invalidate();
				}
			}, 777);
		}
//		PoleCameraActivity main_activity = (PoleCameraActivity) CameraPreview.this
//				.getContext();
		if (manual && !cancelled && (success )) {
			successfully_focused = true;
			successfully_focused_time = focus_complete_time;
		}
		if (set_flash_after_autofocus.length() > 0) {
			
				WybLog.d(TAG, "set flash back to: " + set_flash_after_autofocus);
			Parameters parameters = camera.getParameters();
			parameters.setFlashMode(set_flash_after_autofocus);
			set_flash_after_autofocus = "";
			camera.setParameters(parameters);
		}
		invalidate();
	}

	private void startCameraPreview() {
		long debug_time = 0;
		 {
			WybLog.d(TAG, "startCameraPreview");
			debug_time = System.currentTimeMillis();
		}
		// if( camera != null && !is_taking_photo && !is_preview_started ) {
		if (camera != null && !this.isTakingPhotoOrOnTimer()
				&& !is_preview_started) {
			
				WybLog.d(TAG, "starting the camera preview");
			// else, we reset the preview fps to default in switchVideo
			count_cameraStartPreview++;
			camera.startPreview();
			this.is_preview_started = true;
			 {
				WybLog.d(TAG,
						"time after starting camera preview: "
								+ (System.currentTimeMillis() - debug_time));
			}
			if (this.using_face_detection) {
				
					WybLog.d(TAG, "start face detection");
				try {
					camera.startFaceDetection();
				} catch (RuntimeException e) {
					// I didn't think this could happen, as we only call
					// startFaceDetection() after we've called takePicture() or
					// stopPreview(), which the Android docs say stops the face
					// detection
					// however I had a crash reported on Google Play for Open
					// Camera v1.4
					// 2 Jan 2014, "maxx_ax5", Android 4.0.3-4.0.4
					// startCameraPreview() was called after taking photo in
					// burst mode, but I tested with burst mode and face
					// detection, and can't reproduce the crash on Galaxy Nexus
					
						WybLog.d(TAG, "face detection already started");
				}
				faces_detected = null;
			}
		}
		this.setPreviewPaused(false);
	}

	private void setPreviewPaused(boolean paused) {
		
			WybLog.d(TAG, "setPreviewPaused: " + paused);
//		Activity activity = (Activity) this.getContext();
//		View shareButton = (View) activity.findViewById(R.id.share);
//		View trashButton = (View) activity.findViewById(R.id.trash);
		if (paused) {
			this.phase = PHASE_PREVIEW_PAUSED;
//			shareButton.setVisibility(View.VISIBLE);
//			trashButton.setVisibility(View.VISIBLE);
			// shouldn't call showGUI(false), as should already have been
			// disabled when we started to take a photo
		} else {
			this.phase = PHASE_NORMAL;
//			shareButton.setVisibility(View.GONE);
//			trashButton.setVisibility(View.GONE);
//			preview_image_name = null;
			showGUI(true);
		}
	}

	private void showGUI(final boolean show) {
		
			WybLog.d(TAG, "showGUI: " + show);
		final Activity activity = (Activity) this.getContext();
		activity.runOnUiThread(new Runnable() {
			public void run() {
				final int visibility = show ? View.VISIBLE : View.GONE;
//				View switchCameraButton = (View) activity
//						.findViewById(R.id.switch_camera);
				View flashButton = (View) activity.findViewById(R.id.flash);
//				View focusButton = (View) activity.findViewById(R.id.focus_mode);
//				View exposureButton = (View) activity
//						.findViewById(R.id.exposure);
//				View exposureLockButton = (View) activity
//						.findViewById(R.id.exposure_lock);
//				if (Camera.getNumberOfCameras() > 1)
//					switchCameraButton.setVisibility(visibility);
				if (supported_flash_values != null)
					flashButton.setVisibility(visibility);
//				if (supported_focus_values != null)
//					focusButton.setVisibility(visibility);
//				exposureButton.setVisibility(visibility);
//				exposureLockButton.setVisibility(visibility);
			}
		});
	}

	/*void onAccelerometerSensorChanged(SensorEvent event) {
		this.has_gravity = true;
		for (int i = 0; i < 3; i++) {
			this.gravity[i] = sensor_alpha * this.gravity[i]
					+ (1.0f - sensor_alpha) * event.values[i];
		}
		calculateGeoDirection();

		double x = gravity[0];
		double y = gravity[1];
		this.has_level_angle = true;
		this.level_angle = Math.atan2(-x, y) * 180.0 / Math.PI;
		if (this.level_angle < -0.0) {
			this.level_angle += 360.0;
		}
		this.orig_level_angle = this.level_angle;
		this.level_angle -= (float) this.current_orientation;
		if (this.level_angle < -180.0) {
			this.level_angle += 360.0;
		} else if (this.level_angle > 180.0) {
			this.level_angle -= 360.0;
		}

		this.invalidate();
	}*/

	/*void onMagneticSensorChanged(SensorEvent event) {
		this.has_geomagnetic = true;
		for (int i = 0; i < 3; i++) {
			this.geomagnetic[i] = sensor_alpha * this.geomagnetic[i]
					+ (1.0f - sensor_alpha) * event.values[i];
		}
		calculateGeoDirection();
	}*/

	/*private void calculateGeoDirection() {
		if (!this.has_gravity || !this.has_geomagnetic) {
			return;
		}
		if (!SensorManager.getRotationMatrix(this.deviceRotation,
				this.deviceInclination, this.gravity, this.geomagnetic)) {
			return;
		}
		SensorManager
				.remapCoordinateSystem(this.deviceRotation,
						SensorManager.AXIS_X, SensorManager.AXIS_Z,
						this.cameraRotation);
		this.has_geo_direction = true;
		SensorManager.getOrientation(cameraRotation, geo_direction);
	}*/

	public boolean supportsFaceDetection() {
		
			WybLog.d(TAG, "supportsFaceDetection");
		return supports_face_detection;
	}

	List<String> getSupportedColorEffects() {
		
			WybLog.d(TAG, "getSupportedColorEffects");
		return this.color_effects;
	}

	List<String> getSupportedSceneModes() {
		
			WybLog.d(TAG, "getSupportedSceneModes");
		return this.scene_modes;
	}

	List<String> getSupportedWhiteBalances() {
		
			WybLog.d(TAG, "getSupportedWhiteBalances");
		return this.white_balances;
	}

	String getISOKey() {
		
			WybLog.d(TAG, "getISOKey");
		return this.iso_key;
	}

	List<String> getSupportedISOs() {
		
			WybLog.d(TAG, "getSupportedISOs");
		return this.isos;
	}

	public boolean supportsExposures() {
		
			WybLog.d(TAG, "supportsExposures");
		return this.exposures != null;
	}

	int getMinimumExposure() {
		
			WybLog.d(TAG, "getMinimumExposure");
		return this.min_exposure;
	}

	int getMaximumExposure() {
		
			WybLog.d(TAG, "getMaximumExposure");
		return this.max_exposure;
	}

	int getCurrentExposure() {
		
			WybLog.d(TAG, "getCurrentExposure");
		if (camera == null)
			return 0;
		Parameters parameters = camera.getParameters();
		int current_exposure = parameters.getExposureCompensation();
		return current_exposure;
	}

	List<String> getSupportedExposures() {
		
			WybLog.d(TAG, "getSupportedExposures");
		return this.exposures;
	}

	List<Size> getSupportedPreviewSizes() {
		
			WybLog.d(TAG, "getSupportedPreviewSizes");
		return this.supported_preview_sizes;
	}

	public List<Size> getSupportedPictureSizes() {
		
			WybLog.d(TAG, "getSupportedPictureSizes");
		return this.sizes;
	}

	int getCurrentPictureSizeIndex() {
		
			WybLog.d(TAG, "getCurrentPictureSizeIndex");
		return this.current_size_index;
	}

	List<String> getSupportedFlashValues() {
		return supported_flash_values;
	}

	List<String> getSupportedFocusValues() {
		return supported_focus_values;
	}

	public int getCameraId() {
		return this.cameraId;
	}

	private int getImageQuality() {
		
			WybLog.d(TAG, "getImageQuality");
		String image_quality_s = "100";
		int image_quality = 0;
		try {
			image_quality = Integer.parseInt(image_quality_s);
		} catch (NumberFormatException exception) {
			
				WybLog.e(TAG, "image_quality_s invalid format: " + image_quality_s);
			image_quality = 100;
		}
		return image_quality;
	}

	public void onResume() {
		
			WybLog.d(TAG, "onResume");
		this.app_is_paused = false;
		this.ui_placement_right = true;
		this.openCamera();
	}

	public void onPause() {
		
			WybLog.d(TAG, "onPause");
		this.app_is_paused = true;
		this.closeCamera();
	}

	void onSaveInstanceState(Bundle state) {
		
			WybLog.d(TAG, "onSaveInstanceState");
		
			WybLog.d(TAG, "save cameraId: " + cameraId);
		state.putInt("cameraId", cameraId);
		
			WybLog.d(TAG, "save zoom_factor: " + zoom_factor);
		state.putInt("zoom_factor", zoom_factor);
	}

	public void showToast(final ToastBoxer clear_toast, final String message) {
		class RotatedTextView extends View {
			private String text = "";
			private Paint paint = new Paint();
			private Rect bounds = new Rect();
			private Rect rect = new Rect();

			public RotatedTextView(String text, Context context) {
				super(context);

				this.text = text;
			}

			@Override
			protected void onDraw(Canvas canvas) {
				final float scale = getResources().getDisplayMetrics().density;
				paint.setTextSize(14 * scale + 0.5f); // convert dps to pixels
				paint.setShadowLayer(1, 0, 1, Color.BLACK);
				paint.getTextBounds(text, 0, text.length(), bounds);
				/*
				 * if( MyDebug.LOG ) { WybLog.d(TAG, "bounds: " + bounds); }
				 */
				final int padding = (int) (14 * scale + 0.5f); // convert dps to
																// pixels
				final int offset_y = (int) (32 * scale + 0.5f); // convert dps
																// to pixels
				canvas.save();
				canvas.rotate(ui_rotation, canvas.getWidth() / 2,
						canvas.getHeight() / 2);

				rect.left = canvas.getWidth() / 2 - bounds.width() / 2
						+ bounds.left - padding;
				rect.top = canvas.getHeight() / 2 + bounds.top - padding
						+ offset_y;
				rect.right = canvas.getWidth() / 2 - bounds.width() / 2
						+ bounds.right + padding;
				rect.bottom = canvas.getHeight() / 2 + bounds.bottom + padding
						+ offset_y;

				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.rgb(75, 75, 75));
				canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom,
						paint);

				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(Color.rgb(150, 150, 150));
				canvas.drawLine(rect.left, rect.top, rect.right, rect.top,
						paint);
				canvas.drawLine(rect.left, rect.top, rect.left, rect.bottom,
						paint);

				paint.setStyle(Paint.Style.FILL); // needed for Android 4.4!
				paint.setColor(Color.WHITE);
				canvas.drawText(text, canvas.getWidth() / 2 - bounds.width()
						/ 2, canvas.getHeight() / 2 + offset_y, paint);
				canvas.restore();
			}
		}

		
			WybLog.d(TAG, "showToast");
		final Activity activity = (Activity) this.getContext();
		// We get a crash on emulator at least if Toast constructor isn't run on
		// main thread (e.g., the toast for taking a photo when on timer).
		// Also see
		// http://stackoverflow.com/questions/13267239/toast-from-a-non-ui-thread
		activity.runOnUiThread(new Runnable() {
			public void run() {
				if (clear_toast != null && clear_toast.toast != null)
					clear_toast.toast.cancel();
				/*
				 * clear_toast =
				 * Toast.makeText(activity.getApplicationContext(), message,
				 * Toast.LENGTH_SHORT); clear_toast.show();
				 */

				Toast toast = new Toast(activity);
				if (clear_toast != null)
					clear_toast.toast = toast;
				View text = new RotatedTextView(message, activity);
				toast.setView(text);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	void setUIRotation(int ui_rotation) {
		
			WybLog.d(TAG, "setUIRotation");
		this.ui_rotation = ui_rotation;
	}

	/*void locationChanged(Location location) {
		
			WybLog.d(TAG, "locationChanged");
		this.has_received_location = true;
//		SharedPreferences sharedPreferences = PreferenceManager
//				.getDefaultSharedPreferences(this.getContext());
//		boolean store_location = sharedPreferences.getBoolean(
//				"preference_location", false);
		//���ô洢����λ��
		boolean store_location = false;
		if (store_location) {
			this.location = location;
			// Android camera source claims we need to check lat/long != 0.0d
			if (location != null
					&& (location.getLatitude() != 0.0d || location
							.getLongitude() != 0.0d)) {
				 {
					WybLog.d(TAG, "received location:");
					WybLog.d(TAG,
							"lat " + location.getLatitude() + " long "
									+ location.getLongitude() + " accuracy "
									+ location.getAccuracy());
				}
				this.has_set_location = true;
				this.location_accuracy = location.getAccuracy();
			}
		}
	}*/

	/*private void updateParametersFromLocation() {
		if (camera != null) {
//			SharedPreferences sharedPreferences = PreferenceManager
//					.getDefaultSharedPreferences(this.getContext());
//			boolean store_location = sharedPreferences.getBoolean(
//					"preference_location", false);
			//���ô洢����λ��
			boolean store_location = false;
			// Android camera source claims we need to check lat/long != 0.0d
			if (store_location
					&& location != null
					&& (location.getLatitude() != 0.0d || location
							.getLongitude() != 0.0d)) {
				 {
					WybLog.d(TAG, "updating parameters from location...");
					WybLog.d(TAG,
							"lat " + location.getLatitude() + " long "
									+ location.getLongitude() + " accuracy "
									+ location.getAccuracy());
				}
				Camera.Parameters parameters = camera.getParameters();
				parameters.removeGpsData();
				parameters.setGpsTimestamp(System.currentTimeMillis() / 1000); // initialise
																				// to
																				// a
																				// value
																				// (from
																				// Android
																				// camera
																				// source)
				parameters.setGpsLatitude(location.getLatitude());
				parameters.setGpsLongitude(location.getLongitude());
				parameters.setGpsProcessingMethod(location.getProvider()); // from
																			// http://boundarydevices.com/how-to-write-an-android-camera-app/
				if (location.hasAltitude()) {
					parameters.setGpsAltitude(location.getAltitude());
				} else {
					// Android camera source claims we need to fake one if not
					// present
					// and indeed, this is needed to fix crash on Nexus 7
					parameters.setGpsAltitude(0);
				}
				if (location.getTime() != 0) { // from Android camera source
					parameters.setGpsTimestamp(location.getTime() / 1000);
				}
				try {
					camera.setParameters(parameters);
				} catch (RuntimeException e) {
					// received this crash from Google Play
					
						WybLog.d(TAG, "failed to set parameters for gps info");
					e.printStackTrace();
				}
			} else {
					WybLog.d(TAG, "removing location data from parameters...");
				Camera.Parameters parameters = camera.getParameters();
				parameters.removeGpsData();
				camera.setParameters(parameters);
				this.has_set_location = false;
				has_received_location = false;
			}
		}
	}*/

	public boolean isVideo() {
		// return is_video;
		return false;
	}

	// must be static, to safely call from other Activities
	public static String getFlashPreferenceKey(int cameraId) {
		return "flash_value_" + cameraId;
	}

	// must be static, to safely call from other Activities
	public static String getFocusPreferenceKey(int cameraId) {
		return "focus_value_" + cameraId;
	}

	// must be static, to safely call from other Activities
	public static String getResolutionPreferenceKey(int cameraId) {
		return "camera_resolution_" + cameraId;
	}

	// must be static, to safely call from other Activities
	public static String getVideoQualityPreferenceKey(int cameraId) {
		return "video_quality_" + cameraId;
	}

	// must be static, to safely call from other Activities
	public static String getIsVideoPreferenceKey() {
		return "is_video";
	}

	// must be static, to safely call from other Activities
	public static String getExposurePreferenceKey() {
		return "preference_exposure";
	}

	// must be static, to safely call from other Activities
	public static String getColorEffectPreferenceKey() {
		return "preference_color_effect";
	}

	// must be static, to safely call from other Activities
	public static String getSceneModePreferenceKey() {
		return "preference_scene_mode";
	}

	// must be static, to safely call from other Activities
	public static String getWhiteBalancePreferenceKey() {
		return "preference_white_balance";
	}

	// must be static, to safely call from other Activities
	public static String getISOPreferenceKey() {
		return "preference_iso";
	}

	// for testing:
	public Camera getCamera() {
		/*
		 * if( MyDebug.LOG ) WybLog.d(TAG, "getCamera: " + camera);
		 */
		return this.camera;
	}

	public boolean supportsFocus() {
		return this.supported_focus_values != null;
	}

	public boolean supportsFlash() {
		return this.supported_flash_values != null;
	}

	public boolean supportsExposureLock() {
		return this.is_exposure_locked_supported;
	}

	public String getCurrentFlashValue() {
		if (this.current_flash_index == -1)
			return null;
		return this.supported_flash_values.get(current_flash_index);
	}

	public boolean hasFocusArea() {
		return this.has_focus_area;
	}

	public boolean isTakingPhotoOrOnTimer() {
		// return this.is_taking_photo;
		return this.phase == PHASE_TAKING_PHOTO || this.phase == PHASE_TIMER;
	}

	public boolean isTakingPhoto() {
		return this.phase == PHASE_TAKING_PHOTO;
	}

	public boolean isOnTimer() {
		// return this.is_taking_photo_on_timer;
		return this.phase == PHASE_TIMER;
	}

	public boolean isPreviewStarted() {
		return this.is_preview_started;
	}

	public boolean hasSetLocation() {
		return this.has_set_location;
	}
	
	/**
	 * 重拍选择
	 */
	public void againStart(){
		startCameraPreview();
		showGUI(true);
	}
	
	public void openFlash() {
		if (camera == null) {
			return;
		}
		Parameters parameters = camera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
		camera.setParameters(parameters);
	}

	public void closeFlash() {
		if (camera == null) {
			return;
		}
		Parameters parameters = camera.getParameters();
		parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
		camera.setParameters(parameters);
//		camera.release();
	}

	public boolean isOpenFlash() {

		if (camera == null) {
			return false;
		}
		Parameters p = camera.getParameters();
		WybLog.syse("---闪光灯TYPE：" + p.getFlashMode());

		// 获取闪光灯的状态
		if (p.getFlashMode().equals(Parameters.FLASH_MODE_TORCH)) {
			return true;
		} else {
			return false;
		}

	}
}
