package com.koolbao.saler.wxapi;

import java.util.HashMap;
import java.util.Map;

import com.koolbao.saler.utils.HttpTaskUtils;
import com.koolbao.saler.utils.PunchASyncTask;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	public static final String WEIXIN_APP_ID = "wx70ffff1127732857";
	public static String current_id;
	// private static final String WEIXIN_APP_KEY =
	// "8792dc1b7af5b91217eee1f60545d959";
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 注册微信sdk
		api = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID, true);
		api.registerApp(WEIXIN_APP_ID);
		api.handleIntent(getIntent(), this);
	}

	/**
	 * 微信发送请求到第三方应用时，会回调到该方法
	 */
	@Override
	public void onReq(BaseReq req) {
//		System.out.println(req.getType());
	}

	/**
	 * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	 */
	@Override
	public void onResp(BaseResp resp) {
		String result = "";
		Map<String, String> param = new HashMap<String, String>();
		param.put("g_id", current_id);
		param.put("type", "2");
		param.put("result", "0");
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK: // 分享成功
			result = "分享成功";
			param.put("result", "1");
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:// 取消分享
			result = "取消分享";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:// // 分享失败
			result = "分享失败";
			break;
		default:
			result = "分享失败";
			break;
		}
		new PunchASyncTask(HttpTaskUtils.SHARE_PUNCH, param).execute();
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}

}
