package com.koolbao.saler;

import java.util.ArrayList;

import com.koolbao.saler.adapter.ContentAdapter;
import com.koolbao.saler.fragment.BaoBeiPageFragment;
import com.koolbao.saler.fragment.DataPageFragment;
import com.koolbao.saler.fragment.DiagnosePageFragment;
import com.koolbao.saler.utils.UserInfoUtils;
import com.umeng.analytics.MobclickAgent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShopActivity extends FragmentActivity implements OnClickListener{
	private Button back_btn;
	private TextView diagnose_btn;
	private TextView shopdata_btn;
	private TextView shopbaobei_btn;
	
	private ViewPager contentPager;
	private ContentAdapter contentAdapter;
	private ArrayList<Fragment> pagerItemList = new ArrayList<Fragment>();
	
	private int pager_position = 0;
	
	private SharedPreferences sharePrefer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		
		initCustom();
		initListener();
		changePosition();
	}

	private void initListener() {
		back_btn.setOnClickListener(this);
		diagnose_btn.setOnClickListener(this);
		shopdata_btn.setOnClickListener(this);
		shopbaobei_btn.setOnClickListener(this);
		
		DiagnosePageFragment diagnosePage = new DiagnosePageFragment();
		pagerItemList.add(diagnosePage);
		DataPageFragment dataPage = new DataPageFragment();
		pagerItemList.add(dataPage);
		BaoBeiPageFragment baobeiPage = new BaoBeiPageFragment();
		pagerItemList.add(baobeiPage);
		
		contentAdapter = new ContentAdapter(getSupportFragmentManager(), pagerItemList);
		contentPager.setAdapter(contentAdapter);
		contentPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				pager_position = position;
				changePosition();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initCustom() {
		back_btn = (Button) findViewById(R.id.back_btn);
		diagnose_btn = (TextView) findViewById(R.id.diagnose_btn);
		shopdata_btn = (TextView) findViewById(R.id.shopdata_btn);
		shopbaobei_btn = (TextView) findViewById(R.id.shopbaobei_btn);
		
		contentPager = (ViewPager) findViewById(R.id.content_layout);
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
		
		contentPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == back_btn.getId()) {
			onBackPressed();
		}
		if (v.getId() == diagnose_btn.getId()) {
			contentPager.setCurrentItem(0);
			pager_position = 0;
			changePosition();
		}
		if (v.getId() == shopdata_btn.getId()) {
			contentPager.setCurrentItem(1);
			pager_position = 1;
			changePosition();
		}
		if (v.getId() == shopbaobei_btn.getId()) {
			contentPager.setCurrentItem(2);
			pager_position = 2;
			changePosition();
		}
	}
	
	private void changePosition() {
		diagnose_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.diagnose_btn_normal), null, null);
		diagnose_btn.setTextColor(getResources().getColor(R.color.black));
		diagnose_btn.setClickable(true);
		shopdata_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.fresh_icon_normal), null, null);
		shopdata_btn.setTextColor(getResources().getColor(R.color.black));
		shopdata_btn.setClickable(true);
		shopbaobei_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baobei_btn_normal), null, null);
		shopbaobei_btn.setTextColor(getResources().getColor(R.color.black));
		shopbaobei_btn.setClickable(true);
		switch (pager_position) {
		case 0:
			diagnose_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.diagnose_btn_press), null, null);
			diagnose_btn.setTextColor(getResources().getColor(R.color.kb_txt_yellow));
			diagnose_btn.setClickable(false);
			break;
		case 1:
			shopdata_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.fresh_icon_press), null, null);
			shopdata_btn.setTextColor(getResources().getColor(R.color.kb_txt_yellow));
			shopdata_btn.setClickable(false);
			break;
		case 2:
			shopbaobei_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.baobei_btn_press), null, null);
			shopbaobei_btn.setTextColor(getResources().getColor(R.color.kb_txt_yellow));
			shopbaobei_btn.setClickable(false);
			break;
		}
	}
}
