package com.example.messmanagement;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BazarListActivity extends AppCompatActivity implements RecyclerInterface{
CardView bazarListCard;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String description,id;
    double amount;
    TextView monthTextView,totalAmountTV;
    EditText bazarItemET,bazarAmount;
    Button addBazarBtn;
    ArrayList<Map<String,String>> bazarlist;
    RecyclerView bazarListRecycler;
    Dialog holder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bazar_list);
        sharedPreferences=this.getSharedPreferences(Constants.sharedPrefernces,MODE_PRIVATE);
        bazarListRecycler=findViewById(R.id.bazarListRecycler);
        editor=sharedPreferences.edit();
        initialize();
        setListeners();
        loadMonthDetails();
        new LoadBazarList().execute();
    }

    private void setListeners() {
        addBazarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description=bazarItemET.getText().toString();

                if(description.equals("")){
                    bazarItemET.setError("You must enter something");
                }
                else if(bazarAmount.getText().toString().equals("")){
                    bazarAmount.setError("You must enter something");
                }
                else{

                    amount=Double.parseDouble(bazarAmount.getText().toString());
                    new AddBazar().execute();
                }


            }
        });
    }

    private void loadMonthDetails() {
        new LoadMonthDetails().execute();
    }

    public void initialize(){
        totalAmountTV=findViewById(R.id.totalAmountTV);
        addBazarBtn=findViewById(R.id.addBazarBtn);
        bazarItemET=findViewById(R.id.bazarItemET);
        bazarAmount=findViewById(R.id.bazarAmount);
        bazarListCard=findViewById(R.id.bazarListCard);
        monthTextView=findViewById(R.id.monthTextView);
    }



    class LoadMonthDetails extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"getcurrentMonth",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String name=response.getString("month");
                                String monthid=response.getString("monthid");
                                editor.putString(Constants.currentMonthId,monthid);
                                editor.apply();
                                monthTextView.setText("Bazar History of "+name);
                            } catch (Exception e) {
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

    class AddBazar extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            params.put("month",sharedPreferences.getString(Constants.currentMonthId,null));
            params.put("userid",sharedPreferences.getString(Constants.userId,null));
            params.put("amount",amount);
            params.put("description",description);

            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"addbazar",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                bazarAmount.setText("");
                                bazarItemET.setText("");
                                new LoadBazarList().execute();


                            } catch (Exception e) {
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

    class LoadBazarList extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {

            Map<String,Object> params=new HashMap<>();
            params.put("messid",sharedPreferences.getString(Constants.userMessId,null));
            params.put("month",sharedPreferences.getString(Constants.currentMonthId,null));
            params.put("userid",sharedPreferences.getString(Constants.userId,null));

            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"getbazar",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                bazarlist=new ArrayList<>();
                                double total=0;
                                JSONArray bazarArray=response.getJSONArray("bazar");

                                for (int i=0; i<bazarArray.length(); i++){
                                    JSONObject obj=bazarArray.getJSONObject(i);


                                    Map<String,String> mp=new HashMap<>();
                                    mp.put("items",obj.getString("description"));
                                    total+=obj.getDouble("amount");
                                    mp.put("amount",String.valueOf(obj.getDouble("amount")));
                                    mp.put("id",String.valueOf(obj.getString("_id")));
                                    mp.put("date",obj.getString("createdAt").split("T")[0]);
                                    bazarlist.add(mp);

                                }
                                totalAmountTV.setText("Total amount:  "+total+" ৳");

                                callAdapter();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("That didn't work!last");

                }
            });
            queue.add(jsObjRequest);
            return null;
        }
    }

    private void callAdapter() {
        BazarListAdapter adapter=new BazarListAdapter(getApplicationContext(),bazarlist,this);
        bazarListRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        bazarListRecycler.setAdapter(adapter);
    }


    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {
        id=bazarlist.get(position).get("id");

        EditText itemsET,amountET;
        Button updateBtn,deleteBtn;
        Dialog dialog;
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.updatebazaritem);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        itemsET=dialog.findViewById(R.id.updateBazarItemET);
        amountET=dialog.findViewById(R.id.updateBazarAmount);

        itemsET.setText(bazarlist.get(position).get("items"));
        amountET.setText(bazarlist.get(position).get("amount"));

        updateBtn=dialog.findViewById(R.id.updateBazarBtn);
        deleteBtn=dialog.findViewById(R.id.deleteBazarBtn);

        holder=dialog;
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description=itemsET.getText().toString();
                amount=Double.parseDouble(amountET.getText().toString());

                if(description.equals("")){
                    itemsET.setError("You must enter something");
                }
                else if(amountET.getText().toString().equals("")){
                    amountET.setError("You must enter something");
                }
                else{
                    id=bazarlist.get(position).get("id");
                    new UpdateBazar().execute();
                }

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id=bazarlist.get(position).get("id");
                new DeleteBazar().execute();
            }
        });

        dialog.show();
    }






    class UpdateBazar extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("id",id);
            params.put("amount",amount);
            params.put("description",description);

            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"updatebazar",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("status")==200){
                                    showToast("Updated Successfully");
                                }
                                else{
                                    showToast("Error occured");
                                }
                                new BazarListActivity.LoadBazarList().execute();
                                holder.cancel();

                            } catch (Exception e) {
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

    class DeleteBazar extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Map<String,Object> params=new HashMap<>();
            params.put("id",id);

            final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsObjRequest = new
                    JsonObjectRequest(Request.Method.POST,
                    Constants.LOCAL_SERVER_URL+"deletebazar",
                    new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getInt("status")==200){
                                    showToast("Deleted Successfully");
                                }
                                else{
                                    showToast("Error Occured");
                                }
                                new BazarListActivity.LoadBazarList().execute();
                                holder.cancel();

                            } catch (Exception e) {
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

