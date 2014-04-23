package com.koolbao.saler.widge;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 3-4-4分割电话号码控件
 * @author KuMa
 *
 */
public class PhoneEditText extends EditText {

	public PhoneEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		String text = getText().toString();
		if (text.length() > 3) {
			//第3位号码后面添加一个空格符
			if (text.charAt(3) != ' ') {
				text = text.substring(0, 3) + " " + text.substring(3, text.length());
			}
		}
		if (text.length() > 8) {
			//第7位号码后面添加一个空格符
			if (text.charAt(8) != ' ') {
				text = text.substring(0, 8) + " " + text.substring(8, text.length());
			}
		}
		//号码长度超过11位，自动舍弃后面的串
		if (text.length() > 13) {
			text = text.substring(0, 13);
		}
		//判断是否进行过补全处理
		if (!getText().toString().equals(text)) {
			setText(text);
			setSelection(text.length());
		}
		super.onDraw(canvas);
	}
}
