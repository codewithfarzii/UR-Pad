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
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    TextInputLayout regName, regUserName, regEmail;
    TextInputEditText SetfName, SetuName, Setemail;
    ImageView back;
    CircleImageView profileImage;
    Button update;
    RadioGroup radioGroup;
    TextView phoneNO, fullNameField, userNameField;
    RadioButton selectedGender, male, female, other;
    RelativeLayout progressbar;
    String _password, _dateOfBirth;
    URI downloadURI;
    Uri ImageUri;
    int TAKE_IMAGE_CODE = 10002;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    boolean gallery=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_profile);

        //Hooks
        back = findViewById(R.id.back);
        phoneNO = findViewById(R.id.phoneNo_label);
        update = findViewById(R.id.reg_btn);
        regName = findViewById(R.id.regName);
        regUserName = findViewById(R.id.regUserName);
        regEmail = findViewById(R.id.regEmail);
        radioGroup = findViewById(R.id.radio_group);
        SetfName = findViewById(R.id.SetfName);
        SetuName = findViewById(R.id.SetuName);
        Setemail = findViewById(R.id.Setemail);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        other = findViewById(R.id.other);
        fullNameField = findViewById(R.id.fullname_field);
        userNameField = findViewById(R.id.username_field);
        progressbar = findViewById(R.id.login_progressBar);
        profileImage = findViewById(R.id.profile_image);


        final PopupMenu popupMenu=new PopupMenu(
                this,
                profileImage
        );
        popupMenu.getMenuInflater().inflate(R.menu.select_image_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.nav_takeImage){
                      setProfileImage();
                }
                if(id==R.id.nav_gallerImage){
                    getPermission();
                }

                return false;
            }
        });
        // set data
        getFromSession();
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
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataOnProfile();
            }
        });
    }



    private void LoadImage() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference Imageference = storage.getReference()
                .child("profileImages")
                .child(phoneNO.getText().toString() + ".jpeg");
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

//        Glide.with(this)
//                .load()
//                .into(profileImage);

    }

    private void getPermission() {
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
           if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
               //permission not granted
               String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
               //Pop up fro run time permission
               requestPermissions(permissions,PERMISSION_CODE);
           }else
           {
               // Permmission granted
               getImageFromGallery();
           }
       }
       else{
           // System os is less marshwellow
           getImageFromGallery();
       }
    }
    private void getImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
       intent.setType("image/*");
       gallery=true;
            startActivityForResult(intent, IMAGE_PICK_CODE);
        }
    private void setProfileImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            gallery=false;
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
            {
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    // permsion was grated
                    getImageFromGallery();
                }else{
                    // permission was denied
                    Toast.makeText(this,"Permission Denied..!",Toast.LENGTH_SHORT).show();

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
        if(resultCode==RESULT_OK && requestCode==IMAGE_PICK_CODE){
           ImageUri=data.getData();
           try{
            Bitmap bitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(),ImageUri);
            profileImage.setImageBitmap(bitmap);
            handleUpload(bitmap);
        }catch (IOException e){
               Log.d("check","Error->",e.getCause());
           }
        }
    }

    private void handleUpload(Bitmap bitmap) {
        Log.d("check", "handleUpload called");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(gallery) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        }else{
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(phoneNO.getText().toString() + ".jpeg");
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
    }

    private void setDataOnProfile() {
        progressbar.setVisibility(View.VISIBLE);
        CheckInternet checkInternet = new CheckInternet();
        if (!checkInternet.isConnected(UserProfile.this)) {
            showCustomDialog();
            progressbar.setVisibility(View.GONE);
            Toast.makeText(UserProfile.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!validateName() | !validateUserName() | !validateEmail()) {
            progressbar.setVisibility(View.GONE);
            return;
        }
        String _fullName = regName.getEditText().getText().toString().trim();
        String _username = regUserName.getEditText().getText().toString().trim();
        String _email = regEmail.getEditText().getText().toString().trim();
        int id1 = radioGroup.getCheckedRadioButtonId();
        selectedGender = findViewById(id1);
        String _gender = selectedGender.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.child(phoneNO.getText().toString()).child("fullName").setValue(_fullName);
        reference.child(phoneNO.getText().toString()).child("username").setValue(_username);
        reference.child(phoneNO.getText().toString()).child("email").setValue(_email);
        reference.child(phoneNO.getText().toString()).child("gender").setValue(_gender);

        fullNameField.setText(_fullName);
        userNameField.setText(_username);
        //Create Session
        SessionManager sessionManager = new SessionManager(UserProfile.this, SessionManager.SESSION_USERSESSION);
        sessionManager.createLoginSession(_fullName, _username, _email, phoneNO.getText().toString(), _password, _dateOfBirth, _gender);
        Toast.makeText(UserProfile.this, "Updated", Toast.LENGTH_SHORT).show();
        progressbar.setVisibility(View.GONE);
    }

    private void getFromSession() {
        progressbar.setVisibility(View.VISIBLE);
        // Data From Session Manager
        SessionManager sessionManager = new SessionManager(UserProfile.this, "userLoginSession");
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        String _fullName = userDetails.get(SessionManager.KEY_FULLNAME);
        String _userName = userDetails.get(SessionManager.KEY_USERNAME);
        String _email = userDetails.get(SessionManager.KEY_EMAIL);
        String _phoneNo = userDetails.get(SessionManager.KEY_PHONENUMBER);
        _password = userDetails.get(SessionManager.KEY_SESSIONPASSWORD);
        String _gender = userDetails.get(SessionManager.KEY_GENDER);
        _dateOfBirth = userDetails.get(SessionManager.KEY_DATE);
        SetfName.setText(_fullName);
        SetuName.setText(_userName);
        Setemail.setText(_email);
        phoneNO.setText(_phoneNo);
        userNameField.setText(_userName);
        fullNameField.setText(_fullName);
        if (_gender.matches("Male")) {
            male.setChecked(true);
        }
        if (_gender.matches("Female")) {
            female.setChecked(true);
        }
        if (_gender.matches("Other")) {
            other.setChecked(true);
        }
        LoadImage();
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

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
        builder.setMessage("Please Connet to the Internet for Updation!")
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

    private Boolean validateName() {
        String val = regName.getEditText().getText().toString();
        if (val.isEmpty()) {
            regName.setError("Field can't be empty");
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
        } else if (val.length() >= 15) {
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

    private Boolean validateEmail() {
        String val = regEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            regEmail.setError("Field can't be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            regEmail.setError("Invalid email address");
            return false;
        } else {
            regName.setError(null);
            regEmail.setErrorEnabled(false);
            return true;
        }
    }
}