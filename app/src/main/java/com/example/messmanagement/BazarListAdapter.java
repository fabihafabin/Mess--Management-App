package com.example.messmanagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class BazarListAdapter extends RecyclerView.Adapter<BazarListAdapter.MyViewHolder2> {
    Context context;
    ArrayList<Map<String,String>> list;
    RecyclerInterface recyclerInterface;

    public BazarListAdapter(Context context, ArrayList<Map<String, String>> list,RecyclerInterface recyclerInterface) {
        this.context = context;
        this.list = list;
        this.recyclerInterface=recyclerInterface;
    }

    public BazarListAdapter(Context context, ArrayList<Map<String, String>> list) {
        this.context = context;
        this.list = list;
        this.recyclerInterface=null;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.bazarlistitem,parent,false);
        return new BazarListAdapter.MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        holder.itemsTV.setText(list.get(position).get("items"));
        holder.dateTV.setText(list.get(position).get("date"));
        holder.amountTV.setText(list.get(position).get("amount"));
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder{
TextView itemsTV,dateTV,amountTV;
        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);
            itemsTV=itemView.findViewById(R.id.bazarListItemsTV);
            dateTV=itemView.findViewById(R.id.bazarListDateTV);
            amountTV=itemView.findViewById(R.id.bazarListAmountTV);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(recyclerInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerInterface.onItemClick(pos);
                        }
                    }
                    return true;
                }
            });

        }
    }
}
