package com.app.triponeer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Profile extends Fragment {
    Button btnEditProfile, btnLogout;
    ImageView imgViewProfilePicture;
    TextView txtViewProfileName, txtViewProfileEmail;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();
    private DatabaseReference reference;
    private String emailID = "";
    AccountData account;
    SharedPreferences saving;
    SharedPreferences.Editor edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile, container, false);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        imgViewProfilePicture = view.findViewById(R.id.imgViewProfilePicture);
        txtViewProfileName = view.findViewById(R.id.txtViewProfileName);
        txtViewProfileEmail = view.findViewById(R.id.txtViewProfileEmail);
        account = new AccountData();
        btnEditProfile();
        btnLogout();

        if (user != null) {
            reference = FirebaseDatabase.getInstance().getReference("Users");
            emailID = user.getUid();
            reference.child(emailID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        String name = userProfile.accName;
                        String emails = userProfile.accEmail;


                              txtViewProfileName.setText(name);
                              txtViewProfileEmail.setText(emails);

                    } else if (user != null) {
                        txtViewProfileName.setText(user.getDisplayName());
                        txtViewProfileEmail.setText(user.getEmail());
                        String photoURL = user.getPhotoUrl().toString();
                        photoURL = photoURL + "?type=large";
                        if (photoURL != null) {
                            Picasso.get().load(photoURL).into(imgViewProfilePicture);
                        } else {
                            imgViewProfilePicture.setImageResource(R.drawable.profile_96);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Something wrong happend!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            boolean firstTime = saving.getBoolean(Login.newLogin, true);
            Log.i("TAG", "user is null");
        }
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                        .replace(R.id.fragment_container, new Profile()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        return view;
    }


    void btnEditProfile() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name", txtViewProfileName.getText().toString());
                bundle.putString("email", txtViewProfileEmail.getText().toString());
                EditProfile editProfile = new EditProfile();
                editProfile.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fragment_enter_right_to_left, R.anim.fragment_exit_to_left)
                        .replace(R.id.fragment_container, editProfile).commit();
            }
        });
    }

    void btnLogout() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getContext(), Login.class);
                saving = getContext().getSharedPreferences(Login.saveData, 0);
                edit = saving.edit();
                edit.putBoolean(Login.newLogin, false);
                edit.commit();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(intent);
                getActivity().finish();
            }
        });
    }
}