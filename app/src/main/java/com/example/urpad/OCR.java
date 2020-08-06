package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.List;

public class OCR extends AppCompatActivity {

    Toolbar toolbar;
    private Button captureImage, detectText;
    private ImageView imageView;
    private TextView textView;
    Bitmap imageBitmap;
    private TextRecognizer detector;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int IMAGE_PICK_CODE=1000;
    private static final int PERMISSION_CODE=1001;
    boolean gallery=false;
    String lines = "";
    String blocks = "";
    String words = "";
    Boolean check = false, pic_taken = false;
    Uri ImageUri;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_o_c_r);

        //add tool bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Text Recogination OCR");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ad view hook
        MobileAds.initialize(this,"ca-app-pub-1494531846382800~5982462648"); //app id
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Log.d("check", "ocr activity start");
        captureImage = (Button) findViewById(R.id.capture);
        detectText = (Button) findViewById(R.id.dedect);
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.result);
        detector = new TextRecognizer.Builder(getApplicationContext()).build();
        Log.d("check", "ocr--> hooks donne");
        // Add popup menu
        final PopupMenu popupMenu = new PopupMenu(
                this,
                captureImage
        );
        popupMenu.getMenuInflater().inflate(R.menu.select_image_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_takeImage) {
                    dispatchTakePictureIntent();
                }
                if (id == R.id.nav_gallerImage) {
                    getPermission();
                }

                return false;
            }
        });

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pic_taken=false;
                check=false;
                popupMenu.show();
            }
        });
        detectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check) {
                    Intent i = new Intent(OCR.this, AddNote.class);
                    i.putExtra("lines", lines);
                    startActivity(i);
                    finish();
                } else {
                    if (pic_taken)
                        textView.setText(R.string.NoText);
                    else
                        textView.setText(R.string.NoImage);
                }
            }
        });

    }

    protected void onStart() {
        super.onStart();
        Log.d("check", "OCR->on start -> start listen");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("check", "OCR->on Pause -> stop listen");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("check", "OCR->on stop -> stop listen");
        // finish();

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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE ) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            detectTextFromImage();
        }
        if(resultCode==RESULT_OK && requestCode==IMAGE_PICK_CODE){
            ImageUri=data.getData();
            try{
                imageBitmap = (Bitmap) MediaStore.Images.Media.getBitmap(getContentResolver(),ImageUri);
                imageView.setImageBitmap(imageBitmap);
                detectTextFromImage();
            }catch (IOException e){
                Log.d("check","Error->",e.getCause());
            }
        }
    }

    private void detectTextFromImage() {

        Log.d("check", "Detect called");
        Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<TextBlock> textBlocks = detector.detect(frame);
        for (int index = 0; index < textBlocks.size(); index++) {
            //extract scanned text blocks here
            TextBlock tBlock = textBlocks.valueAt(index);
            blocks = blocks + tBlock.getValue() + "\n" + "\n";
            for (Text line : tBlock.getComponents()) {
                //extract scanned text lines here
                lines = lines + line.getValue() + "\n";
                for (Text element : line.getComponents()) {
                    //extract scanned text words here
                    words = words + element.getValue() + ", ";
                }
            }
        }
        // lines=builder.toString();
        if (textBlocks.size() == 0) {
            textView.setText(R.string.NoText);
            pic_taken = true;
        } else {
            textView.setText(R.string.TextFound);
            check = true;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, Home_Dash.class);
        startActivity(i);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        gotoMain();
        return super.onOptionsItemSelected(item);
    }

    public void gotoMain() {
        Intent i = new Intent(this, Home_Dash.class);
        startActivity(i);
        finish();
    }

}
 /*   FirebaseVisionImage firebaseVisionImage= FirebaseVisionImage.fromBitmap(imageBitmap);
    FirebaseVisionTextDetector firebaseVisionTextDetector= FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
        @Override
        public void onSuccess(FirebaseVisionText firebaseVisionText) {
            displaText(firebaseVisionText);
        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e)
        {
            Toast.makeText(MainActivity.this,"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d("Error:",e.getMessage());
        }
    });



    private void displaText(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList=firebaseVisionText.getBlocks();
        if(blockList.size()==0)
            Toast.makeText(MainActivity.this,"No Text Found in Image",Toast.LENGTH_SHORT).show();
        else{
            for(FirebaseVisionText.Block block:firebaseVisionText.getBlocks()){
                String text=block.getText();
                textView.setText(text);
            }

        }*/






