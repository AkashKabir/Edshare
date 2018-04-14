package com.education.edushare.edushare;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.Adapters.MessageAdapter;
import com.education.edushare.edushare.model.ChatContactsModel;
import com.education.edushare.edushare.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 20-11-2017.
 */

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.tv_new_txt)
    EditText tvNewTxt;
    @BindView(R.id.img_send_msg)
    ImageView imgSendMsg;
    @BindView(R.id.ll_new_msg)
    LinearLayout llNewMsg;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    private SharedPreferences Details;

    public ArrayList<MessageModel> arrayList = new ArrayList<>();
    MessageAdapter mAdapter;
    String uid, title;
    FirebaseUser user;
    public String EncrText;
    DatabaseReference dref, mref, fref,kref;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    String aes, publicsu;
    ChatContactsModel object;
    String userprivateKey;

    public static final String RSA = "RSA";
    String key; // 128 bit key
    String initVector = "RandomInitVector"; // 16 bytes IV

    /********************************************************************/
    //RSA key pair (public and private)
    private KeyPair rsaKey = null;

    //encrypted aes key and ivs combined
    private byte[] encryptedAESKey = null;
    Typeface typeface;
    private String decryptedAESKEy;

    public static int LAST_READ_MESSAGE,LAST_READ_MESSAGE_UPDATED;
    long Intcc;
    public static int CHILDREN_COUNT;
    public Boolean addednewMessage=false;
    /******************************************************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/ShortStack-Regular.ttf");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        object = (ChatContactsModel) getIntent().getSerializableExtra("object");
        init();
        tvInfo.setTypeface(typeface2);
        tvNewTxt.setTypeface(typeface2);
        tvNothing.setVisibility(View.GONE);
        tvNothing.setTypeface(typeface2);
        tvTitle.setTypeface(typeface2);
        fetchMessages();
        imgSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvNewTxt.getText().toString().trim().length() > 0) {
                    //encrypt the message first
                    sendMessage(AESencrypt(aes, initVector, tvNewTxt.getText().toString().trim()));
                    tvNewTxt.setText("");
                }
            }
        });
    }

    private void sendMessage(String trim) {
        DatabaseReference mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("NewRequests");
        MessageModel m = new MessageModel(uid, trim, "sent", "0", user.getUid(), "0", "null");
        MessageModel m1 = new MessageModel(user.getUid(), trim, "received", "0", uid, "0", "null");
        dref = FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(user.getUid()).child("members").child(uid).child("messages");
        String key = dref.push().getKey();
        mref = FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(uid).child("members").child(user.getUid()).child("messages");
        mref.child(key).setValue(m1);
        dref.child(key).setValue(m);
        mMessagesDatabaseReference.child(user.getUid()).setValue("New Message from "+user.getDisplayName());
    }

    private void fetchMessages() {
        fref = FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(user.getUid()).child("members").child(uid).child("messages");
        kref= FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(user.getUid()).child("members").child(uid);

        kref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.child("messages").exists())
                    tvNothing.setVisibility(View.VISIBLE);
                else{
                    String hello;
                    if(dataSnapshot.child("lastRM").exists()){
                        Log.d("MESSAGE", "onDataChange: "+dataSnapshot.toString()+"   "+dataSnapshot.child("lastRM").getValue().toString());
                        hello=dataSnapshot.child("lastRM").getValue().toString();
                    }else{
                        hello="0";
                    }
                    Log.d("MESSAGE","last read msg: "+hello);
                    LAST_READ_MESSAGE=Integer.parseInt(hello);

                    Intcc=dataSnapshot.child("messages").getChildrenCount();
                    CHILDREN_COUNT=(int)Intcc;
                    Log.d("MESSAGE","CHILDREN COUNT: "+dataSnapshot.child("messages").getChildrenCount()+" "+LAST_READ_MESSAGE+ " "+CHILDREN_COUNT);
                    tvNothing.setVisibility(View.GONE);
                }
                fetchmessagenow();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fetchmessagenow() {
        addednewMessage=false;
        fref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                tvNothing.setVisibility(View.GONE);
                MessageModel m = dataSnapshot.getValue(MessageModel.class);
                arrayList.add(m);

                if(arrayList.size()==LAST_READ_MESSAGE && LAST_READ_MESSAGE!=0 && CHILDREN_COUNT>LAST_READ_MESSAGE){
                    arrayList.add(new MessageModel("0000","","","","0000","","null"));
                    addednewMessage=true;
                }

                mAdapter.notifyItemInserted(arrayList.size() - 1);
                rvMsg.smoothScrollToPosition(arrayList.size() - 1);

                if(!addednewMessage) {
                    if (arrayList.size() == CHILDREN_COUNT)
                        kref.child("lastRM").setValue(Integer.toString(arrayList.size()));
                }else if((arrayList.size()-1) == CHILDREN_COUNT){
                    kref.child("lastRM").setValue(Integer.toString(arrayList.size()-1));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void init() {
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);

        //get the aes keys:
        if (object.getType().equals("me")) {
            aes = Details.getString("aeskey", "");
            Log.d("TAG", "Selected for me  " + aes);
        } else {
            userprivateKey = Details.getString("privateKeyString", "");
            aes = GenerateDecryptedAESKeyWithRSA(object.getAeskey());
        }

        publicsu = object.getPublickeysu();
        uid = /*getIntent().getStringExtra("uid");*/object.getUid();
        title = /*getIntent().getStringExtra("title");*/ object.getName();
        user = FirebaseAuth.getInstance().getCurrentUser();
        tvTitle.setText(title);
        mAdapter = new MessageAdapter(this, arrayList, title, uid, aes);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMsg.setLayoutManager(mLayoutManager);
        rvMsg.setItemAnimator(new DefaultItemAnimator());
        rvMsg.setAdapter(mAdapter);
    }

    private String GenerateDecryptedAESKeyWithRSA(String encryptedAESkey) {
        decryptedAESKEy = decryptData(encryptedAESkey, userprivateKey);
        return decryptedAESKEy;
    }

    public String decryptData(String text, String pri_key) {
        try {
            Log.d("TAG", "onClick: decrypt try block in chatactivity " + pri_key);
            Log.d("TAG", " userprivate key: " + userprivateKey);
            Log.d("TAG", "aes text: " + object.getAeskey());
            /*pri_key="to test set the pri_key to the private key opposite to public key from which the data is encrypted";*/
            byte[] data = Base64.decode(text, Base64.DEFAULT | Base64.NO_WRAP | Base64.URL_SAFE);
            PrivateKey privateKey = getPrivateKey(Base64.decode(pri_key.getBytes("utf-8"), Base64.DEFAULT | Base64.NO_WRAP | Base64.URL_SAFE));
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            Log.d("TAG", "onClick: decrypt return before  ");
            return new String(cipher.doFinal(data), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    public String AESencrypt(String key, String initVector, String value) {
        try {

            byte[] decodedKey = Base64.decode(key, Base64.DEFAULT | Base64.NO_WRAP | Base64.URL_SAFE);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Log.d("TAG", "AES: encrypting");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(originalKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeToString(encrypted, Base64.DEFAULT));

            return Base64.encodeToString(encrypted, Base64.DEFAULT | Base64.NO_WRAP | Base64.URL_SAFE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
