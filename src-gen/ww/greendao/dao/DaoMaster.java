package ww.greendao.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import ww.greendao.dao.ItemDao;
import ww.greendao.dao.SchoolDao;
import ww.greendao.dao.GradeDao;
import ww.greendao.dao.ClassesDao;
import ww.greendao.dao.StudentDao;
import ww.greendao.dao.StudentItemDao;
import ww.greendao.dao.RoundResultDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 4): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 4;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        ItemDao.createTable(db, ifNotExists);
        SchoolDao.createTable(db, ifNotExists);
        GradeDao.createTable(db, ifNotExists);
        ClassesDao.createTable(db, ifNotExists);
        StudentDao.createTable(db, ifNotExists);
        StudentItemDao.createTable(db, ifNotExists);
        RoundResultDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        ItemDao.dropTable(db, ifExists);
        SchoolDao.dropTable(db, ifExists);
        GradeDao.dropTable(db, ifExists);
        ClassesDao.dropTable(db, ifExists);
        StudentDao.dropTable(db, ifExists);
        StudentItemDao.dropTable(db, ifExists);
        RoundResultDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(ItemDao.class);
        registerDaoClass(SchoolDao.class);
        registerDaoClass(GradeDao.class);
        registerDaoClass(ClassesDao.class);
        registerDaoClass(StudentDao.class);
        registerDaoClass(StudentItemDao.class);
        registerDaoClass(RoundResultDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
