package com.example.urpad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.time.Year;
import java.util.Calendar;

public class signUp2 extends AppCompatActivity {

    Button back, next;
    ImageView img;
    TextView logo;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;
    String Date;
    RelativeLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        img = findViewById(R.id.logoImage);
        logo = findViewById(R.id.logo);
        back = findViewById(R.id.back);
        next = findViewById(R.id.reg_btn);
        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);
        progressbar = findViewById(R.id.login_progressBar);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backProfile();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                progressbar.setVisibility(View.VISIBLE);
                if (!getDOB() | !getGender()) {
                    progressbar.setVisibility(View.GONE);
                    return;
                }
                Log.d("check", "success Date ->" + Date);
                uploadData();
            }
        });
    }

    public boolean getDOB() {
        Log.d("date", "DOB called");
        Calendar calendar = Calendar.getInstance();
        int c_y = calendar.get(Calendar.YEAR);
        int c_m = calendar.get(Calendar.MONTH);
        int c_d = calendar.get(Calendar.DATE);
        int u_y = datePicker.getYear();
        int u_m = datePicker.getMonth() + 1;
        int u_d = datePicker.getDayOfMonth();
        Date = u_y + "-" + u_m + "-" + u_d;
        return true;
    }

    public boolean getGender() {
        int id = radioGroup.getCheckedRadioButtonId();
        if (id == -1) {
            Toast.makeText(signUp2.this, "Please Select Gender!", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backProfile();
    }

    private void backProfile() {
        Intent i = new Intent(signUp2.this, UserProfile.class);
        startActivity(i);
        finish();
    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(signUp2.this);
        builder.setMessage("Please Connet to the Internet!")
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

    private void uploadData() {
        progressbar.setVisibility(View.VISIBLE);
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(signUp2.this)) {
            showCustomDialog();
            progressbar.setVisibility(View.GONE);
            return;
        }
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        //Get all the data from Intent
        String _fullName = getIntent().getStringExtra("fullName");
        String _username = getIntent().getStringExtra("username");
        int id1 = radioGroup.getCheckedRadioButtonId();
        selectedGender = findViewById(id1);
        String _gender = selectedGender.getText().toString();
        String _date = Date;
        UserData user=new UserData(_fullName,_username,_gender,_date,userID);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(userID).setValue(user);
        progressbar.setVisibility(View.GONE);
        backProfile();
    }
}