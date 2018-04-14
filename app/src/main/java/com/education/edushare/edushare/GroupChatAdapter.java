package com.education.edushare.edushare;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.education.edushare.edushare.model.ChatContactsModel;
import com.education.edushare.edushare.model.DashBoardModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Akash Kabir on 25-12-2017.
 */

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.MyViewHolder> {

    private static GroupChatAdapter.ClickListener clickListener;
    ArrayList<DashBoardModel> arrayList=new ArrayList<>();
    Context context;
    Typeface typeface ;
    public GroupChatAdapter(Context context, ArrayList<DashBoardModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ShortStack-Regular.ttf");

    }

    public void setOnItemClickListener(GroupChatAdapter.ClickListener clickListener) {
        GroupChatAdapter.clickListener = clickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_contact, parent, false);
        return new GroupChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getPname());
        holder.desc.setVisibility(View.GONE);
        holder.title.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public TextView title,desc;
        public ImageView image;
        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.contact_name);
            desc=view.findViewById(R.id.list_desc);
            image=view.findViewById(R.id.img_user);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }
}
