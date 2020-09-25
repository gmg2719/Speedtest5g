package cn.nokia.speedtest5g.speedtest.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.android.volley.util.SharedPreHandler;
import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.adapter.CommonListAdapter;
import cn.nokia.speedtest5g.app.adapter.ViewHolder;
import cn.nokia.speedtest5g.app.bean.Db_JJ_FTPTestInfo;
import cn.nokia.speedtest5g.app.uitl.UtilHandler;
import cn.nokia.speedtest5g.util.Base64Utils;
import cn.nokia.speedtest5g.util.UtilChinese;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 速率PK组员列表适配器
 * @author JQJ
 *
 */
public class SpeedPkGroupMemberAdapter extends CommonListAdapter<Db_JJ_FTPTestInfo> {
	private Context context;

	public SpeedPkGroupMemberAdapter(Context context, List<Db_JJ_FTPTestInfo> list) {
		super(context, list);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = ViewHolder.getInstance(mContext, convertView, R.layout.jqj_speed_test_pk_group_member_item_adapter);
		initConvertView(viewHolder, position);
		return viewHolder.getConvertView();
	}

	private void initConvertView(ViewHolder viewHolder, int position) {
		final Db_JJ_FTPTestInfo item = (Db_JJ_FTPTestInfo) getItem(position);
		TextView tv_speed_pk_group_member_photo = viewHolder.findViewById(R.id.tv_speed_pk_group_member_photo);
		TextView tv_speed_pk_group_member_user_name = viewHolder.findViewById(R.id.tv_speed_pk_group_member_user_name);

		String loginName = Base64Utils.decryptorDes3(SharedPreHandler.getShared(context).getStringShared(TypeKey.getInstance().LOGIN_NAME(), ""));
		if (loginName.equals(item.loginName)) {
			tv_speed_pk_group_member_photo.setText("我");
		} else {
			String userName = item.userName;
			if (!TextUtils.isEmpty(userName)) {
				for (int i = userName.length() - 1; i >= 0; i--) {
					String strChat = userName.substring(i,i + 1);
					if (UtilChinese.getInstance().isChinese(strChat)) {
						userName = strChat;
						break;
					}
				}
			}
			tv_speed_pk_group_member_photo.setText(TextUtils.isEmpty(userName) ?  "" : userName.substring(userName.length() - 1));
		}
		//手机号格式处理
		String strName = item.loginName;
		if(!TextUtils.isEmpty(strName)){
			if(isPhone(strName)){
				strName = UtilHandler.getInstance().hidePhoneNoMid4(strName);
			}
		}
		tv_speed_pk_group_member_user_name.setText(strName);
	}

	private boolean isPhone(String phone) {
		String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
		if (phone.length() != 11) {
			return false;
		} else {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(phone);
			return m.matches();
		}
	}
}
