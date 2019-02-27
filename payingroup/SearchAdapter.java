package com.example.andre.payingroup;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andre on 01/05/18.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> tableNameArrayList;
    ArrayList<String> tableIdArrayList;
    ArrayList<String> tablePasswordArrayList;
    ArrayList<String> tableCreatorIdArrayList;

    class SearchViewHolder extends RecyclerView.ViewHolder {

        TextView tableNameTextView;
        TextView hiddenIdTextView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            tableNameTextView = (TextView) itemView.findViewById(R.id.payingroup_recyclerview_row_activity_textView);
            hiddenIdTextView = (TextView) itemView.findViewById(R.id.payingroup_recyclerview_row_activity_hiddenTextView);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> tableNameArrayList, ArrayList<String> tableIdArrayList, ArrayList<String> tablePasswordArrayList, ArrayList<String> tableCreatorIdArrayList) {
        this.context = context;
        this.tableNameArrayList = tableNameArrayList;
        this.tableIdArrayList = tableIdArrayList;
        this.tablePasswordArrayList = tablePasswordArrayList;
        this.tableCreatorIdArrayList = tableCreatorIdArrayList;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.activity_payingroup_recyclerview_row, parent, false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        final String tablePassword;
        final String tableCreatorId;
        holder.tableNameTextView.setText(tableNameArrayList.get(position));
        holder.hiddenIdTextView.setText((tableIdArrayList.get(position)));
        tablePassword = tablePasswordArrayList.get(position);
        tableCreatorId = tableCreatorIdArrayList.get(position);
        holder.tableNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tableName = (String) holder.tableNameTextView.getText();
                String tableId = (String) holder.hiddenIdTextView.getText();
                Intent intent = new Intent(context, TableAccessActivity.class);
                intent.putExtra("tableName", tableName);
                intent.putExtra("tableId", tableId);
                intent.putExtra("tablePassword", tablePassword);
                intent.putExtra("tableCreatorId", tableCreatorId);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return tableNameArrayList.size();
    }
}
