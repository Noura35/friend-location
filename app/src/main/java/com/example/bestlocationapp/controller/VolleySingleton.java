package com.example.bestlocationapp.controller;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
    private static VolleySingleton instance;
    private RequestQueue requestQueue;
    private static Context context;


    public VolleySingleton(Context context) {
        this.context = context;
        requestQueue= getRequestQueue();

    }

    public static synchronized VolleySingleton getInstance(Context context){
        if (instance == null){
            instance = new VolleySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {

        if(requestQueue == null){
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());

        }

        return requestQueue;
    }


    //reuest eli fe tel lel api
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }


}
