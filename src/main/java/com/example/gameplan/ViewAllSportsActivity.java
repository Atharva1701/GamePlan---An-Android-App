package com.example.gameplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ViewAllSportsActivity extends AppCompatActivity {

    // Define an array of sports names
    private String[] sportsNames = {"Badminton", "Soccer", "Pickle Ball", "Tennis", "Squash"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_sports);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        LinearLayout sportsContainer = findViewById(R.id.sportsContainer);
        for (String sportName : sportsNames) {
            Button sportButton = new Button(this);
            sportButton.setText(sportName);
            sportButton.setBackgroundResource(R.drawable.button_shape);
            sportButton.setOnClickListener(v -> {
                Intent intent = new Intent(ViewAllSportsActivity.this, AddBooking.class);
                intent.putExtra("SPORT_NAME", sportName);
                startActivity(intent);
            });

            // Customize your button's look here
//            sportButton.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark)); // For example, an orange color
//            sportButton.setTextColor(getResources().getColor(android.R.color.white));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(5, 10, 5, 10); // 10px margins on top and bottom
            sportButton.setLayoutParams(layoutParams);

            sportsContainer.addView(sportButton);
        }
    }

    // Method to handle the back button click
    private void onBackClicked() {
        finish();
    }
}
