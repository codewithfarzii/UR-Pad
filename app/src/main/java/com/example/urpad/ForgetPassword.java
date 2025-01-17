package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class ForgetPassword extends AppCompatActivity {

    TextInputLayout currentPassword;
    RelativeLayout progressbar;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        currentPassword = findViewById(R.id.currentPassword);
        progressbar = findViewById(R.id.login_progressBar);
        next = findViewById(R.id.next_btn);

       /* next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check", "next click from forget password");
//                if (!validatePhoneNumber()) {
//                    return;
//                }
                progressbar.setVisibility(View.VISIBLE);
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(ForgetPassword.this)) {
                    showCustomDialog();
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(ForgetPassword.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.d("check", "get data start ");
                //Remove first zero if entered!
//                String _getUserEnteredPhoneNumber = phoneNumber.getEditText().getText().toString();
//                Log.d("check", "phone-> " + _getUserEnteredPhoneNumber);
//                //Remove first zero if entered!
//                if (_getUserEnteredPhoneNumber.charAt(0) == '0') {
//                    _getUserEnteredPhoneNumber = _getUserEnteredPhoneNumber.substring(1);
//                }
//                //Complete phone number
//                final String _phoneNo = "+" + countryCodePicker.getFullNumber() + _getUserEnteredPhoneNumber;
//                Log.d("check", "phoneno-> " + _phoneNo);
                //DataBase
                Query checkUser = FirebaseDatabase.getInstance().getReference("users").orderByChild("phoneNo").equalTo(_phoneNo);
                Log.d("check", "quer run");
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())//some data arrived
                        {
                            Log.d("check", "intent");
                            Intent intent = new Intent(ForgetPassword.this, verifyOTP.class);
                            intent.putExtra("phoneNo", _phoneNo);
                            intent.putExtra("whatToDO", "updateData");
                            startActivity(intent);
                            progressbar.setVisibility(View.GONE);
                            finish();
                        } else {
                            progressbar.setVisibility(View.GONE);
                            Toast.makeText(ForgetPassword.this, "No Such User Exists!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(ForgetPassword.this, "Error -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      callAccountSetting();
    }

    public void callBackScreenFromForgetPassword(View view) {
        // Toast.makeText(this,"Home",Toast.LENGTH_LONG).show();
            callAccountSetting();
    }

    private void callLogin() {
        Intent i = new Intent(this, login.class);
        startActivity(i);
        finish();
    }

    private void callAccountSetting() {
        Intent i = new Intent(ForgetPassword.this, AccountSetting.class);
        startActivity(i);
        finish();
    }

    private Boolean validateCurrentPassword() {
        String val = currentPassword.getEditText().getText().toString();
        if (val.isEmpty()) {
            currentPassword.setError("Field can't be empty");
            return false;
        } else {
            currentPassword.setError("Incorrect password");
            currentPassword.setError(null);
            currentPassword.setErrorEnabled(false);
            return true;
        }
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassword.this);
        builder.setMessage("Please Connet to the Internet for Further Process!")
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
}