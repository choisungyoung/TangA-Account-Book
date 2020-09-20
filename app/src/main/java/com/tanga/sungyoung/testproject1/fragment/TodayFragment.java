package com.tanga.sungyoung.testproject1.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tanga.sungyoung.testproject1.activity.AccountBookActivity;
import com.tanga.sungyoung.testproject1.R;
import com.tanga.sungyoung.testproject1.account.Account;
import com.tanga.sungyoung.testproject1.account.AccountDBHelper;
import com.tanga.sungyoung.testproject1.adapter.ListviewAdapter;
import com.tanga.sungyoung.testproject1.util.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private AccountDBHelper dbHelper;

    public Button confButton;
    public Button yestButton;
    public Button todayButton;
    public Button tomorrowButton;

    public TextView dateText;
    public ListView listView;
    public Spinner spinner_field;
    public TextView imTestView;
    public TextView exTestView;
    public TextView yestTestView;
    public TextView balTestView;
    public EditText priceEditView;
    public List<String> autoList;
    TabLayout tabLaout = null;
    public View acView;

    DecimalFormat df = new DecimalFormat("###,###");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    String result = "";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private AccountBookActivity accountBookActivity = null;

    //자동완성
    private AutoCompleteTextView autoCompleteTextView = null;
    public TodayFragment() {
    }

    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today, container, false);
        acView = view;
        dbHelper = new AccountDBHelper(getContext());

        Date today = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
        String result = dateFormat.format(today);

        dateText = (TextView) view.findViewById(R.id.dateText);
        dateText.setText(result);

        yestTestView = (TextView) view.findViewById(R.id.yestTextView);
        imTestView = (TextView) view.findViewById(R.id.importTextView);
        exTestView = (TextView) view.findViewById(R.id.expenseTextView);
        balTestView = (TextView)view.findViewById(R.id.balanceTextView);
        spinner_field = view.findViewById(R.id.imexSpinner);
        tabLaout = view.findViewById(R.id.tabLaout);
        listView = (ListView) view.findViewById(R.id.listView);
        priceEditView = view.findViewById(R.id.priceEditView);

        //어제내일 init

        //지출/수입 스피너
        String[] spinnerArr = getResources().getStringArray(R.array.imex_array);

        final SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerArr);
        spinner_field.setAdapter(adapter);

        confButton = view.findViewById(R.id.confButton);
        yestButton = view.findViewById(R.id.yestButton);
        todayButton = view.findViewById(R.id.todayButton);
        tomorrowButton = view.findViewById(R.id.tomorrowButton);

        //자동완성
        autoCompleteTextView = (AutoCompleteTextView) acView.findViewById(R.id.accountEditText);

        initListener();
        initFilter();
        showList();
        return view;
    }


    public void initListener(){
        //날짜 리스너
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();   //DatePickerFragment 객체 생성
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                TextView hidden = view.findViewById(R.id.hidden);
                onClick_setting_costume_save(hidden.getText().toString());
                return true;
            }

        });
        tabLaout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showList();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //입력버튼 리스너 추가
        confButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertItem(view);
            }
        });
        //어제버튼
        yestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = Util.getStringToDate(dateText.getText().toString());
                if(date==null)
                    Toast.makeText(getActivity(), autoList.toArray().toString(), Toast.LENGTH_SHORT);
                else{
                    date = new Date(date.getTime()+(1000*60*60*24*-1));
                    dateText.setText(dateFormat.format(date));
                    showList();
                }
            }
        });
        //오늘버튼
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = new Date();      // birthday 버튼의 초기화를 위해 date 객체와 SimpleDataFormat 사용
                if(date==null)
                    Toast.makeText(getActivity(), autoList.toArray().toString(), Toast.LENGTH_SHORT);
                else{
                    dateText.setText(dateFormat.format(date));
                    showList();
                }
            }
        });
        //내일버튼
        tomorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = Util.getStringToDate(dateText.getText().toString());
                if(date==null)
                    Toast.makeText(getActivity(), autoList.toArray().toString(), Toast.LENGTH_SHORT);
                else{
                    date = new Date(date.getTime()+(1000*60*60*24*+1));
                    dateText.setText(dateFormat.format(date));
                    showList();
                }
            }
        });
        //금액 입력 edit변화 이벤트
        priceEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    result="";
                }
                if(!s.toString().equals(result)){     // StackOverflow를 막기위해,
                    result = df.format(Long.parseLong(s.toString().replaceAll(",", "")));   // 에딧텍스트의 값을 변환하여, result에 저장.
                    priceEditView.setText(result);    // 결과 텍스트 셋팅.
                    priceEditView.setSelection(result.length());     // 커서를 제일 끝으로 보냄.
                }
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
    }

    public void showList(){

        Log.d("test@",dateText.getText().toString());
        Cursor cursor = dbHelper.selectAccountByDate(dateText.getText().toString());
        ArrayList<Account> list = new ArrayList<>();
        long im = 0;
        long ex = 0;
        while (cursor.moveToNext()) {
            String imex = cursor.getString(cursor.getColumnIndex("imex"));
            String price = cursor.getString(cursor.getColumnIndex("price"));
            if(imex.equals("지출")){
                ex += Long.parseLong(price);
            }else if(imex.equals("수입")){
                im += Long.parseLong(price);
            }
            if(!imex.equals(tabLaout.getTabAt(tabLaout.getSelectedTabPosition()).getText().toString().replaceAll(" ", ""))){
                continue;
            }
            System.out.println("tabLaoutTest : " + tabLaout.getTabAt(tabLaout.getSelectedTabPosition()).getText().toString());
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String accountName = cursor.getString(cursor.getColumnIndex("accountName"));
            Account account = new Account(date, accountName, price, imex);
            account.setId(id);
            list.add(account);
            Log.d("test", date + ", " + accountName + ", " + price + ", " + imex);
        }
        ListviewAdapter listadapter = new ListviewAdapter(getActivity(), R.layout.listitem, list);
        listView.setAdapter(listadapter);
        Cursor yestCursor = dbHelper.selectAccountYest(dateText.getText().toString());

        String[] imexArr = new String[2];
        int[] sum = new int[2];
        int i = 0;
        while (yestCursor.moveToNext()) {
            imexArr[i] = yestCursor.getString(yestCursor.getColumnIndex("imex"));
            sum[i] = yestCursor.getInt(yestCursor.getColumnIndex("sum"));
            Log.d("test", "imex : " + imexArr[i]);
            i++;
        }

        int yest = 0;
        if(i != 0){
            if(i == 1){
                if(imexArr[0].equals("수입")){
                    yest = sum[0];
                }else if(imexArr[0].equals("지출")){
                    yest = -sum[0];
                }
            }
            else {
                if(imexArr[0].equals("수입")){
                    yest = sum[0] - sum[1];
                }else if(imexArr[0].equals("지출")){
                    yest = sum[1] - sum[0];
                }
                else{
                    Toast.makeText(getActivity(), "Error!!", Toast.LENGTH_SHORT);
                }
            }
        }
        Cursor autoCursor = dbHelper.selectAuto();
        autoList = new ArrayList<>();

        while (autoCursor.moveToNext()) {
            String name = autoCursor.getString(autoCursor.getColumnIndex("accountName"));
            autoList.add(name);
        }
        // AutoCompleteTextView 에 아답터를 연결한다.
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line,  autoList ));

        DecimalFormat format = new DecimalFormat("###,###");
        yestTestView.setText(format.format(yest) + " 원  ");
        imTestView.setText(format.format(im) + " 원  ");
        exTestView.setText(format.format(ex) + " 원  ");
        balTestView.setText(format.format(yest+im-ex) + " 원  ");

    }
    public void deleteItem(String id){
        dbHelper.deleteAccount(id);
        showList();
    }

    public void insertItem(View v) {
        EditText accountName = acView.findViewById(R.id.accountEditText);
        EditText price =acView.findViewById(R.id.priceEditView);
        String priceStr = price.getText().toString().replace(",", "");
        if(accountName.getText().toString().equals("") || priceStr.equals("")){
            Toast.makeText(getActivity().getApplicationContext(), "값을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        Account account = new Account(dateText.getText().toString(), accountName.getText().toString(), priceStr, spinner_field.getSelectedItem().toString());

        Toast.makeText(getActivity().getApplicationContext(), "추가되었습니다.", Toast.LENGTH_SHORT).show();

        dbHelper.insertAccount(account);
        accountName.setText("");
        price.setText("");

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(price.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(accountName.getWindowToken(), 0);
        showList();

    }
    public void onClick_setting_costume_save(final String id){
        new AlertDialog.Builder(getActivity())
                .setTitle("삭제")
                .setMessage("정말로 삭제하시겠습니까?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 확인시 처리 로직
                        deleteItem(id);
                        Toast.makeText(getActivity(), "삭제했습니다.", Toast.LENGTH_SHORT).show();
                        showList();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 취소시 처리 로직
                    }})
                .show();
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
        priceEditView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        this.accountBookActivity = (AccountBookActivity) context;
        super.onAttach(context);
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

class SpinnerAdapter extends ArrayAdapter<String> {
    Context context;
    String[] items = new String[] {};

    public SpinnerAdapter(final Context context,
                          final int textViewResourceId, final String[] objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }

    /**
     * 스피너 클릭시 보여지는 View의 정의
     */
    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setGravity(Gravity.CENTER);
        tv.setText(items[position]);
        tv.setTextSize(20);
        return convertView;
    }

    /**
     * 기본 스피너 View 정의
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setGravity(Gravity.CENTER);
        tv.setText(items[position]);
        tv.setTextSize(18);
        return convertView;
    }

}

