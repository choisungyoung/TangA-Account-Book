package com.example.sungyoung.testproject1.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.account.Account;
import com.example.sungyoung.testproject1.account.AccountContract;
import com.example.sungyoung.testproject1.account.AccountDBHelper;
import com.example.sungyoung.testproject1.fragment.ExpenseFragment;
import com.example.sungyoung.testproject1.fragment.SearchFragment;
import com.example.sungyoung.testproject1.fragment.TodayFragment;
import com.example.sungyoung.testproject1.util.AES256Util;

public class AccountBookActivity extends AppCompatActivity {

    public ListView menuListView = null;
    public ListView settingListView = null;
    public TodayFragment tf;
    ImageView slidImageView = null;
    DrawerLayout drawer;
    private AccountDBHelper dbHelper;

    private long backKeyPressedTime = 0;    //뒤로가기 누른시간
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book);

        dbHelper = new AccountDBHelper(this);

        //메뉴리스트
        //final String[] items = {"기본 금리가 높은 순", "최대 우대 금리가 높은 순", "기간이 짧은 순", "기간이 긴 순", "최소 월 납입금액 순", "우대금리로 계산"};
        final ArrayAdapter menuAdapter = ArrayAdapter.createFromResource(this, R.array.menu_array, android.R.layout.simple_list_item_1);
        final ArrayAdapter settingAdapter = ArrayAdapter.createFromResource(this, R.array.setting_array, android.R.layout.simple_list_item_1);

        //로딩 화면
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);


        tf = new TodayFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.accountFragment, tf);
        fragmentTransaction.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer) ;
        slidImageView = findViewById(R.id.slidImageView);

        menuListView = (ListView) findViewById(R.id.drawer_menulist);
        menuListView.setAdapter(menuAdapter);

        settingListView = (ListView) findViewById(R.id.drawer_settinglist);
        settingListView.setAdapter(settingAdapter);

        initListener();
    }

    public void initListener(){

        drawer.setDrawerListener(new ActionBarDrawerToggle(this, drawer, null,0,0){

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0)
                    slidImageView.setVisibility(View.GONE);
                else {
                    slidImageView.setVisibility(View.VISIBLE);
                }
            }
        });
        slidImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.openDrawer(GravityCompat.END);
                }
            }
        });
        menuListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                switch (position) {
                    case 0:
                        fragmentTransaction.replace(R.id.accountFragment, tf);
                        //오늘 내역
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.accountFragment, new ExpenseFragment());
                        //기간별 내역
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.accountFragment, new SearchFragment());
                        //기간별 내역
                        break;
                }

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                fragmentTransaction.commit();
            }
        });

        settingListView.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Cursor allData = dbHelper.selectAllData() ;
                        String str = allDataToString(allData);
                        String encryptStr;

                        try {
                            encryptStr = AES256Util.Encrypt(str);

                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("accountBookData", encryptStr);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getApplicationContext(), "클립보드에 복사되었습니다.", Toast.LENGTH_LONG);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "암호화에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG);
                            e.printStackTrace();
                        }
                        // 데이터 내보내기
                        break;
                    case 2:
                        final EditText edittext = new EditText(AccountBookActivity.this);

                        AlertDialog.Builder builder = new AlertDialog.Builder(AccountBookActivity.this);
                        builder.setTitle("데이터 가져오기");
                        builder.setMessage("내보내기한 데이터를 붙여넣어 주세요.");
                        builder.setView(edittext);
                        builder.setPositiveButton("덮어쓰기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbHelper.deleteAll();
                                        String decryptStr;
                                        try {
                                            decryptStr = AES256Util.Decrypt(edittext.getText().toString());
                                            setDataAll(decryptStr);
                                            tf.showList();
                                            Toast.makeText(getApplicationContext(), "데이터가 모두 입력되었습니다.", Toast.LENGTH_LONG);
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "복호화에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG);
                                            e.printStackTrace();
                                        }
                                    }
                                });

                        builder.setNegativeButton("추가하기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String decryptStr;
                                        try {
                                            decryptStr = AES256Util.Decrypt(edittext.getText().toString());
                                            setDataAll(decryptStr);
                                            tf.showList();
                                            Toast.makeText(getApplicationContext(), "데이터가 모두 입력되었습니다.", Toast.LENGTH_LONG);
                                        } catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "복호화에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG);
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        // 데이터 불러오기
                        builder.show();
                        break;
                    case 3:
                        //데이터 다지우기 (테스트용)
                        dbHelper.deleteAll();
                        tf.showList();
                        break;
                }

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                fragmentTransaction.commit();
            }
        });
    }
    public String allDataToString(Cursor cursor){
        String imex = null;
        String price = null;
        String accountName = null;
        String date = null;

        String allData = "";
        while (cursor.moveToNext()) {
            date        = cursor.getString(cursor.getColumnIndex("date"));
            accountName = cursor.getString(cursor.getColumnIndex("accountName"));
            price       = cursor.getString(cursor.getColumnIndex("price"));
            imex        = cursor.getString(cursor.getColumnIndex("imex"));

            allData += date+","+accountName+","+price+","+imex+";";
        };
        Toast.makeText(getApplication(), "데이터가 클립보드에 복사되었습니다.",Toast.LENGTH_LONG).show();
        return allData;
    }

    public void setDataAll(String allData){
        String[] accountDataList = allData.split(";");

        for(int i = 0 ; i < accountDataList.length ; i++){
            String[] accountData = accountDataList[i].split(",");
            if(accountData.length != 4){
                Toast.makeText(getApplicationContext(),"잘못된 데이터입니다." ,Toast.LENGTH_LONG).show();
                continue;
            }
            dbHelper.insertAccount(new Account(accountData[0],accountData[1],accountData[2],accountData[3]));
        }

        Toast.makeText(getApplicationContext(),"데이터가 추가되었습니다." ,Toast.LENGTH_LONG).show();

    }

    public void onBackPressed() {
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }
/*
    @Override
    protected void onDestroy() {
        dbHelper.sqliteExport();
        Log.d("test","onDestroy Called!!");
        super.onDestroy();
    }*/
}


