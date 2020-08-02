package com.example.sungyoung.testproject1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.sungyoung.testproject1.R;
import com.example.sungyoung.testproject1.account.Account;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ImexListviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Account> data;
    private int layout;

    public ImexListviewAdapter(Context context, int layout, ArrayList<Account> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Account getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DecimalFormat format = new DecimalFormat("###,###");
//        if (convertView == null) {
//            convertView = inflater.inflate(layout, parent, false);
//        }
        convertView = inflater.inflate(layout, parent, false);
        Account listviewitem = data.get(position);
        TextView accountName = convertView.findViewById(R.id.accountName);
        TextView hidden = convertView.findViewById(R.id.hidden);
        TextView price = convertView.findViewById(R.id.price);
        LinearLayout linearLayout = convertView.findViewById(R.id.list_item);

        if(listviewitem.getAccountName().equals("")){

            price.setTextSize(16);
            price.setText("");
            price.setVisibility(View.GONE);
            accountName.setText(listviewitem.getDate());
            accountName.setTypeface(accountName.getTypeface(), Typeface.BOLD);
            accountName.setTextSize(16);
            accountName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.setEnabled(false);
            linearLayout.setPadding(0,5,0,5);
            linearLayout.setBackgroundColor(Color.rgb(255,240,187));
            return convertView;
        }
        if(listviewitem.getImex().equals("지출")){
            price.setTextColor(Color.RED);
        }
        accountName.setText(listviewitem.getAccountName());
        price.setText(format.format(Integer.parseInt(listviewitem.getPrice())) + " 원");
        hidden.setText(listviewitem.getId());
        return convertView;
    }
}