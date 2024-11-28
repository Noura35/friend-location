package com.example.bestlocationapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bestlocationapp.R;
import com.example.bestlocationapp.Utils.Url;
import com.example.bestlocationapp.controller.SessionManager;
import com.example.bestlocationapp.controller.VolleySingleton;
import com.example.bestlocationapp.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText edpassword;
    private EditText edemail;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView btnRegister;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        edemail=findViewById(R.id.edemail_login);
        edpassword=findViewById(R.id.edpassword_login);
        btnLogin=findViewById(R.id.btnRegister);
        progressBar=findViewById(R.id.progressbarlogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               userLogin();


            }
        });





        btnRegister=findViewById(R.id.btn_login_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void userLogin(){
        final String email = edemail.getText().toString().trim();
        final String password = edpassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            edemail.setError("enter your email please !");
            edemail.requestFocus();
            return;
        }



        if(TextUtils.isEmpty(password)){
            edpassword.setError("enter your password please !");
            edpassword.requestFocus();
            return;
        }




        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                Url.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.VISIBLE);

                try {

                    JSONObject obj = new JSONObject(response);
                    //response mt3 json yrja3 success =true
                    if (obj.getBoolean("success")) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        String userObj = obj.getString("data");
                        User user = new User(userObj);
                        SessionManager.getInstance(getApplicationContext()).Userlogin(user);
                        finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }



        })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>() ;
                params.put("Content-Type","application/json");
                params.put("email",email);
                params.put("password",password);
                params.put("c_password",password);

                return params;
            }
        }

                ;

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);


    }

}