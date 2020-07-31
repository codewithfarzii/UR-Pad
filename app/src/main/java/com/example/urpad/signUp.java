package com.example.urpad;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signUp extends AppCompatActivity {

    TextInputLayout regName, regUserName, regEmail, regPassword;
    Button next, login;
    ImageView img;
    TextView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        img = findViewById(R.id.logoImage);
        logo = findViewById(R.id.logo);
        login = findViewById(R.id.loginbtn);
        next = findViewById(R.id.reg_btn);
        regName = findViewById(R.id.regName);
        regUserName = findViewById(R.id.regUserName);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() | !validateUserName() | !validateEmail() | !validatePassword()) {
                    return;
                }

                String _fullName = regName.getEditText().getText().toString().trim();
                String _username = regUserName.getEditText().getText().toString().trim();
                String _email = regEmail.getEditText().getText().toString().trim();
                String _password = regPassword.getEditText().getText().toString().trim();

                Intent intent = new Intent(signUp.this, signUp2.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(img, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_name");
                intent.putExtra("fullName", _fullName);
                intent.putExtra("username", _username);
                intent.putExtra("email", _email);
                intent.putExtra("password", _password);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp.this, pairs);
                startActivity(intent, options.toBundle());
                startActivity(intent);
                finish();
            }
        });
    }

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()) {
            regName.setError("Field can't be empty");
            return false;
        } else {
            regName.setError(null);
            regName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateUserName() {
        String val = regUserName.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";
        if (val.isEmpty()) {
            regUserName.setError("Field can't be empty");
            return false;
        } else if (val.length() >= 15) {
            regUserName.setError("Username is too long!");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            regUserName.setError("White Spaces are not allowed");
            return false;
        } else {
            regUserName.setError(null);
            regUserName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            regEmail.setError("Field can't be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regName.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();
        String passwrordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";
        if (val.isEmpty()) {
            regPassword.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwrordVal)) {
            regPassword.setError("Password is too weak");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loginActivity();
    }
    private void loginActivity(){
        Intent i = new Intent(signUp.this, login.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(logo, "logo_name");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp.this, pairs);
        startActivity(i, options.toBundle());
        startActivity(i);
        finish();
    }
}