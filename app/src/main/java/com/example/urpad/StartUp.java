package com.example.urpad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StartUp extends AppCompatActivity {

    int AuthUi_REQUEST_CODE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_up);

    }

    public void btnClick(View view) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.note_scan)
                .setAlwaysShowSignInMethodScreen(true)
                .build();
        startActivityForResult(intent, AuthUi_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AuthUi_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // we have signed in the user or we hace new user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("test", "user email--> " + user.getEmail());
                if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                    Toast.makeText(StartUp.this, "Enjoy Our App!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(StartUp.this, "Welcome Back Again!", Toast.LENGTH_SHORT).show();
                }
                SessionManager sessionManager = new SessionManager(StartUp.this, SessionManager.SESSION_REMEMBERME);
                sessionManager.createRememberMeSession();
                homeDash();
            } else {
                //signing failed
                IdpResponse response = IdpResponse.fromResultIntent(data);
                if (response == null) {
                    Log.d("test", "SignUp cancel by user");
                } else
                    Log.d("test", "Error from firebase-->" + response.getError());
            }
        }
    }

    public void homeDash() {
        Intent intent = new Intent(StartUp.this, Home_Dash.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("test", "on start" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            // check shared preferences for phoneNO and Password
            SessionManager sessionManager = new SessionManager(StartUp.this, SessionManager.SESSION_REMEMBERME);
            if (sessionManager.checkRememberMe()) {
                homeDash();
            }
        }
    }
}
