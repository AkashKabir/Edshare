package com.education.edushare.edushare.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.CommunityModel;

import java.util.ArrayList;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {
    ArrayList<CommunityModel> arrayList=new ArrayList<>();
    Context context;
    private static ClickListener clickListener;

    public CommunityAdapter(Context context, ArrayList<CommunityModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forums, parent, false);
        return new CommunityAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getName());
        for (String d:arrayList.get(position).getTagArray()) {
            holder.ll.addView(createNewTextView(d));
        }
        holder.tv_upvotes.setText(Integer.toString(arrayList.get(position).getUpvotes())+" upvotes");
        holder.tv_stars.setText(Integer.toString(arrayList.get(position).getStars())+" stars");
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        CommunityAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 5, 5, 5);
        final TextView textView = new TextView(context);
        textView.setPadding(10, 5, 5, 5);
        textView.setBackgroundResource(R.drawable.tag_bckg);
        textView.setLayoutParams(lparams);
        textView.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        textView.setText(text);
        return textView;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public TextView title,tv_upvotes,tv_stars;
        public ImageView img_stars,img_upvotes;
        public LinearLayout ll,ll_upvotes,ll_stars;
        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.txt_title);
            ll=(LinearLayout) view.findViewById(R.id.linearLayout_tag);
            tv_stars = (TextView) view.findViewById(R.id.tv_stars);
            ll_stars=(LinearLayout) view.findViewById(R.id.ll_stars);
            tv_upvotes= (TextView) view.findViewById(R.id.tv_upvotes);
            ll_upvotes=(LinearLayout) view.findViewById(R.id.ll_upvotes);
            img_stars=view.findViewById(R.id.img_stars);
            img_upvotes=view.findViewById(R.id.img_upvotes);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }
}
