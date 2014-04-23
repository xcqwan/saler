package com.koolbao.saler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.koolbao.saler.adapter.NotifyAdapter;
import com.koolbao.saler.backend.Backend;
import com.koolbao.saler.model.Banner;
import com.koolbao.saler.model.back.BannerBack;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.ToastUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;

public class NavigateActivity extends BaseActivity implements OnClickListener {
	private ViewPager notify_pager;
	private TextView news_btn;
	private TextView communicate_btn;
	private TextView shop_btn;
	private TextView alarm_btn;
	private TextView growup_btn;
	private TextView software_btn;

	// 图片轮播
	private NotifyAdapter notifyAdapter;
	private ScheduledExecutorService scheduledExecutorService;
	private int currentItem = 0;

	private SharedPreferences sharePrefer;

	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 切换当前显示的图片
			notify_pager.setCurrentItem(currentItem);
		};
	};
	
	private Callback<BannerBack> bannerCallBack = new Callback<BannerBack>() {
		
		@Override
		public void success(BannerBack bannerBack, Response arg1) {
			new Delete().from(Banner.class).execute();
			
			//批量插入
			ActiveAndroid.beginTransaction();
			try {
				for (Banner banner : bannerBack.tabledata) {
					banner.save();
				}
				ActiveAndroid.setTransactionSuccessful();
			} finally {
				ActiveAndroid.endTransaction();
			}
			
			initPager();
		}
		
		@Override
		public void failure(RetrofitError arg0) {
			ToastUtils.errorNetWork(getApplicationContext());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_navigate);
		super.onCreate(savedInstanceState);

		setTitleBarTxt(R.string.nav_title);
		setBackBtnVisiable(View.INVISIBLE);
		setActionBtn(R.drawable.user_btn);
		initCustom();
		initListener();
		initPager();
		
		Backend.getInstance().getBannerList(bannerCallBack);
	}

	private void initPager() {
		List<Banner> banner_list = new Select().from(Banner.class).execute();
		
		notifyAdapter = new NotifyAdapter(NavigateActivity.this, banner_list);
		notify_pager.setAdapter(notifyAdapter);

		notify_pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentItem = position;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	private void initListener() {
		news_btn.setOnClickListener(this);
		communicate_btn.setOnClickListener(this);
		shop_btn.setOnClickListener(this);
		alarm_btn.setOnClickListener(this);
		growup_btn.setOnClickListener(this);
		software_btn.setOnClickListener(this);
	}

	/**
	 * 初始化绑定
	 */
	private void initCustom() {
		notify_pager = (ViewPager) findViewById(R.id.notify_pager);
		news_btn = (TextView) findViewById(R.id.news_btn);
		communicate_btn = (TextView) findViewById(R.id.communicate_btn);
		shop_btn = (TextView) findViewById(R.id.shop_btn);
		alarm_btn = (TextView) findViewById(R.id.alarm_btn);
		growup_btn = (TextView) findViewById(R.id.growup_btn);
		software_btn = (TextView) findViewById(R.id.software_btn);

		notify_pager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}

	@Override
	public void actionBtnListener() {
		Intent intent = new Intent();
		// 登录逻辑判断
		if (UserInfoUtils.id == null || StringUtils.isBlank(UserInfoUtils.id)) {
			intent.setClass(this, LoginActivity.class);
		} else {
			intent.setClass(this, UserActivity.class);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		// 资讯速递
		if (v.getId() == news_btn.getId()) {
			intent.setClass(this, NewsActivity.class);
		}
		// 卖家交流
		if (v.getId() == communicate_btn.getId()) {
			intent.setClass(this, CommunicateActivity.class);
		}
		// 店铺诊断
		if (v.getId() == shop_btn.getId()) {
			// 用户登录判断
			if (UserInfoUtils.id == null
					|| StringUtils.isBlank(UserInfoUtils.id)) {
				Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
				intent.setClass(this, LoginActivity.class);
			} else {
				// 店铺绑定判断
				new UserBondASync().execute();
				return;
			}
		}
		// 每日提醒
		if (v.getId() == alarm_btn.getId()) {
			Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
			return;
			// intent.setClass(this, AlarmActivity.class);
		}
		// 成长记录
		if (v.getId() == growup_btn.getId()) {
			// 登录逻辑判断
			if (UserInfoUtils.id == null
					|| StringUtils.isBlank(UserInfoUtils.id)) {
				Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
				intent.setClass(this, LoginActivity.class);
			} else {
				intent.setClass(this, GrowupActivity.class);
			}
		}
		// 软件推荐
		if (v.getId() == software_btn.getId()) {
			Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
			return;
			// intent.setClass(this, SoftwareActivity.class);
		}
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	@Override
	protected void onStart() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		// 当Activity显示出来后，每五秒钟切换一次图片显示
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 5, 5,
				TimeUnit.SECONDS);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 当Activity不可见的时候停止切换
		scheduledExecutorService.shutdown();
		super.onStop();
	}

	/**
	 * 换行切换任务
	 * 
	 */
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (notify_pager) {
				currentItem = (currentItem + 1) % notifyAdapter.getCount();
				// 通过Handler切换图片
				handler.obtainMessage().sendToTarget();
			}
		}
	}

	/**
	 * 用户绑定店铺信息获取
	 * 
	 * @author KuMa
	 * 
	 */
	class UserBondASync extends AsyncTask<Void, Void, Map<String, Object>> {

		@Override
		protected void onPreExecute() {
			Toast.makeText(NavigateActivity.this, "获取用户绑定店铺信息",
					Toast.LENGTH_SHORT).show();
			CustomProgressDialog.createDialog(NavigateActivity.this)
					.canceledOnTouchOutside().show();
		}

		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils
						.requestByHttpPost(HttpTaskUtils.AUTHORIZE_BOND, param);
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
				Toast.makeText(NavigateActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！",
						Toast.LENGTH_SHORT).show();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils
					.parseDataToListdata(result.get("tabledata").toString());
			if (!tabledata.isEmpty()) {
				Map<String, String> shop_info = tabledata.get(0);
				// 存起来
				UserInfoUtils.saveToSharedMapString(sharePrefer, shop_info);
				UserInfoUtils.init(sharePrefer);

				Intent intent = new Intent();
				intent.setClass(NavigateActivity.this, ShopActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			} else {
				// 无绑定信息
				UserInfoUtils.clearBinding(sharePrefer);
				UserInfoUtils.init(sharePrefer);

				Intent intent = new Intent();
				Toast.makeText(NavigateActivity.this, "请先绑定店铺!",
						Toast.LENGTH_SHORT).show();
				intent.setClass(NavigateActivity.this, AuthorizeActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		}
	}
}
