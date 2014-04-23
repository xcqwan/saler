package com.koolbao.saler.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ContentAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> pagerItemList;
	
	public ContentAdapter(FragmentManager fm, ArrayList<Fragment> pagerItemList) {
		super(fm);
		this.pagerItemList = pagerItemList;
	}

	@Override
	public int getCount() {
		return pagerItemList.size();
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		if (position < pagerItemList.size())
			fragment = pagerItemList.get(position);
		else
			fragment = pagerItemList.get(0);
		return fragment;
	}
}
