package com.example.andre.payingroup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class TableCreatedActivity extends AppCompatActivity {

    ArrayList<String> productsArrayList;
    ArrayList<String> pricesArrayList;
    private TextView emptyTable;
    private FirebaseAuth mAuth;
    private ImageButton deleteTable;
    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    CustomAdapterCreated adapterCreated;
    ListView listView;
    List list;
    Task t;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_created);

        mAuth = FirebaseAuth.getInstance();
        final String tableId = getIntent().getStringExtra("tableId");
        productsArrayList = new ArrayList<String>();
        pricesArrayList = new ArrayList<String>();

        listView = (ListView)findViewById(R.id.table_activity_listView);
        emptyTable = (TextView) findViewById(R.id.table_created_activity_empty_textView);
        deleteTable = (ImageButton) findViewById(R.id.table_created_imageButton);
        list = new LinkedList();


        adapterCreated = new CustomAdapterCreated(this, R.layout.activity_table_created_final_row, list);
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
        databaseReference1 = databaseReference.child(tableId);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                String tableName = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    count++;
                    tableName = (String) dataSnapshot.child("tableName").getValue().toString();
                    if (snapshot.hasChildren()) {
                        String itemPrice = (String) snapshot.child("Price").getValue();
                        String itemProduct = (String) snapshot.child("Product").getValue();
                        pricesArrayList.add(itemPrice);
                        productsArrayList.add(itemProduct);
                    }
                }
                for (int i = 0; i < pricesArrayList.size(); ++i) {
                    list.add(new ProductsPrices(productsArrayList.get(i), pricesArrayList.get(i)));
                }
                if (count == 4) {
                    emptyTable.setText("THE TABLE HAS BEEN TOTALLY PAID");
                    emptyTable.setVisibility(View.VISIBLE);
                } else if (count > 4) {
                    emptyTable.setText(tableName);
                    emptyTable.setVisibility(View.VISIBLE);
                    emptyTable.setTextColor(Color.parseColor("#25a7da"));
                    emptyTable.setTextSize(30);

                }
                listView.setAdapter(adapterCreated);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(TableCreatedActivity.this);
                deleteAlert.setMessage("ARE YOU SURE YOU WANT TO DELETE THE TABLE?");
                deleteAlert.setCancelable(false);
                deleteAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setResult(RESULT_OK);
                        new TableDelete().execute();

                    }
                });




                deleteAlert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alert = deleteAlert.create();
                alert.show();
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

    class TableDelete extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            t = databaseReference1.removeValue();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent intent = new Intent(TableCreatedActivity.this, PayInGroupActivity.class);
            startActivity(intent);
        }
    }

    public void onBackPressed() {
        Intent goHome = new Intent(TableCreatedActivity.this, PayInGroupActivity.class);
        startActivity(goHome);
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        final ArrayList<String> productsArray = getIntent().getStringArrayListExtra("productsArray");
        final ArrayList<String> pricesArray = getIntent().getStringArrayListExtra("pricesArray");
        final String tableId = getIntent().getStringExtra("tableId");
        productsArrayList = new ArrayList<String>();
        pricesArrayList = new ArrayList<String>();

        listView = (ListView)findViewById(R.id.table_activity_listView);
        emptyTable = (TextView) findViewById(R.id.table_created_activity_empty_textView);
        deleteTable = (ImageButton) findViewById(R.id.table_created_imageButton);
        list = new LinkedList();


        adapterCreated = new CustomAdapterCreated(this, R.layout.activity_table_created_final_row, list);
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
        databaseReference1 = databaseReference.child(tableId);

        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                String tableName = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    count++;
                    tableName = (String) dataSnapshot.child("tableName").getValue().toString();
                    if (snapshot.hasChildren()) {
                        String itemPrice = (String) snapshot.child("Price").getValue();
                        String itemProduct = (String) snapshot.child("Product").getValue();
                        pricesArrayList.add(itemPrice);
                        productsArrayList.add(itemProduct);
                    }
                }
                for (int i = 0; i < pricesArrayList.size(); ++i) {
                    list.add(new ProductsPrices(productsArrayList.get(i), pricesArrayList.get(i)));
                }
                if (count == 4) {
                    emptyTable.setText("THE TABLE HAS BEEN TOTALLY PAID");
                    emptyTable.setVisibility(View.VISIBLE);
                } else if (count > 4) {
                    emptyTable.setText(tableName);
                    emptyTable.setVisibility(View.VISIBLE);
                    emptyTable.setTextColor(Color.parseColor("#25a7da"));
                    emptyTable.setTextSize(30);

                }
                listView.setAdapter(adapterCreated);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(TableCreatedActivity.this);
                deleteAlert.setMessage("ARE YOU SURE YOU WANT TO DELETE THE TABLE?");
                deleteAlert.setCancelable(false);
                deleteAlert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference1.removeValue();
                        setResult(RESULT_OK);
                        dialog.dismiss();
                        finish();
                    }
                });
                deleteAlert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog alert = deleteAlert.create();
                alert.show();
            }
        });
    }*/
}
