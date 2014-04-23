package com.koolbao.saler.adapter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.koolbao.saler.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsLetterAdapter extends BaseAdapter {
	private Vector<Map<String, String>> mDatas;
	private LayoutInflater mInflater;
	private Set<String> lastMonthsSet;

	public NewsLetterAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mDatas = new Vector<Map<String, String>>();
		lastMonthsSet = new HashSet<String>();
	}

	public Map<String, String> getMessageByIndex(int index) {
		return mDatas.elementAt(index);
	}
	
	public void setLastMonthsSet(Set<String> monthsSet) {
		lastMonthsSet = monthsSet;
	}
	
	public Set<String> getLastMonthsSet() {
		return lastMonthsSet;
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
			convertView = mInflater.inflate(R.layout.item_news, null);
		}
		Map<String, String> item = mDatas.get(position);
		
		if (item != null && !item.isEmpty()) {
			LinearLayout time_bar_linear = (LinearLayout) convertView.findViewById(R.id.time_bar_linear);
			TextView time_bar_tv = (TextView) convertView.findViewById(R.id.time_bar_tv);
			TextView letter_time_tv = (TextView) convertView.findViewById(R.id.letter_time_tv);
			TextView letter_title_tv = (TextView) convertView.findViewById(R.id.letter_title_tv);
			View divided_line = convertView.findViewById(R.id.divided_line);
			View last_line = convertView.findViewById(R.id.last_line);
			
			boolean isTop = Boolean.valueOf(item.get("top"));
			String letter_time = item.get("date");
			String letter_title = item.get("title");
			if (isTop) {
				divided_line.setVisibility(View.GONE);
				time_bar_linear.setVisibility(LinearLayout.VISIBLE);
				time_bar_tv.setText(letter_time.substring(0, 7));
			} else {
				divided_line.setVisibility(View.VISIBLE);
				time_bar_linear.setVisibility(LinearLayout.GONE);
			}
			letter_time_tv.setText(letter_time.substring(5, 10));
			letter_title_tv.setText(letter_title);
			
			if (position == 0) {
				time_bar_linear.setPadding(dip2px(10), dip2px(10), dip2px(10), 0);
			} else {
				time_bar_linear.setPadding(dip2px(10), 0, dip2px(10), 0);
			}
			if (position == mDatas.size() - 1) {
				last_line.setVisibility(View.VISIBLE);
			} else {
				last_line.setVisibility(View.GONE);
			}
		}
		
		return convertView;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public int dip2px(float dpValue) {
		final float scale = mInflater.getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}
