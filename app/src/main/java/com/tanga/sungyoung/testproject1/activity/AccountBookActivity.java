package com.tanga.sungyoung.testproject1.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.tanga.sungyoung.testproject1.R;
import com.tanga.sungyoung.testproject1.account.Account;
import com.tanga.sungyoung.testproject1.account.AccountDBHelper;
import com.tanga.sungyoung.testproject1.fragment.DiaryFragment;
import com.tanga.sungyoung.testproject1.fragment.ExpenseFragment;
import com.tanga.sungyoung.testproject1.fragment.HelpFragment;
import com.tanga.sungyoung.testproject1.fragment.SearchFragment;
import com.tanga.sungyoung.testproject1.fragment.TodayFragment;
import com.tanga.sungyoung.testproject1.util.AES256Util;
import com.tanga.sungyoung.testproject1.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AccountBookActivity extends AppCompatActivity {

    public ListView menuListView = null;
    public ListView settingListView = null;

    ImageView slidImageView = null;
    DrawerLayout drawer;
    private AccountDBHelper dbHelper;
    public TodayFragment todayFragment;

    private long backKeyPressedTime = 0;    //뒤로가기 누른시간
    private Toast toast;

    private ArrayAdapter menuAdapter = null;
    private ArrayAdapter settingAdapter = null;

    private TextView settingTitle = null;
    private int backBoorCount = 10;
    private boolean adminMode = false;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_book);

        //로딩 화면
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        /*************************** init ads *****************************/
        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                // 광고가 문제 없이 로드시 출력됩니다.
                Log.d("@@@", "onAdLoaded");
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fail
                // 광고 로드에 문제가 있을시 출력됩니다.
                Log.d("@@@", "onAdFailedToLoad " + errorCode);
            }
            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }
            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
        /*************************** init ads *****************************/


        dbHelper = new AccountDBHelper(this);

        //메뉴리스트
        //final String[] items = {"기본 금리가 높은 순", "최대 우대 금리가 높은 순", "기간이 짧은 순", "기간이 긴 순", "최소 월 납입금액 순", "우대금리로 계산"};
        menuAdapter = ArrayAdapter.createFromResource(this, R.array.menu_array, android.R.layout.simple_list_item_1);
        settingAdapter = ArrayAdapter.createFromResource(this, R.array.setting_array, android.R.layout.simple_list_item_1);

        todayFragment = new TodayFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.mainFragment, todayFragment);
        fragmentTransaction.commit();

        drawer = (DrawerLayout) findViewById(R.id.drawer) ;
        slidImageView = findViewById(R.id.slidImageView);

        menuListView = (ListView) findViewById(R.id.drawer_menulist);
        menuListView.setAdapter(menuAdapter);

        settingListView = (ListView) findViewById(R.id.drawer_settinglist);
        settingListView.setAdapter(settingAdapter);
        settingTitle = (TextView) findViewById(R.id.settingTitle);



        initListener();
    }

    public void initListener(){
        settingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adminMode)
                    return;
                backBoorCount--;
                if(backBoorCount <= 3){
                    if(backBoorCount == 0){
                        settingAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.setting_array, android.R.layout.simple_list_item_1);
                        String[] settingArray = getResources().getStringArray(R.array.setting_array);
                        String[] adminSettinArray = getResources().getStringArray(R.array.admin_setting_array);
                        String[] temp = new String[settingArray.length + adminSettinArray.length];

                        System.arraycopy(settingArray, 0, temp, 0, settingArray.length);
                        System.arraycopy(adminSettinArray, 0, temp, settingArray.length, adminSettinArray.length);
                        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, temp);
                        settingListView.setAdapter(adapter);
                        Toast.makeText(getApplicationContext(), "관리자모드로 진입하였습니다.", Toast.LENGTH_SHORT).show();
                        adminMode = true;
                    }
                    else{
                        //Toast.makeText(getApplicationContext(), backBoorCount + "회 터치 후 관리자모드로 진입합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
                removeFragment(fragmentTransaction);
                switch (position) {
                    case 0:

                        fragmentTransaction.replace(R.id.mainFragment, todayFragment);
                        //오늘 내역
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.mainFragment, new ExpenseFragment());
                        //기간별 내역
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.mainFragment, new SearchFragment());
                        //기간별 내역
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.mainFragment, new DiaryFragment());
                        //다이어리
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
                        // 도움말
                        fragmentTransaction.replace(R.id.mainFragment, new HelpFragment());
                        break;
                    case 1:
                        JSONObject totalJson = new JSONObject();
                        Cursor allAccount = dbHelper.selectAllAccount() ;
                        Cursor allDiary = dbHelper.selectAllDiary() ;
                        JSONArray accountJsonArray = Util.cur2Json(allAccount);
                        JSONArray memoJsonArray = Util.cur2Json(allDiary);
                        String encryptStr;

                        try {
                            totalJson.put("Account", accountJsonArray);
                            totalJson.put("Diary", memoJsonArray);

                            String str = totalJson.toString();
                            encryptStr = AES256Util.Encrypt(str);

                            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("accountBookData", encryptStr);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(getApplicationContext(), "클립보드에 복사되었습니다.", Toast.LENGTH_LONG).show();
                        }catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "내보내기가 실패하였습니다. 도움말의 이메일로 문의주십시오.", Toast.LENGTH_LONG).show();
                            return;
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "암호화에 실패하였습니다. 도움말의 이메일로 문의주십시오.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        // 데이터 내보내기
                        break;
                    case 2:
                        final EditText edittext = new EditText(AccountBookActivity.this);

                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        p.height = 200;
                        edittext.setLayoutParams(p);

                        edittext.setMaxHeight(400);
                        AlertDialog.Builder builder = new AlertDialog.Builder(AccountBookActivity.this);
                        builder.setTitle("데이터 가져오기");
                        builder.setMessage("내보내기한 데이터를 붙여넣어 주세요.");
                        builder.setView(edittext);
                        builder.setPositiveButton("저장하기",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //dbHelper.deleteAllAccount();
                                        String decryptStr = "";
                                        try {
                                            decryptStr = AES256Util.Decrypt(edittext.getText().toString().replaceAll("(\r\n|\r|\n|\n\r)", ""));
                                            setDataAll(decryptStr);
                                            //tf.showList();
                                            Toast.makeText(getApplicationContext(), "데이터가 모두 입력되었습니다. 앱을 재실행해주십시오.", Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), "데이터 저장 실패. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                            e.printStackTrace();
                                        }catch (Exception e) {
                                            Toast.makeText(getApplicationContext(), "복호화에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                            Log.e("admin saved failed", e.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        // 데이터 불러오기
                        builder.show();
                        break;
                    case 3:
                        //데이터 다지우기
                        dbHelper.deleteAllAccount();
                        dbHelper.deleteAllDiary();
                        dbHelper.printAccountData();
                        break;

/*
                    case 3:
                        dbHelper.printAccountData();
                        break;
                    case 4:
                        dbHelper.printDiaryData();
                        break;
                    case 5:
                        //데이터 다지우기 (테스트용)
                        dbHelper.deleteAllAccount();
                        break;
                    case 6:
                        //데이터 다지우기 (테스트용)
                        dbHelper.deleteAllDiary();
                        break;
                    case 7:
                        //데이터 다지우기 (테스트용)
                        dbHelper.dropAccount();
                        break;
                    case 8:
                        //데이터 다지우기 (테스트용)
                        dbHelper.dropDiary();
                        break;
 */
                }

                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                fragmentTransaction.commit();
            }
        });

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void removeFragment(FragmentTransaction fragmentTransaction){
        /*
        fragmentTransaction.remove(todayFragment);
        fragmentTransaction.remove(expenseFragment);
        fragmentTransaction.remove(searchFragment);
        fragmentTransaction.remove(diaryFragment);
        fragmentTransaction.remove(helpFragment);*/
    }
    public void setDataAll(String allData) throws JSONException {
        JSONObject totalJson = new JSONObject(allData);
        JSONArray accountArray  = totalJson.getJSONArray("Account");
        JSONArray diaryArray     = totalJson.getJSONArray("Diary");

        for(int i = 0 ; i < accountArray.length() ; i++){
            JSONObject json = accountArray.getJSONObject(i);
            String date         = json.getString("date");
            String accountName  = json.getString("accountName");
            String price        = json.getString("price");
            String imex         = json.getString("imex");

            dbHelper.insertAccount(new Account(date, accountName, price, imex));
        }

        for(int i = 0 ; i < diaryArray.length() ; i++){
            JSONObject json = diaryArray.getJSONObject(i);
            String date  = json.getString("date");
            String memo  = json.getString("memo");

            dbHelper.updateMemo(date, memo);
        }
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


