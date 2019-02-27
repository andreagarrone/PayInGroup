package com.example.andre.payingroup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
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

/**
 * Created by Andre on 12/04/18.
 */

public class RegistrationActivity extends Activity {

    private TextView nameTextView;
    private TextView surnameTextView;
    private TextView emailTextView;
    private TextView passwordTextView;
    private TextView confirmPasswordTextView;
    private TextView registrationLabelTextView;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private Button registerNowButton;
    private FirebaseUser user;

    //dichiaro autenticazione
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameEditText = findViewById(R.id.registration_activity_name_editText);
        surnameEditText = findViewById(R.id.registration_activity_surname_editText);
        emailEditText = findViewById(R.id.registration_activity_email_editText);
        passwordEditText = findViewById(R.id.registration_activity_password_editText);
        confirmPasswordEditText = findViewById(R.id.registration_activity_confirm_password_editText);
        registerNowButton = findViewById(R.id.registration_activity_register_now_button);

        registerNowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validateForm()) {
                    createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }

        });

        //inizializzo autenticazione
        mAuth = FirebaseAuth.getInstance();
    }

    //metodo per creazione account
    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            String name = nameEditText.getText().toString();
                            String surname = surnameEditText.getText().toString();
                            if (user != null) {
                                UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name + " " + surname)
                                        .build();

                                user.updateProfile(profile)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }

                                        });

                            }

                            writeNodeUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationActivity.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]

        final String name = nameEditText.getText().toString();

    }


    //metodo che controlla se l'email e la password sono state scritte
    private boolean validateForm() {
        boolean valid = true;

        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Required.");
            valid = false;
        } else {
            nameEditText.setError(null);
        }

        String surname = surnameEditText.getText().toString();
        if (TextUtils.isEmpty(surname)) {
            surnameEditText.setError("Required.");
            valid = false;
        } else {
            surnameEditText.setError(null);
        }

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
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

    public void writeNodeUser() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Users");

        String id = mAuth.getCurrentUser().getUid();
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();

        String email = emailEditText.getText().toString();

        Account account  = new Account(id, name+ " "+surname, email);
        databaseReference.child(id).setValue(account, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getBaseContext(), "Data could not be saved. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Registration successful.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getBaseContext(), LoginRegisterActivity.class));
                }
            }
        });
    }
}

