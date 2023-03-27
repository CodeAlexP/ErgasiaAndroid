package com.example.androidergasia2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth  mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadLocale();
        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeLanguageDialog();
            }
        });
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            finish();
            return;

        }
        Button btnlogin=findViewById(R.id.login);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
        TextView switchtoregister=findViewById(R.id.register);
        switchtoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchtoRegister();
            }
        });

    }
    private void authenticateUser(){
        EditText etLogemail=findViewById(R.id.logeditemail);
        EditText etLogpassword=findViewById(R.id.logeditpassword);

        String email=etLogemail.getText().toString();
        String password=etLogpassword.getText().toString();
        if(email.isEmpty()||password.isEmpty()){

            Toast.makeText(this,"Please fill all fields",Toast.LENGTH_LONG).show();
            return;
        }


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMainactivity();
                        } else {
                           Toast.makeText(LoginActivity.this,"Authentication failed.",
                                   Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void showMainactivity(){
        Intent intent =new Intent(this, Redirect.class);
        startActivity(intent);
        finish();
    }

    private void switchtoRegister(){
        Intent intent =new Intent(this,Register.class);
        startActivity(intent);
        finish();

    }
    private void showChangeLanguageDialog() {
        final String[] listItems = {"Greek-Ελληνικά","English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LoginActivity.this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    //Greek
                    setLocale("el");
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
}