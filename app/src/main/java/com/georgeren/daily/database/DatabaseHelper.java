package com.georgeren.daily.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.georgeren.daily.InitApp;
import com.georgeren.daily.database.table.ZhuanlanTable;

/**
 * Created by georgeRen on 2017/8/29.
 * 自定义数据库
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Daily";
    private static final int DB_VERSION = 2;
    private static final String DROP_TABLE = "drop table if exists ";// 删除表
    private static DatabaseHelper instance = null;
    private static SQLiteDatabase db = null;

    private DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 安全
     * @return
     */
    private static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper(InitApp.AppContext, DB_NAME, null, DB_VERSION);
        }
        return instance;
    }

    /**
     * 安全
     * @return
     */
    public static synchronized SQLiteDatabase getDatabase() {
        if (db == null) {
            db = getInstance().getWritableDatabase();
        }
        return db;
    }

    /**
     * 创建 表
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ZhuanlanTable.CREATE_TABLE);// 创建一张 专栏信息表
    }

    /**
     * 数据库升级：先删除旧表 再 创建新表。缺点明显：旧数据没了？？
     * 根据版本升级：
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL(DROP_TABLE + ZhuanlanTable.TABLENAME);// 先删除旧表
            db.execSQL(ZhuanlanTable.CREATE_TABLE);// 创建表
        }
    }

}
