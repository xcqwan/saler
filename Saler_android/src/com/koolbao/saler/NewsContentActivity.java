package com.koolbao.saler;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.sina.weibo.SinaWeibo.ShareParams;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.PunchASyncTask;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class NewsContentActivity extends BaseActivity implements OnClickListener {
	private final String LIKE_STATUS = "1";
	private final String UNLIKE_STATUS = "-1";
	private WebView news_wbv;
	private ProgressBar loading_pb;
	private Button good_btn;
	private Button bad_btn;
	
	private RelativeLayout rl_weixin;
	private RelativeLayout rl_weibo;
	private RelativeLayout rl_favor;
	private Button bt_cancle;
	
	private PopupWindow mpopupWindow;
	
	private Bundle bundle;
	private String letter_title;
	private String letter_url;
	private String letter_id;
	private String letter_like;
	private String letter_unlike;
	private int user_like;
	
	private SharedPreferences sharePrefer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_news_content);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.newscontent_title);
		setActionBtn(R.drawable.share_btn);
		
		initCustom();
		initListener();
		initDatas();
		
		ShareSDK.initSDK(this);
	}
	
	@Override
	public void actionBtnListener() {
		showPopMenu();
	}
	
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}

	private void initDatas() {
		Intent intent = getIntent();
		bundle = intent.getBundleExtra("letter");
		letter_id = bundle.getString("id");
		letter_like = bundle.getString("like");
		letter_unlike = bundle.getString("unlike");
		letter_title = bundle.getString("title");
		letter_url = bundle.getString("url");
		user_like = Integer.valueOf(bundle.getString("user_like"));
		
		if (user_like == 1) {
			//已赞
			disableBtn();
		}
		if (user_like == -1) {
			//已不赞
			disableBtn();
		}
		
		news_wbv.loadUrl(letter_url);
		good_btn.setText(letter_like);
		bad_btn.setText(letter_unlike);
		
		String id = UserInfoUtils.id;
		Map<String, String> param = new HashMap<String, String>();
		param.put("user_id_app", id);
		param.put("type", "2");
		param.put("content", "阅读了 \"" + letter_title + "\"");
		
		new PunchASyncTask(HttpTaskUtils.GROWN_ADD, param).execute();
	}
	
	/**
	 * 禁用点赞按钮
	 */
	private void disableBtn() {
		good_btn.setClickable(false);
		good_btn.setBackgroundResource(R.drawable.like_btn_unclickable);
		good_btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.like_icon_unclickable), null, null, null);
		good_btn.setTextColor(getResources().getColor(R.color.white));
		
		bad_btn.setClickable(false);
		bad_btn.setBackgroundResource(R.drawable.like_btn_unclickable);
		bad_btn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.unlike_icon_unclickable), null, null, null);
		bad_btn.setTextColor(getResources().getColor(R.color.white));
	}

	private void initListener() {
		good_btn.setOnClickListener(this);
		bad_btn.setOnClickListener(this);
	}

	private void initCustom() {
		news_wbv = (WebView) findViewById(R.id.news_wbv);
		good_btn = (Button) findViewById(R.id.good_btn);
		bad_btn = (Button) findViewById(R.id.bad_btn);
		loading_pb = (ProgressBar) findViewById(R.id.loading_pb);
		
		news_wbv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		//在当前WebView做跳转
		news_wbv.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				loading_pb.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				loading_pb.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}
		});
		
		news_wbv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				loading_pb.setProgress(newProgress);
				super.onProgressChanged(view, newProgress);
			}
		});
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//拦截回退事件使之先在webview做返回判断
		if ((keyCode == KeyEvent.KEYCODE_BACK) && news_wbv.canGoBack()) {
			news_wbv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == good_btn.getId()) {
			//赞
			if (UserInfoUtils.id == null) {
				Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			new LikeASync(LIKE_STATUS).execute();
		}
		if (v.getId() == bad_btn.getId()) {
			//不赞
			if (UserInfoUtils.id == null) {
				Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
				return;
			}
			new LikeASync(UNLIKE_STATUS).execute();
		}
		if (rl_weixin != null && v.getId() == rl_weixin.getId()) {
			// 微信分享
			IWXAPI api = WXAPIFactory.createWXAPI(this, WXEntryActivity.WEIXIN_APP_ID, true);
			api.registerApp(WXEntryActivity.WEIXIN_APP_ID);
			
			WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = letter_url;
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = letter_title;
            msg.description = "我是卖家";
            Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            msg.thumbData = bmpToByteArray(thumb);
            
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction =  "webpage" + System.currentTimeMillis();
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline; 
            api.sendReq(req);
            mpopupWindow.dismiss();
            
            WXEntryActivity.current_id = letter_id;
		}
		if (rl_weibo != null && v.getId() == rl_weibo.getId()) {
			//微博分享
			ShareParams sp = new ShareParams();
			sp.setText(letter_title + letter_url);
			Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
			weibo.share(sp);
			mpopupWindow.dismiss();
			
			Map<String, String> param = new HashMap<String, String>();
			param.put("g_id", letter_id);
			param.put("type", "1");
			param.put("result", "1");
			new PunchASyncTask(HttpTaskUtils.SHARE_PUNCH, param).execute();
		}
		if (rl_favor != null && v.getId() == rl_favor.getId()) {
			if (UserInfoUtils.id == null) {
				Toast.makeText(this, "未登录的用户不能收藏咨询，请先登录!", Toast.LENGTH_SHORT).show();
				return;
			}
			//收藏
			new FavorASync().execute();
			mpopupWindow.dismiss();
		}
		if (bt_cancle != null && v.getId() == bt_cancle.getId()) {
			//取消
			mpopupWindow.dismiss();
		}
	}

	private byte[] bmpToByteArray(Bitmap thumb) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		thumb.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	@SuppressWarnings("deprecation")
	private void showPopMenu() {

		View view = View.inflate(getApplicationContext(), R.layout.share_popup_menu, null);
		rl_weixin = (RelativeLayout) view.findViewById(R.id.rl_weixin);
		rl_weibo = (RelativeLayout) view.findViewById(R.id.rl_weibo);
		rl_favor = (RelativeLayout) view.findViewById(R.id.rl_favor);
		bt_cancle = (Button) view.findViewById(R.id.bt_cancle);

		rl_weixin.setOnClickListener(this);
		rl_weibo.setOnClickListener(this);
		rl_favor.setOnClickListener(this);
		bt_cancle.setOnClickListener(this);
		
		view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));
		
		if(mpopupWindow==null){
			mpopupWindow = new PopupWindow(this);
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());

			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}
		
		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(action_btn, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
		
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mpopupWindow.dismiss();
			}
		});
	}
	
	class FavorASync extends AsyncTask<Void, Void, Map<String, Object>> {

		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id", UserInfoUtils.id);
			param.put("id", letter_id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.ADD_FAVOR, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (result == null) {
				Toast.makeText(NewsContentActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			int error = Integer.valueOf(result.get("error").toString());
			if (error == 0) {
				Toast.makeText(NewsContentActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NewsContentActivity.this, "已经收藏过这条资讯", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class LikeASync extends AsyncTask<Void, Void, Map<String, Object>> {
		private String type;
		private String type_msg;
		
		public LikeASync(String status) {
			type = status;
			if (type.equals(LIKE_STATUS)) {
				type_msg = "点赞";
			} else {
				type_msg = "倒彩";
			}
		}
		
		@Override
		protected void onPreExecute() {
			if (type.equals(LIKE_STATUS)) {
				good_btn.setClickable(false);
			} else {
				bad_btn.setClickable(false);
			}
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			try {
				String request_path = HttpTaskUtils.NEWS_LIKE + "/" + letter_id + "/" + UserInfoUtils.id + "/" + type;
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpGet(request_path);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			if (type.equals(LIKE_STATUS)) {
				good_btn.setClickable(true);
			} else {
				bad_btn.setClickable(true);
			}
			if (result == null) {
				Toast.makeText(NewsContentActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			
			int error = Integer.valueOf(result.get("error").toString());
			if (error == 0) {
				if (type.equals(LIKE_STATUS)) {
					String like = result.get("like").toString();
					good_btn.setText(like);
				} else {
					String unlike = result.get("unlike").toString();
					bad_btn.setText(unlike);
				}
				
				disableBtn();
				Toast.makeText(NewsContentActivity.this, type_msg + "成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(NewsContentActivity.this, type_msg + "失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
