package com.example.urpad;

import androidx.annotation.NonNull;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class verifyOTP extends AppCompatActivity {
    TextView msg;
    PinView pinView;
    Button verify;
    String codeBySystem;
    String fullName, phoneNo, email, username, password, date, gender, whatToDO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        Log.d("check", "came at top");

        //hooks
        verify = findViewById(R.id.verify);
        pinView = findViewById(R.id.pin_view);
        msg = findViewById(R.id.otp_description_text);
        //Get all the data from Intent
        whatToDO = getIntent().getStringExtra("whatToDO");
        phoneNo = getIntent().getStringExtra("phoneNo");
        if (!whatToDO.equals("updateData")) {
            fullName = getIntent().getStringExtra("fullName");
            email = getIntent().getStringExtra("email");
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            date = getIntent().getStringExtra("date");
            gender = getIntent().getStringExtra("gender");
        }


        msg.setText("Enter One Time Password Sent On " + phoneNo);

        sendVerificationCodeToUser(phoneNo);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInternet checkInternet = new CheckInternet();
                if (!checkInternet.isConnected(verifyOTP.this)) {
                    showCustomDialog();
                    Toast.makeText(verifyOTP.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code = pinView.getText().toString();
                if (!code.isEmpty()) {
                    verifyCode(code);
                }
            }
        });

    }

    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,// Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codeBySystem = s;
                    Log.d("code ->", s);
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pinView.setText(code);
                        verifyCode(code);
                        Toast.makeText(verifyOTP.this, "" + code, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(verifyOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(verifyOTP.this, "Verification Completed!", Toast.LENGTH_SHORT).show();

                            //Verification completed successfully here Either
                            // store the data or do whatever desire
                            if (whatToDO.equals("updateData")) {
                                updateOldUsersData();
                            } else {
                                storeNewUsersData();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(verifyOTP.this, "Verification Not Completed! Try again.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(verifyOTP.this)) {
            showCustomDialog();
            Toast.makeText(verifyOTP.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateOldUsersData() {
        Intent i = new Intent(verifyOTP.this, setNewPassword.class);
        i.putExtra("phoneNo", phoneNo);
        startActivity(i);
        finish();

    }

    private void storeNewUsersData() {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("users");

        //Create helperclass reference and store data using firebase
        UserHelperClass addNewUser = new UserHelperClass(fullName, username, email, phoneNo, password, date, gender);
        reference.child(phoneNo).setValue(addNewUser);

        //We will also create a Session here for user logged In
        startActivity(new Intent(getApplicationContext(), signUpSetupMessage.class));
        finish();
    }

    public void goToHomeFromOTP(View view) {
        loginActivity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        loginActivity();
    }

    private void loginActivity() {
        // Toast.makeText(verifyOTP.this, "Home", Toast.LENGTH_LONG).show();
        Intent i = new Intent(verifyOTP.this, login.class);
        startActivity(i);
        finish();
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(verifyOTP.this);
        builder.setMessage("Please Connet to the Internet for verification!")
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