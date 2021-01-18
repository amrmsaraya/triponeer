package com.app.triponeer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    private static final String TAG = "FB Auth";
    Button btnLogin, btnSignup;
    EditText edtTextLoginEmail, edtTextLoginPassword;
    TextView forgetPassword;
    private CallbackManager callbackManager;
    private static final String EMAIl = "email";
    LoginButton loginButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    AccountData account;
    SharedPreferences saving;
    SharedPreferences.Editor edit;
    public static final String saveData = "NewData";
    public static final String newLogin = "NewLogin";
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnlogin);
        btnSignup = findViewById(R.id.btnSignup);
        edtTextLoginEmail = findViewById(R.id.edtTextSignupEmail);
        edtTextLoginPassword = findViewById(R.id.edtTextSignupPassword);
        forgetPassword = findViewById(R.id.forgetPassword);
        progressBar=findViewById(R.id.progressBar3);
        account = new AccountData();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTextLoginEmail.getText().toString().isEmpty() && edtTextLoginPassword.getText().toString().isEmpty()) {
                    edtTextLoginEmail.setError("Enter the required Email");
                    edtTextLoginEmail.requestFocus();
                    edtTextLoginPassword.setError("Enter the required password");
                    edtTextLoginPassword.requestFocus();
                } else if (edtTextLoginEmail.getText().toString().isEmpty()) {
                    edtTextLoginEmail.setError("Enter the required Email");
                    edtTextLoginEmail.requestFocus();
                } else if (edtTextLoginPassword.getText().toString().isEmpty()) {
                    edtTextLoginPassword.setError("Enter the required password");
                    edtTextLoginPassword.requestFocus();
                } else {
                    if (isValid(edtTextLoginEmail.getText().toString()) == false) {
                        edtTextLoginEmail.setError("Enter the valid email");
                        edtTextLoginEmail.requestFocus();
                        return;
                    } else if (edtTextLoginPassword.getText().toString().isEmpty()) {
                        edtTextLoginPassword.setError("Password is required");
                        edtTextLoginPassword.requestFocus();
                        return;
                    } else {
                        String email = edtTextLoginEmail.getText().toString();
                        String password = edtTextLoginPassword.getText().toString();
                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user.isEmailVerified()) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        saving = getApplicationContext().getSharedPreferences(saveData, 0);
                                        edit = saving.edit();
                                        edit.putBoolean(newLogin, true);
                                        try {
                                            edit.putString("Email", user.getEmail());

                                        } catch (Exception e) {
                                            Log.i(TAG, "onComplete: " + e);
                                        }

                                        edit.commit();
                                        Bundle bundle=new Bundle();
                                        bundle.putString("password", edtTextLoginPassword.getText().toString());

                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        user.sendEmailVerification();
                                        Toast.makeText(Login.this, "Check your email to verify you account!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Failed to login!Please check your credentials", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
        setFaceBookBtn();
    }

    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private void setFaceBookBtn() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setPermissions(Arrays.asList(EMAIl));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: " + loginResult);
                handleFaceBookAccessToken(loginResult.getAccessToken());
                FirebaseUser user = mAuth.getCurrentUser();
                saving = getApplicationContext().getSharedPreferences(saveData, 0);
                edit = saving.edit();
                edit.putBoolean(newLogin, true);
                try {
                    edit.putString("Email", user.getEmail());

                } catch (Exception e) {
                    Log.i(TAG, "onComplete: " + e);
                }

                edit.commit();
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:OnCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: " + error.getMessage());
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, ForgetPassword.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void handleFaceBookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFaceBookAccessToken: " + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    account = new AccountData();
                    account.setAccFaceBookEmail(user.getEmail());
                    account.setAccFaceBookName(user.getDisplayName());

                } else {
                    Log.w(TAG, "onComplete: failure", task.getException());
                    Toast.makeText(Login.this, "Authentication Failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }

}