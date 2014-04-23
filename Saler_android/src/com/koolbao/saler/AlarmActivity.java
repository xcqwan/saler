package com.koolbao.saler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.koolbao.saler.adapter.AlarmAdapter;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;

import android.os.Bundle;
import android.os.Handler;

public class AlarmActivity extends BaseActivity  implements IXListViewListener{
	private XListView alarm_lv;
	
	private AlarmAdapter alarmAdapter;
	private String lastRefreshTime = "刚刚";
	
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_alarm);
		super.onCreate(savedInstanceState);
		
		//设置标题栏标题
		setTitleBarTxt(R.string.alarm_title);
		initCustom();
		initListener();
		initXList();
		
		mHandler = new Handler();
	}
	
	private void initXList() {
		alarmAdapter = new AlarmAdapter(this);
		alarm_lv.setAdapter(alarmAdapter);
		
		Vector<Map<String, Object>> data = new Vector<Map<String, Object>>();
		for (int i = 10; i > 1; i--) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("alarm_time", "2014-04-0" + i / 2);
			item.put("alarm_title", "测试数据" + i);
			item.put("isChecked", i % 3 == 1 ? true : false);
			data.add(item);
		}

		addAdapterItem(data);
		lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
		onLoad();
	}
	
	private void onLoad() {
		alarm_lv.stopRefresh();
		alarm_lv.stopLoadMore();
		alarm_lv.setRefreshTime(lastRefreshTime);
	}
	
	private void addAdapterItem(Vector<Map<String, Object>> data) {
		Vector<Map<String, Object>> items = new Vector<Map<String,Object>>();

		Map<String, Object> temp = null;
		Set<String> set = alarmAdapter.getLastMonthsSet();
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				// 获取数据
				temp = data.get(i);
				String letter_month = temp.get("alarm_time").toString();
				boolean isTop = false;
				if (temp.containsKey("top")) {
					isTop = (Boolean) temp.get("top");
				} else {
					temp.put("top", false);
				}

				if (set.contains(letter_month)) {
					items.add(temp);
				} else {
					isTop = true;
					temp.put("top", isTop);
					set.add(letter_month);
					items.add(temp);
				}
			}

			for (Map<String, Object> item : items) {
				alarmAdapter.addItem(item);
			}
		}
	}


	private void initListener() {
		alarm_lv.setXListViewListener(this);
	}

	private void initCustom() {
		alarm_lv = (XListView) findViewById(R.id.alarm_lv);
		
		alarm_lv.setPullLoadEnable(true);
		alarm_lv.setPullRefreshEnable(true);
	}

	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				initXList();
				onLoad();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				int position = alarm_lv.getFirstVisiblePosition();
				Vector<Map<String, Object>> data = new Vector<Map<String, Object>>();
				for (int i = 9; i > 1; i--) {
					Map<String, Object> item = new HashMap<String, Object>();
					item.put("alarm_time", "2014-03-0" + i / 2);
					item.put("alarm_title", "测试数据" + i);
					item.put("isChecked", i % 2 == 1 ? true : false);
					data.add(item);
				}

				addAdapterItem(data);
				alarm_lv.setSelection(position);
				onLoad();
			}
		}, 2000);
	}
}
