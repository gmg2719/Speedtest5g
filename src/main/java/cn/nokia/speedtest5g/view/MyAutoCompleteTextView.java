package cn.nokia.speedtest5g.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.util.InputMethodUtil;

import java.util.ArrayList;
import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.bean.HistoryDb;
import cn.nokia.speedtest5g.app.db.DbHandler;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.TimeUtil;

/**
 * 自定义模糊搜索框，自动匹配数据--以下1,2点必须调用其他一点才可显示历史，否则无效
 * 
 * 	1.插入要模糊搜索的全部数据，以下调用一种即可
 * 		1.1:调用setData(List<String> arrAllHistory);
 * 		1.2:调用initHistory(final int historyType);
 * 
 * 	2.新增一条历史记录，以下调用一种即可
 * 		2.1:addHistory(String str);不保存数据库
 * 		2.2:saveHistory(final String str,final int historyType);需保存数据库
 * 
 * 	3.设置文本为空且不显示历史提示setTextNull();
 * @author zwq
 *
 */
@SuppressLint({"ClickableViewAccessibility", "AppCompatCustomView"})
@SuppressWarnings("unchecked")
public class MyAutoCompleteTextView extends AutoCompleteTextView {
	//初始化
	private final int WHAT_INIT = 1;
	//添加记录
	private final int WHAT_ADD = 2;
	//判断软键盘
	private final int WHAT_SOFT = 3;
	//判断删除
	private final int WHAT_DEL = 4;
	//历史数据更新
	private final int WHAT_UPDATE = 5;
	//首次加载是否显示历史
	private boolean isFirstShow = true;

	//类型 -1为默认类型
	private int mType = -1;
	private ListenerBack mListenerBack;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == WHAT_INIT) {
				setData((List<String>) msg.obj);
			}else if (msg.what == WHAT_ADD) {
				addHistory(msg.obj.toString());
			}else if (msg.what == WHAT_SOFT) {
				if (!isSoftShowing()) {
					InputMethodUtil.getInstances().show(getContext());
				}
			}else if(msg.what == WHAT_DEL){
				removeHistory(msg.obj.toString());
			}else if (msg.what == WHAT_UPDATE) {
				updateData((List<String>) msg.obj);
			}
			return true;
		}
	});

	private int paddingSize;

	public MyAutoCompleteTextView(Context context) {
		super(context);
		init();
	}

	public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		setGravity(Gravity.CENTER_VERTICAL);
		final float scale = getContext().getResources().getDisplayMetrics().density;  
		this.paddingSize = (int) (10 * scale + 0.5f);  
		setDropDownVerticalOffset(paddingSize/10);
		setDropDownBackgroundResource(android.R.color.transparent);
		//添加获取焦点事件
		setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && mAdapter != null) {
					if (mAdapter.getCount() <= 0 && isShowToastLast) {
						addHistory(toastLast);
					}
					nowStrInput = getText().toString();
					setHeightDrop(mAdapter.getCount());
					try {
						if (getContext() != null && !((Activity)getContext()).isFinishing()) {
							showDropDown();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					setText(nowStrInput);
					setSelection(nowStrInput.length());
				}
			}
		});

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAdapter != null) {
					int selectionStart = getSelectionStart();
					if (mAdapter.getCount() <= 0 && isShowToastLast) {
						addHistory(toastLast);
					}
					nowStrInput = getText().toString();
					setHeightDrop(mAdapter.getCount());
					try {
						if (getContext() != null && !((Activity)getContext()).isFinishing()) {
							showDropDown();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					setText(nowStrInput);
					//游标
					setSelection(selectionStart);
					//					dismissDropDown();
				}
				mHandler.removeMessages(WHAT_SOFT);
				mHandler.sendEmptyMessageDelayed(WHAT_SOFT, 500);
			}
		});

		setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isShowToastLast) {
					if (parent.getAdapter().getItem(position) instanceof Spanned) {
						String strItem = ((Spanned)parent.getAdapter().getItem(position)).toString();
						if (strItem.equals(toastLast)) {
							setText(nowStrInput);
							setSelection(nowStrInput.length());
						}
					}
				}
				if(mListenerBack!=null){
					mListenerBack.onListener(mType, parent.getAdapter().getItem(position), true);
				}
			}
		});
	}

	//判断是否软键盘开启
	public boolean isSoftShowing() {  
		if (getContext() instanceof Activity) {
			//获取当前屏幕内容的高度  
			int screenHeight = ((Activity)getContext()).getWindow().getDecorView().getHeight();  
			//获取View可见区域的bottom  
			Rect rect = new Rect();  
			((Activity)getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
			return screenHeight - rect.bottom != 0;  
		}
		return true;
	}  

	//是否显示提示
	private boolean isNullDrop = true;
	/**
	 * 设置文本为空，且不显示历史记录
	 */
	public void setTextNull() {
		setText("");
		this.isNullDrop = false;
	}

	public void hideDrop() {
		this.isNullDrop = false;
	}

	//当前输入的数据
	private String nowStrInput = "";
	//显示在最后一条的记录，不做过滤
	private String toastLast = "暂无搜索记录";
	//是否显示最后一条消息提示
	private boolean isShowToastLast = false;
	/**
	 * 是否显示提示无数据内容
	 * @param isShow
	 */
	public void setShowToastLast(boolean isShow){
		this.isShowToastLast = isShow;
	}

	/**
	 * 设置无数据时候的提示
	 * @param str
	 */
	public void setToastLast(String str){
		this.toastLast = str;
	}

	private MyAutoCompleteTextViewAdapter mAdapter;

	private List<String> mArrListAllHistory;

	/**
	 * 设置全部历史记录
	 * @param arrAllHistory
	 */
	public void setData(List<String> arrAllHistory){
		mArrListAllHistory = arrAllHistory;
		if (mAdapter == null) {
			setThreshold(0);
			mAdapter = new MyAutoCompleteTextViewAdapter(getContext());
			setAdapter(mAdapter);
		}
		mAdapter.setAdapterData(arrAllHistory);
		if (!isFirstShow) {
			dismissDropDown();
		}
	}

	/**
	 * 更新历史数据
	 * 
	 * @param arrAllHistory
	 */
	public void updateData(List<String> arrAllHistory) {
		mArrListAllHistory = arrAllHistory;
		setThreshold(0);
		mAdapter = new MyAutoCompleteTextViewAdapter(getContext());
		mAdapter.setAdapterData(mArrListAllHistory);
		setAdapter(mAdapter);
	}

	/**
	 * 添加一条历史记录
	 * @param str
	 */
	public void addHistory(String str){
		if (TextUtils.isEmpty(str)) {
			return;
		}
		if (mArrListAllHistory == null) {
			mArrListAllHistory = new ArrayList<String>();
		}else if(mArrListAllHistory.size() > 0){
			if (mArrListAllHistory.get(mArrListAllHistory.size() - 1).toString().equals(toastLast)) {
				mArrListAllHistory.remove(mArrListAllHistory.size() - 1);
			}
		}
		mArrListAllHistory.add(str);
		setThreshold(0);
		mAdapter = new MyAutoCompleteTextViewAdapter(getContext());
		mAdapter.setAdapterData(mArrListAllHistory);
		setAdapter(mAdapter);
	}

	/**
	 * 添加一条历史记录
	 * @param str
	 */
	public void removeHistory(String str){
		if (TextUtils.isEmpty(str)) {
			return;
		}
		if (mArrListAllHistory == null) {
			mArrListAllHistory = new ArrayList<String>();
		}else if(mArrListAllHistory.size() > 0){
			mArrListAllHistory.remove(str);
			//			if (mArrListAllHistory.size()==0) {
			//				mArrListAllHistory.add(toastLast);
			//			}
		}

		setThreshold(0);
		mAdapter = new MyAutoCompleteTextViewAdapter(getContext());
		mAdapter.setAdapterData(mArrListAllHistory);
		setAdapter(mAdapter);
	}

	/**
	 * 保存历史搜索数据
	 * @param str 保存的历史数据
	 * @param historyType 对应的历史类型
	 */
	public void saveHistory(String str,int historyType){
		saveHistory(str, historyType, false);
	}

	/**
	 * 保存历史搜索数据
	 * @param str 保存的历史数据
	 * @param historyType 对应的历史类型
	 * @param isEncryption 是否加密
	 */
	public void saveHistory(final String str,final int historyType,final boolean isEncryption){
		saveHistory(str, historyType, isEncryption, true);
	}

	/**
	 * 保存历史搜索数据
	 * @param str 保存的历史数据
	 * @param historyType 对应的历史类型
	 * @param isEncryption 是否加密
	 */
	public void saveHistory(final String str,final int historyType,final boolean isEncryption,final boolean isUpdateUi){
		if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str.trim())) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					ArrayList<HistoryDb> queryObjHistoryDb = null;
					if (isEncryption) {
						queryObjHistoryDb = (ArrayList<HistoryDb>) DbHandler.getInstance().queryObj(HistoryDb.class, "type=? AND valueName=?", new Object[]{historyType,Base64Utils.encrytorDes3(str)});
					}else {
						queryObjHistoryDb = (ArrayList<HistoryDb>) DbHandler.getInstance().queryObj(HistoryDb.class, "type=? AND valueName=?", new Object[]{historyType,str});
					}
					HistoryDb updateHistory;
					if (queryObjHistoryDb != null && queryObjHistoryDb.size() > 0) {
						updateHistory = queryObjHistoryDb.get(0);
						updateHistory.setTimeDate(TimeUtil.getInstance().getNowTimeSS());
						DbHandler.getInstance().updateObj(updateHistory);
					}else {
						updateHistory = new HistoryDb();
						updateHistory.setTimeDate(TimeUtil.getInstance().getNowTimeSS());
						updateHistory.setType(historyType);
						updateHistory.setValueName(isEncryption ? Base64Utils.encrytorDes3(str) : str);
						// 保存的历史数据		
						DbHandler.getInstance().insert(updateHistory);

						// 保存成功后重新查询历史数据，并更新历史列表排序
						ArrayList<String> arrHistory = queryHistory(historyType, isEncryption);
						Message msg = mHandler.obtainMessage();
						msg.what = WHAT_UPDATE;
						msg.obj = arrHistory;
						mHandler.sendMessage(msg);
					}
				}
			}).start();
		}
	}

	private ArrayList<String> queryHistory(int historyType, boolean isEncryption) {
		ArrayList<HistoryDb> queryObj = (ArrayList<HistoryDb>) DbHandler.getInstance().queryObj(HistoryDb.class, "type=? AND timeDate>?", new Object[] { historyType, TimeUtil.getInstance().getNowTimeSS(TimeUtil.getInstance().getDataTimeToLong(-30)) }, "timeDate DESC");
		ArrayList<String> arrHistory = new ArrayList<String>();
		if (queryObj != null && queryObj.size() > 0) {
			if (isEncryption) {
				for (HistoryDb historyDb : queryObj) {
					arrHistory.add(Base64Utils.decryptorDes3(historyDb.getValueName()));
				}
			} else {
				for (HistoryDb historyDb : queryObj) {
					arrHistory.add(historyDb.getValueName());
				}
			}
		}
		return arrHistory;
	}

	public void deleteHistory(final String str,final int historyType,final boolean isEncryption){
		if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str.trim())) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					ArrayList<HistoryDb> queryObjHistoryDb = null;
					if (isEncryption) {
						queryObjHistoryDb = (ArrayList<HistoryDb>) DbHandler.getInstance().queryObj(HistoryDb.class, "type=? AND valueName=?", new Object[]{historyType,Base64Utils.encrytorDes3(str)});
					}else {
						queryObjHistoryDb = (ArrayList<HistoryDb>) DbHandler.getInstance().queryObj(HistoryDb.class, "type=? AND valueName=?", new Object[]{historyType,str});
					}
					HistoryDb deleteHistory;
					if (queryObjHistoryDb != null && queryObjHistoryDb.size() > 0) {
						deleteHistory = queryObjHistoryDb.get(0);
						DbHandler.getInstance().deleteObj(deleteHistory);
						Message msg = mHandler.obtainMessage();
						msg.what = WHAT_DEL;
						msg.obj = str;
						mHandler.sendMessage(msg);
					}
				}

			}).start();
		}
	}

	/**
	 * 初始化查询历史数据
	 * @param historyType 对应的历史类型
	 */
	public void initHistory(int historyType){

		this.mType = historyType;
		initHistory(historyType, false);
	}

	/**
	 * 初始化查询历史数据
	 * @param historyType 对应的历史类型
	 * @param isEncryption 是否加密
	 */
	public void initHistory(final int historyType,final boolean isEncryption){
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<String> arrHistory = queryHistory(historyType, isEncryption);
				Message msg = mHandler.obtainMessage();
				msg.what = WHAT_INIT;
				msg.obj = arrHistory;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	public void setFirstShow(boolean isFirstShow){
		this.isFirstShow = isFirstShow;
	}

	public ArrayList<Spanned> getHistory(){
		if (mAdapter == null) {
			return new ArrayList<Spanned>();
		}
		return mAdapter.mListHistory;
	}

	/**
	 * 设置提示内容的文字颜色
	 * @param colorHint
	 */
	//	public void setToastColor(int colorToast){
	////		this.toastColor = colorToast;
	//	}

	private boolean isMarginLeft = true;
	public void setNotLeftMargin(){
		this.isMarginLeft = false;
	}

	/**
	 * 历史记录适配器
	 * @author zwq
	 */
	private class MyAutoCompleteTextViewAdapter extends BaseAdapter implements Filterable{

		//历史数据集合
		private ArrayList<Spanned> mListHistory,mUnfilteredData;  

		private Context mContext;

		private ArrayFilter mFilter;
		private MyAutoCompleteTextViewAdapter(Context context){
			this.mContext = context;
			this.mListHistory = new ArrayList<Spanned>();
		}

		//设置全部要搜索的记录
		private void setAdapterData(List<String> arrAllHistory){
			if(mListHistory!=null){
				mListHistory.clear();
			}else{
				this.mListHistory = new ArrayList<Spanned>();
			}

			if (arrAllHistory != null && arrAllHistory.size() > 0) {
				for (String str : arrAllHistory) {
					mListHistory.add(Html.fromHtml(str));
				}
			}
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mListHistory == null ? 0 : mListHistory.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mListHistory.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@SuppressLint("InflateParams") 
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HistoryLoginHolder holder = null;
			if(convertView == null){
				holder = new HistoryLoginHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_history_login, null);
				holder.tvHistoryLoginAccount = (TextView) convertView.findViewById(R.id.tv_history_login_account);
				holder.ivHistoryLoginAccountDelete = (ImageView) convertView.findViewById(R.id.iv_history_login_account_delete);
				holder.vHistoryLoginAccountvLine = convertView.findViewById(R.id.v_history_login_line);
				if (!isMarginLeft) {
					LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.tvHistoryLoginAccount.getLayoutParams();
					layoutParams.setMargins(15, 0, 0, 0);  
					holder.tvHistoryLoginAccount.setLayoutParams(layoutParams);
				}
				convertView.setTag(holder);
			}else{
				holder = (HistoryLoginHolder) convertView.getTag();
			}

			holder.tvHistoryLoginAccount.setText((Spanned)getItem(position));
			holder.ivHistoryLoginAccountDelete.setOnClickListener(new MyClickListener(((Spanned)getItem(position)).toString()));
			if(mType !=TypeKey.getInstance().HISTORY_LOGIN_WANGYOUBAO && mType !=TypeKey.getInstance().HISTORY_LOGIN_4A){
				holder.vHistoryLoginAccountvLine.setVisibility(View.INVISIBLE);
			}

			return convertView;  
		}

		@Override
		public Filter getFilter() {
			if (mFilter == null) {  
				mFilter = new ArrayFilter();  
			}  
			return mFilter;  
		}

		//建立过滤器
		private class ArrayFilter extends Filter {  

			//数据过滤
			@Override  
			protected FilterResults performFiltering(CharSequence prefix) {
				FilterResults results = new FilterResults();  
				if (mUnfilteredData == null) {  
					mUnfilteredData = new ArrayList<Spanned>(mListHistory);  
				}  
				nowStrInput = getText().toString();  
				if (prefix == null || prefix.length() == 0) {  
					ArrayList<Spanned> list = mUnfilteredData;  
					results.values = list;  
					results.count = list.size();  
					if (isNullDrop) {
						postDelayed(new Runnable() {
							public void run() {
								try {
									if (getContext() != null && !((Activity)getContext()).isFinishing()) {
										showDropDown();
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}, 200);
					}
				} else {  
					ArrayList<Spanned> unfilteredValues = mUnfilteredData;  
					int count = unfilteredValues.size();  
					ArrayList<Spanned> newValues = new ArrayList<Spanned>(count);  
					for (int i = 0; i < count; i++) {
						String value = unfilteredValues.get(i).toString();
						if (value.contains(nowStrInput) && !value.equals(toastLast)) {//实现模糊查询    
							newValues.add(Html.fromHtml(value.replaceAll(nowStrInput, "<font color= 'red'>" + nowStrInput + "</font>")));
						}
						results.values = newValues;  
						results.count = newValues.size();  
					}  
					if (results.count <= 0 && isShowToastLast) {
						newValues.add(Html.fromHtml(toastLast));
						results.values = newValues;  
						results.count = newValues.size();  
					}
				}
				if (!isNullDrop) {
					results.count = 0;
					isNullDrop = true;
				}
				return results;  
			}  

			@Override  
			protected void publishResults(CharSequence constraint, FilterResults results) {  
				mListHistory = (ArrayList<Spanned>) results.values;  
				if (results.count > 0) {  
					setHeightDrop(results.count);
					notifyDataSetChanged();
				} else {  
					notifyDataSetInvalidated();  
				}  
			}  
		}  
	}

	//设置提示框高度
	private void setHeightDrop(int size){
		if (size > 5) {
			setDropDownHeight(paddingSize * 22);
		}else if(size <= 1){
			setDropDownHeight((int) (paddingSize * 5.5f * size));
		}else {
			setDropDownHeight((int) (paddingSize * 4.5f * size));
		}
	}

	//清除按钮图标
	private Drawable mCloseDrawable;
	//右边清除按钮ID
	private int mResourcesDrawableCloseId;

	/**
	 *  设置是否当内容不为空时候显示清空按钮
	 * @param resourcesDrawableId
	 */
	public void setDrawableRightClose(int resourcesDrawableId){
		mResourcesDrawableCloseId = resourcesDrawableId;
		if (mResourcesDrawableCloseId > 0) {
			setOnTouchListener(touchListener);
		}
	}

	//获取清除按钮
	private void getCloseDrawable(){
		if (mCloseDrawable == null) {
			mCloseDrawable = getResources().getDrawable(mResourcesDrawableCloseId);
			int intrinsicWidth = mCloseDrawable.getIntrinsicWidth();
			int intrinsicHeight = mCloseDrawable.getIntrinsicHeight();
			int temHeight = getHeight() - getPaddingTop() - getPaddingBottom();
			if (intrinsicHeight > temHeight && temHeight > 0) {
				intrinsicHeight = temHeight;
				intrinsicWidth = (int) ((float)intrinsicWidth * ((float)intrinsicHeight/(float)mCloseDrawable.getIntrinsicHeight()));
			}
			mCloseDrawable.setBounds(0,0,intrinsicWidth,intrinsicHeight);  //此为必须写的
		}
	}

	/**
	 * 设置左边图标
	 * @param resourcesDrawableId
	 */
	public void setDrawableLeft(int resourcesDrawableId){
		Drawable drawableLeft = null;
		if (resourcesDrawableId > 0) {
			drawableLeft = getResources().getDrawable(resourcesDrawableId);
			int intrinsicWidth = drawableLeft.getIntrinsicWidth();
			int intrinsicHeight = drawableLeft.getIntrinsicHeight();
			int temHeight = getHeight() - getPaddingTop() - getPaddingBottom();
			if (intrinsicHeight > temHeight && temHeight > 0) {
				intrinsicHeight = temHeight;
				intrinsicWidth = intrinsicWidth * (intrinsicHeight/drawableLeft.getIntrinsicHeight());
			}
			drawableLeft.setBounds(0,0,drawableLeft.getIntrinsicWidth(),intrinsicHeight);  //此为必须写的
		}
		Drawable[] compoundDrawables = getCompoundDrawables();
		setCompoundDrawables(drawableLeft, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
	}

	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		initCloseBit();
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}

	private void initCloseBit(){
		if (mResourcesDrawableCloseId > 0) {
			Drawable[] compoundDrawables = getCompoundDrawables();
			if (length() > 0) {
				if (compoundDrawables[2] == null) {
					getCloseDrawable();
					setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], mCloseDrawable, compoundDrawables[3]);
				}
			}else {
				setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], null, compoundDrawables[3]);
			}
		}
	}

	/**
	 * 触摸事件
	 */
	private OnTouchListener touchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mResourcesDrawableCloseId > 0 && length() > 0) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					//触摸点位置判断
					if (event.getX() > getWidth() - getPaddingRight() - getCompoundDrawables()[2].getMinimumWidth()) {
						return true;
					}
					break;
				case MotionEvent.ACTION_UP:
					//触摸点位置判断
					if (event.getX() > getWidth() - getPaddingRight() - getCompoundDrawables()[2].getMinimumWidth() && event.getX() < getWidth() &&
							event.getY() > 0 && event.getY() < getHeight()) {
						setTextNull();
						return true;
					}
					break;
				default:
					break;
				}
			}
			return false;
		}
	};

	private class MyClickListener implements OnClickListener{
		private String mValue;

		public MyClickListener(String value) {
			// TODO Auto-generated constructor stub
			this.mValue = value;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
            if (v.getId() == R.id.iv_history_login_account_delete) {
                deleteHistory(mValue, mType, false);
            }
		}

	}

	private class  HistoryLoginHolder{
		public View vHistoryLoginAccountvLine;
		private TextView tvHistoryLoginAccount;
		private ImageView ivHistoryLoginAccountDelete;
	}

	public void setListenerBack(ListenerBack back){
		this.mListenerBack = back;
	}
}
