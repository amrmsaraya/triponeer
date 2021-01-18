package com.app.triponeer;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;


public class EditProfile extends Fragment {

    ImageView imgViewProfileImage;
    EditText edtTextName;
    EditText edtTextEmail;
    EditText edtTextPassword;
    EditText edtTextConfirmPassword;
    Button btnSaveEditProfile;
    TextView txtViewEditProfileWarning;
    EditText edtOldPassword;
    Bitmap profileImage;
    String name;
    String email;
    String password;
    FirebaseAuth auth=FirebaseAuth.getInstance();
    private FirebaseUser user=auth.getCurrentUser();
    public static Uri imgpath;
    ProgressBar progressBar;
    DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_profile, container, false);
        name = getArguments().getString("name");
        email = getArguments().getString("email");
        password=getArguments().getString("password");
        initComponent(view);

        edtTextName.setText(name);
        edtTextEmail.setText(email);
        btnSaveEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtTextName.getText().toString().isEmpty() ||
                        edtTextEmail.getText().toString().isEmpty()) {
                      txtViewEditProfileWarning.setText("* Enter the valid data");
                } else if (!isValid(edtTextEmail.getText().toString())) {
                    txtViewEditProfileWarning.setText("* Invalid Email Address !");
                } else if (!edtTextPassword.getText().toString().equals(edtTextConfirmPassword.getText().toString())) {
                    txtViewEditProfileWarning.setText("* password doesn't match !");
                    edtTextPassword.setText("");
                    edtTextConfirmPassword.setText("");
                } else if(user!=null) {

                   /* name = edtTextName.getText().toString();
                    email = edtTextEmail.getText().toString();
                    password = edtTextPassword.getText().toString();*/
                    String oldpassword=edtOldPassword.getText().toString();

                    progressBar.setVisibility(View.VISIBLE);
                    reference= FirebaseDatabase.getInstance().getReference("Users");
                    isNameChanged(view);
                    isEmailChanged(view);
                    isPasswordChanged(view);
                    AuthCredential authCredential= EmailAuthProvider.getCredential(user.getEmail(),oldpassword);

                    user.reauthenticate(authCredential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.updatePassword(edtTextPassword.getText().toString())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                                    user.updateEmail(edtTextEmail.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });


                                    Toast.makeText(getContext(), "Data has been updated", Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .setCustomAnimations(R.anim.fragment_enter_left_to_right, R.anim.fragment_exit_to_right)
                                            .replace(R.id.fragment_container, new Profile()).commit();



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Data is the same and cannot be updated!", Toast.LENGTH_SHORT).show();


                        }
                    });


                }
            }
        });


        imgViewProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

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

    private void initComponent(View view) {
        imgViewProfileImage = view.findViewById(R.id.imgViewProfileImage);
        edtTextName = view.findViewById(R.id.edtTextName);
        edtTextEmail = view.findViewById(R.id.edtTextEmail);
        edtTextPassword = view.findViewById(R.id.edtTextPassword);
        edtTextConfirmPassword = view.findViewById(R.id.edtTextConfirmPassword);
        btnSaveEditProfile = view.findViewById(R.id.btnSaveEditProfile);
        txtViewEditProfileWarning = view.findViewById(R.id.txtViewEditProfileWarning);
        edtOldPassword=view.findViewById(R.id.edtOldPassword);
        progressBar=view.findViewById(R.id.progressBar2);
        profileImage = null;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromStream(Context context, Uri uri,
                                                       int reqWidth, int reqHeight) {
        InputStream inputStream = null;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            inputStream = contentResolver.openInputStream(uri);
            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            inputStream = contentResolver.openInputStream(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.RESULT_OK && requestCode == 1) {
            try {
                final Uri imageUri = data.getData();
                imgpath=imageUri;
                final Bitmap selectedImage = decodeSampledBitmapFromStream(getContext(), imageUri, 200, 200);
                profileImage = selectedImage;
                imgViewProfileImage.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }


    private boolean isNameChanged(View view) {
        if(!name.equals(edtTextName.getText().toString()))
        {
             reference.child(user.getUid()).child("accName").setValue(edtTextName.getText().toString());
             return true;
        }
        else
        {
            return false;
        }
    }
    private boolean isEmailChanged(View view) {
        if(!email.equals(edtTextEmail.getText().toString()))
        {
            reference.child(user.getUid()).child("accEmail").setValue(edtTextEmail.getText().toString());
            return true;
        }
        else
        {
            return false;
        }
    }
    private boolean isPasswordChanged(View view) {
        if(!edtOldPassword.equals(edtTextPassword.getText().toString()))
        {
            reference.child(user.getUid()).child("accPassword").setValue(edtTextPassword.getText().toString());
            return true;
        }
        else
        {
            return false;
        }
    }

}