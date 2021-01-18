package com.app.triponeer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    Button btnSignup;
    EditText edtTextSignupName, edtTextSignupEmail, edtTextSignupPassword, edtTextSignupConfirmPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        edtTextSignupName = findViewById(R.id.edtTextSignupName);
        edtTextSignupEmail = findViewById(R.id.edtTextSignupEmail);
        edtTextSignupPassword = findViewById(R.id.edtTextSignupPassword);
        edtTextSignupConfirmPassword = findViewById(R.id.edtTextSignupConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTextSignupName.getText().toString().isEmpty() &&
                        edtTextSignupEmail.getText().toString().isEmpty() &&
                        edtTextSignupPassword.getText().toString().isEmpty() &&
                        edtTextSignupConfirmPassword.getText().toString().isEmpty()
                ) {
                    edtTextSignupName.setError("Enter the Name");
                    edtTextSignupName.requestFocus();
                    edtTextSignupEmail.setError("Enter the required Email");
                    edtTextSignupEmail.requestFocus();
                    edtTextSignupPassword.setError("Enter the required password");
                    edtTextSignupPassword.requestFocus();
                    edtTextSignupConfirmPassword.setError("Enter the confirm password");
                    edtTextSignupConfirmPassword.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtTextSignupEmail.getText().toString()).matches()) {
                    edtTextSignupEmail.setError("please provide a vaild email");
                    edtTextSignupEmail.requestFocus();
                    return;
                } else if (edtTextSignupPassword.getText().toString().length() < 6) {
                    edtTextSignupPassword.setError("Min password length should be 6 characters!");
                    edtTextSignupPassword.requestFocus();
                    return;
                } else if (!(edtTextSignupPassword.getText().toString().equals(edtTextSignupConfirmPassword.getText().toString()))) {
                    edtTextSignupConfirmPassword.setError("comfirm password not equal password");
                    edtTextSignupConfirmPassword.requestFocus();
                    return;
                } else {
                    String email = edtTextSignupEmail.getText().toString();
                    String password = edtTextSignupPassword.getText().toString();
                    String name = edtTextSignupName.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                AccountData account = new AccountData(name, email, password);
                                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                                        .getCurrentUser().getUid()).setValue(account).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUp.this, "User has been registered successfully!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.VISIBLE);
                                        } else {
                                            Toast.makeText(SignUp.this, "Failed to register! Try again", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                                Intent intent = new Intent(SignUp.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(SignUp.this, "Failed to register! Try again", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }
}