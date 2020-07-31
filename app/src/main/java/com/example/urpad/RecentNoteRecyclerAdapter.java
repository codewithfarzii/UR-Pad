package com.example.urpad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentNoteRecyclerAdapter extends FirestoreRecyclerAdapter<Note, RecentNoteRecyclerAdapter.RecentNoteViewHolder> {
    NoteListener noteListener;

    public RecentNoteRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options,NoteListener noteListener) {
        super(options);
        this.noteListener=noteListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecentNoteViewHolder holder, int position, @NonNull Note note) {
        holder.nTitle.setText(note.getTitle());
        holder.nDate.setText(note.getDate());
        holder.nTime.setText(note.getTime());
    }

    @NonNull
    @Override
    public RecentNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view= layoutInflater.inflate(R.layout.recently_created_card_design,parent,false);
        return new RecentNoteViewHolder(view);
    }

    public class RecentNoteViewHolder extends RecyclerView.ViewHolder{
        TextView nTitle,nDate,nTime;

        public RecentNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            nTitle=itemView.findViewById(R.id.RN_Title);
            nDate=itemView.findViewById(R.id.RN_Date);
            nTime=itemView.findViewById(R.id.RN_Time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());
                    noteListener.handleEditNote(snapshot);
                }
            });

        }
    }
    interface NoteListener{
        public void handleEditNote(DocumentSnapshot snapshot);
    }
}