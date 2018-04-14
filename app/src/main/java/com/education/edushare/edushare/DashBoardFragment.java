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

import com.education.edushare.edushare.Adapters.DashBoardAdapter;
import com.education.edushare.edushare.model.DashBoardModel;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
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
 * Created by Akash Kabir on 20-11-2017.
 */

public class DashBoardFragment extends Fragment {
    public View itemview;
    public ArrayList<DashBoardModel> arrayList = new ArrayList<>();
    int count;
    @BindView(R.id.fab)
    ImageView fab;
    Unbinder unbinder;
    @BindView(R.id.rv_dash)
    RecyclerView rvDash;
    @BindView(R.id.ll_container)
    CoordinatorLayout ll_Container;
    DashBoardAdapter mAdapter;

    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String, String> nameref = new HashMap<>();
    @BindView(R.id.main_ll)
    LinearLayout mainLl;
    ImageView serach;
    @BindView(R.id.search_et)
    SearchView searchEt;
    @BindView(R.id.img_search)
    ImageView imgSearch;
    @BindView(R.id.searchbar)
    LinearLayout searchbar;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;
    @BindView(R.id.search_Tv)
    TextView searchTv;
    @BindView(R.id.info_img)
    ImageView infoImg;
    Animation animationFadeOut;
    Animation animationFadeIn;
    @BindView(R.id.info_tv)
    TextView infoTv;
    @BindView(R.id.cdinfo)
    CardView cdinfo;
    private DatabaseReference mDatabase, mdref, namerefdb;
    String pname, pdetails, puid, userid, username, notes, org, teamsize, category = "All";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mainLl = itemview.findViewById(R.id.main_ll);
        unbinder = ButterKnife.bind(this, itemview);
        cdinfo.setVisibility(View.GONE);
        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);
        init();

        searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEt.setIconified(false);
            }
        });

        searchEt.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchEt.getVisibility() == View.GONE) {
                    searchTv.setVisibility(View.GONE);
                    infoImg.setVisibility(View.GONE);
                    searchEt.setVisibility(View.VISIBLE);
                    searchEt.startAnimation(animationFadeIn);
                } else {
                    searchTv.setVisibility(View.VISIBLE);
                    infoImg.setVisibility(View.VISIBLE);
                    searchEt.setVisibility(View.GONE);
                    searchEt.startAnimation(animationFadeOut);
                }
            }
        });


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

        return itemview;
    }


    private void fetchData() {


        arrayList.clear();
        count = 0;
        ll_Container.setBackgroundResource(0);

        mdref = FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child("All");

        mdref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    pbCon.setVisibility(View.GONE);
                    tvNothing.setVisibility(View.VISIBLE);
                } else {
                    tvNothing.setVisibility(View.GONE);
                    pbCon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mdref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                count++;
                Log.d("TAG", "on Child Added " + dataSnapshot.getChildrenCount());

                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNothing.setVisibility(View.VISIBLE);
                } else if (count >= dataSnapshot.getChildrenCount()) {
                    //stop progress bar here
                    pbCon.setVisibility(View.GONE);
                    //rvContacts.setVisibility(View.VISIBLE);
                }


                if (dataSnapshot.exists()) {
                    tvNothing.setVisibility(View.GONE);
                    HashMap<String, String> nametag = new HashMap<>();
                    HashMap<String, String> nameref = new HashMap<>();
                    HashMap<String, String> members = new HashMap<String, String>();
                    pname = dataSnapshot.child("pname").getValue().toString();
                    pdetails = dataSnapshot.child("pdetails").getValue().toString();
                    userid = dataSnapshot.child("userid").getValue().toString();
                    puid = dataSnapshot.child("puid").getValue().toString();
                    username = dataSnapshot.child("username").getValue().toString();
                    notes = dataSnapshot.child("notes").getValue().toString();
                    org = dataSnapshot.child("org").getValue().toString();
                    teamsize = dataSnapshot.child("teamsize").getValue().toString();

                    //  mDatabase = mdref.child(dataSnapshot.getKey()).child("nameref");

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

                    //Log.d("TAG", "hts " + nameref.toString() + " " + nametag.toString());
                    DashBoardModel m = new DashBoardModel(pname, pdetails, userid, puid, username, notes, org, teamsize, nametag, nameref, members);
                    arrayList.add(m);
                    Log.d("TAG", "onChildAdded: " + m.toS());
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
        //stop progress bar here
        mainLl.setBackgroundResource(0);
    }

    private void init() {
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ShortStack-Regular.ttf");
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        tvNothing.setTypeface(typeface2);
        infoTv.setTypeface(typeface2);
        searchTv.setTypeface(typeface2);
        mAdapter = new DashBoardAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvDash.setLayoutManager(mLayoutManager);
        rvDash.setItemAnimator(new DefaultItemAnimator());
        rvDash.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DashBoardAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getActivity(), ProjectViewActivity.class);
                intent.putExtra("project", arrayList.get(position));
                Log.d("TAG", "onItemClick: " + arrayList.get(position).getNametag().toString() + " nameref " + arrayList.get(position).getNameref().toString());
                startActivity(intent);
            }
        });


        /*********************************************/
        fetchData();
        /************************************************************/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewProjectActivity.class);
                intent.putExtra("type", "project");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
