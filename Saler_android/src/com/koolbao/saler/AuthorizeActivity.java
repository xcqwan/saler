package com.koolbao.saler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class AuthorizeActivity extends BaseActivity implements OnClickListener {
	private Button scan_authorize_btn;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_authorize);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.authorize_title);
		initCustom();
		initListener();
	}
	
	private void initListener() {
		scan_authorize_btn.setOnClickListener(this);
	}

	private void initCustom() {
		scan_authorize_btn = (Button) findViewById(R.id.scan_authorize_btn);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == scan_authorize_btn.getId()) {
			Intent intent = new Intent(this, CaptureActivity.class);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String result_content = data.getStringExtra("result");
				new AuthorizeASync().execute(result_content);
			}
		}
	}
	
	/**
	 * 店铺绑定授权
	 * @author KuMa
	 *
	 */
	class AuthorizeASync extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(AuthorizeActivity.this).canceledOnTouchOutside().show();
		}
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String content = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", UserInfoUtils.id);
			param.put("encrypt", content);
			System.out.println(content);
			System.out.println(UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.AUTHORIZE_SHOP, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(AuthorizeActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Toast.makeText(AuthorizeActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
				new UserBondASync().execute();
			} else {
				Toast.makeText(AuthorizeActivity.this, "绑定失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * 用户绑定店铺信息获取
	 * @author KuMa
	 *
	 */
	class UserBondASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(AuthorizeActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.AUTHORIZE_BOND, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(AuthorizeActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils.parseDataToListdata(result.get("tabledata").toString());
			if (!tabledata.isEmpty()) {
				Map<String, String> shop_info = tabledata.get(0);
				//存起来
				UserInfoUtils.saveToSharedMapString(sharePrefer, shop_info);
				UserInfoUtils.init(sharePrefer);
				setResult(RESULT_OK);
				AuthorizeActivity.this.finish();
			} else {
				//无绑定信息
				Toast.makeText(AuthorizeActivity.this, "获取不到绑定的店铺信息！", Toast.LENGTH_SHORT).show();
				UserInfoUtils.clearBinding(sharePrefer);
				UserInfoUtils.init(sharePrefer);
				AuthorizeActivity.this.finish();
			}
		}
	}
}
