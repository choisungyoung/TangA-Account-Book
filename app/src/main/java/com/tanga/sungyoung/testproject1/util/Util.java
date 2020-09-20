package com.tanga.sungyoung.testproject1.util;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    public static Date getStringToDate(String dateStr){
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        return date;
    }
    public static String addZeroToDay(String day){
        if(day.length() == 1){
            day = "0" + day;
        }
        return day;
    }

    public static String makeSearchMsg(int total, int idx){
        String result = "";
        result = total + "건 중 " + idx + "번째입니다.";
        return result;
    }
    public static JSONArray cur2Json(Cursor cursor) {

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("cur2Json", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }
}
