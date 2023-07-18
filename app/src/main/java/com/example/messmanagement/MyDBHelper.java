package com.example.messmanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME="offline_note";
    private static final int DB_VERSION=1;
    private static final String TABLE_NAME="notes";
    private static final String KEY_ID="id",KEY_EMAIL="email";
    private static final String KEY_TEXT="text",KEY_DATE="date";

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+"("
                +KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_EMAIL+" TEXT,"+KEY_TEXT+" TEXT,"
                +KEY_DATE+" DATE"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addNote(String email, String text, Date date){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_EMAIL,email);
        values.put(KEY_TEXT,text);
        values.put(KEY_DATE,date.toString());

        db.insert(TABLE_NAME,null,values);
    }

    public ArrayList<Map<String,String>> fetchNotes(String email){
        SQLiteDatabase db=this.getReadableDatabase();

        Cursor cursor=db.rawQuery("SELECT * FROM "+TABLE_NAME,null);

        ArrayList<Map<String,String>> arrayList=new ArrayList<>();
        while (cursor.moveToNext()){
            if(cursor!=null){
                HashMap<String,String> mp=new HashMap<>();
                mp.put("id",cursor.getString(0));
                mp.put("email",cursor.getString(1));
                mp.put("text",cursor.getString(2));
                mp.put("date",cursor.getString(3));
                arrayList.add(mp);
            }

        }
        return  arrayList;
    }

    public void updateNoteById(int id,String email, String text, String date){
        SQLiteDatabase db=getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(KEY_ID,id);
        Log.d("idddddd",id+"");
        values.put(KEY_EMAIL,email);
        values.put(KEY_TEXT,text);
        values.put(KEY_DATE,date);
        db.update(TABLE_NAME,values,KEY_ID+" = ?",new String[]{String.valueOf(id)});

    }

    public boolean deleteNoteById(int id){
        SQLiteDatabase db=getWritableDatabase();
        db.delete(TABLE_NAME,KEY_ID+" = ?",new String[]{String.valueOf(id)});
        return true;
    }
}
