package cn.nokia.speedtest5g.app.tx.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.nokia.speedtest5g.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;



/**
 * 上拉加载，下拉刷新控件
 */
@SuppressLint("InflateParams") 
public class PullToRefreshListView extends ListView implements OnScrollListener
{
	public static final int REFRESH = 0;
	public static final int LOAD = 1;

	private static final int SPACE = 20;

	private static final int NONE = 0;
	private static final int PULL = 1;
	private static final int RELEASE = 2;
	private static final int REFRESHING = 3;
	private int state;

	private LayoutInflater inflater;
	public View header;
	private View footer;
	private int footerViewHeight; 
	private ImageView arrow;
	private ProgressBar refreshing;

	private TextView noData;
	private TextView loadFull;
	private ProgressBar loading;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;

	private int firstVisibleItem;
	private int scrollState;
	private int headerContentInitialHeight;
	private int headerContentHeight;

	private boolean isRecorded;
	private boolean isLoading;
	private boolean loadEnable = true;
	private boolean pullEnable = false;
	private boolean isLoadFull;
	private int pageSize = 10;
	private View emptyView;
	//当前是否可加载下一页
	public boolean isNextProgress = true;

	private OnRefreshListener onRefreshListener;
	private OnLoadListener onLoadListener;

	public PullToRefreshListView(Context context)
	{
		super(context);
		initView(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		initView(context);
	}

	public void setOnRefreshListener(OnRefreshListener onRefreshListener)
	{
		this.onRefreshListener = onRefreshListener;
	}

	public void setOnLoadListener(OnLoadListener onLoadListener)
	{
		this.loadEnable = true;
		this.onLoadListener = onLoadListener;
	}

	public boolean isLoadEnable()
	{
		return loadEnable;
	}

	public void setScrollState(int state)
	{
		this.scrollState = state;

	}

	public void setLoadEnable(boolean loadEnable) {
		this.loadEnable = loadEnable;
		if (!loadEnable){
			this.removeFooterView(footer);
		}else if(getFooterViewsCount() <= 0){
			this.addFooterView(footer);
		}
	}
	
	public void removeHeader()
	{

		this.removeHeaderView(header);
		
	}


	/**
	 * 
	 * @param pullEnable true:���?false:������
	 */
	public void setPullEnable(boolean pullEnable)
	{
		this.pullEnable = pullEnable;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	//初始化控件
	private void initView(Context context)
	{
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(100);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(100);
		reverseAnimation.setFillAfter(true);

		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.lyq_listview_footer, null);
		loadFull = (TextView) footer.findViewById(R.id.loadFull);
		noData = (TextView) footer.findViewById(R.id.noData);
		loading = (ProgressBar) footer.findViewById(R.id.loading);

		header = inflater.inflate(R.layout.lyq_pull_to_refresh_header, null);
		arrow = (ImageView) header.findViewById(R.id.arrow);
		refreshing = (ProgressBar) header.findViewById(R.id.refreshing);

		// Ϊlistview
		headerContentInitialHeight = header.getPaddingTop();
		measureView(header);
		headerContentHeight = header.getMeasuredHeight();
		topPadding(-headerContentHeight);
		this.addHeaderView(header);
		this.setOnScrollListener(this);

		// footer
		measureView(footer);
		footerViewHeight = footer.getMeasuredHeight();

		footer.setPadding(0, -footerViewHeight, 0, 0);// ���ؽŲ���
		this.addFooterView(footer);
	}

	public void onRefresh()
	{
		if (onRefreshListener != null)
		{
			onRefreshListener.onRefresh();
		}
	}

	public void onLoad()
	{
		if (onLoadListener != null)
		{
			onLoadListener.onLoad();
		}
	}

	public void onRefreshComplete(String updateTime)
	{
		state = NONE;
		refreshHeaderViewByState();
	}

	public void showHeadFirstTime()
	{
		state = REFRESHING;
		refreshHeaderViewByState();
	}

	// 下载刷新完成
	public void onRefreshComplete()
	{
		String currentTime = getCurrentTime();

		onRefreshComplete(currentTime);
		// initEmptyView();
	}

	//上拉加载完成
	public void onLoadComplete()
	{
		isLoading = false;

		footer.setPadding(0, -footerViewHeight, 0, 0);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		this.firstVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		this.scrollState = scrollState;
		ifNeedLoad(view, scrollState);
	}

	private void ifNeedLoad(AbsListView view, int scrollState)
	{
		if (!loadEnable)
		{
			return;
		}
		try
		{
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					&& !isLoading
					&& view.getLastVisiblePosition() == view
							.getPositionForView(footer) && !isLoadFull)
			{

				isLoading = true;
				footer.setPadding(0, 0, 0, 0);
				this.setSelection(this.getCount());

				onLoad();
			}
		}
		catch (Exception e)
		{
		}
	}


//	@Override
//	public boolean onTouchEvent(MotionEvent ev)
//	{
//		if (!pullEnable)
//		{
//			return super.onTouchEvent(ev);
//		}
//        LogUtils.e("PullRefresh", "*** Action: "+ev.getAction());
//		switch (ev.getAction())
//		{
//		case MotionEvent.ACTION_DOWN:
//
//			if (firstVisibleItem == 0)
//			{
//				isRecorded = true;
//				startY = (int) ev.getY();
//			}
//			break;
//		case MotionEvent.ACTION_CANCEL:
//		case MotionEvent.ACTION_UP:
//			if (state == PULL)
//			{
//				state = NONE;
//				refreshHeaderViewByState();
//			}
//			else if (state == RELEASE)
//			{
//				state = REFRESHING;
//				refreshHeaderViewByState();
//				onRefresh();
//			}
//			isRecorded = false;
//			break;
//		case MotionEvent.ACTION_MOVE:
//			whenMove(ev);
//			break;
//		}
//	      
//		return super.onTouchEvent(ev);
//	}

	/**
	 * 增加手势
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
			if (!pullEnable)
			{
				return super.onTouchEvent(ev);
			}
			switch (ev.getAction())
			{
			case MotionEvent.ACTION_DOWN:

				if (firstVisibleItem == 0)
				{
					isRecorded = true;
					startY = (int) ev.getY();
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (state == PULL)
				{
					state = NONE;
					refreshHeaderViewByState();
				}
				else if (state == RELEASE)
				{
					state = REFRESHING;
					refreshHeaderViewByState();
					onRefresh();
				}
				isRecorded = false;
				break;
			case MotionEvent.ACTION_MOVE:
				whenMove(ev);
				break;
			}
		      
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void setEmptyView(View emptyView)
	{
		this.emptyView = emptyView;
		initEmptyView();
	}

	private void initEmptyView()
	{
		int cout = 2; 
		if (!loadEnable)
		{
			cout--;
		}
		if (getAdapter() != null && getAdapter().getCount() == cout)
		{
			emptyView.setVisibility(View.VISIBLE);
		}
		else
		{
			emptyView.setVisibility(View.GONE);
		}
	}

	private void whenMove(MotionEvent ev)
	{
		if (!isRecorded)
		{
			return;
		}
		int tmpY = (int) ev.getY();
		int space = tmpY - startY;
		int topPadding = space - headerContentHeight;
		switch (state)
		{
		case NONE:
			if (space > 0)
			{
				state = PULL;
				refreshHeaderViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);
			if (this.getAdapter() != null && this.getAdapter().getCount() < 2)
			{
				scrollState = SCROLL_STATE_TOUCH_SCROLL;
			}
			if (scrollState == SCROLL_STATE_TOUCH_SCROLL
					&& space > headerContentHeight + SPACE)
			{

				state = RELEASE;
				refreshHeaderViewByState();
			}
			break;
		case RELEASE:
			topPadding(topPadding);
			if (space > 0 && space < headerContentHeight + SPACE)
			{
				state = PULL;
				refreshHeaderViewByState();
			}
			else if (space <= 0)
			{
				state = NONE;
				refreshHeaderViewByState();
			}
			break;
		}

	}

	private void topPadding(int topPadding)
	{
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	/**
	 * 
	 * @param resultSize
	 */
	public void setResultSize(int resultSize)
	{
		if (resultSize == 0)
		{
			isLoadFull = true;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.GONE);
			noData.setVisibility(View.VISIBLE);
		}
		else if (resultSize > 0 && resultSize < pageSize)
		{
			isLoadFull = true;
			loadFull.setVisibility(View.VISIBLE);
			loading.setVisibility(View.GONE);
			noData.setVisibility(View.GONE);
		}
		else if (resultSize == pageSize)
		{
			isLoadFull = false;
			loadFull.setVisibility(View.GONE);
			loading.setVisibility(View.VISIBLE);
			noData.setVisibility(View.GONE);
		}

	}

	//更新状态
	private void refreshHeaderViewByState()
	{
		switch (state)
		{
		case NONE:
			topPadding(-headerContentHeight);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setImageResource(R.drawable.pull_to_refresh_arrow);
			break;
		case PULL:
			arrow.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setAnimation(reverseAnimation);
			break;
		case RELEASE:
			arrow.setVisibility(View.VISIBLE);
			refreshing.setVisibility(View.GONE);
			arrow.clearAnimation();
			arrow.setAnimation(animation);
			break;
		case REFRESHING:
			topPadding(headerContentInitialHeight);
			refreshing.setVisibility(View.VISIBLE);
			arrow.clearAnimation();
			arrow.setVisibility(View.GONE);
			break;
		}
	}

	//计算控件宽度、高度
	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		}
		else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}


	//刷新监听器
	public interface OnRefreshListener
	{
		public void onRefresh();
	}

	/*
	 * ������ظ��ӿ�
	 */
	public interface OnLoadListener
	{
		public void onLoad();
	}

	public String getCurrentTime(String format)
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public String getCurrentTime()
	{
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}

}
