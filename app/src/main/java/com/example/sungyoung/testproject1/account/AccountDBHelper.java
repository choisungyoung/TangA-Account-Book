package com.example.sungyoung.testproject1.account;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.sungyoung.testproject1.util.Util;

import java.util.Date;

public class AccountDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database";
    public static final int DATABASE_VERSION = 1;

    public AccountDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(AccountContract.AccountEntry.SQL_CREATE_TABLE); // 테이블 생성
        db.execSQL(AccountContract.DiaryEntry.SQL_CREATE_TABLE); // 테이블 생성
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(AccountContract.AccountEntry.SQL_CREATE_TABLE); // 테이블 생성
        sqLiteDatabase.execSQL(AccountContract.DiaryEntry.SQL_CREATE_TABLE); // 테이블 생성
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // 단순히 데이터를 삭제하고 다시 시작하는 정책이 적용될 경우
        //sqLiteDatabase.execSQL(AccountContract.AccountEntry.SQL_DELETE_TABLE);
        //onCreate(sqLiteDatabase);
    }
    public void insertAccount(Account account) {
        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(AccountContract.AccountEntry.COLUMN_ACCOUNTNAME, account.accountName);
        values.put(AccountContract.AccountEntry.COLUMN_DATE, account.date);
        values.put(AccountContract.AccountEntry.COLUMN_PRICE, account.price);
        values.put(AccountContract.AccountEntry.COLUMN_IMEX, account.imex);

        db.insert(AccountContract.AccountEntry.TABLE_NAME, null, values);
    }
    public Cursor selectAllData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, date, accountname, price, imex FROM account ;" ,  null);
        return cursor;
    }
    public Cursor selectAccountByDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, date, accountname, price, imex FROM account where date=? order by ? desc;" ,  new String[]{date, BaseColumns._ID});
        return cursor;
    }

    public Cursor selectAccountByDate(String startDate, String endDate, String imex, String accountName) {
        SQLiteDatabase db = getReadableDatabase();
        String addQeury = "";
        if(accountName != null && !"".equals(accountName)){
            addQeury = "AND accountname like '%"+accountName+"%'";
            Log.d("test", addQeury);
        }
        Cursor cursor = db.rawQuery("SELECT _id, date, accountname, price, imex FROM account where date >= ? and date <= ? and imex = ? "+addQeury+" order by date desc, _id;" ,  new String[]{startDate, endDate, imex});
        return cursor;
    }
    public Cursor selectAccountByName(String accountName) {
        SQLiteDatabase db = getReadableDatabase();
        String addQeury = "";
        if(accountName != null && !"".equals(accountName)){
            addQeury = "AND accountname like '%"+accountName+"%'";
            Log.d("test", addQeury);
        }
        Cursor cursor = db.rawQuery("SELECT _id, date, accountname, price, imex FROM account where 1 = 1 "+addQeury+" order by date desc, _id;" ,  new String[]{});
        return cursor;
    }
    public Cursor selectAccountByDateNname(String startDate, String endDate, String imex, String accountName) {
        SQLiteDatabase db = getReadableDatabase();
        String addQeury = "";
        if(accountName != null && !"".equals(accountName)){
            addQeury = "AND accountname like '%"+accountName+"%'";
            Log.d("test", addQeury);
        }
        Cursor cursor = db.rawQuery("SELECT _id, date, accountname, price, imex FROM account where date >= ? and date <= ? and imex = ? "+addQeury+" order by date desc, _id;" ,  new String[]{startDate, endDate, imex});
        return cursor;
    }
    public Cursor selectAccountYest(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT sum(price) sum, imex FROM (select imex, price from account where date < ?) group by imex;" ,  new String[]{date});
        return cursor;
    }

    public Cursor selectAuto() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT distinct accountname FROM account  order by accountname;" ,  null);
        return cursor;
    }

    public void deleteAccount(String id){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("delete FROM account where _id=? ;" ,new String[]{id});
    }
    public Cursor selectMemo(String date) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT memo FROM diary WHERE date = ?;" ,  new String[]{date});
        return cursor;
    }
    public Cursor selectDiaryLikeMemo(String memo) {
        SQLiteDatabase db = getReadableDatabase();
        memo = "%"+memo+"%";
        Cursor cursor = db.rawQuery("SELECT date FROM diary WHERE memo like ? ;" ,  new String[]{memo});
        return cursor;
    }
    public Cursor updateMemo(String date, String memo){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT memo FROM diary WHERE date = ? ;" ,  new String[]{date});
        if(cursor.getCount() != 0){

            ContentValues values = new ContentValues();
            values.put(AccountContract.DiaryEntry.COLUMN_MEMO, memo);
            db.update(AccountContract.DiaryEntry.TABLE_NAME, values, "date=?",new String[]{date});
        }
        else{
            ContentValues values = new ContentValues();
            values.put(AccountContract.DiaryEntry.COLUMN_DATE, date);
            values.put(AccountContract.DiaryEntry.COLUMN_MEMO, memo);
            db.insert(AccountContract.DiaryEntry.TABLE_NAME, null, values);
        }

        return cursor;
    }

    public void printAccountData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT _id, date, accountname, price, imex FROM account ;" ,  null);

        while(cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String accountName = cursor.getString(cursor.getColumnIndex("accountName"));
            String price = cursor.getString(cursor.getColumnIndex("price"));
            String imex = cursor.getString(cursor.getColumnIndex("imex"));

            Log.d("printAccountData", id +", \t" + date +", \t" + accountName +", \t" + price +", \t" + imex );
        }
    }
    public void printDiaryData(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT date, memo FROM diary ;" ,  null);

        while(cursor.moveToNext()){
            String memo = cursor.getString(cursor.getColumnIndex("memo"));
            String date = cursor.getString(cursor.getColumnIndex("date"));

            Log.d("printAccountData",  date +", \t" + memo );
        }
    }
    public void deleteAllAccount(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("delete FROM account ;" );
    }
    public void deleteAllDiary(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("delete FROM diary ;" );
    }

    public void dropAccount(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(AccountContract.AccountEntry.SQL_DELETE_TABLE);
    }
    public void dropDiary(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(AccountContract.DiaryEntry.SQL_DELETE_TABLE);
    }
}