package com.hotelnow.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hotelnow.fragment.model.CityItem;
import com.hotelnow.fragment.model.KeyWordItem;
import com.hotelnow.fragment.model.RecentCityItem;
import com.hotelnow.fragment.model.SubCityItem;

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
    public void insertKeyword(String keyword) {
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
            String sql = "DELETE FROM "+ DataBases.Keyword_CreateDB._TABLENAME+" WHERE _id = " + key_id;
            mDB.execSQL(sql);
        }
        close();
    }

    /**
     * 지역 - INSERT
     *
     * @param city_ko 지역명
     * @param city_code 지역코드
     * @return
     */
    public void insertHotelCity(String city_ko, String city_code) {
        open();
        ContentValues val = new ContentValues();
        val.put("city_ko", city_ko);
        val.put("city_code", city_code);

        mDB.insert(DataBases.City_CreateDB._TABLENAME, null, val);
        close();
    }

    /**
     * 지역 - DELETE
     *
     * @return
     */
    public void deleteHotelCity() {
        open();

        mDB.delete(DataBases.City_CreateDB._TABLENAME,null, null);

        close();
    }

    /**
     * 지역 리스트 - SELECT ALL
     *
     * @return
     */
    public List<CityItem> selectAllCity() {
        open();
        List<CityItem> items = new ArrayList<CityItem>();
        Cursor cur = null;
        try {
            cur = mDB.query(DataBases.City_CreateDB._TABLENAME, new String[] { "city_ko", "city_code" }, null, null, null, null, null);

            if(cur.moveToFirst()) {
                do {
                    items.add(new CityItem(
                            cur.getString(cur.getColumnIndex("city_ko")),
                            cur.getString(cur.getColumnIndex("city_code"))
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
     * 지역서브 - DELETE
     *
     * @return
     */
    public void deleteHotelSubCity() {
        open();

        mDB.delete(DataBases.SubCity_CreateDB._TABLENAME,null, null);

        close();
    }

    /**
     * 지역서브 리스트 - SELECT ALL
     *
     * @return
     */
    public List<SubCityItem> selectAllSubCity(String city_code) {
        open();
        List<SubCityItem> items = new ArrayList<SubCityItem>();
        Cursor cur = null;
        try {
            cur = mDB.query(DataBases.SubCity_CreateDB._TABLENAME, new String[] { "city_code", "subcity_ko", "subcity_code" }, "city_code = '" + city_code + "'", null, null, null, null);

            if(cur.moveToFirst()) {
                do {
                    items.add(new SubCityItem(
                            cur.getString(cur.getColumnIndex("city_code")),
                            cur.getString(cur.getColumnIndex("subcity_ko")),
                            cur.getString(cur.getColumnIndex("subcity_code"))
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
     * 지역서브 - INSERT
     *
     * @param subcity_ko 지역명
     * @param subcity_code 지역코드
     * @param city_code 부모 지역
     * @return
     */
    public void insertHotelsubCity(String city_code, String subcity_ko, String subcity_code) {
        open();
        ContentValues val = new ContentValues();
        val.put("city_code", city_code);
        val.put("subcity_ko", subcity_ko);
        val.put("subcity_code", subcity_code);

        mDB.insert(DataBases.SubCity_CreateDB._TABLENAME, null, val);
        close();
    }

    /**
     * 최근 지역 선택 - INSERT
     *
     * @param sel_city_id 지역코드
     * @param sel_city_ko 지역명
     * @param sel_subcity_id 서브지역코드
     * @param sel_subcity_ko 서브지역명
     * @param sel_option 호텔 / 액티비티
     * @return
     */
    public void insertRecentCity(String sel_city_id, String sel_city_ko, String sel_subcity_id, String sel_subcity_ko, String sel_option) {
        open();
        ContentValues val = new ContentValues();
        val.put("sel_city_id", sel_city_id);
        val.put("sel_city_ko", sel_city_ko);
        val.put("sel_subcity_id", sel_subcity_id);
        val.put("sel_subcity_ko", sel_subcity_ko);
        val.put("sel_option", sel_option);
        Cursor cur = null;
        try {
            cur = mDB.query(DataBases.RecentCity_CreateDB._TABLENAME, new String[] { "created_date" }, "sel_option = '" + sel_option + "'", null, null, null, "created_date desc");
            if(cur.getCount()==5){
                String sql = "DELETE FROM "+ DataBases.RecentCity_CreateDB._TABLENAME+" WHERE created_date = "
                        + "(select MIN(created_date) from "+DataBases.RecentCity_CreateDB._TABLENAME+ " )";
                mDB.execSQL(sql);
            }
        }
        catch(Exception ex) {}

        mDB.insert(DataBases.RecentCity_CreateDB._TABLENAME, null, val);
        close();
    }

    /**
     * 최근 선택 지역 리스트 - SELECT ALL
     *
     * @return
     */
    public List<RecentCityItem> selectAllRecentCity(String option) {
        open();
        List<RecentCityItem> items = new ArrayList<RecentCityItem>();
        Cursor cur = null;
        try {
            cur = mDB.query(DataBases.RecentCity_CreateDB._TABLENAME, new String[] { "sel_city_id", "sel_city_ko", "sel_subcity_id", "sel_subcity_ko", "sel_option" }, "sel_option = '" + option + "'",
                    null, null, null, "created_date desc");

            if(cur.moveToFirst()) {
                do {
                    items.add(new RecentCityItem(
                            cur.getString(cur.getColumnIndex("sel_city_id")),
                            cur.getString(cur.getColumnIndex("sel_city_ko")),
                            cur.getString(cur.getColumnIndex("sel_subcity_id")),
                            cur.getString(cur.getColumnIndex("sel_subcity_ko")),
                            cur.getString(cur.getColumnIndex("sel_option"))
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

