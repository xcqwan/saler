package com.koolbao.saler;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.koolbao.saler.backend.Backend;
import com.koolbao.saler.model.back.FeedBack;
import com.koolbao.saler.utils.ToastUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.RecordEditText;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends BaseActivity implements OnClickListener {
	private RecordEditText feed_txt_tv;
	private TextView char_warn_tv;
	private Button submit_btn;
	
	private Callback<FeedBack> feedCallBack = new Callback<FeedBack>() {
		
		@Override
		public void success(FeedBack feedback, Response arg1) {
			Log.i("test", feedback.toString());
			if (feedback.status == 0) {
				Toast.makeText(getApplicationContext(), "反馈提交成功", Toast.LENGTH_SHORT).show();
				onBackPressed();
			} else {
				Toast.makeText(getApplicationContext(), "反馈提交失败", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		public void failure(RetrofitError arg0) {
			ToastUtils.errorNetWork(getApplicationContext());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_feedback);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.feedback_title);
		initCustom();
		initListener();
	}

	private void initListener() {
		submit_btn.setOnClickListener(this);
	}

	private void initCustom() {
		feed_txt_tv = (RecordEditText) findViewById(R.id.feed_txt_tv);
		char_warn_tv = (TextView) findViewById(R.id.char_warn_tv);
		submit_btn = (Button) findViewById(R.id.submit_btn);
		
		feed_txt_tv.setCharWarnTextView(char_warn_tv, 150);
	}
	
	private boolean checkForm() {
		String content = feed_txt_tv.getText().toString();
		if (content.isEmpty() || content.replaceAll(" ", "").isEmpty()) {
			Toast.makeText(this, "反馈内容不得为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == submit_btn.getId()) {
			if (checkForm()) {
				String content = feed_txt_tv.getText().toString();
				String phone_version = android.os.Build.VERSION.RELEASE;
				Backend.getInstance().postFeedBack(UserInfoUtils.id, content, phone_version, feedCallBack);
			}
		}
	}

}
