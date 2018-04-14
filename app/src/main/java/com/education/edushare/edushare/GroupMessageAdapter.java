package com.education.edushare.edushare;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.education.edushare.edushare.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Akash Kabir on 25-12-2017.
 */

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.myHolder> {
    Context context;
    private SharedPreferences Details;
    ArrayList<Groupchatmodel> messages;
    String title;
    String uid;
    Typeface typeface;

    public GroupMessageAdapter(Activity context, ArrayList<Groupchatmodel> messages) {
        Details = context.getSharedPreferences("USERDATA", MODE_PRIVATE);
        this.context = context;
        this.messages = messages;
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/ShortStack-Regular.ttf");
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grp_message, parent, false);
        return new myHolder(itemView);
    }

    @Override
    public void onBindViewHolder(myHolder holder, int position) {
        final GroupMessageAdapter.myHolder myholder = (GroupMessageAdapter.myHolder) holder;
        myholder.messageTextView.setVisibility(View.VISIBLE);
        myholder.messageTextView.setTypeface(typeface);
        myholder.sender.setVisibility(View.GONE);
        if (messages.get(position).getSenuid().equals(uid)) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myholder.ll.getLayoutParams();
            params.setMargins(100, 3, 5, 3);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
/*            myholder.messageTextView.setBackgroundResource(R.drawable.chat_send);*/
            myholder.sender.setVisibility(View.GONE);
            myholder.ll.setBackgroundResource(R.drawable.chat_recieve);
            myholder.ll.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myholder.ll.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            params.setMargins(0, 3, 100, 3);
/*            myholder.messageTextView.setBackgroundResource(R.drawable.chat_recieve);*/
            myholder.ll.setBackgroundResource(R.drawable.chat_send);
            myholder.sender.setText(messages.get(position).getSenname());
            myholder.sender.setVisibility(View.VISIBLE);
            myholder.ll.setLayoutParams(params);
        }
        myholder.messageTextView.setText(messages.get(position).getMsgtxt());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public static class myHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, sender;
        LinearLayout ll;
        public myHolder(View itemView) {
            super(itemView);
            ll=itemView.findViewById(R.id.lltt);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text);
            sender = (TextView) itemView.findViewById(R.id.sender_tv);
        }
    }
}