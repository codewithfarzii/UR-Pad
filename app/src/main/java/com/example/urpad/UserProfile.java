package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {

    ImageView back, icon;
    CircleImageView profileImage;
    Button set;
    TextView auth, fullNameField, userNameField, genderField, dobField, topFullName, topUserame;
    RelativeLayout progressbar;
    URI downloadURI;
    Uri ImageUri;
    int TAKE_IMAGE_CODE = 10002;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    boolean gallery = false;
    SessionManager sessionManager;
    String userID,authID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);
        Log.d("check","Started");
        //Hooks
        back = findViewById(R.id.back);
        icon = findViewById(R.id.Imageicon);
        fullNameField = findViewById(R.id.name);
        userNameField = findViewById(R.id.userName);
        genderField = findViewById(R.id.gender);
        dobField = findViewById(R.id.dob);
        topFullName = findViewById(R.id.fullname_field);
        topUserame = findViewById(R.id.username_field);
        progressbar = findViewById(R.id.login_progressBar);
        profileImage = findViewById(R.id.profile_image);
        set = findViewById(R.id.reg_btn);
        auth = findViewById(R.id.phoneNo_label);
        Log.d("check","hooks done");
        final PopupMenu popupMenu = new PopupMenu(
                this,
                profileImage
        );
        popupMenu.getMenuInflater().inflate(R.menu.select_image_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_takeImage) {
                    setProfileImage();
                }
                if (id == R.id.nav_gallerImage) {
                    getPermission();
                }

                return false;
            }
        });
        // set data
        setValues();
        getFromDatabase();
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMain();
            }
        });
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserProfile.this, signUp.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void LoadImage() {
        progressbar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference Imageference = storage.getReference()
                .child("profileImages")
                .child(userID + ".jpeg");
        Imageference.getBytes(1024 * 1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        profileImage.setImageBitmap(bitmap);
                        progressbar.setVisibility(View.GONE);
                    }
                });
        Imageference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("check", "uri-->" + uri);
            }
        });
        progressbar.setVisibility(View.GONE);

//        Glide.with(this)
//                .load()
//                .into(profileImage);

    }

    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission not granted
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //Pop up fro run time permission
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                // Permmission granted
                getImageFromGallery();
            }
        } else {
            // System os is less marshwellow
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        gallery = true;
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void setProfileImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            gallery = false;
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permsion was grated
                    getImageFromGallery();
                } else {
                    // permission was denied
                    Toast.makeText(this, "Permission Denied..!", Toast.LENGTH_SHORT).show();

                }
            }
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profileImage.setImageBitmap(bitmap);
                    handleUpload(bitmap);
            }
        }
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            ImageUri = data.getData();
            try {
                Bitmap bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(), ImageUri);
                profileImage.setImageBitmap(bitmap);
                handleUpload(bitmap);
            } catch (IOException e) {
                Log.d("check", "Error->", e.getCause());
            }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        progressbar.setVisibility(View.VISIBLE);
        Log.d("check", "handleUpload called");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (gallery) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(userID + ".jpeg");
        Log.d("check", "Query Build");
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("check", "Uploaded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("check", "Failed");
                        Log.e("check", "Failure", e.getCause());
                    }
                });
        progressbar.setVisibility(View.GONE);
    }

    private void getFromDatabase() {
        Log.d("checl","start getttig Firebase data");
        progressbar.setVisibility(View.VISIBLE);
        // check from DataBase
        DatabaseReference  ref= FirebaseDatabase.getInstance().getReference("users").child(userID);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //fetch all data
                String _fullname = snapshot.child("fullName").getValue(String.class);
                String _username = snapshot.child("userName").getValue(String.class);
                String _dateOfBirth = snapshot.child("dob").getValue(String.class);
                String _gender = snapshot.child("gender").getValue(String.class);

                fullNameField.setText(_fullname);
                topFullName.setText(_fullname);
                userNameField.setText(_username);
                topUserame.setText(_username);
                genderField.setText(_gender);
                dobField.setText(_dateOfBirth);
                auth.setText(authID);
                Log.d("checl","got Firebase data");
                LoadImage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        gotoMain();
    }

    private void gotoMain() {
        Intent i = new Intent(UserProfile.this, Home_Dash.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressbar.setVisibility(View.VISIBLE);
        setValues();
    }
    public void setValues(){
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        String authEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String authPhoneNo = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if(authEmail.length()>2){
            authID=authEmail;
            icon.setImageDrawable(getDrawable(R.drawable.ic_email));
        }
        if(authPhoneNo.length()>2){
            authID=authPhoneNo;
            icon.setImageDrawable(getDrawable(R.drawable.ic_phone));
        }
    }
}