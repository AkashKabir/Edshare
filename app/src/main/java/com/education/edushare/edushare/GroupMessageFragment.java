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

import com.education.edushare.edushare.model.DashBoardModel;
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
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Akash Kabir on 25-12-2017.
 */

public class GroupMessageFragment extends Fragment implements View.OnClickListener {
    View itemview;
    String pname, pdetails, puid, userid, username, notes, org, teamsize, category = "All";
    FirebaseUser user;
    DatabaseReference mref, dref, mdref;
    @BindView(R.id.info_tv)
    TextView infoTv;
    @BindView(R.id.cdinfo)
    CardView cdinfo;
    @BindView(R.id.search_Tv)
    TextView searchTv;
    @BindView(R.id.info_img)
    ImageView infoImg;
    @BindView(R.id.search_et)
    SearchView searchEt;
    @BindView(R.id.searchbox)
    LinearLayout searchbox;
    @BindView(R.id.rv_contacts)
    RecyclerView rvContacts;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    @BindView(R.id.fab)
    ImageView fab;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    Unbinder unbinder;
    Animation animationFadeOut;
    Animation animationFadeIn;
    Animation animationleft;
    Animation animationRight;
    public ArrayList<DashBoardModel> arrayList = new ArrayList<>();
    GroupChatAdapter mAdapter;
    int count;
    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String, String> nameref = new HashMap<>();
    HashMap<String, String> members = new HashMap<String, String>();
    @BindView(R.id.ccll)
    CoordinatorLayout ccll;
    private String key;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_groupchat, container, false);
        unbinder = ButterKnife.bind(this, itemview);

        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
        animationleft=AnimationUtils.loadAnimation(getActivity(),R.anim.rtol);
        animationRight=AnimationUtils.loadAnimation(getActivity(),R.anim.ltor);
        cdinfo.setVisibility(View.GONE);
        ccll.startAnimation(animationRight);

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
                /*HomeActivity.viewPager.setCurrentItem(2);*/
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
        mAdapter = new GroupChatAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.setAdapter(mAdapter);
        user = FirebaseAuth.getInstance().getCurrentUser();

        mAdapter.setOnItemClickListener(new GroupChatAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                TextView textView = (TextView) view.findViewById(R.id.contact_name);
                String title = textView.getText().toString();

                Intent intent = new Intent(getActivity(), GroupMessageActivity.class);
                intent.putExtra("object", arrayList.get(position));
                startActivity(intent);
            }
        });


        fetchProjects();
        /************************************************************/
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

  /*  private void fetchContacts() {
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("projectsInvolved");
        ///////////////////////////////////////////////////////////////////////
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
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
        mref.addChildEventListener(new ChildEventListener() {
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
                    String name=dataSnapshot.child("name").getValue().toString();
                    String uid=dataSnapshot.getKey();
                    arrayList.add(new ChatContactsModel(name, " ", uid, " ", " "));
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
    }*/

    private void fetchProjects() {

        try {
            arrayList.clear();
            mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("projectsInvolved");
            mdref = FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child("All");
            Log.d("TAG", "exists onDataChange: fetch projects");

            mref.addListenerForSingleValueEvent(new ValueEventListener() {
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

            mref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()) {
                        try {
                            tvNothing.setVisibility(View.GONE);
                            key = dataSnapshot.getKey();
                            Log.d("TAG", "aChange: " + key);
                            mdref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        pname = dataSnapshot.child("pname").getValue().toString();
                                        pdetails = dataSnapshot.child("pdetails").getValue().toString();
                                        userid = dataSnapshot.child("userid").getValue().toString();
                                        puid = dataSnapshot.child("puid").getValue().toString();
                                        username = dataSnapshot.child("username").getValue().toString();
                                        notes = dataSnapshot.child("notes").getValue().toString();
                                        org = dataSnapshot.child("org").getValue().toString();
                                        teamsize = dataSnapshot.child("teamsize").getValue().toString();
                                        for (DataSnapshot postSnapshot : dataSnapshot.child("nameref").getChildren()) {
                                            nameref.put(postSnapshot.getKey().toString(), postSnapshot.getValue().toString());
                                        }

                                        for (DataSnapshot postSnapshot : dataSnapshot.child("nametag").getChildren()) {
                                            nametag.put(postSnapshot.getKey().toString(), postSnapshot.getValue().toString());
                                            //  Log.d("TAG", "values" + postSnapshot.getKey() + " " + postSnapshot.getValue() + " " + count);
                                        }

                                        if (dataSnapshot.child("membersInvolved").exists()) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.child("membersInvolved").getChildren()) {
                                                String name = postSnapshot.child(postSnapshot.getKey()).getValue().toString();
                                                members.put(postSnapshot.getKey().toString(), name);
                                            }
                                        }

                                        arrayList.add(new DashBoardModel(pname, pdetails, userid, puid, username, notes, org, teamsize, nametag, nameref, members));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    public void onClick(View view) {

    }
}
