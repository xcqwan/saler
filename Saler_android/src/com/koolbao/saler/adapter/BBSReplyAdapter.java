package com.koolbao.saler.adapter;

import java.util.Map;
import java.util.Vector;

import com.koolbao.saler.R;
import com.koolbao.saler.utils.DownImage;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BBSReplyAdapter extends BaseAdapter {
	private Vector<Map<String, String>> mDatas;
	private LayoutInflater mInflater;

	public BBSReplyAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mDatas = new Vector<Map<String, String>>();
	}

	public void addItem(Map<String, String> item) {
		mDatas.add(item);
		this.notifyDataSetChanged();
	}

	/**
	 * 将Item内的对象复制到指定的Vector中
	 * 
	 * @param messages
	 */
	public void copyItems(Vector<Map<String, String>> messages) {
		if (messages == null) {
			messages = new Vector<Map<String, String>>();
		}
		if (mDatas != null && mDatas.size() > 0) {
			for (int i = 0; i < mDatas.size(); i++) {
				messages.add(mDatas.get(i));
			}
		}
	}

	public void removeAll() {
		mDatas.clear();
		this.notifyDataSetChanged();
	}

	public int getCount() {
		return mDatas.size();
	}

	public Map<String, String> getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_reply, null);
		}
		Map<String, String> item = mDatas.get(position);
		
		if (item != null && !item.isEmpty()) {
			ImageView reply_img = (ImageView) convertView.findViewById(R.id.reply_img);
			TextView reply_nick_tv = (TextView) convertView.findViewById(R.id.reply_nick_tv);
			TextView reply_time_tv = (TextView) convertView.findViewById(R.id.reply_time_tv);
			TextView reply_content_tv = (TextView) convertView.findViewById(R.id.reply_content_tv);
			
			String content = item.get("content");
			String created = item.get("created").substring(5).replaceAll("-", "/");
			String user_nick = item.get("user_nick");
			String img_url = item.get("img_url");
			
			reply_nick_tv.setText(user_nick);
			reply_time_tv.setText(created);
			reply_content_tv.setText(content);
			
			if (img_url != null && !img_url.isEmpty()) {
				DownImage.with(mInflater.getContext()).load(img_url).into(reply_img);
			}
		}
		
		return convertView;
	}
}
