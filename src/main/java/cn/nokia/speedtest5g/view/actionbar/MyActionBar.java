package cn.nokia.speedtest5g.view.actionbar;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.util.InputMethodUtil;
import com.android.volley.util.MarqueesTextView;

import java.util.LinkedList;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.view.MyAutoCompleteTextView;

public class MyActionBar extends RelativeLayout implements OnClickListener {

    private LayoutInflater mInflater;
    private LinearLayout mBarView;
    private TextView tvSpilX,mTvTitleMicro;
    private MarqueesTextView mTitleView;
    private LinearLayout mActionsView;
    private ImageButton mHomeBtn;
    private RelativeLayout mHomeLayout;
    private ImageView mProgress;
    private int tvRightColro = -1;
    private ListenerBack listenerBackSuper;
    //搜索布局
    private RelativeLayout mLayoutSearch;
    //搜索内容
    private MyAutoCompleteTextView mEtSearch;
    //搜索按钮
    private Button mBtnSearch;
    //搜索框显示后右边图标
    private ImageButton mIvSearch;
    private View titleView;

    @SuppressLint("InflateParams")
	public MyActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBarView  = (LinearLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);
        mHomeLayout	   = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeBtn 	   = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);
        tvSpilX 	   = (TextView) mBarView.findViewById(R.id.actionbar_home_x);
        mTitleView 	   = (MarqueesTextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView   = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);
        mProgress 	   = (ImageView) mBarView.findViewById(R.id.actionbar_progress);
        TypedArray a   = context.obtainStyledAttributes(attrs,R.styleable.MyActionBar);
        mTvTitleMicro  = (TextView) mBarView.findViewById(R.id.action_tv_titleMicro);
        CharSequence title = a.getString(R.styleable.MyActionBar_titles);
        if (title != null) {
            setTitle(title);
        }
        a.recycle();
        titleView = mBarView.findViewById(R.id.bar_super_title);
    	if (titleView != null) {
    		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleView.getLayoutParams();
    		layoutParams.height = getStatusBarHeightOne();
    		titleView.setLayoutParams(layoutParams);
    	}
    }
    
    public void setTopColor(int colorId){
    	if(titleView != null){
    		titleView.setBackgroundColor(getResources().getColor(colorId));
    	}
    }
    
    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeightOne() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int height = getResources().getDimensionPixelSize(resourceId);
        if (height == 0) {
			return (int) Math.ceil(20 * getResources().getDisplayMetrics().density);
		}
        return height;
    }
    
    public void setTitleVisibility(int visibility){
    	mBarView.findViewById(R.id.bar_content).setVisibility(visibility);
    }
    
    /**
     * 显示搜索框
     * @param strHint 提示内容
     * @param isShowKeyboard 是否显示软盘
     */
    public void showSearch(String strHint,boolean isShowKeyboard){
    	if (mLayoutSearch == null) {
    		mLayoutSearch  = (RelativeLayout) mBarView.findViewById(R.id.actionbar_layout_search);
    		getEtSearch();
            mBtnSearch	   = (Button) mBarView.findViewById(R.id.actionbar_btn_search);
            mIvSearch      = (ImageButton) mBarView.findViewById(R.id.actionbar_iv_search);
    		mBtnSearch.setOnClickListener(this);
    		mIvSearch.setOnClickListener(this);
		}
    	if (strHint != null) {
    		mEtSearch.setHint(strHint);
		}
    	mLayoutSearch.setVisibility(View.VISIBLE);
    	
		ScaleAnimation animation =new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f); 
				animation.setDuration(300);//设置动画持续时间 
				mEtSearch.startAnimation(animation);
		if (isShowKeyboard) {
			InputMethodUtil.getInstances().show(getContext());
			//获取焦点
			mEtSearch.requestFocus();
		}
    }
    
    /**
     * 设置搜索框时候的按钮。必须在showSearch方法后调用
     * @return
     */
    public void setSearchIv(int resId){
    	if (mIvSearch != null) {
    		mIvSearch.setImageResource(resId);
    		mIvSearch.setVisibility(View.VISIBLE);
    		mBtnSearch.setVisibility(View.GONE);
		}
    }
    
    public void setSearchBtn(String text){
    	if (mBtnSearch != null) {
    		mIvSearch.setVisibility(View.GONE);
    		mBtnSearch.setVisibility(View.VISIBLE);
    		mBtnSearch.setText(text);
		}
    }
    
    public MyAutoCompleteTextView getEtSearch() {
    	if (mEtSearch == null) {
    		mEtSearch = (MyAutoCompleteTextView) mBarView.findViewById(R.id.actionbar_et_search);
		}
		return mEtSearch;
	}

	/**
     * 搜索框内容
     */
    public String getSearch(){
    	if (mEtSearch == null) {
			return "";
		}
    	return mEtSearch.getText().toString();
    }
    
    /**
     * 当前是否有搜索框
     * @return
     */
    public boolean isSearch(){
    	return (mLayoutSearch != null && mLayoutSearch.getVisibility() == View.VISIBLE) ? true : false;
    }
    
    /**
     * 隐藏搜索布局
     */
    public void hintSearch(){
    	if (mLayoutSearch != null) {
    		InputMethodUtil.getInstances().inputMethod(getContext(), mLayoutSearch.getWindowToken());
    		mLayoutSearch.setVisibility(View.GONE);
    		mEtSearch.setTextNull();
		}
    }

    /**
     *  描述	：设置左边菜单---如返回，回到首页
     */
    public void setHomeAction(Action action) {
        mHomeBtn.setOnClickListener(this);
        mHomeBtn.setTag(action);
        mHomeBtn.setImageResource(action.getDrawable());
        mHomeLayout.setVisibility(View.VISIBLE);
        action.setListenerBack(listenerBackSuper);
        tvSpilX.setVisibility(isHomeSpilx);
    }

    public void clearHomeAction() {
        mHomeLayout.setVisibility(View.GONE);
    }
    
    public void setListenerBackSuper(ListenerBack listenerBackSuper) {
		this.listenerBackSuper = listenerBackSuper;
	}

	private int isHomeSpilx = View.VISIBLE;
    
    private int isMenuSpilx = View.VISIBLE;
    
    /**
     * 设置左边菜单是否有分割线
     * @param visibles
     */
    public void setIsHomeSpilx(int visibles){
    	this.isHomeSpilx = visibles;
    	tvSpilX.setVisibility(visibles);
    }

    /**
     * 设置右边菜单是否有分割线
     * @param visibles
     */
    public void setIsMenuSpilx(int visibles){
    	this.isMenuSpilx = visibles;
    }
    
	/**
	 * 设置标题内容
	 */
    public void setTitle(CharSequence title) {
    	setTitle(title, false);
    }
    
	/**
	 * 设置标题内容
	 * 
	 * @param title
	 * @param isScroll 是否g
	 */
	public void setTitle(CharSequence title, boolean isScroll) {
		if (title != null && title.length() > 10 && !isScroll) {
    		title = title.subSequence(0, 10) + "…";
    	}
    	mTitleView.setText(title);
    }
    
    public void setTitleMicro(CharSequence title){
    	if (title != null && title.length() > 10) {
    		title = title.subSequence(0, 10) + "…";
		}
    	mTvTitleMicro.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    public void setTitleColor(int colorId){
    	mTitleView.setTextColor(colorId);
    }
    
    public void setTitleSize(float sizes){
    	mTitleView.setTextSize(sizes);
    }
    
    public MarqueesTextView getTitileView(){
    	return mTitleView;
    }
    
    public void setTitleViewClickListener(){
    	mTitleView.setOnClickListener(this);
    }
    
    private Animation mAnimation;
    /**
     * Set the enabled state of the progress bar.
     * 设置加载动画
     *   or {@link View#GONE}.
     */
    public void setProgressBarVisibility(boolean visibility) {
    	if (mAnimation == null) {
			mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.iv_rotation);  
			LinearInterpolator lin = new LinearInterpolator();  
			mAnimation.setInterpolator(lin); 
		}
		if (visibility) {
			if (mProgress.getVisibility() == View.GONE) {
				mProgress.startAnimation(mAnimation);
				mProgress.setVisibility(View.VISIBLE);
			}
//			state = 0;
//			mHandler.postDelayed(MyRunnable, 1500);
		}else {
			mProgress.clearAnimation();
			mProgress.setVisibility(View.GONE);
		}
    }

    /**
     * Returns the visibility status for the progress bar.
     * 
     *   or {@link View#GONE}.
     */
    public int getProgressBarVisibility() {
        return mProgress.getVisibility();
    }

    /**
     * Function to set a click listener for Title TextView
     * 
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.actionbar_btn_search) {//搜索按钮
            if (listenerBackSuper != null) {
                listenerBackSuper.onListener(R.id.actionbar_btn_search, mEtSearch.getText().toString(), true);
            }
        } else if (id == R.id.actionbar_iv_search) {//搜索图标按钮
            if (listenerBackSuper != null) {
                listenerBackSuper.onListener(R.id.actionbar_iv_search, mEtSearch.getText().toString(), true);
            }
        } else if (id == R.id.actionbar_home_btn) {//返回键
            if (mLayoutSearch != null && mLayoutSearch.getVisibility() == View.VISIBLE) {
                hintSearch();
                listenerBackSuper.onListener(EnumRequest.MENU_BACK.toInt(), "", false);
            } else {
                final Object tag = view.getTag();
                if (tag instanceof Action) {
                    final Action action = (Action) tag;
                    action.performAction(view);
                }
            }
        } else if (id == R.id.actionbar_title) {//标题
            if (listenerBackSuper != null) {
                listenerBackSuper.onListener(R.id.actionbar_title, null, true);
            }
        } else {
            final Object tag = view.getTag();
            if (tag instanceof Action) {
                final Action action = (Action) tag;
                action.performAction(view);
            }
        }
    }

    /**
     * Adds a list of {@link Action}s.
     * @param actionList the actions to add
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     *  描述	：添加右边菜单项
     */
    public void addAction(Action action) {
    	action.setListenerBack(listenerBackSuper);
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }
    
    public View getRightView(){
    	return mActionsView;
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * @param action the action to add
     * @param index the position at which to add the action
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mActionsView.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     * @return action count
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);
        ImageButton labelView =
                (ImageButton) view.findViewById(R.id.actionbar_item);
        if (action.getDrawable() != -1) {
        	labelView.setVisibility(View.VISIBLE);
                labelView.setImageResource(action.getDrawable());
		}else {
			labelView.setVisibility(View.GONE);
		}
        
        TextView tv = (TextView)view.findViewById(R.id.item_tv_content);
        if (action.getContent().length() <= 0) {
        	labelView.setTag(action);
        	labelView.setOnClickListener(this);
        	tv.setVisibility(View.GONE);
		}else {
			tv.setTag(action);
			tv.setOnClickListener(this);
			tv.setVisibility(View.VISIBLE);
			if (tvRightColro != -1) {
				tv.setTextColor(tvRightColro);
			}
			tv.setText(action.getContent());
		}
        //分割线
        TextView tvSpX = (TextView) view.findViewById(R.id.item_tv_x);
        tvSpX.setVisibility(isMenuSpilx);
        
        return view;
    }

    /**
     * 设置右边文字颜色
     */
    public void setRightColorTv(int color){
    	this.tvRightColro = color;
    }
    
    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    @SuppressWarnings("serial")
	public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        public int getDrawable();
        public void performAction(View view);
        public String getContent();
        public void setListenerBack(ListenerBack listener);
    }

    public static abstract class AbstractAction implements Action {
        final private int mDrawable;
        final private String mContent;

        public AbstractAction(int drawable,String content) {
            mDrawable = drawable;
            mContent = content;
        }

		@Override
        public int getDrawable() {
            return mDrawable;
        }
        
        @Override
        public String getContent() {
        	// TODO Auto-generated method stub
        	return mContent;
        }
    }

    public static class IntentAction extends AbstractAction {
        private Context mContext;
        private Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable,String content) {
            super(drawable,content);
            mContext = context;
            mIntent = intent;
        }

        @Override
        public void performAction(View view) {
            try {
               mContext.startActivity(mIntent); 
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext,
                        "错误",
                        Toast.LENGTH_SHORT).show();
            }
        }

		@Override
		public void setListenerBack(ListenerBack listener) {
			
		}
    }
    
    public static class MenuAction extends AbstractAction {
        private int mType;
        private ListenerBack mListenerBack;
        /**
         * @param drawable 显示图标  不显示图标则为-1
         * @param type 类型
         * @param content 文字
         */
        public MenuAction( int drawable,int type,String content) {
            super(drawable,content);
            mType = type;
        }

        @Override
        public void performAction(View view) {
           if (view != null) {
        	   if (mListenerBack != null) {
        		   mListenerBack.onListener(mType, view, true);
        	   }
           }
        }

		@Override
		public void setListenerBack(ListenerBack listener) {
			mListenerBack = listener;
		}
    }
}
