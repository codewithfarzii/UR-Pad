package com.example.urpad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class setNewPassword extends AppCompatActivity {

    TextInputLayout  newPassword,conPassword;
    RelativeLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        //hooks
        newPassword = findViewById(R.id.new_password);
        conPassword = findViewById(R.id.confirm_password);
        progressbar = findViewById(R.id.login_progressBar);


    }
    public void goToHomeFromSetNewPassword(View view){
       loginActivity();
    }

    public void setNewPasswordBtn(View view){
        progressbar.setVisibility(View.VISIBLE);
        CheckInternet checkInternet=new CheckInternet();
        if(!checkInternet.isConnected(setNewPassword.this)){
            showCustomDialog();
            progressbar.setVisibility(View.GONE);
            Toast.makeText(setNewPassword.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validatePassword1() | !validatePassword2() | !matchPassword()){
            progressbar.setVisibility(View.GONE);
            return;
        }
        String phoneNo = getIntent().getStringExtra("phoneNo");
        Log.d("check","no->"+phoneNo);
        String _newPassword=newPassword.getEditText().getText().toString();

        Log.d("check","newpas->"+_newPassword);
        //Update database
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users");
        reference.child(phoneNo).child("password").setValue(_newPassword);

        Log.d("check","updated");
        Intent i = new Intent(this, PasswordUpdateMessage.class);
        Log.d("check","start intent");
        startActivity(i);
        progressbar.setVisibility(View.GONE);
        finish();

    }
    private Boolean validatePassword1() {
        String val = newPassword.getEditText().getText().toString();
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
            newPassword.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwrordVal)) {
            newPassword.setError("Password is too weak");
            return false;
        } else {
            newPassword.setError(null);
            newPassword.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword2() {
        String val = conPassword.getEditText().getText().toString();
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
            conPassword.setError("Field can't be empty");
            return false;
        } else if (!val.matches(passwrordVal)) {
            conPassword.setError("Password is too weak");
            return false;
        } else {
            conPassword.setError(null);
            conPassword.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean matchPassword() {
        String p1 = newPassword.getEditText().getText().toString();
        String p2 = conPassword.getEditText().getText().toString();
        if (!p1.matches(p2)) {
            conPassword.setError("Password doesn't matches");
            return false;
        } else {
            conPassword.setError(null);
            conPassword.setErrorEnabled(false);
            return true;
        }
    }
    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(setNewPassword.this);
        builder.setMessage("Please Connet to the Internet for Updation!")
                .setCancelable(true)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loginActivity();
    }

    private void loginActivity() {
       // Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, login.class);
        startActivity(i);
        progressbar.setVisibility(View.GONE);
        finish();
    }

}