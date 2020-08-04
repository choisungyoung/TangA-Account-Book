package com.example.sungyoung.testproject1.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sungyoung.testproject1.activity.AccountBookActivity;
import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.account.Account;
import com.example.sungyoung.testproject1.account.AccountDBHelper;
import com.example.sungyoung.testproject1.adapter.ImexListviewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ExpenseFragment extends Fragment {
    AccountBookActivity accountBookActivity = null;
    TextView startDateText = null;
    TextView endDateText = null;
    TabLayout tabLaout = null;
    ListView listView = null;
    private AccountDBHelper dbHelper;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    private OnFragmentInteractionListener mListener;

    public ExpenseFragment() {
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
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String result = dateFormat.format(today);

        dbHelper = new AccountDBHelper(getContext());
        startDateText = (TextView) view.findViewById(R.id.startDateText);
        endDateText = (TextView) view.findViewById(R.id.endDateText);
        tabLaout = view.findViewById(R.id.tabLaout);
        listView = view.findViewById(R.id.listView);
        startDateText.setText(result);
        endDateText.setText(result);

        initListener();
        selectAccount(tabLaout.getSelectedTabPosition());
        return view;
    }
    public void initListener(){
        //날짜 리스너
        startDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String monthStr = (month > 8 ? "" : "0") + (month + 1);
                        String dayStr = (day <= 9 ? "0" : "" ) + day;
                        String date = year + "년 " + monthStr + "월 " + dayStr + "일";
                        if(endDateText.getText().toString().compareTo(date) < 0){
                            date  =endDateText.getText().toString();
                        }
                        startDateText.setText(date);
                        selectAccount(tabLaout.getSelectedTabPosition());
                    }
                };
                Calendar calendar = new GregorianCalendar(Locale.KOREA);
                int nYear = calendar.get(Calendar.YEAR);
                int nMonth = calendar.get(Calendar.MONTH);
                int nDay = calendar.get(Calendar.DATE);
                DatePickerDialog dialog = new DatePickerDialog(v.getContext(), startDateListener, nYear, nMonth, nDay);
                dialog.show();
            }
        });
        endDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String monthStr = (month > 8 ? "" : "0") + (month + 1);
                        String dayStr = (day <= 9 ? "0" : "" ) + day;
                        String date = year + "년 " + monthStr + "월 " + dayStr + "일";
                        if(startDateText.getText().toString().compareTo(date) > 0){
                            date  =startDateText.getText().toString();
                        }
                        endDateText.setText(date);
                        selectAccount(tabLaout.getSelectedTabPosition());
                    }
                };

                Calendar calendar = new GregorianCalendar(Locale.KOREA);
                int nYear = calendar.get(Calendar.YEAR);
                int nMonth = calendar.get(Calendar.MONTH);
                int nDay = calendar.get(Calendar.DATE);
                DatePickerDialog dialog = new DatePickerDialog(v.getContext(), endDateListener, nYear, nMonth, nDay);

                dialog.show();
            }
        });
        tabLaout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectAccount(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void selectAccount(int position){
        String imex1 = position == 0 ? "지출" : "수입";

        Cursor cursor = dbHelper.selectAccountByDateByDate(startDateText.getText().toString(), endDateText.getText().toString(), imex1);
        ArrayList<Account> aList = new ArrayList<>();

        String curDate = "";
        System.out.println("==================================================");
        while (cursor.moveToNext()) {
            String imex = cursor.getString(cursor.getColumnIndex("imex"));
            String price = cursor.getString(cursor.getColumnIndex("price"));
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String accountName = cursor.getString(cursor.getColumnIndex("accountName"));
            Account account = new Account(date, accountName, price, imex);

            if(!date.equals(curDate)){
                Account title = new Account(date, "", "", "");
                curDate = date;
                aList.add(title);
            }
            aList.add(account);
            System.out.println(accountName);
        }

        ImexListviewAdapter iladapter = new ImexListviewAdapter(getActivity(), R.layout.listitem, aList);
        listView.setAdapter(iladapter);
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        accountBookActivity = (AccountBookActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
