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
import com.fpl.myapp.entity.First_Student;
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
	private static List<PH_StudentItem> studentItems = new ArrayList<>();
	private static List<Item> itemList;
	public static int okFlag;
	public static long startTime = 0;

	/**
	 * OKhttp发送请求
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
				OkHttpClient mOkHttpClient = new OkHttpClient().newBuilder().connectTimeout(100, TimeUnit.SECONDS)
						.readTimeout(100, TimeUnit.SECONDS).build();
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
	public static String getMD5(String str) throws Exception {
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
	private static List<PH_Student> students = new ArrayList<>();
	private static boolean StuItemFlag;
	private static int itemFlag;

	/**
	 * 获取学校、班级、年级信息
	 * 
	 * @param context
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
					SaveDBUtil.saveStudentDB(response, context);
					sendOkHttpForStudentPage(Constant.STUDENT_Page_URL, 1, context);
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

	private static int currentStuPage = 0;
	private static List<PH_Student> totalStudents = new ArrayList<>();

	/**
	 * 发送学生分页请求
	 * 
	 * @param studentPageUrl
	 * @param i
	 *            页数
	 * @param context
	 */
	private static void sendOkHttpForStudentPage(final String studentPageUrl, final int i, final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(50, TimeUnit.SECONDS).build();
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(studentPageUrl).append("?pageNo=" + i + "&" + "pageSize=1000");
				stringBuilder.append("&signature=" + getSignatureVal2(i));
				Log.i("stringBuilder.toString()", stringBuilder.toString());
				Request request = new Request.Builder().url(stringBuilder.toString()).get().build();
				final Call call = client.newCall(request);
				call.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						String result = response.body().string();
						First_Student currentStudent = JSON.parseObject(result, First_Student.class);
						currentStuPage = currentStudent.getPageNo();
						if (currentStudent.getTotalCount() == DbService.getInstance(context).getStudentsCount()) {
							Log.i("------------", DbService.getInstance(context).getStudentsCount() + "学生信息已存在");
							HttpUtil.getStudentItemInfo(context);
							return;
						} else {
							List<PH_Student> currentResult = currentStudent.getResult();
							Log.i("student当前页", currentStudent.getPageNo() + "");
							if (i == 1) {
								totalStudents = currentResult;
							} else {
								totalStudents.addAll(currentResult);
							}
							currentStuPage++;
							if (currentStudent.getPageNo() == currentStudent.getTotalPage()) {
								HttpUtil.getStudentItemInfo(context);
								SaveDBUtil.saveStudentPage(context, totalStudents);
								return;
							} else {
								sendOkHttpForStudentPage(Constant.STUDENT_Page_URL, currentStuPage, context);
							}
						}
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						Log.e("下载失败", arg1 + "");
						sendOkHttpForStudentPage(Constant.STUDENT_Page_URL, currentStuPage, context);
					}
				});
			}
		}).start();

	}

	/**
	 * 获取学生分页信息加密
	 * 
	 * @param pageNo
	 * @return
	 */
	protected static String getSignatureVal2(int pageNo) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			List<String> list = new ArrayList<String>();
			list.add("pageNo");
			list.add(pageNo + "");
			list.add("pageSize");
			list.add("1000");
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

	private static int currentPage = 0;

	/**
	 * 发送学生项目分页请求
	 * 
	 * @param path
	 * @param i
	 * @param params
	 * @param context
	 */
	public static void sendOkhttpForStudentItem(final String path, final int i, final Map<String, String> params,
			final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(50, TimeUnit.SECONDS).build();
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
						currentPage = currentStuItem.getPageNo();
						if (currentStuItem.getTotalCount() == DbService.getInstance(context).getStudentItemsCount()) {
							Log.i("-----------------", "学生项目信息已存在");
							return;
						} else {
							List<PH_StudentItem> currentResult = currentStuItem.getResult();
							if (i == 1) {
								studentItems = currentStuItem.getResult();
							} else {
								studentItems.addAll(currentResult);
							}
							Log.i("studentItem当前页", currentStuItem.getPageNo() + "");
							currentPage++;
							if (currentStuItem.getPageNo() == currentStuItem.getTotalPage()) {
								Log.i("studentItems", studentItems.size() + "");
								SaveDBUtil.saveStudentItemDB(studentItems, context, currentStuItem.getTotalPage(),
										currentStuItem.getPageNo());
								return;
							} else {
								sendOkhttpForStudentItem(Constant.STUDENT_ITEM_URL, currentPage, null, context);
							}
						}
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						Log.e("下载失败", arg1 + "");
						sendOkhttpForStudentItem(Constant.STUDENT_ITEM_URL, currentPage, null, context);
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
			sendOkhttpForStudentItem(Constant.STUDENT_ITEM_URL, 1, null, context);
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
			startTime = System.currentTimeMillis();
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
					HttpUtil.getStudentInfo(context);
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
