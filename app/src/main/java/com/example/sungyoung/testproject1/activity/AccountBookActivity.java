package com.example.sungyoung.testproject1.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
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
import com.example.sungyoung.testproject1.fragment.TodayFragment;

public class AccountBookActivity extends AppCompatActivity {

    public ListView menuListView = null;
    public TodayFragment tf;
    ImageView slidImageView = null;
    DrawerLayout drawer;
    private AccountDBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book);

        dbHelper = new AccountDBHelper(this);

        //메뉴리스트
        //final String[] items = {"기본 금리가 높은 순", "최대 우대 금리가 높은 순", "기간이 짧은 순", "기간이 긴 순", "최소 월 납입금액 순", "우대금리로 계산"};
        final ArrayAdapter menuAdapter = ArrayAdapter.createFromResource(this, R.array.menu_array, android.R.layout.simple_list_item_1);

        tf = new TodayFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.accountFragment, tf);
        fragmentTransaction.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer) ;
        slidImageView = findViewById(R.id.slidImageView);
        menuListView = (ListView) findViewById(R.id.drawer_menulist);
        menuListView.setAdapter(menuAdapter);

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
                        Cursor allData = dbHelper.selectAllData() ;
                        String str = allDataToString(allData);

                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("accountBookData", str);
                        clipboardManager.setPrimaryClip(clipData);
                        // 데이터 내보내기
                        break;
                    case 3:
                        final EditText edittext = new EditText(AccountBookActivity.this);

                        AlertDialog.Builder builder = new AlertDialog.Builder(AccountBookActivity.this);
                        builder.setTitle("데이터 가져오기");
                        builder.setMessage("내보내기한 데이터를 붙여넣어 주세요.");
                        builder.setView(edittext);
                        builder.setPositiveButton("덮어쓰기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbHelper.deleteAll();
                                        setDataAll(edittext.getText().toString());
                                        tf.showList();
                                    }
                                });

                        builder.setNegativeButton("추가하기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        setDataAll(edittext.getText().toString());
                                        tf.showList();
                                    }
                                });
                        // 데이터 불러오기
                        builder.show();
                        break;
                    case 4:
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
/*
    @Override
    protected void onDestroy() {
        dbHelper.sqliteExport();
        Log.d("test","onDestroy Called!!");
        super.onDestroy();
    }*/
}


