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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateMessActivity extends AppCompatActivity {
EditText createMessNameET,createMessMonth;
Button createMessBtn;
String messName,monthTitle;
FirebaseAuth firebaseAuth;
FirebaseUser firebaseUser;
FirebaseFirestore firebaseFirestore;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mess);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        initialize();
        setListeners();
    }
    public void initialize(){
        progressBar=findViewById(R.id.createMessProgressBar);
        createMessNameET=findViewById(R.id.createMessNameET);
        createMessMonth=findViewById(R.id.createMessMonth);
        createMessBtn=findViewById(R.id.createMessBtn);
    }
    public void setListeners(){
        createMessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressbar(true);
                messName=createMessNameET.getText().toString();
                monthTitle=createMessMonth.getText().toString();

                if(messName.equals("")){
                    createMessNameET.setError("must enter a name");
                    showProgressbar(false);
                }
                if(monthTitle.equals("")){
                    createMessMonth.setError("must enter month name");
                    showProgressbar(false);
                }
                if(!messName.equals("") && !monthTitle.equals("")){
                    //addDataToFireBase();
                    new AddToOwnServer().execute();
                }
            }
        });
    }
    public void showProgressbar(boolean bool){
        if(bool){
            progressBar.setVisibility(View.VISIBLE);
            createMessBtn.setVisibility(View.INVISIBLE);
        }
        else{
            createMessBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
public void showToast(String message){
    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
}
    class AddToOwnServer extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            Map<String,String> params=new HashMap<>();
            params.put("messName",messName);
            params.put("month",monthTitle);
            params.put("email",firebaseUser.getEmail());
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"addmess",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                showProgressbar(false);
                                String messid=response.getString("messid");
                                String monthid=response.getString("monthid");
                                addMessIdToFireBase(messid,monthid);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!");
                    showProgressbar(false);
                }
            });
            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);
            return null;
        }
    }
    public void addMessIdToFireBase(String messid,String monthid){
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constants.userMessId,messid);
        editor.apply();
        Map<String,Object> mp=new HashMap<>();
        mp.put("messid",messid);
        mp.put("monthid",monthid);
        DocumentReference documentReference=firebaseFirestore.collection("users")
                .document(firebaseUser.getEmail());
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