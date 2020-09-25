package cn.nokia.speedtest5g.app.activity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.WybLog;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.view.actionbar.MyActionBar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ZoomButtonsController;

/**
 * 网页加载
 * @author zwq
 *
 */
public class BookWebActivity extends BaseActionBarActivity {
	@SuppressWarnings("unused")
	private final String TAG = "BookWebActivity";
	private WebView mWebView;
	private String mTitle;
	private String URL = "http://183.252.192.83/jankan/index.html";
	private ProgressBar mProgressBar;
	private Map<String,String> mapHeaders;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_book_web);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		String strUrl = getIntent().getStringExtra("Url");
		if (strUrl != null && !strUrl.isEmpty()) {
			URL = strUrl;
		}else {
			URL = NetWorkUtilNow.getInstances().getToIp();
			String[] splitUrl = URL.split("/");
			URL = URL.replace(splitUrl[splitUrl.length - 1] + "/", "jankan/index.html");
		}
		if (getIntent().hasExtra("Title")) {
			mTitle = getIntent().getStringExtra("Title");
		}
		WybLog.syso("地址：" + URL);
		init(mTitle, true, true);
		webSetting();
	}

	@Override
	public void init(Object titleId, boolean isBack, boolean isScroll) {
		super.init(titleId, isBack, isScroll);
		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_menu_change, EnumRequest.MENU_SELECT_ONE.toInt(), ""));
	}

	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		if (type == EnumRequest.MENU_BACK.toInt()) {
			//			if (mWebView.canGoBack()) {
			//				mWebView.goBack();
			//			} else {
			this.finish();
			//			}
		}else if (type == EnumRequest.MENU_SELECT_ONE.toInt()) {
			mWebView.reload();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("SetJavaScriptEnabled")
	private void webSetting() {
		mWebView = (WebView) findViewById(R.id.webView);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLoadWithOverviewMode(true);
		if (Build.VERSION.SDK_INT >= 11) {// 用于判断是否为Android 3.0系统,然后隐藏缩放控件
			webSettings.setDisplayZoomControls(false);
		} else {
			setZoomControlGone(mWebView);
		}
		//user_id和auth_code，auth_code=decryptorDes3(user_id+时间戳)

		getHeaderMap();
		mWebView.loadUrl(URL,mapHeaders);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mapHeaders == null) {
					getHeaderMap();
				}
				view.loadUrl(url,mapHeaders);
				return true;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					mProgressBar.setVisibility(View.GONE);
				} else {
					if (mProgressBar.getVisibility() == View.GONE)
						mProgressBar.setVisibility(View.VISIBLE);
					mProgressBar.setProgress(newProgress);
				}
			}
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (TextUtils.isEmpty(mTitle)) {
					actionBar.setTitle(title);
				}
			}
		});
		mWebView.setDownloadListener(new WebViewDownLoadListener());
	}

	private void getHeaderMap(){
		mapHeaders = new HashMap<String, String>();
		mapHeaders.put("user_id",getUserID());
		mapHeaders.put("auth_code",NetWorkUtilNow.getInstances().getUtf(Base64Utils.encrytorDes3(getUserID() + System.currentTimeMillis())));
	}

	private void setZoomControlGone(View view) {
		Class<WebView> classType;
		Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private class WebViewDownLoadListener implements DownloadListener {
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
				long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
	//			mWebView.goBack();
	//			return true;
	//		}
	//		return super.onKeyDown(keyCode, event);
	//	}
}