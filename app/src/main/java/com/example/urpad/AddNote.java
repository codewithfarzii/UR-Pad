package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.FirebaseAuthAnonymousUpgradeException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;

public class AddNote extends AppCompatActivity {
    Toolbar toolbar;
    EditText nTitle, nContent;
    Calendar c;
    String currentDate, currentTime;
    Spinner font_family;
    TextView font_size;
    Button minus, plus, left_aln, center_aln, right_aln, colorPicker, bgcolorPicker;
    int textDefaultColor;
    Note note;
    boolean letupdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_note);

        //ID's
        nTitle = findViewById(R.id.nTitle);
        nContent = findViewById(R.id.nContent);
        if (getIntent().hasExtra("Edit_note")) {
            note = getIntent().getParcelableExtra("Edit_note");
            nTitle.setText(note.getTitle());
            nContent.setText(note.getContent());
            letupdate = true;
        }
        if (getIntent().hasExtra("lines")) {
            Intent data = getIntent();
            nContent.setText(data.getStringExtra("lines"));
        }

        //Get Current Date & Time
        c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy\nEEEE");
        currentDate = dateFormat.format(c.getTime());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = timeFormat.format(c.getTime());

        //add tool bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Initializing
        font_family = (Spinner) findViewById(R.id.fontfamily);
        minus = findViewById(R.id.minus);
        plus = findViewById(R.id.plus);
        font_size = findViewById(R.id.font_size);
        left_aln = findViewById(R.id.leftalign);
        center_aln = findViewById(R.id.centeralign);
        right_aln = findViewById(R.id.rightalign);
        colorPicker = findViewById(R.id.colorpicker);
        bgcolorPicker = findViewById(R.id.bgcolorpicker);
        //Adapter
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.font_family, R.layout.spinner_home);
        adapter1.setDropDownViewResource(R.layout.spinner_dropdown);
        font_family.setAdapter(adapter1);
        //Styling
        font_family.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_font = font_family.getSelectedItem().toString();
                Typeface t = Typeface.create(selected_font, Typeface.NORMAL);
                nContent.setTypeface(t);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = Integer.parseInt(font_size.getText().toString());
                if (i > 1) {
                    i--;
                    font_size.setText(i.toString());
                    nContent.setTextSize(i);
                    plus.setEnabled(true);
                } else {
                    minus.setEnabled(false);
                }
            }

        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer i = Integer.parseInt(font_size.getText().toString());
                if (i < 99) {
                    i++;
                    font_size.setText(i.toString());
                    nContent.setTextSize(i);
                    minus.setEnabled(true);
                } else {
                    plus.setEnabled(false);
                }
            }
        });
        left_aln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nContent.setGravity(Gravity.LEFT);
            }
        });
        center_aln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nContent.setGravity(Gravity.CENTER);
            }
        });
        right_aln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nContent.setGravity(Gravity.RIGHT);
            }
        });

        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        bgcolorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bgopenColorPicker();
            }
        });
        //functions
        nTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    getSupportActionBar().setTitle(s);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void openColorPicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, textDefaultColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                SpannableString s = new SpannableString(nContent.getText().toString());
                ForegroundColorSpan fgc = new ForegroundColorSpan(color);
                s.setSpan(fgc, nContent.getSelectionStart(), nContent.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                nContent.setText(s);
            }
        });
        dialog.show();
    }

    private void bgopenColorPicker() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, textDefaultColor, false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                nContent.setBackgroundColor(color);
            }
        });
        dialog.show();
    }


    private String pad(int i) {
        if (i < 10)
            return "0" + i;
        else
            return String.valueOf(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);    //Register your save_menu
        return true;
    }

    private void gotMain() {
        Intent i = new Intent(this, DisplayNotes.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotMain();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            gotMain();
            Toast.makeText(this, "Cancelled! Not Saved!", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        if (item.getItemId() == R.id.save) {
            String str = nTitle.getText().toString();
            if (str.isEmpty()) {
                Toast.makeText(this, "Title is must!", Toast.LENGTH_SHORT).show();
            } else {
                if (letupdate) {
                    updateNote();
                } else {
                    addnNote();
                }
            }

        } else {
            gotMain();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateNote() {
        DocumentReference docref = FirebaseFirestore.getInstance()
                .collection("notes")
                .document(note.getNoteID());
        Log.d("check", "from update func->" + note.getNoteID());
        note.setTitle(nTitle.getText().toString());
        note.setSearch((nTitle.getText().toString()).toLowerCase());
        note.setContent(nContent.getText().toString());
        docref.set(note, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddNote.this, "Updated Successfully!", Toast.LENGTH_SHORT).show();
                        gotMain();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNote.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    private void addnNote() {
        SessionManager sessionManager = new SessionManager(AddNote.this, "userLoginSession");
        HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
        String _phoneNo = userDetails.get(SessionManager.KEY_PHONENUMBER);
        Log.d("check", "add func");
        String userID = _phoneNo;
        Log.d("check", userID);
        Note note = new Note(nTitle.getText().toString(), nContent.getText().toString(), false, currentDate, currentTime, userID, nTitle.getText().toString().toLowerCase());
        Log.d("check", "note");
        FirebaseFirestore.getInstance()
                .collection("notes")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddNote.this, "Note Saved Successfully", Toast.LENGTH_LONG).show();
                        gotMain();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNote.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

    }

}