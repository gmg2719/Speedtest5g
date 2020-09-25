package cn.nokia.speedtest5g.app.adapter;

import java.util.List;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.gridview.util.Item;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import cn.nokia.speedtest5g.app.uitl.ImageOptionsUtil;
import cn.nokia.speedtest5g.app.uitl.ModeClickHandler;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 添加控件页面适配器
 * 
 * @author Administrator
 *
 */
public class FunctionAdapter extends CommonListAdapter<Item> {
	private int hidePosition = AdapterView.INVALID_POSITION;

	public FunctionAdapter(Context context) {
		super(context);
	}

	public ListenerBack listenerBack;

	public FunctionAdapter(Context context, ListenerBack listenerBack) {
		this(context);
		this.listenerBack = listenerBack;
	}

	public void setData(List<Item> list) {
		updateData(list);
	}

	/**
	 * 更新某一项数据
	 * 
	 * @param item
	 */
	public void updateItem(Item item) {
		if (item != null && getCount() > 0) {
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).menuCode.equals(item.menuCode)) {
					mList.remove(i);
					mList.add(i, item);
					notifyDataSetChanged();
					return;
				}
			}
		}
	}

	public String getMyAppsMenuCodes() {
		String menuCodes = "";
		if (mList == null || mList.size() <= 0) {
			return menuCodes;
		}
		for (int i = 0; i < mList.size(); i++) {
			menuCodes = menuCodes + mList.get(i).menuCode + (i == mList.size() - 1 ? "" : ",");
		}
		return menuCodes;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = ViewHolder.getInstance(mContext, convertView, R.layout.adapter_toolall);
		initConvertView(vh, position);

		return vh.getConvertView();
	}

	private void initConvertView(ViewHolder vh, int position) {
		ImageView ivAppIcon = (ImageView) vh.findViewById(R.id.toolall_top_iv);
		TextView tvAppName = (TextView) vh.findViewById(R.id.toolall_top_tv);
		TextView tvNumber = (TextView) vh.findViewById(R.id.toolall_top_tv_number);
		ImageView ivMarkRed = (ImageView) vh.findViewById(R.id.toolall_top_iv_make_red);
		ImageView mIvRightTop = (ImageView) vh.findViewById(R.id.toolall_top_iv_right);
		ImageView ivEditStstus = (ImageView) vh.findViewById(R.id.toolall_top_cb_edit_status);
		final Item info = (Item) getItem(position);
		if (!TextUtils.isEmpty(info.drawableUrl)) {
			ImageLoader.getInstance().displayImage(info.drawableUrl, ivAppIcon, ImageOptionsUtil.getInstances().getIconOption());
		}else {
			ivAppIcon.setImageResource(info.getDrawable());
		}
		if (TextUtils.isEmpty(info.getName())) {
			tvAppName.setVisibility(View.GONE);
		} else {
			tvAppName.setVisibility(View.VISIBLE);
			tvAppName.setText(info.getName());
			tvAppName.setTextColor(info.getColorTv());
		}
		// 如果选中状态===針對網友自動化功能
		if (info.isSelect()) {
			vh.getConvertView().setBackgroundColor(Color.parseColor("#F3F6FB"));
		} else {
			vh.getConvertView().setBackgroundColor(info.getColorBg());
		}
		if (info.getNumber() > 0) {
			if (info.getNumber() > 99) {
				tvNumber.setText("99");
			} else {
				tvNumber.setText(String.valueOf(info.getNumber()));
			}
			tvNumber.setVisibility(View.VISIBLE);
		} else {
			tvNumber.setVisibility(View.GONE);
		}
		if (info.isMarkRed()) {
			ivMarkRed.setVisibility(View.VISIBLE);
		} else {
			ivMarkRed.setVisibility(View.GONE);
		}
		if (info.rightTopIcon == -1) {
			mIvRightTop.setImageResource(R.drawable.transparent);
		} else {
			mIvRightTop.setImageResource(info.rightTopIcon);
		}
		if (info.isEditStatus) {
			ivEditStstus.setVisibility(View.VISIBLE);
			if (info.isMyApp) {
				ivEditStstus.setImageResource(R.drawable.icon_remove);
			} else {
				ivEditStstus.setImageResource(R.drawable.icon_add);
			}
			ivEditStstus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (getCount() <= 7 && info.isMyApp) {
						((BaseActionBarActivity) mContext).showCommon("自定义模块过少");
						return;
					}
					if (!info.isMyApp) {
						List<Item> myAppList = ModeClickHandler.getInstances((BaseActionBarActivity) mContext).getMyApps(false);
						if (myAppList.size() >= 15) {
							((BaseActionBarActivity) mContext).showCommon("当前自定义应用已满");
							return;
						}
					}
					info.isMyApp = !info.isMyApp;
					if (listenerBack != null) {
						listenerBack.onListener(v.getId(), info, true);
					}
				}
			});
			// 拖动项隐藏
			if (position == hidePosition) {
				ivEditStstus.setVisibility(View.GONE);
				ivEditStstus.setImageResource(R.drawable.transparent);
				ivAppIcon.setImageResource(R.drawable.transparent);
				tvAppName.setText("");
			}
		} else {
			ivEditStstus.setVisibility(View.GONE);
			ivEditStstus.setImageResource(R.drawable.transparent);
		}
	}

	public void hideView(int pos) {
		hidePosition = pos;
		notifyDataSetChanged();
	}

	public void showHideView() {
		hidePosition = AdapterView.INVALID_POSITION;
		notifyDataSetChanged();
	}

	public void removeView(int pos) {
		mList.remove(pos);
		notifyDataSetChanged();
	}

	// 更新拖动时的gridView
	public void swapView(int draggedPos, int destPos) {
		// 从前向后拖动，其他item依次前移
		if (draggedPos < destPos) {
			mList.add(destPos + 1, (Item) getItem(draggedPos));
			mList.remove(draggedPos);
		}
		// 从后向前拖动，其他item依次后移
		else if (draggedPos > destPos) {
			mList.add(destPos, (Item) getItem(draggedPos));
			mList.remove(draggedPos + 1);
		}
		hidePosition = destPos;
		notifyDataSetChanged();
	}
}
