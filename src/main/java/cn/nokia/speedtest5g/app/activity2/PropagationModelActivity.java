package cn.nokia.speedtest5g.app.activity2;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.activity.BaseActionBarActivity;
import cn.nokia.speedtest5g.dialog.WheelOtherStrPopuo;

/**
 * LTE自由传播模型估算
 * @author xiongzk
 *
 */
public class PropagationModelActivity extends BaseActionBarActivity implements OnClickListener {
	
	//设备类型
	private String[] arr_equ_type = SpeedTest5g.getContext().getResources().getStringArray(R.array.equipment_type);
	//类型对应数据
	private String[] arr_equ_data = SpeedTest5g.getContext().getResources().getStringArray(R.array.equipment_data);
	//类型对应模数
	private String[] arr_mod_num = {"1/2", "1/2/3", "1/2", "1/2", "1/2", "1/2", "1/2", "1/2", "1", "1/2"};
	//频段
	private String[] arr_band = {"900", "1900", "2300", "2600"};
	//距离
	private String[] arr_dis = {"1", "10", "20", "30", "40", "50", "100", "150", "200", "250", "300", "350", "400", "450", "500", "550", "600", "650", "700", "750", "800", "850", "900", "950", "1000"};
	//墙
	private String[] arr_wall = {"0", "1", "2", "3", "4"};
	//墙损耗值
	private String[] arr_wall_data = {"0", "20", "40", "50", "60"};
	
	private Button btnEquType;
	//设备功率、典型天线增益、LTE CRS功率、天线口输出LTE功率、模数
	private TextView tvEquWate,tvTpAnteGain,tvLteCrsPower,tvOutLtePower,tvModNum;
	//频段、距离、塔高、墙
	private Button btnBand,btnDist,btnNumWall;
	
	private TextView tvPropagationLoss;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.jqj_lteestimate_tool_activity);
		mBgTopColor = R.color.bg_color;
		mBgParentColor = R.color.bg_color;
		mTitleTextColor = R.color.gray_c0c0c3;
		
		initView();
		initViewText();
		
		init("LTE自由传播模型估算",true,true);
	}
	
	//初始化控件
	private void initView() {
		//----------------------------LTE设备
		tvEquWate = (TextView) findViewById(R.id.equ_wate);
		tvTpAnteGain = (TextView) findViewById(R.id.typical_antenna_gain);
		tvLteCrsPower = (TextView) findViewById(R.id.lte_crs_power);
		tvOutLtePower = (TextView) findViewById(R.id.output_lte_power);
		tvModNum = (TextView) findViewById(R.id.model_num);
		
		btnEquType = (Button) findViewById(R.id.equ_type);
		btnEquType.setOnClickListener(this);
		
		//----------------------------LTE传播损耗估算
		btnBand = (Button) findViewById(R.id.lte_band);
		btnDist = (Button) findViewById(R.id.equ_dist);
		btnNumWall = (Button) findViewById(R.id.num_of_wall);
		btnBand.setOnClickListener(this);
		btnDist.setOnClickListener(this);
		btnNumWall.setOnClickListener(this);
		
		tvPropagationLoss = (TextView) findViewById(R.id.propagation_loss);
	}
	
	//初始化文字
	private void initViewText() {
		String[] data = arr_equ_data[0].split(",");
		btnEquType.setText(arr_equ_type[0]);
		tvEquWate.setText(Double.parseDouble(data[0]) + "");
		tvTpAnteGain.setText(Double.parseDouble(data[1]) + "");
		tvLteCrsPower.setText(data[2]);
		tvOutLtePower.setText(data[3]);
		tvModNum.setText(arr_mod_num[0]);
		
		btnBand.setText("900");
		btnDist.setText("50");
		btnNumWall.setText("0");
		DecimalFormat df = new DecimalFormat("#.00");
		double tv = 32.45 + 20 * (Math.log(900.0) / Math.log(10.0)) + 20 * (Math.log(50.0 / 1000.0) / Math.log(10.0));
		tvPropagationLoss.setText(df.format(tv));
	}
	
	private int wallPosition = 0;
	@Override
	public void onListener(int type, Object object, boolean isTrue) {
		super.onListener(type, object, isTrue);
		int position = 0;
		double band = 2300.0;
		double dis = 50.0;
		double wall = 0.0;
		double pl = 32.45 + 20 * (Math.log(band) / Math.log(10.0)) + 20 * (Math.log(dis / 1000.0) / Math.log(10.0)) + wall;
		DecimalFormat df = new DecimalFormat("#.00");
        if (type == R.id.equ_type) {
            position = Integer.parseInt(object.toString());
            String[] data = arr_equ_data[position].split(",");
            btnEquType.setText(arr_equ_type[position]);
            tvEquWate.setText(Double.parseDouble(data[0]) + "");
            tvTpAnteGain.setText(Double.parseDouble(data[1]) + "");
            tvLteCrsPower.setText(data[2]);
            tvOutLtePower.setText(data[3]);
            tvModNum.setText(arr_mod_num[position]);
        } else if (type == R.id.lte_band) {
            position = Integer.parseInt(object.toString());
            btnBand.setText(arr_band[position]);

            band = Double.parseDouble(btnBand.getText().toString());
            dis = Double.parseDouble(btnDist.getText().toString());
            wall = Double.parseDouble(arr_wall_data[wallPosition]);
            pl = 32.45 + 20 * (Math.log(band) / Math.log(10.0)) + 20 * (Math.log(dis / 1000.0) / Math.log(10.0)) + wall;
            tvPropagationLoss.setText(df.format(pl));
        } else if (type == R.id.equ_dist) {
            position = Integer.parseInt(object.toString());
            btnDist.setText(arr_dis[position]);

            band = Double.parseDouble(btnBand.getText().toString());
            dis = Double.parseDouble(btnDist.getText().toString());
            wall = Double.parseDouble(arr_wall_data[wallPosition]);
            pl = 32.45 + 20 * (Math.log(band) / Math.log(10.0)) + 20 * (Math.log(dis / 1000.0) / Math.log(10.0)) + wall;
            tvPropagationLoss.setText(df.format(pl));
        } else if (type == R.id.num_of_wall) {
            position = Integer.parseInt(object.toString());
            wallPosition = position;
            btnNumWall.setText(arr_wall[position]);

            band = Double.parseDouble(btnBand.getText().toString());
            dis = Double.parseDouble(btnDist.getText().toString());
            wall = Double.parseDouble(arr_wall_data[wallPosition]);
            pl = 32.45 + 20 * (Math.log(band) / Math.log(10.0)) + 20 * (Math.log(dis / 1000.0) / Math.log(10.0)) + wall;
            tvPropagationLoss.setText(df.format(pl));
        }
	}
	
	@Override
	public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.equ_type) {
            WheelOtherStrPopuo.getInstances().show(this, v, arr_equ_type, "设备类型", v.getId(), this, false).isPostion();
        } else if (id == R.id.lte_band) {
            WheelOtherStrPopuo.getInstances().show(this, v, arr_band, "频段", v.getId(), this, false).isPostion();
        } else if (id == R.id.equ_dist) {
            WheelOtherStrPopuo.getInstances().show(this, v, arr_dis, "距离", v.getId(), this, false).isPostion();
        } else if (id == R.id.num_of_wall) {
            WheelOtherStrPopuo.getInstances().show(this, v, arr_wall, "墙", v.getId(), this, false).isPostion();
        }
	}
}
