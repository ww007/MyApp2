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
	private ICInfo icInfo;
	private String sex;
	private TextView tvNumber;
	private TextView tvName;
	private TextView tvGender;
	private ListView lvIcInfo;
	private TextView tvShow;
	private SharedPreferences sharedPreferences;
	public ArrayList<String> projects = new ArrayList<>();
	private String[] newProject;
	private String[] newValue;
	private int number50, numberH, numberW, numberFHL, numberLDTY, numberYWQZ, number800, number1000, numberFWC,
			numberZWTQQ, numberTS, numberLSL, numberRSL, numberYTXS, numberMG, numberPQ, numberHWSXQ, numberLQYQ,
			numberZFP, numberTJZ, numberZQYQ, numberYY;
	private List<Item> items;
	private ImageButton ibQuit;
	private Logger log = Logger.getLogger(ICInformationActivity.class);

	Handler mHandler = new Handler();
	Runnable updateTv = new Runnable() {
		@Override
		public void run() {
			tvShow.setText("读取中...");
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_icinformation);
		// 读取本地存储的选中项目
		sharedPreferences = getSharedPreferences("projects", Activity.MODE_PRIVATE);
		int selected = sharedPreferences.getInt("size", 0);
		Log.i("selected=", selected + "");

		items = DbService.getInstance(this).loadAllItem();

		if (items.isEmpty()) {
			projects.add("身高");
			projects.add("体重");
			projects.add("肺活量");
			projects.add("50米跑");
			projects.add("立定跳远");
			projects.add("仰卧起坐");
			projects.add("坐位体前屈");
			projects.add("引体向上");
			projects.add("800米跑");
			projects.add("1000米跑");
		} else {
			for (Item item : items) {
				if (item.getMachineCode().equals("9")) {
					projects.add("左眼视力");
					projects.add("右眼视力");
				} else {
					projects.add(item.getItemName());
				}
			}
		}

		Log.i("projects=", projects.toString());

		initView();
		setListener();
	}

	@Override
	public void onNewIntent(final Intent intent) {
		tvShow.setText("读取中断，请重新刷卡！");
		readCard(intent);

	}

	/**
	 * 读卡
	 * 
	 * @param intent
	 */
	private void readCard(final Intent intent) {
		try {
			NFCItemServiceImpl itemService = new NFCItemServiceImpl(intent);
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

			if (items.isEmpty()) {
				// 读取身高体重
				IC_ItemResult itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
				Log.i("读取身高体重测试", itemResultHW.toString());
				if (itemResultHW.getResult()[0].getResultFlag() != 1) {
					newValue[0] = "（未测）";
					newValue[1] = "（未测）";
				} else {
					double height = itemResultHW.getResult()[0].getResultVal();
					double weight = itemResultHW.getResult()[2].getResultVal();
					newValue[0] = height / 10 + " cm";
					newValue[1] = weight / 1000 + " kg";
				}
				readOne(itemService, 2, Constant.VITAL_CAPACITY, " ml");
				readOne(itemService, 3, Constant.RUN50, " ms");
				readOne(itemService, 4, Constant.BROAD_JUMP, " cm");
				readOne(itemService, 5, Constant.SIT_UP, " 个");
				readOne(itemService, 6, Constant.SIT_AND_REACH, " mm");
				if (sex.equals("女")) {
					newValue[7] = "（无）";
				} else {
					readOne(itemService, 7, Constant.PULL_UP, " 个");
				}

				IC_ItemResult itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
				Log.i("读取中长跑测试", itemResultMiddleRace.toString());
				if (sex.equals("女")) {
					newValue[9] = "（无）";
					if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
						newValue[8] = "（未测）";
					} else {
						newValue[8] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
					}
				} else {
					newValue[8] = "（无）";
					if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
						newValue[9] = "（未测）";
					} else {
						newValue[9] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
					}
				}
			} else {

				for (int i = 0; i < items.size(); i++) {
					switch (items.get(i).getMachineCode()) {
					case "" + Constant.RUN50:
						number50 = i;
						// 读取50米
						readOne(itemService, number50, Constant.RUN50, " ms");
						break;
					case "" + Constant.HEIGHT_WEIGHT:
						if (items.get(i).getItemName().equals("身高")) {
							numberH = i;
						} else {
							numberW = i;
						}
						break;
					case "" + Constant.VITAL_CAPACITY:
						numberFHL = i;
						// 读取肺活量
						readOne(itemService, numberFHL, Constant.VITAL_CAPACITY, " ml");
						break;
					case "" + Constant.BROAD_JUMP:
						numberLDTY = i;
						// 读取立定跳远
						readOne(itemService, numberLDTY, Constant.BROAD_JUMP, " cm");
						break;
					case "" + Constant.SIT_UP:
						numberYWQZ = i;
						// 读取仰卧起坐
						if (sex.equals("男")) {
							newValue[numberYWQZ] = "（无）";
						} else {
							readOne(itemService, numberYWQZ, Constant.SIT_UP, " 个");
						}
						break;
					case "" + Constant.MIDDLE_RACE:
						if (items.get(i).getItemName().equals("800米跑")) {
							number800 = i;
						} else {
							number1000 = i;
						}
						// 读取中长跑
						readMiddleRun(itemService);
						break;
					case "" + Constant.PUSH_UP:
						numberFWC = i;
						// 读取俯卧撑
						readOne(itemService, numberFWC, Constant.PUSH_UP, " 个");
						break;
					case "" + Constant.SIT_AND_REACH:
						numberZWTQQ = i;
						// 读取坐位体前屈
						readOne(itemService, numberZWTQQ, Constant.SIT_AND_REACH, " cm");
						break;
					case "" + Constant.ROPE_SKIPPING:
						numberTS = i;
						// 读取跳绳
						readOne(itemService, numberTS, Constant.ROPE_SKIPPING, " 个");
						break;
					case "" + Constant.VISION:
						if (items.get(i).getItemName().equals("左眼视力")) {
							numberLSL = i;
						} else {
							numberRSL = i;
						}
						readVision(itemService);
						break;
					case "" + Constant.PULL_UP:
						numberYTXS = i;
						// 读取引体向上
						if (sex.equals("女")) {
							newValue[numberYTXS] = "（无）";
						} else {
							readOne(itemService, numberYTXS, Constant.PULL_UP, " 个");
						}
						break;
					case "" + Constant.JUMP_HEIGHT:
						numberMG = i;
						// 读取摸高
						readOne(itemService, numberMG, Constant.JUMP_HEIGHT, " cm");
						break;
					case "" + Constant.VOLLEYBALL:
						numberPQ = i;
						// 读取排球
						readOne(itemService, numberPQ, Constant.VOLLEYBALL, " 个");
						break;
					case "" + Constant.INFRARED_BALL:
						numberHWSXQ = i;
						// 读取红外实心球
						readOne(itemService, numberHWSXQ, Constant.INFRARED_BALL, " cm");
						break;
					case "" + Constant.BASKETBALL_SKILL:
						numberLQYQ = i;
						// 读取篮球运球
						readOne(itemService, numberLQYQ, Constant.BASKETBALL_SKILL, " ms");
						break;
					case "" + Constant.SHUTTLE_RUN:
						numberZFP = i;
						// 读取折返跑
						readOne(itemService, numberZFP, Constant.SHUTTLE_RUN, " ms");
						break;
					case "" + Constant.KICKING_SHUTTLECOCK:
						numberTJZ = i;
						// 读取踢毽子
						readOne(itemService, numberTJZ, Constant.KICKING_SHUTTLECOCK, " ms");
						break;
					case "" + Constant.FOOTBALL_SKILL:
						numberZQYQ = i;
						// 读取足球运球
						readOne(itemService, numberZQYQ, Constant.FOOTBALL_SKILL, " ms");
						break;
					case "" + Constant.SWIM:
						numberYY = i;
						// 读取游泳
						readOne(itemService, numberYY, Constant.SWIM, " ms");
						break;
					default:
						break;
					}

				}
				readHW(itemService);
			}

			icInfos.clear();
			updateView();

			mAdapter.notifyDataSetChanged();
			lvIcInfo.invalidate();
			tvShow.setText("读取完毕!");

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
			itemResultVision = itemService.IC_ReadItemResult(Constant.VISION);
			Log.i("读取视力测试", itemResultVision.toString());
			if (itemResultVision.getResult()[0].getResultFlag() != 1) {
				newValue[numberLSL] = "（未测）";
				newValue[numberRSL] = "（未测）";
			} else {
				double left = itemResultVision.getResult()[0].getResultVal();
				double right = itemResultVision.getResult()[2].getResultVal();
				newValue[numberLSL] = left + "";
				newValue[numberRSL] = right + "";
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
		try {
			itemResultMiddleRace = itemService.IC_ReadItemResult(Constant.MIDDLE_RACE);
			Log.i("读取中长跑测试", itemResultMiddleRace.toString());
			if (sex.equals("女")) {
				newValue[number1000] = "（无）";
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					newValue[number800] = "（未测）";
				} else {
					newValue[number800] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
				}
			} else {
				newValue[number800] = "（无）";
				if (itemResultMiddleRace.getResult()[0].getResultFlag() != 1) {
					newValue[number1000] = "（未测）";
				} else {
					newValue[number1000] = itemResultMiddleRace.getResult()[0].getResultVal() + " ms";
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
		try {
			itemResultHW = itemService.IC_ReadItemResult(Constant.HEIGHT_WEIGHT);
			Log.i("读取身高体重测试", itemResultHW.toString());
			if (itemResultHW.getResult()[0].getResultFlag() != 1) {
				newValue[numberH] = "（未测）";
				newValue[numberW] = "（未测）";
			} else {
				double height = itemResultHW.getResult()[0].getResultVal();
				double weight = itemResultHW.getResult()[2].getResultVal();
				newValue[numberH] = height / 10 + " cm";
				newValue[numberW] = weight / 1000 + " kg";
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有身高体重项目");
			e.printStackTrace();
		}

	}

	/**
	 * 读取只有一次成绩的项目
	 * 
	 * @param itemService
	 * @param number
	 *            序号
	 * @param code
	 *            项目机器代码
	 * @param unit
	 *            单位
	 */
	private void readOne(IItemService itemService, int number, int code, String unit) {
		IC_ItemResult itemResult;
		try {
			itemResult = itemService.IC_ReadItemResult(code);
			Log.d(code + "一次成绩：", itemResult.toString());
			if (itemResult.getResult()[0].getResultFlag() != 1) {
				newValue[number] = "（未测）";
			} else {
				newValue[number] = itemResult.getResult()[0].getResultVal() + unit;
			}
		} catch (Exception e) {
			log.debug("此IC卡中没有项目机器代码为" + code + "的项目");
		}

	}

	private void initView() {
		newProject = new String[projects.size()];
		newValue = new String[projects.size()];

		for (int i = 0; i < projects.size(); i++) {
			newProject[i] = projects.get(i).toString();
		}

		tvShow = (TextView) findViewById(R.id.tv_icinfo_show);
		lvIcInfo = (ListView) findViewById(R.id.lv_icinfo);
		icInfos = new ArrayList<ICInfo>();

		updateView();

		tvNumber = (TextView) findViewById(R.id.tv_icinfo_number_show);
		tvName = (TextView) findViewById(R.id.tv_icinfo_name_show);
		tvGender = (TextView) findViewById(R.id.tv_icinfo_gender_show);
		ibQuit = (ImageButton) findViewById(R.id.ib_quit);

	}

	private void updateView() {
		// 获取数据
		getData();
		mAdapter = new ICInfoAdapter(this, icInfos);
		Log.i("icInfos", icInfos + "");
		lvIcInfo.setAdapter(mAdapter);
	}

	public void getData() {

		for (int i = 0; i < newProject.length; i++) {
			icInfo = new ICInfo();
			icInfo.setProjectTitle(newProject[i]);
			icInfo.setProjectValue(newValue[i]);
			icInfos.add(icInfo);
		}
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
