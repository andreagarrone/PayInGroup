package com.example.andre.payingroup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Andre on 02/05/18.
 */

public class TableAccessActivity extends Activity {

    private TextView tablePasswordTextView;
    private EditText tablePasswordEditText;
    private TextView tableNameTextView;
    private Button enterButton;
    String tableName;
    String tableId;
    String tablePassword;
    String tableCreatorId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_access);

        tablePasswordTextView = (TextView) findViewById(R.id.table_access_activity_password_textView);
        tablePasswordEditText = (EditText) findViewById(R.id.table_access_activity_password_editText);
        tableNameTextView = (TextView) findViewById(R.id.table_access_activity_table_name_textView);
        enterButton = (Button) findViewById(R.id.table_access_activity_enter_button);
        tableName = getIntent().getStringExtra("tableName");
        tableId = getIntent().getStringExtra("tableId");
        tablePassword = getIntent().getStringExtra("tablePassword");
        tableCreatorId = getIntent().getStringExtra("tableCreatorId");
        mAuth = FirebaseAuth.getInstance();
        final String tableCreatorIdUser = mAuth.getCurrentUser().getUid();
        tableNameTextView.setText(tableName);
        Dialog dialog = new Dialog(this, R.style.DialogTheme);


        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = tablePasswordEditText.getText().toString();
                if (password.equals(tablePassword) && tableCreatorIdUser.equals(tableCreatorId)) {
                    Intent intent = new Intent(TableAccessActivity.this, TableCreatedActivity.class);
                    intent.putExtra("tableName", tableName);
                    intent.putExtra("tableId", tableId);
                    startActivity(intent);
                } else if (password.equals(tablePassword) && !tableCreatorIdUser.equals(tableCreatorId)) {
                    Intent intent = new Intent(TableAccessActivity.this, TableSearchedActivity.class);
                    intent.putExtra("tableName", tableName);
                    intent.putExtra("tableId", tableId);
                    startActivity(intent);
                } else if (TextUtils.isEmpty(password)) {
                    tablePasswordEditText.setError("Required.");
                } else {
                    tablePasswordEditText.setError("Password incorrect.");
                }
            }
        });

    }
}
