package cn.nokia.speedtest5g.util.camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.nokia.speedtest5g.app.uitl.WybLog;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;

public class CameraUtil {
	private static final String TAG = "CamParaUtil";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CameraUtil myCamPara = null;

	private CameraUtil() {

	}
	
	/** 检查设备是否提供摄像头 */ 
	public static boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){ 
	        return true;
	    } else {
	        return false;
	    }
	}

	public static CameraUtil getInstance() {
		if (myCamPara == null) {
			myCamPara = new CameraUtil();
			return myCamPara;
		} else {
			return myCamPara;
		}
	}

	public Size getPropPreviewSize(List<Size> list, float th,
                                   int minWidth) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.width >= minWidth)) {
				WybLog.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i = 0;// 如果没找到，就选最小的size
		}
		return list.get(i);
	}

	public Size getPropPictureSize(List<Size> list, float th,
                                   int minWidth) {
		Collections.sort(list, sizeComparator);

		int i = 0;
		for (Size s : list) {
			if ((s.width >= minWidth)) {
				WybLog.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
				break;
			}
			i++;
		}
		if (i == list.size()) {
			i = 0;// 如果没找到，就选最小的size
		}
		return list.get(i);
	}

	public boolean equalRate(Size s, float rate) {
		float r = (float) (s.width) / (float) (s.height);
		if (Math.abs(r - rate) <= 0.03) {
			return true;
		} else {
			return false;
		}
	}

	public class CameraSizeComparator implements Comparator<Size> {
		public int compare(Size lhs, Size rhs) {
			if (lhs.width == rhs.width) {
				return 0;
			} else if (lhs.width > rhs.width) {
				return 1;
			} else {
				return -1;
			}
		}

	}

	/**
	 * 打印支持的previewSizes
	 * 
	 * @param params
	 */
	public void printSupportPreviewSize(Camera.Parameters params) {
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		for (int i = 0; i < previewSizes.size(); i++) {
			Size size = previewSizes.get(i);
			WybLog.i(TAG, "previewSizes:width = " + size.width + " height = "
					+ size.height);
		}

	}

	/**
	 * 打印支持的pictureSizes
	 * 
	 * @param params
	 */
	public void printSupportPictureSize(Camera.Parameters params) {
		List<Size> pictureSizes = params.getSupportedPictureSizes();
		for (int i = 0; i < pictureSizes.size(); i++) {
			Size size = pictureSizes.get(i);
			WybLog.i(TAG, "pictureSizes:width = " + size.width + " height = "
					+ size.height);
		}
	}

	/**
	 * 打印支持的聚焦模式
	 * 
	 * @param params
	 */
	public void printSupportFocusMode(Camera.Parameters params) {
		List<String> focusModes = params.getSupportedFocusModes();
		for (String mode : focusModes) {
			WybLog.i(TAG, "focusModes--" + mode);
		}
	}
}
