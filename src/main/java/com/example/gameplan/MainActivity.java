package com.example.gameplan;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private GestureDetector gestureDetector;
    private String sportName;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSportButton(R.id.buttonBadminton, "Badminton");
        setupSportButton(R.id.buttonSoccer, "Soccer");
        setupSportButton(R.id.buttonPickleBall, "Pickle Ball");
        setupSportButton(R.id.buttonTennis, "Tennis");
        setupSportButton(R.id.buttonSquash, "Squash");

//        viewMyBookings(R.id.viewMyBookingsButton);

        ImageView logout = findViewById(R.id.logout);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        TextView viewAllSportsTextView = findViewById(R.id.view_all_sports);

        // Set an OnClickListener on the TextView
        viewAllSportsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the ViewAllSportsActivity
                Intent intent = new Intent(MainActivity.this, ViewAllSportsActivity.class);
                startActivity(intent);
            }
        });

//        Button buttonBadminton = findViewById(R.id.buttonBadminton);
//        buttonBadminton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity_kt.this,AddBooking.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        Button viewMyBookingsButton = findViewById(R.id.viewMyBookingsButton);
        viewMyBookingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReservationManagementActivity.class);
            startActivity(intent);
        });

        viewFlipper = findViewById(R.id.viewFlipper2);
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.startFlipping();

        viewFlipper.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) {
                    viewFlipper.showPrevious();
                } else {
                    viewFlipper.showNext();
                }
                return true;
            }
            return false;
        }
    }

    private void setupSportButton(int buttonId, String sportName) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("GamePlanPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Sportname", sportName);
            editor.apply();
            Intent intent = new Intent(MainActivity.this, AddBooking.class);
            startActivity(intent);
        });
    }
//    public void viewMyBookings(int bId) {
//        Button viewMyBookingsButton = findViewById(R.id.viewMyBookingsButton);
//        viewMyBookingsButton.setOnClickListener(v -> {
//            Intent intent = new Intent(MainActivity.this, ReservationManagementActivity.class);
//            startActivity(intent);
//        });
//
//    }

}
