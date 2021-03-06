package com.tanga.sungyoung.testproject1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.tanga.sungyoung.testproject1.R;
import com.tanga.sungyoung.testproject1.account.AccountDBHelper;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MemoDialog extends Dialog {

    private AccountDBHelper dbHelper;

    private Context context;
    private Button confirmButton;
    private EditText memoEditText;
    private String curDate;

    InputMethodManager imm ;
    public MemoDialog(@NonNull Context context, String curDate) {
        super(context);
        this.context = context;
        this.curDate = curDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_memo);
        init();
        initListener();
    }

    public void init(){
        dbHelper = new AccountDBHelper(getContext());
        Window win = getWindow();
        WindowManager.LayoutParams winLp = win.getAttributes();
        winLp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        win.setAttributes(winLp);

        confirmButton = (Button)findViewById(R.id.bottom_button);
        memoEditText = (EditText)findViewById(R.id.memoEditText);

        Cursor cursor = dbHelper.selectMemo(curDate);
        String memo = "";
        if(cursor.moveToNext()){
            memo = cursor.getString(cursor.getColumnIndex("memo"));
        }
        memoEditText.setText(memo);
    }
    public void initListener(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imm = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(memoEditText.getWindowToken(), 0);

                closeDialog();
            }
        });


    }
    public void closeDialog(){
        this.dismiss();
    }


    @Override
    protected void onStop() {
        dbHelper.updateMemo(curDate, memoEditText.getText().toString());
        super.onStop();
    }
}
