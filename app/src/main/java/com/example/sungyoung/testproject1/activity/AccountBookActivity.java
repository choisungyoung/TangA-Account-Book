package com.example.sungyoung.testproject1.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.fragment.ExpenseFragment;
import com.example.sungyoung.testproject1.fragment.TodayFragment;

public class AccountBookActivity extends AppCompatActivity {

    public ListView menuListView = null;
    public TodayFragment tf;
    ImageView slidImageView = null;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book);

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
                System.out.println("됨됨");
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
                        //지출내역
                        break;
                    case 2:
                        //수입내역
                        break;
                }

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                fragmentTransaction.commit();
            }
        });
    }
/*
    @Override
    protected void onDestroy() {
        dbHelper.sqliteExport();
        Log.d("test","onDestroy Called!!");
        super.onDestroy();
    }*/
}


