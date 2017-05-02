package com.fpl.myapp.activity;

import com.fpl.myapp2.R;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.GreenDaoHelper;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import de.greenrobot.dao.async.AsyncSession;

/**
 * 开机引导界面
 *
 */
public class SplashScreenActivity extends Activity {

	private int SPLASH_TIME_OUT = 2000;
	private Handler mHandle = new Handler();;
	private Context context;
	Runnable getItem = new Runnable() {
		@Override
		public void run() {
			HttpUtil.getItemInfo(context);
		}
	};

	Runnable getstuInfo = new Runnable() {
		@Override
		public void run() {
			HttpUtil.getStudentInfo(context);
		}
	};
	Runnable getstuItem = new Runnable() {
		@Override
		public void run() {
			HttpUtil.getStudentItemInfo(context);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		context = this;

		// 调用NetUtil中的网络判断方法
		boolean result = NetUtil.netState(context);
		isWifiConnected(result);

	}

	private void isWifiConnected(boolean result) {
		if (true == result) {
			if (DbService.getInstance(context).loadAllStudentItem().isEmpty()) {

//				mHandle.post(getItem);
//				mHandle.post(getstuInfo);
//				mHandle.post(getstuItem);
				HttpUtil.getStudentItemInfo(context);
				if (HttpUtil.okFlag == 3000) {
					SPLASH_TIME_OUT = 2000;
				} else {
					SPLASH_TIME_OUT = 20000;
				}
			} else {
				SPLASH_TIME_OUT = 2000;
			}
			handlePost();
		} else {
			if (DbService.getInstance(context).loadAllStudentItem().isEmpty()) {
				Toast.makeText(context, "提示：当前未连接网路，相关数据下载失败", Toast.LENGTH_SHORT).show();
			}
			handlePost();
		}

	}

	private void handlePost() {
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}
