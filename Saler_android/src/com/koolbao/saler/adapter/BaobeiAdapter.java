package com.koolbao.saler.adapter;

import java.util.List;
import java.util.Map;

import com.koolbao.saler.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BaobeiAdapter extends BaseAdapter {
	private List<Map<String, String>> data;
	private Context mContext;
	
	public BaobeiAdapter(Context context, List<Map<String, String>> data_list) {
		mContext = context;
		data = data_list;
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
            convertView = inflater.inflate(R.layout.item_baobei, null);
        }
		TextView bb_title = (TextView) convertView.findViewById(R.id.baobei_title);
        TextView bb_price = (TextView) convertView.findViewById(R.id.baobei_price);
        TextView bb_sku = (TextView) convertView.findViewById(R.id.baobei_sku);
        TextView bb_pv = (TextView) convertView.findViewById(R.id.baobei_pv);
        TextView bb_fov = (TextView) convertView.findViewById(R.id.baobei_fov);
        TextView bb_uv = (TextView) convertView.findViewById(R.id.baobei_uv);
        TextView bb_sales = (TextView) convertView.findViewById(R.id.baobei_sales);
        ImageView bb_pic = (ImageView) convertView.findViewById(R.id.baobei_pic);
        
        Map<String, String> itemData = data.get(position);
        if (!itemData.isEmpty()) {
        	bb_title.setText(itemData.get("after_title"));
        	bb_price.setText("￥: " + itemData.get("item_price"));
        	bb_sku.setText(itemData.get("sku"));
        	bb_pv.setText(itemData.get("pv"));
        	bb_fov.setText(itemData.get("item_fov"));
        	bb_uv.setText(itemData.get("sv"));
        	bb_sales.setText(itemData.get("achieve_items"));
        	
        	String bb_pic_path = itemData.get("pic_url");
        	Picasso.with(mContext).load(bb_pic_path).into(bb_pic);
		}
		return convertView;
	}
}
