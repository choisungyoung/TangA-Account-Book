package com.example.sungyoung.testproject1.account;


import android.provider.BaseColumns;

final public class AccountContract {
    private AccountContract() {
    }

    // 하나의 테이블에 필요한 내용을 하나의 클래스에 정의한다.
    public static class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ACCOUNTNAME = "accountName";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_IMEX = "imex";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        _ID+ " INTEGER PRIMARY KEY," +
                        COLUMN_DATE+ " TEXT," +
                        COLUMN_ACCOUNTNAME + " TEXT," +
                        COLUMN_PRICE + " TEXT," +
                        COLUMN_IMEX + " TEXT)";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    // 하나의 테이블에 필요한 내용을 하나의 클래스에 정의한다.
    public static class DiaryEntry implements BaseColumns {
        public static final String TABLE_NAME = "diary";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MEMO = "memo";
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                        COLUMN_DATE+ " TEXT," +
                        COLUMN_MEMO + " TEXT)";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}