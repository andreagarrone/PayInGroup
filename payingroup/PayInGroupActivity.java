package com.example.andre.payingroup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PayInGroupActivity extends AppCompatActivity {

    private Button createTableButton;
    private EditText searchTableEditText;
    private RecyclerView recyclerView;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ArrayList<String> tableNameArrayList;
    ArrayList<String> tableIdArrayList;
    ArrayList<String> tablePasswordArrayList;
    ArrayList<String> tableCreatorIdArrayList;
    SearchAdapter searchAdapter;



    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_in_group);

        createTableButton = findViewById(R.id.payingroup_activity_create_table_button);
        searchTableEditText = (EditText) findViewById(R.id.payingroup_activity_editText);
        recyclerView = findViewById(R.id.payingroup_activity_recyclerView);

        createTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), TableDetailsActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        Log.d("ciao", "ciao");
        tableNameArrayList = new ArrayList<>();
        tableIdArrayList = new ArrayList<>();
        tablePasswordArrayList = new ArrayList<>();
        tableCreatorIdArrayList = new ArrayList<>();


        searchTableEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());
                } else {
                    tableNameArrayList.clear();
                    tableIdArrayList.clear();
                    tablePasswordArrayList.clear();
                    searchAdapter.notifyDataSetChanged();
                    recyclerView.removeAllViews();
                }
            }
        });


    }

    private void setAdapter(final String searchedString) {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableNameArrayList.clear();
                tableIdArrayList.clear();
                tablePasswordArrayList.clear();
                recyclerView.removeAllViewsInLayout();

                int counter = 0;

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    String tableName = snapshot.child("tableName").getValue(String.class);
                    String tableId = snapshot.child("tableId").getValue(String.class);
                    String tablePassword = snapshot.child("tablePassword").getValue(String.class);
                    String tableCreatorId = snapshot.child("tableCreatorId").getValue(String.class);

                    if (tableName.toLowerCase().contains(searchedString.toLowerCase())) {
                        tableNameArrayList.add(tableName);
                        tableIdArrayList.add(tableId);
                        tablePasswordArrayList.add(tablePassword);
                        tableCreatorIdArrayList.add(tableCreatorId);
                        counter++;
                    }

                    if (counter == 15) {
                        break;
                    }
                }

                searchAdapter = new SearchAdapter(PayInGroupActivity.this, tableNameArrayList, tableIdArrayList, tablePasswordArrayList, tableCreatorIdArrayList);
                recyclerView.setAdapter(searchAdapter);


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
                signOut();
                startActivity(new Intent(getBaseContext(), LoginRegisterActivity.class));
                return true;
            case R.id.guide:
                startActivity(new Intent(getBaseContext(), GuideActivity.class));
                return true;
            case R.id.user:
                startActivity(new Intent(getBaseContext(), UserProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

    public void onBackPressed() {
        Intent exitIntent = new Intent(Intent.ACTION_MAIN);
        exitIntent.addCategory(Intent.CATEGORY_HOME);
        exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(exitIntent);
    }
}

