package com.fpl.myapp.activity.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.adapter.ChengjiAdapter;
import com.fpl.myapp.base.NFCActivity;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.entity.RunGrade;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_ItemResult;
import com.wnb.android.nfc.dataobject.entity.IC_Result;
import com.wnb.android.nfc.dataobject.entity.Student;
import com.wnb.android.nfc.dataobject.service.IItemService;
import com.wnb.android.nfc.dataobject.service.impl.NFCItemServiceImpl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RunGradeActivity extends NFCActivity {

	private Bundle bd;
	private ArrayList<RunGrade> runGrades;
	private ChengjiAdapter adapter;
	private TextView tvTitle;
	private String title;
	private ListView lvGrade;
	private Button btnQuit;
	private Button btnSure;
	private String currentName;
	private int currentPosition;
	private Context context;

	private Logger log = Logger.getLogger(RunGradeActivity.class);
	private Student student = new Student();
	private Handler mHandler = new Handler();
	Runnable tip = new Runnable() {

		@Override
		public void run() {
			NetUtil.showToast(context, "成绩还未写入IC卡中，请重新刷卡");
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_run_grade);
		context = this;

		Intent intent = getIntent();
		bd = intent.getExtras();
		title = intent.getStringExtra("title");
		runGrades = (ArrayList<RunGrade>) bd.getSerializable("grades");
		log.info(title + "成绩：" + runGrades.toString());

		initView();
		setListener();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		writeCard(intent);
		// readCard(intent);
	}

	/**
	 * 读卡操作
	 * 
	 * @param intent
	 */
	private void readCard(Intent intent) {

		try {
			IItemService itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();
			showView();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 写卡操作
	 * 
	 * @param intent
	 */
	private List<String> stuCodes = new ArrayList<>();

	private void writeCard(Intent intent) {

		try {
			Log.i("title=", title);
			final NFCItemServiceImpl itemService = new NFCItemServiceImpl(intent);
			student = itemService.IC_ReadStuInfo();

			if (student == null) {
				NetUtil.showToast(context, "此卡无效");
				return;
			}
			stuCodes.clear();
			for (RunGrade runGrade : ChengjiAdapter.datas) {
				stuCodes.add(runGrade.getStuCode());
			}
			Log.i("stuCodes", stuCodes.toString());

			if (stuCodes.contains(student.getStuCode())) {
				for (int i = 0; i < ChengjiAdapter.datas.size(); i++) {
					final int j = i;
					if (student.getStuCode().equals(ChengjiAdapter.datas.get(i).getStuCode())) {
						Log.i("11111111111111", i + "==============");
						new AlertDialog.Builder(RunGradeActivity.this).setTitle("当前IC卡已有成绩，是否覆盖？")
								.setMessage("温馨提示：此操作时请不要移开IC卡以防写卡失败！").setPositiveButton("否", null)
								.setNegativeButton("是", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										try {
											ChengjiAdapter.datas.get(j).setName("");
											ChengjiAdapter.datas.get(j).setSex(0);
											ChengjiAdapter.datas.get(j).setStuCode("");
											readAndWrite(itemService);
											return;
										} catch (Exception e) {
											log.error(title + "成绩还未写入");
											mHandler.post(tip);
										}
									}
								}).show();
					}
				}
			} else {
				readAndWrite(itemService);
				Log.i("22222222222222", "==============");
			}
		} catch (Exception e) {
			log.error(title + "写卡操作失败");
		}
	}

	private void readAndWrite(IItemService itemService) throws Exception {
		if (title.equals("50米跑")) {
			String time = runGrades.get(currentPosition).getTime();
			int result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
					+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
					+ Integer.parseInt(time.substring(6, 9).toString());
			log.info("50米跑写卡=>" + "成绩：" + result1 + "，学生：" + student.toString());
			IC_Result[] result50M = new IC_Result[4];
			result50M[0] = new IC_Result(result1, 1, 0, 0);// 成绩1
			IC_ItemResult ItemResult50M = new IC_ItemResult(Constant.RUN50, 0, 0, result50M);
			boolean is50MResult = itemService.IC_WriteItemResult(ItemResult50M);
			log.info("50米跑写卡=>" + is50MResult);
		} else if (title.equals("800/1000米跑")) {
			String time = runGrades.get(currentPosition).getTime();
			int result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
					+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
					+ Integer.parseInt(time.substring(6, 9).toString());
			log.info("800/1000米跑写卡=>" + "成绩：" + result1 + "，学生：" + student.toString());
			IC_Result[] resultMiddleRace = new IC_Result[4];
			resultMiddleRace[0] = new IC_Result(result1, 1, 0, 0);// 成绩1
			IC_ItemResult ItemResultMiddleRace = new IC_ItemResult(Constant.MIDDLE_RACE, 0, 0, resultMiddleRace);
			boolean isMiddleRaceResult = itemService.IC_WriteItemResult(ItemResultMiddleRace);
			log.info("800/1000米跑写卡=>" + isMiddleRaceResult);
		} else if (title.equals("50米x8往返跑")) {
			String time = runGrades.get(currentPosition).getTime();
			int result1 = Integer.parseInt(time.subSequence(0, 2).toString()) * 60 * 1000
					+ Integer.parseInt(time.subSequence(3, 5).toString()) * 1000
					+ Integer.parseInt(time.substring(6, 9).toString());
			log.info("50米x8往返跑写卡=>" + "成绩：" + result1 + "，学生：" + student.toString());
			IC_Result[] resultZFP = new IC_Result[4];
			resultZFP[0] = new IC_Result(result1, 1, 0, 0);// 成绩1
			IC_ItemResult ItemResultZFP = new IC_ItemResult(Constant.SHUTTLE_RUN, 0, 0, resultZFP);
			boolean isZFPResult = itemService.IC_WriteItemResult(ItemResultZFP);
			log.info("50米x8往返跑写卡=>" + isZFPResult);
		}
		showView();
	}

	private void showView() {
		currentName = student.getStuName();
		log.info("当前读卡位置=>" + lvGrade.getFirstVisiblePosition() + "=>" + student.toString());
		updateView(currentPosition, student);
		currentPosition++;
		adapter.setSelectItem(currentPosition);
		adapter.notifyDataSetInvalidated();
	}

	/**
	 * 更新当前listView显示部分信息
	 * 
	 * @param itemIndex
	 * @param student
	 */
	private void updateView(int itemIndex, Student student) {
		runGrades.get(itemIndex).setName(currentName);
		currentName = "";
		// 得到第一个可显示控件的位置
		int visiblePosition = lvGrade.getFirstVisiblePosition();
		// 只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
		if (itemIndex - visiblePosition >= 0) {
			// 得到要更新的item的view
			View view = lvGrade.getChildAt(itemIndex - visiblePosition);
			// 调用adapter更新界面
			adapter.updateView(view, itemIndex, student);
		}

	}

	private ArrayList<RunGrade> datas;
	private TextView tv;

	private void setListener() {
		lvGrade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPosition = position;
				adapter.setSelectItem(position);
				adapter.notifyDataSetInvalidated();
			}
		});

		btnSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(RunGradeActivity.this).setTitle("保存成绩").setMessage("成绩将被保存后退出，是否确认？")
						.setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								datas = ChengjiAdapter.datas;
								if (DbService.getInstance(context).getStudentItemsCount() == 0) {
									NetUtil.showToast(context, "相关数据未下载，不能保存");
									return;
								}
								if (title.equals("50米跑")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 9).toString());
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.RUN50 + "", "50米跑");
										}
									}

								} else if (title.equals("800/1000米跑")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 9).toString());
											String proName = null;
											if (runGrade.getSex() == 1) {
												proName = "1000米跑";
											} else {
												proName = "800米跑";
											}
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.MIDDLE_RACE + "", proName);
										}
									}
								} else if (title.equals("50米x8往返跑")) {
									for (RunGrade runGrade : datas) {
										if (!runGrade.getName().isEmpty()) {
											int result = Integer.parseInt(
													runGrade.getTime().subSequence(0, 2).toString()) * 60 * 1000
													+ Integer.parseInt(runGrade.getTime().subSequence(3, 5).toString())
															* 1000
													+ Integer.parseInt(runGrade.getTime().substring(6, 9).toString());
											SaveDBUtil.saveGradesDB(context, runGrade.getStuCode(), result + "", 0,
													Constant.SHUTTLE_RUN + "", "50米x8往返跑");
										}
									}
								}
								finish();
							}
						}).show();
				// lvGrade.setEnabled(false);
				// btnSure.setEnabled(false);
			}
		});
		btnQuit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(RunGradeActivity.this).setTitle("确认").setMessage("成绩未保存，是否退出？")
						.setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								finish();
							}
						}).show();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			new AlertDialog.Builder(RunGradeActivity.this).setTitle("确认").setMessage("成绩未保存，是否退出？")
					.setPositiveButton("取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).setNegativeButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).show();
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		lvGrade = (ListView) findViewById(R.id.lv_run_grade);
		lvGrade.setChoiceMode(ListView.CHOICE_MODE_SINGLE);// 设置单选模式

		tvTitle = (TextView) findViewById(R.id.tv_title_runGrade);
		btnQuit = (Button) findViewById(R.id.btn_grade_quit);
		btnSure = (Button) findViewById(R.id.btn_grade_sure);
		tv = (TextView) findViewById(R.id.tv_runGrade);

		tvTitle.setText(title);
		adapter = new ChengjiAdapter(this, runGrades);
		lvGrade.setAdapter(adapter);
	}

}
