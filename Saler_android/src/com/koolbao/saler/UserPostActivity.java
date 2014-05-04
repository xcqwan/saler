package com.koolbao.saler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.koolbao.saler.adapter.NewsLetterAdapter;
import com.koolbao.saler.adapter.PostAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class UserPostActivity extends BaseActivity implements OnClickListener, IXListViewListener, OnItemClickListener, OnItemLongClickListener, OnRefreshListener {
	public final int MY_POST_TAB = 1;
	public final int REPLY_TAB = 2;
	public final int FAVOR_TAB = 3;
	public final int LOAD_STATUS = 10;
	public final int REFRESH_STATUS = 11;
	private Button my_post_btn;
	private Button reply_btn;
	private Button favor_btn;
	private XListView post_lv;
	private RelativeLayout loading_rl;
	private SwipeRefreshLayout swipe_srl;
	
	private NewsLetterAdapter newsAdapter;
	private PostAdapter postAdapter;
	private String lastRefreshTime = "刚刚";
	
	private int pager_position = MY_POST_TAB;
	private int current_page = 0;
	private String page_size = "10";
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user_post);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.user_post_title);
		initCustom();
		initListener();
		changePosition();
		
		refreshList();
	}

	private void initListener() {
		my_post_btn.setOnClickListener(this);
		reply_btn.setOnClickListener(this);
		favor_btn.setOnClickListener(this);
		post_lv.setXListViewListener(this);
		post_lv.setOnItemClickListener(this);
		post_lv.setOnItemLongClickListener(this);
		swipe_srl.setOnRefreshListener(this);
	}

	private void initCustom() {
		my_post_btn = (Button) findViewById(R.id.my_post_btn);
		reply_btn = (Button) findViewById(R.id.reply_btn);
		favor_btn = (Button) findViewById(R.id.favor_btn);
		loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);
		
		swipe_srl = (SwipeRefreshLayout) findViewById(R.id.swipe_srl);
		swipe_srl.setColorScheme(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light);
		post_lv = (XListView) findViewById(R.id.post_lv);
		post_lv.setPullLoadEnable(false);
		post_lv.setPullRefreshEnable(false);
		post_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	private void refreshList() {
		current_page = 0;
		changePosition();
		if (postAdapter != null) {
			postAdapter.removeAll();
		}
		postAdapter= null;
		if (newsAdapter != null) {
			newsAdapter.removeAll();
		}
		
		newsAdapter= null;
		post_lv.setPullLoadEnable(false);
		new ListDataASync(pager_position).execute(REFRESH_STATUS);
	}
	
	/**
	 * 选项卡选择改变
	 */
	private void changePosition() {
		my_post_btn.setBackgroundResource(R.drawable.btn_normal);
		reply_btn.setBackgroundResource(R.drawable.btn_normal);
		favor_btn.setBackgroundResource(R.drawable.btn_normal);
		switch (pager_position) {
		case MY_POST_TAB:
			my_post_btn.setBackgroundResource(R.drawable.btn_press);
			break;
		case REPLY_TAB:
			reply_btn.setBackgroundResource(R.drawable.btn_press);
			break;
		case FAVOR_TAB:
			favor_btn.setBackgroundResource(R.drawable.btn_press);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == my_post_btn.getId()) {
			pager_position = MY_POST_TAB;
			changePosition();
			refreshList();
		}
		if (v.getId() == reply_btn.getId()) {
			pager_position = REPLY_TAB;
			changePosition();
			refreshList();
		}
		if (v.getId() == favor_btn.getId()) {
			pager_position = FAVOR_TAB;
			changePosition();
			refreshList();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		if (pager_position == FAVOR_TAB) {
			Map<String, String> letterMap = newsAdapter.getItem(position - 1);
			Bundle bundle = new Bundle();
			for (String key : letterMap.keySet()) {
				bundle.putString(key, letterMap.get(key).toString());
			}
			Intent intent = new Intent(this, NewsContentActivity.class);
			intent.putExtra("letter", bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			
			String[] param = new String[2];
			param[0] = letterMap.get("title");
			param[1] = "2";
			new FormSubmitASync().execute(param);
		} else {
			Map<String, String> postMap = postAdapter.getItem(position - 1);
			String[] param = new String[2];
			param[0] = postMap.get("post_id");
			param[1] = postMap.get("title");
			new LoadBBSASync().execute(param);
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (pager_position != FAVOR_TAB) {
			return false;
		}
		Map<String, String> item_data = newsAdapter.getItem(position - 1);
		final String news_id = item_data.get("id");
		new AlertDialog.Builder(this)    
		                .setTitle("收藏提醒")  
		                .setMessage("是否取消收藏？")  
		                .setNegativeButton("否", null)
		                .setPositiveButton("是", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new CancelFavorASync().execute(news_id);
							}
						})
		                .show();
		return false;
	}
	
	@Override
	public void onRefresh() {
		current_page = 0;
		new ListDataASync(pager_position).execute(REFRESH_STATUS);
	}

	@Override
	public void onLoadMore() {
		new ListDataASync(pager_position).execute(LOAD_STATUS);
	}
	
	private void onLoad() {
		swipe_srl.setRefreshing(false);
		post_lv.stopRefresh();
		post_lv.stopLoadMore();
		post_lv.setRefreshTime(lastRefreshTime);
	}
	
	class ListDataASync extends AsyncTask<Integer, Void, Map<String, Object>> {
		private int status;
		private int load_position;
		
		public ListDataASync(int load_type) {
			load_position = load_type;
		}
		
		@Override
		protected void onPreExecute() {
			if (newsAdapter == null && postAdapter == null) {
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
				Map<String, Object> requst_msg = null;
				if (load_position != FAVOR_TAB) {
					//帖子
					param.put("type", load_position + "");
					requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.USER_POST_LIST, param);
				} else {
					//资讯
					requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.FAVOR_LIST, param);
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
				Toast.makeText(UserPostActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				onLoad();
				return;
			}
			if (!result.containsKey("tabledata")) {
				Toast.makeText(UserPostActivity.this, "出现未知错误", Toast.LENGTH_SHORT).show();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils.parseDataToListdata(result.get("tabledata").toString());
			//刷新
			if (status == REFRESH_STATUS) {
				//咨询
				if (load_position == FAVOR_TAB) {
					System.out.println(result);
					newsAdapter = new NewsLetterAdapter(UserPostActivity.this);
					post_lv.setAdapter(newsAdapter);
					Vector<Map<String, String>> datas = new Vector<Map<String,String>>(tabledata);
					for (Map<String, String> item : datas) {
						newsAdapter.addItem(item);
					}
				} else {
					//帖子
					postAdapter = new PostAdapter(UserPostActivity.this);
					post_lv.setAdapter(postAdapter);
					Vector<Map<String, String>> datas = new Vector<Map<String,String>>(tabledata);
					for (Map<String, String> item : datas) {
						postAdapter.addItem(item);
					}
				}
				lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
				onLoad();
			} else {
				int position = post_lv.getFirstVisiblePosition();
				//咨询
				if (load_position == FAVOR_TAB) {
					Vector<Map<String, String>> datas = new Vector<Map<String,String>>(tabledata);
					for (Map<String, String> item : datas) {
						newsAdapter.addItem(item);
					}
				} else {
					//帖子
					Vector<Map<String, String>> datas = new Vector<Map<String,String>>(tabledata);
					for (Map<String, String> item : datas) {
						postAdapter.addItem(item);
					}
				}
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
		private String post_title;
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(UserPostActivity.this).canceledOnTouchOutside().show();
		}

		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String post_id = params[0];
			post_title = params[1];
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
				Toast.makeText(UserPostActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Intent intent = new Intent(UserPostActivity.this, BBSContentActivity.class);
				Bundle bundle = new Bundle();
				for (String key : result.keySet()) {
					bundle.putString(key, result.get(key).toString());
				}
				intent.putExtra("bbs_bundle", bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				
				String[] param = new String[2];
				param[0] = post_title;
				param[1] = "3";
				new FormSubmitASync().execute(param);
			} else {
				Toast.makeText(UserPostActivity.this, "获取帖子内容失败!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class FormSubmitASync extends AsyncTask<String, Void, Void> {
		
		@Override
		protected Void doInBackground(String... params) {
			String id = UserInfoUtils.id;
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", id);
			param.put("type", params[1]);
			param.put("content", "阅读了 \"" + params[0] + "\"");
			try {
				HttpTaskUtils.requestByHttpPost(HttpTaskUtils.GROWN_ADD, param);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * 取消收藏
	 * @author KuMa
	 *
	 */
	class CancelFavorASync extends AsyncTask<String, Void, Map<String, Object>> {

		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String news_id = params[0];
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id", UserInfoUtils.id);
			param.put("id", news_id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.CANCEL_FAVOR, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (result == null) {
				Toast.makeText(UserPostActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			int error = Integer.valueOf(result.get("error").toString());
			if (error == 0) {
				Toast.makeText(UserPostActivity.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
				if (pager_position == FAVOR_TAB) {
					refreshList();
				}
			} else {
				Toast.makeText(UserPostActivity.this, "已经取消收藏过这条资讯", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
