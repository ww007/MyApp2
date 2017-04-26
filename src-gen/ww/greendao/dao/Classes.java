package ww.greendao.dao;

import ww.greendao.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CLASSES.
 */
public class Classes {

    private Long ClassID;
    private long GradeID;
    private String ClassCode;
    private String ClassName;
    private String Remark1;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ClassesDao myDao;

    private Grade grade;
    private Long grade__resolvedKey;


    public Classes() {
    }

    public Classes(Long ClassID) {
        this.ClassID = ClassID;
    }

    public Classes(Long ClassID, long GradeID, String ClassCode, String ClassName, String Remark1) {
        this.ClassID = ClassID;
        this.GradeID = GradeID;
        this.ClassCode = ClassCode;
        this.ClassName = ClassName;
        this.Remark1 = Remark1;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClassesDao() : null;
    }

    public Long getClassID() {
        return ClassID;
    }

    public void setClassID(Long ClassID) {
        this.ClassID = ClassID;
    }

    public long getGradeID() {
        return GradeID;
    }

    public void setGradeID(long GradeID) {
        this.GradeID = GradeID;
    }

    public String getClassCode() {
        return ClassCode;
    }

    public void setClassCode(String ClassCode) {
        this.ClassCode = ClassCode;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String ClassName) {
        this.ClassName = ClassName;
    }

    public String getRemark1() {
        return Remark1;
    }

    public void setRemark1(String Remark1) {
        this.Remark1 = Remark1;
    }

    /** To-one relationship, resolved on first access. */
    public Grade getGrade() {
        long __key = this.GradeID;
        if (grade__resolvedKey == null || !grade__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GradeDao targetDao = daoSession.getGradeDao();
            Grade gradeNew = targetDao.load(__key);
            synchronized (this) {
                grade = gradeNew;
            	grade__resolvedKey = __key;
            }
        }
        return grade;
    }

    public void setGrade(Grade grade) {
        if (grade == null) {
            throw new DaoException("To-one property 'GradeID' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.grade = grade;
            GradeID = grade.getGradeID();
            grade__resolvedKey = GradeID;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
