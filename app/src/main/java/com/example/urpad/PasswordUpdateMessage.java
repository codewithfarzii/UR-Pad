package com.example.urpad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

public class PasswordUpdateMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_update_message);
    }
    public void backToLogin(View view){
        loginActivity();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loginActivity();
    }

    private void loginActivity() {
        Toast.makeText(this,"Now Login to Your account",Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, login.class);
        startActivity(i);
        finish();
    }
}