package com.example.sungyoung.testproject1.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryFragment extends Fragment {
    private CalGridAdapter calGridAdapter;
    private GridView gridView;
    private TextView currentMonth;
    String pattern = "yyyy년 MM월";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);  // 출력용으로 쓸 데이트 포맷
    public DiaryFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diary, container, false);
        init(view);
        initListener();
        return view;
    }

    public void init(View view){
        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        String todayStr = simpleDateFormat.format(today);
        currentMonth.setText(todayStr);

        gridView = (GridView)view.findViewById(R.id.cal_gridview);

        initCalendar();
    }
    public void initListener() {

    }

    public void initCalendar(){
        Calendar calendar = Calendar.getInstance();
        currentMonth.setText("2020년 08월");
        String today = currentMonth.getText().toString() + " 01일";
        Date date = Util.getStringToDate(today);

        calendar.setTime(date);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK); //요일

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); //말일 구하기
        int finishDate = calendar.get(Calendar.DATE);   // 출력 형식을 지정해줍니다.

        calGridAdapter = new CalGridAdapter();

        for(int i = 0 ; i < weekIndex ; i++){
            calGridAdapter.addItem(new CalGridItem("",""));
        }

    /*
    *  db에서 메모 들고오기
    * */

        for(int i = 1 ; i <= finishDate ; i++){
            calGridAdapter.addItem(new CalGridItem( Integer.toString(i), "메모@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + i));
        }


        gridView.setAdapter(calGridAdapter);
    }

    class CalGridAdapter extends BaseAdapter {
        ArrayList<CalGridItem> items = new ArrayList<CalGridItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(CalGridItem calGridItem){
            items.add(calGridItem);
        }

        @Override
        public CalGridItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            CalGridViewer calGridViewer = new CalGridViewer(getContext());
            Calendar calendar = Calendar.getInstance();
            boolean isToday = false;

            String selDate = currentMonth.getText().toString();
            Log.d("test111 selDate", selDate);
            Date date = new Date();
            String today = simpleDateFormat.format(date);
            Log.d("test111 today", today);

            calendar.setTime(date);
            String day = Integer.toString(calendar.get(Calendar.DATE));   // 출력 형식을 지정해줍니다.

            Log.d("test111 day", day);
            if(today.equals(selDate) && day.equals(items.get(i).getDay())){
                isToday = true;
                Log.d("test111 isToday", "true");
            }

            calGridViewer.setItem(items.get(i), isToday);
            return calGridViewer;
        }
    }
}

class CalGridViewer extends LinearLayout {

    TextView textView;
    TextView textView2;
    LinearLayout dateoutter;
    public CalGridViewer(Context context) {
        super(context);

        init(context);
    }

    public CalGridViewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public void init(Context context){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.calgriditem, this,true);
        dateoutter = (LinearLayout)findViewById(R.id.dateoutter);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
    }

    public void setItem(CalGridItem singerItem, boolean isToday){

        textView.setText(singerItem.getDay());
        textView2.setText(singerItem.getMemo());

        if(isToday){
            textView.setTextColor(Color.BLACK);
            textView.setTypeface(null, Typeface.BOLD);
        }
    }
}

class CalGridItem {

    private String day;
    private String memo;

    public CalGridItem(String day, String memo) {
        this.day = day;
        this.memo = memo;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "CalGridItem{" +
                "day='" + day + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}