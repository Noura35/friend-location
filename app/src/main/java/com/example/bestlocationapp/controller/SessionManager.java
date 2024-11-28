package com.example.bestlocationapp.controller;


//bech yera9eb el user fel app

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.bestlocationapp.model.User;
import com.example.bestlocationapp.view.LoginActivity;


public class SessionManager {

    private static final String SHARED_PREF_NAME="userToken";
    private static final String KEY_NAME="name";
    private static final String KEY_EMAIL="email";
    private static final String KEY_TOKEN="token";
    private static final String KEY_ID="userId";

    private static SessionManager mInstance;
    private static Context Cntxt;

    public SessionManager( Context Cntxt) {
        this.Cntxt=Cntxt;
    }


    public static synchronized SessionManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }


    public void Userlogin(User user){

        //save les info de user connecté
        SharedPreferences sharedPreferences=Cntxt.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(KEY_ID,user.getId());
        editor.putString(KEY_NAME,user.getName());
        editor.putString(KEY_EMAIL,user.getEmail());
        editor.putString(KEY_TOKEN,user.getToken());
        editor.apply();

    }


    public boolean isLoginIn(){
        SharedPreferences sharedPreferences=Cntxt.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN,null) != null;

    }


    public User getToken(){
        SharedPreferences sharedPreferences=Cntxt.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return new User(sharedPreferences.getString(KEY_TOKEN,null));
    }

    public void UserloginOut(){

        //delete les info de user connecté
        SharedPreferences sharedPreferences=Cntxt.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Cntxt.startActivity(new Intent(Cntxt, LoginActivity.class));

    }

    public User getUser(User user){

        //save les info de user connecté
        SharedPreferences sharedPreferences=Cntxt.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return new User(sharedPreferences.getInt(KEY_ID,-1),
                sharedPreferences.getString(KEY_NAME,null),
                sharedPreferences.getString(KEY_EMAIL,null),
                sharedPreferences.getString(KEY_TOKEN,null)
                );

    }



}
