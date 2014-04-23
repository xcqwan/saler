package com.koolbao.saler.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.koolbao.saler.R;
import com.koolbao.saler.adapter.DiagnoseAdapter;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.widge.CustomProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiagnosePageFragment extends Fragment {
	private Context mContext;
	private View view;
	private ListView diagnose_lv;
	private TextView score_tv;
	private TextView health_tv;
	
	private DiagnoseAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.page_diagnose, null);
		mContext = inflater.getContext();
		
		initCustom();
		initList();
		return view;
	}

	private void initList() {
		List<String> data = new ArrayList<String>();
		if (UserInfoUtils.diagnose_list != null) {
			List<String> tabledata = HttpTaskUtils.parseDataToListArray(UserInfoUtils.diagnose_list);
			for (String item : tabledata) {
				if (StringUtils.isBlank(item)) {
					continue;
				}
				data.add(item);
			}
			int score = Integer.valueOf(UserInfoUtils.diagnose_score);
			score_tv.setText(score + "");
			if (score > 80) {
				health_tv.setText("健康");
			} else if (score > 60) {
				health_tv.setText("亚健康");
			} else {
				health_tv.setText("不健康");
			}
		}
		
		adapter = new DiagnoseAdapter(this.getActivity(), data);
		diagnose_lv.setAdapter(adapter);
		
		new DiagnoseASync().execute();
	}

	private void initCustom() {
		diagnose_lv = (ListView) view.findViewById(R.id.diagnose_lv);
		score_tv = (TextView) view.findViewById(R.id.score_tv);
		health_tv = (TextView) view.findViewById(R.id.health_tv);
		
		diagnose_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
	}
	
	class DiagnoseASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			CustomProgressDialog.createDialog(mContext).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", UserInfoUtils.id);
			param.put("shop_id", UserInfoUtils.shop_id);
			try {
				Thread.sleep(500);
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.SHOP_DIAGNOSE, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(mContext, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				String scoreJson = result.get("score").toString();
				String tableDataJson = result.get("tabledata").toString();
				//存到shared
				UserInfoUtils.saveDiagnose(mContext.getSharedPreferences(UserInfoUtils.FileName, Activity.MODE_PRIVATE), scoreJson, tableDataJson);
				int score = Integer.valueOf(scoreJson);
				score_tv.setText(score + "");
				if (score > 80) {
					health_tv.setText("健康");
				} else if (score > 60) {
					health_tv.setText("亚健康");
				} else {
					health_tv.setText("不健康");
				}
				List<String> tabledata = HttpTaskUtils.parseDataToListArray(tableDataJson);
				if (!tabledata.isEmpty()) {
					List<String> data = new ArrayList<String>();
					for (String item : tabledata) {
						if (StringUtils.isBlank(item)) {
							continue;
						}
						data.add(item);
					}
					adapter = new DiagnoseAdapter(mContext, data);
					diagnose_lv.setAdapter(adapter);
				}
			} else {
				Toast.makeText(mContext, "获取诊断数据失败", Toast.LENGTH_SHORT).show();
			}
			
		}
	}
}
