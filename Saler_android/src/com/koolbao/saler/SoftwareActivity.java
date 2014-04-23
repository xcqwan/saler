package com.koolbao.saler;

import android.os.Bundle;

public class SoftwareActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_software);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.software_title);
	}
}
