package com.koolbao.saler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.koolbao.saler.adapter.RecordAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class GrowupActivity extends BaseActivity implements IXListViewListener {
	public final int LOAD_STATUS = 1;
	public final int REFRESH_STATUS = 0;
	public final int REQUEST_FORM = 0;
	private XListView record_lv;
	private RelativeLayout loading_rl;
	
	private RecordAdapter recordAdapter;
	private String lastRefreshTime = "刚刚";
	
	
	private int current_page = 0;
	private String page_size = "10";
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_growup);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.growup_title);
		setActionBtn(R.drawable.record_btn);
		initCustom();
		initListener();
		initXList();
		
		onRefresh();
	}
	
	private void initXList() {
		recordAdapter = new RecordAdapter(this);
		record_lv.setAdapter(recordAdapter);
		
		Vector<Map<String, String>> data = new Vector<Map<String, String>>();

		addAdapterItem(data);
		lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
		onLoad();
	}
	
	private void onLoad() {
		record_lv.stopRefresh();
		record_lv.stopLoadMore();
		record_lv.setRefreshTime(lastRefreshTime);
	}

	private void addAdapterItem(Vector<Map<String, String>> data) {
		Vector<Map<String, String>> items = new Vector<Map<String,String>>();

		Map<String, String> temp = null;
		Set<String> set = recordAdapter.getLastMonthsSet();
		if (data != null && data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				// 获取数据
				temp = data.get(i);
				String grown_date = temp.get("created").substring(0, 10);
				boolean isTop = false;
				if (temp.containsKey("top")) {
					isTop = Boolean.valueOf(temp.get("top"));
				} else {
					temp.put("top", String.valueOf(false));
				}

				if (set.contains(grown_date)) {
					items.add(temp);
				} else {
					isTop = true;
					temp.put("top",  String.valueOf(isTop));
					set.add(grown_date);
					items.add(temp);
				}
			}

			for (Map<String, String> item : items) {
				recordAdapter.addItem(item);
			}
		}
	}

	private void initListener() {
		record_lv.setXListViewListener(this);
	}

	private void initCustom() {
		record_lv = (XListView) findViewById(R.id.record_lv);
		loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);
		
		record_lv.setPullLoadEnable(false);
		record_lv.setPullRefreshEnable(true);
		record_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}

	@Override
	public void actionBtnListener() {
		Intent intent = new Intent(this, RecordFormActivity.class);
		startActivityForResult(intent, REQUEST_FORM);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 添加记录成功回调
		if (requestCode == REQUEST_FORM && resultCode == RESULT_OK) {
			onRefresh();
		}
	}

	@Override
	public void onRefresh() {
		current_page = 0;
		new GrownASync().execute(REFRESH_STATUS);
	}

	@Override
	public void onLoadMore() {
		new GrownASync().execute(LOAD_STATUS);
	}
	
	class GrownASync extends AsyncTask<Integer, Void, Map<String, Object>> {
		private int status;
		
		@Override
		protected void onPreExecute() {
			if (recordAdapter == null || recordAdapter.isEmpty()) {
				loading_rl.setVisibility(ProgressBar.VISIBLE);
			}
		}
		
		@Override
		protected Map<String, Object> doInBackground(Integer... params) {
			status = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("page", (++current_page) + "");
			param.put("page_size", page_size);
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.GROWN_LIST, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			loading_rl.setVisibility(ProgressBar.GONE);
			if (result == null) {
				Toast.makeText(GrowupActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				onLoad();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils.parseDataToListdata(result.get("tabledata").toString());
			//刷新
			if (status == REFRESH_STATUS) {
				recordAdapter = new RecordAdapter(GrowupActivity.this);
				record_lv.setAdapter(recordAdapter);
				Vector<Map<String, String>> datas = new Vector<Map<String,String>>(tabledata);
				addAdapterItem(datas);
				lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
				onLoad();
			} else {
				int position = record_lv.getFirstVisiblePosition();
				Vector<Map<String, String>> datas = new Vector<Map<String, String>>(tabledata);
				addAdapterItem(datas);
				record_lv.setSelection(position);
				onLoad();
			}
			
			int page_count = Integer.valueOf(result.get("page_count").toString());
			if (page_count > current_page) {
				record_lv.setPullLoadEnable(true);
			} else {
				record_lv.setPullLoadEnable(false);
			}
		}
	}
}
