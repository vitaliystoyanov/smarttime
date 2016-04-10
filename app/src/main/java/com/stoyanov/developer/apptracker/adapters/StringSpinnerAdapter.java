package com.stoyanov.developer.apptracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stoyanov.developer.apptracker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringSpinnerAdapter extends BaseAdapter {

    private ArrayList<String> data = new ArrayList<>();
    private Context context;

    public StringSpinnerAdapter(Context context) {
        this.context = context;
    }

    public void clear() {
        data.clear();
    }

    public void addItem(String item) {
        data.add(item);
    }

    public void addItems(List<String> list) {
        data.addAll(list);
    }

    public void addItems(String[] items) {
        Collections.addAll(this.data, items);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = LayoutInflater.from(context).inflate(R.layout.toolbar_spinner_item_dropdown, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));

        return view;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
            view = LayoutInflater.from(context).inflate(R.layout.
                    toolbar_spinner_item_actionbar, parent, false);
            view.setTag("NON_DROPDOWN");
        }
        TextView textView = (TextView) view.findViewById(android.R.id.text1);
        textView.setText(getTitle(position));
        return view;
    }

    private String getTitle(int position) {
        return position >= 0 && position < data.size() ? data.get(position) : "";
    }
}