package com.koolbao.saler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.koolbao.saler.adapter.PostAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.utils.WidgeCommon;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class CommunicateActivity extends BaseActivity implements OnClickListener, IXListViewListener, OnItemClickListener {
	public final int FRESH_TAB = 1;
	public final int ACTIVITY_TAB = 2;
	public final int QUESTION_TAB = 3;
	public final int SEARCH_TAB = 4;
	public final int LOAD_STATUS = 10;
	public final int REFRESH_STATUS = 11;
	private TextView fresh_btn;
	private TextView activity_btn;
	private TextView question_btn;
	private SearchView search_sv;
	
	private RelativeLayout loading_rl;
	private XListView post_lv;
	private PostAdapter postAdapter;

	private String lastRefreshTime = "刚刚";
	
	private int pager_position = FRESH_TAB;
	private int current_page = 0;
	private String page_size = "10";
	
	private SharedPreferences sharePrefer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_communicate);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.communicate_title);
		setActionBtn(R.drawable.record_btn);
		initCustom();
		initListener();
		initList();
		
		refreshList();
	}
	
	@Override
	public void actionBtnListener() {
		//检查是否登录
		if (UserInfoUtils.id == null) {
			Toast.makeText(this, "未登录用户不能发帖，请先登录!", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, BBSFormActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	private void initList() {
		postAdapter = new PostAdapter(this);
		post_lv.setAdapter(postAdapter);
		
		Vector<Map<String, String>> data = new Vector<Map<String, String>>();
		addAdapterItem(data);
		lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
		onLoad();
	}
	
	private void refreshList() {
		current_page = 0;
		changePosition();
		if (postAdapter != null) {
			postAdapter.removeAll();
		}
		postAdapter= null;
		post_lv.setPullLoadEnable(false);
		
		Integer[] param = new Integer[2];
		param[0] = pager_position;
		param[1] = REFRESH_STATUS;
		new PostASync().execute(param);
	}
	
	private void onLoad() {
		post_lv.stopRefresh();
		post_lv.stopLoadMore();
		post_lv.setRefreshTime(lastRefreshTime);
	}
	
	private void addAdapterItem(Vector<Map<String, String>> data) {
		for (Map<String, String> item : data) {
			postAdapter.addItem(item);
		}
	}

	private void initListener() {
		fresh_btn.setOnClickListener(this);
		activity_btn.setOnClickListener(this);
		question_btn.setOnClickListener(this);
		post_lv.setXListViewListener(this);
		post_lv.setOnItemClickListener(this);
		
		search_sv.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				pager_position = SEARCH_TAB;
				refreshList();
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				return false;
			}
		});
	}

	private void initCustom() {
		fresh_btn = (TextView) findViewById(R.id.fresh_btn);  
		activity_btn = (TextView) findViewById(R.id.activity_btn);
		question_btn = (TextView) findViewById(R.id.question_btn);
		post_lv = (XListView) findViewById(R.id.post_lv);
		loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);
		search_sv = (SearchView) findViewById(R.id.search_sv);
		
		post_lv.setPullLoadEnable(false);
		post_lv.setPullRefreshEnable(true);
		//解决列表拖动到上下边界时出现的蓝色阴影
		post_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		WidgeCommon.changeSearchViewDefaultIcon(search_sv, R.drawable.search_icon);
		WidgeCommon.changeSearchViewDefaultEditStyle(search_sv);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	/**
	 * 选项卡选择改变
	 */
	private void changePosition() {
		fresh_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.fresh_icon_normal), null, null);
		fresh_btn.setTextColor(getResources().getColor(R.color.black));
		fresh_btn.setClickable(true);
		activity_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.activity_icon_normal), null, null);
		activity_btn.setTextColor(getResources().getColor(R.color.black));
		activity_btn.setClickable(true);
		question_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.question_icon_normal), null, null);
		question_btn.setTextColor(getResources().getColor(R.color.black));
		question_btn.setClickable(true);
		switch (pager_position) {
		case FRESH_TAB:
			fresh_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.fresh_icon_press), null, null);
			fresh_btn.setTextColor(getResources().getColor(R.color.kb_txt_yellow));
			fresh_btn.setClickable(false);
			break;
		case ACTIVITY_TAB:
			activity_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.activity_icon_press), null, null);
			activity_btn.setTextColor(getResources().getColor(R.color.kb_txt_yellow));
			activity_btn.setClickable(false);
			break;
		case QUESTION_TAB:
			question_btn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.question_icon_press), null, null);
			question_btn.setTextColor(getResources().getColor(R.color.kb_txt_yellow));
			question_btn.setClickable(false);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == fresh_btn.getId()) {
			pager_position = FRESH_TAB;
			refreshList();
		}
		if (v.getId() == activity_btn.getId()) {
			pager_position = ACTIVITY_TAB;
			refreshList();
		}
		if (v.getId() == question_btn.getId()) {
			pager_position = QUESTION_TAB;
			
			refreshList();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		Map<String, String> postMap = postAdapter.getItem(position - 1);
		new LoadBBSASync().execute(postMap.get("post_id"));
		
	}
	
	@Override
	public void onRefresh() {
		current_page = 0;
		Integer[] param = new Integer[2];
		param[0] = pager_position;
		param[1] = REFRESH_STATUS;
		new PostASync().execute(param);
	}

	@Override
	public void onLoadMore() {
		Integer[] param = new Integer[2];
		param[0] = pager_position;
		param[1] = LOAD_STATUS;
		new PostASync().execute(param);
	}
	
	class PostASync extends AsyncTask<Integer, Void, Map<String, Object>> {
		private int status;
		private int load_position;
		
		@Override
		protected void onPreExecute() {
			if (postAdapter == null) {
				loading_rl.setVisibility(ProgressBar.VISIBLE);
			}
		}
		
		@Override
		protected Map<String, Object> doInBackground(Integer... params) {
			load_position = params[0];
			status = params[1];
			Map<String, String> param = new HashMap<String, String>();
			param.put("page", (++current_page) + "");
			param.put("page_size", page_size);
			//搜索
			if (load_position == SEARCH_TAB) {
				String filter_txt = search_sv.getQuery().toString();
				filter_txt = filter_txt.replaceAll("\n", "").replaceAll(" ", "");
				param.put("search", filter_txt);
			} else {
				param.put("cat_id", load_position + "");
			}
			try {
				Map<String, Object> requst_msg = null;
				if (load_position == SEARCH_TAB) {
					requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.POST_SEARCH_LIST, param);
				} else {
					requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.POST_LIST, param);
				}
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			loading_rl.setVisibility(ProgressBar.GONE);
			if (load_position != pager_position) {
				return;
			}
			if (result == null) {
				Toast.makeText(CommunicateActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				onLoad();
				return;
			}
			if (!result.containsKey("tabledata")) {
				Toast.makeText(CommunicateActivity.this, "出现未知错误", Toast.LENGTH_SHORT).show();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils.parseDataToListdata(result.get("tabledata").toString());
			if (tabledata.isEmpty()) {
				Toast.makeText(CommunicateActivity.this, "未找到符合条件的帖子", Toast.LENGTH_SHORT).show();
			}
			//刷新
			if (status == REFRESH_STATUS) {
				postAdapter = new PostAdapter(CommunicateActivity.this);
				post_lv.setAdapter(postAdapter);
				Vector<Map<String, String>> datas = new Vector<Map<String,String>>(tabledata);
				addAdapterItem(datas);
				lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
				onLoad();
			} else {
				int position = post_lv.getFirstVisiblePosition();
				Vector<Map<String, String>> datas = new Vector<Map<String, String>>(tabledata);
				addAdapterItem(datas);
				post_lv.setSelection(position);
				onLoad();
			}
			
			int page_count = Integer.valueOf(result.get("page_count").toString());
			if (page_count > current_page) {
				post_lv.setPullLoadEnable(true);
			} else {
				post_lv.setPullLoadEnable(false);
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
			CustomProgressDialog.createDialog(CommunicateActivity.this).canceledOnTouchOutside().show();
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
				Toast.makeText(CommunicateActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Intent intent = new Intent(CommunicateActivity.this, BBSContentActivity.class);
				Bundle bundle = new Bundle();
				for (String key : result.keySet()) {
					bundle.putString(key, result.get(key).toString());
				}
				intent.putExtra("bbs_bundle", bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			} else {
				Toast.makeText(CommunicateActivity.this, "获取帖子内容失败!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
