package com.education.edushare.edushare.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.DashBoardModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Akash Kabir on 21-12-2017.
 */

public class StarredProjectsAdapter extends RecyclerView.Adapter<StarredProjectsAdapter.MyViewHolder> {
    ArrayList<DashBoardModel> arrayList = new ArrayList<>();
    ArrayList<DashBoardModel> backuParrayList = new ArrayList<>();
    Context context;
    private static StarredProjectsAdapter.ClickListener clickListener;
    Typeface typeface ;
    public ArrayList<DashBoardModel> contactListFiltered = new ArrayList<>();

   public StarredProjectsAdapter(Context context, ArrayList<DashBoardModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        contactListFiltered = arrayList;
        backuParrayList = arrayList;
       typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/OpenSans-Regular.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new StarredProjectsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getPname());
        Iterator it = arrayList.get(position).getNametag().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            holder.ll.addView(createNewTextView(pair.getKey().toString()));
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(StarredProjectsAdapter.ClickListener clickListener) {
        StarredProjectsAdapter.clickListener = clickListener;
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

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public LinearLayout ll;

        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.txt_title);
            ll = (LinearLayout) view.findViewById(R.id.linearLayout_tag);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }
}
