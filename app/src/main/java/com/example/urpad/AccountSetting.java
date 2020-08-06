package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;

public class AccountSetting extends AppCompatActivity {

    String userID;
    RelativeLayout progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account_setting);
        progressbar=findViewById(R.id.login_progressBar);
    }

    public void onBackPressed() {
        super.onBackPressed();
        gotMain();
    }

    public void backhome(View view) {
        gotMain();
    }

    private void gotMain() {
        Intent i = new Intent(this, Home_Dash.class);
        startActivity(i);
        finish();
    }

    public void changePassword(View view) {
        Intent i = new Intent(this, setNewPassword.class);
        i.putExtra("setting", "yes");
        startActivity(i);
        finish();
    }

    public void deleteAccount(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetting.this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_alert))
                .setMessage("Are you sure you want to delete this Account!\nIt will delete your data too!")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressbar.setVisibility(View.VISIBLE);
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancel", null).show();

    }

    public void deleteCloudData(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AccountSetting.this);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_alert))
                .setMessage("Are you sure you want to clear Cloud data!\nIt will delete your all data!")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressbar.setVisibility(View.VISIBLE);
                        deleteData();
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }

    private void deleteData() {
        userID= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userid", userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshotList) {
                            batch.delete(snapshot.getReference());
                        }
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressbar.setVisibility(View.GONE);
                                Toast.makeText(AccountSetting.this, "All Cloud Data Delete!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressbar.setVisibility(View.GONE);
                        Toast.makeText(AccountSetting.this, "Couldn't delete!\nTry Again Later!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAccount() {
        userID= FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteData();
                LogOut();
                Toast.makeText(AccountSetting.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(AccountSetting.this, "Couldn't Delete Right Now!\nTry again later!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void LogOut() {
        progressbar.setVisibility(View.GONE);
        SessionManager sessionManager = new SessionManager(AccountSetting.this, SessionManager.SESSION_REMEMBERME);
        sessionManager.clearUser();
       Intent i = new Intent(AccountSetting.this,   StartUp.class);
        startActivity(i);
        finish();
    }
}