package com.example.messmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MemberJoinActivity extends AppCompatActivity {
Button memberJoinLogoutBtn,memberJoinBtn;
EditText memberJoinEditText;
ProgressBar memberJoinProgressbar;
String messCode;
FirebaseAuth firebaseAuth;
FirebaseFirestore firebaseFirestore;

String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_join);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        email=firebaseAuth.getCurrentUser().getEmail();
        initialize();
        setListeners();
    }

    public void initialize(){
        memberJoinLogoutBtn=findViewById(R.id.memberJoinLogoutBtn);
        memberJoinEditText=findViewById(R.id.memberJoinEditText);
        memberJoinBtn=findViewById(R.id.memberJoinBtn);
        memberJoinProgressbar=findViewById(R.id.memberJoinProgressbar);
    }
    public void showProgress(boolean bool){
        if(bool){
            memberJoinProgressbar.setVisibility(View.VISIBLE);
            memberJoinBtn.setVisibility(View.INVISIBLE);
            memberJoinLogoutBtn.setVisibility(View.INVISIBLE);
        }
        else{
            memberJoinProgressbar.setVisibility(View.INVISIBLE);
            memberJoinBtn.setVisibility(View.VISIBLE);
            memberJoinLogoutBtn.setVisibility(View.VISIBLE);
        }
    }
    public void setListeners(){
        memberJoinLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        memberJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messCode=memberJoinEditText.getText().toString();
                if(messCode.equals("")){
                    memberJoinEditText.setError("can't be empty");
                }
                else{
                    showProgress(true);
                    new CheckMessCode().execute();
                }
            }
        });
    }
    public  void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    class CheckMessCode extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("mess_id",messCode);
            params.put("email",email);
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"joinmess",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                showProgress(false);
                                int status=response.getInt("status");
                                if(status==200){
                                    String messid=response.getString("messid");
                                    showToast("found this: "+messid);
                                    addMessIdToFirebase(messid);
                                }
                                else if(status==500){
                                    showToast("id is not valid");
                                }
                                else if(status==404){
                                    showToast("not match");
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!");
                    showProgress(false);
                }
            });
            queue.add(jsObjRequest);
            return null;
        }
    }
    public void addMessIdToFirebase(String messid){
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.userMessId,messid);
        editor.apply();
        Map<String,Object> mp=new HashMap<>();
        mp.put("messid",messid);
        DocumentReference documentReference=firebaseFirestore.collection("users")
                .document(email);
        documentReference.set(mp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                    finish();
                }
            }
        });
    }
}