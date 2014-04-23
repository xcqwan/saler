package com.koolbao.saler;

import java.util.HashMap;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.widge.CustomProgressDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AboutUsActivity extends BaseActivity implements OnClickListener {
	private Button update_btn; 
	private LinearLayout kb_card_linear;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_about_us);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.about_us_text);
		initCustom();
		initListener();
		
		new checkUpdateASync().execute();
	}

	private void initListener() {
		update_btn.setOnClickListener(this);
		kb_card_linear.setOnClickListener(this);
	}

	private void initCustom() {
		update_btn = (Button) findViewById(R.id.update_btn);
		//默认隐藏
		update_btn.setVisibility(View.INVISIBLE);
		
		kb_card_linear = (LinearLayout) findViewById(R.id.kb_card_linear);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == update_btn.getId()) {
			Uri uri = Uri.parse("http://shop.koolbao.com/Saler_android.apk");
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
		//客服电话
		if (v.getId() == kb_card_linear.getId()) {
			/*
			 * 调用拨打电话
			 */
			// 传入服务， parse（）解析号码
//			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:0571-87853991"));
//			// 通知activtity处理传入的call服务
//			startActivity(intent);
			
			/*
			 * 调用拨号盘
			 */
			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:0571-87853991")); 
		    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    startActivity(intent);
		}
	}
	
	class checkUpdateASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(AboutUsActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("version", getResources().getString(R.string.app_version));
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.GET_VERSION, param);
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
				Toast.makeText(AboutUsActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			int isNewVersion = Integer.valueOf(result.get("isNewVersion").toString());
			if (isNewVersion > 0) {
				update_btn.setVisibility(View.VISIBLE);
			}
		}
		
	}
}
