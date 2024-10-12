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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Set_New_Password extends AppCompatActivity {

    private EditText editTextNewPassword ;
    private EditText editTextConfirmPassword;

    private String newPassword , confrimPassword;

    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_new_password);
        editTextNewPassword = findViewById(R.id.NewPassword);
        editTextConfirmPassword = findViewById(R.id.ConfirmPassword);
        String email = getIntent().getStringExtra("email");

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("User");



        Button button = findViewById(R.id.SubmitPassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPassword = editTextNewPassword.getText().toString();
                confrimPassword = editTextConfirmPassword.getText().toString();

                if(newPassword.equals(confrimPassword)){
                    usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for (DataSnapshot userSnapShot:snapshot.getChildren()){
                                    User user = userSnapShot.getValue(User.class);
                                    assert email != null;
                                    assert user != null;
                                    if(email.equals(user.getEmail())){
                                        usersRef.child(Objects.requireNonNull(userSnapShot.getKey())).child("password").setValue(newPassword);
                                        Toast.makeText(Set_New_Password.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Set_New_Password.this,Login.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

    }
}