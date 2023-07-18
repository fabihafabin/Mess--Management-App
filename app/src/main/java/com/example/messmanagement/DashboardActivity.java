package com.example.messmanagement;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
//CardView signoutCard;
FirebaseAuth firebaseAuth;
FirebaseUser firebaseUser;
FirebaseFirestore firebaseFirestore;
CardView addMemberCard,offlineNotesCard,profileCard,signoutCard,membersCardDash,playGameCard;
CardView bazarDatesCard,bazarListCard,settingsCard,monthHistoryCard;
CardView addMealCard;

SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;
    LinearLayout managerLayout1;
//dialog
EditText messNameET,dialogAddMemberET;
Button dialogAddMemberBtn;
    Dialog dialog,dialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dialog1=new Dialog(getApplicationContext());
        dialog1.setContentView(R.layout.bazar_date_layout);
        dialog1.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog1.setCancelable(true);


        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        editor=sharedPreferences.edit();
        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=firebaseAuth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.add_member_dialog_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        initialize();
        initializeLayouts();
        setListeners();
        if(checkIfLoadNeeded()){
            setUserData();
        }

        hideRoleBaseDash();


//        signoutCard=findViewById(R.id.signoutCard);
//

//        Button create=dialog.findViewById(R.id.createMess);
//

//
//        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,reward);
//        spinner.setAdapter(adapter);
//
//        signoutCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.signOut();
//                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
//                finish();
//            }
//        });
    }
    public void initialize(){
        addMealCard=findViewById(R.id.addMealCard);
        monthHistoryCard=findViewById(R.id.monthHistoryCard);
        settingsCard=findViewById(R.id.settingsCard);
        bazarListCard=findViewById(R.id.bazarListCard);
        bazarDatesCard=findViewById(R.id.bazarDatesCard);
        playGameCard=findViewById(R.id.playGameCard);
        membersCardDash=findViewById(R.id.membersCardDash);
        dialogAddMemberBtn=dialog.findViewById(R.id.dialogAddMemberBtn);
        dialogAddMemberET=dialog.findViewById(R.id.dialogAddMemberET);
        addMemberCard=findViewById(R.id.addMemberCard);
        offlineNotesCard=findViewById(R.id.offlineNotesCard);
        profileCard=findViewById(R.id.profileCard);
        signoutCard=findViewById(R.id.signoutCard);
    }
    public void initializeLayouts(){
        managerLayout1=findViewById(R.id.managerLayout1);
    }
    public void hideRoleBaseDash(){
        String role=sharedPreferences.getString(Constants.userRole,null);
        if(role.equals("member")){
            managerLayout1.setVisibility(View.GONE);
        }
        else if(role.equals("manager")){

        }
        else{
            showToast("Sorry, Error Occured, please resignin");
        }
    }
    public boolean checkIfLoadNeeded(){
        if(sharedPreferences.getString(String.valueOf(Constants.userId),null)==null || sharedPreferences.getString(String.valueOf(Constants.userEmail),null)==null || sharedPreferences.getString(String.valueOf(Constants.userMessId),null)==null){
            return true;
        }
        else{
            return false;
        }
    }
    public void setUserData(){
        new LoadUserData().execute();
    }
    public void setListeners(){
        addMealCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddMealActivity.class));
            }
        });

        monthHistoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MonthDetailsActivity.class));
            }
        });
        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
            }
        });
        bazarListCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),BazarListActivity.class));
            }
        });
        bazarDatesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),BazarDateActivity.class));
            }
        });
        playGameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),GameChooseActivity.class));
            }
        });
        dialogAddMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memberEmail=dialogAddMemberET.getText().toString();

                if(memberEmail.equals("")){
                    dialogAddMemberET.setError("must enter a mail");
                }
                else{
                    DocumentReference documentReference=firebaseFirestore.collection("users")
                            .document(memberEmail);
                    documentReference.get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot value = task.getResult();
                                                String messid=value.getString("messid");
//                                                showToast(messid);
                                                if(messid!=null){
                                                    showToast("User is already a member of other Mess");
                                                }
                                                else{
                                                    addMember(memberEmail);
                                                }
                                            }
                                        }
                                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showToast(e.getMessage());
                                }
                            });

                }
            }
        });
        addMemberCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
        membersCardDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MembersListActivity.class));
            }
        });
        offlineNotesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(give(),OfflineNotesActivity.class));
            }
        });
        signoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                editor.clear();
                editor.apply();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });
        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(give(),ProfileActivity.class));
            }
        });

    }



    public  void addMember(String memberEmail){
        Map<String,Object> params=new HashMap<>();
        params.put("memberemail",memberEmail);
        params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsObjRequest = new
                JsonObjectRequest(Request.Method.POST,
                Constants.LOCAL_SERVER_URL+"addmember",
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status=response.getInt("status");
                            if(status==200){
                                Map<String,Object> mp=new HashMap<>();
                                mp.put("messid",sharedPreferences.getString(Constants.userMessId,null));
                                DocumentReference documentReference=firebaseFirestore.collection("users")
                                        .document(memberEmail);
                                documentReference.set(mp, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dialogAddMemberET.setText("");
                                            dialog.cancel();
                                            showToast("Member added successfully");
                                        }
                                    }
                                });
                            }
                            else if(status==500){
                                showToast("User doesn't exist");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        showToast(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast("That didn't work!");

            }
        });
        queue.add(jsObjRequest);
    }
    public Context give(){
        return getApplicationContext();
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    class LoadUserData extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("email",firebaseAuth.getCurrentUser().getEmail());
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"getuser",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String userId=response.getString("_id");
                                String name=response.getString("name");
                                String email=response.getString("email");
                                String role=response.getString("role");
                                String phone=response.getString("phone");

                                showToast("newly loaded");
                                JSONObject messObj=response.getJSONObject("mess");
                                editor.putString(Constants.userEmail,email);
                                editor.putString(Constants.userId,userId);
                                editor.putString(Constants.userRole,role);
                                editor.putString(Constants.userName,name);
                                editor.putString(Constants.userPhone,phone);
                                editor.putString(Constants.userMessId,messObj.getString("_id"));
                                editor.putString(Constants.userMessName,messObj.getString("messName"));
                                editor.putString(Constants.currentMonthId,messObj.getString("month"));
                                editor.putString(Constants.currentMonthName,messObj.getString("month"));

                                editor.apply();
//                                showToast("Data loaded");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!");

                }
            });
            queue.add(jsObjRequest);
            return null;
        }
    }
}