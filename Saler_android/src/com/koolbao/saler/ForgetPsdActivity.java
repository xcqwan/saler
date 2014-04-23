package com.koolbao.saler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPsdActivity extends BaseActivity implements OnClickListener {
	private TextView step_tv;
	private View step_1_line;
	private View step_2_line;
	private View step_3_line;
	private EditText input_et;
	private Button submit_btn;
	
	private int current_step = 1;
	
	private String tel;
	private String code;
	private String user_id;
	
	private SharedPreferences sharePrefer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_forget_psd);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.forget_title);
		initCustom();
		initListener();
		
		showStep(current_step);
	}
	
	private void initListener() {
		submit_btn.setOnClickListener(this);
	}

	private void initCustom() {
		step_tv = (TextView) findViewById(R.id.step_tv);
		step_1_line = (View) findViewById(R.id.step_1_line);
		step_2_line = (View) findViewById(R.id.step_2_line);
		step_3_line = (View) findViewById(R.id.step_3_line);
		input_et = (EditText) findViewById(R.id.input_et);
		submit_btn = (Button) findViewById(R.id.submit_btn);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == submit_btn.getId()) {
			switch (current_step) {
			case 1:
				if (checkPhone()) {
					new SendCodeASync().execute(input_et.getText().toString());
				}
				break;
			case 2:
				if (checkCode()) {
					new UpdatePsdASync().execute(input_et.getText().toString());
				}
				break;
			case 3:
				if (checkPsd()) {
					new UpdatePsdASync().execute(input_et.getText().toString());
				}
				break;
			}
		}
	}
	
	private boolean checkPsd() {
		String content = input_et.getText().toString();
		if (content.isEmpty() || content.length() < 6) {
			Toast.makeText(this, "密码长度不能少于6个字符", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private boolean checkCode() {
		String content = input_et.getText().toString();
		if (content.isEmpty() || content.length() != 6 || !StringUtils.isNumeric(content)) {
			Toast.makeText(this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private boolean checkPhone() {
		String content = input_et.getText().toString();
		if (content.isEmpty() || content.length() != 11 || !StringUtils.isNumeric(content)) {
			Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	private void showStep(int step) {
		switch (step) {
		case 1:
			step_tv.setText(R.string.step_1);
			step_1_line.setVisibility(View.VISIBLE);
			step_2_line.setVisibility(View.INVISIBLE);
			step_3_line.setVisibility(View.INVISIBLE);
			input_et.setHint(R.string.phone_hint);
			input_et.setText("");
			submit_btn.setText(R.string.check_text);
			break;
		case 2:
			step_tv.setText(R.string.step_2);
			step_1_line.setVisibility(View.VISIBLE);
			step_2_line.setVisibility(View.VISIBLE);
			step_3_line.setVisibility(View.INVISIBLE);
			input_et.setHint(R.string.check_hint);
			input_et.setText("");
			submit_btn.setText(R.string.check_btn);
			break;
		case 3:
			step_tv.setText(R.string.step_3);
			step_1_line.setVisibility(View.VISIBLE);
			step_2_line.setVisibility(View.VISIBLE);
			step_3_line.setVisibility(View.VISIBLE);
			input_et.setHint(R.string.setting_psd_hint);
			input_et.setText("");
			submit_btn.setText(R.string.modify_psd);
			break;
		}
	}
	
	/**
	 * 发送验证码
	 * @author KuMa
	 *
	 */
	class SendCodeASync  extends AsyncTask<String, Void, Map<String, Object>> {
		private String request_tel;
		@Override
		protected void onPreExecute() {
			submit_btn.setClickable(false);
			CustomProgressDialog.createDialog(ForgetPsdActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			request_tel = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("tel", request_tel);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.CHECK_TEL, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			submit_btn.setClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(ForgetPsdActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Fail)) {
				//错误提示
				Toast.makeText(ForgetPsdActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
			} else {
				//发送成功
				Toast.makeText(ForgetPsdActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
				tel = request_tel;
				current_step = 2;
				showStep(current_step);
			}
		}
	}
	
	class UpdatePsdASync extends AsyncTask<String, Void, Map<String, Object>> {
		private String request_msg;
		@Override
		protected void onPreExecute() {
			submit_btn.setClickable(false);
			CustomProgressDialog.createDialog(ForgetPsdActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			request_msg = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("tel", tel);
			if (current_step == 2) {
				param.put("code", request_msg);
			} else {
				param.put("code", code);
				param.put("id", user_id);
				param.put("password", request_msg);
			}
			try {
				Thread.sleep(500);
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.UPDATE_PSD, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			submit_btn.setClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(ForgetPsdActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				if (current_step == 2) {
					Toast.makeText(ForgetPsdActivity.this, "验证成功，请修改密码", Toast.LENGTH_SHORT).show();
					code = request_msg;
					user_id = result.get("id").toString();
					current_step = 3;
					showStep(current_step);
				} else {
					Toast.makeText(ForgetPsdActivity.this, "恭喜你, 密码修改成功", Toast.LENGTH_SHORT).show();
					onBackPressed();
				}
			} else {
				if (current_step == 2) {
					Toast.makeText(ForgetPsdActivity.this, "验证码验证失败, 请重新核对", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ForgetPsdActivity.this, "密码修改失败, 请重试", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}
