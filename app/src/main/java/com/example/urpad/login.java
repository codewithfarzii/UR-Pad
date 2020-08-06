package com.example.urpad;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Queue;

public class login extends AppCompatActivity {

    Button signup, login, forget;
    ImageView img;
    TextView logo, sologan1, signtxt;
    TextInputLayout regPassword;
    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;
    RelativeLayout progressbar;
    CheckBox rememberMe;
    TextInputEditText phoneNo_RM, password_RM;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        //Initializing
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);
        img = findViewById(R.id.logoImage);
        logo = findViewById(R.id.logo);
        sologan1 = findViewById(R.id.slogan1);
        signtxt = findViewById(R.id.signtxt);
        regPassword = findViewById(R.id.regPassword);
        forget = findViewById(R.id.forget_btn);
        phoneNumber = findViewById(R.id.login_phone_number);
        countryCodePicker = findViewById(R.id.code_picker);
        progressbar = findViewById(R.id.login_progressBar);
        rememberMe = findViewById(R.id.rember_me);
        phoneNo_RM = findViewById(R.id.phoneNo_RM);
        password_RM = findViewById(R.id.password_RM);

        // check shared preferences for phoneNO and Password
        SessionManager sessionManager = new SessionManager(login.this, SessionManager.SESSION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
//            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailFromSession();
//            phoneNo_RM.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPHONENUMBER));
//            password_RM.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
        }
        //ad view hook
        MobileAds.initialize(this,"ca-app-pub-1494531846382800~5982462648"); //app id
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //functions
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this, signUp.class);
                Pair[] pairs = new Pair[8];
                pairs[0] = new Pair<View, String>(img, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_name");
                pairs[2] = new Pair<View, String>(sologan1, "logo_slogan");
                pairs[3] = new Pair<View, String>(signtxt, "signup_tran");
                pairs[4] = new Pair<View, String>(signup, "phone_tran");
                pairs[5] = new Pair<View, String>(phoneNumber, "password_tran");
                pairs[6] = new Pair<View, String>(login, "go_tran");
                pairs[7] = new Pair<View, String>(signup, "signbtn_tran");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login.this, pairs);
                startActivity(i, options.toBundle());
                startActivity(i);
                finish();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(login.this, ForgetPassword.class);
                startActivity(i);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePhoneNumber() | !validatePassword()) {
                    return;
                }
                progressbar.setVisibility(View.VISIBLE);
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(login.this)) {
                    showCustomDialog();
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(login.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                //get data
                final String _password = regPassword.getEditText().getText().toString();
                String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString();

                //Remove first zero if entered!
                if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
                    _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
                }
                //Complete phone number
                final String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhoneNumber;
                Log.d("check", "phoneno-> " + _phoneNo);

                if (rememberMe.isChecked()) {
                    SessionManager sessionManager = new SessionManager(login.this, SessionManager.SESSION_REMEMBERME);
//                    sessionManager.createRememberMeSession(_getUserEnteredPhoneNumber, _password);
                }


                // check from DataBase
                Query checkUser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phoneNo").equalTo(_phoneNo);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())//some data arrived
                        {
                            phoneNumber.setError(null);
                            phoneNumber.setErrorEnabled(false);

                            String systemPassword = snapshot.child(_phoneNo).child("password").getValue(String.class);
                            if (systemPassword.equals(_password)) {
                                regPassword.setError(null);
                                regPassword.setErrorEnabled(false);
                                progressbar.setVisibility(View.GONE);
                                //fetch all data
                                String _fullname = snapshot.child(_phoneNo).child("fullName").getValue(String.class);
                                String _username = snapshot.child(_phoneNo).child("username").getValue(String.class);
                                String _email = snapshot.child(_phoneNo).child("email").getValue(String.class);
                                String _phoneno = snapshot.child(_phoneNo).child("phoneNo").getValue(String.class);
                                String _password = snapshot.child(_phoneNo).child("password").getValue(String.class);
                                String _dateOfBirth = snapshot.child(_phoneNo).child("date").getValue(String.class);
                                String _gender = snapshot.child(_phoneNo).child("gender").getValue(String.class);
                                //  Toast.makeText(login.this, _fullname + "--" + _email + "--" + _phoneno + "--" + _dateOfBirth, Toast.LENGTH_SHORT).show();

                                //Create Session
                                Log.d("check", "intent called");
                                Intent i = new Intent(login.this, Home_Dash.class);
                                startActivity(i);
                                finish();
                            } else {
                                progressbar.setVisibility(View.GONE);
                                Toast.makeText(login.this, "Incorrect Password!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(login.this, "No Such User Exists!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(login.this, "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(login.this)) {
            showCustomDialog();
            Toast.makeText(login.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
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


    private Boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString();
        if (val.isEmpty()) {
            phoneNumber.setError("Field can't be empty");
            return false;
        } else if (val.length() > 15) {
            phoneNumber.setError("Invalid No of digits");
            return false;
        } else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = regPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            regPassword.setError("Field can't be empty");
            return false;
        } else {
            regPassword.setError(null);
            regPassword.setErrorEnabled(false);
            return true;
        }
    }
}