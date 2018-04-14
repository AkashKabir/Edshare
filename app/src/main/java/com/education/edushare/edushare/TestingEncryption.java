package com.education.edushare.edushare;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.education.edushare.edushare.Utils.RSAEncryptDecrypt;
import com.google.gson.Gson;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 23-12-2017.
 */

public class TestingEncryption extends AppCompatActivity {
    KeyPairGenerator kpg;
    PublicKey publicKey;
    PrivateKey privateKey;
    byte[] encryptedBytes, decryptedBytes, ebresult, dbresult;
    Cipher cipher, cipher1;
    String encrypted, decrypted;
    private SharedPreferences Details;
    KeyPair kp;

    @BindView(R.id.inputtedUnencryptedText)
    EditText inputtedUnencryptedText;
    @BindView(R.id.encryptButton)
    Button encryptButton;
    @BindView(R.id.encryptedText)
    EditText encryptedText;
    @BindView(R.id.decryptButton)
    Button decryptButton;
    @BindView(R.id.decryptedText)
    EditText decryptedText;
    @BindView(R.id.clearButton)
    Button clearButton;

    //key length
    public static final int KEY_LENGTH = 1028;
    //main family of rsa
    public static final String RSA = "RSA";
    String key; // 128 bit key
    String initVector = "RandomInitVector"; // 16 bytes IV
    String secretKey,EncryptedAESkey,DecryptedAESkey;
    private String privatekeyString;
    private String publickeyString;
    private String publickeysu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);
        ButterKnife.bind(this);

        InitializePAPkeystoString(getkeypair());
        getpublickeyofseconduser();
        generateAESkey();
        key=secretKey;
        Log.d("TAG", "Public key:  "+publickeyString+" privatekey: "+privatekeyString);
        Log.d("TAG", "public key second user : "+publickeysu);
        Log.d("TAG", "AES key: "+secretKey);
        EncryptedAESkey=GenerateEncryptedAESKeyWithRSA(secretKey,publickeysu);
        Log.d("TAG", "EncryptedAES key : "+EncryptedAESkey);
        DecryptedAESkey=GenerateDecryptedAESKeyWithRSA(EncryptedAESkey);
        Log.d("TAG", "Decrypted AES key: "+DecryptedAESkey);

        SavePublicAndAESkey(publicKey,EncryptedAESkey);



        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: encrypt button clicked");
                String txt = inputtedUnencryptedText.getText().toString().trim();
                /* try {
                    ebresult = RSAEncrypt(txt);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }
                encrypted = new String(ebresult);
                encryptedText.setText(encrypted);*/
                /*encrypted=encryptData(txt);*/
                encrypted = AESencrypt(key, initVector, txt);
                encryptedText.setText(encrypted);

            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: decrypt button clicked");
      /*          decrypted=decryptData(encrypted);*/
              /*  *//*try {
                    decrypted = RSADecrypt(ebresult);
                    Log.d("TAG", "onCreate: decrypted text is: " + decrypted);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                }*//*

                decryptedText.setText(decrypted);*/
                decrypted = AESdecrypt(key, initVector, encrypted);
                decryptedText.setText(decrypted);

            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                encryptedText.setText("");
                decryptedText.setText("");
                inputtedUnencryptedText.setText("");
            }
        });

        kp = getkeypair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();

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
    private void InitializePAPkeystoString(KeyPair kp){
        byte[] encodedPublicKey = kp.getPublic().getEncoded();
        publickeyString = Base64.encodeToString(encodedPublicKey,Base64.DEFAULT);
      /*  publickeyString = kp.getPublic().toString();*/
        byte[] encodedPrivateKey = kp.getPrivate().getEncoded();
        privatekeyString = Base64.encodeToString(encodedPrivateKey,Base64.DEFAULT);
       /* privatekeyString=kp.getPrivate().toString();*/
    }

    private void getpublickeyofseconduser() {
        publickeysu=publickeyString;
    }
    private void generateAESkey() {
        KeyGenerator keyGen = null;
        try {
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGen.init(128);
        SecretKey tempkey=keyGen.generateKey();
        secretKey = Base64.encodeToString(tempkey.getEncoded(),Base64.DEFAULT);
    }
    private String GenerateEncryptedAESKeyWithRSA(String skey,String publickeyofseconduser) {
        encrypted=encryptData(skey,publickeyofseconduser);
        return encrypted;
    }
    private void SavePublicAndAESkey(PublicKey publicKey, String encryptedAESkey) {
    }
    private String GenerateDecryptedAESKeyWithRSA(String encryptedAESkey) {
        decrypted=decryptData(encryptedAESkey,privatekeyString);
        return decrypted;
    }
    public String encryptData(String text, String pub_key) {
        /*Encrypting the Aes key with the publickeyofseconduser so that he could encrypt it...it will be shared only when request
        is accepted*/
        try {
            Log.d("TAG", "onClick: encrypt try block");
            byte[] data = text.getBytes("utf-8");
            PublicKey publicKey = getPublicKey(Base64.decode(pub_key.getBytes("utf-8"), Base64.DEFAULT));
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            Log.d("TAG", "onClick: encrypt return before  ");
            return Base64.encodeToString(cipher.doFinal(data), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String decryptData(String text, String pri_key) {
        try {
            Log.d("TAG", "onClick: decrypt try block ");
            byte[] data = Base64.decode(text, Base64.DEFAULT);
            PrivateKey privateKey = getPrivateKey(Base64.decode(pri_key.getBytes("utf-8"),Base64.DEFAULT));
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            Log.d("TAG", "onClick: decrypt return before  ");
            return new String(cipher.doFinal(data), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String AESencrypt(String key, String initVector, String value) {
        try {
            byte[] decodedKey = Base64.decode(key,Base64.DEFAULT);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Log.d("TAG", "AES: encrypting");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(originalKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encodeToString(encrypted, Base64.DEFAULT));

            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
    public String AESdecrypt(String key, String initVector, String encrypted) {
        try {
            byte[] decodedKey = Base64.decode(key,Base64.DEFAULT);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Log.d("TAG", "AES:   decrypting");
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(originalKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    /**
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey getPublicKey(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }
    /**
     *
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







    public byte[] RSAEncrypt(final String plain) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Log.d("TAG", "RSAEncrypt: encrypting");

        Log.d("TAG", "RSAEncrypt: " + publicKey.toString() + " " + privateKey.toString());
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        encryptedBytes = cipher.doFinal(plain.getBytes());
        return encryptedBytes;
    }

    public String RSADecrypt(final byte[] encryptedBytes) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Log.d("TAG", "RSADecrypt:   decrypting");
        cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedBytes = cipher1.doFinal(encryptedBytes);
        decrypted = new String(decryptedBytes);
        System.out.println("DDecrypted?????" + decrypted);
        return decrypted;
    }
}
