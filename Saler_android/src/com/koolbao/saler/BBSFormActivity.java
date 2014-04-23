package com.koolbao.saler;

import java.util.HashMap;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.RecordEditText;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class BBSFormActivity extends BaseActivity {
	private EditText bbs_title_et;
	private RecordEditText bbs_txt_tv;
	private TextView char_warn_tv;
	private RadioButton fresh_rb;
	private RadioButton activity_rb;
	private RadioButton question_rb;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_bbsform);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.bbs_form_title);
		setActionBtn(R.drawable.submit_btn);
		initCustom();
	}

	private void initCustom() {
		bbs_title_et = (EditText) findViewById(R.id.bbs_title_et);
		bbs_txt_tv = (RecordEditText) findViewById(R.id.bbs_txt_tv);
		char_warn_tv = (TextView) findViewById(R.id.char_warn_tv);
		fresh_rb = (RadioButton) findViewById(R.id.fresh_rb);
		activity_rb = (RadioButton) findViewById(R.id.activity_rb);
		question_rb = (RadioButton) findViewById(R.id.question_rb);
		
		bbs_txt_tv.setCharWarnTextView(char_warn_tv, 500);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	@Override
	public void actionBtnListener() {
		if (checkForm()) {
			new BBSFormSubmitASync().execute();
		}
	}
	
	private boolean checkForm() {
		String title = bbs_title_et.getText().toString();
		if (title == null || title.isEmpty() || title.replaceAll(" ",	"").isEmpty()) {
			Toast.makeText(this, "帖子主题不得为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		String content = bbs_txt_tv.getText().toString();
		if (content == null || content.isEmpty() || content.replaceAll(" ",	"").isEmpty()) {
			Toast.makeText(this, "帖子内容不得为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	class BBSFormSubmitASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			setActionBtnClickable(false);
			CustomProgressDialog.createDialog(BBSFormActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			//帖子类型
			String bbs_type = "1";
			if (fresh_rb.isChecked()) {
				bbs_type = "1";
			}
			if (activity_rb.isChecked()) {
				bbs_type = "2";
			} 
			if (question_rb.isChecked()) {
				bbs_type = "3";
			}
			String content = bbs_txt_tv.getText().toString();
			while (content.contains("\n\n")) {
				content = content.replaceAll("\n\n", "\n");
			}
			String title = bbs_title_et.getText().toString();
			title = title.replaceAll("\n", "");
			Map<String, String> param = new HashMap<String, String>();
			param.put("title", title);
			param.put("content", content);
			param.put("cat_id", bbs_type);
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.ADD_BBS, param);
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
				Toast.makeText(BBSFormActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				String post_id = result.get("post_id").toString();
				new LoadBBSASync().execute(post_id);
			} else {
				Toast.makeText(BBSFormActivity.this, "发表失败", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	/**
	 * 加载帖子内容
	 * @author KuMa
	 *
	 */
	class LoadBBSASync extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(BBSFormActivity.this).canceledOnTouchOutside().show();
		}

		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String post_id = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("post_id", post_id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.BBS_CONTENT, param);
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
				Toast.makeText(BBSFormActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Intent intent = new Intent(BBSFormActivity.this, BBSContentActivity.class);
				Bundle bundle = new Bundle();
				for (String key : result.keySet()) {
					bundle.putString(key, result.get(key).toString());
				}
				intent.putExtra("bbs_bundle", bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				BBSFormActivity.this.finish();
			} else {
				Toast.makeText(BBSFormActivity.this, "获取帖子内容失败!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
