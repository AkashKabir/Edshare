package com.education.edushare.edushare.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.RequestModel;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Akash Kabir on 10-12-2017.
 */

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    ArrayList<RequestModel> arrayList=new ArrayList<>();
    Context context;
    FirebaseUser user;
    DatabaseReference dref,mref,pref,kref;
    Typeface typeface ;
    DatabaseReference mMessagesDatabaseReference ;
    private static RequestAdapter.ClickListener clickListener;
    String encrypted,EncryptedAESkey;
    String secretKey;
    public static final String RSA = "RSA";

    public String userPublickey;
    private SharedPreferences Details;
    private SharedPreferences.Editor prefEditor;

    public RequestAdapter(Context context, ArrayList<RequestModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
        user= FirebaseAuth.getInstance().getCurrentUser();
        mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/OpenSans-Regular.ttf");
        Details = context.getSharedPreferences("USERDATA", MODE_PRIVATE);
        prefEditor = Details.edit();
    }

    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_req, parent, false);
        return new RequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RequestAdapter.MyViewHolder holder, final int position) {
        holder.tvRequested.setVisibility(View.GONE);
        fetchImage(holder,position);
/*        holder.tvRequested.setTypeface(typeface);
        holder.tvAccepted.setTypeface(typeface);
        holder.title.setTypeface(typeface);
        holder.desc.setTypeface(typeface);*/
        holder.tvAccepted.setVisibility(View.GONE);
        holder.imag_rej.setVisibility(View.VISIBLE);
        holder.img_accp.setVisibility(View.VISIBLE);
        holder.title.setText(arrayList.get(position).getReqname());
        holder.desc.setText(arrayList.get(position).getName());
        if(arrayList.get(position).getStatus().equals("sent")){
            holder.imag_rej.setVisibility(View.GONE);
            holder.img_accp.setVisibility(View.GONE);
            holder.tvAccepted.setVisibility(View.GONE);
            holder.tvRequested.setVisibility(View.VISIBLE);
        }else if(arrayList.get(position).getStatus().equals("accepted")){
            holder.imag_rej.setVisibility(View.GONE);
            holder.img_accp.setVisibility(View.GONE);
            holder.tvAccepted.setVisibility(View.VISIBLE);
            holder.tvRequested.setVisibility(View.GONE);
        }else if(arrayList.get(position).getStatus().equals("rejected")){
            holder.imag_rej.setVisibility(View.GONE);
            holder.img_accp.setVisibility(View.GONE);
            holder.tvAccepted.setText("REJECTED");
            holder.tvAccepted.setVisibility(View.VISIBLE);
            holder.tvRequested.setVisibility(View.GONE);
        }

        holder.img_accp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> val=new HashMap<>();
                val.put(arrayList.get(position).getRequid().toString(),"connected");
               /* FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("requests").child(arrayList.get(position).getRequid()).child(arrayList.get(position).getRequid()).removeValue();*/
                holder.imag_rej.setVisibility(View.GONE);
                holder.img_accp.setVisibility(View.GONE);
                holder.tvAccepted.setVisibility(View.VISIBLE);
                Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                dref=FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(user.getUid()).child("members").child(arrayList.get(position).getRequid());
                mref=FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(arrayList.get(position).getRequid()).child("members").child(user.getUid());

                dref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            userPublickey=Details.getString("publicKeyString","");
                            //generate aes key:
                            generateAESkey();
                            prefEditor.putString("aeskey",secretKey);
                            prefEditor.commit();
                            Log.d("TAG", "onDataChange: aeskey "+secretKey);
                            //generate encrypted aes key
                            encrypted=GenerateEncryptedAESKeyWithRSA(secretKey,arrayList.get(position).getPublickey());

                            Log.d("TAG", "onDataChange: public key used for aes "+arrayList.get(position).getPublickey());
                            HashMap<String,String> hash=new HashMap<String, String>();
                            hash.put("name",arrayList.get(position).getReqname());
                            hash.put("key",encrypted);
                            hash.put("publicKey",arrayList.get(position).getPublickey());
                            hash.put("type","me");
                            dref.setValue(hash);
                            HashMap<String,String> hash2=new HashMap<String, String>();
                            hash2.put("name",user.getDisplayName());
                            hash2.put("key",encrypted);
                            hash2.put("publicKey",userPublickey);
                            hash2.put("type","notme");
                            mref.setValue(hash2);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





                //add that project to involved list
                pref = FirebaseDatabase.getInstance().getReference().child("users").child(arrayList.get(position).getRequid()).child("projectsInvolved");
                String pushkey=arrayList.get(position).getReqpuid();
                HashMap<String,String> map=new HashMap<>();
                map.put(pushkey,"joined");
                map.put("name",arrayList.get(position).getName().trim());
                pref.child(pushkey).setValue(map);

                //add that accepted request to status of user
                FirebaseDatabase.getInstance().getReference().child("users").child(arrayList.get(position).getRequid()).child("requests").child(arrayList.get(position).getReqpuid()).child(arrayList.get(position).getRequid()).child("status").setValue("accepted");
                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("requests").child(arrayList.get(position).getReqpuid()).child(arrayList.get(position).getRequid()).child("status").setValue("accepted");

                //add member to the involved list in project
                FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child("All").child(arrayList.get(position).getReqpuid()).child("membersInvolved").child(arrayList.get(position).getRequid()).child(arrayList.get(position).getRequid()).setValue(arrayList.get(position).getReqname());

                //send acceptance notification
                mMessagesDatabaseReference.child(arrayList.get(position).getRequid()).child("NewRequests").child(user.getUid()).setValue("Project request accepted by "+user.getDisplayName()+" for project "+arrayList.get(position).getName());

            }
        });


        holder.imag_rej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: "+arrayList.get(position).getReqpuid()+" "+arrayList.get(position).getRequid());
                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("requests").child(arrayList.get(position).getReqpuid()).child(arrayList.get(position).getRequid()).child("status").setValue("rejected");
                FirebaseDatabase.getInstance().getReference().child("users").child(arrayList.get(position).getRequid()).child("requests").child(arrayList.get(position).getReqpuid()).child(arrayList.get(position).getRequid()).child("status").setValue("rejected");
                holder.imag_rej.setVisibility(View.GONE);
                holder.img_accp.setVisibility(View.GONE);
                holder.tvAccepted.setText("REJECTED");
                holder.tvAccepted.setVisibility(View.VISIBLE);
                holder.tvRequested.setVisibility(View.GONE);

                mMessagesDatabaseReference.child(arrayList.get(position).getRequid()).child("NewRequests").child(user.getUid()).setValue("Project request rejected by "+user.getDisplayName()+" for project "+arrayList.get(position).getName());
                Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(RequestAdapter.ClickListener clickListener) {
        RequestAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView title,tvRequested,tvAccepted,desc;
        public TextView img_accp,imag_rej;
        ImageView imguser;
        public LinearLayout ll,ll_upvotes,ll_stars;
        public MyViewHolder(View view) {
            super(view);
            imguser= view.findViewById(R.id.img_user);
            title = (TextView) view.findViewById(R.id.contact_name);
            img_accp=view.findViewById(R.id.img_accept);
            imag_rej=view.findViewById(R.id.img_reject);
            tvAccepted=view.findViewById(R.id.tv_accepted);
            tvRequested=view.findViewById(R.id.tv_requested);
           // view.setOnClickListener(this);
            desc=view.findViewById(R.id.list_desc);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
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
        secretKey = Base64.encodeToString(tempkey.getEncoded(),Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);
    }

    private String GenerateEncryptedAESKeyWithRSA(String skey,String publickeyofseconduser) {
        encrypted=encryptData(skey,publickeyofseconduser);
        return encrypted;
    }

    public String encryptData(String text, String pub_key) {
        /*Encrypting the Aes key with the publickeyofseconduser so that he could encrypt it...it will be shared only when request
        is accepted*/
        try {
            Log.d("TAG", "onClick: encrypt try block");
            byte[] data = text.getBytes("utf-8");
            PublicKey publicKey = getPublicKey(Base64.decode(pub_key.getBytes("utf-8"), Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE));
            Log.d("TAG", "after getpublickey ");
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            Log.d("TAG", "onClick: encrypt return before  ");
            return Base64.encodeToString(cipher.doFinal(data), Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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


    public void fetchImage(final RequestAdapter.MyViewHolder holder, int pos) {
        //==================================================================*/
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(arrayList.get(pos).getRequid()+ ".jpg");
        SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
                holder.imguser.setImageDrawable(bitmap);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                holder.imguser.setImageResource(R.drawable.default2);
            }
        };
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(riversRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .into(target);
    }

}
