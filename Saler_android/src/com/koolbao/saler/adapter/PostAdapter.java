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

public class PostAdapter extends BaseAdapter {
	private Vector<Map<String, String>> mDatas;
	private LayoutInflater mInflater;

	public PostAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.item_post, null);
		}
		Map<String, String> item = mDatas.get(position);
		
		if (item != null && !item.isEmpty()) {
			TextView title_tv = (TextView) convertView.findViewById(R.id.title_tv);
			TextView views_tv = (TextView) convertView.findViewById(R.id.views_tv);
			TextView reply_tv = (TextView) convertView.findViewById(R.id.reply_tv);
			TextView author_tv = (TextView) convertView.findViewById(R.id.author_tv);
			ImageView author_iv = (ImageView) convertView.findViewById(R.id.author_iv);
			
			String title = item.get("title");
			String author = item.get("user_nick");
			String views = item.get("hit");
			String comments = item.get("comments");
			String img_url = item.get("img_url");
			
			title_tv.setText(title);
			views_tv.setText(views);
			reply_tv.setText(comments);
			author_tv.setText(author);
			if (img_url.isEmpty()) {
				author_iv.setImageResource(R.drawable.myshop);
			} else {
				DownImage.with(mInflater.getContext()).load(img_url).into(author_iv);
			}
		}
		
		return convertView;
	}
}
