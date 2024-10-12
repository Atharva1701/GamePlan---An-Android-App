package com.example.gameplan;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddBooking extends AppCompatActivity {

    private DatabaseReference bookingRef;
    private Spinner courtSpinner;
    private Button dateButton, addButton;
    private int year, month, day;
    private RadioGroup timeSlotRadioGroup;
    private String sportName;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences("GamePlanPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("Username", null);
        String sportName = sharedPreferences.getString("Sportname",null);


        bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        courtSpinner = findViewById(R.id.courtSpinner);
        dateButton = findViewById(R.id.dateButton);
        addButton = findViewById(R.id.addButton);
        timeSlotRadioGroup = findViewById(R.id.timeSlotRadioGroup);

        populateCourtSpinner();
        dateButton.setOnClickListener(v -> showDatePickerDialog());

        addButton.setOnClickListener(v -> {
            Log.d("AddBooking", "Username: " + username);
            Log.d("AddBooking2", "Sportname: " + sportName);
            String courtId = courtSpinner.getSelectedItem().toString();
            String date = dateButton.getText().toString();
            int selectedId = timeSlotRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a time slot.", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selectedTimeSlotButton = findViewById(selectedId);
            String timeSlot = selectedTimeSlotButton.getText().toString();


            if (validateFields(courtId, date, username, timeSlot)) {
                addBookingToDatabase(sportName, username, courtId, date, timeSlot);
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });
//        @SuppressLint("WrongViewCast") Button backButton = findViewById(R.id.backButton);
//        backButton.setOnClickListener(v -> {
//            Intent intent = new Intent(AddBooking.this, MainActivity_kt.class); // Replace HomePageActivity.class with your actual homepage activity class name
//            startActivity(intent);
//            finish();
//        });
        setupRadioButtons();
    }
    private void setupRadioButtons() {
        timeSlotRadioGroup = findViewById(R.id.timeSlotRadioGroup);
        // Loop through all radio buttons in the group
        for (int i = 0; i < timeSlotRadioGroup.getChildCount(); i++) {
            final RadioButton button = (RadioButton) timeSlotRadioGroup.getChildAt(i);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If the button was already checked, clear the check
                    if (button.isChecked() && lastCheckedId == button.getId()) {
                        timeSlotRadioGroup.clearCheck();
                        lastCheckedId = -1;
                    } else {
                        // Otherwise, mark it as the last checked ID
                        lastCheckedId = button.getId();
                    }
                }
            });
        }

        // Set the initial state as no button checked
        timeSlotRadioGroup.clearCheck();
    }
        private int lastCheckedId = -1;
    // This variable will hold the ID of the last checked radio button

//    private void populateCourtSpinner() {
//        ArrayAdapter<CharSequence> courtAdapter = ArrayAdapter.createFromResource(this, R.array.courts_array, android.R.layout.simple_spinner_item);
//        courtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        courtSpinner.setAdapter(courtAdapter);
//    }

    private void populateCourtSpinner() {
        ArrayAdapter<CharSequence> courtAdapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.courts_array)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.BLACK);
                return view;
            }
        };

        courtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courtSpinner.setAdapter(courtAdapter);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) ->
                dateButton.setText(String.format("%02d/%02d/%04d", monthOfYear + 1, dayOfMonth, year1)), year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private boolean validateFields(String courtId, String date, String username, String timeSlot) {
        boolean isValid =  !courtId.isEmpty() && !date.isEmpty() && username != null && !username.isEmpty() && !timeSlot.isEmpty();
        return isValid;
    }

    private void addBookingToDatabase(String sportName, String username, String courtId, String date, String timeSlot) {
        String bookingId = bookingRef.push().getKey();
        Booking booking = new Booking(sportName,bookingId, username, courtId, date, timeSlot);

        bookingRef.child(bookingId).setValue(booking).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddBooking.this, "Booking confirmed", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddBooking.this, MainActivity.class); // Assuming MainActivity_kt is your homepage
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(AddBooking.this, "Failed to confirm booking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
