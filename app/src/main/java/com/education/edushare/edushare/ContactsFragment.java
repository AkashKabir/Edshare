package com.education.edushare.edushare;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.Adapters.ContactsAdapter;
import com.education.edushare.edushare.model.ChatContactsModel;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Akash Kabir on 20-11-2017.
 */

public class ContactsFragment extends Fragment implements View.OnClickListener {
    View itemview;
    FirebaseUser user;
    DatabaseReference mref, dref;
    @BindView(R.id.rv_contacts)
    RecyclerView rvContacts;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;
    Unbinder unbinder;
    @BindView(R.id.ll_container)
    LinearLayout ll_Container;
    public ArrayList<ChatContactsModel> arrayList = new ArrayList<>();
    ContactsAdapter mAdapter;
    @BindView(R.id.search_et)
    SearchView searchEt;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    int count;
    @BindView(R.id.info_tv)
    TextView infoTv;
    @BindView(R.id.info_img)
    ImageView infoImg;
    Animation animationFadeOut;
    Animation animationFadeIn;
    @BindView(R.id.cdinfo)
    CardView cdinfo;
    @BindView(R.id.fab)
    ImageView fab;
    @BindView(R.id.ccll)
    CoordinatorLayout ccll;
    Animation animationleft;
    Animation animationRight;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_contacts, container, false);
        unbinder = ButterKnife.bind(this, itemview);
        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
        animationleft=AnimationUtils.loadAnimation(getActivity(),R.anim.rtol);
        animationRight=AnimationUtils.loadAnimation(getActivity(),R.anim.ltor);
        ccll.startAnimation(animationRight);
        cdinfo.setVisibility(View.GONE);
        infoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cdinfo.getVisibility() == View.GONE) {
                    cdinfo.setVisibility(View.VISIBLE);
                    infoTv.startAnimation(animationFadeIn);

                } else {
                    infoTv.startAnimation(animationFadeOut);
                    cdinfo.setVisibility(View.GONE);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                HomeActivity.viewPager.setCurrentItem(5);*/
                    HomeActivity.ChangeFragment();

            }
        });

        init();
        return itemview;
    }

    private void init() {
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ShortStack-Regular.ttf");
        tvNothing.setTypeface(typeface2);
        infoTv.setTypeface(typeface2);
        mAdapter = new ContactsAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.setAdapter(mAdapter);
        user = FirebaseAuth.getInstance().getCurrentUser();

        mAdapter.setOnItemClickListener(new ContactsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                TextView textView = (TextView) view.findViewById(R.id.contact_name);
                String title = textView.getText().toString();

                Intent intent = new Intent(getActivity(), ChatActivity.class);
               /* intent.putExtra("title", title);
                intent.putExtra("uid", arrayList.get(position).getUid());*/
                intent.putExtra("object", arrayList.get(position));
                startActivity(intent);
            }
        });

        /***********************************************************/
        //  prepareSampleData();
        fetchContacts();
        /************************************************************/
    }


    private void fetchContacts() {
       try {
           arrayList.clear();
           dref = FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(user.getUid()).child("members");
           dref.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if (!dataSnapshot.exists()) {
                       pbCon.setVisibility(View.GONE);
                       tvNothing.setVisibility(View.VISIBLE);
                   } else {
                       pbCon.setVisibility(View.GONE);
                       tvNothing.setVisibility(View.GONE);
                   }

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
           count = 0;
           dref.addChildEventListener(new ChildEventListener() {
               @Override
               public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                   count++;
                   Log.d("TAG", "on Child Added " + dataSnapshot.getChildrenCount());
                   if (dataSnapshot.getChildrenCount() == 0) {
                       tvNothing.setVisibility(View.VISIBLE);
                   } else if (count >= dataSnapshot.getChildrenCount()) {
                       //stop progress bar here
                       pbCon.setVisibility(View.GONE);
                       rvContacts.setVisibility(View.VISIBLE);
                   }

                   if (dataSnapshot.exists()) {
                       String name = dataSnapshot.child("name").getValue().toString();
                       String uid = dataSnapshot.getKey();
                       String type = dataSnapshot.child("type").getValue().toString();
                       String Aeskey = dataSnapshot.child("key").getValue().toString();
                       String publickeysu = dataSnapshot.child("publicKey").getValue().toString();
                       Log.d("TAG", "onChildAdded: " + publickeysu + " " + type);
                       arrayList.add(new ChatContactsModel(name, type, uid, publickeysu, Aeskey));
                       mAdapter.notifyDataSetChanged();
                   }
               }

               @Override
               public void onChildChanged(DataSnapshot dataSnapshot, String s) {

               }

               @Override
               public void onChildRemoved(DataSnapshot dataSnapshot) {

               }

               @Override
               public void onChildMoved(DataSnapshot dataSnapshot, String s) {

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
       }catch (Exception e){
           e.printStackTrace();
       }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {

    }
}
