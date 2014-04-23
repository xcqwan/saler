package com.koolbao.saler;

import com.koolbao.saler.utils.BusProvider;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity {
	private Button back_btn;
	private TextView title_tv;
	protected Button action_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		title_tv = (TextView) findViewById(R.id.header_title_tv);
		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		action_btn = (Button) findViewById(R.id.action_btn);
	}
	
	public void setBackBtnVisiable(int visibility) {
		back_btn.setVisibility(visibility);
	}

	public void setActionBtn(int resid) {
		action_btn.setVisibility(View.VISIBLE);
		action_btn.setBackgroundResource(resid);
		action_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				actionBtnListener();
			}
		});
	}
	
	public void setActionBtnClickable(boolean clickable) {
		action_btn.setClickable(clickable);
	}

	public void actionBtnListener() {
		Toast.makeText(this, "尚未重写Action事件", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BusProvider.getInstance().register(this);
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		BusProvider.getInstance().unregister(this);
		MobclickAgent.onPause(this);
	}

	/**
	 * 设置标题栏标题
	 * 
	 * @param resid
	 */
	public void setTitleBarTxt(int resid) {
		title_tv.setText(resid);
	}
	
	public void setTitleBarTxt(String title){
		title_tv.setText(title);
	}

	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
