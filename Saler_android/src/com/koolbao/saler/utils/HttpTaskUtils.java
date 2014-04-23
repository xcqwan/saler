package com.koolbao.saler.utils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpTaskUtils {
	public static final String DAILY_DATA = "diagnose/get_wifi_zonglan_table";
	public static final String DAILY_FLASH = "diagnose/get_wifi_zonglan_flash";
	public static final String BAOBEI_DATA = "diagnose/get_baobei_rank_table";
	public static final String BAOBEI_DATA_TOTAL = "diagnose/get_baobei_rank_total";
	
	public static final String Requst_Fail = "500"; 
	public static final String Requst_Success = "200"; 
	
	public static final String BASE_URL = "http://shop.koolbao.com/";
	public static final String CHECK_TEL = "register/check_tel";
	public static final String REGIST_USER = "register/validate_users";
	public static final String LOGIN_USER = "register/login_user";
	public static final String INFO_USER = "user_center/get_user_infos";
	public static final String NICK_MODIFY = "user_center/update_users";
	public static final String GROWN_LIST = "growth/show_growth_list";
	public static final String GROWN_ADD = "growth/insert_growth";
	public static final String POST_LIST = "seller_bbs/show_articles";
	public static final String POST_SEARCH_LIST = "seller_bbs/search_article";
	public static final String FAVOR_LIST = "app/ajax_fav/get_fav_list";
	public static final String ADD_FAVOR = "app/ajax_fav/add_fav";
	public static final String CANCEL_FAVOR = "app/ajax_fav/un_fav";
	public static final String ADD_BBS = "seller_bbs/insert_artice";
	public static final String ADD_BBS_REPLY = "seller_bbs/reply_artice";
	public static final String BBS_CONTENT = "seller_bbs/get_artice_content";
	public static final String BBS_REPLY_LIST = "seller_bbs/show_reply_artices";
	public static final String USER_POST_LIST = "user_center/show_bbs";
	public static final String UPDATE_PSD = "register/update_password";
	public static final String BANNER_LIST = "banner_app/show_banners";
	public static final String SHOP_DIAGNOSE = "diagnose/show_diagnose";
	public static final String AUTHORIZE_SHOP = "author/binding_app";
	public static final String AUTHORIZE_BOND = "author/validate_user";
	public static final String UPLOAD_IMG = "user_center/do_upload";
	public static final String GET_VERSION = "author/get_android_version";
	public static final String SHARE_PUNCH = "app/share/index";
	
	public static final String NEWS_API = "http://shop.koolbao.com/app/ajax_blog/get_list/";
	public static final String NEWS_POST_API = "http://shop.koolbao.com/app/ajax_blog/get/";
	public static final String NEWS_LIKE = "http://shop.koolbao.com/app/ajax_blog/like";
	
	private static final String BOUNDARY =  UUID.randomUUID().toString(); // 边界标识 随机生成  
    private static final String PREFIX = "--";  
    private static final String LINE_END = "\r\n";  
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	
	/**
	 * HttpPost请求
	 * @param path
	 * 		访问地址
	 * @param param
	 * 		参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> requestByHttpPost(String path, Map<String, String> param) throws Exception {
		// 新建HttpPost对象
		HttpPost httpPost = new HttpPost(BASE_URL + path);
		// Post参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String key : param.keySet()) {	
			params.add(new BasicNameValuePair(key, param.get(key)));
		}
		// 设置字符集
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		// 设置参数实体
		httpPost.setEntity(entity);
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		 // 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		int try_times = 0;
		while (try_times < 3) {
			// 获取HttpResponse实例
			HttpResponse httpResp = httpClient.execute(httpPost);
			// 判断是够请求成功
			if (httpResp.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				HttpEntity respEntity = httpResp.getEntity();
				String resp = EntityUtils.toString(respEntity, "UTF-8");
				JSONObject json = new JSONObject(resp);
				Iterator<String> keys = json.keys();
				Map<String, Object> result = new HashMap<String, Object>();
				while (keys.hasNext()) {
					String key = keys.next();
					result.put(key, json.getString(key));
				}
				return result;
			} else {
				Log.i("test", "HttpPost方式请求失败" + httpResp.getStatusLine().getStatusCode());
				try_times ++;
			}
		}
		return null;
	}
	
	public static Map<String, Object> upLoadFileByPost(
			Map<String, String> param, File imageFile) throws Exception {
		String result = null;
		URL url = new URL(BASE_URL + UPLOAD_IMG);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000);
		conn.setConnectTimeout(10000);
		conn.setDoInput(true); // 允许输入流
		conn.setDoOutput(true); // 允许输出流
		conn.setUseCaches(false); // 不允许使用缓存
		conn.setRequestMethod("POST"); // 请求方式
		conn.setRequestProperty("Charset", "utf-8"); // 设置编码
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("user-agent",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
				+ BOUNDARY);

		/**
		 * 当文件不为空，把文件包装并且上传
		 */
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		StringBuffer sb = null;
		String params = "";

		/***
		 * 以下是用于上传参数
		 */
		if (param != null && param.size() > 0) {
			Iterator<String> it = param.keySet().iterator();
			while (it.hasNext()) {
				sb = null;
				sb = new StringBuffer();
				String key = it.next();
				String value = param.get(key);
				sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
				sb.append("Content-Disposition: form-data; name=\"")
						.append(key).append("\"").append(LINE_END)
						.append(LINE_END);
				sb.append(value).append(LINE_END);
				params = sb.toString();
				dos.write(params.getBytes());
			}
		}

		sb = null;
		params = null;
		sb = new StringBuffer();
		/**
		 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
		 * 比如:abc.png
		 */
		sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
		sb.append("Content-Disposition:form-data; name=\"userfile\"; filename=\""
				+ imageFile.getName() + "\"" + LINE_END);
		sb.append("Content-Type:image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的
															// ，用于服务器端辨别文件的类型的
		sb.append(LINE_END);
		params = sb.toString();
		sb = null;
		dos.write(params.getBytes());
		/** 上传文件 */
		InputStream is = new FileInputStream(imageFile);
		byte[] bytes = new byte[1024];
		int len = 0;
		while ((len = is.read(bytes)) != -1) {
			dos.write(bytes, 0, len);
		}
		is.close();

		dos.write(LINE_END.getBytes());
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
		dos.write(end_data);
		dos.flush();
		//
		// dos.write(tempOutputStream.toByteArray());
		/**
		 * 获取响应码 200=成功 当响应成功，获取响应的流
		 */
		int res = conn.getResponseCode();
		if (res == 200) {
			InputStream input = conn.getInputStream();
			StringBuffer sb1 = new StringBuffer();
			int ss;
			while ((ss = input.read()) != -1) {
				sb1.append((char) ss);
			}
			result = sb1.toString();
			return parseTableDataToMap(result);
		} else {
			Log.i("test", "HttpPost方式请求失败" + res);
		}
		return null;
	}
	
	/**
	 * Get请求
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> requestByHttpGet(String path) throws Exception {
		// 新建HttpPost对象
		HttpGet httpGet = new HttpGet(path);
		// 获取HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		 // 请求超时
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        // 读取超时
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		int try_times = 0;
		while (try_times < 3) {
			// 获取HttpResponse实例
			HttpResponse httpResp = httpClient.execute(httpGet);
			// 判断是够请求成功
			if (httpResp.getStatusLine().getStatusCode() == 200) {
				// 获取返回的数据
				HttpEntity respEntity = httpResp.getEntity();
				String resp = EntityUtils.toString(respEntity, "UTF-8");
				JSONObject json = new JSONObject(resp);
				Iterator<String> keys = json.keys();
				Map<String, Object> result = new HashMap<String, Object>();
				while (keys.hasNext()) {
					String key = keys.next();
					result.put(key, json.getString(key));
				}
				return result;
			} else {
				Log.i("test", "HttpPost方式请求失败" + httpResp.getStatusLine().getStatusCode());
				try_times ++;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseTableDataToMap(String json) {
		if (json.equals("[]")) {
			return new HashMap<String, Object>();
		}
		while (json.startsWith("[")) {
			json = json.substring(1, json.length() - 1);
		}
		try {
			JSONObject jsonobj = new JSONObject(json);
			Iterator<String> keys = jsonobj.keys();
			Map<String, Object> result = new HashMap<String, Object>();
			while (keys.hasNext()) {
				String key = keys.next();
				result.put(key, jsonobj.getString(key));
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("test", "JSON转换MAP失败");
		}
		
		return new HashMap<String, Object>();
	}
	
	public static Map<String, JSONArray> parseListDataToJSONArray(String json) {
		try {
			JSONArray keyArray = new JSONArray();
			JSONArray valueArray = new JSONArray();
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				String item = jsonArray.get(i).toString();
				JSONObject itemobj = new JSONObject(item);
				keyArray.put(itemobj.get("key"));
				valueArray.put(itemobj.get("value"));
			}
			Map<String, JSONArray> result = new HashMap<String, JSONArray>();
			result.put("date", keyArray);
			result.put("value", valueArray);
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
			Log.i("test", "JSON转换Array失败");
		}
		return new HashMap<String, JSONArray>();
	}
	
	public static JSONArray parseDataToJS(JSONArray array) {
		try {
			JSONArray result = new JSONArray();
			for (int i = 0; i < array.length(); i++) {
				String value = array.getString(i);
				if (value.startsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				result.put(Double.valueOf(value));
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return array;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, String>> parseDataToListdata(String json) {
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		if (json.equals("[]") || !json.startsWith("[")) {
			return result;
		}
		try {
			JSONArray arraylist = new JSONArray(json);
			for (int i = 0; i < arraylist.length(); i++) {
				String itemJson = arraylist.getString(i);
				JSONObject itemObj = new JSONObject(itemJson);
				Iterator<String> keys = itemObj.keys();
				Map<String, String> itemMap = new HashMap<String, String>();
				while (keys.hasNext()) {
					String key = keys.next();
					itemMap.put(key, itemObj.getString(key));
				}
				result.add(itemMap);
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static List<String> parseDataToListArray(String json) {
		List<String> result = new ArrayList<String>();
		if (json.equals("[]") || !json.startsWith("[")) {
			return result;
		}
		try {
			JSONArray arraylist = new JSONArray(json);
			for (int i = 0; i < arraylist.length(); i++) {
				String itemJson = arraylist.getString(i);
				result.add(itemJson);
			}
			return result;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
}
