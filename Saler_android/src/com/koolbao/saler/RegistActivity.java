package com.koolbao.saler;

import java.util.HashMap;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.PhoneEditText;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistActivity extends BaseActivity implements OnClickListener{
	private PhoneEditText form_phone_et;
	private EditText form_psd_et;
	private EditText form_checkcode_et;
	private Button action_check_btn;
	private Button form_submit_btn;
	
	private CheckCodeCountDownTimer ccTimer;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_regist);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.regist_title);
		initCustom();
		initListener();
	}
	
	private void initListener() {
		action_check_btn.setOnClickListener(this);
		form_submit_btn.setOnClickListener(this);
	}

	private void initCustom() {
		form_phone_et = (PhoneEditText) findViewById(R.id.form_phone_et);
		form_psd_et = (EditText) findViewById(R.id.form_psd_et);
		form_checkcode_et = (EditText) findViewById(R.id.form_checkcode_et);
		action_check_btn = (Button) findViewById(R.id.action_check_btn);
		form_submit_btn = (Button) findViewById(R.id.form_submit_btn);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	/**
	 * 检查输入内容
	 * @return
	 */
	private boolean checkForm() {
		String phone = form_phone_et.getText().toString().replaceAll(" ", "");
		String password = form_psd_et.getText().toString();
		String checkcode = form_checkcode_et.getText().toString();
		if (phone.length() < 11) {
			Toast.makeText(this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.length() < 6) {
			Toast.makeText(this, "密码长度不得小于六位", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (checkcode.length() != 6) {
			Toast.makeText(this, "验证码格式不对", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 * 重发验证码按钮初始化
	 */
	private void initActionCheckBtn() {
		// 重发验证码按钮不可点击
		action_check_btn.setClickable(false);
		ccTimer = new CheckCodeCountDownTimer(60000, 1000);
		ccTimer.start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == action_check_btn.getId()) {
			String phone = form_phone_et.getText().toString().replaceAll(" ", "");
			if (phone.length() < 11) {
				Toast.makeText(this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
				return;
			}
			new SendCodeASync().execute(phone);
		}
		if (v.getId() == form_submit_btn.getId()) {
			if (checkForm()) {
				String phone = form_phone_et.getText().toString().replaceAll(" ", "");
				String password = form_psd_et.getText().toString();
				String code = form_checkcode_et.getText().toString();
				String[] params = new String[3];
				params[0] = phone;
				params[1] = password;
				params[2] = code;
				
				new RegistASync().execute(params);
			}
		}
	}
	
	/**
	 * 注册
	 * @author KuMa
	 *
	 */
	class RegistASync extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			form_submit_btn.setClickable(false);
			CustomProgressDialog.createDialog(RegistActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String tel = params[0];
			String password = params[1];
			String code = params[2];
			Map<String, String> param = new HashMap<String, String>();
			param.put("tel", tel);
			param.put("password", password);
			param.put("code", code);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.REGIST_USER, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			form_submit_btn.setClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(RegistActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				//注册成功, 返回登录界面
				Toast.makeText(RegistActivity.this, "恭喜你, 注册成功！", Toast.LENGTH_SHORT).show();
				onBackPressed();
			} else {
				//注册失败
				Toast.makeText(RegistActivity.this, "注册失败, 失败码: " + status, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 发送验证码
	 * @author KuMa
	 *
	 */
	class SendCodeASync  extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			action_check_btn.setClickable(false);
			CustomProgressDialog.createDialog(RegistActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String tel = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("tel", tel);
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
			action_check_btn.setClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(RegistActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Fail)) {
				//错误提示
				Toast.makeText(RegistActivity.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
			} else {
				//发送成功
				Toast.makeText(RegistActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
				initActionCheckBtn();
			}
		}
		
	}
	
	/**
	 * 验证码倒计时
	 * @author KuMa
	 *
	 */
	class CheckCodeCountDownTimer extends CountDownTimer {

		public CheckCodeCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			//倒计时结束，设置重发验证码按钮可点击，并且修改按钮样式
			action_check_btn.setClickable(true);
			action_check_btn.setText(R.string.check_text);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			long unFinish = millisUntilFinished / 1000;
			String btn_text = "获取验证码(";
			if (unFinish < 10) {
				btn_text += "0";
			}
			btn_text += unFinish + ")";
			action_check_btn.setText(btn_text);
		}
	}
}
