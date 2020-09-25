package cn.nokia.speedtest5g.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.MarqueesTextView;
import com.android.volley.util.SharedPreHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.activity2.PhotoAlbumActivity;
import cn.nokia.speedtest5g.app.bean.Db_Modular;
import cn.nokia.speedtest5g.app.bean.WybBitmapDb;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.request.RequestSmsCodeLogin;
import cn.nokia.speedtest5g.app.respon.BaseRespon;
import cn.nokia.speedtest5g.app.respon.Login;
import cn.nokia.speedtest5g.app.respon.ResponseHomeInitData;
import cn.nokia.speedtest5g.app.respon.SessionBean;
import cn.nokia.speedtest5g.app.uitl.HttpPostUtil;
import cn.nokia.speedtest5g.app.uitl.ImageOptionsUtil;
import cn.nokia.speedtest5g.app.uitl.NetInfoUtil;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.PathUtil;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.app.uitl.WaterMarkUtil;
import cn.nokia.speedtest5g.app.uitl.upload.UploadNowTypeStr;
import cn.nokia.speedtest5g.dialog.CommonDialog;
import cn.nokia.speedtest5g.dialog.MyCodeDialog;
import cn.nokia.speedtest5g.msg.MsgListActivity;
import cn.nokia.speedtest5g.msg.respone.MsgDetail;
import cn.nokia.speedtest5g.speedtest.bean.RequestHead;
import cn.nokia.speedtest5g.speedtest.bean.RequestUploadHead;
import cn.nokia.speedtest5g.speedtest.bean.ResponseHead;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestDataSet;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestMenuPopup;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestServerRequestUtil;
import cn.nokia.speedtest5g.speedtest.util.SpeedTestUtil;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;
import cn.nokia.speedtest5g.view.MyCornerImageView;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

/**
 * 主页-我的
 * @author JQJ
 *
 */
public class MainHomePersonalActivity extends BaseActionBarHandlerActivity{

	public static final String ON_RESUME_UPDATE = "cn.nokia.speedtest5g.personal.onresume";
	public static final String ON_SELECT = "cn.nokia.speedtest5g.personal.select"; //选择相册 还是拍照
	public static final String ON_SELECT_PHOTO = "cn.nokia.speedtest5g.personal.select.photo"; //相册选择返回
	private static final int LOGIN_TYPE_NET_TOURIST = -1000;//游客登陆
	public final static int RESULT_CODE_PZ = 1000;
	public final static int RESULT_CODE_CJ = 2000;

	//手机号
	private TextView mTvPhoneNo = null;
	//没有消息列表提示
	private View mViewNoToastData = null;
	private MarqueesTextView mMarqueesTextView = null;
	private List<MsgDetail> msgList = null;
	private MyCornerImageView mImageView = null;

	private String mLoginType = null;
	private LinearLayout mLlExit = null;
	private UpdateConfigReceiver mReceiver = new UpdateConfigReceiver();
	private OnResumeReceiver mOnResumeReceiver = new OnResumeReceiver();
	private String mHeadPath = null; //头像路径
	private String mUserId = "";

	private String mBatchNo = null;
	private String mFilePath = "";
	private String mFileName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainhome_personal);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		init("", false);

		registerReceiver(mReceiver, new IntentFilter(TypeKey.getInstance().ACTION_MAINHOME_SUPER));
		registerReceiver(mOnResumeReceiver, new IntentFilter(ON_RESUME_UPDATE));
		registerReceiver(mUpdateReceiver, new IntentFilter(ON_SELECT_PHOTO));
		registerReceiver(mSelectReceiver, new IntentFilter(ON_SELECT));

		refreshUi();
	}

	/**
	 * 请求头像
	 */
	private void requestHead(){
		if(!NetInfoUtil.isNetworkConnected(mActivity)){
			return;
		}
		mLoginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
		//userid变更 需更新头像
		if("Phone".equals(mLoginType) && !getUserID().equals(mUserId)){
			String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_APP_PERSONAL_CENTER_USER);
			RequestHead request = new RequestHead();
			request.userId = getUserID();
			NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(requestUrl, JsonHandler.getHandler().toJson(request), 
					-1, new ListenerBack() {

				@Override
				public void onListener(int type, Object object, boolean isTrue) {
					try{
						ResponseHead response = JsonHandler.getHandler().getTarget(object.toString(), ResponseHead.class);
						if (response != null) {
							if (response.isRs()) {
								mUserId = getUserID();
								String headAddr = response.datas.headAddr;
								if(TextUtils.isEmpty(headAddr)){ //没有头像地址  给个默认的
									mHeadPath = "drawable://" + R.drawable.icon_speed_test_head_flag;
								}else{
									//加上前缀
									mHeadPath = getResources().getString(R.string.URL1) + response.datas.headAddr;
								}
								updateHead();
							} else {
								showCommon(response.getMsg());
							}
						} else {
							showCommon(object.toString());
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}else{
			updateHead();
		}
	}

	/**
	 * 登陆登出更新广播
	 * @author JQJ
	 *
	 */
	private class UpdateConfigReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(arg1.getAction().equals(TypeKey.getInstance().ACTION_MAINHOME_SUPER)){
				int type = arg1.getIntExtra("type", -1);
				if(type == -1000){
					refreshUi();
					requestHead();
					//刷新水印
					WaterMarkUtil.showWatermarkView(MainHomePersonalActivity.this, getUser());
				}
			}
		}
	}

	@Override
	public void init(Object titleId, boolean isBack) {
		super.init(titleId, isBack);

		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_share_white, EnumRequest.MENU_SELECT_ONE.toInt(), ""));

		mImageView = (MyCornerImageView)findViewById(R.id.mainhome_personal_iv);
		mMarqueesTextView   = (MarqueesTextView)findViewById(R.id.mainhome_personal_mtv_notice);
		mTvPhoneNo 			= (TextView) findViewById(R.id.mainhome_personal_tv_phoneno);
		mViewNoToastData    = findViewById(R.id.mainhome_personal_tv_msgToast_nodata);
		mLlExit = (LinearLayout)findViewById(R.id.mainhome_personal_ll_exit);
	}

	/**
	 * 加载头像
	 */
	private void updateHead(){
		mLoginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
		if("Phone".equals(mLoginType)){
			ImageLoader.getInstance().displayImage(mHeadPath, mImageView, ImageOptionsUtil.getInstances().getOptionsDisk());
		}else if("Tourist".equals(mLoginType)){
			//加载本地未登录头像
			mImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_speed_test_not_login_head));
		}
	}

	/**
	 * 刷新
	 */
	private void refreshUi(){
		mLoginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
		if(TextUtils.isEmpty(mLoginType)){
			mTvPhoneNo.setText("未登录");
			//不显示退出按钮
			mLlExit.setVisibility(View.INVISIBLE);
			return;
		}
		if("Phone".equals(mLoginType)){
			String phoneNo = getUserPhone();
			if(!TextUtils.isEmpty(phoneNo) && UtilHandler.getInstance().isMobileNO(phoneNo)){
				mTvPhoneNo.setText(UtilHandler.getInstance().hidePhoneNoMid4(phoneNo));
			}else{
				mTvPhoneNo.setText(phoneNo);
			}
			//显示退出按钮
			mLlExit.setVisibility(View.VISIBLE);
		}else if("Tourist".equals(mLoginType)){
			mTvPhoneNo.setText("点击登录");
			//不显示退出按钮
			mLlExit.setVisibility(View.INVISIBLE);
		}
	}

	private void updateMsgLayout() {
		if (msgList != null && msgList.size() > 0) {
			mMarqueesTextView.setVisibility(View.VISIBLE);
			mViewNoToastData.setVisibility(View.GONE);
		} else {
			mMarqueesTextView.setVisibility(View.GONE);
			mViewNoToastData.setVisibility(View.VISIBLE);
		}
	}

	private void showNotice(){
		ResponseHomeInitData responseHomeInitData = SpeedTestDataSet.mResponseHomeInitData;
		if(responseHomeInitData != null && responseHomeInitData.datas != null){
			msgList = responseHomeInitData.datas.msgList;
			if(msgList != null && msgList.size() > 0){
				MsgDetail msgDetail = msgList.get(0);
				mMarqueesTextView.setText(msgDetail.title);
			}
		}
		updateMsgLayout();
	}

	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				if(intent.getAction().equals(ON_SELECT_PHOTO)){
					int type = intent.getIntExtra("type", 0);
					if(type == 0) {
						onPhoto(intent.getStringExtra("path")+"/", intent.getStringExtra("name"));
					}
				}
			}
		}
	};

	/**
	 * 更新广播   onResume  高版本系统  在tabActivit框架中不会调用
	 * @author JQJ
	 *
	 */
	private class OnResumeReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if(arg1.getAction().equals(ON_RESUME_UPDATE)){
				SpeedTestServerRequestUtil.getInstance().requestCenterConfig();
				SpeedTestServerRequestUtil.getInstance().requestNotice();
				showNotice();
				requestHead();
			}
		}
	}

	private void selectPhoto(View v){
		new SpeedTestMenuPopup().show(MainHomePersonalActivity.this, v, new ListenerBack() {

			@Override
			public void onListener(int type,Object object,boolean isTrue) {
                if (type == R.id.speed_test_popup_btn_pz) {//拍照
                    //调用系统相机
                    callCapture();
                } else if (type == R.id.speed_test_popup_btn_xc) {//相册
                    Intent intent = new Intent(MainHomePersonalActivity.this, PhotoAlbumActivity.class);
                    intent.putExtra("actionTo", ON_SELECT_PHOTO);
                    goIntent(intent, false);
                }
			}
		});
	}

	private File _filePz;
	private void callCapture() {
		_filePz = new File(PathUtil.getInstances().getCurrentPath());
		if (!_filePz.exists()) {
			_filePz.mkdirs();
		}
		//获得文件
		_filePz = new File(PathUtil.getInstances().getCurrentPath()+"/temp.jpg");
		//判断文件是否为null
		if (_filePz != null) {
			if (_filePz.exists()){
				_filePz.delete();
			}
			Uri imageUriCamear;
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			if (Build.VERSION.SDK_INT >= 24) {
                String packageName = SystemUtil.getInstance().getPackageName(MainHomePersonalActivity.this);
				imageUriCamear = FileProvider.getUriForFile(MainHomePersonalActivity.this,  String.format("%s"+".fileProvider", packageName), _filePz);//通过FileProvider创建一个content类型的Uri
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
			}else{
				imageUriCamear = Uri.fromFile(_filePz);// localTempImgDir和localTempImageFileName是自己定义的名字
			}
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 90);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUriCamear);
			startActivityForResult(intent, RESULT_CODE_PZ);
		}
	}

	@Override
	protected void onResume() {
		if (_filePz != null){
			if (PathUtil.getInstances().isExitFile(PathUtil.getInstances().getCurrentPath()+"/temp.jpg")){
				//获得临时文件
				Bitmap _bitmap = BitmapFactory.decodeFile(PathUtil.getInstances().getCurrentPath()+"/temp.jpg");
				if (_bitmap != null) {
					//将图片保存到系统图库，并返回数据库中指定的路径
					String urlStr = MediaStore.Images.Media.insertImage(getContentResolver(), _bitmap, "", "cbb_photo");
					if (!TextUtils.isEmpty(urlStr)) {
						//发送更新SD卡通知
						MainHomePersonalActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(urlStr)));
					}
					onPhoto(PathUtil.getInstances().getCurrentPath()+"/", "temp.jpg");
				}
			}
			_filePz = null;
		}
		super.onResume();
	}

	private BroadcastReceiver mSelectReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				String action = intent.getAction();
				if(action.equals(ON_SELECT)){
					int value = intent.getIntExtra("SELECT", 0);
					if(value == RESULT_CODE_CJ){
						uploadHead();
					}
				}
			}
		}
	};

	/**
	 * 头像上传
	 */
	private void uploadHead(){
		showMyDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String filePath = mFilePath + mFileName;
				File file = new File(filePath);
				if(file != null){
					final WybBitmapDb csvBitmapDb = new WybBitmapDb();
					csvBitmapDb.setPath(mFilePath);
					csvBitmapDb.setName(mFileName);
					csvBitmapDb.setType(UploadNowTypeStr.FILE_VALUE_HEAD);
					csvBitmapDb.setUserId(getUserID());
					csvBitmapDb.setRemarks(mBatchNo); //流水号
					String url = NetWorkUtilNow.getInstances().getToIp() + 
							SpeedTest5g.getContext().getString(R.string.URL_WYBBITMAP_UPLOAD);
					String result = HttpPostUtil.getHttpGetUtil().doPostFile(url, csvBitmapDb);
					BaseRespon info = JsonHandler.getHandler().getTarget(result, BaseRespon.class);
					if(info != null){
						if(info.isRs()){
							//上传基础数据
							RequestUploadHead request = new RequestUploadHead();
							request.userId = getUserID();
							request.headAddr = filePath.replaceAll(PathUtil.getInstances().getCurrentPath(), "");
							request.batchNo = csvBitmapDb.getRemarks();
							NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDesNoCancel(
									NetWorkUtilNow.getInstances().getToIp() + 
									SpeedTest5g.getContext().getString(R.string.URL_APP_UPLOAD_HEAD),
									JsonHandler.getHandler().toJson(request), -1, new ListenerBack(){

										@Override
										public void onListener(int type, Object object,
												boolean isTrue) {
											dismissMyDialog();
											if (isTrue) {
												BaseRespon info = JsonHandler.getHandler().getTarget(object.toString(), BaseRespon.class);
												if(info != null){
													if(info.isRs()){
														showCommonToThread("更换头像成功");
														mHeadPath = "file://" + csvBitmapDb.getPath() + csvBitmapDb.getName(); //本地图片
														//上传成功  更新头像
														ImageLoader.getInstance().displayImage(mHeadPath, mImageView, ImageOptionsUtil.getInstances().getOptionsDisk());
													}else{
														showCommonToThread(info.getMsg());
													}
												}else{
													showCommonToThread("更换头像失败");
												}
											}
										}
									});
						}else{
							dismissMyDialog();
							showCommonToThread(info.getMsg());
						}
					}else{
						dismissMyDialog();
						showCommonToThread("更换头像失败");
					}
				}
			}
		}).start();
	}

	private void showCommonToThread(final String str){
		if (isExecute()){
			showCommon(str);
		}else if (!isFinishing()){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showCommon(str);
				}
			});
		}
	}

	/**
	 * 加载图片 裁剪
	 * @param path
	 * @param name
	 */
	private void onPhoto(String path, String name) {
		mBatchNo = TimeUtil.getInstance().getNowTime();
		mFilePath = PathUtil.getInstances().getCurrentPath() + "/head/";
		mFileName = mBatchNo + ".jpg";

		File dir = new File(mFilePath);
		File cropImage = new File(mFilePath+mFileName);
		try {
			if(!dir.exists()){
				dir.mkdir();
			}
			cropImage.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.putExtra("crop", "true");
		if (Build.VERSION.SDK_INT >= 24) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(cropImage));// localTempImgDir和localTempImageFileName是自己定义的名字
            String packageName = SystemUtil.getInstance().getPackageName(MainHomePersonalActivity.this);
			intent.setDataAndType(FileProvider.getUriForFile(MainHomePersonalActivity.this, String.format("%s"+".fileProvider", packageName),new File(path + name)), "image/*");
		}else{
			intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(cropImage));// localTempImgDir和localTempImageFileName是自己定义的名字
			intent.setDataAndType(Uri.fromFile(new File(path + name)), "image/*");
		}
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
		intent.putExtra("outputX", 720);
		intent.putExtra("outputY", 720);
		intent.putExtra("scale", true);
		intent.putExtra("scaleUpIfNeeded", true);
		intent.putExtra("return-data", false);
		getParent().startActivityForResult(intent, RESULT_CODE_CJ);
	}

	public void onClickListener(View v){
        int id = v.getId();
        if (id == R.id.mainhome_personal_ll_info) {//头像
            if ("Phone".equals(mLoginType)) {
                if (!SpeedTestUtil.getInstance().isNetwork(mActivity)) {
                    return;
                }

                if (SpeedTestDataSet.mPersonalCenterMap == null) {
                    showCommon("缺失配置");
                    return;
                }
                selectPhoto(v);
            } else if ("Tourist".equals(mLoginType)) { //跳转至登录界面
                Intent intent = new Intent(MainHomePersonalActivity.this, LoginActivity.class);
                goIntent(intent, false);
            }
        } else if (id == R.id.mainhome_personal_btn_fj_code) {//福建版下载
            if (SpeedTestDataSet.mPersonalCenterMap == null) {
                showCommon("缺失配置");
                return;
            }
            new MyCodeDialog(this).setData(SpeedTestDataSet.mPersonalCenterMap).show(0);
        } else if (id == R.id.mainhome_personal_btn_qg_code) {//全国版下载
            if (SpeedTestDataSet.mPersonalCenterMap == null) {
                showCommon("缺失配置");
                return;
            }
            new MyCodeDialog(this).setData(SpeedTestDataSet.mPersonalCenterMap).show(1);
        } else if (id == R.id.mainhome_personal_btn_share) {//测速小知识
            if (SpeedTestDataSet.mPersonalCenterMap == null) {
                showCommon("缺失配置");
                return;
            }
            Intent intentDown = new Intent(this, BookWebActivity.class);
            intentDown.putExtra("Url", SpeedTestDataSet.mPersonalCenterMap.get("52"));
            intentDown.putExtra("Title", "测速小知识");
            goIntent(intentDown, false);
        } else if (id == R.id.mainhome_personal_mtv_notice) {
            goIntent(MsgListActivity.class, null, false);
        } else if (id == R.id.mainhome_personal_btn_set) {//设置
            if (SpeedTestDataSet.mPersonalCenterMap == null) {
                showCommon("缺失配置");
                return;
            }
            String str53 = SpeedTestDataSet.mPersonalCenterMap.get("53");
            String str54 = SpeedTestDataSet.mPersonalCenterMap.get("54");
            Intent intentSet = new Intent(this, SettingsActivity.class);
            intentSet.putExtra("str53", str53);
            intentSet.putExtra("str54", str54);
            goIntent(intentSet, false);
        } else if (id == R.id.mainhome_personal_btn_clearCache) {//清空缓存
            new CommonDialog(MainHomePersonalActivity.this).setListener(MainHomePersonalActivity.this)
                    .setButtonText("确定", "取消").show("该操作会清空本地测试数据\n是否清空缓存数据？",
                    EnumRequest.DIALOG_TOAST_BTN_THREE.toInt());
        } else if (id == R.id.mainhome_personal_btn_exit) {//退出
            if (!SpeedTestUtil.getInstance().isNetwork(mActivity)) {
                return;
            }

            new CommonDialog(MainHomePersonalActivity.this).setListener(MainHomePersonalActivity.this)
                    .setButtonText("确定", "取消").show("您确定要退出登录？",
                    EnumRequest.DIALOG_TOAST_BTN_ONE.toInt());
        }
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		//退出登录提示
		if (type ==  EnumRequest.DIALOG_TOAST_BTN_ONE.toInt()) {
			if (isTrue) {
				showMyDialog();
//				Wq_ReadOtherCellNameUtil.getIntances().closeUserId();
				//退出登录选择
				SessionBean mSessionBean = new SessionBean();
				mSessionBean.setSession_id(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().LOGIN_SESSION_ID, ""));
				NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_USER_EXIT_LOGIN),JsonHandler.getHandler().toJson(mSessionBean), -1, null);

				//退出 清空手机号登陆信息  游客登陆信息   再进来就是新游客
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("PhoneUserId", "");
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("PhoneSessionId", "");
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristUserId", "");
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristSessionId", "");
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().USER_PHONE(), "");
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setLongShared(TypeKey.getInstance().LAST_SMS_CODE_LONG_TIME, 0);
				//清空用户ID
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_ID(), "");
				SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, "");

				//游客登陆
				touristLogin();
			}
		}else if (type == EnumRequest.DIALOG_TOAST_BTN_THREE.toInt()) {
			if (isTrue) {
				showMyDialog();
				new Thread(new Runnable() {

					@Override
					public void run() {
						String loginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
						if("Tourist".equals(loginType)){ //游客登陆
							SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristUserId", "");
							SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristSessionId", "");
						}

						List<Db_Modular> queryAllModular = (List<Db_Modular>) DbHandler.getInstance().queryAll(Db_Modular.class);
						DbHandler.getInstance().deleteDb();
						SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().APP_CONF_DICT_TIME(), "");//字典配置时间
						SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().APP_CONF_FTP_TIME(), "");//ftp配置时间
						if (queryAllModular != null && queryAllModular.size() > 0) {
							for (Db_Modular db_Modular : queryAllModular) {
								DbHandler.getInstance().insert(db_Modular);
							}
						}
						sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.TASK_MAINHOME_CLEARCACHE.toInt()));
					}
				}).start();
			}
		}else if(type == LOGIN_TYPE_NET_TOURIST){
			dismissMyDialog();
			if (isTrue) {
				Login responseLogin = JsonHandler.getHandler().getTarget(object.toString(), Login.class);
				if (responseLogin != null){
					if(responseLogin.isRs()) {
						saveTouristUserData(responseLogin);
					}else{
						loginErr(responseLogin.getMsg());
					}
				}
			}else {
				loginErr(object.toString());
			}
		}else if(type == EnumRequest.MENU_SELECT_ONE.toInt()){
			if(SpeedTestDataSet.mPersonalCenterMap == null){
				showCommon("缺失配置");
				return;
			}

			String url = SpeedTestDataSet.mPersonalCenterMap.get("4702");

			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
			intent.putExtra(Intent.EXTRA_TEXT, "“5G网速测试”下载地址：" + url + "（请使用系统原生浏览器下载）");
			goIntent(Intent.createChooser(intent, "分享软件"), false);
		}
	}

	/**
	 * 游客登录
	 * @param info
	 */
	private void saveTouristUserData(Login info){

		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("QgLoginType", "Tourist");

		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristUserId", info.getDatas().getUser_id());
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("TouristSessionId", info.getDatas().getSession_id());

		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("PhoneUserId", "");
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("PhoneSessionId", "");
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().USER_PHONE(), "");

		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_SESSION_ID, info.getDatas().getSession_id());
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_ID(), info.getDatas().getUser_id());
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_NAME(), Base64Utils.encrytorDes3(info.getDatas().getUser_name()));
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_MENU(), info.getDatas().getMenu_ids());
		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared(TypeKey.getInstance().LOGIN_DEPART(), info.getDatas().getDepart_codes());

		SharedPreHandler.getShared(SpeedTest5g.getContext()).setStrShared("QgLoginType", "Tourist");

		//发广播更新 radiobButton
		Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
		intent.putExtra("type", -1000);
		MainHomePersonalActivity.this.sendBroadcast(intent);
	}

	private void touristLogin(){
		RequestSmsCodeLogin smsCodeLoginRequset = new RequestSmsCodeLogin();
		smsCodeLoginRequset.imsi=(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMSI(), ""));
		smsCodeLoginRequset.imei=(SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared(TypeKey.getInstance().PHONE_IMEI(), ""));
		smsCodeLoginRequset.mobile=("-1");
		smsCodeLoginRequset.uuid=UUID.randomUUID().toString();
		NetWorkUtilNow.getInstances().readNetworkPostJsonOfStringDes(NetWorkUtilNow.getInstances().getToIp() + getString(R.string.URL_APP_LOGIN_QG),
				JsonHandler.getHandler().toJson(smsCodeLoginRequset), LOGIN_TYPE_NET_TOURIST, MainHomePersonalActivity.this,30 * 1000);
	}

	//显示错误信息提示
	private void loginErr(String strMsg){
		if (isFinishing()) {
			return;
		}
		if (TextUtils.isEmpty(strMsg) || strMsg.length() > 200) {
			strMsg = "网络开小差了";
		}
		isTemporary = true;
		if (mToastDialog == null) {
			mToastDialog = new CommonDialog(MainHomePersonalActivity.this);
		}
		mToastDialog.show(strMsg);
	}

	@Override
	public void onHandleMessage(MyEvents events) {
		switch (events.getMode()) {
		case TASK:
			//清空缓存
			if (events.getType() == EnumRequest.TASK_MAINHOME_CLEARCACHE.toInt()) {
				dismissMyDialog();
				showCommon("缓存清除成功");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try{
			if(mReceiver != null){
				unregisterReceiver(mReceiver);
			}
			if(mOnResumeReceiver != null){
				unregisterReceiver(mOnResumeReceiver);
			}
			if(mUpdateReceiver != null){
				unregisterReceiver(mUpdateReceiver);
			}
			if(mSelectReceiver != null){
				unregisterReceiver(mSelectReceiver);
			}
		}catch(Exception e){
		}
	}
}
