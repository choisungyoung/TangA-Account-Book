package com.example.sungyoung.testproject1.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.account.Account;
import com.example.sungyoung.testproject1.account.AccountDBHelper;
import com.example.sungyoung.testproject1.activity.AccountBookActivity;
import com.example.sungyoung.testproject1.adapter.ImexListviewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {
    AccountBookActivity accountBookActivity = null;
    TabLayout tabLaout = null;
    ListView listView = null;
    Button searchButton = null;

    //자동완성
    private AutoCompleteTextView autoCompleteTextView = null;
    public List<String> autoList;

    private AccountDBHelper dbHelper;
    private DatePickerDialog.OnDateSetListener startDateListener;
    private DatePickerDialog.OnDateSetListener endDateListener;
    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String endDate = dateFormat.format(today);
        Date date = new Date(today.getTime()+(1000*60*60*24*-7));
        String startDate = dateFormat.format(date);

        dbHelper = new AccountDBHelper(getContext());
        tabLaout = view.findViewById(R.id.tabLaout);
        listView = view.findViewById(R.id.listView);

        //자동완성
        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);    //검색버튼 초기화

        initAutoComplete();
        initListener();
        initFilter();
        selectAccount(tabLaout.getSelectedTabPosition());
        return view;
    }

    public void initListener(){
        //날짜 리스너
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

        //자동완성 클릭이벤트
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    Cursor autoCursor = dbHelper.selectAuto();
                    autoList = new ArrayList<>();

                    while (autoCursor.moveToNext()) {
                        String name = autoCursor.getString(autoCursor.getColumnIndex("accountName"));
                        autoList.add(name);
                    }
                    // AutoCompleteTextView 에 아답터를 연결한다.
                    autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_dropdown_item_1line,  autoList ));

                    Toast.makeText(getActivity(), autoList.toArray().toString(), Toast.LENGTH_SHORT);
                    autoCompleteTextView.showDropDown();
                }
            }
        });
        //검색버튼 리스너 추가
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAccount(tabLaout.getSelectedTabPosition());
            }
        });
    }

    public void selectAccount(int position){
        String imex1 = position == 0 ? "지출" : "수입";

        Cursor cursor = dbHelper.selectAccountByName(imex1, autoCompleteTextView.getText().toString());
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

    public void initAutoComplete() {
        Cursor autoCursor = dbHelper.selectAuto();
        autoList = new ArrayList<>();

        while (autoCursor.moveToNext()) {
            String name = autoCursor.getString(autoCursor.getColumnIndex("accountName"));
            autoList.add(name);
        }
        // AutoCompleteTextView 에 아답터를 연결한다.
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line,  autoList ));
    }

    public void initFilter(){
        autoCompleteTextView.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        },new InputFilter.LengthFilter(30)});
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
