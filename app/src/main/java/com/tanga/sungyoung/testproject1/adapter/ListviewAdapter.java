package com.tanga.sungyoung.testproject1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tanga.sungyoung.testproject1.R;
import com.tanga.sungyoung.testproject1.account.Account;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<Account> data;
    private int layout;

    public ListviewAdapter(Context context, int layout, ArrayList<Account> data) {
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
        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }
        Account listviewitem = data.get(position);

        TextView accountName = convertView.findViewById(R.id.accountName);
        accountName.setText(listviewitem.getAccountName());

        TextView price = convertView.findViewById(R.id.price);
        price.setText(format.format(Long.parseLong(listviewitem.getPrice())) + " 원");

        if (listviewitem.getImex().equals("지출")) {
            price.setTextColor(Color.RED);
        }

        TextView hidden = convertView.findViewById(R.id.hidden);
        hidden.setText(listviewitem.getId());
        return convertView;
    }
}