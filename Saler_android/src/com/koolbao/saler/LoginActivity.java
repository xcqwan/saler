package com.koolbao.saler;

import java.util.HashMap;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.PhoneEditText;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

public class LoginActivity extends BaseActivity implements OnClickListener {
	public static Activity actInstance = new Activity();
	
	private PhoneEditText form_phone_et;
	private EditText form_psd_et;
	private Button form_submit_btn;
	private TextView action_regist_tv;
	private TextView action_forget_tv;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.login_title);
		initCustom();
		initListener();
		
		actInstance = this;
	}
	
	private void initListener() {
		form_submit_btn.setOnClickListener(this);
		action_regist_tv.setOnClickListener(this);
		action_forget_tv.setOnClickListener(this);
	}

	private void initCustom() {
		form_phone_et = (PhoneEditText) findViewById(R.id.form_phone_et);
		form_psd_et = (EditText) findViewById(R.id.form_psd_et);
		form_submit_btn = (Button) findViewById(R.id.form_submit_btn);
		action_regist_tv = (TextView) findViewById(R.id.action_regist_tv);
		action_forget_tv = (TextView) findViewById(R.id.action_forget_tv);
		
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
		if (phone.length() < 11) {
			Toast.makeText(this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (password.length() < 6) {
			Toast.makeText(this, "密码长度不得小于六位", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		//登录按钮
		if (v.getId() == form_submit_btn.getId()) {
			//检查输入内容，然后发送登录请求
			if (checkForm()) {
				String phone = form_phone_et.getText().toString().replaceAll(" ", "");
				String password = form_psd_et.getText().toString();
				String[] params = new String[3];
				params[0] = phone;
				params[1] = password;
				
				new LoginASync().execute(params);
			}
		}
		//新用户注册
		if (v.getId() == action_regist_tv.getId()) {
			Intent intent = new Intent(this, RegistActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		//忘记密码
		if (v.getId() == action_forget_tv.getId()) {
			Intent intent = new Intent(this, ForgetPsdActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	
	class LoginASync extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			form_submit_btn.setClickable(false);
			CustomProgressDialog.createDialog(LoginActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String tel = params[0];
			String password = params[1];
			Map<String, String> param = new HashMap<String, String>();
			param.put("tel", tel);
			param.put("password", password);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.LOGIN_USER, param);
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
				Toast.makeText(LoginActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				//登录成功, 跳转到用户中心界面
				Map<String, Object> user_info = HttpTaskUtils.parseTableDataToMap(result.get("tabledata").toString());
				SharedPreferences sharePrefer = LoginActivity.this.getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
				UserInfoUtils.saveToShared(sharePrefer, user_info);
				UserInfoUtils.init(sharePrefer);
				
				Intent intent = new Intent(LoginActivity.this, UserActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			} else {
				//登录失败
				Toast.makeText(LoginActivity.this, "登录失败, 用户名或密码错误!", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
