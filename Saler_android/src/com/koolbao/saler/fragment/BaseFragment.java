package com.koolbao.saler.fragment;

import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

public class BaseFragment extends Fragment {
	Map<String, Object> reqest_msg = null;
	Map<String, Object> reqest_msg_total = null;
	
	public void syncOperate(final String path, final View view, final Runnable run, final Map<String, String> param) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					if (path.equals(HttpTaskUtils.BAOBEI_DATA_TOTAL)) {
						reqest_msg_total = HttpTaskUtils.requestByHttpPost(path, param);
					} else {
						reqest_msg = HttpTaskUtils.requestByHttpPost(path, param);
					}
				} catch (Exception e) {
					Log.i("test", "网络链接失败");
					return;
				}
				view.post(run);
			}
		}).start();
	}
}
