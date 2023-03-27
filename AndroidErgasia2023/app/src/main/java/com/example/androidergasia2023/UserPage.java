package com.example.androidergasia2023;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import  android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class UserPage extends AppCompatActivity implements LocationListener {
    private FirebaseAuth mAuth;
    private final int GALLERY_REQ_CODE = 1000;
    ImageView imggallery;
    LocationManager locationManager;
    public static String spinvalue;
    public static String longi,lati;
    public static String encoded="NUll";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_userpage);
        loadLocale();
        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });

        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser currentuser=auth.getCurrentUser();
        if(currentuser==null){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Button btnlogout=findViewById(R.id.logoutmain);
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        Spinner spinnernatdis=findViewById(R.id.natdis);
        EditText comments=findViewById(R.id.comments);


        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.natdis, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnernatdis.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        imggallery=findViewById(R.id.imageGallery);
        Button btnOpenGal=findViewById(R.id.opengal);
        btnOpenGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery=new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLERY_REQ_CODE);
            }
        });



    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {"Greek-Ελληνικά","English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserPage.this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    //Greek
                    setLocale("gr");
                    recreate();
                }
                if(i==1){
                    //English
                    setLocale("en");
                    recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show alert dialog
        mDialog.show();
    }

    private void setLocale(String lang) {
        Locale locale =  new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared preferenced
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My language",lang);
        editor.apply();
    }
    //load language saved in shared preferences
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_language", "");
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            if(requestCode==GALLERY_REQ_CODE){
                imggallery.setImageURI(data.getData());


            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
       longi=String.valueOf(location.getLongitude());
        lati=String.valueOf(location.getLatitude());
    }




    public void go1(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        //locationManager.removeUpdates(this);



            Spinner sp = findViewById(R.id.natdis);
            imggallery = findViewById(R.id.imageGallery);

            BitmapDrawable drawable = (BitmapDrawable) imggallery.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            imggallery.setImageBitmap(bitmap);


            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            FirebaseAuth auth=FirebaseAuth.getInstance();
            FirebaseUser currentuser=auth.getCurrentUser();

            spinvalue = sp.getSelectedItem().toString();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);


            String timeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date());


            EditText comments=findViewById(R.id.comments);

            if(lati!=null&&longi!=null) {
                Data data = new Data(spinvalue, encoded, timeStamp, lati, longi, comments.getText().toString());


                FirebaseDatabase.getInstance().getReference("data").
                        child("events").push().
                        setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UserPage.this, "Data Reported Successfully ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(UserPage.this, "Error Data Insertion ", Toast.LENGTH_SHORT).show();

                            }
                        });
            }



       // Bitmap bitmap2 = decodeBase64(encoded);



    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void logoutUser(){

        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }






}