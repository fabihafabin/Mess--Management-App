package com.example.messmanagement;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class OfflineNotesActivity extends AppCompatActivity implements OfflineNotesRecyclerInterface{
Button addNoteBtn,saveNoteBtn;
    Dialog dialog;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    EditText offlineNotesET;
    MyDBHelper dbHelper;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<Map<String,String>> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_notes);
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        list=new ArrayList<>();
        initialize();
        setListeners();
        loadNotes();
    }
    public void initialize(){
        recyclerView=findViewById(R.id.notesRecyclerView);
        addNoteBtn=findViewById(R.id.addNoteBtn);
        dbHelper=new MyDBHelper(this);
        firebaseAuth=FirebaseAuth.getInstance();
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.note_add_layout);
        offlineNotesET=dialog.findViewById(R.id.offlineNotesET);
        saveNoteBtn=dialog.findViewById(R.id.saveNoteBtn);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

//        Button create=dialog.findViewById(R.id.createMess);
    }
    public void setListeners(){
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        saveNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteText=offlineNotesET.getText().toString();
                if(noteText.equals("")){
                    offlineNotesET.setError("You must add a note");
                }
                else{
                    Date date=new Date();
                    dbHelper.addNote(firebaseAuth.getCurrentUser().getEmail(),noteText,date);
                    showToast("Note Added");
                    dialog.cancel();
                    loadNotes();
                    offlineNotesET.setText("");


                }
            }
        });
    }
    public void loadNotes(){
        list=dbHelper.fetchNotes(sharedPreferences.getString(Constants.userEmail,null));
        OffilineNoteRecyclerAdapter adapter=new OffilineNoteRecyclerAdapter(getApplicationContext(),list,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        recyclerView.setAdapter(adapter);

    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {

        dbHelper.deleteNoteById(Integer.parseInt(list.get(position).get("id")));
        loadNotes();
    }

    @Override
    public void onItemEdit(int position) {
        Dialog dialog=new Dialog(this);
        dialog.setContentView(R.layout.note_add_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        EditText dialogET=dialog.findViewById(R.id.offlineNotesET);
        Button dialogSaveBtn=dialog.findViewById(R.id.saveNoteBtn);
        dialogET.setText(list.get(position).get("text"));


        dialogSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.updateNoteById(Integer.parseInt(list.get(position).get("id")),list.get(position).get("email"),list.get(position).get("text"),list.get(position).get("date"));
                dialog.cancel();
            }
        });
        dialog.show();
    }

}