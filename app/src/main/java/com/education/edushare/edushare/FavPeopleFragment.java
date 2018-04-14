package com.education.edushare.edushare;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Created by Akash Kabir on 26-11-2017.
 */

public class FavPeopleFragment extends Fragment {
    @BindView(R.id.search_et)
    SearchView searchEt;
    @BindView(R.id.rv_contacts)
    RecyclerView rvContacts;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    Unbinder unbinder;
    View itemview;
    FirebaseUser user;
    String key;
    DatabaseReference mref, dref;
    FavPeopleAdapter mAdapter;
    public ArrayList<ChatContactsModel> arrayList = new ArrayList<>();
    @BindView(R.id.searchbox)
    LinearLayout searchbox;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    int count = 0;
    @BindView(R.id.cdinfo)
    CardView cdinfo;
    @BindView(R.id.fab)
    ImageView fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_contacts, container, false);
        unbinder = ButterKnife.bind(this, itemview);
        init();
        fab.setVisibility(View.GONE);
        return itemview;
    }

    private void init() {
        Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ShortStack-Regular.ttf");
        tvNothing.setTypeface(typeface2);
        searchbox.setVisibility(View.GONE);
        cdinfo.setVisibility(View.GONE);
        mAdapter = new FavPeopleAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvContacts.setLayoutManager(mLayoutManager);
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.setAdapter(mAdapter);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("starredUsers");

        mAdapter.setOnItemClickListener(new FavPeopleAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                TextView textView = (TextView) view.findViewById(R.id.contact_name);
                String title = textView.getText().toString();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
              /*  intent.putExtra("title", title);
                intent.putExtra("uid", arrayList.get(position).getUid());*/
                intent.putExtra("object", arrayList.get(position));
                startActivity(intent);
            }
        });

        /***********************************************************/
        //  prepareSampleData();
        fetchFavContacts();
        /************************************************************/
    }

    private void fetchFavContacts() {
/*        dref = FirebaseDatabase.getInstance().getReference().child("users");*/
        dref = FirebaseDatabase.getInstance().getReference().child("CHAT").child("coach").child(user.getUid()).child("members");
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
                count++;
                Log.d("TAG", "on Child Added " + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() == 0) {
                    tvNothing.setVisibility(View.VISIBLE);
                } else if (count >= dataSnapshot.getChildrenCount()) {
                    //stop progress bar here
                    pbCon.setVisibility(View.GONE);
                    // rvContacts.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.exists()) {
                    tvNothing.setVisibility(View.GONE);
                    key = dataSnapshot.getKey();
                    dref.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String uid = dataSnapshot.getKey();
                                String type = dataSnapshot.child("type").getValue().toString();
                                String Aeskey = dataSnapshot.child("key").getValue().toString();
                                String publickeysu = dataSnapshot.child("publicKey").getValue().toString();
                                arrayList.add(new ChatContactsModel(name, type, uid, publickeysu, Aeskey));
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Log.d("TAG", "no fav project");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchFavContacts();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                fetchFavContacts();
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
