package com.education.edushare.edushare.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.DashBoardModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Akash Kabir on 21-11-2017.
 */

public class DashBoardAdapter extends RecyclerView.Adapter<DashBoardAdapter.MyViewHolder> implements Filterable {
    ArrayList<DashBoardModel> arrayList=new ArrayList<>();
    ArrayList<DashBoardModel> backuParrayList=new ArrayList<>();
    Context context;
    Typeface typeface ;
    private static ClickListener clickListener;
    public ArrayList<DashBoardModel> contactListFiltered=new ArrayList<>();

   public DashBoardAdapter(Context context, ArrayList<DashBoardModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
        contactListFiltered=arrayList;
        backuParrayList=arrayList;
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/OpenSans-Regular.ttf");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getPname());
       /* holder.title.setTypeface(typeface);*/
        Iterator it = arrayList.get(position).getNametag().entrySet().iterator();
        Log.d("TAG", "onBindViewHolder: before doing this: "+arrayList.get(position).getNametag().toString());
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            holder.ll.addView(createNewTextView(pair.getKey().toString()));
        }
        Log.d("TAG", "onBindViewHolder: after doing this: "+arrayList.get(position).getNametag().toString());
    }

    public void setOnItemClickListener(DashBoardAdapter.ClickListener clickListener) {
        DashBoardAdapter.clickListener = clickListener;
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = backuParrayList;
                } else {
                    ArrayList<DashBoardModel> filteredList = new ArrayList<>();
                    for (DashBoardModel row : arrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        Log.d("TAG", "performFiltering: "+row.getPname()+" "+charSequence);
                        if (row.getPname().toLowerCase().contains(charString.toLowerCase()) || row.getNametag().get(charSequence)!=null) {
                            Log.d("TAG", "performFiltering: into ifffffffff ");
                            filteredList.add(row);
                            Log.d("TAG", "performFiltering: ADDED"+row.getPname()+" "+charString);
                        }else{
                            Log.d("TAG", "not in iffffffffffffffffff performFiltering: ");
                        }

                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                arrayList = (ArrayList<DashBoardModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title;
        public LinearLayout ll;
        public MyViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(R.id.txt_title);
            ll=(LinearLayout) view.findViewById(R.id.linearLayout_tag);
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }
}
