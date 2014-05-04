package com.koolbao.saler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.koolbao.saler.adapter.BBSReplyAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.PunchASyncTask;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

public class BBSContentActivity extends BaseActivity implements OnClickListener, IXListViewListener{
	private TextView bbs_time_tv;
	private TextView bbs_author_tv;
	private TextView bbs_title_tv;
	private TextView bbs_content_tv;
	private TextView bbs_views_tv;
	private TextView bbs_reply_tv;
	private XListView bbs_reply_lv;
	private RelativeLayout loading_rl;
	private ScrollView page_srcoll;
	
	private EditText my_reply_et;
	private Button submit_btn;
	
	private BBSReplyAdapter replyAdapter;
	private String lastRefreshTime = "刚刚";
	
	private int current_page = 0;
	private String page_size = "5";
	
	private String post_id;
	private Bundle bundle;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_bbscontent);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.bbs_content_title);
		initCustom();
		initListener();
		initList();
		initDatas();
		
		new ReplyASync().execute();
	}
	
	private void initDatas() {
		Intent intent = getIntent();
		bundle = intent.getBundleExtra("bbs_bundle");
		Map<String, Object> tabledata = HttpTaskUtils.parseTableDataToMap(bundle.getString("tabledata"));
		if (tabledata.isEmpty()) {
			Toast.makeText(this, "帖子已被删除", Toast.LENGTH_SHORT).show();
			onBackPressed();
			return;
		}
		post_id = tabledata.get("post_id").toString();
		String title = tabledata.get("title").toString();
		String content = tabledata.get("content").toString();
		String created = tabledata.get("created").toString();
		String user_nick = tabledata.get("user_nick").toString();
		String views = tabledata.get("hit").toString();
		String comments = tabledata.get("comments").toString();
		
		bbs_time_tv.setText(created.substring(5, 16).replaceAll("-", "/"));
		bbs_title_tv.setText(title);
		bbs_content_tv.setText(Html.fromHtml(content));
		bbs_content_tv.setMovementMethod(LinkMovementMethod.getInstance());
		bbs_author_tv.setText(user_nick);
		bbs_views_tv.setText(views);
		bbs_reply_tv.setText(comments);
		
		if (user_nick.equals("酷宝数据")) {
			String id = UserInfoUtils.id;
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", id);
			param.put("type", "3");
			param.put("content", "阅读了 \"" + title + "\"");
			
			new PunchASyncTask(HttpTaskUtils.GROWN_ADD, param).execute();
		}
	}

	private void initList() {
		replyAdapter = new BBSReplyAdapter(this);
		bbs_reply_lv.setAdapter(replyAdapter);
		
		Vector<Map<String, String>> data = new Vector<Map<String, String>>();
		addAdapterItem(data);
		lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
		onLoad();
	}

	private void initListener() {
		bbs_reply_lv.setXListViewListener(this);
		submit_btn.setOnClickListener(this);
		
		page_srcoll.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				findViewById(R.id.bbs_reply_lv).getParent().requestDisallowInterceptTouchEvent(false); 
                return false;
			}
		});
		
		bbs_reply_lv.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true); 
                return false;
			}
		});
	}

	private void initCustom() {
		page_srcoll = (ScrollView) findViewById(R.id.page_srcoll);
		bbs_time_tv = (TextView) findViewById(R.id.bbs_time_tv);
		bbs_author_tv = (TextView) findViewById(R.id.bbs_author_tv);
		bbs_title_tv = (TextView) findViewById(R.id.bbs_title_tv);
		bbs_content_tv = (TextView) findViewById(R.id.bbs_content_tv);
		bbs_views_tv = (TextView) findViewById(R.id.views_tv);
		bbs_reply_tv = (TextView) findViewById(R.id.reply_tv);
		bbs_reply_lv = (XListView) findViewById(R.id.bbs_reply_lv);
		loading_rl = (RelativeLayout) findViewById(R.id.loading_rl);
		
		my_reply_et = (EditText) findViewById(R.id.my_reply_et);
		submit_btn = (Button) findViewById(R.id.submit_btn);
		
		bbs_reply_lv.setPullRefreshEnable(false);
		bbs_reply_lv.setPullLoadEnable(false);
		//解决列表拖动到上下边界时出现的蓝色阴影
		bbs_reply_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		page_srcoll.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == submit_btn.getId()) {
			if (UserInfoUtils.id == null) {
				Toast.makeText(this, "未登录的用户不能参与回帖，请先登录!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (checkForm()) {
				new ReplySubmitASync().execute();
			}
		}
	}

	private boolean checkForm() {
		String reply_txt = my_reply_et.getText().toString();
		if (reply_txt.isEmpty() || reply_txt.replaceAll(" ", "").isEmpty()) {
			Toast.makeText(this, "回复内容不得为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onRefresh() {
		//没有下拉刷新
	}

	@Override
	public void onLoadMore() {
		//上拉加载
		new ReplyASync().execute();
	}
	
	private void onLoad() {
		bbs_reply_lv.stopLoadMore();
		bbs_reply_lv.setRefreshTime(lastRefreshTime);
	}
	
	private void addAdapterItem(Vector<Map<String, String>> data) {
		for (Map<String, String> item : data) {
			if (replyAdapter == null) {
				replyAdapter = new BBSReplyAdapter(this);
				bbs_reply_lv.setAdapter(replyAdapter);
			}
			replyAdapter.addItem(item);
		}
	}
	
	/**
	 * 加载回复列表
	 * @author KuMa
	 *
	 */
	class ReplyASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			if (replyAdapter == null) {
				loading_rl.setVisibility(ProgressBar.VISIBLE);
			}
		}

		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("post_id", post_id);
			param.put("page", (++current_page) + "");
			param.put("page_size", page_size);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.BBS_REPLY_LIST, param);
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
				Toast.makeText(BBSContentActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				onLoad();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils.parseDataToListdata(result.get("tabledata").toString());
			
			int position = bbs_reply_lv.getFirstVisiblePosition();
			Vector<Map<String, String>> datas = new Vector<Map<String, String>>(tabledata);
			addAdapterItem(datas);
			bbs_reply_lv.setSelection(position);
			onLoad();
			
			int page_count = Integer.valueOf(result.get("page_count").toString());
			if (page_count > current_page) {
				bbs_reply_lv.setPullLoadEnable(true);
			} else {
				bbs_reply_lv.setPullLoadEnable(false);
			}
		}
	}
	
	class ReplySubmitASync extends AsyncTask<Void, Void, Map<String, Object>> {

		@Override
		protected void onPreExecute() {
			submit_btn.setClickable(false);
			CustomProgressDialog.createDialog(BBSContentActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("post_id", post_id);
			param.put("content", my_reply_et.getText().toString());
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.ADD_BBS_REPLY, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			submit_btn.setClickable(true);
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(BBSContentActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				//回复成功刷新回复列表
				Toast.makeText(BBSContentActivity.this, "回复成功", Toast.LENGTH_SHORT).show();
				my_reply_et.setText("");
				current_page = 0;
				if (replyAdapter != null) {
					replyAdapter.removeAll();
				}
				replyAdapter = null;
				bbs_reply_lv.setPullLoadEnable(false);
				new ReplyASync().execute();
			} else {
				Toast.makeText(BBSContentActivity.this, "回复失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
