package com.koolbao.saler.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "banner")
public class Banner extends Model {
	// ID
	@Column(name = "content_id")
	public String content_id;
	// banner URL
	@Column(name = "image_url")
	public String image_url;
	// 类型 2.资讯/3.帖子
	@Column(name = "type")
	public String type;
	
	@Override
	public String toString() {
		return "content_id : " + content_id + ", image_url : " + image_url + ", type : " + type;
	}
}
