package com.example.sungyoung.testproject1.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.account.AccountDBHelper;
import com.example.sungyoung.testproject1.dialog.MemoDialog;
import com.example.sungyoung.testproject1.util.Util;
import com.example.sungyoung.testproject1.view.ExpandableHeightGridView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class DiaryFragment extends Fragment {
    private AccountDBHelper dbHelper;
    private CalGridAdapter calGridAdapter;
    private ExpandableHeightGridView gridView;
    private TextView currentMonth;
    private ImageView prevYearBtn;
    private ImageView prevMonthBtn;
    private ImageView nextYearBtn;
    private ImageView nextMonthBtn;

    private EditText searchEditText;
    private TextView resultTextView;
    private Button searchButton;
    InputMethodManager imm ;
    String pattern = "yyyy년 MM월";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);  // 출력용으로 쓸 데이트 포맷

    private String searchQuery = "";
    private ArrayList<String> resultList;
    private int index;


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
        initCalendar(null);
        initListener();
        return view;
    }

    public void init(View view){
        dbHelper = new AccountDBHelper(getContext());
        currentMonth = (TextView) view.findViewById(R.id.currentMonth);
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        String todayStr = simpleDateFormat.format(today);
        currentMonth.setText(todayStr);

        gridView = (ExpandableHeightGridView)view.findViewById(R.id.cal_gridview);
        gridView.setExpanded(true);
        prevYearBtn = (ImageView) view.findViewById(R.id.prevYear);
        prevMonthBtn = (ImageView) view.findViewById(R.id.prevMonth);
        nextYearBtn = (ImageView) view.findViewById(R.id.nextYear);
        nextMonthBtn = (ImageView) view.findViewById(R.id.nextMonth);

        searchEditText = (EditText) view.findViewById(R.id.searchEditText);
        resultTextView = (TextView) view.findViewById(R.id.resultTextView);
        searchButton = (Button) view.findViewById(R.id.searchButton);

        resultList = new ArrayList<>();
    }
    public void initListener() {

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //키패드내리기
                imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);


                String query = searchEditText.getText().toString();
                if("".equals(query)){
                    initCalendar(null);
                    Toast.makeText(view.getContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Cursor cursor = dbHelper.selectDiaryLikeMemo(query); //현재검색중인 결과와 다르면

                if(searchQuery.equals(query) && resultList.size() == cursor.getCount()){
                    // 검색안함
                    if(resultList.size() == 0){
                        return;
                    }

                    resultTextView.setText(Util.makeSearchMsg(resultList.size(), index + 1));
                    initCalendar(resultList.get(index));
                    index = (index+1) % resultList.size();
                }
                else{
                    resultList = new ArrayList<>();
                    searchQuery = query;                    // 현재검색하고 있는 검색어 저장

                    while(cursor.moveToNext()){
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        resultList.add(date);
                    }

                    final Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
                    today.setHours(0);
                    today.setMinutes(0);
                    today.setSeconds(0);

                    Collections.sort(resultList, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            Date date1 = Util.getStringToDate(s1);
                            Date date2 = Util.getStringToDate(s2);
                            return Long.compare(Math.abs(today.getTime() - date1.getTime()), Math.abs(today.getTime() - date2.getTime()));
                        }
                    });
                    for(String s : resultList){
                        Log.d("selectDiaryLikeMemo", s);
                    }
                    index = 0;
                    if(resultList.size() == 0){
                        initCalendar(null);
                        resultTextView.setText("검색결과가 없습니다.");
                        return;
                    }
                    resultTextView.setText(Util.makeSearchMsg(resultList.size(), index + 1));
                    initCalendar(resultList.get(index));
                    index = (index+1) % resultList.size();

                }

            }
        });

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

                initCalendar(null);
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

                initCalendar(null);
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

                initCalendar(null);
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

                initCalendar(null);
            }
        });

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
                        if(resultList.size() > 0){
                            //검색 중 메모 갔다와도 검색 결과 나오게
                            initCalendar(resultList.get(index-1 >= 0 ? index-1 : resultList.size() - 1 ));
                        }
                        else{
                            initCalendar(null);
                        }
                    }
                });
            }
        });


    }

    public void initCalendar(String paramDate){

        String startDay = "";
        boolean isSearch = false;
        if(paramDate == null){
            startDay = currentMonth.getText().toString() + " 01일";
        }
        else{
            //검색 중인 경우
            isSearch = true;
            startDay = paramDate;
            Date d = Util.getStringToDate(startDay);
            String monthStr = simpleDateFormat.format(d);
            currentMonth.setText(monthStr);

            startDay = monthStr + " 01일";;
        }

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
        if(isSearch){
            d = Util.getStringToDate(paramDate);
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
        if(filledDay > 35){
            filledDay = 42 -filledDay;
        }else{
            filledDay = 35 -filledDay;
        }

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

            Log.d("getViewIndex", Integer.toString(i));
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