package com.education.edushare.edushare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.education.edushare.edushare.model.DashBoardModel;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Akash Kabir on 19-11-2017.
 */

public class ProjectViewActivity extends AppCompatActivity {
    DatabaseReference dref, mref;

    @BindView(R.id.txt_title)
    TextView txtTitle;
    @BindView(R.id.editText_tag)
    TextView editTextTag;
    @BindView(R.id.linearLayout_ref)
    LinearLayout linearLayoutRef;
    @BindView(R.id.editText_name)
    TextView editTextName;
    @BindView(R.id.editText_cllg)
    TextView editTextCllg;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.ll_ip)
    LinearLayout llip;
    @BindView(R.id.ll_pdetails)
    LinearLayout linearLayoutDetails;
    @BindView(R.id.img_profile)
    CircleImageView imgProfile;
    @BindView(R.id.txt_details)
    TextView txtDetails;

    public DashBoardModel project;
    public String title;
    LinearLayout linearLayoutTagInTitle, LinearlayoutTaginReferences;
    @BindView(R.id.txt_notes)
    TextView txtNotes;
    @BindView(R.id.linearLayout_mem_ref)
    LinearLayout linearLayoutMemRef;
    @BindView(R.id.tv_team_Size)
    TextView tvTeamSize;
    Boolean starred=false;
    FirebaseUser user;
    @BindView(R.id.img_star)
    ImageView imgStar;
    DatabaseReference cref;
    Typeface typeface2;
    private SharedPreferences Details;
    private SharedPreferences.Editor prefEditor;
    private String userpublickey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        ButterKnife.bind(this);
        typeface2 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        user = FirebaseAuth.getInstance().getCurrentUser();
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);
        init();

        imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!starred) {
                    cref.child("starred").setValue("yes");
                    imgStar.setImageResource(R.drawable.ic_star_yellow);
                    starred=true;
                } else {
                    cref.child("starred").removeValue();
                    starred=false;
                    imgStar.setImageResource(R.drawable.ic_star_grey);
                }
            }
        });
    }



    private void init() {
/*
        txtDetails.setTypeface(typeface2);
        editTextName.setTypeface(typeface2);
        editTextCllg.setTypeface(typeface2);*/
        project = (DashBoardModel) getIntent().getSerializableExtra("project");
        if (project.getUserid().equals(user.getUid()))
            btnConnect.setVisibility(View.INVISIBLE);


        dref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("requests").child(project.getPuid()).child(user.getUid());
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnConnect.setEnabled(false);
                    String key = dataSnapshot.getKey();
                    String value = dataSnapshot.getValue().toString();
                    Log.d("TAG", "key: " + key + " " + value);
                    String status=dataSnapshot.child("status").getValue().toString();
                    if (status.equals("sent"))
                        btnConnect.setText("Requested");
                    else if (status.equals("accepted"))
                        btnConnect.setText("Connected");
                    else if(status.equals("rejected")){
                        btnConnect.setText("Rejected");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        linearLayoutTagInTitle = llip.findViewById(R.id.linearLayout_tag);
        LinearlayoutTaginReferences = linearLayoutDetails.findViewById(R.id.linearLayout_tag);
        cref=FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("starredProjects").child(project.getPuid());
        loadProject();

        cref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    imgStar.setImageResource(R.drawable.ic_star_yellow);
                    starred=true;
                }else{
                    starred=false;
                    imgStar.setImageResource(R.drawable.ic_star_grey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadProject() {
        //**********************************************************************************/
        String SkillsPreferences[] = {"C++", "Java"};
        String Members[] = {"Abhishek khuranna", "Smriti sonal","sample user","sample user"};
        ArrayList<String> TagArray = new ArrayList<>();
        ArrayList<String> References = new ArrayList<>();

        txtTitle.setText(project.getPname());
        txtDetails.setText(project.getPdetails());
        editTextCllg.setText(project.getOrg());
        editTextName.setText(project.getUsername());

        Iterator it = project.getNameref().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.d("TAG", "loadProjectfdafdafdfsf: "+pair.getKey());
            linearLayoutRef.addView(createNewTextViewRef(pair.getValue().toString()));
        }

       Iterator it2 = project.getNametag().entrySet().iterator();
        Log.d("TAG", "loadProject: checking for nametag"+project.getNametag().toString());
        while (it2.hasNext()) {
            Map.Entry pair2 = (Map.Entry) it2.next();
            Log.d("TAG", "loadProject: "+pair2.getKey());
            LinearlayoutTaginReferences.addView(createNewTextView(pair2.getKey().toString()));
        }

        Iterator it3 = project.getMembers().entrySet().iterator();
        Log.d("TAG", "loadProject: checking for members"+project.getMembers().toString());
        while (it3.hasNext()) {
            Map.Entry pair3 = (Map.Entry) it3.next();
            Log.d("TAG", "loadProject: "+pair3.getKey());
            linearLayoutMemRef.addView(createNewTextMember(pair3.getValue().toString(),pair3.getKey().toString()));
        }

        for (String a : SkillsPreferences)
            linearLayoutTagInTitle.addView(createNewTextView(a));

        /*for (String a : Members)
            linearLayoutMemRef.addView(createNewTextMember(a,""));*/
        tvTeamSize.setText("EXPECTED TEAM SIZE: " + project.getTeamsize());
        fetchImage();
    }

    private View createNewTextViewRef(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 5, 5, 5);
        final TextView textView = new TextView(this);
        textView.setPadding(10, 5, 5, 5);
        textView.setBackgroundResource(R.drawable.ref_bkg);
        textView.setLayoutParams(lparams);
        textView.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        textView.setText(text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(textView.getText().toString()));
                startActivity(i);
            }
        });
        return textView;
    }


    private TextView createNewTextView(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 5, 5, 5);
        final TextView textView = new TextView(this);
        textView.setPadding(10, 5, 5, 5);
        textView.setBackgroundResource(R.drawable.tag_bckg);
        textView.setLayoutParams(lparams);
        textView.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        textView.setText(text);
        return textView;
    }

    private LinearLayout customtest(String text) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 5, 5, 5);
        final CustomTv customTv = new CustomTv(this);
        customTv.add(text);
        return customTv;
    }

    private TextView createNewTextMember(final String text, String keymine) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 2, 5, 0);
        final TextView textView = new TextView(this);
        textView.setPadding(10, 5, 5, 5);
        textView.setBackgroundResource(R.drawable.mem_bkg);
        textView.setLayoutParams(lparams);
        textView.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        textView.setText(text);
        textView.setHint(keymine);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key=textView.getText().toString();
                String uid=textView.getHint().toString();
                Intent intent = new Intent(ProjectViewActivity.this, ProfileActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });

        return textView;
    }

    @OnClick({R.id.img_profile, R.id.btn_connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_profile:
                Intent intent = new Intent(ProjectViewActivity.this, ProfileActivity.class);
                intent.putExtra("uid",project.getUserid());
                startActivity(intent);
                break;
            case R.id.btn_connect: {
                userpublickey =Details.getString("publicKeyString","");
                DatabaseReference mMessagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(project.getUserid()).child("NewRequests");
                mref = FirebaseDatabase.getInstance().getReference().child("users").child(project.getUserid()).child("requests").child(project.getPuid()).child(user.getUid());
                HashMap<String, String> val = new HashMap<>();
                HashMap<String, String> val2 = new HashMap<>();
                val.put(user.getUid(), user.getDisplayName());
                val.put("status", "requested");
                val.put("publickey",userpublickey);
                val.put("name",project.getPname());
                val2.put("name",project.getPname());
                val2.put(user.getUid().toString(), project.getUsername());
                val2.put("status", "sent");
                val2.put("publickey",userpublickey);
                FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("requests").child(project.getPuid()).child(user.getUid()).setValue(val2);
                mref.setValue(val);
                btnConnect.setText("Request Sent");
                mMessagesDatabaseReference.child(user.getUid()).setValue("Project request from "+user.getDisplayName());
                break;
            }
        }
    }


    public void fetchImage() {
        //==================================================================*/
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference riversRef = mStorageRef.child(project.getUserid() + ".jpg");
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
