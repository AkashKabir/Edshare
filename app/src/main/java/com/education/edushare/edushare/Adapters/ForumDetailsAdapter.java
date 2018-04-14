package com.education.edushare.edushare.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.ForumMessageModel;

import java.util.ArrayList;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class ForumDetailsAdapter extends RecyclerView.Adapter<ForumDetailsAdapter.MyViewHolder> {
    ArrayList<ForumMessageModel> arrayList = new ArrayList<>();
    Context context;

    public ForumDetailsAdapter(Context context, ArrayList<ForumMessageModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum_msg, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvMsg.setText(arrayList.get(position).getMessage());
        holder.tvSender.setText(arrayList.get(position).getUsername());
        holder.tvNoOfLikes.setText(Integer.toString(arrayList.get(position).getLikes()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMsg, tvNoOfLikes, tvSender;
        public ImageView img_like, img_reply;

        public MyViewHolder(View view) {
            super(view);
            tvMsg = (TextView) view.findViewById(R.id.tv_msg);
            img_like = view.findViewById(R.id.img_like);
            img_reply = view.findViewById(R.id.img_reply);
            tvNoOfLikes = view.findViewById(R.id.tv_no_of_likes);
            tvSender = view.findViewById(R.id.tv_sender);
        }


    }
}
