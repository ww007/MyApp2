package ww.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import ww.greendao.dao.School;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SCHOOL.
*/
public class SchoolDao extends AbstractDao<School, Long> {

    public static final String TABLENAME = "SCHOOL";

    /**
     * Properties of entity School.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property SchoolID = new Property(0, Long.class, "SchoolID", true, "SCHOOL_ID");
        public final static Property SchoolName = new Property(1, String.class, "SchoolName", false, "SCHOOL_NAME");
        public final static Property SchoolYear = new Property(2, String.class, "SchoolYear", false, "SCHOOL_YEAR");
        public final static Property Remark1 = new Property(3, String.class, "Remark1", false, "REMARK1");
    };

    private DaoSession daoSession;


    public SchoolDao(DaoConfig config) {
        super(config);
    }
    
    public SchoolDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SCHOOL' (" + //
                "'SCHOOL_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: SchoolID
                "'SCHOOL_NAME' TEXT NOT NULL UNIQUE ," + // 1: SchoolName
                "'SCHOOL_YEAR' TEXT," + // 2: SchoolYear
                "'REMARK1' TEXT);"); // 3: Remark1
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SCHOOL'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, School entity) {
        stmt.clearBindings();
 
        Long SchoolID = entity.getSchoolID();
        if (SchoolID != null) {
            stmt.bindLong(1, SchoolID);
        }
        stmt.bindString(2, entity.getSchoolName());
 
        String SchoolYear = entity.getSchoolYear();
        if (SchoolYear != null) {
            stmt.bindString(3, SchoolYear);
        }
 
        String Remark1 = entity.getRemark1();
        if (Remark1 != null) {
            stmt.bindString(4, Remark1);
        }
    }

    @Override
    protected void attachEntity(School entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public School readEntity(Cursor cursor, int offset) {
        School entity = new School( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // SchoolID
            cursor.getString(offset + 1), // SchoolName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // SchoolYear
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // Remark1
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, School entity, int offset) {
        entity.setSchoolID(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSchoolName(cursor.getString(offset + 1));
        entity.setSchoolYear(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRemark1(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(School entity, long rowId) {
        entity.setSchoolID(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(School entity) {
        if(entity != null) {
            return entity.getSchoolID();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
