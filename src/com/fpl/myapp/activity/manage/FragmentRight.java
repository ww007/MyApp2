package com.fpl.myapp.activity.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.ProjectAdapter;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpCallbackListener;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import ww.greendao.dao.Item;

public class FragmentRight extends Fragment {
	private Context context;
	private SharedPreferences sharedPreferences;
	private String[] projectName = { "50米跑", "800/1000米跑", "身高体重", "肺活量", "立定跳远", "仰卧起坐", "坐位体前屈", "引体向上" };
	private ArrayList<Map<String, Object>> dataList = new ArrayList<>();
	private ArrayList<String> strings = new ArrayList<>();
	private ArrayList<String> nameData = new ArrayList<>();
	private ArrayList<String> names = new ArrayList<>();
	private ArrayList<String> names1 = new ArrayList<>();
	private List<Item> itemList = new ArrayList<>();
	private ArrayList<String> projects = new ArrayList<>();
	private List<Item> newList;
	private ListView lvProject;
	private ProjectAdapter adapter;
	private Button btn;

	Handler mHandler = new Handler();
	Runnable updateItem = new Runnable() {
		@Override
		public void run() {
			getItems();
		}
	};
	Runnable showView = new Runnable() {
		@Override
		public void run() {
			NetUtil.showToast(context, "服务器连接异常");
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_fragment_right, container, false);
		context = getActivity();

		// 获取保存在本地的选中项目
		sharedPreferences = context.getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		for (int i = 0; i < selected; i++) {
			strings.add(sharedPreferences.getString(i + "", ""));
			Log.i("strings=", strings.toString());
		}

		newList = DbService.getInstance(context).loadAllItem();
		for (int i = 0; i < newList.size(); i++) {
			names1.add(newList.get(i).getItemName());
		}

		initView(view);
		setListener();

		// getItems();
		return view;
	}

	private void getItems() {
		sendItemRequest();
		// 判断获取的项目是否更新
		if (nameData.size() == names.size()) {
			for (int i = 0; i < nameData.size(); i++) {
				if (nameData.get(i).toString().equals(names.get(i).toString())) {
					NetUtil.showToast(context, "已为最新项目");
				} else {
					Log.i("更新", "------------");
					dataList = getNewDate(names);
					showList();
				}
			}

		} else {
			Log.i("更新", "------------");
			dataList = getNewDate(names);
			showList();
		}
	}

	private CheckBox cb;

	private void setListener() {
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				isWifiConnected(NetUtil.netState(context));
			}
		});

		lvProject.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cb = (CheckBox) view.findViewById(R.id.cb_project);
				cb.toggle();
				ProjectAdapter.getIsSelected().put(position, cb.isChecked());
			}

		});
	}

	private void initView(View view) {
		lvProject = (ListView) view.findViewById(R.id.lv_project);
		btn = (Button) view.findViewById(R.id.btn_getInfo);

		removeName(names1);
		dataList = getNewDate(names1);
		showList();

	}

	private void showList() {

		Log.i("dataList=", dataList.toString());
		adapter = new ProjectAdapter(context, dataList);
		lvProject.setAdapter(adapter);
		for (int i = 0; i < dataList.size(); i++) {
			for (int j = 0; j < strings.size(); j++) {
				if (dataList.get(i).get("name").equals(strings.get(j))) {
					ProjectAdapter.getIsSelected().put(i, true);
				}
			}
		}
	}

	/**
	 * 根据返回值判断接下来的操作
	 * 
	 * @param result
	 */
	protected void isWifiConnected(boolean result) {

		if (true == result) {
			mHandler.post(updateItem);
		} else {
			NetUtil.checkNetwork(getActivity());
		}
	}

	/**
	 * 获取最新项目信息
	 */
	private void sendItemRequest() {

		try {
			HttpUtil.sendOkhttp(Constant.ITEM_URL, null, new HttpCallbackListener() {

				@Override
				public void onFinish(String response) {
					names.clear();
					// 解析获取的Json数据
					itemList = JSON.parseArray(response, Item.class);
					Log.i("item--->", itemList.get(0).getItemName());
					// 获取项目名字集合
					for (int j = 0; j < itemList.size(); j++) {
						names.add(itemList.get(j).getItemName());
					}
					if (DbService.getInstance(context).loadAllItem().isEmpty()) {
						DbService.getInstance(context).saveItemLists(itemList);
						Log.i("success", "保存项目信息成功");
					}
					removeName(names);
					Log.i("2.names=", names + "");
					nameData.clear();
					for (int i = 0; i < dataList.size(); i++) {
						nameData.add(dataList.get(i).get("name").toString());
						Log.i("nameData=", nameData + "");
					}
					HttpUtil.getStudentInfo(context);
				}

				@Override
				public void onError(Exception e) {
					Log.i("error", "数据下载失败");
				}
			});
		} catch (Exception e) {
			Log.i("error", "连接服务器异常");
			mHandler.post(showView);
		}
	}

	private ArrayList<Map<String, Object>> getNewDate(List<String> strings) {
		dataList.clear();
		for (int i = 0; i < strings.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("xuhao", i + 9);
			map.put("name", strings.get(i));
			dataList.add(map);
		}
		return dataList;
	}

	/**
	 * 移除项目集合中基本项目
	 * 
	 * @param stringList
	 */
	private void removeName(ArrayList<String> stringList) {
		for (int i = 0; i < projectName.length; i++) {
			stringList.remove(projectName[i]);
		}
		for (Iterator<String> it = stringList.iterator(); it.hasNext();) {
			String item = it.next();
			if (item.equals("身高") || item.equals("体重") || item.equals("一分钟仰卧起坐") || item.equals("1000米跑")
					|| item.equals("800米跑")) {
				it.remove();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 退出前获取选中项目
		for (int i = 0; i < dataList.size(); i++) {
			if (ProjectAdapter.getIsSelected().get(i)) {
				projects.add(dataList.get(i).get("name").toString());
			}
		}
		// SharedPreferences保存选中的项目信息
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("size", projects.size());
		for (int i = 0; i < projects.size(); i++) {
			editor.putString(i + "", projects.get(i));
		}
		editor.commit();
		Log.i("数据成功写入SharedPreferences！", editor + "");
	}
}
