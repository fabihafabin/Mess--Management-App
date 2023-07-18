package com.example.messmanagement;

import java.util.ArrayList;
import java.util.Random;

public class Constants {
    public static final String KEY_ID="";
    public static final String KEY_URL="https://messmanagement038.herokuapp.com/";
    public static final String sharedPrefernces ="myPref";
    public static final String userEmail ="email";
    public static final String userRole ="role";
    public static final String userId ="userId";
    public static final String userMessId ="messId";
    public static final String currentMonthId ="monthId";
    public static final String currentMonthName ="monthName";
    public static final String userName ="name";
    public static final String userPhone ="phone";
    public static final String userMessName ="messname";
//    public static final String LOCAL_SERVER_URL="http://192.168.43.37:3000/";
    public static final String LOCAL_SERVER_URL="https://messmanagement038.herokuapp.com/";
//    public static final String REMOTE_SERVER_URL="";

    public static ArrayList<Integer> cc=new ArrayList<>();

    public static Integer getColors() {
        Random rand = new Random();
        int random_integer = rand.nextInt(6-0) + 0;
        cc.add(R.color.darkPrimary);
        cc.add(R.color.colorAccent1);
        cc.add(R.color.colorBottomCardStart);
        cc.add(R.color.colorBottomCardEnd);
        cc.add(R.color.primaryTextColor);
        cc.add(R.color.colorAccent1);
        return cc.get(random_integer);
    }
}
