package com.koolbao.saler.widge;

import com.koolbao.saler.R;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

public class RecordEditText extends EditText {
	private TextView char_warn_tv;
	private int maxTextLength = 0;

	public RecordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setCharWarnTextView(TextView view, int max) {
		char_warn_tv = view;
		maxTextLength = max;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		String text = getText().toString();
		int length = text.length();
		if (maxTextLength > 0 && length > maxTextLength) {
			text = text.substring(0, maxTextLength);
			setText(text);
			length = text.length();
		}
		if (char_warn_tv != null) {
			char_warn_tv.setText("还可输入" + (maxTextLength - length) + "个字");
			if (maxTextLength - length < 10) {
				char_warn_tv.setTextColor(getContext().getResources().getColor(R.color.score_red));
			} else {
				char_warn_tv.setTextColor(getContext().getResources().getColor(R.color.black));
			}
		}
		super.onDraw(canvas);
	}

}
