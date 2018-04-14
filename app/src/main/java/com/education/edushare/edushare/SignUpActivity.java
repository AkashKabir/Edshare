package com.education.edushare.edushare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.education.edushare.edushare.Utils.RSAEncryptDecrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Bytes;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Akash Kabir on 04-12-2017.
 */

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    int RESULT_LOAD_IMG = 1;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.et_cnf_pwd)
    EditText etCnfPwd;
    @BindView(R.id.et_org)
    EditText etOrg;
    @BindView(R.id.signup_btn)
    Button signupBtn;
    @BindView(R.id.tv_login_here)
    TextView tvLoginHere;
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    FirebaseAuth mAuth;
    DatabaseReference database, gref, mref, cref;
    @BindView(R.id.pbar)
    ProgressBar progressBar;
    FirebaseUser user;
    String uid, profilePicURL;

    private SharedPreferences Details;
    private SharedPreferences.Editor prefEditor;

    //key length
    public static final int KEY_LENGTH = 1028;
    //main family of rsa
    public static final String RSA = "RSA";
    String initVector = "RandomInitVector";
    String secretKey,EncryptedAESkey,DecryptedAESkey;
    private String privatekeyString;
    private String publickeyString;
    private String enprivatekeyString;
    private String enpublickeyString;
    private String publickeysu;
    public String aeskey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);  //stores user name and path to profilepic
        prefEditor = Details.edit();
        signupBtn.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.signup_btn: {
                signupFirebase();
                /*
                Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
                startActivity(i);
                finish();*/
                break;
            }
            case R.id.img_profile: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                break;
            }
        }
    }


    private void signupFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etPwd.getText().toString().trim()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUpActivity.this, "Email Already Registered", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        Toast.makeText(SignUpActivity.this, "Network Connection not Available", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sign up failed: try Again", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("Vex-Life", task.getException().toString());
                    }
                } else {

                    //aeskey
                    aeskey=etPwd.getText().toString().trim()+etPwd.getText().toString().trim();
                    aeskey=aeskey.substring(0,16);
                    Log.d("TAG", "onComplete: aeskey: "+aeskey+" aes length: "+aeskey.length());


                    //generate public private key pair
                    InitializePAPkeystoString(getkeypair());

                    //encrypt public and private with aes:
                    enpublickeyString=AESencrypt(aeskey,initVector,publickeyString);
                    enprivatekeyString=AESencrypt(aeskey,initVector,privatekeyString);

                    progressBar.setVisibility(View.GONE);
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    final String username = etUsername.getText().toString();
                    final String org = etOrg.getText().toString();
                    HashMap<String, String> userdetails = new HashMap<String, String>();
                    userdetails.put("name", username);
                    userdetails.put("org", org);
                    userdetails.put("email", etEmail.getText().toString());
                    userdetails.put("pkey",enpublickeyString);
                    userdetails.put("prkey",enprivatekeyString);
                    Log.d("TAG", "signUpActivity aeskey: "+aeskey);
                    Log.d("TAG", "signUpActivity privatekey: "+privatekeyString);
                    Log.d("TAG", "signUpActivity publickey:" + publickeyString);
                    Log.d("TAG", "signUpActivity enpkey: "+enpublickeyString);
                    Log.d("TAG", "signUpActivity enprkey: "+enprivatekeyString);
                    Log.d("SignUpActivity", "createUserWithEmail:onComplete:" + task.isSuccessful());
                    //==============================================================

                    database = FirebaseDatabase.getInstance().getReference();
                    uid = mAuth.getCurrentUser().getUid();
                    // Log.d("TAG", "path:  "+mMessagesDatabaseReference.child("usernames").child(mAuth.getCurrentUser().getUid()).toString());

                    mref = database.child("CHAT");
                    database.child("users").child(uid).setValue(userdetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mref.child("coach").child(uid).child("name").setValue(username);
                            //***********************************************************************************************
                            StorageReference myfileRef = FirebaseStorage.getInstance().getInstance().getReference().child(user.getUid() + ".jpg");
                            Bitmap bitmap = ((BitmapDrawable) imgProfile.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = myfileRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(SignUpActivity.this, "Image Upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(SignUpActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            });
                            profilePicURL = "https://firebasestorage.googleapis.com/" + org;
                            setProfile();
                            //==============================================================
                        }
                    });
                    sendverificationMail();
                }
            }
        });
    }

    private void sendverificationMail() {
        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        // Re-enable button
                        //  findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this,
                                    "Verification email sent to " + email,
                                    Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(SignUpActivity.this, LoginActivity.class);
                            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        } else {
                            // Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(SignUpActivity.this,
                                    "Failed to send verification email. Try again",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                });
    }

    private void setProfile() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(etUsername.getText().toString())
                .setPhotoUri(Uri.parse(profilePicURL))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User profile created. " + user.getDisplayName());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgProfile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(SignUpActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void InitializePAPkeystoString(KeyPair kp){
        byte[] encodedPublicKey = kp.getPublic().getEncoded();
        publickeyString = Base64.encodeToString(encodedPublicKey,Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);

        byte[] encodedPrivateKey = kp.getPrivate().getEncoded();
        privatekeyString = Base64.encodeToString(encodedPrivateKey,Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);

        prefEditor.putString("publicKeyString",publickeyString);
        prefEditor.putString("privateKeyString",privatekeyString);
        prefEditor.commit();
        Log.d("TAG", "InitializePAPkeystoString: publickey "+ publickeyString+" privatekey: "+privatekeyString);

    }

    private KeyPair getkeypair() {
        KeyPairGenerator kpg = null;
        try {
            //get an RSA key generator
            kpg = KeyPairGenerator.getInstance(RSA);
        } catch (NoSuchAlgorithmException e) {
            Log.e(RSAEncryptDecrypt.class.getName(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
        kpg.initialize(KEY_LENGTH);
        return kpg.genKeyPair();
    }

    public String AESencrypt(String key, String initVector, String value) {
        try {

           /* byte[] decodedKey = Base64.decode(key, Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");*/
            Log.d("TAG", "AES: encrypting");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeToString(encrypted,Base64.DEFAULT));

            return Base64.encodeToString(encrypted, Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


}
