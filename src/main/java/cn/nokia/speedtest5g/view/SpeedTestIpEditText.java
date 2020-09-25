package cn.nokia.speedtest5g.view;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.interfaces.ListenerBack;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * ip输入框  输满自动跳到下一个 或按.跳到下一个
 * @author JQJ
 *
 */
public class SpeedTestIpEditText extends LinearLayout {

	private EditText firIPEdit = null;
	private EditText secIPEdit = null;
	private EditText thirIPEdit = null;
	private EditText fourIPEdit = null;

	private String firstIP = "";
	private String secondIP = "";
	private String thirdIP = "";
	private String fourthIP = "";

	private ListenerBack mListenerBack = null;

	public SpeedTestIpEditText(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		LayoutInflater.from(context).inflate(R.layout.jqj_speed_test_ip_edittext_layout, this);

		firIPEdit = (EditText) findViewById(R.id.speed_test_et_First_Text);
		secIPEdit = (EditText) findViewById(R.id.speed_test_et_Second_Text);
		thirIPEdit = (EditText) findViewById(R.id.speed_test_et_Third_Text);
		fourIPEdit = (EditText) findViewById(R.id.speed_test_et_Four_Text);

		setIPEditTextListener(context);

		setIPEditTextDelete();
	}

	public void setListenerBack(ListenerBack listenerBack){
		mListenerBack = listenerBack;
	}

	private void setIPEditTextDelete(){
		secIPEdit.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction()== KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
					if(secIPEdit.isFocused()){
						String content = secIPEdit.getText().toString();
						if(TextUtils.isEmpty(content)){
							firIPEdit.setFocusable(true);
							firIPEdit.requestFocus();
						}
					}
				}
				return false;
			}
		});

		thirIPEdit.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction()== KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
					if(thirIPEdit.isFocused()){
						String content = thirIPEdit.getText().toString();
						if(TextUtils.isEmpty(content)){
							secIPEdit.setFocusable(true);
							secIPEdit.requestFocus();
						}
					}
				}
				return false;
			}
		});

		fourIPEdit.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction()== KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
					if(fourIPEdit.isFocused()){
						String content = fourIPEdit.getText().toString();
						if(TextUtils.isEmpty(content)){
							thirIPEdit.setFocusable(true);
							thirIPEdit.requestFocus();
						}
					}
				}
				return false;
			}
		});
	}

	private void setIPEditTextListener(final Context context) {
		//设置第一个IP字段的事件监听
		firIPEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().trim().contains(".")){
					if(s.toString().trim().contains(".")){
						firstIP = s.toString().trim().substring(0, s.length() - 1);
					}
					secIPEdit.setFocusable(true);
					secIPEdit.requestFocus();
				}else if(s.length() > 1){
					firstIP = s.toString().trim();
					if(Integer.parseInt(firstIP) > 255){
						firstIP = s.toString().trim().substring(0, s.length() - 1);
					}else if(Integer.parseInt(firstIP) > 25){
						secIPEdit.setFocusable(true);
						secIPEdit.requestFocus();
					}
				}else {
					firstIP = s.toString().trim();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				firIPEdit.removeTextChangedListener(this);
				firIPEdit.setText(firstIP);
				firIPEdit.setSelection(firIPEdit.length());
				firIPEdit.addTextChangedListener(this);
				isFill();
			}
		});

		//设置第二个IP字段的事件监听
		secIPEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().trim().contains(".")){
					secondIP = s.toString().trim().substring(0, s.length() - 1);
					thirIPEdit.setFocusable(true);
					thirIPEdit.requestFocus();
				}else if(s.length() > 1){
					secondIP = s.toString().trim();
					if(Integer.parseInt(secondIP) > 255){
						secondIP = s.toString().trim().substring(0, s.length() - 1);
					}else if(Integer.parseInt(secondIP) > 25){
						thirIPEdit.setFocusable(true);
						thirIPEdit.requestFocus();
					}
				}else {
					secondIP = s.toString().trim();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				secIPEdit.removeTextChangedListener(this);
				secIPEdit.setText(secondIP);
				secIPEdit.setSelection(secondIP.length());
				secIPEdit.addTextChangedListener(this);
				isFill();
			}
		});

		//设置第三个IP字段的事件监听
		thirIPEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().trim().contains(".")){
					thirdIP = s.toString().trim().substring(0, s.length() - 1);
					fourIPEdit.setFocusable(true);
					fourIPEdit.requestFocus();
				}else if(s.length() > 1){
					thirdIP = s.toString().trim();
					if(Integer.parseInt(thirdIP) > 255){
						thirdIP = s.toString().trim().substring(0, s.length() - 1);
					}else if(Integer.parseInt(thirdIP) > 25){
						fourIPEdit.setFocusable(true);
						fourIPEdit.requestFocus();
					}
				}else {
					thirdIP = s.toString().trim();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				thirIPEdit.removeTextChangedListener(this);
				thirIPEdit.setText(thirdIP);
				thirIPEdit.setSelection(thirdIP.length());
				thirIPEdit.addTextChangedListener(this);
				isFill();
			}
		});

		//设置第四个IP字段的事件监听
		fourIPEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.toString().trim().contains(".")){
					fourthIP = s.toString().trim().substring(0, s.length() - 1);
				}else if(s.length() > 1){
					fourthIP = s.toString().trim();
					if(Integer.parseInt(fourthIP) > 255){
						fourthIP = s.toString().trim().substring(0, s.length() - 1);
					}
				}else {
					fourthIP = s.toString().trim();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				fourIPEdit.removeTextChangedListener(this);
				fourIPEdit.setText(fourthIP);
				fourIPEdit.setSelection(fourthIP.length());
				fourIPEdit.addTextChangedListener(this);
				isFill();
			}
		});
	}

	/**
	 * 是否ip都填好
	 */
	private void isFill(){
		boolean isFill = true;
		if(TextUtils.isEmpty(firIPEdit.getText().toString())){
			isFill = false;
		}
		if(TextUtils.isEmpty(secIPEdit.getText().toString())){
			isFill = false;
		}
		if(TextUtils.isEmpty(thirIPEdit.getText().toString())){
			isFill = false;
		}
		if(TextUtils.isEmpty(fourIPEdit.getText().toString())){
			isFill = false;
		}

		if(mListenerBack != null){
			mListenerBack.onListener(-1, null, isFill);
		}
	}

	/**
	 * 返回整个ip地址
	 * @return
	 */
	public String getIpText(){
		if(TextUtils.isEmpty(firstIP) || TextUtils.isEmpty(secondIP)
				|| TextUtils.isEmpty(thirdIP) || TextUtils.isEmpty(fourthIP)){
			return  null;
		}
		return  firstIP + "." + secondIP + "." + thirdIP + "." + fourthIP;
	}

	/**
	 * 本地读取的ip地址显示至界面
	 * @param ipText
	 */
	public void setIpText(String ipText){
		if(TextUtils.isEmpty(ipText) || ipText == null){
			return;
		}
		String[] temp = null;
		temp = ipText.split("\\.");
		if(temp != null){
			firIPEdit.setText(temp[0]);
			secIPEdit.setText(temp[1]);
			thirIPEdit.setText(temp[2]);
			fourIPEdit.setText(temp[3]);
			this.setFocusable(true);
			this.requestFocus();
		}
	}
}