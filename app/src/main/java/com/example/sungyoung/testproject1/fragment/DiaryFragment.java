package com.example.sungyoung.testproject1.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.account.AccountDBHelper;
import com.example.sungyoung.testproject1.dialog.MemoDialog;
import com.example.sungyoung.testproject1.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DiaryFragment extends Fragment {
    private AccountDBHelper dbHelper;

    private CalGridAdapter calGridAdapter;
    private GridView gridView;
    private TextView currentMonth;
    private ImageView prevYearBtn;
    private ImageView prevMonthBtn;
    private ImageView nextYearBtn;
    private ImageView nextMonthBtn;


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
        initCalendar();
        initListener();
        return view;
    }

    public void init(View view){
        dbHelper = new AccountDBHelper(getContext());
        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        String todayStr = simpleDateFormat.format(today);
        currentMonth.setText(todayStr);

        gridView = (GridView)view.findViewById(R.id.cal_gridview);

        prevYearBtn = (ImageView) view.findViewById(R.id.prevYear);
        prevMonthBtn = (ImageView) view.findViewById(R.id.prevMonth);
        nextYearBtn = (ImageView) view.findViewById(R.id.nextYear);
        nextMonthBtn = (ImageView) view.findViewById(R.id.nextMonth);

    }
    public void initListener() {
        prevYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curYear = currentMonth.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = Util.getStringToDate(curYear + " 01일");
                calendar.setTime(date);
                calendar.add(calendar.YEAR, -1);
                calendar.getTime();
                String prevYear = simpleDateFormat.format(calendar.getTime());
                currentMonth.setText(prevYear);

                initCalendar();
            }
        });
        prevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curMonth = currentMonth.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = Util.getStringToDate(curMonth + " 01일");
                calendar.setTime(date);
                calendar.add(calendar.MONTH, -1);
                calendar.getTime();
                String prevMonth = simpleDateFormat.format(calendar.getTime());
                currentMonth.setText(prevMonth);

                initCalendar();
            }
        });

        nextYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curYear = currentMonth.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = Util.getStringToDate(curYear + " 01일");
                calendar.setTime(date);
                calendar.add(calendar.YEAR, +1);
                calendar.getTime();
                String nextYear = simpleDateFormat.format(calendar.getTime());
                currentMonth.setText(nextYear);

                initCalendar();
            }
        });
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String curMonth = currentMonth.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = Util.getStringToDate(curMonth + " 01일");
                calendar.setTime(date);
                calendar.add(calendar.MONTH, +1);
                calendar.getTime();
                String prevMonth = simpleDateFormat.format(calendar.getTime());
                currentMonth.setText(prevMonth);

                initCalendar();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //메모 다이얼로그 오픈
                TextView curDay = (TextView)view.findViewById(R.id.day);
                String curDate = currentMonth.getText().toString() + " " + curDay.getText().toString() + "일";

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

        Calendar calendar = Calendar.getInstance();
        String startDay = currentMonth.getText().toString() + " 01일";
        Date date = Util.getStringToDate(startDay);

        calendar.setTime(date);
        int weekIndex = calendar.get(Calendar.DAY_OF_WEEK); //요일

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); //말일 구하기
        int finishDate = calendar.get(Calendar.DATE);   // 출력 형식을 지정해줍니다.

        calGridAdapter = new CalGridAdapter();

        for(int i = 1 ; i < weekIndex ; i++){
            calGridAdapter.addItem(new CalGridItem("",""));
        }

    /*
    *  db에서 메모 들고오기
    * */

        Cursor cursor;
        for(int i = 1 ; i <= finishDate ; i++){
            cursor = dbHelper.selectMemo(currentMonth.getText().toString() + " "+i+"일");
            String memo = "";
            if(cursor.moveToNext()){
                memo = cursor.getString(cursor.getColumnIndex("memo"));
            }
            calGridAdapter.addItem(new CalGridItem( Integer.toString(i), memo));
        }

        //나머지칸 채우기
        int filledDay = (weekIndex + finishDate - 1);
        if(filledDay > 35){
            filledDay = 42 -filledDay;
        }else{
            filledDay = 35 -filledDay;
        }
        for(int i = 1 ; i <= filledDay  ; i++){
            calGridAdapter.addItem(new CalGridItem( "0"+Integer.toString(i), ""));
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
            String day = Integer.toString(calendar.get(Calendar.DATE));   // 출력 형식을 지정해줍니다.

            if(today.contains(selDate) && day.equals(items.get(i).getDay())){
                isToday = true;
            }

            if(items.get(i).getDay().startsWith("0")){
                items.get(i).setDay(items.get(i).getDay().substring(1));
            }

            calGridViewer.setItem(items.get(i), isToday);
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

    public void setItem(CalGridItem singerItem, boolean isToday){

        day.setText(singerItem.getDay());
        memo.setText(singerItem.getMemo());

        if(isToday){
            day.setTextColor(Color.BLACK);
            day.setTypeface(null, Typeface.BOLD);
            dateoutter.setBackgroundResource(R.drawable.border_red);
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