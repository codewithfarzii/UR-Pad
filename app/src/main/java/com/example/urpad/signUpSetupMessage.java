package com.example.urpad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

public class signUpSetupMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_setup_message);
    }
    public void backToLogin(View view){
       loginActivity();
    }
    public void onBackPressed() {
        super.onBackPressed();
        loginActivity();
    }

    private void loginActivity() {
       // Toast.makeText(this,"Now Login to Your account",Toast.LENGTH_LONG).show();
        Intent i = new Intent(signUpSetupMessage.this, login.class);
        startActivity(i);
        finish();
    }
}