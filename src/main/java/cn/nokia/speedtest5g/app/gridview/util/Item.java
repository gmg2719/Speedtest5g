package cn.nokia.speedtest5g.app.gridview.util;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spanned;
import android.text.TextUtils;

import java.io.Serializable;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;

/**
 * 应用模块信息
 */
@SuppressWarnings("serial")
public class Item implements Serializable {
	public String menuCode;// 应用模块菜单编码（权限码）
	private int id;
	private String name;
	private Spanned mSpanned;
	private int drawable;
	private String content;
	private Spanned mSpannedContent;
	private String[] tagArr;
	// 是否是特殊控件(不可删除，不可移动)---添加控件
	private boolean isSpecial;
	// 消息数量
	private int number;
	// 是否可点击
	private boolean isClick = true;
	// 当前是否选中
	private boolean isSelect = false;
	// 是否标记红点
	private boolean isMarkRed = false;
	// 字体颜色
	private int colorTv = Color.WHITE;
	// 背景颜色
	private int colorBg = Color.TRANSPARENT;
	// 是否现在左上边文本
	public int rightTopIcon = -1;
	public boolean isEditStatus = false;// 是否进入编辑状态
	public boolean isMyApp = false;// 是否是我的应用

	// ------------启动相关数据start
	// 对应包名
	public String strPackage;
	// 启动的绝对路径
	public String strLauchPath;
	// 下载地址
	public String appDownUrl;
	// 提示内容
	private String testMsg;
	// 统计标题
	private String titleCode;
	// 统计代号--风云榜
	private String code;
	//升级状态 （2强制升级，其他则无需强制-但如果无应用情况下，需强制升级）
	public int updageStatus;
	// 跳转对象
	private Intent intent;
	private boolean isStartActivityForResult = false;
	private int requestCode;
	//图标地址
	public String iconUrl;
	//最新版本
	public int newAppVersionCode;
	//最新版本
	public String newAppVersionName;
	//最新版本升级内容
	public String newAppContent;
	// ------------启动相关数据end
	//模块图标地址
	public String drawableUrl;

	/**
	 * 初始化模块时，使用该构造函数
	 * 
	 * @param menuCode
	 *            模块编码=权限码，没有权限的模块不会展示
	 * @param id
	 * @param name
	 *
	 * @see cn.nokia.speedtest5g.app.uitl.ModeClickHandler#initGridView(int)
	 */
	public Item(String menuCode, int id, String name, String drawableUrl) {
		super();
		this.menuCode = menuCode;
		this.id = id;
		this.name = name;
		this.drawableUrl = drawableUrl;
		this.drawable = -1;
		if (id != 0) {
			this.code = String.valueOf(id);
		}
	}

	public Item(String name, int colorBg, int colorTv, boolean isSelect) {
		super();
		this.name = name;
		this.colorTv = colorTv;
		this.colorBg = colorBg;
		this.isSelect = isSelect;
	}

	public Item(int id, String name, int drawable, boolean isSp) {
		super();
		this.id = id;
		this.name = name;
		this.drawable = drawable;
		this.isSpecial = isSp;
	}

	public Item(int id, String name, int drawable, int number) {
		super();
		this.id = id;
		this.name = name;
		this.drawable = drawable;
		this.number = number;
	}

	public Item(int id, int number) {
		super();
		this.id = id;
		this.number = number;
	}

	public Item(boolean clicks) {
		super();
		this.isClick = clicks;
		this.name = " ";
	}
	
	/**
	 * 设置第三方应用数据
	 * @param newAppVersionCode 最新版本
	 * @param newAppVersionName 最新版本信息
	 * @param strPackage 启动包
	 * @param strLauchPath 启动类
	 * @param appUrl 下载地址
	 */
	public void setThirdPartyApp(int newAppVersionCode,String newAppVersionName,String newAppContent,String strPackage,String strLauchPath,String appUrl){
		this.newAppVersionCode = newAppVersionCode;
		this.newAppVersionName = newAppVersionName;
		this.newAppContent 	   = newAppContent;
		this.strPackage		   = strPackage;
		this.strLauchPath	   = strLauchPath;
		this.appDownUrl		   = appUrl;
		setTestMsg("'" + name + "'需要更新,是否现在更新?");
	}
	
	public String getVersionContent(){
		if (TextUtils.isEmpty(newAppContent)) {
			return getTestMsg();
		}
		return newAppContent;
	}

	/**
	 * 设置是否是新功能版本
	 * 
	 * @param isNew
	 */
	public void setNewVersion(boolean isNew) {
		this.rightTopIcon = isNew ? R.drawable.icon_new : -1;
		setColorTv(isNew ? Color.RED : ContextCompat.getColor(SpeedTest5g.getContext(), R.color.white));
	}

	public void setColor(int colorTv, int colorBg) {
		this.colorTv = colorTv;
		this.colorBg = colorBg;
	}

	public String[] getTagArr() {
		return tagArr;
	}

	public void setTagArr(String... tagArr) {
		this.tagArr = tagArr;
	}

	public String getContent() {
		return content == null ? "" : content;
	}

	public Item setContent(String content) {
		this.content = content;
		return this;
	}

	public Spanned getmSpannedContent() {
		return mSpannedContent;
	}

	public void setmSpannedContent(Spanned mSpannedContent) {
		this.mSpannedContent = mSpannedContent;
	}

	public Spanned getmSpanned() {
		return mSpanned;
	}

	public void setmSpanned(Spanned mSpanned) {
		this.mSpanned = mSpanned;
	}

	public int getColorBg() {
		return colorBg;
	}

	public void setColorBg(int colorBg) {
		this.colorBg = colorBg;
	}

	public int getColorTv() {
		return colorTv;
	}

	public void setColorTv(int colorTv) {
		this.colorTv = colorTv;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public boolean isMarkRed() {
		return isMarkRed;
	}

	public void setMarkRed(boolean isMarkRed) {
		this.isMarkRed = isMarkRed;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public boolean isClick() {
		return isClick;
	}

	public void setClick(boolean isClick) {
		this.isClick = isClick;
	}

	public String getTestMsg() {
		return testMsg;
	}

	public void setTestMsg(String testMsg) {
		this.testMsg = testMsg;
	}

	public String getTitleCode() {
		return titleCode;
	}

	public void setTitleCode(String titleCode) {
		if (id != 0) {
			this.code = String.valueOf(id);
		}
		this.titleCode = titleCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public boolean isStartActivityForResult() {
		return isStartActivityForResult;
	}

	public void setStartActivityForResult(boolean isStartActivityForResult) {
		this.isStartActivityForResult = isStartActivityForResult;
	}

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

	public boolean isSpecial() {
		return isSpecial;
	}

	public void setSpecial(boolean isSpecial) {
		this.isSpecial = isSpecial;
	}
}
