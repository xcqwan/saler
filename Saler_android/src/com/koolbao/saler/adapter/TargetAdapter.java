package com.koolbao.saler.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koolbao.saler.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TargetAdapter extends BaseAdapter {
	private List<Map<String, String>> data;
	private List<Map<String, String>> targetKey;
	private Context mContext;
	
	public TargetAdapter(Context context, List<Map<String, Object>> tabledata) {
		mContext = context;
		data = new ArrayList<Map<String,String>>();
		targetKey = getTargets();
		Map<String, Object> map1 = tabledata.get(0);
		Map<String, Object> map2 = tabledata.get(1);
		for (Map<String, String> target : targetKey) {
			String key = target.keySet().iterator().next();
			Map<String, String> item = new HashMap<String, String>();
			item.put("title", target.get(key));
			if (!map1.isEmpty() && map1.containsKey(key)) {
				String value1_str = map1.get(key).toString();
				String value2_str = map2.get(key).toString();
				double value1 = Double.valueOf(value1_str.replace("%", ""));
				double value2 = Double.valueOf(value2_str.replace("%", ""));
				String value = Double.toString(value1);
				if (value.endsWith(".0")) {
					value = value.substring(0, value.length() - 2);
				}
				if (value1_str.contains("%")) {
					value += "%";
				}
				item.put("value", value);
				item.put("column", key);
				if (value1 >= value2) {
					item.put("trend", "1");
				} else {
					item.put("trend", "0");
				}
			}
			data.add(item);
		}
	}
	private List<Map<String, String>> getTargets() {
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		Map<String, String> target = new HashMap<String, String>();
		target.put("pv", "浏览量");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("uv", "访客数");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("achieve_payment", "成交金额");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("per_ticket_sales", "客单价");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("store_payment_rate", "成交转化率");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("achieve_users", "成交人数");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("achieve_trades", "成交订单数");
		result.add(target);
		target = new HashMap<String, String>();
		target.put("item_fov", "宝贝收藏数");
		result.add(target);
		target = new HashMap<String, String>();
		return result;
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Map<String, String> getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {  
            LayoutInflater inflater = LayoutInflater.from(mContext);  
            convertView = inflater.inflate(R.layout.item_target, null);
        }
		Map<String, String> item = data.get(position);
		String title = item.get("title");
		String value = "--";
		String trend = "0";
		if (item.containsKey("value")) {
			value = item.get("value");
		}
		if (item.containsKey("trend")) {
			trend = item.get("trend");
		}
		TextView title_tv = (TextView) convertView.findViewById(R.id.title_tv);
		TextView target_tv = (TextView) convertView.findViewById(R.id.target_tv);
		ImageView trend_iv = (ImageView) convertView.findViewById(R.id.trend_iv);
		
		title_tv.setText(title);
		target_tv.setText(value);
		if (trend.equals("0")) {
			trend_iv.setImageResource(R.drawable.greendown);
		} else {
			trend_iv.setImageResource(R.drawable.redup);
		}
		return convertView;
	}
}
