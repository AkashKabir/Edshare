package com.education.edushare.edushare;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.edushare.edushare.Adapters.StarredProjectsAdapter;
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
 * Created by Akash Kabir on 26-11-2017.
 */


public class FavProjectFragment extends Fragment {

    @BindView(R.id.search_et)
    SearchView searchEt;
    @BindView(R.id.rv_dash)
    RecyclerView rvDash;
    @BindView(R.id.ll_container)
    CoordinatorLayout ll_Container;
    Unbinder unbinder;
    StarredProjectsAdapter madapter;
    DatabaseReference mref;
    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String, String> nameref = new HashMap<>();
    HashMap<String ,String > members=new HashMap<String, String>();
    @BindView(R.id.searchbar)
    LinearLayout searchbar;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;

    @BindView(R.id.pb_con)
    DotProgressBar pbCon;

    @BindView(R.id.main_ll)
    LinearLayout mainLl;
    private DatabaseReference mDatabase, mdref, namerefdb;
    String pname, pdetails, puid, userid, username, notes, org, teamsize, category = "All";
    public View itemview;
    public ArrayList<DashBoardModel> arrayList = new ArrayList<>();
    FirebaseUser user;
    String key;
    int count = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_dashboard2, container, false);
        unbinder = ButterKnife.bind(this, itemview);
        init();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("starredProjects");
        fetchData();
        return itemview;
    }

    private void fetchData() {
        arrayList.clear();
        ll_Container.setBackgroundResource(0);
        mdref = FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child("All");
        Log.d("TAG", "exists onDataChange: fetch projects");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    pbCon.setVisibility(View.GONE);
                    tvNothing.setVisibility(View.VISIBLE);
                }else{
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
                count++;
                Log.d("TAG", "on Child Added " + dataSnapshot.getChildrenCount());
                if (count >= dataSnapshot.getChildrenCount()) {
                    //stop progress bar here
                    pbCon.setVisibility(View.GONE);
                    //rvContacts.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.exists()) {
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
                                mDatabase = mdref.child(dataSnapshot.getKey()).child("nameref");

                                for (DataSnapshot postSnapshot : dataSnapshot.child("nameref").getChildren()) {
                                    nameref.put(postSnapshot.getKey().toString(), postSnapshot.getValue().toString());
                                }

                                for (DataSnapshot postSnapshot : dataSnapshot.child("nametag").getChildren()) {
                                    nametag.put(postSnapshot.getKey().toString(), postSnapshot.getValue().toString());
                                    //  Log.d("TAG", "values" + postSnapshot.getKey() + " " + postSnapshot.getValue() + " " + count);
                                }

                                if(dataSnapshot.child("membersInvolved").exists()){
                                    for (DataSnapshot postSnapshot: dataSnapshot.child("membersInvolved").getChildren()){
                                        String name=postSnapshot.child(postSnapshot.getKey()).getValue().toString();
                                        members.put(postSnapshot.getKey().toString(),name);
                                    }
                                }

                                arrayList.add(new DashBoardModel(pname, pdetails, userid, puid, username, notes, org, teamsize, nametag, nameref,members));
                                madapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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

    private void init() {
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ShortStack-Regular.ttf");
        tvNothing.setTypeface(typeface2);
        searchbar.setVisibility(View.GONE);
        madapter = new StarredProjectsAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvDash.setLayoutManager(mLayoutManager);
        rvDash.setItemAnimator(new DefaultItemAnimator());
        rvDash.setAdapter(madapter);

        madapter.setOnItemClickListener(new StarredProjectsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getActivity(), ProjectViewActivity.class);
                intent.putExtra("project", arrayList.get(position));
                startActivity(intent);
            }
        });

        /*********************************************/

        /************************************************************/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
