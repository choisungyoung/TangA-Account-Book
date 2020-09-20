package com.tanga.sungyoung.testproject1.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tanga.sungyoung.testproject1.R;
import com.tanga.sungyoung.testproject1.account.AccountDBHelper;
import com.tanga.sungyoung.testproject1.dialog.MemoDialog;
import com.tanga.sungyoung.testproject1.util.Util;
import com.tanga.sungyoung.testproject1.view.ExpandableHeightGridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarFragment extends Fragment {
    private AccountDBHelper dbHelper;
    private CalGridAdapter calGridAdapter;
    private ExpandableHeightGridView gridView;
    private TextView currentMonth;

    InputMethodManager imm ;
    String pattern = "yyyy년 MM월";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);  // 출력용으로 쓸 데이트 포맷

    String searchDate = "";

    public CalendarFragment() {
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
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        init(view);
        initCalendar();
        initListener();
        return view;
    }

    public void init(View view){
        dbHelper = new AccountDBHelper(getContext());
        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용

        String todayStr = getArguments().getString("date") == null ? simpleDateFormat.format(today) : getArguments().getString("date");
        currentMonth.setText(todayStr);
        searchDate = getArguments().getString("searchDate");
        gridView = (ExpandableHeightGridView)view.findViewById(R.id.cal_gridview);
        gridView.setExpanded(true);
    }
    public void initListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //메모 다이얼로그 오픈

                if(!calGridAdapter.getItem(i).isUse()){
                    return;
                }
                TextView curDay = (TextView)view.findViewById(R.id.day);
                String curDate = currentMonth.getText().toString() + " " + Util.addZeroToDay(curDay.getText().toString()) + "일";

                final View innerView = getLayoutInflater().inflate(R.layout.dialog_memo, null);
                MemoDialog mmoDialog = new MemoDialog(getContext(), curDate);
                mmoDialog.setCanceledOnTouchOutside(true);
                mmoDialog.setCancelable(true);
                mmoDialog.setContentView(innerView);
                mmoDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                mmoDialog.show();
                mmoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        initCalendar();
                    }
                });
            }
        });


    }

    public void initCalendar(){

        String startDay = "";
        boolean isSearch = true;
        startDay = currentMonth.getText().toString() + " 01일";

        Calendar calendar = Calendar.getInstance();
        Date date = Util.getStringToDate(startDay);

        calendar.setTime(date);
        calendar.add(calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); //말일 구
        int prevFinishDate = calendar.get(Calendar.DATE);   // 출력 형식을 지정해줍니다.

        calendar.setTime(date);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK); //요일

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); //말일 구하기
        int finishDate = calendar.get(Calendar.DATE);   // 출력 형식을 지정해줍니다.

        calGridAdapter = new CalGridAdapter();

        for(int i = 1 ; i < weekIndex ; i++){
            //보이는 이전달 채우기
            calGridAdapter.addItem(new CalGridItem(Integer.toString(prevFinishDate-(weekIndex-1) + i),"", false, false));
        }

    /*
    *  db에서 메모 들고오기
    * */
        Date d = null;
        int day = 0;
        if(searchDate != "" && currentMonth.getText().toString().equals(searchDate.substring(0, 9))){
            d = Util.getStringToDate(searchDate);
            calendar.setTime(d);
            day = calendar.get(Calendar.DATE);
        }

        Cursor cursor;
        for(int i = 1 ; i <= finishDate ; i++){
            //사용할 30일 채우기

            cursor = dbHelper.selectMemo(currentMonth.getText().toString() + " "+Util.addZeroToDay(Integer.toString(i))+"일");
            String memo = "";
            if(cursor.moveToNext()){
                memo = cursor.getString(cursor.getColumnIndex("memo"));
            }
            if(isSearch && day == i){
                calGridAdapter.addItem(new CalGridItem( Integer.toString(i), memo, true, true));
            }
            else{
                calGridAdapter.addItem(new CalGridItem( Integer.toString(i), memo, true, false));
            }
        }

        //나머지칸 채우기
        int filledDay = (weekIndex + finishDate - 1);
        filledDay = 42 -filledDay;

        /*if(filledDay > 35){
            filledDay = 42 -filledDay;
        }else{
            filledDay = 35 -filledDay;
        }*/

        for(int i = 1 ; i <= filledDay  ; i++){
            calGridAdapter.addItem(new CalGridItem( Util.addZeroToDay(Integer.toString(i)), "", false, false));
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
            Date date = new Date();
            String today = simpleDateFormat.format(date);

            calendar.setTime(date);
            String day = Integer.toString(calendar.get(Calendar.DATE));

            if(today.contains(selDate) && day.equals(items.get(i).getDay())){
                isToday = true;
            }

            if(items.get(i).getDay().startsWith("0")){
                items.get(i).setDay(items.get(i).getDay().substring(1));
            }

            calGridViewer.setItem(items.get(i), isToday, i);
            return calGridViewer;
        }
    }
}

class CalGridViewer extends LinearLayout {

    TextView day;
    TextView memo;
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
        day = (TextView)findViewById(R.id.day);
        memo = (TextView)findViewById(R.id.memo);
    }

    public void setItem(CalGridItem singerItem, boolean isToday, int index){

        day.setText(singerItem.getDay());
        memo.setText(singerItem.getMemo());

        if(isToday){
            day.setTextColor(Color.BLACK);
            day.setTypeface(null, Typeface.BOLD);
            dateoutter.setBackgroundResource(R.drawable.border_red);
        }
        if(!singerItem.isUse()){
            day.setTextColor(Color.LTGRAY);
            memo.setTextColor(Color.LTGRAY);
            dateoutter.setBackgroundColor(Color.rgb(252,252,252));
        }
        else{
            if(index % 7 == 0){
                //일요일
                day.setTextColor(Color.RED);
            }
            else if(index % 7 == 6){
                //토요일
                day.setTextColor(Color.rgb(63,81,181));
            }
        }
        if(singerItem.isUse() && singerItem.isSearch()){
            dateoutter.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary2));
        }
    }
}

class CalGridItem {

    private String day;
    private String memo;
    private boolean isUse;
    private boolean isSearch;

    public CalGridItem(String day, String memo, boolean isUse, boolean isSearch) {
        this.day = day;
        this.memo = memo;
        this.isUse = isUse;
        this.isSearch = isSearch;
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

    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public boolean isSearch() {
        return isSearch;
    }

    public void setSearch(boolean search) {
        isSearch = search;
    }

    @Override
    public String toString() {
        return "CalGridItem{" +
                "day='" + day + '\'' +
                ", memo='" + memo + '\'' +
                ", isUse=" + isUse +
                ", isSearch=" + isSearch +
                '}';
    }
}