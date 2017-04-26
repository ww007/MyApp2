package com.fpl.myapp.activity.manage;

import com.fpl.myapp2.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

public class SystemManagementActivity extends Activity {

	private ListView lv;
	private String[] data = { "WLAN", "项目设置", "日期和时间", "存储", "关于本机" };
	private ImageButton ibQuit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_management);

		initView();
		setListener();
	}

	private void initView() {
		ibQuit = (ImageButton) findViewById(R.id.ib_system_quit);
		lv = (ListView) findViewById(R.id.lv_system_manager);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,
				data);
		lv.setAdapter(adapter);
	}

	private void setListener() {
		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					if (android.os.Build.VERSION.SDK_INT > 10) {
						startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
					} else {
						startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					}
					break;
				case 1:
					startActivity(new Intent(SystemManagementActivity.this, ProjectManagerActivity.class));
					break;
				case 2:
					startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
					break;
				case 3:
					startActivity(new Intent(android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
					break;
				case 4:
					break;
				default:
					break;
				}

			}
		});

	}

}
