package com.fpl.myapp.activity.online;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import com.fpl.myapp2.R;
import com.alibaba.fastjson.JSON;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.entity.PH_RoundGround;
import com.fpl.myapp.ui.ArcProgressBar;
import com.fpl.myapp.util.Constant;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ww.greendao.dao.RoundResult;

public class OnlineActivity extends Activity {

	private ArcProgressBar mArcProgressBar;
	private boolean result;
	private Context context;
	private List<RoundResult> roundResults;
	private Logger log = Logger.getLogger(OnlineActivity.class);
	private String MACID;
	private String ip;
	private String number;
	private SharedPreferences mSharedPreferences;
	private String MACORIMEI;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mArcProgressBar.setProgress((100 / (roundResults.size() / 1000)) * proCount);
				mArcProgressBar.setmArcText("正在发送中");
				mArcProgressBar.setProgressDesc("");
				break;
			case 2:
				mArcProgressBar.setProgress(100);
				mArcProgressBar.setProgressDesc("发送完毕");
				mArcProgressBar.setmArcText("");
				break;
			case 3:
				Toast.makeText(context, "服务器连接失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	private int ONLYNUMBER;
	private ArrayList<PH_RoundGround> ph_RoundGrounds;
	private PH_RoundGround ph_RoundGround;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online);
		context = this;
		mSharedPreferences = context.getSharedPreferences("ipAddress", Activity.MODE_PRIVATE);

		// SharedPreferences获取保存的上传地址
		ip = mSharedPreferences.getString("ip", "");
		number = mSharedPreferences.getString("number", "");
		// 获取Android机IMEI号
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		MACID = tm.getDeviceId();
		Log.i("IMEI=", MACID);

		MACORIMEI = MACID;

		ONLYNUMBER = mSharedPreferences.getInt("macorimei", 0);

		ph_RoundGrounds = new ArrayList<PH_RoundGround>();
		initView();
		setListener();

	}

	private boolean shortPress = false;
	private ImageView ivReturn;
	private ImageView ivSend;

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			shortPress = false;
			showAddDialog();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_F2) {
			shortPress = false;
			new AlertDialog.Builder(this).setTitle("选择唯一标识").setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(new String[] { "IMEI", "MAC地址" }, ONLYNUMBER,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog, int which) {
									switch (which) {
									case 0:
										MACORIMEI = MACID;
										Log.d("IMEI=", MACORIMEI);
										SharedPreferences.Editor editor1 = mSharedPreferences.edit();
										editor1.putInt("macorimei", 0);
										editor1.commit();
										break;
									case 1:
										MACORIMEI = NetUtil.getLocalMacAddressFromWifiInfo(context);
										Log.d("MAC=", MACORIMEI);
										SharedPreferences.Editor editor2 = mSharedPreferences.edit();
										editor2.putInt("macorimei", 1);
										editor2.commit();
										break;
									default:
										break;
									}
								}
							})
					.setNegativeButton("确定", null).show();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				event.startTracking();
				if (event.getRepeatCount() == 0) {
					shortPress = true;
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_F2) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				event.startTracking();
				if (event.getRepeatCount() == 0) {
					shortPress = true;
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_F1) {
			if (shortPress) {
				// Toast.makeText(this, "shortPress", Toast.LENGTH_LONG).show();
			} else {
			}
			shortPress = false;
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_F2) {
			if (shortPress) {
			} else {
			}
			shortPress = false;
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void initView() {
		mArcProgressBar = (ArcProgressBar) findViewById(R.id.progressBar);
		ivReturn = (ImageView) findViewById(R.id.iv_online_return);
		ivSend = (ImageView) findViewById(R.id.iv_online_send);

	}

	private void setListener() {
		ivReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ivSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new Thread() {
					public void run() {
						roundResults = DbService.getInstance(context).loadAllRoundResult();
						// 调用NetUtil中的网络判断方法
						result = NetUtil.netState(context);
						isWifiConnected(result);
					};
				}.run();

			}

		});

	}

	/**
	 * 自定义输入上传地址弹窗
	 */
	@SuppressLint("InflateParams")
	private void showAddDialog() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null);
		final EditText editIP = (EditText) textEntryView.findViewById(R.id.et_IP);
		final EditText editNumber = (EditText) textEntryView.findViewById(R.id.et_number);
		editIP.setText(ip);
		editNumber.setText(number);
		AlertDialog.Builder ad1 = new AlertDialog.Builder(OnlineActivity.this);
		ad1.setTitle("输入上传地址:");
		ad1.setIcon(android.R.drawable.ic_dialog_info);
		ad1.setView(textEntryView);
		ad1.setPositiveButton("是", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int i) {
				ip = editIP.getText().toString();
				number = editNumber.getText().toString();

				// SharedPreferences保存输入的上传地址
				SharedPreferences.Editor editor = mSharedPreferences.edit();
				editor.putString("ip", ip);
				editor.putString("number", number);
				editor.commit();

			}
		});
		ad1.setNegativeButton("否", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int i) {

			}
		});
		ad1.show();// 显示对话框

	}

	/**
	 * 根据返回值判断接下来的操作
	 * 
	 * @param result
	 */
	protected void isWifiConnected(boolean result) {

		if (true == result) {
			if (ip.isEmpty() || number.isEmpty()) {
				Toast.makeText(context, "请先设置上传地址", Toast.LENGTH_SHORT).show();
				return;
			}
			if (roundResults.isEmpty()) {
				NetUtil.showToast(context, "上传数据为空");
			} else {
				postRoundResults(context, roundResults);
			}
		} else {
			NetUtil.checkNetwork(OnlineActivity.this);
		}

	}

	private long time1;
	private long time2;
	private long useTime;
	private int isOver = 0;
	private int proCount = 1;

	/**
	 * 
	 * @param context
	 * @param roundResults
	 * @param i
	 */
	private void postRoundResults(final Context context, final List<RoundResult> roundResults) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				time1 = System.currentTimeMillis();
				OkHttpClient okHttpClient = new OkHttpClient();
				MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");
				for (int j = 0; j < roundResults.size(); j++) {
					ph_RoundGround = new PH_RoundGround();
					Log.i("roundResults", roundResults.get(j).getRoundResultID() + "");
					String stuCode = roundResults.get(j).getStudentCode();
					String itemCode = roundResults.get(j).getItemCode();
					String result = roundResults.get(j).getResult().toString();
					int round = roundResults.get(j).getRoundNo();
					String time = roundResults.get(j).getTestTime();
					int state = roundResults.get(j).getResultState();
					ph_RoundGround.setIsLastResult(0);
					ph_RoundGround.setItemCode(itemCode);
					ph_RoundGround.setMac(MACORIMEI);
					ph_RoundGround.setResult(result);
					ph_RoundGround.setResultState(state);
					ph_RoundGround.setRoundNo(round);
					ph_RoundGround.setStudentCode(stuCode);
					ph_RoundGround.setTestTime(time);
					ph_RoundGrounds.add(ph_RoundGround);
					Log.i("ph_RoundGrounds.size()=", ph_RoundGrounds.size() + "");
					if (ph_RoundGrounds.size() == 1000 || j == roundResults.size() - 1) {
						if (j == roundResults.size() - 1) {
							isOver = 1;
						}
						try {
							Log.i("ph_RoundGrounds=", ph_RoundGrounds.toString());
							// json为String类型的json数据
							String jsonResult = JSON.toJSONString(ph_RoundGrounds, true);
							Log.i("jsonResult=", jsonResult);
							RequestBody requestBody = RequestBody.create(JSONTYPE, jsonResult);
							Log.i("requestBody", requestBody.toString());
							String url = "http://" + ip + ":" + number + Constant.ROUND_RESULT_SAVE_URL;
							Request request = new Request.Builder()
									.url(url + "?signature=" + HttpUtil.getMD5(Constant.TOKEN)).post(requestBody)
									.build();
							Log.i("url=", url + "?signature=" + HttpUtil.getMD5(Constant.TOKEN));
							// Call call = okHttpClient.newCall(request);
							Response response = okHttpClient.newCall(request).execute();
							if (response.isSuccessful()) {
								Log.d("返回值", response.body().string());
								if (response.body().string().equals("1")) {
									if (isOver == 1) {
										time2 = System.currentTimeMillis();
										useTime = time2 - time1;
										log.info("上传用时：" + useTime + "ms");
										handler.sendEmptyMessage(2);
									} else {
										handler.sendEmptyMessage(1);
										proCount++;
									}
								} else {
									log.error("密码验证失败");
								}
								ph_RoundGrounds.clear();

							} else {
								log.error("上传失败");
								NetUtil.showToast(context, "服务器连接失败");
								// postRoundResults(context, roundResults);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
		}).start();
	}
}
