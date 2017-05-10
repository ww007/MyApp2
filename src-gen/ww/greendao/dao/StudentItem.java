package ww.greendao.dao;

import java.util.List;
import ww.greendao.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table STUDENT_ITEM.
 */
public class StudentItem {

    private Long StudentItemID;
    private String studentCode;
    private String itemCode;
    private Integer lastResult;
    private Integer resultState;
    private String lastTestTime;
    private Integer TestState;
    private String Remark1;
    private String Remark2;
    private String Remark3;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient StudentItemDao myDao;

    private List<RoundResult> roundResults;

    public StudentItem() {
    }

    public StudentItem(Long StudentItemID) {
        this.StudentItemID = StudentItemID;
    }

    public StudentItem(Long StudentItemID, String studentCode, String itemCode, Integer lastResult, Integer resultState, String lastTestTime, Integer TestState, String Remark1, String Remark2, String Remark3) {
        this.StudentItemID = StudentItemID;
        this.studentCode = studentCode;
        this.itemCode = itemCode;
        this.lastResult = lastResult;
        this.resultState = resultState;
        this.lastTestTime = lastTestTime;
        this.TestState = TestState;
        this.Remark1 = Remark1;
        this.Remark2 = Remark2;
        this.Remark3 = Remark3;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStudentItemDao() : null;
    }

    public Long getStudentItemID() {
        return StudentItemID;
    }

    public void setStudentItemID(Long StudentItemID) {
        this.StudentItemID = StudentItemID;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Integer getLastResult() {
        return lastResult;
    }

    public void setLastResult(Integer lastResult) {
        this.lastResult = lastResult;
    }

    public Integer getResultState() {
        return resultState;
    }

    public void setResultState(Integer resultState) {
        this.resultState = resultState;
    }

    public String getLastTestTime() {
        return lastTestTime;
    }

    public void setLastTestTime(String lastTestTime) {
        this.lastTestTime = lastTestTime;
    }

    public Integer getTestState() {
        return TestState;
    }

    public void setTestState(Integer TestState) {
        this.TestState = TestState;
    }

    public String getRemark1() {
        return Remark1;
    }

    public void setRemark1(String Remark1) {
        this.Remark1 = Remark1;
    }

    public String getRemark2() {
        return Remark2;
    }

    public void setRemark2(String Remark2) {
        this.Remark2 = Remark2;
    }

    public String getRemark3() {
        return Remark3;
    }

    public void setRemark3(String Remark3) {
        this.Remark3 = Remark3;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<RoundResult> getRoundResults() {
        if (roundResults == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RoundResultDao targetDao = daoSession.getRoundResultDao();
            List<RoundResult> roundResultsNew = targetDao._queryStudentItem_RoundResults(StudentItemID);
            synchronized (this) {
                if(roundResults == null) {
                    roundResults = roundResultsNew;
                }
            }
        }
        return roundResults;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetRoundResults() {
        roundResults = null;
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
