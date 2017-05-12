package com.fpl.myapp.activity.online;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.StudentItem;

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
				Log.i("进度更新", currentPage + "");
				mArcProgressBar.setProgress((int) ((100 * currentPage) / (totalCount / 1000)));
				mArcProgressBar.setmArcText("正在发送中");
				mArcProgressBar.setProgressDesc("");
				break;
			case 2:
				mArcProgressBar.setProgress(100);
				mArcProgressBar.setProgressDesc("发送完毕");
				mArcProgressBar.setmArcText("");
				break;
			case 3:
				NetUtil.showToast(context, "数据为空");
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

		initView();
		setListener();
	}

	private boolean shortPress = false;
	private ImageView ivReturn;
	private ImageView ivSend;
	private long totalCount;

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
		totalCount = DbService.getInstance(context).getRoundResultsCount();
		Log.i("成绩表总条数：", totalCount + "");
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
			time1 = System.currentTimeMillis();
			postRoundResultsForPage(context, 0);
		} else {
			NetUtil.checkNetwork(OnlineActivity.this);
		}

	}

	private long time1;
	private long time2;
	private long useTime;
	private int currentPage = 0;

	// private String testResult;
	// private String[] h = { "1750", "1600", "1650", "1660", "1645", "1755",
	// "1880", "1715", "1695", "1900" };
	// private String[] w = { "65000", "55000", "50000", "66000", "56500",
	// "60500", "70000", "61500", "50500", " 51000" };
	// private String[] fhl = { "1000", "2000", "3000", "4000", "4500", "3500",
	// "5000", "2510", "3545", "4400" };
	// private String[] r50 = { "10000", "11100", "11200", "9000", "9500",
	// "10500", "11500", "12510", "13545", "14400" };
	// private String[] zwtqq = { "10", "111", "-100", "150", "-50", "-200",
	// "300", "80", "50", "-90" };
	// private String[] ywqz = { "10", "111", "100", "150", "50", "200", "300",
	// "80", "30", "90" };
	// private String[] ldty = { "1500", "1666", "1000", "2000", "3000", "1800",
	// "3500", "3800", "3100", "3900" };
	// private String[] ytxx = { "15", "16", "10", "20", "30", "18", "35", "38",
	// "31", "39" };
	// private String[] r1000 = { "60000", "70000", "80000", "110000", "130000",
	// "180000", "350000", "380000", "310000",
	// "390000" };
	// private String[] r800 = { "60000", "70000", "80000", "110000", "130000",
	// "180000", "350000", "380000", "310000",
	// "390000" };

	private void postRoundResultsForPage(final Context context, final int page) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ivSend.setClickable(false);
				currentPage = page;
				ph_RoundGrounds = new ArrayList<PH_RoundGround>();
				OkHttpClient okHttpClient = new OkHttpClient();
				MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");
				roundResults = DbService.getInstance(context).getRoundResultForPage(page);
				// List<StudentItem> studentItems =
				// DbService.getInstance(context).getStudentItemsForPage(page);
				if (roundResults.isEmpty() && page == 0) {
					handler.sendEmptyMessage(3);
					return;
				} else if (roundResults.size() != 1000) {
					currentPage = -1;
				}
				for (RoundResult roundResult : roundResults) {
					ph_RoundGround = new PH_RoundGround();
					String stuCode = roundResult.getStudentCode();
					String itemCode = roundResult.getItemCode();
					String result = roundResult.getResult().toString();
					int round = roundResult.getRoundNo();
					String time = roundResult.getTestTime();
					int state = roundResult.getResultState();
					ph_RoundGround.setIsLastResult(0);
					ph_RoundGround.setItemCode(itemCode);
					ph_RoundGround.setMac(MACORIMEI);
					ph_RoundGround.setResult(result);
					ph_RoundGround.setResultState(state);
					ph_RoundGround.setRoundNo(round);
					ph_RoundGround.setStudentCode(stuCode);
					ph_RoundGround.setTestTime(time);
					ph_RoundGrounds.add(ph_RoundGround);
				}
				// for (StudentItem studentItem : studentItems) {
				// Random rand = new Random();
				// int num = rand.nextInt(10);
				// ph_RoundGround = new PH_RoundGround();
				// String stuCode = studentItem.getStudentCode();
				// String itemCode = studentItem.getItemCode();
				// switch (itemCode) {
				// case "E01":
				// testResult = h[num];
				// break;
				// case "E02":
				// testResult = w[num];
				// break;
				// case "E03":
				// testResult = fhl[num];
				// break;
				// case "E04":
				// testResult = r50[num];
				// break;
				// case "E05":
				// testResult = zwtqq[num];
				// break;
				// case "E06":
				// testResult = ytxx[num];
				// break;
				// case "E07":
				// testResult = ywqz[num];
				// break;
				// case "E08":
				// testResult = r1000[num];
				// break;
				// case "E09":
				// testResult = ldty[num];
				// break;
				// case "E011":
				// testResult = ytxx[num];
				// break;
				// case "E012":
				// testResult = r1000[num];
				// break;
				// case "E013":
				// testResult = r800[num];
				// break;
				//
				// default:
				// break;
				// }
				// ph_RoundGround.setIsLastResult(0);
				// ph_RoundGround.setItemCode(itemCode);
				// ph_RoundGround.setMac(MACORIMEI);
				// ph_RoundGround.setResult(testResult);
				// ph_RoundGround.setResultState(0);
				// ph_RoundGround.setRoundNo(1);
				// ph_RoundGround.setStudentCode(stuCode);
				// ph_RoundGround.setTestTime("2017-05-11 16:00:00");
				// ph_RoundGrounds.add(ph_RoundGround);
				// }
				Log.i("ph_RoundGrounds.size()=", ph_RoundGrounds.size() + "");
				try {
					// json为String类型的json数据
					String jsonResult = JSON.toJSONString(ph_RoundGrounds);
					Log.i("jsonResult=", jsonResult);
					RequestBody requestBody = RequestBody.create(JSONTYPE, jsonResult);
					Log.i("requestBody", requestBody.toString());
					String url = "http://" + ip + ":" + number + Constant.ROUND_RESULT_SAVE_URL;
					Request request = new Request.Builder().url(url + "?signature=" + HttpUtil.getMD5(Constant.TOKEN))
							.post(requestBody).build();
					Log.i("url=", url + "?signature=" + HttpUtil.getMD5(Constant.TOKEN));
					Call call = okHttpClient.newCall(request);
					call.enqueue(new Callback() {

						@Override
						public void onResponse(Call arg0, Response response) throws IOException {
							String responseNo = response.body().string();
							Log.i("返回值", responseNo + "---" + currentPage);
							if (currentPage == -1) {
								log.info("上传完毕");
								time2 = System.currentTimeMillis();
								useTime = time2 - time1;
								log.info("上传用时：" + useTime + "ms");
								handler.sendEmptyMessage(2);
								showNotification(context);
								return;
							} else {
								currentPage++;
								handler.sendEmptyMessage(1);
								postRoundResultsForPage(context, currentPage);
							}
						}

						@Override
						public void onFailure(Call arg0, IOException arg1) {
							log.error("上传失败");
							postRoundResultsForPage(context, currentPage);
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					log.error("服务器连接中断");
					postRoundResultsForPage(context, currentPage);
				}
			}
		}).start();
	}

	private void showNotification(Context context) {
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app));
		builder.setAutoCancel(true);
		builder.setContentTitle("MyApp通知");
		builder.setContentText("上传数据完成");
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// 设置点击跳转
		Intent hangIntent = new Intent();
		// hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// hangIntent.setClass(context, MainActivity.class);
		// 如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
		PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setFullScreenIntent(hangPendingIntent, true);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, builder.build());
	}

	// @Override
	// protected void onResume() {
	// super.onResume();
	// handler.sendEmptyMessage(1);
	// }
}
