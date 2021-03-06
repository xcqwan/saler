package com.koolbao.saler.utils;

import com.koolbao.saler.R;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

public class WidgeCommon {
	/**
	 * 更改搜索框默认搜索图标
	 * @param sv
	 * 		搜索框
	 * @param srid
	 * 		图片资源
	 */
	public static void changeSearchViewDefaultIcon(SearchView sv, int srid) {
		int searchIconID = sv.getContext().getResources().getIdentifier("android:id/search_mag_icon", null, null);
		ImageView searchIcon = (ImageView) sv.findViewById(searchIconID);
		searchIcon.setImageResource(srid);
	}
	
	/**
	 * 更改搜索框默认输入框的样式
	 * @param sv
	 * 		搜索框
	 */
	public static void changeSearchViewDefaultEditStyle(SearchView sv) {
		int searchLinearID = sv.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
		LinearLayout linear = (LinearLayout) sv.findViewById(searchLinearID);
		linear.setBackgroundResource(R.color.white);
	}
	
	/**
     * 转换图片成圆形
     * @param bitmap 传入Bitmap对象
     * @return
     */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}
}
