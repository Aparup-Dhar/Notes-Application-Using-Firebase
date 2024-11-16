package com.example.yellowpages_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class Signin extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        TextView signup_redirect = findViewById(R.id.signup_redirect);
        signup_redirect.setOnClickListener(v -> {
            Intent intent = new Intent(Signin.this, Signup.class);
            startActivity(intent);
            finish();
        });

        ImageButton submit_button = findViewById(R.id.submit_button);


        submit_button.setOnClickListener(v -> {

            EditText email_edittext = findViewById(R.id.email);
            EditText password_edittext = findViewById(R.id.password);

            String email = email_edittext.getText().toString();
            String password = password_edittext.getText().toString();

            if(!email.isEmpty() && !password.isEmpty()){
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    FancyToast.makeText(Signin.this, "Successfully signed in!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                    Intent intent = new Intent(Signin.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails
                                    FancyToast.makeText(Signin.this, "Authentication failed!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                                }
                            }
                        });
            }
            else {
                FancyToast.makeText(this, "All fields are required", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        });
    }

}