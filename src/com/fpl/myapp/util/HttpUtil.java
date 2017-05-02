package com.fpl.myapp.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apaches.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fpl.myapp.db.DbService;
import com.fpl.myapp.db.SaveDBUtil;
import com.fpl.myapp.entity.First_StudentItem;
import com.fpl.myapp.entity.PH_Student;
import com.fpl.myapp.entity.PH_StudentItem;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ww.greendao.dao.Item;

public class HttpUtil {
	private static List<PH_StudentItem> studentItems;
	private static List<Item> itemList;
	public static int okFlag;

	/**
	 * OKhttp发送http get请求
	 * 
	 * @param path
	 * @param params
	 * @param listener
	 */

	public static int sendOkhttp(final String path, final Map<String, String> params,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建okHttpClient对象
				OkHttpClient mOkHttpClient = new OkHttpClient();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(path).append("?");
				try {
					if (params != null && params.size() != 0) {
						for (Map.Entry<String, String> entry : params.entrySet()) {
							// 转换成UTF-8
							stringBuilder.append(entry.getKey()).append("=")
									.append(URLEncoder.encode(entry.getValue(), "utf-8"));

							stringBuilder.append("&");
						}
					}
					// 连接signature
					stringBuilder.append("signature=" + getSignatureVal(params));
					Log.i("---------", stringBuilder.toString());
					// 创建一个Request
					Request request = new Request.Builder().url(stringBuilder.toString()).build();
					Response response = mOkHttpClient.newCall(request).execute();
					if (response.isSuccessful()) {
						String result = response.body().string();
						Log.i("下载成功", result);
						okFlag = 1;
						listener.onFinish(result);
					} else {
						okFlag = 3000;
						throw new IOException("Unexpected code " + response);
					}
				} catch (UnsupportedEncodingException e) {
					okFlag = 3000;
					e.printStackTrace();
				} catch (IOException e) {
					okFlag = 3000;
					e.printStackTrace();
				}
			}
		}).start();
		return okFlag;

	}

	public static int sendOkhttp1(final String path, final int i, final Map<String, String> params,
			final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 创建okHttpClient对象
				OkHttpClient mOkHttpClient = new OkHttpClient();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(path).append("?pageNo=" + i + "&");
				try {
					if (params != null && params.size() != 0) {
						for (Map.Entry<String, String> entry : params.entrySet()) {
							// 转换成UTF-8
							stringBuilder.append(entry.getKey()).append("=")
									.append(URLEncoder.encode(entry.getValue(), "utf-8"));

							stringBuilder.append("&");
						}
					}
					// 连接signature
					stringBuilder.append("signature=" + getSignatureVal1(params, i));
					Log.i("---------", stringBuilder.toString());
					// 创建一个Request
					Request request = new Request.Builder().url(stringBuilder.toString()).build();
					Response response = mOkHttpClient.newCall(request).execute();
					if (response.isSuccessful()) {
						String result = response.body().string();
						Log.i("下载成功", result);
						okFlag = 1;
						listener.onFinish(result);
					} else {
						okFlag = 3000;
						throw new IOException("Unexpected code " + response);
					}
				} catch (UnsupportedEncodingException e) {
					okFlag = 3000;
					e.printStackTrace();
				} catch (IOException e) {
					okFlag = 3000;
					e.printStackTrace();
				}
			}
		}).start();
		return okFlag;

	}

	public static int sendOkhttp2(final String path, final int i, final Map<String, String> params,
			final Context context, final int totalPage) {
		// 创建okHttpClient对象
		OkHttpClient mOkHttpClient = new OkHttpClient();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(path).append("?pageNo=" + i + "&");
		try {
			if (params != null && params.size() != 0) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					// 转换成UTF-8
					stringBuilder.append(entry.getKey()).append("=")
							.append(URLEncoder.encode(entry.getValue(), "utf-8"));

					stringBuilder.append("&");
				}
			}
			// 连接signature
			stringBuilder.append("signature=" + getSignatureVal1(params, i));
			Log.i("---------", stringBuilder.toString());
			// 创建一个Request
			Request request = new Request.Builder().url(stringBuilder.toString()).build();
			Response response = mOkHttpClient.newCall(request).execute();
			if (response.isSuccessful()) {
				String result = response.body().string();
				Log.i("下载成功", result);
				okFlag = 1;
			} else {
				okFlag = 3000;
				throw new IOException("Unexpected code " + response);
			}
		} catch (UnsupportedEncodingException e) {
			okFlag = 3000;
			e.printStackTrace();
		} catch (IOException e) {
			okFlag = 3000;
			e.printStackTrace();
		}
		return okFlag;

	}

	/**
	 * MD5加密
	 * 
	 * @param paramMap
	 *            加密参数
	 * @param i
	 * @return
	 */
	public static String getSignatureVal(Map<String, String> paramMap) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			List<String> list = new ArrayList<String>();
			if (paramMap != null && paramMap.size() != 0) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					list.add(entry.getKey());
					list.add(entry.getValue());
				}
			}
			// 字典排序
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				stringBuilder.append(list.get(i));
			}
			stringBuilder.append(Constant.TOKEN);
			return HttpUtil.getMD5(stringBuilder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	public static String getSignatureVal1(Map<String, String> paramMap, int pageNo) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			List<String> list = new ArrayList<String>();
			if (paramMap != null && paramMap.size() != 0) {
				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					list.add(entry.getKey());
					list.add(entry.getValue());
				}
			}
			list.add("pageNo");
			list.add(pageNo + "");
			// 字典排序
			Collections.sort(list);
			for (int i = 0; i < list.size(); i++) {
				stringBuilder.append(list.get(i));
			}
			stringBuilder.append(Constant.TOKEN);
			return HttpUtil.getMD5(stringBuilder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * MD5加密算法
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	private static String getMD5(String str) throws Exception {
		try {
			// 生成一个MD5加密计算摘要
			// MessageDigest md = MessageDigest.getInstance("MD5");
			// 计算md5函数
			// md.update(str.getBytes());
			// digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
			// BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
			return DigestUtils.md5Hex(str.getBytes("UTF-8"));

		} catch (Exception e) {
			throw new Exception("MD5加密失败", e);
		}
	}

	private static int studentFlag;
	private static List<PH_Student> students;
	private static boolean StuItemFlag;
	private static int itemFlag;

	/**
	 * 获取学生信息
	 * 
	 * @param context
	 * @param mAsyncSession
	 * @return
	 */
	public static int getStudentInfo(final Context context) {
		try {
			String m = getMD5("fpl@*!");
			Map<String, String> map = new HashMap<>();
			map.put("signature", m);
			sendOkhttp(Constant.STUDENT_URL, null, new HttpCallbackListener() {
				@Override
				public void onFinish(String response) {
					com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(response);
					JSONArray jsonStudent = jsonObject.getJSONArray("student");
					students = JSON.parseArray(jsonStudent.toJSONString(), PH_Student.class);
					if (DbService.getInstance(context).loadAllStudent().size() != students.size()) {
						studentFlag = SaveDBUtil.saveStudentDB(response, students, context);
					} else {
						Log.i("存在", "学生信息已存在");
					}
				}

				@Override
				public void onError(Exception e) {
					Log.i("error", "下载学生信息失败");

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (studentFlag == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	public static void sendOkhttp0(final String path, final int i, final Map<String, String> params,
			final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(path).append("?pageNo=" + i + "&");
				if (params != null && params.size() != 0) {
					for (Map.Entry<String, String> entry : params.entrySet()) {
						// 转换成UTF-8
						try {
							stringBuilder.append(entry.getKey()).append("=")
									.append(URLEncoder.encode(entry.getValue(), "utf-8"));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						stringBuilder.append("&");
					}
				}
				// 连接signature
				stringBuilder.append("signature=" + getSignatureVal1(params, i));
				Request request = new Request.Builder().url(stringBuilder.toString()).get().build();
				final Call call = client.newCall(request);
				call.enqueue(new Callback() {
					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						String result = response.body().string();
						First_StudentItem currentStuItem = JSON.parseObject(result, First_StudentItem.class);
						int totalPage = currentStuItem.getTotalPage();
						studentItems.addAll(currentStuItem.getResult());
						Log.i("当前页--当前大小", currentStuItem.getPageNo() + "--" + studentItems.size());
						if (studentItems.size() == currentStuItem.getTotalCount()) {
							SaveDBUtil.saveStudentItemDB(studentItems, context, totalPage);
						}
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						client.newCall(call.request()).enqueue(this);
						Log.e("下载失败", arg1 + "" + i);
					}
				});
			}
		}).start();
	}

	/**
	 * 获取学生项目信息
	 * 
	 * @param context
	 */
	public static boolean getStudentItemInfo(final Context context) {
		try {
			String m = getMD5("fpl@*!");
			Map<String, String> map = new HashMap<>();
			map.put("signature", m);
			// sendOkhttp0(Constant.STUDENT_ITEM_URL, 1, null, context, 0);
			sendOkhttp1(Constant.STUDENT_ITEM_URL, 1, null, new HttpCallbackListener() {

				@Override
				public void onFinish(String response) {
					final long time1 = System.currentTimeMillis();
					First_StudentItem firstStudentItems = JSON.parseObject(response, First_StudentItem.class);
					int totalPage = firstStudentItems.getTotalPage();
					Log.i("学生项目总数", firstStudentItems.getTotalCount() + "");
					Log.i("总页数", firstStudentItems.getTotalPage() + "");
					if (DbService.getInstance(context).loadAllStudentItem().size() != firstStudentItems
							.getTotalCount()) {
						// Toast.LENGTH_SHORT).show();
						// SaveDBUtil.saveStudentItemDB(studentItems, context,
						// totalPage);
						for (int i = 1; i < totalPage + 1; i++) {
							// sendOkhttp2(Constant.STUDENT_ITEM_URL, i, null,
							// context, totalPage);
							sendOkhttp0(Constant.STUDENT_ITEM_URL, i, null, context);
							// sendOkhttp1(Constant.STUDENT_ITEM_URL, i, null,
							// new HttpCallbackListener() {
							//
							// @Override
							// public void onFinish(String response) {
							// // JSONObject object =
							// // JSON.parseObject(response);
							// // JSONArray result =
							// // object.getJSONArray("result");
							// First_StudentItem currentStuItem =
							// JSON.parseObject(response,
							// First_StudentItem.class);
							// studentItems = currentStuItem.getResult();
							// SaveDBUtil.saveStudentItemDB(studentItems,
							// context, totalPage);
							// StuItemFlag = true;
							// long time2 = System.currentTimeMillis();
							// long t = time2 - time1;
							// Log.i("保存学生项目成绩完成,存储用时：", t + "ms");
							// }
							//
							// @Override
							// public void onError(Exception e) {
							// // TODO Auto-generated method stub
							//
							// }
							// });
						}

					} else {
						Log.i("存在", "学生项目已存在");
					}
				}

				@Override
				public void onError(Exception e) {
					Log.i("error--->", "下载学生项目失败");
					StuItemFlag = false;
				}
			});
			// 请求学生项目成绩信息
			// sendOkhttp(Constant.STUDENT_ITEM_URL, null, new
			// HttpCallbackListener() {
			// @Override
			// public void onFinish(String response) {
			// long time1 = System.currentTimeMillis();
			// totalStudentItems = JSON.parseArray(response,
			// First_StudentItem.class);
			// int totalPage = totalStudentItems.get(0).getTotalPage();
			// for (int i = 1; i < totalPage + 1; i++) {
			//
			// }
			// if (DbService.getInstance(context).loadAllStudentItem().size() !=
			// studentItems.size()) {
			// // Toast.LENGTH_SHORT).show();
			// SaveDBUtil.saveStudentItemDB(studentItems, context);
			// } else {
			// Log.i("存在", "学生项目已存在");
			// }
			// StuItemFlag = true;
			// long time2 = System.currentTimeMillis();
			// long t = time2 - time1;
			// Log.i("保存学生项目成绩完成,存储用时：", t + "ms");
			// }
			//
			// @Override
			// public void onError(Exception e) {
			// Log.i("error--->", "下载学生项目失败");
			// StuItemFlag = false;
			// }
			// });
		} catch (Exception e) {
			Toast.makeText(context, "服务器连接异常...", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		return StuItemFlag;
	}

	/**
	 * 获取项目信息
	 * 
	 * @param context
	 */
	public static int getItemInfo(final Context context) {

		try {
			String m = HttpUtil.getMD5("fpl@*!");
			Map<String, String> map = new HashMap<>();
			map.put("signature", m);
			sendOkhttp(Constant.ITEM_URL, null, new HttpCallbackListener() {
				public void onFinish(String response) {
					itemFlag = 1;
					// 解析获取的Json数据
					itemList = JSON.parseArray(response, Item.class);
					if (DbService.getInstance(context).loadAllItem().size() != itemList.size()) {
						DbService.getInstance(context).saveItemLists(itemList);
						Log.i("success", "保存项目信息成功");
					} else {
						Log.i("fail", "项目信息已存在");
					}
				}

				@Override
				public void onError(Exception e) {
					Log.i("error", "项目数据下载失败");
				}
			});
		} catch (Exception e) {
			itemFlag = -1;
			e.printStackTrace();
		}
		return itemFlag;
	}

}
