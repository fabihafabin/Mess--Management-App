package com.example.messmanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMealActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    ArrayList<Map<String,Object>> list;
    ArrayList<String> nameList;
    ArrayList<String> userIdList;
    Spinner mealSpinner;
    Button addMealBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);

        mealSpinner=findViewById(R.id.mealSpinner);
        userIdList=new ArrayList<>();
        nameList=new ArrayList<>();
        addMealBtn=findViewById(R.id.addMealBtn);
        list=new ArrayList<>();

        loadMembers();
    }
    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    public void loadMembers(){
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
                                JSONArray membersArr=response.getJSONArray("membersArray");
                                for (int i=0; i<membersArr.length(); i++){
                                    JSONObject obj=membersArr.getJSONObject(i);
                                    nameList.add(obj.getString("name"));
                                    userIdList.add(obj.getString("_id"));
//                                    list.add(member);
                                }
                                showToast(list.size()+"");
//                                CustomMemberAdapter adapter=new CustomMemberAdapter(getApplicationContext(), list);
                                ArrayAdapter adapter=new ArrayAdapter(getApplicationContext(),  android.R.layout.simple_spinner_item,nameList);
                                mealSpinner.setAdapter(adapter);
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
}