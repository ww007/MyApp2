package com.fpl.myapp.db;

import java.util.List;

import android.content.Context;
import de.greenrobot.dao.query.QueryBuilder;
import ww.greendao.dao.Classes;
import ww.greendao.dao.ClassesDao;
import ww.greendao.dao.DaoSession;
import ww.greendao.dao.Grade;
import ww.greendao.dao.GradeDao;
import ww.greendao.dao.Item;
import ww.greendao.dao.ItemDao;
import ww.greendao.dao.RoundResult;
import ww.greendao.dao.RoundResultDao;
import ww.greendao.dao.School;
import ww.greendao.dao.SchoolDao;
import ww.greendao.dao.Student;
import ww.greendao.dao.StudentDao;
import ww.greendao.dao.StudentItem;
import ww.greendao.dao.StudentItemDao;

public class DbService {
	private static DbService instance;
	private static Context appContext;
	private DaoSession mDaoSession;
	private ClassesDao classesDao;
	private GradeDao gradeDao;
	private ItemDao itemDao;
	private RoundResultDao roundResultDao;
	private SchoolDao schoolDao;
	private StudentDao studentDao;
	private StudentItemDao studentItemDao;

	public static DbService getInstance(Context context) {
		if (instance == null) {
			instance = new DbService();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.mDaoSession = GreenDaoHelper.getDaoSession(context);
			instance.classesDao = instance.mDaoSession.getClassesDao();
			instance.gradeDao = instance.mDaoSession.getGradeDao();
			instance.itemDao = instance.mDaoSession.getItemDao();
			instance.roundResultDao = instance.mDaoSession.getRoundResultDao();
			instance.schoolDao = instance.mDaoSession.getSchoolDao();
			instance.studentDao = instance.mDaoSession.getStudentDao();
			instance.studentItemDao = instance.mDaoSession.getStudentItemDao();
		}
		return instance;
	}

	// -------------------------------
	public List<Classes> queryClassesByCode(String code) {
		QueryBuilder<Classes> qb = classesDao.queryBuilder();
		List<Classes> classes = qb.where(ClassesDao.Properties.ClassCode.eq(code)).list();
		return classes;
	}

	public List<Grade> queryGradeByCode(String code) {
		QueryBuilder<Grade> qb = gradeDao.queryBuilder();
		List<Grade> grades = qb.where(GradeDao.Properties.GradeCode.eq(code)).list();
		return grades;
	}

	public List<Item> queryItemByCode(String code) {
		QueryBuilder<Item> qb = itemDao.queryBuilder();
		List<Item> items = qb.where(ItemDao.Properties.ItemCode.eq(code)).list();
		return items;
	}

	public List<Item> queryItemByName(String name) {
		QueryBuilder<Item> qb = itemDao.queryBuilder();
		List<Item> items = qb.where(ItemDao.Properties.ItemName.eq(name)).list();
		return items;
	}

	public List<Item> queryItemByMachineCode(String code) {
		QueryBuilder<Item> qb = itemDao.queryBuilder();
		List<Item> items = qb.where(ItemDao.Properties.MachineCode.eq(code)).list();
		return items;
	}

	public List<Student> queryStudentByCode(String code) {
		QueryBuilder<Student> qb = studentDao.queryBuilder();
		List<Student> students = qb.where(StudentDao.Properties.StudentCode.eq(code)).list();
		return students;
	}
	public List<School> querySchoolByName(String name) {
		QueryBuilder<School> qb = schoolDao.queryBuilder();
		List<School> schools = qb.where(SchoolDao.Properties.SchoolName.eq(name)).list();
		return schools;
	}

	public List<RoundResult> queryRoundResultByID(long stuItemID) {
		QueryBuilder<RoundResult> qb = roundResultDao.queryBuilder();
		List<RoundResult> roundResults = qb.where(RoundResultDao.Properties.StudentItemID.eq(stuItemID)).list();
		return roundResults;
	}

	public List<RoundResult> queryRoundResultByResult(int result) {
		QueryBuilder<RoundResult> qb = roundResultDao.queryBuilder();
		List<RoundResult> roundResults = qb.where(RoundResultDao.Properties.Result.eq(result)).list();
		return roundResults;
	}

	public List<StudentItem> queryStudentItemByCode(String stuCode, String itemCode) {
		QueryBuilder<StudentItem> qb = studentItemDao.queryBuilder();
		qb.where(qb.and(StudentItemDao.Properties.StudentCode.eq(stuCode), StudentItemDao.Properties.ItemCode.eq(itemCode)));
		return qb.list();
	}

	public Classes loadClasses(long id) {
		return classesDao.load(id);
	}

	public Grade loadGrade(long id) {
		return gradeDao.load(id);
	}

	public Item loadItem(long id) {
		return itemDao.load(id);
	}

	public RoundResult loadRoundResult(long id) {
		return roundResultDao.load(id);
	}

	public School loadSchool(long id) {
		return schoolDao.load(id);
	}

	public Student loadStudent(long id) {
		return studentDao.load(id);
	}

	public StudentItem loadStudentItem(long id) {
		return studentItemDao.load(id);
	}

	// -----------------------------------
	public List<Classes> loadAllClasses() {
		return classesDao.loadAll();
	}

	public List<Grade> loadAllGrade() {
		return gradeDao.loadAll();
	}

	public List<Item> loadAllItem() {
		return itemDao.loadAll();
	}

	public List<RoundResult> loadAllRoundResult() {
		return roundResultDao.loadAll();
	}

	public List<School> loadAllSchool() {
		return schoolDao.loadAll();
	}

	public List<Student> loadAllStudent() {
		return studentDao.loadAll();
	}

	public List<StudentItem> loadAllStudentItem() {
		return studentItemDao.loadAll();
	}

	// ------------------------------------------
	public List<Classes> queryClasses(String where, String... params) {
		return classesDao.queryRaw(where, params);
	}

	public List<Grade> queryGrade(String where, String... params) {
		return gradeDao.queryRaw(where, params);
	}

	public List<Item> queryItem(String where, String... params) {
		return itemDao.queryRaw(where, params);
	}

	public List<RoundResult> queryRoundResult(String where, String... params) {
		return roundResultDao.queryRaw(where, params);
	}

	public List<School> querySchool(String where, String... params) {
		return schoolDao.queryRaw(where, params);
	}

	public List<Student> queryStudent(String where, String... params) {
		return studentDao.queryRaw(where, params);
	}

	public List<StudentItem> queryStudentItem(String where, String... params) {
		return studentItemDao.queryRaw(where, params);
	}

	// -----------------------------------
	public long saveClasses(Classes classes) {
		return classesDao.insertOrReplace(classes);
	}

	public long saveGrade(Grade grade) {
		return gradeDao.insertOrReplace(grade);
	}

	public long saveItem(Item item) {
		return itemDao.insertOrReplace(item);
	}

	public long saveRoundResult(RoundResult roundResult) {
		return roundResultDao.insertOrReplace(roundResult);
	}

	public long saveSchool(School school) {
		return schoolDao.insertOrReplace(school);
	}

	public long saveStudent(Student student) {
		return studentDao.insertOrReplace(student);
	}

	public long saveStudentItem(StudentItem studentItem) {
		return studentItemDao.insertOrReplace(studentItem);
	}

	// --------------------------------------------------------
	public void saveClassesLists(final List<Classes> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		classesDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					Classes classes = list.get(i);
					classesDao.insertOrReplace(classes);
				}
			}
		});

	}

	public void saveGradeLists(final List<Grade> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		gradeDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					Grade grade = list.get(i);
					gradeDao.insertOrReplace(grade);
				}
			}
		});

	}

	public void saveItemLists(final List<Item> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		itemDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					Item item = list.get(i);
					itemDao.insertOrReplace(item);
				}
			}
		});

	}

	public void saveRoundResultLists(final List<RoundResult> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		roundResultDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					RoundResult roundResult = list.get(i);
					roundResultDao.insertOrReplace(roundResult);
				}
			}
		});

	}

	public void saveSchoolLists(final List<School> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		schoolDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					School school = list.get(i);
					schoolDao.insertOrReplace(school);
				}
			}
		});

	}

	public void saveStudentLists(final List<Student> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		studentDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					Student student = list.get(i);
					studentDao.insertOrReplace(student);
				}
			}
		});

	}

	public void saveStudentItemLists(final List<StudentItem> list) {
		if (list == null || list.isEmpty()) {
			return;
		}
		studentItemDao.getSession().runInTx(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < list.size(); i++) {
					StudentItem studentItem = list.get(i);
					studentItemDao.insertOrReplace(studentItem);
				}
			}
		});

	}

	// ----------------------------------
	public void deleteAllClasses() {
		classesDao.deleteAll();
	}

	public void deleteAllGrade() {
		gradeDao.deleteAll();
	}

	public void deleteAllItem() {
		itemDao.deleteAll();
	}

	public void deleteAllRoundResult() {
		roundResultDao.deleteAll();
	}

	public void deleteAllSchool() {
		schoolDao.deleteAll();
	}

	public void deleteAllStudent() {
		studentDao.deleteAll();
	}

	public void deleteAllStudentItem() {
		studentItemDao.deleteAll();
	}

	// ---------------------------------------
	public void deleteClasses(long id) {
		classesDao.deleteByKey(id);
	}

	public void deleteGrade(long id) {
		gradeDao.deleteByKey(id);
	}

	public void deleteItem(long id) {
		itemDao.deleteByKey(id);
	}

	public void deleteRoundResult(long id) {
		roundResultDao.deleteByKey(id);
	}

	public void deleteSchool(long id) {
		schoolDao.deleteByKey(id);
	}

	public void deleteStudent(long id) {
		studentDao.deleteByKey(id);
	}

	public void deleteStudentItem(long id) {
		studentItemDao.deleteByKey(id);
	}

	// -----------------------------------------
	public void deleteClasses(Classes classes) {
		classesDao.delete(classes);
	}

	public void deleteGrade(Grade grade) {
		gradeDao.delete(grade);
	}

	public void deleteItem(Item item) {
		itemDao.delete(item);
	}

	public void deleteRoundResult(RoundResult roundResult) {
		roundResultDao.delete(roundResult);
	}

	public void deleteSchool(School school) {
		schoolDao.delete(school);
	}

	public void deleteStudent(Student student) {
		studentDao.delete(student);
	}

	public void deleteStudentItem(StudentItem studentItem) {
		studentItemDao.delete(studentItem);
	}
}
