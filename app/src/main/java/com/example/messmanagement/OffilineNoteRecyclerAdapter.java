package com.example.messmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class OffilineNoteRecyclerAdapter extends RecyclerView.Adapter<OffilineNoteRecyclerAdapter.MyViewHolder>{
    Context context;
    ArrayList<Map<String,String>> list;
    MyDBHelper dbHelper;
    OfflineNotesRecyclerInterface offlineNotesRecyclerInterface;
    public OffilineNoteRecyclerAdapter(Context context, ArrayList<Map<String, String>> list,OfflineNotesRecyclerInterface offlineNotesRecyclerInterface) {
        this.context = context;
        this.list = list;
        this.offlineNotesRecyclerInterface=offlineNotesRecyclerInterface;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.single_note_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.noteTv.setText(list.get(position).get("text"));
        holder.dateTv.setText(list.get(position).get("date"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView noteTv,dateTv;
        ImageView noteEditIcon,noteDeleteIcon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTv=itemView.findViewById(R.id.noteTextView);
            dateTv=itemView.findViewById(R.id.noteDate);
            noteEditIcon=itemView.findViewById(R.id.noteEditIcon);
            noteDeleteIcon=itemView.findViewById(R.id.noteDeleteIcon);
            noteTv.setBackgroundResource(Constants.getColors());
            noteDeleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(offlineNotesRecyclerInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            offlineNotesRecyclerInterface.onItemClick(pos);
                        }
                    }
                }
            });
            noteEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(offlineNotesRecyclerInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            offlineNotesRecyclerInterface.onItemEdit(pos);
                        }
                    }
                }
            });

        }
    }
}
