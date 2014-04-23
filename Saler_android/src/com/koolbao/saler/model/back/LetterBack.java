package com.koolbao.saler.model.back;

import java.util.List;

import com.koolbao.saler.model.Letter;

public class LetterBack {
	public int page_size;
	public int pages;
	public int page;
	public List<Letter> logs;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("page_size : " + page_size);
		sb.append("\npages : " + pages);
		sb.append("\npage : " + page);
		sb.append("\nlogs : " + logs);
		return sb.toString();
	}
}


