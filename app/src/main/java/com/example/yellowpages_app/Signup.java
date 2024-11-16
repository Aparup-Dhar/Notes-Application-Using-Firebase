package com.example.yellowpages_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        TextView signin_redirect = findViewById(R.id.signin_redirect);

        ImageButton submit_button = findViewById(R.id.submit_button);

        submit_button.setOnClickListener(v -> {

            EditText email_edittext = findViewById(R.id.email);
            EditText password_edittext = findViewById(R.id.password);
            EditText confirm_password_edittext = findViewById(R.id.confirm_password);

            String email = email_edittext.getText().toString();
            String password = password_edittext.getText().toString();
            String confirm_password =  confirm_password_edittext.getText().toString();

            if(!email.isEmpty() && !password.isEmpty() && !confirm_password.isEmpty()){
                if (password.equals(confirm_password)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        FancyToast.makeText(Signup.this, "Successfully signed up!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                                        Intent intent = new Intent(Signup.this, Signin.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign up fails
                                        FancyToast.makeText(Signup.this, "Authentication failed!", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                                    }
                                }
                            });
                }
                else {
                    FancyToast.makeText(this, "Passwords do not match", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                }
            }
            else {
                FancyToast.makeText(this, "All fields are required", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
            }
        });



        signin_redirect.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Signin.class);
            startActivity(intent);
            finish();
        });

    }
}

