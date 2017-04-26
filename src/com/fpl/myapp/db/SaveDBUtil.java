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
import ww.greendao.dao.Classes;
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
			Long SchoolID = DbService.getInstance(context).querySchoolByName(grades.get(i).getSchoolName().toString())
					.get(0).getSchoolID();
			Grade grade = new Grade(null, SchoolID, grades.get(i).getGradeCode(), grades.get(i).getGradeName(), null);
			DbService.getInstance(context).saveGrade(grade);
		}
		Log.i("----------", "保存年级信息完成");
		// 保存班级信息
		for (int i = 0; i < classes.size(); i++) {
			Long GradeID = DbService.getInstance(context).queryGradeByCode(classes.get(i).getGradeCode()).get(0)
					.getGradeID();
			Classes newClass = new Classes(null, GradeID, classes.get(i).getClassCode(), classes.get(i).getClassName(),
					null);
			DbService.getInstance(context).saveClasses(newClass);
		}
		Log.i("----------", "保存班级信息完成");
		// 保存学生信息
		for (int i = 0; i < students.size(); i++) {
			Long ClassID = DbService.getInstance(context).queryClassesByCode(students.get(i).getClassCode()).get(0)
					.getClassID();
			Long GradeID = DbService.getInstance(context).queryGradeByCode(students.get(i).getGradeCode()).get(0)
					.getGradeID();
			Student student = new Student(null, students.get(i).getStudentCode(), students.get(i).getStudentName(),
					students.get(i).getSex(), ClassID, GradeID, students.get(i).getIDCardNo(),
					students.get(i).getICCardNo(), students.get(i).getDownloadTime(), null, null, null);
			flag = DbService.getInstance(context).saveStudent(student);
		}
		Log.i("students", students.size() + "");
		Log.i("----------", "保存学生信息完成");
		if (students.size() != 0 && flag != -1) {
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
	 */
	public static void saveStudentItemDB(List<PH_StudentItem> studentItems, Context context) {
		Log.i("studentItems.size()=", studentItems.size() + "");
		for (PH_StudentItem stuItem : studentItems) {
			String itemCode = stuItem.getItemCode();
			String studentCode = stuItem.getStudentCode();
//			List<Item> items = DbService.getInstance(context).queryItemByCode(itemCode);
//			List<Student> stus = DbService.getInstance(context).queryStudentByCode(studentCode);
//			Long StudentID = null;
//			Long ItemID = null;
//			if (stus.size() != 0 && items.size() != 0) {
//				StudentID = stus.get(0).getStudentID();
//				ItemID = items.get(0).getItemID();
//			}
			StudentItem studentItem = new StudentItem(null, studentCode, itemCode, null, 0, null, 0, null, null, null);
			DbService.getInstance(context).saveStudentItem(studentItem);
		}
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
//		long studentID = DbService.getInstance(context).queryStudentByCode(stuCode).get(0).getStudentID();
//		long ItemID = -1;
		String itemCode="";
		if (tvTitle.equals("身高") || tvTitle.equals("体重") || tvTitle.equals("1000米跑") || tvTitle.equals("800米跑")
				|| tvTitle.equals("左眼视力") || tvTitle.equals("右眼视力")) {
			itemCode = DbService.getInstance(context).queryItemByName(tvTitle).get(0).getItemCode();
		} else {
			itemCode = DbService.getInstance(context).queryItemByMachineCode(code).get(0).getItemCode();
		}

		List<StudentItem> studentItems = DbService.getInstance(context).queryStudentItemByCode(stuCode, itemCode);
		if (studentItems.isEmpty()) {
			return 0;
		}
		Long studentItemID = studentItems.get(0).getStudentItemID();
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
