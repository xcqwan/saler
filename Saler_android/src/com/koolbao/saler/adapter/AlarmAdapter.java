package com.koolbao.saler.adapter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.koolbao.saler.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {
	private Vector<Map<String, Object>> mDatas;
	private LayoutInflater mInflater;
	private Set<String> lastMonthsSet;

	public AlarmAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mDatas = new Vector<Map<String, Object>>();
		lastMonthsSet = new HashSet<String>();
	}

	public Map<String, Object> getMessageByIndex(int index) {
		return mDatas.elementAt(index);
	}
	
	public void setLastMonthsSet(Set<String> monthsSet) {
		lastMonthsSet = monthsSet;
	}
	
	public Set<String> getLastMonthsSet() {
		return lastMonthsSet;
	}

	public void addItem(Map<String, Object> item) {
		mDatas.add(item);
		this.notifyDataSetChanged();
	}

	/**
	 * 将Item内的对象复制到指定的Vector中
	 * 
	 * @param messages
	 */
	public void copyItems(Vector<Map<String, Object>> messages) {
		if (messages == null) {
			messages = new Vector<Map<String, Object>>();
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

	public Map<String, Object> getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_alarm, null);
		}
		Map<String, Object> item = mDatas.get(position);
		
		if (item != null && !item.isEmpty()) {
			LinearLayout time_bar_linear = (LinearLayout) convertView.findViewById(R.id.time_bar_linear);
			TextView time_bar_tv = (TextView) convertView.findViewById(R.id.time_bar_tv);
			CheckBox read_cb = (CheckBox) convertView.findViewById(R.id.read_cb);
			TextView alarm_title_tv = (TextView) convertView.findViewById(R.id.alarm_title_tv);
			
			boolean isTop = (Boolean) item.get("top");
			String alarm_time = item.get("alarm_time").toString();
			boolean isChecked = (Boolean) item.get("isChecked");
			String alarm_title = item.get("alarm_title").toString();
			if (isTop) {
				time_bar_linear.setVisibility(LinearLayout.VISIBLE);
				time_bar_tv.setText(alarm_time);
			} else {
				time_bar_linear.setVisibility(LinearLayout.GONE);
			}
			read_cb.setChecked(isChecked);
			alarm_title_tv.setText(alarm_title);
			
			read_cb.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					CheckBox ckb = (CheckBox)v;
					if (ckb.isChecked()) {
						Toast.makeText(mInflater.getContext(), "勾选中了第" + (position + 1) + "项", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			alarm_title_tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Toast.makeText(mInflater.getContext(), "点击了第" + (position + 1) + "项的内容", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		return convertView;
	}
}
