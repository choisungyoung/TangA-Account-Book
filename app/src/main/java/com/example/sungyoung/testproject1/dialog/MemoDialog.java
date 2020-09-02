package com.example.sungyoung.testproject1.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.sungyoung.testproject1.R;

public class MemoDialog extends Dialog {

    private Context context;
    private Button cofirmButton;
    private EditText memoEditText;

    public MemoDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_memo);
        init();
        initListener();
    }

    public void init(){
        Window win = getWindow();
        WindowManager.LayoutParams winLp = win.getAttributes();
        winLp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        win.setAttributes(winLp);

        cofirmButton = (Button)findViewById(R.id.bottom_button);
        memoEditText = (EditText)findViewById(R.id.memoEditText);
    }
    public void initListener(){
        cofirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog();
            }
        });
    }
    public void closeDialog(){
        this.dismiss();
    }
}
