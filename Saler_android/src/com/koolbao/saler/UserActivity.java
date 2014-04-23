package com.koolbao.saler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.koolbao.saler.utils.DownImage;
import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.UserInfoUtils;
import com.koolbao.saler.utils.WidgeCommon;
import com.koolbao.saler.widge.CustomProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

public class UserActivity extends BaseActivity implements OnClickListener {
	public static final int REQUEST_NICK = 1;
	public static final int REQUEST_CAMERA = 2;
	public static final int REQUEST_PHOTO = 3;
	public static final int REQUEST_AUTHORIZE = 4;
	public static final int PHOTO_REQUEST_CUT = 5;
	private LinearLayout user_shop_linear;
	private LinearLayout user_nick_linear;
	private ImageView user_img;
	private TextView user_phone_tv;
	private TextView user_nick_tv;
	private TextView user_post_tv;
	private TextView user_shop_tv;
	
	private TextView about_us_tv;
	private Button cancel_login_btn;
	
	private Bitmap myBitmap;
	
	private SharedPreferences sharePrefer;
	// 创建一个以当前时间为名称的文件
    File tempFile = new File(Environment.getExternalStorageDirectory(), "header.jpg");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_user);
		super.onCreate(savedInstanceState);
		
		setTitleBarTxt(R.string.user_title);
		initCustom();
		initListener();
		initData();
		
		new LoadInfoASync().execute();
	}
	
	private void initData() {
		String tel = UserInfoUtils.tel;
		String user_nick = UserInfoUtils.user_nick;
		String img_url = UserInfoUtils.img_url;
		String store_title = UserInfoUtils.store_title;
		user_phone_tv.setText(tel);
		if (user_nick != null) {
			user_nick_tv.setText(user_nick);
		}
		if (img_url != null && !img_url.isEmpty()) {
			DownImage.with(this).load(img_url).into(user_img);
		}
		if (store_title != null) {
			user_shop_tv.setText(store_title);
			user_shop_linear.setClickable(false);
		} else {
			user_shop_tv.setText(R.string.user_shop_demo_text);
			user_shop_linear.setClickable(true);
		}
	}

	private void initListener() {
		user_shop_linear.setOnClickListener(this);
		user_nick_linear.setOnClickListener(this);
		cancel_login_btn.setOnClickListener(this);
		user_post_tv.setOnClickListener(this);
		user_img.setOnClickListener(this);
		about_us_tv.setOnClickListener(this);
	}

	private void initCustom() {
		user_shop_linear = (LinearLayout) findViewById(R.id.user_shop_linear);
		user_nick_linear = (LinearLayout) findViewById(R.id.user_nick_linear);
		user_img = (ImageView) findViewById(R.id.user_img);
		user_phone_tv = (TextView) findViewById(R.id.user_phone_tv);
		user_nick_tv = (TextView) findViewById(R.id.user_nick_tv);
		user_post_tv = (TextView) findViewById(R.id.user_post_tv);
		user_shop_tv = (TextView) findViewById(R.id.user_shop_tv);
		about_us_tv = (TextView) findViewById(R.id.about_us_tv);
		cancel_login_btn = (Button) findViewById(R.id.cancel_login_btn);
		
		sharePrefer = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
		UserInfoUtils.init(sharePrefer);
	}
	
	@Override
	protected void onDestroy() {
		if (myBitmap != null) {
			myBitmap = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		//用户头像
		if (v.getId() == user_img.getId()) {
			final CharSequence[] items = { "相册", "拍照" };
                AlertDialog dlg = new AlertDialog.Builder(this).setTitle("选择图片").setItems(items,
                                new DialogInterface.OnClickListener()
                                {
                                        public void onClick ( DialogInterface dialog , int item )
                                        {
                                                // 这里item是根据选择的方式，
                                                // 在items数组里面定义了两种方式，拍照的下标为1所以就调用拍照方法
                                                if (item == 1){
                                                        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                                                        // 指定调用相机拍照后照片的储存路径
                                                        getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
                                                        startActivityForResult(getImageByCamera, REQUEST_CAMERA);
                                                } else{
                                                        Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                                                        getImage.addCategory(Intent.CATEGORY_OPENABLE);
                                                        getImage.setType("image/jpeg");
                                                        startActivityForResult(getImage, REQUEST_PHOTO);
                                                }
                                        }
                                }).create();
                dlg.show();
		}
		//注销登录
		if (v.getId() == cancel_login_btn.getId()) {
			SharedPreferences shared = getSharedPreferences(UserInfoUtils.FileName, MODE_PRIVATE);
			UserInfoUtils.clearUserShared(shared);
			UserInfoUtils.init(shared);
			
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			this.finish();
		}
		//修改用户昵称
		if (v.getId() == user_nick_linear.getId()) {
			Intent intent = new Intent(this, NickModifyActivity.class);
			startActivityForResult(intent, REQUEST_NICK);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		//我的帖子
		if (v.getId() == user_post_tv.getId()) {
			Intent intent = new Intent(this, UserPostActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		//绑定店铺
		if (v.getId() == user_shop_linear.getId()) {
			Intent intent = new Intent(this, AuthorizeActivity.class);
			startActivityForResult(intent, REQUEST_AUTHORIZE);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
		//关于我是卖家
		if (v.getId() == about_us_tv.getId()) {
			Intent intent = new Intent(this, AboutUsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//授权成功回调
		if (requestCode == REQUEST_AUTHORIZE && resultCode == RESULT_OK) {
			new UserBondASync().execute();
		}
		//修改昵称回调
		if (requestCode == REQUEST_NICK && resultCode == RESULT_OK) {
			String new_nick = data.getStringExtra("nick");
			user_nick_tv.setText(new_nick);
		}
		//摄像头获取头像回调
		if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
			startPhotoZoom(Uri.fromFile(tempFile), 125);
		}
		// 相册获取头像回调
		if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				startPhotoZoom(originalUri, 125);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//图片裁剪回调
		if (requestCode == PHOTO_REQUEST_CUT && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			myBitmap = (Bitmap) extras.get("data");
			
			myBitmap = WidgeCommon.toRoundBitmap(myBitmap);
			uploadBitmap(myBitmap);
		}
	}
	
	/**
	 * 调用图片剪裁
	 * @param uri
	 * @param size
	 */
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}
	
	/**
	 * 上传图片
	 * @param bitmap
	 */
	private void uploadBitmap(Bitmap bitmap) {
		FileOutputStream fos = null;  
		BufferedOutputStream bos = null;  
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();  
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
		    byte[] byteArray = baos.toByteArray();
		    
		    if (!tempFile.exists()) {  
		    	tempFile.createNewFile();  
		    }  
		    fos = new FileOutputStream(tempFile);  
		    bos = new BufferedOutputStream(fos);  
		    bos.write(byteArray);
		    
		    String pictureDir = tempFile.getPath();
		    new UpLoadImgASync(bitmap).execute(pictureDir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    if (baos != null) {  
		        try {  
		            baos.close();  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		    }  
		    if (bos != null) {  
		        try {  
		            bos.close();  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		    }  
		    if (fos != null) {  
		        try {  
		            fos.close();  
		        } catch (Exception e) {  
		            e.printStackTrace();  
		        }  
		    }  
		}
	}
	
	/**
	 * 用户中心数据加载
	 * @author KuMa
	 *
	 */
	class LoadInfoASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(UserActivity.this, "获取用户信息...", Toast.LENGTH_SHORT).show();
			CustomProgressDialog.createDialog(UserActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			String id = UserInfoUtils.id;
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.INFO_USER, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(UserActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				Map<String, Object> user_info = HttpTaskUtils.parseTableDataToMap(result.get("tabledata").toString());
				UserInfoUtils.saveToShared(sharePrefer, user_info);
				UserInfoUtils.init(sharePrefer);
				initData();
				new UserBondASync().execute();
			} else {
				Toast.makeText(UserActivity.this, "获取用户信息失败", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	/**
	 * 用户绑定店铺信息获取
	 * @author KuMa
	 *
	 */
	class UserBondASync extends AsyncTask<Void, Void, Map<String, Object>> {
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(UserActivity.this, "获取用户绑定店铺信息", Toast.LENGTH_SHORT).show();
			CustomProgressDialog.createDialog(UserActivity.this).canceledOnTouchOutside().show();
		}
		
		@Override
		protected Map<String, Object> doInBackground(Void... params) {
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.requestByHttpPost(HttpTaskUtils.AUTHORIZE_BOND, param);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(UserActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			List<Map<String, String>> tabledata = HttpTaskUtils.parseDataToListdata(result.get("tabledata").toString());
			if (!tabledata.isEmpty()) {
				Map<String, String> shop_info = tabledata.get(0);
				//存起来
				UserInfoUtils.saveToSharedMapString(sharePrefer, shop_info);
				UserInfoUtils.init(sharePrefer);
				initData();
			} else {
				//无绑定信息
				UserInfoUtils.clearBinding(sharePrefer);
				UserInfoUtils.init(sharePrefer);
				initData();
			}
		}
	}
	
	/**
	 * 头像上传
	 * @author KuMa
	 *
	 */
	class UpLoadImgASync extends AsyncTask<String, Void, Map<String, Object>> {
		private Bitmap asyncBitmap;
		
		public UpLoadImgASync(Bitmap bitmap) {
			asyncBitmap = bitmap;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(UserActivity.this, "正在上传头像...", Toast.LENGTH_SHORT).show();
			CustomProgressDialog.createDialog(UserActivity.this).canceledOnTouchOutside().show();
		}

		@Override
		protected Map<String, Object> doInBackground(String... params) {
			String img_path = params[0];
			File imgFile = new File(img_path);
			Map<String, String> param = new HashMap<String, String>();
			param.put("user_id_app", UserInfoUtils.id);
			try {
				Map<String, Object> requst_msg = HttpTaskUtils.upLoadFileByPost(param, imgFile);
				return requst_msg;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Map<String, Object> result) {
			CustomProgressDialog.getDialog().cancel();
			if (result == null) {
				Toast.makeText(UserActivity.this, "网络请求失败, 请查看网络连接或者稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}
			String status = result.get("status").toString();
			if (status.equals(HttpTaskUtils.Requst_Success)) {
				// 把得到的图片绑定在控件上显示
				asyncBitmap = WidgeCommon.toRoundBitmap(asyncBitmap);
				user_img.setImageBitmap(asyncBitmap);
				Toast.makeText(UserActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(UserActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
