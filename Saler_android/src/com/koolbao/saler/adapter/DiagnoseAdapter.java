package com.koolbao.saler.adapter;

import java.util.List;

import com.koolbao.saler.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiagnoseAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> mData;
	
	public DiagnoseAdapter(Context context, List<String> data) {
		mContext = context;
		mData = data;
	}
	
	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {  
            LayoutInflater inflater = LayoutInflater.from(mContext);  
            convertView = inflater.inflate(R.layout.item_diagnose, null);
        }
		String diagnose_item_msg = mData.get(position);
		TextView diagnose_item_tv = (TextView) convertView.findViewById(R.id.diagnose_item_tv);
		diagnose_item_tv.setText(diagnose_item_msg);
		return convertView;
	}

}
