package com.example.medilocate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnLoginPage;
    Button btnRegister;
    Button btnAboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.myToolbar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        btnLoginPage = findViewById(R.id.btnLoginPage);
        btnRegister = findViewById(R.id.btnRegister);
        btnAboutUs = findViewById(R.id.btnAboutUs);

        btnLoginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to RegisterActivity
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to aboutUs
                Intent intent = new Intent(MainActivity.this, aboutUs.class);
                startActivity(intent);
            }
        });


    }
}
