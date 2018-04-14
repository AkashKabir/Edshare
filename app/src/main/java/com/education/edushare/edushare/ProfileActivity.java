package com.education.edushare.edushare;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.education.edushare.edushare.Adapters.ProjectsInvolvedAdapter;
import com.education.edushare.edushare.model.DashBoardModel;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_designation)
    TextView tvDesignation;
    @BindView(R.id.btn_add)
    ImageView btnAdd;
    @BindView(R.id.img_msg)
    ImageView imgMsg;
    @BindView(R.id.txt_about)
    TextView txtAbout;
    @BindView(R.id.txt_interests)
    TextView txtInterests;
    @BindView(R.id.rv_projects)
    RecyclerView rvProjects;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
    public ArrayList<DashBoardModel> arrayList = new ArrayList<>();
    ProjectsInvolvedAdapter mAdapter;
    @BindView(R.id.upload_profile)
    Button uploadProfile;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    private StorageReference mStorageRef;
    DatabaseReference ref, pref;
    String TagArray[] = {"a", "b", "c", "Artificial Intelligence", "google india how are you"};
    String uid;
    Typeface typeface;
    FirebaseUser user;

    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String ,String > members=new HashMap<String, String>();
    HashMap<String, String> nameref = new HashMap<>();
    private DatabaseReference mDatabase, mref, mdref, namerefdb;
    String key, name, designation, skills, about;
    Boolean starred = false;
    String pname, pdetails, puid, userid, username, notes, org, teamsize, category = "All";
    Typeface typeface2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        typeface2 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        uid = getIntent().getStringExtra("uid");
        typeface= Typeface.createFromAsset(getAssets(), "fonts/ShortStack-Regular.ttf");
        tvNothing.setTypeface(typeface);
        tvDesignation.setTypeface(typeface);
        init();
        uploadProfile.setVisibility(View.GONE);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!starred) {
                    ref.child("starred").setValue("yes");
                    btnAdd.setImageResource(R.drawable.ic_star_yellow);
                    starred = false;
                } else {
                    ref.child("starred").removeValue();
                    starred = true;
                    btnAdd.setImageResource(R.drawable.ic_star_grey);
                }
            }
        });
        loadProfile(uid);
    }

    private void init() {
      /*  tvName.setTypeface(typeface2);
        tvDesignation.setTypeface(typeface2);
        txtAbout.setTypeface(typeface2);
        txtInterests.setTypeface(typeface2);*/
        llContainer.setBackgroundResource(0);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAdapter = new ProjectsInvolvedAdapter(this, arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvProjects.setLayoutManager(mLayoutManager);
        rvProjects.setItemAnimator(new DefaultItemAnimator());
        rvProjects.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ProjectsInvolvedAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(ProfileActivity.this, ProjectViewActivity.class);
                intent.putExtra("project", arrayList.get(position));
                startActivity(intent);
            }
        });
        /***********************************************************/
        prepareSampleData();
        fetchProfile();
        /************************************************************/
        ref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("starredUsers").child(uid);
        if (uid.equals(user.getUid())) {
            btnAdd.setVisibility(View.GONE);
            imgMsg.setVisibility(View.GONE);
        } else {

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        btnAdd.setImageResource(R.drawable.ic_micus);
                        starred = true;
                    } else {
                        starred = false;
                        btnAdd.setImageResource(R.drawable.ic_star_grey);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void fetchProfile() {
        pref = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        pref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profile").exists()) {
                    about = dataSnapshot.child("profile").child("about").getValue().toString();
                    skills = dataSnapshot.child("profile").child("about").getValue().toString();
                    txtAbout.setText(about);
                    txtInterests.setText(skills);
                } else {
                    txtAbout.setHint("not provided");
                    txtInterests.setHint("not provied");
                }
                name = dataSnapshot.child("name").getValue().toString();
                designation = dataSnapshot.child("org").getValue().toString();
                tvName.setText(name);
                tvDesignation.setText(designation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        fetchProjects();
    }

    private void fetchProjects() {
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("projectsInvolved");
        mdref = FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child("All");

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    tvNothing.setVisibility(View.VISIBLE);
                }else{
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

                                if(dataSnapshot.child("membersInvolved").exists()){
                                    for (DataSnapshot postSnapshot: dataSnapshot.child("membersInvolved").getChildren()){
                                        String name=postSnapshot.child(postSnapshot.getKey()).getValue().toString();
                                        members.put(postSnapshot.getKey().toString(),name);
                                    }
                                }

                                arrayList.add(new DashBoardModel(pname, pdetails, userid, puid, username, notes, org, teamsize, nametag, nameref,members));
                                mAdapter.notifyDataSetChanged();
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

 /*       mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    key = dataSnapshot.getKey();
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
                                arrayList.add(new DashBoardModel(pname, pdetails, userid, puid, username, notes, org, teamsize, nametag, nameref));
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    /***********************************************************/
    private void prepareSampleData() {
       /* arrayList.add(new DashBoardModel("hello it is amazing",TagArray,"1","g","a","hello"));
        arrayList.add(new DashBoardModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.2",TagArray,"1","a","a","hello"));
        arrayList.add(new DashBoardModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.3",TagArray,"1","b","a","hello"));
        arrayList.add(new DashBoardModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.4",TagArray,"1","c","a","hello"));
        arrayList.add(new DashBoardModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.5",TagArray,"1","d","a","hello"));
        arrayList.add(new DashBoardModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.6",TagArray,"1","e","a","hello"));
        arrayList.add(new DashBoardModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.7",TagArray,"1","f","a","hello"));
        madapter.notifyDataSetChanged();*/
    }

    /***********************************************************/
    private void loadProfile(String uid) {
        fetchImage();
    }

    public void fetchImage() {
        //==================================================================*/
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(uid + ".jpg");
        SimpleTarget target = new SimpleTarget<GlideBitmapDrawable>() {
            @Override
            public void onResourceReady(GlideBitmapDrawable bitmap, GlideAnimation glideAnimation) {
                imgProfile.setImageDrawable(bitmap);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                imgProfile.setImageResource(R.drawable.default2);
            }
        };
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(riversRef).signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .into(target);
    }
}
