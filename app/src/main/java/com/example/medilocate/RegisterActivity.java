package com.example.medilocate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etUsername, etPassword;
    private Button btnRegister;
    private RequestQueue requestQueue;
    private String registerUrl = "http://192.168.1.105/ICT602_2/register.php"; // Replace with your register.php URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Set click listener for the Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        // Get input values from EditText fields
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        // Create a StringRequest to send data to the server
        StringRequest stringRequest = new StringRequest(Request.Method.POST, registerUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from the server
                        String message;
                        if (response.equals("success")) {
                            // Registration successful
                            message = "User " + username + " has been successfully created";
                        } else {
                            // Registration failed
                            message = "Registration failed";
                        }
                        // Show AlertDialog
                        showAlertDialog(message);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors that occur during the request
                        Toast.makeText(RegisterActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("RegisterActivity", "Volley error: " + error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Create a Map to hold the parameters to send to the server
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Add the StringRequest to the RequestQueue
        requestQueue.add(stringRequest);
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    // Navigate to LoginActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close the current activity
                })
                .setCancelable(false)
                .show();
    }
}
