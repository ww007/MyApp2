package com.fpl.myapp.activity;

import com.fpl.myapp2.R;

import org.apache.http.protocol.HTTP;

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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import de.greenrobot.dao.async.AsyncSession;

/**
 * ������������
 *
 */
public class SplashScreenActivity extends Activity {

	private static Handler mHandle = new Handler();;
	private Context context;
	private int SPLASH_TIME_OUT = 2000;
	public static ProgressBar pbSplash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		context = this;

		pbSplash = (ProgressBar) findViewById(R.id.pb_splash);
		// ����NetUtil�е������жϷ���
		boolean result = NetUtil.netState(context);
		isWifiConnected(result);

	}

	// ���������̵߳�Handler,�����������е���Ϣ��Message��

	private void isWifiConnected(boolean result) {
		if (true == result) {
			if (DbService.getInstance(context).getStudentItemsCount() == 0) {
				pbSplash.setVisibility(View.VISIBLE);
				HttpUtil.getItemInfo(context);
			}else {
				handlePost(SPLASH_TIME_OUT);
			}
			
		} else {
			if (DbService.getInstance(context).loadAllItem().isEmpty()) {
				Toast.makeText(context, "��ʾ����ǰδ������·�������������ʧ��", Toast.LENGTH_SHORT).show();
			}
			handlePost(2000);
		}

	}

	public static void handleUI(final int progress) {
		mHandle.post(new Runnable() {
			@Override
			public void run() {
				pbSplash.setProgress(progress);
			}
		});
	}

	private void handlePost(int time) {
		mHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
				finish();
			}
		}, time);
	}

}
