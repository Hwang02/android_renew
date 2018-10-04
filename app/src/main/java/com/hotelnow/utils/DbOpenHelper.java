package com.hotelnow.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelnow.fragment.model.KeyWordItem;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "hotelnow.db";
    private static final int DATABASE_VERSION = 1;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper{

        // 생성자
        public DatabaseHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DataBases.Keyword_CreateDB._CREATE);
            db.execSQL(DataBases.City_CreateDB._CREATE);
            db.execSQL(DataBases.SubCity_CreateDB._CREATE);
            db.execSQL(DataBases.qCity_CreateDB._CREATE);
            db.execSQL(DataBases.qCategory_CreateDB._CREATE);
            db.execSQL(DataBases.RecentList_CreateDB._CREATE);
            db.execSQL(DataBases.RecentCity_CreateDB._CREATE);
        }

        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.Keyword_CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.City_CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.SubCity_CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.qCity_CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.qCategory_CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.RecentList_CreateDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS "+ DataBases.RecentCity_CreateDB._TABLENAME);
            onCreate(db);
        }
    }

    /**
     * 최근 검색어 - INSERT
     *
     * @param keyword
     * @return
     */
    public void insertKeywordArea(String keyword) {
        open();
        ContentValues val = new ContentValues();
        val.put("keyword", keyword);
        Cursor cur = null;
        try {
            cur = mDB.query(DataBases.Keyword_CreateDB._TABLENAME, new String[] { _ID, "keyword" }, null, null, null, null, _ID+" desc");
            if(cur.getCount()==5){
                String sql = "DELETE FROM "+ DataBases.Keyword_CreateDB._TABLENAME+" WHERE _id = "
                        + "(select MIN(_id) from "+DataBases.Keyword_CreateDB._TABLENAME+ " )";
                mDB.execSQL(sql);
            }
        }
        catch(Exception ex) {}
        mDB.insert(DataBases.Keyword_CreateDB._TABLENAME, null, val);
        close();
    }

    /**
     * 최근검색어 리스트 - SELECT ALL
     *
     * @return
     */
    public List<KeyWordItem> selectAllKeyword() {
        open();
        List<KeyWordItem> items = new ArrayList<KeyWordItem>();
        Cursor cur = null;
        try {
            cur = mDB.query(DataBases.Keyword_CreateDB._TABLENAME, new String[] { _ID, "keyword" }, null, null, null, null, _ID+" desc");

            if(cur.moveToFirst()) {
                do {
                    items.add(new KeyWordItem(
                            cur.getInt(cur.getColumnIndex(_ID)),
                            cur.getString(cur.getColumnIndex("keyword"))
                    ));
                }
                while(cur.moveToNext());
            }
        }
        catch(Exception ex) {}
        finally {
            if(cur != null) {
                cur.close();
            }
            close();
        }
        return items;
    }

    /**
     * 최근 검색어 - DELETE
     *
     * @param key_id
     */
    public void deleteKeyword(String key_id, boolean isAll) {
        open();
        if(isAll) {
            mDB.delete(DataBases.Keyword_CreateDB._TABLENAME,null, null);
        }
        else{
//            mDB.delete(DataBases.Keyword_CreateDB._TABLENAME, _ID+" = '" + key_id + "'", null);
            String sql = "DELETE FROM "+ DataBases.Keyword_CreateDB._TABLENAME+" WHERE _id = " + key_id;
            mDB.execSQL(sql);
        }
        close();
    }




    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        mDB.close();
    }

}

