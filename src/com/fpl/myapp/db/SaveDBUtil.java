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
import com.fpl.myapp.activity.MainActivity;
import com.fpl.myapp.activity.SplashScreenActivity;
import com.fpl.myapp.entity.PH_Class;
import com.fpl.myapp.entity.PH_Grade;
import com.fpl.myapp.entity.PH_School;
import com.fpl.myapp.entity.PH_Student;
import com.fpl.myapp.entity.PH_StudentItem;
import com.fpl.myapp.util.HttpUtil;
import com.fpl.myapp.util.NetUtil;
import com.wnb.android.nfc.dataobject.entity.IC_Result;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
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
	public static int over = 0;
	private static NotificationManager notificationManager;
	private static ArrayList<Student> mStudents = new ArrayList<>();
	private static AsyncSession mAsyncSession = DbService.mDaoSession.startAsyncSession();
	private static ArrayList<StudentItem> mStudentItems;
	private static ArrayList<Classes> mClasses;
	private static ArrayList<Grade> mGrade;

	/**
	 * ���ݿ��в�ѯ���гɼ�������IC��
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
		// �����ɼ����浽result[0]
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == Collections.max(list)) {
				result[0] = new IC_Result(list.get(i), 1, 0, 0);
				list.remove(i);
			}
		}
		// ����ȥ���ɼ��������ɼ�����
		for (int i = 0; i < list.size(); i++) {
			result[i + 1] = new IC_Result(list.get(i), 1, 0, 0);
		}
	}

	/**
	 * �洢ѧУ���༶���꼶�����ݿ�
	 * 
	 * @param response
	 * @param context
	 */
	public static void saveStudentDB(String response, Context context) {
		mClasses = new ArrayList<Classes>();
		mGrade = new ArrayList<Grade>();
		// ������ȡ��Json����
		JSONObject jsonObject = JSON.parseObject(response);
		JSONArray jsonSchool = jsonObject.getJSONArray("school");
		JSONArray jsonClass = jsonObject.getJSONArray("class");
		JSONArray jsonGrade = jsonObject.getJSONArray("grade");
		schools = JSON.parseArray(jsonSchool.toJSONString(), PH_School.class);
		classes = JSON.parseArray(jsonClass.toJSONString(), PH_Class.class);
		grades = JSON.parseArray(jsonGrade.toJSONString(), PH_Grade.class);

		// ����ѧУ��Ϣ
		for (int i = 0; i < schools.size(); i++) {
			School school = new School(null, schools.get(i).getSchoolName(), schools.get(i).getSchoolYear(), null);
			DbService.getInstance(context).saveSchool(school);
		}
		Log.i("----------", "����ѧУ��Ϣ���");
		// �����꼶��Ϣ
		for (int i = 0; i < grades.size(); i++) {
			String schoolName = grades.get(i).getSchoolName();
			String gradeCode = grades.get(i).getGradeCode();
			String gradeName = grades.get(i).getGradeName();
			Long SchoolID = DbService.getInstance(context).querySchoolByName(schoolName).getSchoolID();
			Grade grade = new Grade(null, SchoolID, gradeCode, gradeName, null);
			mGrade.add(grade);
		}
		DbService.gradeDao.insertOrReplaceInTx(mGrade);
		Log.i("mGrade", mGrade.size() + "");
		Log.i("----------", "�����꼶��Ϣ���");
		// mAsyncSession.runInTx(new Runnable() {
		// @Override
		// public void run() {
		// DbService.gradeDao.insertOrReplaceInTx(mGrade);
		// Log.i("mGrade", mGrade.size() + "");
		// Log.i("----------", "�����꼶��Ϣ���");
		// }
		// });
		// ����༶��Ϣ
		for (int i = 0; i < classes.size(); i++) {
			String gradeCode = classes.get(i).getGradeCode();
			String classCode = classes.get(i).getClassCode();
			String className = classes.get(i).getClassName();
			long gradeID = DbService.getInstance(context).queryGradeByCode(gradeCode).getGradeID();
			Classes newClass = new Classes(null, gradeID, classCode, className, null);
			// if(context.getClass().equals(SplashScreenActivity.class)) {
			// SplashScreenActivity.handleUI(((i + 1) * 100) / classes.size());
			// }
			mClasses.add(newClass);
		}
		mAsyncSession.runInTx(new Runnable() {
			@Override
			public void run() {
				DbService.classesDao.insertOrReplaceInTx(mClasses);
				Log.i("mClasses", mClasses.size() + "");
				Log.i("----------", "����༶��Ϣ���");
			}
		});
	}

	/**
	 * �洢ѧ����Ϣ�����ݿ�
	 * 
	 * @param context
	 * @param students
	 */
	public static void saveStudentPage(final Context context, final List<PH_Student> students) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// ����ѧ����Ϣ
				Log.i("students.size()---", students.size() + "");
				for (int i = 0; i < students.size(); i++) {
					String calssCode = students.get(i).getClassCode();
					String gradeCode = students.get(i).getGradeCode();
					Long classID = DbService.getInstance(context).queryClassesByCode(calssCode).getClassID();
					Long gradeID = DbService.getInstance(context).queryGradeByCode(gradeCode).getGradeID();
					Student student = new Student(null, students.get(i).getStudentCode(),
							students.get(i).getStudentName(), students.get(i).getSex(), classID, gradeID,
							students.get(i).getIDCardNo(), students.get(i).getICCardNo(),
							students.get(i).getDownloadTime(), null, null, null);
					mStudents.add(student);
				}
				mAsyncSession.runInTx(new Runnable() {
					@Override
					public void run() {
						DbService.studentDao.insertOrReplaceInTx(mStudents);
						Log.i("students", mStudents.size() + "");
						Log.i("----------", "����ѧ����Ϣ���");
					}
				});
			}
		}).start();

	}

	/**
	 * �洢ѧ����Ŀ��Ϣ�����ݿ�
	 * 
	 * @param studentItems
	 *            ��ȡ����ѧ����Ŀ��Ϣ
	 * @param context
	 * @param i
	 * @param j
	 * @param totalPage
	 */
	public static void saveStudentItemDB(final List<PH_StudentItem> studentItems, final Context context,
			final int totalPage, final int currentPage) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mStudentItems = new ArrayList<>();
				for (PH_StudentItem stuItem : studentItems) {
					String itemCode = stuItem.getItemCode();
					String studentCode = stuItem.getStudentCode();
					StudentItem studentItem = new StudentItem(null, studentCode, itemCode, null, 0, null, 0, null, null,
							null);
					mStudentItems.add(studentItem);
				}

				mAsyncSession.runInTx(new Runnable() {
					@Override
					public void run() {
						if (context.getClass().equals(SplashScreenActivity.class)) {
							DbService.studentItemDao.insertOrReplaceInTx(mStudentItems);
							Intent intent = new Intent(context, MainActivity.class);
							context.startActivity(intent);
						}
						Log.i("currentPage----------", "����ѧ����Ŀ��Ϣ���");
						Log.i("mStudentItems", mStudentItems.size() + "");
						showNotification(context);
					}
				});
			}
		}).start();
	}

	/**
	 * �������ر��������ʾ��Ϣ
	 * 
	 * @param context
	 */
	private static void showNotification(final Context context) {
		long hasTime = System.currentTimeMillis() - HttpUtil.startTime;
		Log.i("��ʼ����ʱ��", hasTime + "");
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(R.drawable.app);
		builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app));
		builder.setAutoCancel(true);
		builder.setContentTitle("MyApp֪ͨ");
		builder.setContentText("���ݳ�ʼ�����");
		builder.setTicker("���ݳ�ʼ�����,���Կ�ʼ����");
		builder.setDefaults(Notification.DEFAULT_SOUND);
		// ���õ����ת
		Intent hangIntent = new Intent();
		// hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// hangIntent.setClass(context, MainActivity.class);
		// ���������PendingIntent�Ѿ����ڣ����ڲ����µ�Intent֮ǰ����ȡ������ǰ��
		PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		builder.setFullScreenIntent(hangPendingIntent, true);
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, builder.build());
	}

	/**
	 * �洢�ɼ������ݿ�
	 * 
	 * @param context
	 * @param stuCode
	 *            ѧ������
	 * @param etChengji
	 *            �ɼ�
	 * @param resultState
	 * @param code
	 *            ��Ŀ��������
	 * @param tvTitle
	 *            ��Ŀ����
	 * @return
	 */
	public static int saveGradesDB(Context context, String stuCode, String etChengji, int resultState, String code,
			String tvTitle) {
		int RoundNo = 1;
		String itemCode = "";
		if (tvTitle.equals("���") || tvTitle.equals("����") || tvTitle.equals("1000����") || tvTitle.equals("800����")
				|| tvTitle.equals("��������") || tvTitle.equals("��������")) {
			itemCode = DbService.getInstance(context).queryItemByName(tvTitle).getItemCode();
			// itemID =
			// DbService.getInstance(context).queryItemByName(tvTitle).getItemID();
		} else {
			itemCode = DbService.getInstance(context).queryItemByMachineCode(code).getItemCode();
			// itemID =
			// DbService.getInstance(context).queryItemByMachineCode(code).getItemID();
		}

		// long stuID =
		// DbService.getInstance(context).queryStudentByCode(stuCode).get(0).getStudentID();
		StudentItem studentItems = DbService.getInstance(context).queryStudentItemByCode(stuCode, itemCode);
		if (studentItems == null) {
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
		// ��ȡ��ǰϵͳʱ��
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
		Date curDate = new Date(java.lang.System.currentTimeMillis());// ��ȡ��ǰʱ��
		String currentTime = formatter.format(curDate);
		Log.i("currentTime--->", currentTime);
		// �洢��ǰ�����ִ�
		String macAddress = NetUtil.getLocalMacAddressFromWifiInfo(context);
		RoundResult roundResult = new RoundResult(null, studentItemID, stuCode, itemCode, currentResult, RoundNo,
				currentTime, resultState, 0, macAddress, null, null);
		DbService.getInstance(context).saveRoundResult(roundResult);

		return 1;

	}
}
