package com.education.edushare.edushare;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.education.edushare.edushare.Utils.RSAEncryptDecrypt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.spongycastle.util.test.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 20-11-2017.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.app_details)
    TextView appDetails;
    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.ll_login)
    LinearLayout llLogin;
    @BindView(R.id.pbar)
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String TAG = "TAG";
    private SharedPreferences Details;
    private SharedPreferences.Editor prefEditor;
    public DatabaseReference dref;

    private String enprivatekeyString;
    private String enpublickeyString;

    //key length
    public static final int KEY_LENGTH = 1028;
    //main family of rsa
    public static final String RSA = "RSA";
    String initVector = "RandomInitVector";
    String secretKey,EncryptedAESkey,DecryptedAESkey;
    private String privatekeyString;
    private String publickeyString;
    private String publickeysu;
    FirebaseUser user;
    public String aeskey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Monofett.ttf");
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/ShortStack-Regular.ttf");
        appDetails.setTypeface(typeface2);
        name.setTypeface(typeface);
        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        init();
    }

    private void init() {
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);  //stores user name and path to profilepic
        prefEditor = Details.edit();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_login: {
                progressBar.setVisibility(View.VISIBLE);
                signIn();
               /* Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();*/
                break;
            }
            case R.id.btn_signup: {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                break;
            }
        }
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(etUsername.getText().toString().trim(), etPwd.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);

                            //aeskey
                            aeskey=etPwd.getText().toString().trim()+etPwd.getText().toString().trim();
                            aeskey=aeskey.substring(0,16);
                            Log.d("TAG", "LoginActivity: aeskey: "+aeskey+" aes length: "+aeskey.length());
                            user = FirebaseAuth.getInstance().getCurrentUser();
                         /*   startService(new Intent(LoginActivity.this, Notifications.class));*/
                            StoreProfile();
                            fetchkeys();
                           /* InitializePAPkeystoString(getkeypair());*/
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void fetchkeys() {
        dref= FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                enpublickeyString=dataSnapshot.child("pkey").getValue().toString();
                enprivatekeyString=dataSnapshot.child("prkey").getValue().toString();
                privatekeyString=AESdecrypt(aeskey,initVector,enprivatekeyString);
                publickeyString=AESdecrypt(aeskey,initVector,enpublickeyString);
                prefEditor.putString("publicKeyString",publickeyString);
                prefEditor.putString("privateKeyString",privatekeyString);
                prefEditor.commit();
                Log.d("TAG", "LoginAct aeskey: "+aeskey);
                Log.d("TAG", "LoginAct privatekey: "+privatekeyString);
                Log.d("TAG", "LoginAct publickey:" + publickeyString);
                Log.d("TAG", "LoginAct enpkey: "+enpublickeyString);
                Log.d("TAG", "LoginAct enprkey: "+enprivatekeyString);
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void StoreProfile() {

        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            prefEditor.putString("name",name);
            prefEditor.putString("email",email);
            prefEditor.putString("url",photoUrl.toString());
            prefEditor.commit();

           /* // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
*/
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        }
    }


    public String AESdecrypt(String key, String initVector, String encrypted) {
        try {
            /*byte[] decodedKey = Base64.decode(key, Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");*/
            Log.d("TAG", "AES:   decrypting");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
