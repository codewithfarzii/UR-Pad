package com.example.urpad;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.time.Year;
import java.util.Calendar;

public class signUp2 extends AppCompatActivity {

    Button login, next;
    ImageView img;
    TextView logo;
    RadioGroup radioGroup;
    RadioButton selectedGender;
    DatePicker datePicker;
    String Date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        img = findViewById(R.id.logoImage);
        logo = findViewById(R.id.logo);
        login = findViewById(R.id.loginbtn);
        next = findViewById(R.id.reg_btn);
        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!getDOB() | !getGender()) {
                    return;
                }
                Log.d("check", "success Date ->" + Date);
                int id1 = radioGroup.getCheckedRadioButtonId();
                selectedGender = findViewById(id1);
                String _fullName = getIntent().getStringExtra("fullName");
                String _email = getIntent().getStringExtra("email");
                String _username = getIntent().getStringExtra("username");
                String _password = getIntent().getStringExtra("password");
                String _gender = selectedGender.getText().toString();
                String _date = Date;
                Intent intent = new Intent(signUp2.this, signUp3.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(img, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_name");
                intent.putExtra("fullName", _fullName);
                intent.putExtra("username", _username);
                intent.putExtra("email", _email);
                intent.putExtra("password", _password);
                intent.putExtra("date", _date);
                intent.putExtra("gender", _gender);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp2.this, pairs);
                startActivity(intent, options.toBundle());
                startActivity(intent);
                finish();

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
        if (c_y != u_y && (u_y + 10) <= c_y)
            return true;
        else {
            Toast.makeText(signUp2.this, "You must be 10 Years old to use this software", Toast.LENGTH_SHORT).show();
            return false;
        }
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
        loginActivity();
    }

    private void loginActivity() {
        Intent i = new Intent(signUp2.this, login.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(img, "logo_image");
        pairs[1] = new Pair<View, String>(logo, "logo_name");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signUp2.this, pairs);
        startActivity(i, options.toBundle());
        startActivity(i);
        finish();
    }
}