package cn.nokia.speedtest5g.app.thread;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TimeZone;

import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.uitl.MyToSpile;
import cn.nokia.speedtest5g.app.uitl.PathUtil;

import android.content.Intent;
import android.os.Handler;

/**
 * 工参数据下载更新时间查询
 * @author zwq
 *
 */
public class JzgcDataUpdateRunnable extends BaseRunnable {

	public JzgcDataUpdateRunnable(Handler handler, int what) {
		super(handler, what);
	}

	@Override
	public void run() {
		super.run();
		//获取权限表
		HashMap<String, String> toCode = MyToSpile.getInstances().getToCode(1);
		if (toCode != null && toCode.size() > 0) {
			String path = PathUtil.getInstances().getCurrentPath() + PathUtil.getInstances().getDbPath() + "/";
			File f = new File(path);
			//目录是否存在,若不存在就创建
			if (f != null && f.exists()) {
				long updateNewTime = 0;
				Iterator<Entry<String, String>> iter = toCode.entrySet().iterator();
				//遍历权限表并获取本地已下载的文件及时间
				while(iter.hasNext()){
					//判断本地文件是否存在，如果存在则取时间
					f = new File(String.format("%s%s%s", path,iter.next().getKey(),".cif"));
					if (f != null && f.exists() && f.isFile()) {
						long lastModified = f.lastModified();
						if (lastModified > updateNewTime) {
							updateNewTime = lastModified;
						}
					}
				}
				if (updateNewTime != 0) {
					//获取最新更新当天0时时间戳
					updateNewTime = updateNewTime - (updateNewTime + TimeZone.getDefault().getRawOffset())%(1000*3600*24);
					int day = (int) ((System.currentTimeMillis() - updateNewTime)/(1000*3600*24));
					Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
					intent.putExtra("type", EnumRequest.OTHER_MAINHOME_TYPE_PERSONAL.toInt());
					intent.putExtra("updateDay", day);
					SpeedTest5g.getContext().sendBroadcast(intent);
					return;
				}
			}
			//代表未下载
			Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
			intent.putExtra("type", EnumRequest.OTHER_MAINHOME_TYPE_PERSONAL.toInt());
			intent.putExtra("updateDay", Integer.MAX_VALUE);
			SpeedTest5g.getContext().sendBroadcast(intent);
		}
	}
}
