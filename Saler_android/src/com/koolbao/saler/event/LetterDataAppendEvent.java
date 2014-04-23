package com.koolbao.saler.event;

import java.util.List;

import com.koolbao.saler.model.Letter;

public class LetterDataAppendEvent {
	public final boolean isAppend;
	public final List<Letter> data;
	
	public LetterDataAppendEvent(List<Letter> data, boolean isAppend) {
		this.isAppend = isAppend;
		this.data = data;
	}
	
}
