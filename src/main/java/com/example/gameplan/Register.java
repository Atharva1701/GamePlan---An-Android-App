package com.example.gameplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    String firstname, lastname, email, phone, password, cnf_password,username;
    FirebaseDatabase db;
    DatabaseReference reference;

    // Remove the initialization here
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextFirstname;
    private EditText editTextLastname;
    private EditText editTextPhone;

    private EditText editTextUsername;

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Initialize EditText fields after setContentView
        editTextEmail = findViewById(R.id.Email);
        editTextPassword = findViewById(R.id.Password);
        editTextConfirmPassword = findViewById(R.id.ConfirmPassword);
        editTextFirstname = findViewById(R.id.FirstName);
        editTextLastname = findViewById(R.id.LastName);
        editTextPhone = findViewById(R.id.Phone);
        editTextUsername = findViewById(R.id.UserName);


        Button submit = findViewById(R.id.buttonRegister);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstname = editTextFirstname.getText().toString();
                lastname = editTextLastname.getText().toString();
                email = editTextEmail.getText().toString();
                phone = editTextPhone.getText().toString();
                password = editTextPassword.getText().toString();
                cnf_password = editTextConfirmPassword.getText().toString();
                username = editTextUsername.getText().toString();

                if (!isValidEmail(email)) {
                    editTextEmail.setError("Enter a valid email address");
                    return;
                }

                if (password.length() < 8) {
                    editTextPassword.setError("Password must be at least 8 characters long");
                    return;
                }

                if (!password.equals(cnf_password)){

                    editTextConfirmPassword.setError("Passwords Don't Match");
                    return;
                }
                else{
                    User user = new User(firstname,lastname,email,phone,password,username);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference("User");
                    reference.child(username).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editTextFirstname.setText("");
                            editTextLastname.setText("");
                            editTextPhone.setText("");
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                            editTextConfirmPassword.setText("");
                            editTextUsername.setText("");
                            Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Register.this , Login.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

    }
}