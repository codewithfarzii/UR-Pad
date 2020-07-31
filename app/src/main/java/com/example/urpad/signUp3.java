package com.example.urpad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

public class signUp3 extends AppCompatActivity {
    Button login, next;
    ImageView img;
    TextView logo;
    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;
    LinearLayout  scrollView;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up3);
        img=findViewById(R.id.logoImage);
        logo=findViewById(R.id.logo);
        login = findViewById(R.id.loginbtn);
        next = findViewById(R.id.reg_btn);
        phoneNumber=findViewById(R.id.signup_phone_number);
        countryCodePicker=findViewById(R.id.code_picker);
        scrollView=findViewById(R.id.parentlayout);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validatePhoneNumber()){
                    return;
                }
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(signUp3.this)) {
                    showCustomDialog();
                    Toast.makeText(signUp3.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Get all the data from Intent
               String _fullName = getIntent().getStringExtra("fullName");
                String _email = getIntent().getStringExtra("email");
                String _username = getIntent().getStringExtra("username");
                String _password = getIntent().getStringExtra("password");
                String _gender = getIntent().getStringExtra("gender");
                String _date = getIntent().getStringExtra("date");

                  //Get complete phone number
                String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString().trim();
                   //Remove first zero if entered!
                if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
                    _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
                }
                //Complete phone number
                final String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhoneNumber;

                Intent intent = new Intent(getApplicationContext(), verifyOTP.class);

              //Pass all fields to the next activity
                intent.putExtra("fullName", _fullName);
                intent.putExtra("email", _email);
                intent.putExtra("username", _username);
                intent.putExtra("password", _password);
                intent.putExtra("date", _date);
                intent.putExtra("gender", _gender);
                intent.putExtra("phoneNo", _phoneNo);
                intent.putExtra("whatToDO", "createNewUser"); // This is to identify that which action should OTP perform after verification.

                //Add Transition
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(scrollView, "transition_OTP_screen");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp3.this, pairs);
                    startActivity(intent, options.toBundle());
                    finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });
    }
      private Boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString();
        if (val.isEmpty()) {
            phoneNumber.setError("Field can't be empty");
            return false;
        }
        else if(val.length()>15){
            phoneNumber.setError("Invalid No of digits");
            return false;
        }
        else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(signUp3.this)) {
            showCustomDialog();
            Toast.makeText(signUp3.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(signUp3.this);
        builder.setMessage("Please Connet to the Internet for Login!")
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
        Intent i = new Intent(signUp3.this, login.class);
        Pair[] pairs=new Pair[2];
        pairs[0]=new Pair<View,String>(img,"logo_image");
        pairs[1]=new Pair<View,String>(logo,"logo_name");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp3.this,pairs);
        startActivity(i,options.toBundle());
        startActivity(i);
        finish();
    }
}