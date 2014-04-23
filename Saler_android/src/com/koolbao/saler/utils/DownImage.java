package com.koolbao.saler.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.koolbao.saler.memrycache.ImageFileCache;
import com.koolbao.saler.memrycache.ImageMemoryCache;

/**
 * 异步加载网络图片
 * @author KuMa
 *
 */
public class DownImage extends AsyncTask<String, Void, Bitmap> {
	private static ImageMemoryCache memoryCache;
	private static ImageFileCache fileCache;
	private ImageView imgView;
	private String imgUrl;
	
	public static DownImage with(Context context) {
		return new DownImage(context);
	}
	
	public DownImage(Context context) {
		if (memoryCache == null) {
			memoryCache = new ImageMemoryCache(context);
		}
		if (fileCache == null) {
			fileCache = new ImageFileCache();
		}
	}
	
	public DownImage load(String url) {
		this.imgUrl = url;
		return this;
	}
	
	public void into(ImageView iamgeView) {
		this.imgView = iamgeView;
		this.execute(imgUrl);
	}

	protected Bitmap doInBackground(String... utls) {
		try {
			String url = utls[0];
			// 从内存缓存中获取图片
		    Bitmap bitmap = memoryCache.getBitmapFromCache(url);
		    if (bitmap == null) {
		        // 文件缓存中获取
		    	bitmap = fileCache.getImage(url);
		        if (bitmap == null) {
		            // 从网络获取
		        	try {
		    			bitmap = safeDecodeStream(new URL(url), 80, 80);
		    		} catch (Exception e) {
		    			e.printStackTrace();
		    		}
		            if (bitmap != null) {
		                fileCache.saveBitmap(bitmap, url);
		                memoryCache.addBitmapToCache(url, bitmap);
		            }
		        } else {
		            // 添加到内存缓存
		            memoryCache.addBitmapToCache(url, bitmap);
		        }
		    }
		    
		    return WidgeCommon.toRoundBitmap(bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Bitmap result) {
		if (null == result) {
			return;
		}
		imgView.setImageBitmap(result);
	}
	
	protected Bitmap safeDecodeStream(URL url, int width, int height)  throws MalformedURLException, IOException{  
        int scale = 1;  
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        InputStream is = url.openStream();
        
        if(width>0 || height>0){  
            // Decode image size without loading all data into memory  
            options.inJustDecodeBounds = true;  
            BitmapFactory.decodeStream(is, null, options);  
              
            int w = options.outWidth;  
            int h = options.outHeight;  
            while (true) {  
                if ((width>0 && w/2 < width) || (height>0 && h/2 < height)){  
                    break;  
                }
                w /= 2;
                h /= 2;
                scale *= 2;  
            }  
        }  
  
        // Decode with inSampleSize option  
        is = url.openStream();
        options.inJustDecodeBounds = false;  
        options.inSampleSize = scale;  
        bitmap = BitmapFactory.decodeStream(is, null, options);
        return bitmap;
    }     
}
