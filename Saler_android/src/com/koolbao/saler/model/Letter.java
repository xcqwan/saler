package com.koolbao.saler.model;

import java.util.HashMap;
import java.util.Map;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "letter")
public class Letter extends Model {
	// 资讯ID
	@Column(name = "letter_id")
	public String id;
	// 标题
	@Column(name = "title")
	public String title;
	// 作者
	@Column(name = "author")
	public String author;
	//日期
	@Column(name = "date")
	public String date;
	//点赞
	@Column(name = "like")
	public String like;
	// 不赞
	@Column(name = "unlike")
	public String unlike;
	//是否点赞
	@Column(name = "user_like")
	public String user_like;
	//页面
	@Column(name = "url")
	public String url;
	
	public Map<String, String> asMap() {
		Map<String, String> result = new  HashMap<String, String>();
		result.put("id", id);
		result.put("title", title);
		result.put("author", author);
		result.put("date", date);
		result.put("like", like);
		result.put("unlike", unlike);
		result.put("user_like", user_like);
		result.put("url", url);
		
		return result;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id : " + id);
		sb.append(", title : " + title);
		sb.append(", author : " + author);
		sb.append(", date : " + date);
		sb.append("}");
		return sb.toString();
	}
}
