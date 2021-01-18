package com.app.triponeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    SharedPreferences saving;
    public static final String saveData = "NewData";
    public static final String newLogin = "NewLogin";
    String user;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser users = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saving = getSharedPreferences(saveData, 0);
                boolean firstTime = saving.getBoolean(newLogin, false);
                user = saving.getString("Email", "null");
                if (!firstTime) {
                    Intent login = new Intent(Splash.this, Login.class);
                    startActivity(login);
                    finish();
                } else if (firstTime) {
                    Intent Home = new Intent(Splash.this, MainActivity.class);
                    startActivity(Home);
                    finish();
                } else {
                    Intent login = new Intent(Splash.this, Login.class);
                    startActivity(login);
                    finish();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}