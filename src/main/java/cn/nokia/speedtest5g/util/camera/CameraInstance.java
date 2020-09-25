package cn.nokia.speedtest5g.util.camera;

import java.io.IOException;
import java.util.List;
import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

public class CameraInstance {
	private static CameraInstance mCameraInstance;
	private Camera mCamera;
	private Parameters mParams;
	private String mFlashMode = Parameters.FLASH_MODE_OFF;
	private int maxZoom = 0;
	private boolean isPreviewing = false;
	private TakePictureCallback mTakePictureCallback = null;
	
	public interface TakePictureCallback {
		public void takePicture(byte[] b);
	}

	/** 安全获得相机实例 */
	public static synchronized CameraInstance getInstance() {
		if (mCameraInstance == null) {
			mCameraInstance = new CameraInstance();
		}
		return mCameraInstance;
	}
	
	public void openCamera() {
		if (mCamera == null) {
			try{
				mCamera = Camera.open();
				mParams = mCamera.getParameters();
				maxZoom = mParams.getMaxZoom();
			}catch (Exception e){ 
		    	
		    } 
		}
	}
	
	public void closeCamera() {
		if (null != mCamera) {
			mCamera.setPreviewCallback(null);
			stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	public void stopPreview(){
		if (isPreviewing && mCamera !=null) {
			mCamera.stopPreview();
			isPreviewing = false;
		}
	}
	
	public void startPreview(SurfaceHolder holder, float previewRate) {
		if (isPreviewing && mCamera !=null) {
			stopPreview();
		}

		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			setParameters(previewRate);
			mCamera.startPreview();
			addAutoFoucusCallBack();
			isPreviewing = true;
		}
	}
	
	public boolean isPreviewing() {
		return isPreviewing;
	}
	
	public int getMaxZoom(){
		return maxZoom;
	}
	
	public boolean setZoom(int zoom){
		if(mParams!=null && mCamera!=null){
			if(zoom < 0) zoom = 0;
			if(zoom > maxZoom) zoom = maxZoom;
			mParams.setZoom(zoom);
			mCamera.setParameters(mParams);
			return true;
		}
		return false;
	}
	
	public boolean setFlashMode(String flashmode) {
		if(mParams!=null && mCamera!=null){
			if (isPreviewing) {
				mCamera.stopPreview();
			}
			if (flashmode.contains(Parameters.FLASH_MODE_AUTO)) {
				mFlashMode = Parameters.FLASH_MODE_AUTO;
			} else if (flashmode.contains(Parameters.FLASH_MODE_ON)) {
				mFlashMode = Parameters.FLASH_MODE_ON;
			} else if (flashmode.contains(Parameters.FLASH_MODE_RED_EYE)) {
				mFlashMode = Parameters.FLASH_MODE_RED_EYE;
			} else if (flashmode.contains(Parameters.FLASH_MODE_TORCH)) {
				mFlashMode = Parameters.FLASH_MODE_TORCH;
			} else {
				mFlashMode = Parameters.FLASH_MODE_OFF;
			}
			mParams.setFlashMode(mFlashMode);
			mCamera.setParameters(mParams);
			if (isPreviewing) {
				mCamera.startPreview();
				addAutoFoucusCallBack();
			}
			return true;
		}
		return false;
	}
	
	public String getFlashMode() {
		return mFlashMode;
	}

	private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
		}
	};
	private void addAutoFoucusCallBack(){
		if(mCamera != null){
			if(mFlashMode == Parameters.FLASH_MODE_OFF)
				mCamera.autoFocus(mAutoFocusCallback);
		}
	}
	@SuppressLint("InlinedApi")
	public void setAutoFoucus() {
		if(mCamera != null){
//			if (isPreviewing) {
//				mCamera.stopPreview();
//			}
			mParams = mCamera.getParameters();
			List<String> focusModes = mParams.getSupportedFocusModes();
			if (focusModes
					.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				mParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}else if (focusModes.contains(Parameters.FOCUS_MODE_AUTO)) {
				mParams.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			}
			mCamera.setParameters(mParams);
			mCamera.cancelAutoFocus();
//			if (isPreviewing) {
//				mCamera.startPreview();
//			}
		}
	}
	
	private void setParameters(float previewRate){
		if (mCamera != null) {
			mParams = mCamera.getParameters();
			mParams.setPictureFormat(ImageFormat.JPEG);
			Size pictureSize = CameraUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(), previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CameraUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);
			mCamera.setDisplayOrientation(90);
			setAutoFoucus();
			mParams = mCamera.getParameters();
		}
	}
	

	

	// 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
//	private ShutterCallback mShutterCallback = new ShutterCallback(){
//		public void onShutter() {}
//	};

	private PictureCallback mJpegPictureCallback = new PictureCallback(){
		public void onPictureTaken(byte[] data, Camera camera) {
			stopPreview();
//			Bitmap bitmap = null;
//			if (null != data) {
//				bitmap = loadBitmap(data);
//			}
			mTakePictureCallback.takePicture(data);
//			if(!bitmap.isRecycled()){
//				bitmap.recycle();  
//				bitmap = null;  
//	        } 
		}
	};
	
	public void doTakePicture(TakePictureCallback callback) {
		//TODO takePicture
		mTakePictureCallback = callback;
		if (isPreviewing && (mCamera != null)) {
			mCamera.takePicture( null, null, mJpegPictureCallback);
		}
	}
}
