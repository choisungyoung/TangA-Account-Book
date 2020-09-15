package com.example.sungyoung.testproject1.util;

import android.widget.Toast;

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
}
