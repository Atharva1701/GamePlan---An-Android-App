package com.example.gameplan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {

    private EditText editTextEmail;
    private static final String TAG = "ForgotPasswordActivity";

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("User");

        String username = getIntent().getStringExtra("username");
        editTextEmail = findViewById(R.id.ConfirmEmail);
        Button buttonOtp = findViewById(R.id.buttonOtp);




        buttonOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                if (!isValidEmail(email)) {
                    editTextEmail.setError("Enter a valid email address");
                    return;
                }
                List<String> emailList = new ArrayList<>();
                usersRef.orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                assert user != null;
                                String useremail = user.getEmail();

                                emailList.add(useremail);
                            }
                                if (emailList.contains(email)) {
                                    // Generate random 6-digit OTP
                                    String otp = generateOTP();

                                    // Send OTP via email
                                    sendOTP(email, otp);

                                    // Notify user
                                    Toast.makeText(ForgotPassword.this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ForgotPassword.this, Password_Reset_OTP.class);
                                    intent.putExtra("OTP", otp);
                                    intent.putExtra("email", email);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                   Toast.makeText(ForgotPassword.this, "Email-ID not Registered", Toast.LENGTH_SHORT).show();
                                }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Toast.makeText(ForgotPassword.this, "Email-ID not Registered", Toast.LENGTH_SHORT).show();
                    }
                }
                );
            }

        });
    }

    // Method to check if email is valid
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to generate random 6-digit OTP
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate a random 6-digit number
        return String.valueOf(otp);
    }

    @SuppressLint("StaticFieldLeak")
    private void sendOTP(final String email, final String otp) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final String username = "gameplansportsbusiness@gmail.com"; // Your email address
                final String password = "qely sxvv wnly vkgh"; // Your email password

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(email));
                    message.setSubject("Your OTP for password reset");
                    message.setText("Dear User,"
                            + "\n\nYour OTP for password reset is: " + otp);

                    // Send the email
                    Transport.send(message);

                    // Log success message
                    Log.d(TAG, "Email sent successfully to: " + email);
                } catch (MessagingException e) {
                    // Log and display error message
                    Log.e(TAG, "Failed to send email", e);
                    Toast.makeText(ForgotPassword.this, "Failed to send OTP. Please try again later.", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        }.execute();
    }


}
