package cn.nokia.speedtest5g.app.uitl;

import java.io.File;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.bean.WybBitmapDb;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 加载图片工具
 * @author zwq
 *
 */
public class ImageOptionsUtil {

	private static ImageOptionsUtil iou = null;

	public synchronized static ImageOptionsUtil getInstances(){
		if (iou == null) {
			iou = new ImageOptionsUtil();
		}
		return iou;
	}

	/**
	 *  DisplayImageOptions是用于设置图片显示的类
	 */
	private DisplayImageOptions mOptions,mOptionsDisk, mOptionsDiskForTscs, mOptionsDiskForSplash; 

	public DisplayImageOptions getOptions(){
		if (mOptions == null) {
			mOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.none)//加载开始默认的图片   
			.showImageForEmptyUri(R.drawable.none)  // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.options_wrong)  //加载图片出现问题，会显示该图片
			.cacheInMemory(false)//内存缓存--设置下载的图片是否缓存在内存中
			.cacheOnDisk(false)//磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
			.considerExifParams(false)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(10)) //图片圆角显示，值为整数
			.build();
		}
		return mOptions;
	}

	private DisplayImageOptions displayImageOptions; 
	private int lastCornerRadiusForDp;

	/**
	 * 设置图片显示圆角ji
	 * 
	 * @param cornerRadiusForDp
	 *            图片圆角半径(单位为dp，若要显示圆形图片半径设置为控件宽(或高)的一半即可)
	 * @return
	 */
	public DisplayImageOptions getOptions(int cornerRadiusForDp) {
		if (lastCornerRadiusForDp != cornerRadiusForDp || displayImageOptions == null) {
			lastCornerRadiusForDp = cornerRadiusForDp;
			displayImageOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.none)// 加载开始默认的图片
			.showImageForEmptyUri(R.drawable.none) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.options_wrong) // 加载图片出现问题，会显示该图片
			.cacheInMemory(false)// 内存缓存--设置下载的图片是否缓存在内存中
			.cacheOnDisk(false)// 磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
			.considerExifParams(false)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.displayer(new RoundedBitmapDisplayer(UtilHandler.getInstance().dpTopx(cornerRadiusForDp))) // 图片圆角显示，值为整数
			.build();
		}
		return displayImageOptions;
	}


	private DisplayImageOptions optionsIcon; 
	/**
	 * 获取首页功能模块option
	 * @return
	 */
	public DisplayImageOptions getIconOption() {
		if (optionsIcon == null) {
			optionsIcon = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.none)// 加载开始默认的图片
			.showImageForEmptyUri(R.drawable.none) // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.none) // 加载图片出现问题，会显示该图片
			.cacheInMemory(true)// 内存缓存--设置下载的图片是否缓存在内存中
			.cacheOnDisk(true)// 磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		}
		return optionsIcon;
	}
	/**
	 * 保存缓存
	 * @return
	 */
	public DisplayImageOptions getOptionsDisk(){
		if (mOptionsDisk == null) {
			mOptionsDisk = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_photo_loading)//加载开始默认的图片   
			.showImageForEmptyUri(R.drawable.icon_speed_test_head_flag)  // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.icon_speed_test_head_flag)  //加载图片出现问题，会显示该图片
			.cacheInMemory(true)//内存缓存--设置下载的图片是否缓存在内存中
			.cacheOnDisk(true)//磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.NONE)
			//			.displayer(new RoundedBitmapDisplayer(20)) //图片圆角显示，值为整数
			.build();
		}
		return mOptionsDisk;
	}

	/**
	 * 加载闪屏页配置
	 * @return
	 */
	public DisplayImageOptions getOptionsDiskForSplash(){
		if (mOptionsDiskForSplash == null) {
			mOptionsDiskForSplash = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.login_bg_large)//加载开始默认的图片   
			.showImageForEmptyUri(R.drawable.login_bg_large)  // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.login_bg_large)  //加载图片出现问题，会显示该图片
			.cacheInMemory(true)//内存缓存--设置下载的图片是否缓存在内存中
			.cacheOnDisk(true)//磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.NONE)
			//			.displayer(new RoundedBitmapDisplayer(20)) //图片圆角显示，值为整数
			.build();
		}
		return mOptionsDiskForSplash;
	}

	/**
	 * 保存缓存
	 * @return
	 */
	public DisplayImageOptions getOptionsDiskForTscs(){
		if (mOptionsDiskForTscs == null) {
			mOptionsDiskForTscs = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.icon_btn_photo)//加载开始默认的图片   
			.showImageForEmptyUri(R.drawable.options_smile_face)  // 设置图片Uri为空或是错误的时候显示的图片
			.showImageOnFail(R.drawable.options_smile_face)  //加载图片出现问题，会显示该图片
			.cacheInMemory(true)//内存缓存--设置下载的图片是否缓存在内存中
			.cacheOnDisk(true)//磁盘缓存--设置下载的图片是否缓存在SD卡中----/sdcard/Android/data/[package_name]/cache
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.imageScaleType(ImageScaleType.NONE)
			.displayer(new RoundedBitmapDisplayer(UtilHandler.getInstance().dpTopx(5))) //图片圆角显示，值为整数
			.build();
		}
		return mOptionsDiskForTscs;
	}

	public void displayImage(WybBitmapDb ckBitmapDb,ImageView iv){
		iv.setTag(R.id.IDgroup, ckBitmapDb);
		ImageLoader.getInstance().displayImage(ckBitmapDb.getNetUrl(), iv,getOptionsDisk(),new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String arg0, View arg1) {

			}

			//加载失败
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

			}

			//加载完毕
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				WybBitmapDb pMsg = (WybBitmapDb) arg1.getTag(R.id.IDgroup);
				if (pMsg != null && !pMsg.getNetUrl().isEmpty()) {
					File file = new File(pMsg.getPath());
					if (!file.exists()) {
						file.mkdirs();
					}
					file = ImageLoader.getInstance().getDiskCache().get(pMsg.getNetUrl());
					PathUtil.getInstances().CopySdcardFile(file.getPath(), pMsg.getPath() + pMsg.getName());
					arg1.setTag(R.id.idName, pMsg.getPath() + pMsg.getName());
				}
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {

			}
		});
	}
}
