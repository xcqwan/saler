package com.koolbao.saler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.RecordEditText;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RecordFormActivity extends BaseActivity {
	private EditText record_date_et;
	private RecordEditText record_txt_tv;
	private TextView char_warn_tv;
	
	private SharedPreferences sharePrefer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_record_form);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.record_form_title);
		setActionBtn(R.drawable.submit_btn);
		initCustom();
	}

	private void initCustom() {
		record_date_et = (EditText) findViewById(R.id.record_date_et);
		record_txt_tv = (RecordEditText) findViewById(R.id.record_txt_tv);
		char_warn_tv = (TextView) findViewById(R.id.char_warn_tv);
		
		record_txt_tv.setCharWarnTextView(char_warn_tv, 140);
		record_date_et.setText(DateFormatUtils.format(new Date(), "yyyy-MM-dd"));
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	@Override
	public void actionBtnListener() {
		if (checkForm()) {
			new FormSubmitASync().execute();
		}
	}
	
	private boolean checkForm() {
		String content = record_txt_tv.getText().toString();
		if (content == null || content.isEmpty() || content.replaceAll(" ",	"").isEmpty()) {
			Toast.makeText(this, "记录内容不得为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	class FormSubmitASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			setActionBtnClickable(false);
			CustomProgressDialog.createDialog(RecordFormActivity.this).canceledOnTouchOutside().show();
		}

		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			String id = UserInfoUtils.id;
			String content = record_txt_tv.getText().toString();
			while (content.contains("\n\n")) {
				content = content.replaceAll("\n\n", "\n");
			}
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", id);
			param.put("type", "4");
			param.put("content", content);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.GROWN_ADD, param);
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
				Toast.makeText(RecordFormActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Toast.makeText(RecordFormActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
				RecordFormActivity.this.setResult(RESULT_OK);
				onBackPressed();
			} else {
				Toast.makeText(RecordFormActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
