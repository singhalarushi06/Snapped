package com.example.snapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {
    Button continueButton;
    EditText storeCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        continueButton = findViewById(R.id.continue_to_checkout_button);
        storeCode = findViewById(R.id.store_code);

        continueButton.setOnClickListener(v -> {
            String code = storeCode.getText().toString();
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            intent.putExtra("STORE_CODE", code);
            startActivity(intent);
        });

        super.onCreate(savedInstanceState);
    }
}