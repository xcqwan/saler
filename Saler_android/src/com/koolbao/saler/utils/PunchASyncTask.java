package com.koolbao.saler.utils;

import java.util.Map;

import android.os.AsyncTask;

public class PunchASyncTask extends AsyncTask<Void, Void, Void> {
	private String url;
	private Map<String, String> param;
	
	public PunchASyncTask(String url, Map<String, String> param) {
		this.url = url;
		this.param = param;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			HttpTaskUtils.requestByHttpPost(url, param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
