package com.fpl.myapp.activity.information;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.activity.project.BroadJumpActivity;
import com.fpl.myapp.adapter.ICInfoAdapter;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.entity.ICInfo;
import com.fpl.myapp.util.Constant;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import ww.greendao.dao.Item;

public class ICInformationActivity extends NFCActivity {
	private ICInfoAdapter mAdapter;
	private ArrayList<ICInfo> icInfos;
	private String sex;
	private TextView tvNumber;
	private TextView tvName;
	private TextView tvGender;
	private ListView lvIcInfo;
	private TextView tvShow;
	private SharedPreferences sharedPreferences;

	public ArrayList<String> projects = new ArrayList<>();
	// private String[] newProject;
	// private String[] newValue;
	// private int number50, numberH, numberW, numberFHL, numberLDTY,
	// numberYWQZ, number800, number1000, numberFWC,
	// numberZWTQQ, numberTS, numberLSL, numberRSL, numberYTXS, numberMG,
	// numberPQ, numberHWSXQ, numberLQYQ,
	// numberZFP, numberTJZ, numberZQYQ, numberYY;
	private List<Item> items;
	private ImageButton ibQuit;
	private Logger log = Logger.getLogger(ICInformationActivity.class);

	Handler mHandler = new Handler();
	Runnable updateTv1 = new Runnable() {
		@Override
		public void run() {
			tvShow.setText("读取中...");
		}
	};
	Runnable updateTv2 = new Runnable() {
		@Override
		public void run() {
			tvShow.setText("读取完毕!");
		}
	};
	private ICInfo icInfo1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icinformation);
		// 读取本地存储的选中项目
		sharedPreferences = getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		Log.i("selected=", selected + "");

		icInfo1 = new ICInfo();

		initView();
		setListener();
	}

	@Override
	public void onNewIntent(final Intent intent) {
		tvShow.setText("读取中断，请重新刷卡！");
		readCard(intent);

	}

	private void readCard(Intent intent) {
		NFCItemServiceImpl itemService;
		try {
			icInfos.clear();
			itemService = new NFCItemServiceImpl(intent);
			Student student = itemService.IC_ReadStuInfo();
			Log.i("StudentTest===", student.toString());
			if (1 == student.getSex()) {
				sex = "男";
			} else {
				sex = "女";
			}

			tvGender.setText(sex);
			tvName.setText(student.getStuName().toString());
			tvNumber.setText(student.getStuCode().toString());

			readHW(itemService);
			readMiddleRun(itemService);
			readVision(itemService);
			readCommon(itemService, Constant.BASKETBALL_SKILL, "ms", "篮球运球");
			readCommon(itemService, Constant.BROAD_JUMP, "cm", "立定跳远");
			readCommon(itemService, Constant.FOOTBALL_SKILL, "ms", "足球运球");
			readCommon(itemService, Constant.INFRARED_BALL, "cm", "实心球");
			readCommon(itemService, Constant.JUMP_HEIGHT, "cm", "摸高");
			readCommon(itemService, Constant.KICKING_SHUTTLECOCK, "个", "踢毽子");
			readCommon(itemService, Constant.PULL_UP, "个", "引体向上");
			readCommon(itemService, Constant.PUSH_UP, "个", "俯卧撑");
			readCommon(itemService, Constant.ROPE_SKIPPING, "个", "跳绳");
			readCommon(itemService, Constant.RUN50, "ms", "50米跑");
			readCommon(itemService, Constant.SHUTTLE_RUN, "ms", "折返跑");
			readCommon(itemService, Constant.SIT_AND_REACH, "mm", "坐位体前屈");
			readCommon(itemService, Constant.SIT_UP, "个", "仰卧起坐");
			readCommon(itemService, Constant.SWIM, "ms", "游泳");
			readCommon(itemService, Constant.VITAL_CAPACITY, "ml", "肺活量");
			readCommon(itemService, Constant.VOLLEYBALL, "ms", "排球");
			readCommon(itemService, Constant.WALKING1500, "ms", "1500米健步走");
			readCommon(itemService, Constant.WALKING2000, "ms", "2000米健步走");

			updateView();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readCommon(NFCItemServiceImpl itemService, int code, String unit, String name) {
		IC_ItemResult itemResult;
		ICInfo icInfo = new ICInfo();
		try {
			itemResult = itemService.IC_ReadItemResult(code);
			Log.d(name + "：", itemResult.toString());
			if (itemResult.getResult()[0].getResultFlag() != 1) {
				Log.i(code + "", name + "没有成绩");
			} else {
				icInfo.setProjectTitle(name);
				icInfo.setProjectValue(itemResult.getResult()[0].getResultVal() + unit);
				icInfos.add(icInfo);
			}

		} catch (Exception e) {
			log.debug("此IC卡中没有" + name + "项目");
		}

	}

	private void updateView() {
		// 获取数据
		mHandler.post(updateTv2);
		mAdapter = new ICInfoAdapter(this, icInfos);
		Log.i("icInfos", icInfos + "");
		lvIcInfo.setAdapter(mAdapter);
		// tvShow.setText("读取完毕!");
	}

	/**
	 * 读取视力
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readVision(NFCItemServiceImpl itemService) {
		// 读取视力
		IC_ItemResult itemResultVision;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultVision = itemService.IC_ReadItemResult(Constant.VISION);
			if (itemResultVision.getResult()[0].getResultFlag() != 1) {
				Log.i("", "视力没有数据");
			} else {
				double left = itemResultVision.getResult()[0].getResultVal();
				double right = itemResultVision.getResult()[2].getResultVal();
				icInfo.setProjectTitle("左眼视力");
				icInfo.setProjectValue(left + "");
				icInfo1.setProjectTitle("右眼视力");
				icInfo1.setProjectValue(right + "");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有视力项目");
			e.printStackTrace();
		}

	}

	/**
	 * 从IC卡读取中长跑成绩
	 * 
	 * @param itemService
	 * @throws Exception
	 */
	private void readMiddleRun(IItemService itemService) {
		IC_ItemResult itemResultMiddleRace;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
			Log.i("读取中长跑测试", itemResultMiddleRace.toString());
			if (sex.equals("女")) {
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					Log.i("", "800米跑没有成绩");
				} else {
					icInfo.setProjectTitle("800米跑");
					icInfo.setProjectValue(itemResultMiddleRace.getResult()[0].getResultVal() + " ms");
					icInfos.add(icInfo);
				}
			} else {
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					Log.i("", "1000米跑没有成绩");
				} else {
					icInfo.setProjectTitle("1000米跑");
					icInfo.setProjectValue(itemResultMiddleRace.getResult()[0].getResultVal() + " ms");
					icInfos.add(icInfo);
				}
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有中长跑项目");
			e.printStackTrace();
		}

	}

	/**
	 * 从IC卡读取身高体重
	 * 
	 * @param itemService
	 */
	private void readHW(IItemService itemService) {
		// 读取身高体重
		
		IC_ItemResult itemResultHW;
		ICInfo icInfo = new ICInfo();
		try {
			itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
			Log.i("读取身高体重测试", itemResultHW.toString());
			if (itemResultHW.getResult()[0].getResultFlag() != 1) {
				Log.i("", "身高体重没有数据");
			} else {
				double height = itemResultHW.getResult()[0].getResultVal();
				double weight = itemResultHW.getResult()[2].getResultVal();
				icInfo.setProjectTitle("身高");
				icInfo.setProjectValue(height / 10 + " cm");
				icInfo1.setProjectTitle("体重");
				icInfo1.setProjectValue(weight / 1000 + " kg");
				icInfos.add(icInfo);
				icInfos.add(icInfo1);
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有身高体重项目");
			e.printStackTrace();
		}

	}
	
	private void initView() {
		tvShow = (TextView) findViewById(R.id.tv_icinfo_show);
		lvIcInfo = (ListView) findViewById(R.id.lv_icinfo);
		icInfos = new ArrayList<ICInfo>();

		tvNumber = (TextView) findViewById(R.id.tv_icinfo_number_show);
		tvName = (TextView) findViewById(R.id.tv_icinfo_name_show);
		tvGender = (TextView) findViewById(R.id.tv_icinfo_gender_show);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);

	}

	private void setListener() {

		ibQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
