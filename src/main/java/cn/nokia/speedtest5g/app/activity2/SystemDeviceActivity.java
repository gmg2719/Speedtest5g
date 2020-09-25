package cn.nokia.speedtest5g.app.activity2;

import cn.nokia.speedtest5g.R;

import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.app.uitl.SystemUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

/**
 * 设备信息
 * @author JQJ
 *
 */
public class SystemDeviceActivity extends BaseActionBarActivity {

	private TextView tvSystemDeviceName;
	private TextView tvSystemDeviceModel;
	private TextView tvSystemDeviceSystemVersion;
	private TextView tvSystemDeviceSystemRoot;

	private TextView tvSystemDeviceCamPhoneNum;
	private TextView tvSystemDeviceCamImei1;
	private TextView tvSystemDeviceCamCarrier1;
	private TextView tvSystemDeviceCamImsi1;
	private TextView tvSystemDeviceCamIccid1;

	//	private TextView tvSystemDeviceImei;
	//	private TextView tvSystemDeviceSystemCpu;
	private TextView tvSystemDeviceSystemRam;
	private TextView tvSystemDeviceSystemStorage;
	private TextView tvSystemDeviceSystemDpi;
	private TextView tvSystemDeviceBatteryType;
	private TextView tvSystemDeviceBatteryCapacity;
	private TextView tvSystemDeviceBatteryVk;
	private TextView tvSystemDeviceBatteryTemp;
	private TextView tvSystemDeviceBatteryState;
	private TextView tvSystemDeviceBatteryBattery;
	private TextView tvSystemDeviceBatteryPercent;
	private TextView tvSystemDeviceBatteryHealth;

	private Activity mActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_system_device_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;

		mActivity = this;
		init("系统信息", true);

		initView();
		// 注册广播
		registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		setData();
	}

	private void initView() {
		tvSystemDeviceName = (TextView) findViewById(R.id.tv_system_device_name);
		tvSystemDeviceModel = (TextView) findViewById(R.id.tv_system_device_model);
		tvSystemDeviceSystemVersion = (TextView) findViewById(R.id.tv_system_device_system_version);
		tvSystemDeviceSystemRoot = (TextView) findViewById(R.id.tv_system_device_system_root);

		tvSystemDeviceCamPhoneNum = (TextView) findViewById(R.id.tv_system_device_cam_phone_num);
		tvSystemDeviceCamImei1 = (TextView) findViewById(R.id.tv_system_device_cam_imei1);
		tvSystemDeviceCamCarrier1 = (TextView) findViewById(R.id.tv_system_device_cam_carrier1);
		tvSystemDeviceCamImsi1 = (TextView) findViewById(R.id.tv_system_device_cam_imsi1);
		tvSystemDeviceCamIccid1 = (TextView) findViewById(R.id.tv_system_device_cam_iccid1);

		//		tvSystemDeviceImei = (TextView) findViewById(R.id.tv_system_device_imei);
		//		tvSystemDeviceSystemCpu = (TextView) findViewById(R.id.tv_system_device_system_cpu);
		tvSystemDeviceSystemRam = (TextView) findViewById(R.id.tv_system_device_system_ram);
		tvSystemDeviceSystemStorage = (TextView) findViewById(R.id.tv_system_device_system_storage);
		tvSystemDeviceSystemDpi = (TextView) findViewById(R.id.tv_system_device_system_dpi);
		tvSystemDeviceBatteryType = (TextView) findViewById(R.id.tv_system_device_battery_type);
		tvSystemDeviceBatteryCapacity = (TextView) findViewById(R.id.tv_system_device_battery_capacity);
		tvSystemDeviceBatteryVk = (TextView) findViewById(R.id.tv_system_device_battery_vk);
		tvSystemDeviceBatteryTemp = (TextView) findViewById(R.id.tv_system_device_battery_temp);
		tvSystemDeviceBatteryState = (TextView) findViewById(R.id.tv_system_device_battery_state);
		tvSystemDeviceBatteryBattery = (TextView) findViewById(R.id.tv_system_device_battery_battery);
		tvSystemDeviceBatteryPercent = (TextView) findViewById(R.id.tv_system_device_battery_percent);
		tvSystemDeviceBatteryHealth = (TextView) findViewById(R.id.tv_system_device_battery_health);
	}

	private void setData() {
		tvSystemDeviceName.setText("系统");
		tvSystemDeviceModel.setText(SystemUtil.getInstance().getSystemModel());
		tvSystemDeviceSystemVersion.setText(SystemUtil.getInstance().getSystemVersion());
		tvSystemDeviceSystemRoot.setText(SystemUtil.getInstance().isRootSystem()?"是":"否");

		tvSystemDeviceCamPhoneNum.setText(SystemUtil.getInstance().getPhoneNum(mActivity));
		tvSystemDeviceCamImei1.setText(SystemUtil.getInstance().getIMEI(mActivity));
		tvSystemDeviceCamCarrier1.setText(SystemUtil.getInstance().getISP(mActivity));
		tvSystemDeviceCamImsi1.setText(SystemUtil.getInstance().getIMSI(mActivity));
		tvSystemDeviceCamIccid1.setText(SystemUtil.getInstance().getIccid(mActivity));

		//		tvSystemDeviceImei.setText(SystemUtil.getInstance().getIMEI(mActivity));
		//		tvSystemDeviceSystemCpu.setText(SystemUtil.getInstance().getCpuInfo()[0]);

		tvSystemDeviceSystemRam.setText(Formatter.formatFileSize(mActivity, SystemUtil.getInstance().getMemoryUsed(mActivity)) + "/" + Formatter.formatFileSize(mActivity,SystemUtil.getInstance().getMemoryTotal(mActivity)));
		tvSystemDeviceSystemStorage.setText(Formatter.formatFileSize(mActivity, SystemUtil.getInstance().getStorageInfo(mActivity, 3)) + "/" + 
				Formatter.formatFileSize(mActivity, SystemUtil.getInstance().getStorageInfo(mActivity, 1)));
		tvSystemDeviceSystemDpi.setText(SystemUtil.getInstance().getScreenInfo(mActivity, 2));
		tvSystemDeviceBatteryCapacity.setText(SystemUtil.getInstance().getBatteryCapacity(mActivity));

		//		tvSystemDeviceBatteryType.setText("");
		//		tvSystemDeviceBatteryCapacity.setText("");
		//		tvSystemDeviceBatteryVk.setText("");
		//		tvSystemDeviceBatteryTemp.setText("");
		//		tvSystemDeviceBatteryState.setText("");
		//		tvSystemDeviceBatteryBattery.setText("");
		//		tvSystemDeviceBatteryPercent.setText("");
		//		tvSystemDeviceBatteryHealth.setText("");
	}

	@Override
	public void onDestroy() {
		try {
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
		}
		super.onDestroy();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 当前电量
			int currLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			// 总电量
			int total = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
			// 剩余电量
			int percent = currLevel * 100 / total;
			// 电池类型
			String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
			// 温度
			int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
			// 电压
			int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
			// 状态
			int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			// 连接的电源插座
			int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			// 健康
			int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);

			tvSystemDeviceBatteryType.setText(technology);
			tvSystemDeviceBatteryVk.setText(voltage / 1000.0 + "V");
			tvSystemDeviceBatteryTemp.setText(temperature / 10.0 + "℃");

			String batteryState = SystemUtil.getInstance().converBatteryStatus(status);
			tvSystemDeviceBatteryState.setText(batteryState);

			String batteryPlugged = SystemUtil.getInstance().converBatteryPlugged(plugged);
			tvSystemDeviceBatteryBattery.setText(batteryPlugged + "");

			tvSystemDeviceBatteryPercent.setText(percent + "%");

			String batteryHealth = SystemUtil.getInstance().converBatteryHealth(health);
			tvSystemDeviceBatteryHealth.setText(batteryHealth + "");
		}
	};
}
