package cn.nokia.speedtest5g.app.uitl;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;

/**
 * 动画操作类
 * 
 * @author xujianjun
 *
 */
public class AnimationUtil {

	private static AnimationUtil aUtil = null;

	public synchronized static AnimationUtil getInstances() {
		if (aUtil == null) {
			aUtil = new AnimationUtil();
		}
		return aUtil;
	}

	/**
	 * 设置图片闪烁
	 */
	public static void flicker(View iv) {
		// 闪烁
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
		alphaAnimation.setDuration(1000);
		alphaAnimation.setRepeatCount(Animation.INFINITE);// 表示重复多次。也可以设定具体重复的次数
		alphaAnimation.setRepeatMode(Animation.REVERSE);// 表示动画结束后，反过来再执行
		// iv.setAnimation(alphaAnimation1);
		// alphaAnimation1.startNow();
		iv.startAnimation(alphaAnimation);
	}

	// 底部菜单
	private Animation mAnimationInput, mAnimationOut;

	public void ivAnimation(boolean isInput, View v) {
		if (mAnimationInput == null && isInput) {
			mAnimationInput = AnimationUtils.loadAnimation(SpeedTest5g.getContext(), R.anim.popup_bottom_input);
			LinearInterpolator lin = new LinearInterpolator();
			mAnimationInput.setInterpolator(lin);
		} else if (mAnimationOut == null) {
			mAnimationOut = AnimationUtils.loadAnimation(SpeedTest5g.getContext(), R.anim.popup_bottom_out);
			LinearInterpolator lin = new LinearInterpolator();
			mAnimationOut.setInterpolator(lin);
		}
		v.clearAnimation();
		if (isInput) {
			v.startAnimation(mAnimationInput);
		} else {
			v.startAnimation(mAnimationOut);
		}
		v.setVisibility(isInput ? View.VISIBLE : View.GONE);
	}

	private Animation mAnimationInputTop;

	/**
	 * 由上往下动画打开
	 * 
	 * @param v
	 */
	public void startTopAnim(View v) {
		if (mAnimationInputTop == null) {
			mAnimationInputTop = AnimationUtils.loadAnimation(SpeedTest5g.getContext(), R.anim.popup_title_input);
			LinearInterpolator lin = new LinearInterpolator();
			mAnimationInputTop.setInterpolator(lin);
		}
		v.clearAnimation();
		v.setAnimation(mAnimationInputTop);
		mAnimationInputTop.start();
		v.setVisibility(View.VISIBLE);
	}

	/**
	 * 左边显示右边隐藏
	 * 
	 * @param v
	 * @param isShow
	 */
	public void leftEnterRightExit(Activity activity, View v, boolean isShow) {
//		 if (mAnimationLeftEnter == null && isShow) {
//		 mAnimationLeftEnter =
//		 AnimationUtils.loadAnimation(MyApplication.getContext(),
//		 R.anim.popup_right_enter);
//		 LinearInterpolator lin = new LinearInterpolator();
//		 mAnimationLeftEnter.setInterpolator(lin);
//		 } else if (mAnimationRightExit == null) {
//		 mAnimationRightExit =
//		 AnimationUtils.loadAnimation(MyApplication.getContext(),
//		 R.anim.popup_left_exit);
//		 LinearInterpolator lin = new LinearInterpolator();
//		 mAnimationRightExit.setInterpolator(lin);
//		 }
//		 v.clearAnimation();
//		 if (isShow) {
//		 v.startAnimation(mAnimationLeftEnter);
//		 } else {
//		 v.startAnimation(mAnimationRightExit);
//		 }

		if (isShow) {
			// 向右边移入
			v.setAnimation(AnimationUtils.makeInAnimation(activity, true));
		} else {
			// 向左边移出
			v.setAnimation(AnimationUtils.makeOutAnimation(activity, false));
		}
		v.setVisibility(isShow ? View.VISIBLE : View.GONE);

	}
	
	/**
	 * 左边显示右边隐藏
	 * 
	 * @param v
	 * @param isShow
	 */
	public void leftEnterRightExitDelay500(Activity activity, View v, boolean isShow) {
		if (isShow) {
			// 向右边移入
//			v.setAnimation(AnimationUtils.makeInAnimation(activity, true));
			v.startAnimation(AnimationUtils.loadAnimation(SpeedTest5g.getContext(),
					 R.anim.popup_right_enter));
		} else {
			// 向左边移出
			v.setAnimation(AnimationUtils.makeOutAnimation(activity, false));
		}
		v.setVisibility(isShow ? View.VISIBLE : View.GONE);

	}

	// 底部菜单
	private Animation mAnimationBottomEnter, mAnimationTopExit;

	/**
	 * 底部显示上面隐藏
	 * 
	 * @param v
	 * @param isShow
	 */
	public void buttomEnterTopExit(Activity activity, View v, boolean isShow) {
		if (mAnimationBottomEnter == null && isShow) {
			mAnimationBottomEnter = AnimationUtils.loadAnimation(SpeedTest5g.getContext(), R.anim.popup_bottom_enter);
			LinearInterpolator lin = new LinearInterpolator();
			mAnimationBottomEnter.setInterpolator(lin);
		} else if (mAnimationTopExit == null) {
			mAnimationTopExit = AnimationUtils.loadAnimation(SpeedTest5g.getContext(), R.anim.popup_top_exit);
			LinearInterpolator lin = new LinearInterpolator();
			mAnimationTopExit.setInterpolator(lin);
		}
		v.clearAnimation();
		if (isShow) {
			v.startAnimation(mAnimationBottomEnter);
		} else {
			v.startAnimation(mAnimationTopExit);
		}

	}
	
	/**
	 * 隐藏 从顶部隐藏
	 */
	public void upHideLoading(View view) {

		TranslateAnimation mShowAction = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, -1.0f);
		mShowAction.setDuration(500);
		mShowAction.setFillAfter(true);
		view.startAnimation(mShowAction);

		// ivLoading.clearAnimation();

	}
	
	/**
	 * 宽度收缩
	 */
	public void widthScaleAnimEnd(final View view1,final View view2){
		Animation loadAnimation = AnimationUtils.loadAnimation(SpeedTest5g.getContext(), R.anim.anim_width_scale);
		loadAnimation.setInterpolator(new LinearInterpolator());
		loadAnimation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view1.setVisibility(View.GONE);
			}
		});
		view1.clearAnimation();
		view1.startAnimation(loadAnimation);
		widthScaleAnimStart(view2);
	}
	
	/**
	 * 透明度0-1
	 * @param v
	 */
	public void widthScaleAnimStart(View v){
		v.setVisibility(View.VISIBLE);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        //设置动画持续时长
        alphaAnimation.setDuration(1500);
        //设置动画结束之后的状态是否是动画的最终状态，true，表示是保持动画结束时的最终状态
        alphaAnimation.setFillAfter(true);
        v.clearAnimation();
		v.startAnimation(alphaAnimation);
	}
}
