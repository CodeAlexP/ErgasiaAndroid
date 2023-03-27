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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView banner, registerUser;
    private EditText editTextEmail, editTextPassword, editTextFName, editTextLName;
    private CheckBox checkBox;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
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
       Button btnRegister=findViewById(R.id.registerUser);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                registerUser();
            }

        });
        TextView textViewswitchtologin=findViewById(R.id.switchtologin);
        textViewswitchtologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchtologin();
            }
        });

    }
    private void showChangeLanguageDialog() {
        final String[] listItems = {"Greek-Ελληνικά","English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Register.this);
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



    private void registerUser() {
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextFName = (EditText) findViewById(R.id.fname);
        editTextLName = (EditText) findViewById(R.id.lname);
        checkBox = (CheckBox) findViewById(R.id.checkBox3);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);


        String fname = editTextFName.getText().toString();
        String lname = editTextLName.getText().toString();
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        Boolean checkbox = checkBox.isChecked();


        if (fname.isEmpty()) {
            editTextFName.setError("First name is required!");
            editTextFName.requestFocus();
            return;

        }
        if (lname.isEmpty()) {
            editTextLName.setError("Last name is required!");
            editTextLName.requestFocus();
            return;

        }


        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;

        }
        if (password.length() < 6) {

            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);




        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user=new User(fname,lname,email,checkbox);
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            showMainActivity();
                                        }
                                    });

                        } else {
                            Toast.makeText(Register.this,"Authentication failed.",Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    private void  switchtologin(){

        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMainActivity() {
        Intent intent=new Intent(this, Redirect.class);
        startActivity(intent);
        finish();

    }
}

