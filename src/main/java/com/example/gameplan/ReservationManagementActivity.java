package com.example.gameplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ReservationManagementActivity extends AppCompatActivity {

    private RecyclerView bookingsRecyclerView;
    private TextView noBookingsTextView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private DatabaseReference bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_management);

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);
        noBookingsTextView = findViewById(R.id.noBookingsTextView);

        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");

        // Initialize RecyclerView
        bookingList = new ArrayList<>();
        // Ensure you are using this constructor in ReservationManagementActivity
        bookingAdapter = new BookingAdapter(bookingList, bookingsRef, this);

        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Fetch user bookings from Firebase
        fetchUserBookings();
    }

    private void fetchUserBookings() {
        SharedPreferences sharedPreferences = getSharedPreferences("GamePlanPrefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("Username", null);

        if (userId != null) {
            Log.d("CurrentUser", "Username: " + userId);
            Query userBooking = bookingsRef.orderByChild("userId").equalTo(userId);
            userBooking.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Booking booking = snapshot.getValue(Booking.class);
                            bookingList.add(booking);
                        }
                        bookingAdapter.notifyDataSetChanged();
                        bookingsRecyclerView.setVisibility(View.VISIBLE);
                        noBookingsTextView.setVisibility(View.GONE);
                    } else {
                        bookingsRecyclerView.setVisibility(View.GONE);
                        noBookingsTextView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ReservationManagementActivity.this, "Failed to fetch bookings: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("ReservationManagement", "No username found in SharedPreferences.");
            noBookingsTextView.setVisibility(View.VISIBLE);
            noBookingsTextView.setText("You need to log in to view your bookings.");
            // Optionally, redirect to the login activity
        }
    }

    // Navigate to the homepage
    public void navigateToHomePage(View view) {
        Intent intent = new Intent(this, MainActivity.class); // Replace HomePageActivity.class with your actual homepage activity class name
        startActivity(intent);
        finish();
    }
}