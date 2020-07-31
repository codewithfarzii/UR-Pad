package com.example.urpad.HomeAdapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.urpad.DisplayNotes;
import com.example.urpad.OCR;
import com.example.urpad.R;

import java.util.ArrayList;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder>
{
    ArrayList<FeaturedHelperClass> featuredLocations;

    public FeaturedAdapter(ArrayList<FeaturedHelperClass> featuredLocations) {
        this.featuredLocations = featuredLocations;
    }
    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.featured_card_design, parent, false);
        FeaturedViewHolder featuredViewHolder = new FeaturedViewHolder(view);
        return featuredViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {

        FeaturedHelperClass featuredHelperClass = featuredLocations.get(position);

        holder.image.setImageResource(featuredHelperClass.getImage());
        holder.title.setText(featuredHelperClass.getTitle());
        holder.desc.setText(featuredHelperClass.getDescription());

    }

    @Override
    public int getItemCount() {
        return featuredLocations.size();
    }

    public static class FeaturedViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, desc;

        public FeaturedViewHolder(@NonNull final View itemView) {
            super(itemView);
            //Hooks
            image = itemView.findViewById(R.id.featured_image);
            title = itemView.findViewById(R.id.featured_title);
            desc = itemView.findViewById(R.id.featured_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(getAdapterPosition()==0) {
                        Toast.makeText(v.getContext(), "note", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(v.getContext(),DisplayNotes.class);
                        v.getContext().startActivity(i);
                    }
                    if(getAdapterPosition()==1) {
                        Toast.makeText(v.getContext(), "ocr", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(v.getContext(), OCR.class);
                        v.getContext().startActivity(i);
                    }
                    if(getAdapterPosition()==2)
                        Toast.makeText(v.getContext(),"about",Toast.LENGTH_SHORT).show();

                  //  i.putExtra("ID",String.valueOf(mExample_items.get(getAdapterPosition()).getID()));
                  //  v.getContext().startActivity(i);
                }
            });
        }
    }
}
