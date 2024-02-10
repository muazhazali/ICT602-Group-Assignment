package com.example.medilocate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    RequestQueue requestQueue;
    String loginUrl = "http://192.168.1.105/ICT602_2/login.php"; // Replace with your login.php URL
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                response -> {
                    // Print the response string for debugging
                    Log.d("LoginActivity", "Response string: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            // Login successful, store username in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.apply();

                            // Display the retrieved username in logcat
                            Log.d("LoginActivity", "Retrieved username: " + username);

                            // Navigate to MapsActivity
                            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Login failed, display error message
                            Toast.makeText(LoginActivity.this, "Login failed. Invalid username or password.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Volley error occurred
                    Toast.makeText(LoginActivity.this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }

    private void saveUsername(String username) {
        // Save username in SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("username", username);
        editor.apply();
    }


}
