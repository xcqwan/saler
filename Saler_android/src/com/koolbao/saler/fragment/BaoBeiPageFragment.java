package com.koolbao.saler.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.koolbao.saler.R;
import com.koolbao.saler.adapter.BaobeiAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.XListView;
import com.koolbao.saler.widge.XListView.IXListViewListener;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;

public class BaoBeiPageFragment extends BaseFragment implements IXListViewListener, OnRefreshListener {
	//页面布局
	private View view;
	private RelativeLayout date_content;
	private TextView date_tv;
	//页面指标
	private TextView trade_baobeis;
	private TextView trade_nums;
	private TextView item_fovs;
	private TextView no_trade_nums;
	private XListView baobei_lv;
	private SwipeRefreshLayout swipe_srl;
	
	private String before_change;
	int page_count;
	int page_current;
	private BaobeiAdapter adapter;
	private List<Map<String, String>> bb_data_list;
	
	private String lastRefreshTime = "刚刚";
	
	private String page_size = "5";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_baobei, null);
		initCostom();
		initBaobeiList();
		initListener();
		changeDate();
		return view;
	}
	
	/**
	 * 初始化宝贝列表
	 */
	private void initBaobeiList() {
		bb_data_list = new ArrayList<Map<String,String>>();
		if (reqest_msg == null) {
			baobei_lv.setPullLoadEnable(false);
			page_count = 1;
			page_current = 1;
		} else {
			bb_data_list = HttpTaskUtils.parseDataToListdata(reqest_msg.get("tabledata").toString());
			page_count = Integer.valueOf(reqest_msg.get("page_count").toString());
			page_current = Integer.valueOf(reqest_msg.get("page_current").toString());
			if (page_count > page_current) {
				baobei_lv.setPullLoadEnable(true);
			} else {
				baobei_lv.setPullLoadEnable(false);
			}
		}
		adapter = new BaobeiAdapter(getActivity(), bb_data_list);
		baobei_lv.setAdapter(adapter);
		lastRefreshTime = DateFormatUtils.format(new Date(), "yyyy/MM/dd HH:mm:ss");
		onLoad();
	}

	public void changeDate() {
		String afterChange = date_tv.getText().toString();
		if (before_change != null && before_change.equals(afterChange)) {
			return;
		}
		Map<String, String> pq = new HashMap<String, String>();
		pq.put("user_id_app", UserInfoUtils.id);
		pq.put("user_id", UserInfoUtils.user_id);
		pq.put("shop_id", UserInfoUtils.shop_id);
		pq.put("add_time", date_tv.getText().toString());
		//初始化指标数据
		syncOperate(HttpTaskUtils.BAOBEI_DATA_TOTAL, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg_total != null) {
					try {
						Map<String, Object> tabledata = HttpTaskUtils.parseTableDataToMap(reqest_msg_total.get("tabledata").toString());
						if (tabledata.isEmpty()) {
							Toast.makeText(getActivity(), "这一天暂无指标数据", Toast.LENGTH_SHORT).show();
						} else {
							showData(tabledata);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
		
		//初始化宝贝列表
		pq.put("page", "1");
		pq.put("page_size", page_size);
		syncOperate(HttpTaskUtils.BAOBEI_DATA, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg != null) {
					try {
						Map<String, Object> tabledata = HttpTaskUtils.parseTableDataToMap(reqest_msg.get("tabledata").toString());
						if (tabledata.isEmpty()) {
							Toast.makeText(getActivity(), "这一天暂无宝贝数据", Toast.LENGTH_SHORT).show();
						} else {
							initBaobeiList();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
	}
	
	/**
	 * 展示页面数据
	 * @param tabledata
	 */
	private void showData(Map<String, Object> data) {
		trade_baobeis.setText(data.get("trade_baobeis").toString());
		item_fovs.setText(data.get("item_fovs").toString());
		trade_nums.setText(data.get("trade_nums").toString());
		no_trade_nums.setText(data.get("no_trade_nums").toString());
	}

	/**
	 * 初始化监听事件
	 */
	private void initListener() {
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
		
		baobei_lv.setXListViewListener(this);
		swipe_srl.setOnRefreshListener(this);
	}



	/**
	 * 初始化页面组件
	 */
	private void initCostom() {
		date_content = (RelativeLayout) view.findViewById(R.id.date_content);
		date_tv = (TextView) view.findViewById(R.id.start_date);
		date_tv.setText(DateFormatUtils.format(DateUtils.addDays(new Date(), -1), "yyyy-MM-dd"));
		baobei_lv = (XListView) view.findViewById(R.id.baobei_lv);
		baobei_lv.setDivider(null);
		baobei_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		swipe_srl = (SwipeRefreshLayout) view.findViewById(R.id.swipe_srl);
		swipe_srl.setColorScheme(R.color.blue_light, R.color.green_light, R.color.orange_light, R.color.red_light);
		
		trade_baobeis = (TextView) view.findViewById(R.id.trade_baobeis);
		trade_nums = (TextView) view.findViewById(R.id.trade_nums);
		item_fovs = (TextView) view.findViewById(R.id.item_fovs);
		no_trade_nums = (TextView) view.findViewById(R.id.no_trade_nums);
	}



	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onLoadMore() {
		final Map<String, String> pq = new HashMap<String, String>();
		pq.put("user_id_app", UserInfoUtils.id);
		pq.put("user_id", UserInfoUtils.user_id);
		pq.put("shop_id", UserInfoUtils.shop_id);
		pq.put("add_time", date_tv.getText().toString());
		pq.put("page", Integer.toString(page_current + 1));
		pq.put("page_size", page_size);
		//加载数据
		syncOperate(HttpTaskUtils.BAOBEI_DATA, view, new Runnable() {
			
			@Override
			public void run() {
				if (reqest_msg != null) {
					try {
						List<Map<String, String>> data_list = HttpTaskUtils.parseDataToListdata(reqest_msg.get("tabledata").toString());
						for (Map<String, String> item : data_list) {
							bb_data_list.add(item);
						}
						adapter.notifyDataSetChanged();
						onLoad();
						page_count = Integer.valueOf(reqest_msg.get("page_count").toString());
						page_current = Integer.valueOf(reqest_msg.get("page_current").toString());
						if (page_count > page_current) {
							baobei_lv.setPullLoadEnable(true);
						} else {
							baobei_lv.setPullLoadEnable(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getActivity(), "网络连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		}, pq);
	}
	
	private void onLoad() {
		swipe_srl.setRefreshing(false);
		baobei_lv.stopLoadMore();
		baobei_lv.stopRefresh();
		baobei_lv.setRefreshTime(lastRefreshTime);
	}

	@Override
	public void onRefresh() {
		before_change = null;
		changeDate();
	}
}
