package com.example.andre.payingroup;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Andre on 17/04/18.
 */

public class CameraActivity extends AppCompatActivity {

    ImageView imageView;
    final int CROP_PIC = 98;
    Uri fileUri;
    Bitmap bitmap2;
    Button readInvoice;
    String tableName;
    String tablePassword;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        tableName = getIntent().getStringExtra("tableName");
        tablePassword = getIntent().getStringExtra("tablePassword");

        imageView = (ImageView) findViewById(R.id.camera_activity_imageView);
        readInvoice = (Button) findViewById(R.id.camera_activity_button_capture);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, 99);

        readInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getTextFromImage(view);
            }
        });
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    //save image method
    private File getOutputMediaFile() {

        // External sdcard location
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Create the storage directory if it does not exist
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {

                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(storageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("CIAO", String.valueOf(requestCode));

        if (resultCode != RESULT_OK)
            return;

        if(requestCode == 99) {

            //After taking a picture, do the crop
            performCrop();

        } else if(requestCode == CROP_PIC) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                bitmap2 = BitmapFactory.decodeFile(fileUri.getPath(), options);

                imageView.setImageBitmap(bitmap2);
        }
    }

    //crop image method
    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(fileUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            //cropIntent.putExtra("aspectX", 2);
            //cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            //cropIntent.putExtra("outputX", 256);
            //cropIntent.putExtra("outputY", 256);
            // true to return a Bitmap, false to directly save the cropped iamge
            cropIntent.putExtra("return-data", false);
            //save output image in uri
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //ocr from image method
    private void getTextFromImage(View v) {

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if(!textRecognizer.isOperational()) {

            Toast.makeText(getApplicationContext(), "Could not get the text", Toast.LENGTH_SHORT).show();
        }
        else {
            String text1 = null;
            String text2 = null;
            TextBlock text = null;
            Frame frame = new Frame.Builder().setBitmap(bitmap2).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            Intent data = new Intent(CameraActivity.this, TableConfirmActivity.class);

            for (int i = 0; i < items.size(); ++i) {
                text = items.valueAt(i);
                if(i == 0) {
                    text1 = stringBuilder.append(text.getValue()).toString();
                } else if(i ==1) {
                    stringBuilder.setLength(0);
                    text2 = stringBuilder.append(text.getValue()).toString();

                }

            }

            data.putExtra("text1", text1);
            data.putExtra("text2", text2);
            data.putExtra("tableName", tableName);
            data.putExtra("tablePassword", tablePassword);
            startActivity(data);
        }
    }
}
