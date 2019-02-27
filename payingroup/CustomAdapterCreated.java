package com.example.andre.payingroup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andre on 24/04/18.
 */

public class CustomAdapterCreated extends ArrayAdapter<ProductsPrices> {

    public CustomAdapterCreated(Context context, int textViewResourceId,
                                List list) {
        super(context, textViewResourceId, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.activity_table_created_final_row, null);
        TextView productsTextView = (TextView)convertView.findViewById(R.id.table_created_final_row_activity_products_textView);
        TextView pricesTextView = (TextView)convertView.findViewById(R.id.table_created_final_row_activity_prices_textView);

        ProductsPrices c = getItem(position);
        productsTextView.setText(c.getProducts());
        pricesTextView.setText(c.getPrices());

        return convertView;
    }
}
