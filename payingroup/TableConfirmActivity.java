package com.example.andre.payingroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Andre on 19/04/18.
 */

public class TableConfirmActivity extends AppCompatActivity {

    EditText editText1;
    EditText editText2;
    Button confirmButton;
    String products;
    String prices;
    String tableName;
    String tablePassword;
    String tableId;
    ArrayList<String> productsArray = new ArrayList<String>();
    ArrayList<String> pricesArray = new ArrayList<String>();
    //dichiaro autenticazione
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_confirm);

        editText1 = (EditText) findViewById(R.id.table_confirm_activity_invoice_editText1);
        editText1.setMovementMethod(new ScrollingMovementMethod());
        editText2 = (EditText) findViewById(R.id.table_confirm_activity_invoice_editText2);
        editText2.setMovementMethod(new ScrollingMovementMethod());
        //inizializzo autenticazione
        mAuth = FirebaseAuth.getInstance();
        confirmButton = (Button) findViewById(R.id.table_confirm_activity_confirm_button);

        products = getIntent().getStringExtra("text1");
        editText1.setText(products);
        prices = getIntent().getStringExtra("text2");
        prices = prices.replaceAll("\n+", "\n");
        prices = prices.replaceAll("((?!\n+)\\s+)", " ");
        prices = prices.replaceAll("((?!\n+)\\s+)", "");
        tableName = getIntent().getStringExtra("tableName");
        tablePassword = getIntent().getStringExtra("tablePassword");
        editText2.setText(prices);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                products = editText1.getText().toString();
                prices = editText2.getText().toString();
                Scanner productScanner = new Scanner(products);
                Scanner priceScanner = new Scanner(prices);
                while (priceScanner.hasNextLine()) {

                    productsArray.add(productScanner.nextLine());
                    pricesArray.add(priceScanner.nextLine());
                }


                writeNodeTable();
                Intent intent = new Intent(TableConfirmActivity.this, TableCreatedActivity.class);
                intent.putStringArrayListExtra("productsArray", productsArray);
                intent.putStringArrayListExtra("pricesArray", pricesArray);
                intent.putExtra("tableId", tableId);
                startActivity(intent);

            }
        });

    }

    public void writeNodeTable() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
        String tableCreatorId = mAuth.getCurrentUser().getUid();
        tableId = FirebaseDatabase.getInstance().getReference("tableId").push().getKey();

        Table table  = new Table(tableCreatorId, tableName, tablePassword, tableId);
        databaseReference.child(tableId).setValue(table, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Toast.makeText(getBaseContext(), "Data could not be saved. " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Table creation successful.", Toast.LENGTH_SHORT).show();

                }
            }
        });
        String [] products = new String[productsArray.size()];
        for(int i = 0; i < productsArray.size(); ++i) {
            products [i] = "Product " + i;
            databaseReference.child(tableId).child(products[i]).child("Product").setValue(productsArray.get(i));
            databaseReference.child(tableId).child(products[i]).child("Price").setValue(pricesArray.get(i));
        }
    }
}
