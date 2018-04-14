package com.education.edushare.edushare;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.education.edushare.edushare.Adapters.ProjectsInvolvedAdapter;
import com.education.edushare.edushare.model.DashBoardModel;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
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
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Akash Kabir on 20-11-2017.
 */

public class ProfileFragment extends Fragment {
    View rootview;
    @BindView(R.id.ll_container)
    LinearLayout llContainer;
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
    EditText txtAbout;
    @BindView(R.id.txt_interests)
    EditText txtInterests;
    @BindView(R.id.rv_projects)
    RecyclerView rvProjects;
    @BindView(R.id.tv_about)
    TextView tvAbout;
    @BindView(R.id.tv_edit2)
    TextView tvEdit2;
    @BindView(R.id.tv_interests)
    TextView tvInterests;
    @BindView(R.id.tv_edit)
    TextView tvEdit;
    @BindView(R.id.search_et)
    TextView searchEt;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;
    @BindView(R.id.main_ll)
    LinearLayout mainLl;

    private StorageReference mStorageRef;
    DatabaseReference ref;
    Unbinder unbinder;
    public View itemview;
    public ArrayList<DashBoardModel> arrayList = new ArrayList<>();
    ProjectsInvolvedAdapter mAdapter;
    DatabaseReference mref, pref;
    FirebaseUser user;
    String key, name, designation, skills, about;
    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String, String> nameref = new HashMap<>();
    HashMap<String, String> members = new HashMap<String, String>();
    @BindView(R.id.upload_profile)
    Button uploadProfile;
    @BindView(R.id.logout)
    Button logout;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    private DatabaseReference mDatabase, mdref, namerefdb;
    String pname, pdetails, puid, userid, username, notes, org, teamsize, category = "All";
    Typeface typeface2;
    Typeface typeface;
    int flaguploadbutton1 = 0, flaguploadbutton2 = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootview);

        llContainer.setBackgroundResource(0);
        typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Regular.ttf");
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ShortStack-Regular.ttf");
        tvNothing.setTypeface(typeface);
        txtAbout.setVisibility(View.GONE);
        txtInterests.setVisibility(View.GONE);
        mainLl.setVisibility(View.GONE);
        uploadProfile.setVisibility(View.GONE);

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvEdit.getText().toString().trim().equals("EDIT")) {
                    tvAbout.setVisibility(View.GONE);
                    txtAbout.setVisibility(View.VISIBLE);
                    tvEdit.setText("CANCEL");
                    uploadProfile.setVisibility(View.VISIBLE);
                    flaguploadbutton1 = 1;
                } else if (tvEdit.getText().toString().trim().equals("CANCEL")) {
                    tvAbout.setVisibility(View.VISIBLE);
                    txtAbout.setVisibility(View.GONE);
                    tvEdit.setText("EDIT");
                    if (flaguploadbutton2 == 0) {
                        uploadProfile.setVisibility(View.GONE);
                        flaguploadbutton1 = 0;
                    }

                }

            }
        });

        tvEdit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvEdit2.getText().toString().trim().equals("EDIT")) {
                    tvInterests.setVisibility(View.GONE);
                    txtInterests.setVisibility(View.VISIBLE);
                    tvEdit2.setText("CANCEL");
                    uploadProfile.setVisibility(View.VISIBLE);
                    flaguploadbutton2 = 1;
                } else if (tvEdit2.getText().toString().trim().equals("CANCEL")) {
                    tvInterests.setVisibility(View.VISIBLE);
                    txtInterests.setVisibility(View.GONE);
                    tvEdit2.setText("EDIT");
                    if (flaguploadbutton1 == 0) {
                        uploadProfile.setVisibility(View.GONE);
                        flaguploadbutton2 = 0;
                    }
                }
            }
        });

        init();

        logout.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          getActivity().stopService(new Intent(getActivity(), Notifications.class));
                                          FirebaseAuth.getInstance().signOut();
                                          Intent intent = new Intent(getActivity(), LoginActivity.class);
                                          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                          startActivity(intent);
                                      }
                                  }
        );

        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAbout.setText(txtAbout.getText().toString().trim());
                tvInterests.setText(txtInterests.getText().toString().trim());
                txtAbout.setVisibility(View.GONE);
                txtInterests.setVisibility(View.GONE);
                tvAbout.setVisibility(View.VISIBLE);
                tvInterests.setVisibility(View.VISIBLE);
                flaguploadbutton1 = 0;
                flaguploadbutton2 = 0;
                tvEdit2.setText("EDIT");
                tvEdit.setText("EDIT");
                Toast.makeText(getActivity(), "Uploading profile", Toast.LENGTH_SHORT).show();

                HashMap<String, String> map = new HashMap<>();
                map.put("about", txtAbout.getText().toString().trim());
                map.put("skills", txtInterests.getText().toString().trim());
                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("profile").setValue(map, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        uploadProfile.setVisibility(View.GONE);
                    }
                });
            }
        });

        return rootview;
    }

    private void init() {
       /* tvName.setTypeface(typeface2);
        tvDesignation.setTypeface(typeface2);
        txtAbout.setTypeface(typeface2);
        txtInterests.setTypeface(typeface2);*/
        btnAdd.setVisibility(View.GONE);
        imgMsg.setVisibility(View.GONE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAdapter = new ProjectsInvolvedAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvProjects.setLayoutManager(mLayoutManager);
        rvProjects.setItemAnimator(new DefaultItemAnimator());
        rvProjects.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ProjectsInvolvedAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getActivity(), ProjectViewActivity.class);
                intent.putExtra("project", arrayList.get(position));
                startActivity(intent);
            }
        });
        /***********************************************************/
        fetchProfile();
        /************************************************************/
    }

    private void fetchProfile() {
        fetchProjects();
        fetchImage();
        pref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        pref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("profile").exists()) {
                    about = dataSnapshot.child("profile").child("about").getValue().toString();
                    skills = dataSnapshot.child("profile").child("skills").getValue().toString();
                    txtAbout.setText(about);
                    txtInterests.setText(skills);
                    tvAbout.setText(about);
                    tvInterests.setText(skills);
                }
                name = dataSnapshot.child("name").getValue().toString();
                designation = dataSnapshot.child("org").getValue().toString();
                Log.d("TAG", "onDataChange: " + name + " " + designation);
                tvName.setText(name);
                tvDesignation.setTypeface(typeface);
                tvDesignation.setText(designation);
                pbCon.setVisibility(View.GONE);
                mainLl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void fetchProjects() {
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("projectsInvolved");
        mdref = FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child("All");
        Log.d("TAG", "exists onDataChange: fetch projects");

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    tvNothing.setVisibility(View.VISIBLE);
                } else {
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
    }


    /***********************************************************/
    private void prepareSampleData() {
        mAdapter.notifyDataSetChanged();
    }

    /***********************************************************/
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void fetchImage() {
        //==================================================================*/
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(user.getUid() + ".jpg");
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
