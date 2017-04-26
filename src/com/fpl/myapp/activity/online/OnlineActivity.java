package com.fpl.myapp.activity.online;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

import com.fpl.myapp2.R;
import com.fpl.myapp.db.DbService;
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
import okhttp3.FormBody;
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
				mArcProgressBar.setProgress((100 / roundResults.size()) * count);
				mArcProgressBar.setmArcText("正在发送中");
				mArcProgressBar.setProgressDesc("");
				break;
			case 2:
				if (errorCount != 0) {
					String text = errorCount + "条数据上传失败";
					Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
				}
				mArcProgressBar.setProgress(100);
				mArcProgressBar.setProgressDesc("发送完毕");
				mArcProgressBar.setmArcText("");
				count = 0;
				break;
			case 3:
				Toast.makeText(context, "服务器连接失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	private int ONLYNUMBER;

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

		MACORIMEI = MACID;

		ONLYNUMBER = mSharedPreferences.getInt("macorimei", 0);

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
			roundResults = DbService.getInstance(context).loadAllRoundResult();
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

	private Map<String, String> paramsValue = new HashMap<>();
	private int count = 0;
	private int errorCount = 0;
	private int hasCount = 0;

	private void postRoundResults(final Context context, final List<RoundResult> roundResults) {
		new Thread() {

			@Override
			public void run() {
				// 连接signature
				// 创建一个OkHttpClient对象
				OkHttpClient okHttpClient = new OkHttpClient();
				for (RoundResult roundResult : roundResults) {
					count++;
					try {
						String stuCode = roundResult.getStudentItem().getStudentCode();
						String itemCode = roundResult.getStudentItem().getItemCode();
						RequestBody body = new FormBody.Builder().add("studentCode", stuCode).add("itemCode", itemCode)
								.add("result", roundResult.getResult().toString())
								.add("roundNo", roundResult.getRoundNo().toString())
								.add("testTime", roundResult.getTestTime())
								.add("resultState", roundResult.getResultState().toString())
								.add("isLastResult", roundResult.getIsLastResult().toString()).add("mac", MACORIMEI)
								.build();
						paramsValue.put("studentCode", stuCode);
						paramsValue.put("itemCode", itemCode);
						paramsValue.put("result", roundResult.getResult().toString());
						paramsValue.put("roundNo", roundResult.getRoundNo().toString());
						paramsValue.put("testTime", roundResult.getTestTime());
						paramsValue.put("resultState", roundResult.getResultState().toString());
						paramsValue.put("isLastResult", roundResult.getIsLastResult().toString());
						paramsValue.put("mac", MACORIMEI);

						// 创建一个请求对象
						String url = "http://" + ip + ":" + number + Constant.ROUND_RESULT_SAVE_URL;
						Request request = new Request.Builder()
								.url(url + "?signature=" + HttpUtil.getSignatureVal(paramsValue)).post(body).build();
						// 发送请求获取响应
						Response response = okHttpClient.newCall(request).execute();
						// 判断请求是否成功
						if (response.isSuccessful()) {
							// 打印服务端返回结果
							String m = response.body().string();
							if (m.equals("-3")) {
								hasCount++;
							} else if (m.equals("1")) {
								Log.i("---", "重复上传");
							} else {
								errorCount++;
								log.error(count + "返回值： " + m + "上传失败" + roundResult);
							}
							Log.i(count + "轮次成绩", "response ----->" + m);
							if (count == roundResults.size()) {
								log.debug(errorCount + "条数据上传失败");
								handler.sendEmptyMessage(2);
							} else {
								handler.sendEmptyMessage(1);
							}
						} else {
							handler.sendEmptyMessage(3);
							break;
						}

					} catch (IOException e) {
						Log.e("----", "上传出错");
						handler.sendEmptyMessage(3);
						break;
					}
				}
				count = 0;
			}
		}.start();
	}
}
