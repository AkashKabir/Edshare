package com.education.edushare.edushare;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.edushare.edushare.Adapters.RequestAdapter;
import com.education.edushare.edushare.model.RequestModel;
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
 * Created by Akash Kabir on 10-12-2017.
 */

public class RequestFragment extends Fragment {
    String requid, reqpuid, reqname, status;
    View itemview;
    @BindView(R.id.rv_dash)
    RecyclerView rvDash;
    @BindView(R.id.pb_dash)
    DotProgressBar pbDash;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    Unbinder unbinder;
    ArrayList<RequestModel> arrayList = new ArrayList<>();
    RequestAdapter mAdapter;
    DatabaseReference dref;
    FirebaseUser user;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;
    int count = 0;
    @BindView(R.id.search_et)
    TextView searchEt;
    String name;
    String publickey;
    @BindView(R.id.info_tv)
    TextView infoTv;
    @BindView(R.id.info_img)
    ImageView infoImg;
    Animation animationFadeOut;
    Animation animationFadeIn;
    @BindView(R.id.cdinfo)
    CardView cdinfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_community, container, false);
        unbinder = ButterKnife.bind(this, itemview);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAdapter = new RequestAdapter(getActivity(), arrayList);
        cdinfo.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvDash.setLayoutManager(mLayoutManager);
        rvDash.setItemAnimator(new DefaultItemAnimator());
        rvDash.setAdapter(mAdapter);
        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);


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

        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ShortStack-Regular.ttf");
        tvNothing.setTypeface(typeface2);
        infoTv.setTypeface(typeface2);
        searchEt.setTypeface(typeface2);
        checkforRequests();
        return itemview;
    }

    private void checkforRequests() {
        pbDash.setVisibility(View.GONE);
        Log.d("TAG", "checkforRequests: ");
        dref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("requests");
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

        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("TAG", "into child added: ");
                count++;
                Log.d("TAG", "on Child Added " + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNothing.setVisibility(View.VISIBLE);
                } else if (count >= dataSnapshot.getChildrenCount()) {
                    //stop progress bar here
                    /*pbCon.setVisibility(View.GONE);*/
                    // rvContacts.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.exists()) {
                    try {
                        //pbDash.setVisibility(View.GONE);
                        tvNothing.setVisibility(View.GONE);
                        llContainer.setBackgroundResource(0);
                        Log.d("TAG", "exists: ");

                        reqpuid = dataSnapshot.getKey();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            requid = postSnapshot.getKey().toString();
                            name = postSnapshot.child("name").getValue().toString();
                            reqname = postSnapshot.child(requid).getValue().toString();
                            status = postSnapshot.child("status").getValue().toString();
                            publickey = postSnapshot.child("publickey").getValue().toString();
                            arrayList.add(new RequestModel(requid, reqpuid, reqname, status, name, publickey));
                            mAdapter.notifyDataSetChanged();
                            Log.d("TAG", "onChildAdded: " + requid + " " + reqname + " " + reqpuid);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "No Requests yet", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
