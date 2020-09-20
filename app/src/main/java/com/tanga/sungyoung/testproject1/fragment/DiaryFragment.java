package com.tanga.sungyoung.testproject1.fragment;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tanga.sungyoung.testproject1.R;
import com.tanga.sungyoung.testproject1.account.AccountDBHelper;
import com.tanga.sungyoung.testproject1.dialog.MyYearMonthPickerDialog;
import com.tanga.sungyoung.testproject1.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class DiaryFragment extends Fragment {
    private AccountDBHelper dbHelper;

    private EditText searchEditText;
    private TextView resultTextView;
    private Button searchButton;
    InputMethodManager imm ;
    String pattern = "yyyy년 MM월";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);  // 출력용으로 쓸 데이트 포맷

    private TextView calendarTextView;
    private String searchQuery = "";
    private String searchDate = "";
    private ArrayList<String> resultList;
    private int index;


    private ViewPager mPager;
    private CalendarSlidePagerAdapter pagerAdapter;

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            Log.d("YearMonthPickerTest", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth);
            String date = year + "년 " + Util.addZeroToDay(Integer.toString(monthOfYear) + "월");
            pagerAdapter.setCurdate(date);

            initSearchBox();
            initCalendar();
        }
    };

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
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        String todayStr = simpleDateFormat.format(today);

        searchEditText = (EditText) view.findViewById(R.id.searchEditText);
        resultTextView = (TextView) view.findViewById(R.id.resultTextView);
        searchButton = (Button) view.findViewById(R.id.searchButton);
        resultList = new ArrayList<>();

        calendarTextView = (TextView) view.findViewById(R.id.calendarTextView);
        //init view pager
        mPager = (ViewPager) view.findViewById(R.id.calendarView);
        pagerAdapter = new CalendarSlidePagerAdapter(getActivity().getSupportFragmentManager());
        pagerAdapter.setCurdate(todayStr);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);   //중간위치에서 시작하기
    }
    public void initListener() {

        calendarTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getActivity().getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                int index = mPager.getCurrentItem();
                Log.d("addOnPageChangeListener", "onPageScrollStateChanged" + i + " " + index);
                if(i != 0 || index == 1){
                    // index == 1이 무조건 현재 보이는 페이지기 때문에 슬라이드가 되지 않았음을 의미함
                    // i == 0이 슬라이드가 완전이 다 되었음을 의미함
                    return;
                }

                String curdate = pagerAdapter.getCurdate();
                String curMonth = curdate;
                Calendar calendar = Calendar.getInstance();
                Date date = Util.getStringToDate(curMonth + " 01일");
                calendar.setTime(date);

                if(index == 0){
                    calendar.add(calendar.MONTH, -1);
                }
                else if(index == 1){
                    calendar.add(calendar.MONTH, 0);
                }
                else{
                    calendar.add(calendar.MONTH, +1);
                }
                calendar.getTime();
                String prevMonth = simpleDateFormat.format(calendar.getTime());

                pagerAdapter.setCurdate(prevMonth);
                mPager.setAdapter(pagerAdapter);
                mPager.setCurrentItem(1);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //키패드내리기
                imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);


                String query = searchEditText.getText().toString();
                if("".equals(query)){
                    Toast.makeText(view.getContext(), "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
                    initSearchBox();
                    initCalendar();
                    return;
                }

                Cursor cursor = dbHelper.selectDiaryLikeMemo(query); //현재검색중인 결과와 다르면

                if(searchQuery.equals(query) && resultList.size() == cursor.getCount()){
                    // 검색안함
                    if(resultList.size() == 0){
                        return;
                    }

                    resultTextView.setText(Util.makeSearchMsg(resultList.size(), index + 1));
                    searchDate = resultList.get(index);
                    initCalendar();
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
                        initSearchBox();
                        initCalendar();
                        resultTextView.setText("검색결과가 없습니다.");
                        return;
                    }
                    resultTextView.setText(Util.makeSearchMsg(resultList.size(), index + 1));
                    searchDate = resultList.get(index);
                    initCalendar();
                    index = (index+1) % resultList.size();

                }
            }
        });
    }

    public void initSearchBox(){
        resultList.clear();
        searchDate = "";
        resultTextView.setText("");
        searchEditText.setText("");

    }
    public void initCalendar(){
        if(searchDate != ""){
            pagerAdapter.setCurdate( searchDate.substring(0, 9));
        }
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);
    }

    public CalendarFragment setCalendarBunddle(CalendarFragment f, String date) {
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("date", date);
        args.putString("searchDate", searchDate);
        f.setArguments(args);

        return f;
    }

    private class CalendarSlidePagerAdapter extends FragmentStatePagerAdapter {
        String curdate = "2020년 01월";
        public CalendarSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        public void setCurdate(String date){
            this.curdate = date;
        }
        public String getCurdate(){
            return curdate;
        }

        @Override
        public Fragment getItem(int position) {

            String curMonth = this.curdate;
            Calendar calendar = Calendar.getInstance();
            Date date = Util.getStringToDate(curMonth + " 01일");
            calendar.setTime(date);
            if(position == 0){
                calendar.add(calendar.MONTH, -1);
            }
            else if(position == 1){
                calendar.add(calendar.MONTH, 0);
            }
            else{
                calendar.add(calendar.MONTH, +1);
            }
            calendar.getTime();
            String prevMonth = simpleDateFormat.format(calendar.getTime());

            return setCalendarBunddle(new CalendarFragment() , prevMonth);
        }

        @Override
        public int getCount() {
            return 3;
        }


    }
}
