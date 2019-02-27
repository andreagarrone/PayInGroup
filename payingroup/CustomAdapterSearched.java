package com.example.andre.payingroup;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andre on 24/04/18.
 */

public class CustomAdapterSearched extends ArrayAdapter<ProductsPrices> {

    public CustomAdapterSearched(Context context, int textViewResourceId,
                         List list) {
        super(context, textViewResourceId, list);
    }

    ArrayList<String> selectedStrings = new ArrayList<String>();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_table_searched_row, null);
        //TextView productsTextView = (TextView)convertView.findViewById(R.id.table_searched_row_activity_products_textView);
        final CheckBox pricesCheckBox = (CheckBox) convertView.findViewById(R.id.table_searched_row_activity_prices_checkBox);

        ProductsPrices c = getItem(position);
        //productsTextView.setText(c.getProducts());
        pricesCheckBox.setText(c.getProducts() + ": " +c.getPrices() + " €");

        pricesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    selectedStrings.add(pricesCheckBox.getText().toString());
                } else {
                    selectedStrings.remove(pricesCheckBox.getText().toString());
                }
            }

        });

        return convertView;
    }
    ArrayList<String> getSelectedString(){
        return selectedStrings;
    }
}