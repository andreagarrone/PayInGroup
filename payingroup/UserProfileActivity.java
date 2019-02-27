package com.example.andre.payingroup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andre on 07/05/18.
 */

public class UserProfileActivity extends Activity {

    private TextView nameSurnameTextView;
    private TextView emailTextView;
    private FirebaseAuth mAuth;
    private TextView emptyTextView;
    ListView listView;
    String userId;
    ArrayList<String> tablesArrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    DatabaseReference databaseReferenceTables;
    String tableId;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nameSurnameTextView = (TextView) findViewById(R.id.user_profile_nameSurname_TextView);
        emptyTextView = (TextView) findViewById(R.id.user_profile_empty_TextView);
        emailTextView = (TextView) findViewById(R.id.user_profile_email_TextView);
        listView = (ListView) findViewById(R.id.user_profile_listView);
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Users");
        databaseReference1 = databaseReference.child(userId);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("name")) {
                        nameSurnameTextView.setText(snapshot.getValue().toString());
                    } else if (snapshot.getKey().equals("email")) {
                        emailTextView.setText("("+snapshot.getValue().toString()+")");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReferenceTables = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_user_profile_row, R.id.user_profile_row_TextView, tablesArrayList);
        databaseReferenceTables.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String creatorId = (String) snapshot.child("tableCreatorId").getValue();
                    if (creatorId.equals(userId)) {
                        count++;
                        String tableName = (String) snapshot.child("tableName").getValue();
                        tablesArrayList.add(tableName);

                    }
                }
                if (count == 0) {
                    emptyTextView.setText("YOU DON'T HAVE ANY TABLES");
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Intent intent = new Intent(UserProfileActivity.this, TableCreatedActivity.class);
                databaseReferenceTables.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            String tableName = (String) snapshot.child("tableName").getValue().toString();
                            if (tableName.equals(tablesArrayList.get(position))) {
                                tableId = (String) snapshot.getKey();
                                intent.putExtra("tableId", tableId);
                            }
                        }
                        startActivityForResult(intent, 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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
            case R.id.home:
                startActivity(new Intent(getBaseContext(), PayInGroupActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void signOut() {
        mAuth.signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            arrayAdapter.clear();
            nameSurnameTextView = (TextView) findViewById(R.id.user_profile_nameSurname_TextView);
            emptyTextView = (TextView) findViewById(R.id.user_profile_empty_TextView);
            emailTextView = (TextView) findViewById(R.id.user_profile_email_TextView);
            listView = (ListView) findViewById(R.id.user_profile_listView);
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getUid();

            databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Users");
            databaseReference1 = databaseReference.child(userId);

            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (snapshot.getKey().equals("name")) {
                            nameSurnameTextView.setText(snapshot.getValue().toString());
                        } else if (snapshot.getKey().equals("email")) {
                            emailTextView.setText("("+snapshot.getValue().toString()+")");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            databaseReferenceTables = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_user_profile_row, R.id.user_profile_row_TextView, tablesArrayList);
            databaseReferenceTables.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count = 0;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String creatorId = (String) snapshot.child("tableCreatorId").getValue();
                        if (creatorId.equals(userId)) {
                            count++;
                            String tableName = (String) snapshot.child("tableName").getValue();
                            tablesArrayList.add(tableName);

                        }
                    }
                    if (count == 0) {
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                    listView.setAdapter(arrayAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final Intent intent = new Intent(UserProfileActivity.this, TableCreatedActivity.class);
                    databaseReferenceTables.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                String tableName = (String) snapshot.child("tableName").getValue().toString();
                                if (tableName.equals(tablesArrayList.get(position))) {
                                    tableId = (String) snapshot.getKey();
                                    intent.putExtra("tableId", tableId);
                                }
                            }
                            startActivityForResult(intent, 1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }
}

