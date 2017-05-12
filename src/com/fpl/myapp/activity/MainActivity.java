package com.fpl.myapp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.help.HelpActivity;
import com.fpl.myapp.activity.information.ICInformationActivity;
import com.fpl.myapp.activity.manage.SystemManagementActivity;
import com.fpl.myapp.activity.online.OnlineActivity;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.wnb.android.nfc.dataobject.entity.ItemProperty;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import ww.greendao.dao.RoundResult;
import android.widget.AdapterView.OnItemClickListener;
import de.greenrobot.dao.async.AsyncSession;

public class MainActivity extends Activity {
	private int[] icon = { R.drawable.main_projects_selector, R.drawable.main_online_selector,
			R.drawable.main_iccard_selector, R.drawable.main_manager_selector, R.drawable.main_help_selector,
			R.drawable.main_quit_selector };
	private String[] iconname = { "项目选择", "计算机联机", "测试卡信息", "系统管理", "帮助", "退出" };
	private GridView gvMain;
	private ArrayList<Map<String, Object>> dataList;
	private SimpleAdapter simAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
		setListeners();

	}

	private void initView() {
		gvMain = (GridView) findViewById(R.id.gv_main);
		// 新建list
		dataList = new ArrayList<Map<String, Object>>();
		// 获取数据
		getData();
		// 新建适配器
		String[] from = { "image", "text" };
		int[] to = { R.id.image, R.id.text };
		simAdapter = new SimpleAdapter(this, dataList, R.layout.item, from, to);
		// 配置适配器
		gvMain.setAdapter(simAdapter);
	}

	private void setListeners() {
		gvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(MainActivity.this, ProjectSelectionActivity.class));
					break;
				case 1:
					startActivity(new Intent(MainActivity.this, OnlineActivity.class));
					break;
				case 2:
					startActivity(new Intent(MainActivity.this, ICInformationActivity.class));
					break;
				case 3:
					startActivity(new Intent(MainActivity.this, SystemManagementActivity.class));
					break;
				case 4:
					startActivity(new Intent(MainActivity.this, HelpActivity.class));
					break;
				case 5:
					finish();
					// close();
					break;
				default:
					break;
				}
			}

		});
	}

	/**
	 * 关机，需root权限
	 */
	private void close() {
		try {
			Log.v("", "root Runtime->shutdown");
			// Process proc =Runtime.getRuntime().exec(new
			// String[]{"su","-c","shutdown"}); //关机
			Process proc = Runtime.getRuntime().exec(new String[] { "su", "-c", "reboot -p" }); // 关机
			proc.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ADBShell adbShell = new ADBShell();
		adbShell.simulateKey(KeyEvent.KEYCODE_POWER);
		Instrumentation mInst = new Instrumentation();
		mInst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP);
	}

	public List<Map<String, Object>> getData() {

		for (int i = 0; i < icon.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", icon[i]);
			map.put("text", iconname[i]);
			dataList.add(map);
		}

		return dataList;
	}

}
