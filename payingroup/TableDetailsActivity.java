package com.example.andre.payingroup;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Andre on 11/04/18.
 */

public class TableDetailsActivity extends Activity {

    private TextView tableNameTextView;
    private TextView passwordTextView;
    private TextView confirmPasswordTextView;
    private EditText tableNameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private Button createTableButton;
    private FirebaseUser user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_details);

        tableNameTextView = findViewById(R.id.table_details_activity_table_name_textView);
        tableNameEditText = findViewById(R.id.table_details_activity_table_name_editText);
        passwordTextView = findViewById(R.id.table_details_activity_password_textView);
        passwordEditText = findViewById(R.id.table_details_activity_password_editText);
        confirmPasswordTextView = findViewById(R.id.table_details_activity_confirm_password_textView);
        confirmPasswordEditText = findViewById(R.id.table_details_activity_confirm_password_editText);
        createTableButton = findViewById(R.id.table_details_activity_create_table_button);
        createTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateForm()) {
                    Intent data = new Intent(TableDetailsActivity.this, CameraActivity.class);
                    String tableName = tableNameEditText.getText().toString();
                    String tablePassword = passwordEditText.getText().toString();
                    data.putExtra("tableName", tableName);
                    data.putExtra("tablePassword", tablePassword);
                    startActivity(data);
                }
            }
        });


    }


    //metodo che controlla se l'email e la password sono state scritte
    private boolean validateForm() {
        boolean valid = true;

        String name = tableNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            tableNameEditText.setError("Required.");
            valid = false;
        } else {
            tableNameEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        String confirmPassword = confirmPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Required.");
            valid = false;

        } else if(!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password doesn't match");
            valid = false;

        } else {
            confirmPasswordEditText.setError(null);
        }

        return valid;
    }


}
