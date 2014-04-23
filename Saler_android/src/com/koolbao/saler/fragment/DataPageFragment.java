package com.koolbao.saler.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.koolbao.saler.FlashActivity;
import com.koolbao.saler.R;
import com.koolbao.saler.adapter.TargetAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataPageFragment extends BaseFragment{
	//布局声明
	private View view;
	private RelativeLayout date_content;
	private TextView date_tv;
	private GridView target_gv;
	
	private TargetAdapter adapter;
	private List<Map<String, Object>> target_data;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_shopdata, null);
		initCostom();
		initTargetGrid();
		initListener();
		changeDate();
		return view;
	}
	
	private void initTargetGrid() {
		target_data = new ArrayList<Map<String,Object>>();
		if (reqest_msg == null) {
			target_data.add(new HashMap<String, Object>());
			target_data.add(new HashMap<String, Object>());
		} else {
			Map<String, Object> table1 = HttpTaskUtils.parseTableDataToMap(reqest_msg.get("tabledata1").toString());
			Map<String, Object> table2 = HttpTaskUtils.parseTableDataToMap(reqest_msg.get("tabledata2").toString());
			target_data.add(table1);
			target_data.add(table2);
		}
		adapter = new TargetAdapter(getActivity(), target_data);
		target_gv.setAdapter(adapter);
	}

	/**
	 * 初始化监听事件
	 */
	private void initListener() {
		//时间选择
		date_content.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String dateStr = date_tv.getText().toString();
				final WheelDatePicker wheel = new WheelDatePicker(getActivity(), dateStr, date_tv);
				wheel.showAtLocation(getActivity().findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
				wheel.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						if (wheel.handle_type == 1) {
							changeDate();
						}
					}
				});
			}
		});
		//点击指标
		target_gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Map<String, String> target = adapter.getItem(position);
				String column = target.get("column");
				String title = target.get("title");
				startFlashActivity(column, title);
			}
		});
	}
	
	/**
	 * 打开flash页面
	 * @param column
	 * @param title
	 */
	public void startFlashActivity(String column, String title) {
		String date = date_tv.getText().toString();
		Intent intent = new Intent(getActivity(), FlashActivity.class);
		intent.putExtra("column", column);
		intent.putExtra("title", title);
		intent.putExtra("date", date);
		startActivity(intent);
	}
	
	public void changeDate() {
		String add_time = date_tv.getText().toString();
		String add_time_tongqi = add_time;
		try {
			add_time_tongqi = DateFormatUtils.format(DateUtils.addDays(DateUtils.parseDate(add_time, "yyyy-MM-dd"), -1), "yyyy-MM-dd");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Map<String, String> pq = new HashMap<String, String>();
		pq.put("user_id_app", UserInfoUtils.id);
		pq.put("shop_id", UserInfoUtils.shop_id);
		pq.put("add_time", add_time);
		pq.put("add_time_tongqi", add_time_tongqi);
		//初始化
		syncOperate(HttpTaskUtils.DAILY_DATA, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg != null) {
					Map<String, Object> tabledata = HttpTaskUtils.parseTableDataToMap(reqest_msg.get("tabledata1").toString());
					if (tabledata.isEmpty()) {
						Toast.makeText(getActivity(), "这一天暂无数据", Toast.LENGTH_SHORT).show();
					} else {
						initTargetGrid();
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
	}

	/**
	 * 初始化组件
	 */
	private void initCostom() {
		//页面布局初始化
		date_content = (RelativeLayout) view.findViewById(R.id.date_content);
		date_tv = (TextView) view.findViewById(R.id.start_date);
		date_tv.setText(DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd"));
		target_gv = (GridView) view.findViewById(R.id.target_gv);
		
		target_gv.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}
