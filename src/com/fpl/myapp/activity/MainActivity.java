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
import com.fpl.myapp.activity.project.ProjectSelectionActivity;
import com.fpl.myapp.base.NFCActivity;
import com.wnb.android.nfc.dataobject.entity.ItemProperty;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends NFCActivity {
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

	@Override
	public void onNewIntent(Intent intent) {
		initICCard(intent);
	}

	/**
	 * 初始化IC卡
	 * 
	 * @param intent
	 */
	private void initICCard(Intent intent) {
		try {
			IItemService service = new NFCItemServiceImpl(intent);
			initProject(service);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 初始化项目信息
	 * 
	 * @param service
	 */
	private void initProject(IItemService service) {

		try {
			Student student = new Student("L4403052004011421F8", "许星辰", 1, 6, 16, "2017-02-16");
			boolean isStu = service.IC_WriteStuInfo(student);
			Log.i("StudentTest===========", isStu + "");

			List<ItemProperty> itemProperties = new ArrayList<ItemProperty>();
			ItemProperty itemHW = new ItemProperty(1, 0, 0, 0, 0);
			itemProperties.add(itemHW);
			ItemProperty itemFHL = new ItemProperty(2, 0, 0, 0, 0);
			itemProperties.add(itemFHL);
			ItemProperty itemLDTY = new ItemProperty(3, 0, 0, 0, 0);
			itemProperties.add(itemLDTY);
			ItemProperty itemMG = new ItemProperty(4, 0, 0, 0, 0);
			itemProperties.add(itemMG);
			ItemProperty itemFWC = new ItemProperty(5, 0, 0, 0, 0);
			itemProperties.add(itemFWC);
			ItemProperty itemYWQZ = new ItemProperty(6, 0, 0, 0, 0);
			itemProperties.add(itemYWQZ);
			ItemProperty itemZWTQQ = new ItemProperty(7, 0, 0, 0, 0);
			itemProperties.add(itemZWTQQ);
			ItemProperty itemTS = new ItemProperty(8, 0, 0, 0, 0);
			itemProperties.add(itemTS);
			ItemProperty itemSL = new ItemProperty(9, 0, 0, 0, 0);
			itemProperties.add(itemSL);
			ItemProperty itemYTXS = new ItemProperty(10, 0, 0, 0, 0);
			itemProperties.add(itemYTXS);
			ItemProperty itemHWSXQ = new ItemProperty(11, 0, 0, 0, 0);
			itemProperties.add(itemHWSXQ);
			ItemProperty itemZCP = new ItemProperty(12, 0, 0, 0, 0);
			itemProperties.add(itemZCP);
			ItemProperty itemPQ = new ItemProperty(13, 0, 0, 0, 0);
			itemProperties.add(itemPQ);
			ItemProperty itemLQYQ = new ItemProperty(14, 0, 0, 0, 0);
			itemProperties.add(itemLQYQ);
			ItemProperty itemZFP = new ItemProperty(15, 0, 0, 0, 0);
			itemProperties.add(itemZFP);
			ItemProperty item50M = new ItemProperty(18, 0, 0, 0, 0);
			itemProperties.add(item50M);
			boolean isProperty = service.IC_WriteProperty(itemProperties);
			Log.i("WpropertyTest==========", isProperty + "");
		} catch (Exception e) {
			e.printStackTrace();
		}

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
