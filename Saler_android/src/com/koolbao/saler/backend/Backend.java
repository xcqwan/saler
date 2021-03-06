package com.koolbao.saler.backend;

import com.koolbao.saler.model.back.BannerBack;
import com.koolbao.saler.model.back.FeedBack;
import com.koolbao.saler.model.back.LetterBack;
import com.koolbao.saler.utils.HttpTaskUtils;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public class Backend {
	private static Backend singlton = null;
	private RestAdapter restAdapter;
	private Server serverInstance;
	
	public static Backend getInstance() {
		if (singlton == null) {
			singlton = new Backend();
		}
		return singlton;
	}
	
	private Backend() {
		restAdapter = new RestAdapter.Builder().setEndpoint(HttpTaskUtils.BASE_URL).build();
		restAdapter.setLogLevel(LogLevel.FULL);
		serverInstance = restAdapter.create(Server.class);
	}
	
	public void getLetters(int page, String user_id, Callback<LetterBack> callback) {
		if (user_id == null) {
			serverInstance.getLetters(page, callback);
		} else {
			serverInstance.getLetters(page, user_id, callback);
		}
	}
	
	public void getBannerList(Callback<BannerBack> callback) {
		serverInstance.getBannerList(callback);
	}
	
	public void postFeedBack(String user_id, String content, String phone_version, Callback<FeedBack> callback) {
		serverInstance.postFeedBack(user_id, content, phone_version, callback);
	}
	
	interface Server {
		@GET("/app/ajax_blog/get_list/{page}")
		void getLetters(@Path("page") int page, Callback<LetterBack> callback);
		
		@GET("/app/ajax_blog/get_list/{page}/{user_id}")
		void getLetters(@Path("page") int page, @Path("user_id") String user_id, Callback<LetterBack> callback);
		
		@GET("/banner_app/show_banners")
		void getBannerList(Callback<BannerBack> callback);
		
		@FormUrlEncoded
		@POST("/app/feedback/add_feedback")
		void postFeedBack(@Field("user_id") String user_id, @Field("content") String content, @Field("phone_version") String phone_version, Callback<FeedBack> callback);
	}
}
