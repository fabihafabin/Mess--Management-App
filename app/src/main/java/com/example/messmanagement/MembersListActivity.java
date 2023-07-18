package com.example.messmanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MembersListActivity extends AppCompatActivity {
ArrayList<Map<String,Object>> list;
ArrayList<String> imagesString;
SharedPreferences sharedPreferences;
FirebaseFirestore firebaseFirestore;
ListView memberListView;
SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        firebaseFirestore=FirebaseFirestore.getInstance();
        editor=sharedPreferences.edit();
        memberListView=findViewById(R.id.memberListView);
        list=new ArrayList<>();
        loadMembers();
    }
    public void loadMembers(){
        imagesString=new ArrayList<>();
        Map<String,Object> params=new HashMap<>();
        params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsObjRequest = new
                JsonObjectRequest(Request.Method.POST,
                Constants.LOCAL_SERVER_URL+"getmembers",
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status=response.getInt("status");
                            if(status==200){
                                Log.d("aaaaaa",response.toString());
                                JSONArray membersArr=response.getJSONArray("membersArray");
                                for (int i=0; i<membersArr.length(); i++){
                                    Map<String,Object> member=new HashMap<>();
                                    JSONObject obj=membersArr.getJSONObject(i);
                                    member.put("name",obj.getString("name"));
                                    member.put("email",obj.getString("email"));
                                    member.put("phone",obj.getString("phone"));
                                    member.put("role",obj.getString("role"));
                                    list.add(member);
                                }
                                CustomMemberAdapter adapter=new CustomMemberAdapter(getApplicationContext(), list, imagesString);
                                memberListView.setAdapter(adapter);


                            }
                            else if(status==500){
                                showToast("User doesn't exist");
                            }
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
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
}