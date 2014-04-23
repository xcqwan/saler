package com.koolbao.saler;

import java.util.HashMap;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class NickModifyActivity extends BaseActivity {
	private EditText nick_et;
	
	private SharedPreferences sharePrefer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_nick_modify);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.nick_modify_title);
		setActionBtn(R.drawable.submit_btn);
		initCustom();
	}

	private void initCustom() {
		nick_et = (EditText) findViewById(R.id.nick_et);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	@Override
	public void actionBtnListener() {
		String nick = nick_et.getText().toString();
		if (nick == null || nick.isEmpty() || nick.replaceAll(" ", "").isEmpty()) {
			Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (nick.equals("酷宝数据")) {
			Toast.makeText(this, "禁用此昵称!", Toast.LENGTH_SHORT).show();
			return;
		}
		new NickModifyASync().execute(nick);
	}
	
	class NickModifyASync extends AsyncTask<String, Void, Map<String, Object>> {
		private String nick;
		
		@Override
		protected void onPreExecute() {
			setActionBtnClickable(false);
			CustomProgressDialog.createDialog(NickModifyActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(String... params) {
			nick = params[0];
			String id = UserInfoUtils.id;
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_nick", nick);
			param.put("user_id_app", id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.NICK_MODIFY, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			setActionBtnClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(NickModifyActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Toast.makeText(NickModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
                intent.putExtra("nick", nick);
                NickModifyActivity.this.setResult(RESULT_OK, intent);

                onBackPressed();
			} else {
				Toast.makeText(NickModifyActivity.this, "昵称修改失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
