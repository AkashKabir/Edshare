package com.education.edushare.edushare.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.MessageModel;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private SharedPreferences Details;
    List<MessageModel> messages;
    String title;
    Typeface typeface ;
    String aes;
    String key; // 128 bit key
    String initVector = "RandomInitVector"; // 16 bytes IV
    String decrypted;
    public MessageAdapter(Activity Context, List<MessageModel> messages, String sendername, String title,String aes) {
        Details = Context.getSharedPreferences("USERDATA", MODE_PRIVATE);
        this.title = title;
        this.aes=aes;
        this.context = Context;
        this.messages = messages;
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/ShortStack-Regular.ttf");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new myHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final myHolder myholder = (myHolder) holder;
        myholder.messageTextView.setVisibility(View.VISIBLE);
        myholder.rlImg.setVisibility(View.GONE);
        myholder.messageTextView.setTypeface(typeface);

        Log.d("MESSAGE", "adapter: "+messages.get(position).getMsgType());
        if (messages.get(position).getMsgType().equals("sent")) {
            decrypted=AESdecrypt(aes,initVector,messages.get(position).getMsgTxt());
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myholder.messageTextView.getLayoutParams();
            params.setMargins(100, 3, 5, 3);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            myholder.messageTextView.setBackgroundResource(R.drawable.chat_send);
            myholder.rlImg.setBackgroundResource(R.drawable.chat_send);
            myholder.messageTextView.setLayoutParams(params);
        } else if(messages.get(position).getUserUid().equals("0000") && messages.get(position).getMsgUser().equals("0000")) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myholder.messageTextView.getLayoutParams();
            params.setMargins(0,6, 0, 6);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            myholder.messageTextView.setBackgroundResource(R.drawable.ref_bkg);
            myholder.messageTextView.setLayoutParams(params);
            myholder.messageTextView.setText("new messages");
            myholder.messageTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        }
        else{
            decrypted=AESdecrypt(aes,initVector,messages.get(position).getMsgTxt());
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myholder.messageTextView.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.setMargins(0, 3, 100, 3);
            myholder.messageTextView.setBackgroundResource(R.drawable.chat_recieve);
            myholder.rlImg.setBackgroundResource(R.drawable.chat_recieve);
            myholder.messageTextView.setLayoutParams(params);
        }

        if (!messages.get(position).getMsgTxt().equals("null") && messages.get(position).getImgUrl().equals("null") && !messages.get(position).getUserUid().equals("0000"))
        {
            myholder.messageTextView.setText(decrypted.toString().trim());
        }

        else if (!messages.get(position).getImgUrl().equals("null")) {
            myholder.messageTextView.setVisibility(View.GONE);
            myholder.rlImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class myHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, imgTv;
        ImageView imageMsg;
        RelativeLayout rlImg;
        ProgressBar pbar;

        public myHolder(View itemView) {
            super(itemView);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text);
            rlImg = itemView.findViewById(R.id.rl_img_msg);
            imageMsg = itemView.findViewById(R.id.imagemsssg);
            imgTv = itemView.findViewById(R.id.textmsgimg);
            pbar = itemView.findViewById(R.id.animbar);
        }

    }


    public String AESdecrypt(String key, String initVector, String encrypted) {
        try {
            byte[] decodedKey = Base64.decode(key, Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE);
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            Log.d("TAG", "AES:   decrypting: "+encrypted+" decoded : "+decodedKey.length);
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(originalKey.getEncoded(), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT|Base64.NO_WRAP | Base64.URL_SAFE));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
