package com.koolbao.saler.model.back;

import java.util.List;

import com.koolbao.saler.model.Banner;

public class BannerBack {
	public List<Banner> tabledata;
	
	@Override
	public String toString() {
		return tabledata.toString();
	}
}
