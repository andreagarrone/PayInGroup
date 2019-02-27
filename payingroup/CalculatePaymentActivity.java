package com.example.andre.payingroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.paypal.android.MEP.PayPalActivity.EXTRA_ERROR_ID;
import static com.paypal.android.MEP.PayPalActivity.EXTRA_ERROR_MESSAGE;

public class CalculatePaymentActivity extends Activity {

    TextView priceSum;
    float amount;
    String tableCreatorId;
    String tableCreatorEmail;
    String tableId;
    List list = new LinkedList();
    private Button cashButton;
    ArrayList<String> selectedStrings;
    ArrayList<String> newSelectedStrings;
    private CheckoutButton launchPayPalButton;
    private boolean paypalLibraryInit = false;
    private static final int REQUEST_PAYPAL_CHECKOUT = 2;
    final static public int PAYPAL_BUTTON_ID = 10001;
    Task t;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_payment);
        tableCreatorId = getIntent().getStringExtra("tableCreatorId");
        tableId = getIntent().getStringExtra("tableId");
        selectedStrings = getIntent().getStringArrayListExtra("selectedStrings");
        priceSum = (TextView) findViewById(R.id.calculate_payment_activity_price_textView);
        cashButton = (Button) findViewById(R.id.calculate_payment_activity_cash_button);
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        newSelectedStrings = new ArrayList<>();


        final CustomAdapterSearched adapter = new CustomAdapterSearched(this, R.layout.activity_table_created_final_row, list);
        selectedStrings.trimToSize();
        for (int j = 0; j < selectedStrings.size(); ++j) {
            String string = selectedStrings.get(j);
            ArrayList<Character> chars = new ArrayList<Character>();
            char[] c = string.toCharArray();
            for (int x = 0; x < c.length; x++) {
                chars.add(c[x]);
            }


            StringBuilder builder = new StringBuilder(chars.size());
            for(int i = chars.size()-6; i < chars.size()-2; ++i)
            {

                builder.append(chars.get(i));
            }
            newSelectedStrings.add(builder.toString());

        }
        double[] doubleList = new double[newSelectedStrings.size()];
        double sum = 0;
        //casting from arraylist<string> to double array and calculate prices sum
        for (int i = 0; i < newSelectedStrings.size(); ++i) {
            try {
                doubleList[i] = format.parse(newSelectedStrings.get(i)).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sum += doubleList[i];
        }

        priceSum.setText(String.valueOf(sum));
        amount = Float.parseFloat(priceSum.getText().toString());
        final DatabaseReference databaseReferenceUsers = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Users");
        DatabaseReference databaseReferenceUsers1 = databaseReferenceUsers.child(tableCreatorId);


        databaseReferenceUsers1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableCreatorEmail = (String) dataSnapshot.child("email").getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //CASH
        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new TableDelete().execute();
            }
        });

        //PAYPAL
        initLibrary();

        showPayPalButton();


    }

    private void showPayPalButton() {

        removePayPalButton();
        PayPal payPal = PayPal.getInstance();
        launchPayPalButton= payPal.getCheckoutButton(this, PayPal.BUTTON_278x43, CheckoutButton.TEXT_PAY);

        /*launchPayPalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payPalButtonClick(amount);
            }
        });*/

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = 70;
        params.topMargin = 50;
        params.leftMargin = 20;
        params.rightMargin = 20;

        launchPayPalButton.setLayoutParams(params);
        launchPayPalButton.setId(PAYPAL_BUTTON_ID);

        ((LinearLayout) findViewById(R.id.activity_calculate_payment_id)).addView(launchPayPalButton);
        ((LinearLayout) findViewById(R.id.activity_calculate_payment_id)).setGravity(Gravity.CENTER_HORIZONTAL);

    }

    private void payPalButtonClick(float amount) {

        PayPalPayment payment = new PayPalPayment();

        payment.setCurrencyType("EURO");

        payment.setRecipient("andrea.garrone@live.com");
        BigDecimal st = new BigDecimal(amount);
        st = st.setScale(2, RoundingMode.HALF_UP);
        payment.setSubtotal(st);
        payment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);

        Intent checkoutIntent = PayPal.getInstance().checkout(payment, this);
        startActivityForResult(checkoutIntent, REQUEST_PAYPAL_CHECKOUT);

    }

    private void initLibrary() {

        PayPal payPal = PayPal.getInstance();
        if (payPal == null) {
            payPal = PayPal.initWithAppID(this, "APP- 80W284485P519543T", PayPal.ENV_NONE);
            payPal.setLanguage("en_US");
            paypalLibraryInit = true;
        }
    }

    public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                String paykey = intent.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
                this.paymentSucceded(paykey);
                break;
            case Activity.RESULT_CANCELED:
                this.paymentCanceled();
                break;
            case PayPalActivity.RESULT_FAILURE:
                String errorID = intent.getStringExtra(EXTRA_ERROR_ID);
                String errorMessage = intent.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
                this.paymentFailed(errorID, errorMessage);

        }
    }

    public void paymentSucceded(String payKey) {
        Toast.makeText(getBaseContext(), "Payment succeded!. ", Toast.LENGTH_SHORT).show();
    }

    public void paymentFailed(String errorId, String errorMessage) {
        Toast.makeText(getBaseContext(), "Payment failed!. ", Toast.LENGTH_SHORT).show();
    }

    public void paymentCanceled() {
        Toast.makeText(getBaseContext(), "Payment canceled!. ", Toast.LENGTH_SHORT).show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_PAYPAL_CHECKOUT) {
            PayPalActivityResult(requestCode, resultCode, intent);
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    final Runnable showPayPalButtonRunnable = new Runnable() {
        public void run() {
            showPayPalButton();
        }
    };

    final Runnable checkforPayPalInitRunnable = new Runnable() {
        public void run() {
            checkForPayPalLibraryInit();
        }
    };

    private void checkForPayPalLibraryInit() {
        // Loop as long as the library is not initialized
        while (paypalLibraryInit == false) {
            try {
                // wait 1/2 a second then check again
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Show an error to the user
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Error initializing PayPal Library")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // Could do anything here to handle the
                                        // error
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        // If we got here, it means the library is initialized.
        // So, add the "Pay with PayPal" button to the screen
        runOnUiThread(showPayPalButtonRunnable);
    }

    private void removePayPalButton() {
        // Avoid an exception for setting a parent more than once
        if (launchPayPalButton != null) {
            ((LinearLayout) findViewById(R.id.activity_calculate_payment_id))
                    .removeView(launchPayPalButton);
        }
    }

    class TableDelete extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://payingroup.firebaseio.com/Tables");
            final DatabaseReference databaseReference1 = databaseReference.child(tableId);
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        if (snapshot.hasChildren()) {

                            String itemPrice = (String) snapshot.child("Price").getValue();
                            String itemProduct = (String) snapshot.child("Product").getValue();
                            for (int i = 0; i < selectedStrings.size(); ++i) {
                                if (selectedStrings.get(i).equals(itemProduct+": "+itemPrice+" €")) {
                                    t = snapshot.getRef().removeValue();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            setResult(RESULT_OK);
            finish();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}

