package com.example.andre.payingroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andre on 02/05/18.
 */

public class TableSearchedActivity extends AppCompatActivity {


    //String tableName;
    String tableId;
    private Button button;
    private TextView emptyTable;
    ArrayList<String> productsPricesArrayList;
    ArrayList<String> productsArrayList;
    ArrayList<String> pricesArrayList;
    List list = new LinkedList();
    ListView listView;
    ArrayList<String> selectedStrings;
    private FirebaseAuth mAuth;
    String tableCreatorId;
    static boolean loaded = false;
    CustomAdapterSearched adapter;

    DatabaseReference databaseReference;
    DatabaseReference databaseReference1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_searched);
        button = (Button) findViewById(R.id.table_searched_activity_calculate_button);
        mAuth = FirebaseAuth.getInstance();
        selectedStrings = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.table_searched_activity_listView);
        emptyTable = (TextView) findViewById(R.id.table_searched_activity_empty_textView);
        tableId = getIntent().getStringExtra("tableId");
        productsPricesArrayList = new ArrayList<String>();
        productsArrayList = new ArrayList<String>();
        pricesArrayList = new ArrayList<String>();
        adapter = new CustomAdapterSearched(this, R.layout.activity_table_created_final_row, list);
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
        databaseReference1 = databaseReference.child(tableId);
        pricesArrayList.clear();
        productsArrayList.clear();
        list.clear();
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableCreatorId = (String) dataSnapshot.child("tableCreatorId").getValue();
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    count++;
                    if (snapshot.hasChildren()) {
                        String itemPrice = (String) snapshot.child("Price").getValue();
                        String itemProduct = (String) snapshot.child("Product").getValue();
                        pricesArrayList.add(itemPrice);
                        productsArrayList.add(itemProduct);
                    }
                }
                if (count == 4) {
                    emptyTable.setText("THE TABLE HAS BEEN ALREADY PAID");
                    emptyTable.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                }
                for (int i = 0; i < pricesArrayList.size(); ++i) {
                    list.add(new ProductsPrices(productsArrayList.get(i), pricesArrayList.get(i)));
                }
                //selectedStrings = adapter.getSelectedString();
                listView.setAdapter(adapter);
                pricesArrayList.clear();
                productsArrayList.clear();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedStrings = adapter.getSelectedString();
                if (selectedStrings.isEmpty()) {
                    Toast.makeText(getBaseContext(), "Check some items. ", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(TableSearchedActivity.this, CalculatePaymentActivity.class);
                    intent.putStringArrayListExtra("selectedStrings", selectedStrings);
                    intent.putExtra("tableCreatorId", tableCreatorId);
                    intent.putExtra("tableId", tableId);
                    startActivityForResult(intent, 1);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        emptyTable = (TextView) findViewById(R.id.table_searched_activity_empty_textView);
        if (resultCode == RESULT_OK) {
            adapter.clear();
            adapter = new CustomAdapterSearched(this, R.layout.activity_table_created_final_row, list);
            databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
            databaseReference1 = databaseReference.child(tableId);
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    tableCreatorId = (String) dataSnapshot.child("tableCreatorId").getValue();
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        count++;
                        if (snapshot.hasChildren()) {
                            String itemPrice = (String) snapshot.child("Price").getValue();
                            String itemProduct = (String) snapshot.child("Product").getValue();
                            pricesArrayList.add(itemPrice);
                            productsArrayList.add(itemProduct);
                        }
                    }
                    if (count == 3) {
                        emptyTable.setText("THE TABLE HAS BEEN PAID");
                        emptyTable.setVisibility(View.VISIBLE);
                        button.setVisibility(View.GONE);
                    }
                    for (int i = 0; i < pricesArrayList.size(); ++i) {
                        list.add(new ProductsPrices(productsArrayList.get(i), pricesArrayList.get(i)));
                    }
                    //selectedStrings = adapter.getSelectedString();
                    listView.setAdapter(adapter);
                    pricesArrayList.clear();
                    productsArrayList.clear();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
            Toast.makeText(getBaseContext(), "Payment succeded!. ", Toast.LENGTH_SHORT).show();
        } else if (resultCode == RESULT_CANCELED) {

        }
    }
}
