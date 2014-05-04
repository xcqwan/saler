package com.koolbao.saler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.time.DateFormatUtils;

import retrofit.Callback;
import retrofit.client.Response;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.koolbao.saler.adapter.NewsLetterAdapter;
import com.koolbao.saler.backend.Backend;
import com.koolbao.saler.event.LetterDataAppendEvent;
import com.koolbao.saler.event.XListLoadFinishEvent;
import com.koolbao.saler.event.XListLoadStartEvent;
import com.koolbao.saler.event.XListPageChangeEvent;
import com.koolbao.saler.event.XListRefreshFinishEvnent;
import com.koolbao.saler.event.XListRefreshStartEvent;
import com.koolbao.saler.model.Letter;
import com.koolbao.saler.model.back.LetterBack;
import com.koolbao.saler.utils.BusProvider;
import com.koolbao.saler.utils.ToastUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;
import com.squareup.otto.Subscribe;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.content.Intent;
import android.content.SharedPreferences;

public class NewsActivity extends BaseActivity implements IXListViewListener, OnItemClickListener, OnRefreshListener {
	public final int LOAD_STATUS = 1;
	public final int REFRESH_STATUS = 0;
	private XListView news_lv;
	private SwipeRefreshLayout swipe_srl;

	private NewsLetterAdapter newsAdapter;
	private String lastRefreshTime = "刚刚";
	
	private int currentPage = 0;
	
	private SharedPreferences sharePrefer;
	
	Callback<LetterBack> refreshCallBack = new Callback<LetterBack>() {
		public void success(LetterBack back, Response response) {
			BusProvider.getInstance().post(new XListPageChangeEvent(back.pages, back.page));
			BusProvider.getInstance().post(new LetterDataAppendEvent(back.logs, false));
		};
		
		public void failure(retrofit.RetrofitError arg0) {
			ToastUtils.errorNetWork(getApplicationContext());
		};
	};
	
	Callback<LetterBack> loadCallBack = new Callback<LetterBack>() {
		public void success(LetterBack back, Response response) {
			BusProvider.getInstance().post(new XListPageChangeEvent(back.pages, back.page));
			BusProvider.getInstance().post(new LetterDataAppendEvent(back.logs, true));
		};
		
		public void failure(retrofit.RetrofitError arg0) {
			ToastUtils.errorNetWork(getApplicationContext());
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_news);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.news_title);
		initCustom();
		initListener();
		initXList();
		
		Backend.getInstance().getLetters(++currentPage, UserInfoUtils.id, refreshCallBack);
	}

	private void initListener() {
		news_lv.setXListViewListener(this);
		news_lv.setOnItemClickListener(this);
		swipe_srl.setOnRefreshListener(this);
	}

	private void initXList() {
		newsAdapter = new NewsLetterAdapter(this);
		news_lv.setAdapter(newsAdapter);
		
		Vector<Map<String, String>> data = new Vector<Map<String, String>>();
		List<Letter> letters = new Select().from(Letter.class).execute();
		for (Letter letter : letters) {
			data.add(letter.asMap());
		}
		addAdapterItem(data);
		lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
		onLoad();
	}
	
	@Subscribe
	public void onXListPageChange(XListPageChangeEvent event) {
		int pages = event.total_page;
		int page = event.current_page;
		if (pages > page) {
			news_lv.setPullLoadEnable(true);
		} else {
			news_lv.setPullLoadEnable(false);
		}
	}
	
	/**
	 * 刷新
	 * @param event
	 */
	@Subscribe
	public void onXListRefreshStart(XListRefreshStartEvent event) {
		currentPage = 0;
		Backend.getInstance().getLetters(++currentPage, UserInfoUtils.id, refreshCallBack);
	}
	
	/**
	 * 加载更多
	 * @param event
	 */
	@Subscribe
	public void onXListLoadStart(XListLoadStartEvent event) {
		Backend.getInstance().getLetters(++currentPage, UserInfoUtils.id, loadCallBack);
	}
	
	/**
	 * 上拉数据刷新完成
	 * @param event
	 */
	@Subscribe
	public void onXListRefreshFinish(XListRefreshFinishEvnent event) {
		initXList();
	}
	
	/**
	 * 下拉数据加载完成
	 * @param event
	 */
	@Subscribe
	public void onXListLoadFinish(XListLoadFinishEvent event) {
		int position = news_lv.getFirstVisiblePosition();
		initXList();
		news_lv.setSelection(position);
	}
	
	/**
	 * 资讯数据修改
	 * @param event
	 */
	@Subscribe
	public void onLetterDataAppend(LetterDataAppendEvent event) {
		List<Letter> data = event.data;
		if (!event.isAppend) {
			new Delete().from(Letter.class).execute();
		}
		//批量插入
		ActiveAndroid.beginTransaction();
		try {
			for (Letter letter : data) {
				letter.save();
			}
			ActiveAndroid.setTransactionSuccessful();
		} finally {
			ActiveAndroid.endTransaction();
		}
		
		if (!event.isAppend) {
			//通知资讯数据刷新
			BusProvider.getInstance().post(new XListRefreshFinishEvnent());
		} else {
			//通知资讯数据增加
			BusProvider.getInstance().post(new XListLoadFinishEvent());
		}
	}

	private void addAdapterItem(Vector<Map<String, String>> data) {
		Vector<Map<String, String>> items = new Vector<Map<String,String>>();

		Map<String, String> temp = null;
		Set<String> set = newsAdapter.getLastMonthsSet();
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				// 获取数据
				temp = data.get(i);
				String letter_month = temp.get("date").toString().substring(0, 7);
				boolean isTop = false;
				if (temp.containsKey("top")) {
					isTop = Boolean.valueOf(temp.get("top"));
				} else {
					temp.put("top", Boolean.toString(false));
				}

				if (set.contains(letter_month)) {
					items.add(temp);
				} else {
					isTop = true;
					temp.put("top", Boolean.toString(isTop));
					set.add(letter_month);
					items.add(temp);
				}
			}

			for (Map<String, String> item : items) {
				newsAdapter.addItem(item);
			}
		}
	}

	private void initCustom() {
		swipe_srl = (SwipeRefreshLayout) findViewById(R.id.swipe_srl);
		swipe_srl.setColorScheme(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light);
		news_lv = (XListView) findViewById(R.id.news_lv);
		news_lv.setPullLoadEnable(false);
		news_lv.setPullRefreshEnable(false);
		news_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	private void onLoad() {
		swipe_srl.setRefreshing(false);
		news_lv.stopRefresh();
		news_lv.stopLoadMore();
		news_lv.setRefreshTime(lastRefreshTime);
	}

	@Override
	public void onRefresh() {
		BusProvider.getInstance().post(new XListRefreshStartEvent());
	}

	@Override
	public void onLoadMore() {
		BusProvider.getInstance().post(new XListLoadStartEvent());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Map<String, String> letterMap = newsAdapter.getItem(position - 1);
		Bundle bundle = new Bundle();
		for (String key : letterMap.keySet()) {
			bundle.putString(key, letterMap.get(key).toString());
		}
		Intent intent = new Intent(this, NewsContentActivity.class);
		intent.putExtra("letter", bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
}
