package com.example.gameplan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Password_Reset_OTP extends AppCompatActivity {

    private EditText entered_otp;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset_otp);

        email = getIntent().getStringExtra("email");

        entered_otp = findViewById(R.id.EnterOtp);
        String otp = getIntent().getStringExtra("OTP");
        Button submit_otp = findViewById(R.id.SubmitOtp);

        submit_otp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String OTP = entered_otp.getText().toString();
                assert otp != null;
                if(otp.equals(OTP)){
                    Intent intent = new Intent(Password_Reset_OTP.this, Set_New_Password.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Password_Reset_OTP.this, "OTP entered is incorrect ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
