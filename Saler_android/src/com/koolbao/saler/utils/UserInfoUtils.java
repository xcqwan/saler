package com.koolbao.saler.utils;

import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserInfoUtils {
	public static final String FileName = "iseller_userinfo";
	public static final String CompleteName = "complete";
	//绑定店铺ID
	public static String shop_id = null;
	//绑定用户ID
	public static String user_id = null;
	//绑定店铺名称
	public static String store_title = null;
	//用户ID
	public static String id = null;
	//用户号码
	public static String tel = null;
	//用户昵称
	public static String user_nick = null;
	//用户头像
	public static String img_url = null;
	//banner
	public static String banner_list = null;
	//diagnose
	public static String diagnose_list = null;
	public static String diagnose_score = null;
	
	public static void init(SharedPreferences sharedPerfeencesr) {
		shop_id = sharedPerfeencesr.getString("store_id", null);
		user_id = sharedPerfeencesr.getString("visitor_id", null);
		store_title = sharedPerfeencesr.getString("store_title", null);
		id = sharedPerfeencesr.getString("id", null);
		tel = sharedPerfeencesr.getString("tel", null);
		user_nick = sharedPerfeencesr.getString("user_nick", null);
		img_url = sharedPerfeencesr.getString("img_url", null);
		banner_list = sharedPerfeencesr.getString("banner_list", null);
		diagnose_list = sharedPerfeencesr.getString("diagnose_list", null);
		diagnose_score = sharedPerfeencesr.getString("diagnose_score", null);
	}
	
	public static void saveToShared(SharedPreferences sharedPerfeencesr, Map<String, Object> data) {
		Editor edit = sharedPerfeencesr.edit();
		for (String key : data.keySet()) {
			edit.putString(key, data.get(key).toString());
		}
		edit.commit();
	}
	
	public static void saveToSharedMapString(SharedPreferences sharedPerfeencesr, Map<String, String> data) {
		Editor edit = sharedPerfeencesr.edit();
		for (String key : data.keySet()) {
			edit.putString(key, data.get(key));
		}
		edit.commit();
	}
	
	public static void saveBannerList(SharedPreferences sharedPerfeencesr, String list) {
		Editor edit = sharedPerfeencesr.edit();
		edit.putString("banner_list", list);
		edit.commit();
		banner_list = list;
	}
	
	public static void clearUserShared(SharedPreferences sharedPerfeencesr) {
		Editor edit = sharedPerfeencesr.edit();
		edit.remove("store_id");
		edit.remove("visitor_id");
		edit.remove("store_title");
		edit.remove("id");
		edit.remove("tel");
		edit.remove("img_url");
		edit.remove("user_nick");
		edit.remove("diagnose_list");
		edit.remove("diagnose_score");
		edit.commit();
	}

	public static void saveDiagnose(SharedPreferences sharedPerfeencesr, String scoreJson, String tableDataJson) {
		Editor edit = sharedPerfeencesr.edit();
		edit.putString("diagnose_list", tableDataJson);
		edit.putString("diagnose_score", scoreJson);
		edit.commit();
		diagnose_list = tableDataJson;
		diagnose_score = scoreJson;
	}

	public static void clearBinding(SharedPreferences sharedPerfeencesr) {
		Editor edit = sharedPerfeencesr.edit();
		edit.remove("store_id");
		edit.remove("visitor_id");
		edit.remove("store_title");
		edit.commit();
	}
}
