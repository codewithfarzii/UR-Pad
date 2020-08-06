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

    TextInputLayout regName, regUserName;
    Button next, back;
    ImageView img;
    TextView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        img = findViewById(R.id.logoImage);
        logo = findViewById(R.id.logo);
        back = findViewById(R.id.back);
        next = findViewById(R.id.reg_btn);
        regName = findViewById(R.id.regName);
        regUserName = findViewById(R.id.regUserName);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backProfile();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateName() | !validateUserName()) {
                    return;
                }
                String _fullName = regName.getEditText().getText().toString().trim();
                String _username = regUserName.getEditText().getText().toString().trim();

                Intent intent = new Intent(signUp.this, signUp2.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(img, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_name");
                intent.putExtra("fullName", _fullName);
                intent.putExtra("username", _username);
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
        } else if (val.length() > 20) {
            regName.setError("Name is too long!");
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
        } else if (val.length() >= 18) {
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backProfile();
    }

    private void backProfile() {
        Intent i = new Intent(signUp.this, UserProfile.class);
        startActivity(i);
        finish();
    }
}