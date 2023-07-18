package com.example.messmanagement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class CustomMemberAdapter extends ArrayAdapter<Map<String,Object>> {

    private final Context context;
    private final ArrayList<Map<String,Object>> values;
    private final ArrayList<String> images;
FirebaseFirestore firebaseFirestore;

    public CustomMemberAdapter(Context context, ArrayList<Map<String, Object>> values, ArrayList<String> images) {
        super(context, -1, values);
        firebaseFirestore=FirebaseFirestore.getInstance();
        this.context = context;
        this.values = values;
        this.images = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.layout_member_row, parent, false);

        TextView memberName = rowView.findViewById(R.id.memberNameTv);
        TextView memberPhone = rowView.findViewById(R.id.memberPhoneTv);
        ImageView memberProfileImageView=rowView.findViewById(R.id.memberProfileImageView);

        //TextView eventType = rowView.findViewById(R.id.tvEventType);

        Map<String,Object> e = values.get(position);
        memberName.setText(e.get("name").toString());
        memberPhone.setText(e.get("phone").toString());
        firebaseFirestore.collection("images")
                .document(e.get("email").toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String ss=document.getString("image");
                                byte[] decodedString = Base64.decode(ss, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                 memberProfileImageView.setImageBitmap(decodedByte);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        return rowView;
    }
}
