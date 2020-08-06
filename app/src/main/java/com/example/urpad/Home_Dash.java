package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.example.urpad.HomeAdapter.FeaturedAdapter;
import com.example.urpad.HomeAdapter.FeaturedHelperClass;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;

public class Home_Dash extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FirebaseAuth.AuthStateListener,RecentNoteRecyclerAdapter.NoteListener {

    static final float END_SCALE = 0.7f;
    RecyclerView featured_recycler, recent_created_note;
    private RecentNoteRecyclerAdapter madapter;
    RecyclerView.Adapter adapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    RatingBar rateUs;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home__dash);
        Log.d("check", "Home start");
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            startLoginActivity();
        }
        //hooks
        featured_recycler = findViewById(R.id.featured_recycler);
        recent_created_note = findViewById(R.id.recent_created_recycler);
        recent_created_note.setHasFixedSize(true);

        //ad view hook
        MobileAds.initialize(this,getResources().getString(R.string.appID)); //app id
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // recent_created_note.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);


        //functions
        navigationDrawer();
        featured_recycler();
        recentNotes();
    }

    private void navigationDrawer() {
        Log.d("check", "navg call");
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            case R.id.nav_search:
                search();
                break;
            case R.id.nav_profile:
               userProfile();
                break;
            case R.id.nav_logout:
                LogOut();
                break;
            case R.id.nav_exit:
                onDestroy();
                this.finish();
                break;
            case R.id.nav_rate_us:
                showDialogRating();
                navigationView.setCheckedItem(R.id.nav_home);
                break;
            default:
                navigationView.setCheckedItem(R.id.nav_home);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(R.id.nav_home);
        return true;
    }

    private void userProfile() {
        Intent i = new Intent(Home_Dash.this, UserProfile.class);
        startActivity(i);
        finish();
    }

    private void showDialogRating() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Rate Our App");
        builder.setMessage("Give FeedBack");
        View itemView = LayoutInflater.from(this).inflate(R.layout.rating, null);
        RatingBar rating_bar = itemView.findViewById(R.id.ratting_bar);
        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recentNotes() {
        Log.d("check", "recent note call");
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        Query query = FirebaseFirestore.getInstance()
                .collection("notes")
                .whereEqualTo("userid", userID)
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(10);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        Log.d("check", "Home->options" + options.toString());
        recent_created_note.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        madapter = new RecentNoteRecyclerAdapter(options,this);
        recent_created_note.setAdapter(madapter);
        Log.d("check", "Home->madapter" + recent_created_note.getChildCount());
        madapter.startListening();
    }

    private void featured_recycler() {
        Log.d("check", "featured call");
        featured_recycler.setHasFixedSize(true);
        featured_recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();
        featuredLocations.add(new FeaturedHelperClass(R.drawable.book_card, "Note Book", getResources().getString(R.string.noteBook_details)));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.ocr_card, "OCR", getResources().getString(R.string.ocr_details)));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.about_card, "About", getResources().getString(R.string.about_details)));
        adapter = new FeaturedAdapter(featuredLocations);
        featured_recycler.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            Log.d("test", "on start" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        Log.d("check", "Home->on start -> start listen");
        madapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("check", "Home->on Pause -> stop listen");
        madapter.stopListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("check", "Home->on stop -> stop listen");
        madapter.stopListening();
        finish();

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        recentNotes();
    }

    public void startLoginActivity() {
        Intent i = new Intent(Home_Dash.this, StartUp.class);
        startActivity(i);
        finish();
    }
    public void LogOut() {
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startLoginActivity();
                        }
                    }
                });
    }

    @Override
    public void handleEditNote(DocumentSnapshot snapshot) {
        Note note = snapshot.toObject(Note.class);
        note.setNoteID(snapshot.getId());
        Intent i = new Intent(Home_Dash.this, AddNote.class);
        i.putExtra("Edit_note", note);
        startActivity(i);
        finish();
    }
    public void searchView(View view){
        search();
    }
    public void search(){
        Intent i = new Intent(Home_Dash.this, Search.class);
        startActivity(i);
        finish();
    }
    public void  callNotesActivity(View view){
        Intent i = new Intent(Home_Dash.this, DisplayNotes.class);
        startActivity(i);
        finish();
    }
    public void  callProfileActivity(View view){
        Intent i = new Intent(Home_Dash.this, UserProfile.class);
        startActivity(i);
        finish();
    }
    public void  callSettingActivity(View view){
        Intent i = new Intent(Home_Dash.this, AccountSetting.class);
        startActivity(i);
        finish();
    }
}