package com.example.sungyoung.testproject1.util;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
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
}
