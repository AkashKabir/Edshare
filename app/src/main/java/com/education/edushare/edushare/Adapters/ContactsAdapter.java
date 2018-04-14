package com.education.edushare.edushare.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.education.edushare.edushare.R;
import com.education.edushare.edushare.model.ChatContactsModel;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Akash Kabir on 21-11-2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private static ClickListener clickListener;
    ArrayList<ChatContactsModel> arrayList=new ArrayList<>();
    Context context;
    Typeface typeface ;

    public ContactsAdapter(Context context, ArrayList<ChatContactsModel> arrayList){
        this.context=context;
        this.arrayList=arrayList;
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ShortStack-Regular.ttf");

    }

    @Override
    public ContactsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.MyViewHolder holder, int position) {
        holder.title.setText(arrayList.get(position).getName());
        holder.desc.setVisibility(View.GONE);
        holder.title.setTypeface(typeface);
        fetchImage(holder,position);
      /*  holder.title.setTypeface(typeface);
        holder.desc.setTypeface(typeface);*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        ContactsAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        public TextView title,desc;
        public CircleImageView image;
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


    public void fetchImage(final MyViewHolder holder,int pos) {
        //==================================================================*/
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(arrayList.get(pos).getUid() + ".jpg");
        SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
                holder.image.setImageDrawable(bitmap);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                holder.image.setImageResource(R.drawable.default2);
            }
        };
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(riversRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .into(target);
    }
}
