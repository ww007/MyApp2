package ww.greendao.dao;

import ww.greendao.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ROUND_RESULT.
 */
public class RoundResult {

	private Long RoundResultID;
	private long StudentItemID;
	private String studentCode;
	private String itemCode;
	private Integer Result;
	private Integer RoundNo;
	private String TestTime;
	private Integer ResultState;
	private Integer IsLastResult;
	private String Mac;
	private String Remark1;
	private String Remark2;

	/** Used to resolve relations */
	private transient DaoSession daoSession;

	/** Used for active entity operations. */
	private transient RoundResultDao myDao;

	private StudentItem studentItem;
	private Long studentItem__resolvedKey;

	public RoundResult() {
	}

	public RoundResult(Long RoundResultID) {
		this.RoundResultID = RoundResultID;
	}

	public RoundResult(Long RoundResultID, long StudentItemID, String studentCode, String itemCode, Integer Result,
			Integer RoundNo, String TestTime, Integer ResultState, Integer IsLastResult, String Mac, String Remark1,
			String Remark2) {
		this.RoundResultID = RoundResultID;
		this.StudentItemID = StudentItemID;
		this.studentCode = studentCode;
		this.itemCode = itemCode;
		this.Result = Result;
		this.RoundNo = RoundNo;
		this.TestTime = TestTime;
		this.ResultState = ResultState;
		this.IsLastResult = IsLastResult;
		this.Mac = Mac;
		this.Remark1 = Remark1;
		this.Remark2 = Remark2;
	}

	/** called by internal mechanisms, do not call yourself. */
	public void __setDaoSession(DaoSession daoSession) {
		this.daoSession = daoSession;
		myDao = daoSession != null ? daoSession.getRoundResultDao() : null;
	}

	public Long getRoundResultID() {
		return RoundResultID;
	}

	public void setRoundResultID(Long RoundResultID) {
		this.RoundResultID = RoundResultID;
	}

	public long getStudentItemID() {
		return StudentItemID;
	}

	public void setStudentItemID(long StudentItemID) {
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

	public Integer getResult() {
		return Result;
	}

	public void setResult(Integer Result) {
		this.Result = Result;
	}

	public Integer getRoundNo() {
		return RoundNo;
	}

	public void setRoundNo(Integer RoundNo) {
		this.RoundNo = RoundNo;
	}

	public String getTestTime() {
		return TestTime;
	}

	public void setTestTime(String TestTime) {
		this.TestTime = TestTime;
	}

	public Integer getResultState() {
		return ResultState;
	}

	public void setResultState(Integer ResultState) {
		this.ResultState = ResultState;
	}

	public Integer getIsLastResult() {
		return IsLastResult;
	}

	public void setIsLastResult(Integer IsLastResult) {
		this.IsLastResult = IsLastResult;
	}

	public String getMac() {
		return Mac;
	}

	public void setMac(String Mac) {
		this.Mac = Mac;
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

	/** To-one relationship, resolved on first access. */
	public StudentItem getStudentItem() {
		long __key = this.StudentItemID;
		if (studentItem__resolvedKey == null || !studentItem__resolvedKey.equals(__key)) {
			if (daoSession == null) {
				throw new DaoException("Entity is detached from DAO context");
			}
			StudentItemDao targetDao = daoSession.getStudentItemDao();
			StudentItem studentItemNew = targetDao.load(__key);
			synchronized (this) {
				studentItem = studentItemNew;
				studentItem__resolvedKey = __key;
			}
		}
		return studentItem;
	}

	public void setStudentItem(StudentItem studentItem) {
		if (studentItem == null) {
			throw new DaoException(
					"To-one property 'StudentItemID' has not-null constraint; cannot set to-one to null");
		}
		synchronized (this) {
			this.studentItem = studentItem;
			StudentItemID = studentItem.getStudentItemID();
			studentItem__resolvedKey = StudentItemID;
		}
	}

	/**
	 * Convenient call for {@link AbstractDao#delete(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void delete() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.delete(this);
	}

	/**
	 * Convenient call for {@link AbstractDao#update(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void update() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.update(this);
	}

	/**
	 * Convenient call for {@link AbstractDao#refresh(Object)}. Entity must
	 * attached to an entity context.
	 */
	public void refresh() {
		if (myDao == null) {
			throw new DaoException("Entity is detached from DAO context");
		}
		myDao.refresh(this);
	}

	@Override
	public String toString() {
		return "RoundResult [RoundResultID=" + RoundResultID + ", StudentItemID=" + StudentItemID + ", studentCode="
				+ studentCode + ", itemCode=" + itemCode + ", Result=" + Result + ", RoundNo=" + RoundNo + ", TestTime="
				+ TestTime + ", ResultState=" + ResultState + ", IsLastResult=" + IsLastResult + ", Mac=" + Mac
				+ ", Remark1=" + Remark1 + ", Remark2=" + Remark2 + ", studentItem=" + studentItem
				+ ", studentItem__resolvedKey=" + studentItem__resolvedKey + "]";
	}

}
