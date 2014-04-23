package com.koolbao.saler.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koolbao.saler.BBSContentActivity;
import com.koolbao.saler.NewsContentActivity;
import com.koolbao.saler.model.Banner;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class NotifyAdapter extends PagerAdapter {
	private List<Banner> mData;
	private Context mContext;
	
	public NotifyAdapter(Context context, List<Banner> data) {
		mContext = context;
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0 == arg1;
	}
	
	public Object getItem(int position) {
		return mData.get(position);
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
	
	@Override
	public Object instantiateItem(View container, int position) {
		ImageView image = new ImageView(mContext);
		image.setScaleType(ScaleType.FIT_XY);
		
		final Banner item = mData.get(position);
		String url = item.image_url;
		Picasso.with(mContext).load(url).into(image);
		((ViewPager)container).addView(image);
		
		image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int type = Integer.valueOf(item.type);
				String content_id = item.content_id;
				if (type == 2) {
					//资讯
					new LoadNewsASync().execute(content_id);
				}
				if (type == 3) {
					//帖子
					new LoadBBSASync().execute(content_id);
				}
			}
		});
		return image;
	}
	
	/**
	 * 加载帖子内容
	 * @author KuMa
	 *
	 */
	class LoadBBSASync extends AsyncTask<String, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(mContext).canceledOnTouchOutside().show();
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
				Toast.makeText(mContext, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Intent intent = new Intent(mContext, BBSContentActivity.class);
				Bundle bundle = new Bundle();
				for (String key : result.keySet()) {
					bundle.putString(key, result.get(key).toString());
				}
				intent.putExtra("bbs_bundle", bundle);
				mContext.startActivity(intent);
			} else {
				Toast.makeText(mContext, "获取帖子内容失败!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class LoadNewsASync extends AsyncTask<String, Void, Map<String, Object>> {

		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(mContext).canceledOnTouchOutside().show();
		}

		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String news_id = params[0];
			try {
				Thread.sleep(500);
				String request_path = HttpTaskUtils.NEWS_POST_API + news_id;
				if (UserInfoUtils.id != null) {
					request_path += "/" + UserInfoUtils.id;
				}
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpGet(request_path);
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
				Toast.makeText(mContext, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.isEmpty()) {
				Toast.makeText(mContext, "获取资讯内容失败!", Toast.LENGTH_SHORT).show();
				return;
			}
			Bundle bundle = new Bundle();
			for (String key : result.keySet()) {
				bundle.putString(key, result.get(key).toString());
			}
			Intent intent = new Intent(mContext, NewsContentActivity.class);
			intent.putExtra("letter", bundle);
			mContext.startActivity(intent);
		}
	}
}
