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

public class BazarUserRecyclerAdapter extends RecyclerView.Adapter<BazarUserRecyclerAdapter.MyViewHolder3> {
    Context context;
    ArrayList<Map<String,String>> list;
    RecyclerInterface recyclerInterface;
    public BazarUserRecyclerAdapter(Context context, ArrayList<Map<String, String>> list,RecyclerInterface recyclerInterface) {
        this.context = context;
        this.list = list;
        this.recyclerInterface=recyclerInterface;
    }

    @NonNull
    @Override
    public MyViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.bazar_user_recycle_listitem,parent,false);
        return new BazarUserRecyclerAdapter.MyViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder3 holder, int position) {
        holder.bazarUserNameTV.setText(list.get(position).get("name"));
        holder.bazarUserAmountTV.setText(list.get(position).get("amount")+" ৳");
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    class MyViewHolder3 extends RecyclerView.ViewHolder{
        TextView bazarUserNameTV,bazarUserAmountTV;


        public MyViewHolder3(@NonNull View itemView) {
            super(itemView);
            bazarUserNameTV=itemView.findViewById(R.id.bazarUserNameTV);
            bazarUserAmountTV=itemView.findViewById(R.id.bazarUserAmountTV);

            itemView.setBackgroundResource(Constants.getColors());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerInterface!=null){
                        int pos=getAdapterPosition();
                        if(pos!=RecyclerView.NO_POSITION){
                            recyclerInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
