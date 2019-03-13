package com.hotelnow.utils;

import android.provider.BaseColumns;

public class DataBases {

    // 호텔 시티
    public static final class City_CreateDB implements BaseColumns {
        public static final String city_ko = "city_ko";
        public static final String city_code = "city_code";
        public static final String _TABLENAME = "hcity";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + city_ko + " text not null , "
                        + city_code + " text not null );";
    }

    // 호텔 서브시티
    public static final class SubCity_CreateDB implements BaseColumns {
        public static final String city_code = "city_code";
        public static final String subcity_ko = "subcity_ko";
        public static final String subcity_code = "subcity_code";
        public static final String _TABLENAME = "hsubcity";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + city_code + " text not null , "
                        + subcity_ko + " text not null , "
                        + subcity_code + " text not null );";
    }

    // 액티비티 시티
    public static final class qCity_CreateDB implements BaseColumns {
        public static final String qcity_ko = "qcity_ko";
        public static final String qcity_id = "qcity_id";
        public static final String _TABLENAME = "qcity";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + qcity_ko + " text not null , "
                        + qcity_id + " text not null );";
    }

    // 액티비티 카테고리
    public static final class qCategory_CreateDB implements BaseColumns {
        public static final String qcategory_ko = "qcategory_ko";
        public static final String qcategory_id = "qcategory_id";
        public static final String _TABLENAME = "qcategory";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + qcategory_ko + " text not null , "
                        + qcategory_id + " text not null );";
    }

    // 최근 본 상품 리스트
    public static final class RecentList_CreateDB implements BaseColumns {
        public static final String sel_id = "sel_id";
        public static final String sel_option = "sel_option";
        public static final String created_date = "created_date";
        public static final String _TABLENAME = "recent";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + _ID + " integer primary key autoincrement, "
                        + sel_id + " text not null , "
                        + sel_option + " text not null , "
                        + created_date + " DATETIME DEFAULT (datetime('now','localtime')));";
    }

    // 최근 본 지역 리스트
    public static final class RecentCity_CreateDB implements BaseColumns {
        public static final String sel_city_id = "sel_city_id";
        public static final String sel_city_ko = "sel_city_ko";
        public static final String sel_subcity_id = "sel_subcity_id";
        public static final String sel_subcity_ko = "sel_subcity_ko";
        public static final String sel_option = "sel_option";
        public static final String created_date = "created_date";
        public static final String _TABLENAME = "recent_city";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + sel_city_id + " text not null , "
                        + sel_city_ko + " text not null , "
                        + sel_subcity_id + " text , "
                        + sel_subcity_ko + " text , "
                        + sel_option + " text not null , "
                        + created_date + " DATETIME DEFAULT (datetime('now','localtime')));";
    }

    // 최근 검색어
    public static final class Keyword_CreateDB implements BaseColumns {
        public static final String keyword = "keyword";
        public static final String keyid = "keyid";
        public static final String created_date = "created_date";
        public static final String _TABLENAME = "search_recent";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + _ID + " integer primary key autoincrement, "
                        + keyid + " text not null , "
                        + keyword + " text not null , "
                        + created_date + " DATETIME DEFAULT (datetime('now','localtime')));";
    }

    // 관심상품
    public static final class Favorite_CreateDB implements BaseColumns {
        public static final String keyid = "keyid";
        public static final String type = "type";
        public static final String created_date = "created_date";
        public static final String _TABLENAME = "favorite_list";
        public static final String _CREATE =
                "create table " + _TABLENAME + "("
                        + _ID + " integer primary key autoincrement, "
                        + keyid + " text not null , "
                        + type + " text not null , "
                        + created_date + " DATETIME DEFAULT (datetime('now','localtime')));";
    }

}
