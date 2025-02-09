package com.example.snapped;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Cart extends AppCompatActivity {

    private TextView items;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        items = findViewById(R.id.items);
        button = findViewById(R.id.button);

        items.setText(getIntent().getStringExtra("ITEMS"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cart.this, ThankYouActivity.class);
                startActivity(intent);
            }
        });
    }
}