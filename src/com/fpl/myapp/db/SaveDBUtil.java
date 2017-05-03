package com.fpl.myapp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fpl.myapp2.R;
import com.fpl.myapp.entity.PH_Class;
import com.fpl.myapp.entity.PH_Grade;
import com.fpl.myapp.entity.PH_School;
import com.fpl.myapp.entity.PH_Student;
import com.fpl.myapp.entity.PH_StudentItem;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_Result;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import de.greenrobot.dao.async.AsyncSession;
import ww.greendao.dao.Classes;
import ww.greendao.dao.DaoSession;
import ww.greendao.dao.Grade;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.School;
import ww.greendao.dao.Student;
import ww.greendao.dao.StudentItem;

public class SaveDBUtil {
	private static List<PH_School> schools = new ArrayList<>();
	private static List<PH_Grade> grades = new ArrayList<>();
	private static List<PH_Class> classes = new ArrayList<>();
	private static long flag = -1;
	public static int over = 0;
	private static NotificationManager notificationManager;
	private static ArrayList<Student> mStudents;
	private static AsyncSession mAsyncSession = DbService.mDaoSession.startAsyncSession();
	private static ArrayList<StudentItem> mStudentItems;
	private static ArrayList<Classes> mClasses;
	private static ArrayList<Grade> mGrade;
	private static long startTime;

	/**
	 * 数据库中查询所有成绩并存入IC卡
	 * 
	 * @param context
	 * @param roundResults
	 * @param result
	 */
	public static void queryRoundResults(List<RoundResult> roundResults, IC_Result[] result) {
		List<Integer> list = new ArrayList<>();
		for (RoundResult roundResult : roundResults) {
			list.add(roundResult.getResult());
		}
		// 将最大成绩保存到result[0]
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == Collections.max(list)) {
				result[0] = new IC_Result(list.get(i), 1, 0, 0);
				list.remove(i);
			}
		}
		// 将除去最大成绩的其它成绩保存
		for (int i = 0; i < list.size(); i++) {
			result[i + 1] = new IC_Result(list.get(i), 1, 0, 0);
		}
	}

	/**
	 * 存储学生信息到数据库
	 * 
	 * @param response
	 *            获取到的学生信息
	 * @param students
	 *            学生对象
	 * @param context
	 * @return
	 */
	public static int saveStudentDB(String response, List<PH_Student> students, Context context) {
		startTime = System.currentTimeMillis();
		mStudents = new ArrayList<Student>();
		mClasses = new ArrayList<Classes>();
		mGrade = new ArrayList<Grade>();
		// 解析获取的Json数据
		JSONObject jsonObject = JSON.parseObject(response);
		JSONArray jsonSchool = jsonObject.getJSONArray("school");
		JSONArray jsonClass = jsonObject.getJSONArray("class");
		JSONArray jsonGrade = jsonObject.getJSONArray("grade");
		schools = JSON.parseArray(jsonSchool.toJSONString(), PH_School.class);
		classes = JSON.parseArray(jsonClass.toJSONString(), PH_Class.class);
		grades = JSON.parseArray(jsonGrade.toJSONString(), PH_Grade.class);

		// 保存学校信息
		for (int i = 0; i < schools.size(); i++) {
			School school = new School(null, schools.get(i).getSchoolName(), schools.get(i).getSchoolYear(), null);
			DbService.getInstance(context).saveSchool(school);
		}
		Log.i("----------", "保存学校信息完成");
		// 保存年级信息
		for (int i = 0; i < grades.size(); i++) {
			String schoolName = grades.get(i).getSchoolName();
			String gradeCode = grades.get(i).getGradeCode();
			String gradeName = grades.get(i).getGradeName();
			Long SchoolID = DbService.getInstance(context).querySchoolByName(schoolName).get(0).getSchoolID();
			Grade grade = new Grade(SchoolID, gradeCode, gradeName, null);
			mGrade.add(grade);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.gradeDao.insertOrReplaceInTx(mGrade);
				Log.i("mGrade", mGrade.size() + "");
				Log.i("----------", "保存年级信息完成");
			}
		});
		// 保存班级信息
		for (int i = 0; i < classes.size(); i++) {
			String gradeCode = classes.get(i).getGradeCode();
			String classCode = classes.get(i).getClassCode();
			String className = classes.get(i).getClassName();
			Classes newClass = new Classes(gradeCode, classCode, className, null);
			mClasses.add(newClass);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.classesDao.insertOrReplaceInTx(mClasses);
				Log.i("mClasses", mClasses.size() + "");
				Log.i("----------", "保存班级信息完成");
			}
		});
		// 保存学生信息
		for (int i = 0; i < students.size(); i++) {
			Student student = new Student(students.get(i).getStudentCode(), students.get(i).getStudentName(),
					students.get(i).getSex(), students.get(i).getClassCode(), students.get(i).getGradeCode(),
					students.get(i).getIDCardNo(), students.get(i).getICCardNo(), students.get(i).getDownloadTime(),
					null, null, null);
			mStudents.add(student);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.studentDao.insertOrReplaceInTx(mStudents);
				Log.i("students", mStudents.size() + "");
				Log.i("----------", "保存学生信息完成");
			}
		});
		if (mStudents.size() != 0 && flag != -1) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 存储学生项目信息到数据库
	 * 
	 * @param studentItems
	 *            获取到的学生项目信息
	 * @param context
	 * @param totalPage
	 */
	public static void saveStudentItemDB(List<PH_StudentItem> studentItems, Context context) {
		mStudentItems = new ArrayList<>();
		for (PH_StudentItem stuItem : studentItems) {
			String itemCode = stuItem.getItemCode();
			String studentCode = stuItem.getStudentCode();
			StudentItem studentItem = new StudentItem(null, studentCode, itemCode, null, 0, null, 0, null, null, null);
			mStudentItems.add(studentItem);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.studentItemDao.insertOrReplaceInTx(mStudentItems);
				long endTime = System.currentTimeMillis();
				long hasTime = endTime - startTime;
				Log.i("初始化用时：", hasTime + "ms");
				Log.i("mStudentItems", mStudentItems.size() + "");
				Log.i("----------", "保存学生项目信息完成");
			}
		});
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app));
		builder.setAutoCancel(true);
		builder.setContentTitle("MyApp通知");
		builder.setContentText("数据初始化完成");
		builder.setTicker("数据初始化完成,可以开始测试");
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// 设置点击跳转
		Intent hangIntent = new Intent();
		// hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// hangIntent.setClass(context, MainActivity.class);
		// 如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
		PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setFullScreenIntent(hangPendingIntent, true);
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, builder.build());
	}

	/**
	 * 存储成绩到数据库
	 * 
	 * @param context
	 * @param stuCode
	 *            学生考号
	 * @param etChengji
	 *            成绩
	 * @param resultState
	 * @param code
	 *            项目机器代码
	 * @param tvTitle
	 *            项目名称
	 * @return
	 */
	public static int saveGradesDB(Context context, String stuCode, String etChengji, int resultState, String code,
			String tvTitle) {
		int RoundNo = 1;
		String itemCode = "";
		if (tvTitle.equals("身高") || tvTitle.equals("体重") || tvTitle.equals("1000米跑") || tvTitle.equals("800米跑")
				|| tvTitle.equals("左眼视力") || tvTitle.equals("右眼视力")) {
			itemCode = DbService.getInstance(context).queryItemByName(tvTitle).get(0).getItemCode();
		} else {
			itemCode = DbService.getInstance(context).queryItemByMachineCode(code).get(0).getItemCode();
		}

		StudentItem studentItems = DbService.getInstance(context).queryStudentItemByCode(stuCode, itemCode);
		if (studentItems==null) {
			return 0;
		}
		Long studentItemID = studentItems.getStudentItemID();
		List<RoundResult> round = DbService.getInstance(context).queryRoundResultByID(studentItemID);
		List<Integer> rounds = new ArrayList<>();

		if (round.size() != 0) {
			for (int i = 0; i < round.size(); i++) {
				rounds.add(round.get(i).getRoundNo());
			}
			Log.i("rounds", rounds.toString());
			RoundNo = Collections.max(rounds) + 1;
		}

		Integer currentResult = Integer.parseInt(etChengji);
		// 获取当前系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date curDate = new Date(java.lang.System.currentTimeMillis());// 获取当前时间
		String currentTime = formatter.format(curDate);
		Log.i("currentTime--->", currentTime);
		// 存储当前测试轮次
		String macAddress = NetUtil.getLocalMacAddressFromWifiInfo(context);
		RoundResult roundResult = new RoundResult(null, studentItemID, currentResult, RoundNo, currentTime, resultState,
				0, macAddress, null, null);
		DbService.getInstance(context).saveRoundResult(roundResult);

		return 1;

	}
}
