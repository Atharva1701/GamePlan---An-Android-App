package com.example.gameplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("User");

        // Initialize EditText and Button
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);

        // Set OnClickListener for the login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve text from EditText fields
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();


                // Validate password length
                if (password.length() < 8) {
                    editTextPassword.setError("Password must be at least 8 characters long");
                    return;
                }

              usersRef.orderByKey().equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {
                      if(snapshot.exists()){
                          for (DataSnapshot userSnapshot : snapshot.getChildren()){
                              User user = userSnapshot.getValue(User.class);
                              assert user != null;
                              //This is for sending the username to the addbooking class
                              //We could send userId instead, more to look on this
                              SharedPreferences sharedPreferences = getSharedPreferences("GamePlanPrefs", MODE_PRIVATE);
                              SharedPreferences.Editor editor = sharedPreferences.edit();
                              editor.putString("Username", username); // Saving the username
                              editor.apply();
                              if(user.getPassword().equals(password) && user.getType().equals("User")){
                                  Intent intent = new Intent(Login.this , MainActivity.class);
                                  startActivity(intent);
                                  finish();
                                  return;
                              }
                              if(user.getPassword().equals(password)&& user.getType().equals("Admin")){
                                  Intent intent = new Intent(Login.this,Admin_Dashboard.class);
                                  startActivity((intent));
                                  finish();
                                  return;

                              }
                              //Needed to add code for Booking new Slot
                              //This is to send the userId to the AddBooking class, since we are not using FirebaseAuth
                              if (user.getPassword().equals(password)) {
                                  Intent intent = new Intent(Login.this, AddBooking.class);
                                  startActivity(intent);
                                  finish();
                                  return;
                              }
                          }

                      }
                      Toast.makeText(Login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                      Toast.makeText(Login.this, "Login failed. Please try again", Toast.LENGTH_SHORT).show();

                  }
              });
            }
        });

        // Set OnClickListener for the "Forgot Password" TextView
        TextView textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ForgotPassword activity
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                startActivity(intent);
                intent.putExtra("username",editTextUsername.getText().toString());
                finish();
            }
        });

        TextView textViewRegister = findViewById((R.id.textViewRegister));
        textViewRegister.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        }));


    }
}
