package com.example.urpad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class NotesRecyclerAdapter extends FirestoreRecyclerAdapter<Note,NotesRecyclerAdapter.NoteViewHolder> {
    NoteListener noteListener;

    public NotesRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Note> options,NoteListener noteListener) {
        super(options);
        this.noteListener=noteListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.nTitle.setText(note.getTitle());
        holder.nDate.setText("Date: "+note.getDate());
        holder.nTime.setText("Time: "+note.getTime());
        holder.ncheckBox.setChecked(note.getisCompleted());

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View v= layoutInflater.inflate(R.layout.display_notes_card,parent,false);
        return new NoteViewHolder(v);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView nTitle,nDate,nTime;
        CheckBox ncheckBox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            nTitle=itemView.findViewById(R.id.RN_Title);
            nDate=itemView.findViewById(R.id.RN_Date);
            nTime=itemView.findViewById(R.id.RN_Time);
            ncheckBox=itemView.findViewById(R.id.ncheck);

            ncheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());

                    Note note=getItem(getAdapterPosition());
                    if(note.getisCompleted()!=isChecked)
                        noteListener.handleCheckedChanged(isChecked,snapshot);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentSnapshot snapshot=getSnapshots().getSnapshot(getAdapterPosition());
                    noteListener.handleEditNote(snapshot);
                }
            });
        }
        public void deleteItem(){
            noteListener.handleDeleteNote(getSnapshots().getSnapshot(getAdapterPosition()));
        }

    }
    interface NoteListener{
        public void handleCheckedChanged(boolean isCheck, DocumentSnapshot snapshot);
        public void handleEditNote(DocumentSnapshot snapshot);
        public void handleDeleteNote(DocumentSnapshot snapshot);
    }
}
