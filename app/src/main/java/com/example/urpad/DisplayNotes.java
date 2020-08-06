package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DisplayNotes extends AppCompatActivity implements FirebaseAuth.AuthStateListener, NotesRecyclerAdapter.NoteListener {

   final String TAG="check";
    Toolbar toolbar;
    private RecyclerView recyclerView;
    FirestoreRecyclerAdapter notesRecyclerAdapter;
    RelativeLayout progressbar;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_notes);
        progressbar = findViewById(R.id.login_progressBar);
        progressbar.setVisibility(View.VISIBLE);

        //Adding tool bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ad view hook
        MobileAds.initialize(this,"ca-app-pub-1494531846382800~5982462648"); //app id
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // intersitial ad
        prepareAD();

        //RecycleView Setup
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        initRecyclerView();
        progressbar.setVisibility(View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_menu, menu);    //Register your menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Log.d("check", "add clock");
            // Toast.makeText(this, "add clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(DisplayNotes.this, AddNote.class);
            i.putExtra("from", "new");
            startActivity(i);
            finish();
        }else{
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mInterstitialAd.setAdListener(new AdListener(){
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        gotMain();
                    }
                });
            }else
                gotMain();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {

        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(this);
        Log.d("check", "Display->on start -> start listen");
        notesRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
        Log.d("check", "Display->on stop -> stop listen");
        notesRecyclerAdapter.stopListening();
    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.d("check", "Display->on Pause -> stop listen");
        notesRecyclerAdapter.stopListening();

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               Log.d("auth", "state change");
        initRecyclerView() ;
    }

    private void initRecyclerView() {
        progressbar.setVisibility(View.VISIBLE);
        Log.d("check", "recy Called");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        Log.d("check", "np->" + userID);
        Query query = FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userid", userID)
                .orderBy("isCompleted", Query.Direction.ASCENDING)
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notesRecyclerAdapter = new NotesRecyclerAdapter(options, this);
        recyclerView.setAdapter(notesRecyclerAdapter);
        notesRecyclerAdapter.startListening();
        ItemTouchHelper itemTouchHelper =new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        progressbar.setVisibility(View.GONE);
    }
    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if(direction==ItemTouchHelper.LEFT){
                progressbar.setVisibility(View.VISIBLE);
                NotesRecyclerAdapter.NoteViewHolder noteViewHolder=(NotesRecyclerAdapter.NoteViewHolder) viewHolder;
                noteViewHolder.deleteItem();
            }
        }
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(DisplayNotes.this, R.color.colorAccent))
                    .addActionIcon(R.drawable.ic_delete)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    @Override
    public void handleCheckedChanged(boolean isCheck, DocumentSnapshot snapshot) {
        snapshot.getReference().update("isCompleted", isCheck)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            Toast.makeText(DisplayNotes.this, "Updated", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(DisplayNotes.this, "Failed to attempt Update! Try Again Later", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void handleEditNote(DocumentSnapshot snapshot) {
        Note note = snapshot.toObject(Note.class);
        note.setNoteID(snapshot.getId());
        Log.d(TAG, note.getNoteID());
        Intent i = new Intent(DisplayNotes.this, AddNote.class);
        i.putExtra("Edit_note", note);
        startActivity(i);
        finish();
    }
    @Override
    public void handleDeleteNote(DocumentSnapshot snapshot) {

       final DocumentReference documentReference=snapshot.getReference();
       final Note note=snapshot.toObject(Note.class);

       documentReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"Note-->Deleted");
                        notesRecyclerAdapter.startListening();
                       // initRecyclerView();
                        progressbar.setVisibility(View.GONE);
                        Snackbar.make(recyclerView,"Note Deleted",Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        progressbar.setVisibility(View.VISIBLE);
                                        documentReference.set(note);
                                      //  initRecyclerView();
                                        progressbar.setVisibility(View.GONE);
                                    }
                                }).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Note-->Not Deleted");
                    }
                });

    }
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener(){
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                   gotMain();
                }
            });
        } else {
            gotMain();
            super.onBackPressed();
        }
    }
    private void gotMain() {
        Intent i = new Intent(this, Home_Dash.class);
        startActivity(i);
        finish();
    }

    public  void prepareAD(){
        // interstitial ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1494531846382800/7826135153");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }
}