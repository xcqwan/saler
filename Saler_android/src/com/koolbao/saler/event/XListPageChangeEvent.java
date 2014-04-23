package com.koolbao.saler.event;

public class XListPageChangeEvent {
	public final int total_page;
	public final int current_page;
	
	public XListPageChangeEvent(int total_page, int current_page) {
		this.total_page = total_page;
		this.current_page = current_page;
	}
}
