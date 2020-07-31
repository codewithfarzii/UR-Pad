package com.example.urpad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Search extends AppCompatActivity implements NotesRecyclerAdapter.NoteListener {
    final String TAG = "check->SEARCH-->";
    private RecyclerView recyclerView;
    FirestoreRecyclerAdapter notesRecyclerAdapter;
    RelativeLayout progressbar;
    SearchView searchView;
    String serachtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "Search activity start");

        //Hooks
        progressbar = findViewById(R.id.login_progressBar);
        searchView =findViewById(R.id.searchNote);

        //RecycleView Setup
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        Log.d(TAG, "hooks done");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  progressbar.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                progressbar.setVisibility(View.VISIBLE);
                Log.d(TAG,newText.toLowerCase());
                serachtext=newText.toLowerCase();
                initRecyclerView(newText.toLowerCase());
                return false;
            }
        });
    }

       @Override
       protected void onStart() {

           super.onStart();
           Log.d(TAG, "->on start -> start listen");
          // notesRecyclerAdapter.startListening();

       }

       @Override
       protected void onStop() {
           super.onStop();
           Log.d(TAG, "->on stop -> stop listen");
           notesRecyclerAdapter.stopListening();
       }

       @Override
       protected void onPause() {

           super.onPause();
           Log.d(TAG, "on Pause -> stop listen");
           notesRecyclerAdapter.stopListening();

       }

       private void initRecyclerView(String search) {
           Log.d("check", "recy Called");
           SessionManager sessionManager = new SessionManager(Search.this, "userLoginSession");
           HashMap<String, String> userDetails = sessionManager.getUserDetailFromSession();
           String _phoneNo = userDetails.get(SessionManager.KEY_PHONENUMBER);
           Log.d("check", "np->" + _phoneNo);
           Query query = FirebaseFirestore.getInstance()
                   .collection("notes")
                   .whereEqualTo("userid", _phoneNo)
                  .whereEqualTo("search",search)
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
                       .addBackgroundColor(ContextCompat.getColor(Search.this, R.color.colorAccent))
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
                               Toast.makeText(Search.this, "Updated", Toast.LENGTH_SHORT).show();

                           } else {
                               Toast.makeText(Search.this, "Failed to attempt Update! Try Again Later", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
       }

       @Override
       public void handleEditNote(DocumentSnapshot snapshot) {
           Note note = snapshot.toObject(Note.class);
           note.setNoteID(snapshot.getId());
           Log.d(TAG, note.getNoteID());
           Intent i = new Intent(Search.this, AddNote.class);
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
                            initRecyclerView(serachtext);
                           progressbar.setVisibility(View.GONE);
                           Snackbar.make(recyclerView,"Note Deleted",Snackbar.LENGTH_LONG)
                                   .setAction("Undo", new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           progressbar.setVisibility(View.VISIBLE);
                                           documentReference.set(note);
                                           initRecyclerView(serachtext);
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
}