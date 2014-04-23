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

public class RecordAdapter extends BaseAdapter {
	private Vector<Map<String, String>> mDatas;
	private LayoutInflater mInflater;
	private Set<String> lastMonthsSet;

	public RecordAdapter(Context context) {
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
			convertView = mInflater.inflate(R.layout.item_record, null);
		}
		Map<String, String> item = mDatas.get(position);
		
		if (item != null && !item.isEmpty()) {
			LinearLayout time_bar_linear = (LinearLayout) convertView.findViewById(R.id.time_bar_linear);
			TextView time_bar_tv = (TextView) convertView.findViewById(R.id.time_bar_tv);
			TextView record_tv = (TextView) convertView.findViewById(R.id.record_tv);
			LinearLayout top_divided_line = (LinearLayout) convertView.findViewById(R.id.top_divided_line);
			View divided_left_line = convertView.findViewById(R.id.divided_left_line);
			LinearLayout first_line_linear = (LinearLayout) convertView.findViewById(R.id.first_line_linear);
			
			if (position != 0) {
				first_line_linear.setVisibility(View.GONE);
			} else {
				first_line_linear.setVisibility(View.VISIBLE);
			}
			
			boolean isTop = Boolean.valueOf(item.get("top"));
			String record_time = item.get("created");
			String record_txt = item.get("content");
			if (isTop) {
				time_bar_linear.setVisibility(LinearLayout.VISIBLE);
				top_divided_line.setVisibility(LinearLayout.VISIBLE);
				divided_left_line.setVisibility(View.VISIBLE);
				time_bar_tv.setText(record_time.substring(0, 10));
			} else {
				time_bar_linear.setVisibility(LinearLayout.GONE);
				top_divided_line.setVisibility(LinearLayout.GONE);
				divided_left_line.setVisibility(View.INVISIBLE);
			}
			record_tv.setText(record_txt);
		}
		
		return convertView;
	}
}
