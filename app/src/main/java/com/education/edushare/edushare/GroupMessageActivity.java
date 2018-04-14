package com.education.edushare.edushare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.model.DashBoardModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 25-12-2017.
 */

public class GroupMessageActivity extends AppCompatActivity {


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.tv_new_txt)
    EditText tvNewTxt;
    @BindView(R.id.img_send_msg)
    ImageView imgSendMsg;
    @BindView(R.id.ll_new_msg)
    LinearLayout llNewMsg;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    @BindView(R.id.linearLayout_tag)
    LinearLayout linearLayoutTag;
    @BindView(R.id.show_mem)
    TextView showMem;

    private SharedPreferences Details;
    public ArrayList<Groupchatmodel> arrayList = new ArrayList<>();
    GroupMessageAdapter mAdapter;
    String uid, title;
    FirebaseUser user;
    public String EncrText;
    DatabaseReference dref, mref, fref;
    String aes, publicsu;
    DashBoardModel object;
    Typeface typeface;
    String userprivateKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grp);
        ButterKnife.bind(this);
        ButterKnife.bind(this);
        Typeface typeface2 = Typeface.createFromAsset(getAssets(), "fonts/ShortStack-Regular.ttf");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        object = (DashBoardModel) getIntent().getSerializableExtra("object");
        init();

        Iterator it = object.getMembers().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            linearLayoutTag.addView(createNewTextView(pair.getValue().toString(), pair.getKey().toString()));
        }

        linearLayoutTag.setVisibility(View.GONE);
        tvInfo.setVisibility(View.GONE);
        tvInfo.setTypeface(typeface2);
        tvNewTxt.setTypeface(typeface2);
        tvTitle.setTypeface(typeface2);
        tvNothing.setTypeface(typeface2);
        fetchMessages();

        showMem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linearLayoutTag.getVisibility()==View.VISIBLE)
                    linearLayoutTag.setVisibility(View.GONE);
                else
                    linearLayoutTag.setVisibility(View.VISIBLE);
            }
        });

        imgSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvNewTxt.getText().toString().trim().length() > 0) {
                    //encrypt the message first
                    sendMessage(tvNewTxt.getText().toString().trim());
                    tvNewTxt.setText("");
                }
            }
        });

    }


    private void sendMessage(String trim) {
        Groupchatmodel m = new Groupchatmodel(user.getDisplayName(), user.getUid(), trim);
        dref = FirebaseDatabase.getInstance().getReference().child("projectgroupchat").child(object.getPuid()).child("messages");
        dref.push().setValue(m);
    }

    private void fetchMessages() {
        fref = FirebaseDatabase.getInstance().getReference().child("projectgroupchat").child(object.getPuid()).child("messages");
        fref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    tvNothing.setVisibility(View.VISIBLE);
                else
                    tvNothing.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        fref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                tvNothing.setVisibility(View.GONE);
                Groupchatmodel m = dataSnapshot.getValue(Groupchatmodel.class);
                Log.d("TAG", "GROUP MESSAGE: " + m.getSenuid() + " " + m.getSenname() + " " + m.getMsgtxt());
                arrayList.add(m);
                mAdapter.notifyItemInserted(arrayList.size() - 1);
                rvMsg.smoothScrollToPosition(arrayList.size() - 1);
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
        Details = getSharedPreferences("USERDATA", MODE_PRIVATE);
        title = /*getIntent().getStringExtra("title");*/ object.getPname();
        user = FirebaseAuth.getInstance().getCurrentUser();
        tvTitle.setText(title);
        mAdapter = new GroupMessageAdapter(this, arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMsg.setLayoutManager(mLayoutManager);
        rvMsg.setItemAnimator(new DefaultItemAnimator());
        rvMsg.setAdapter(mAdapter);
    }

    private TextView createNewTextView(final String text, String key) {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(5, 5, 5, 5);
        final TextView textView = new TextView(this);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setPadding(10, 5, 5, 5);
        textView.setBackgroundResource(R.drawable.tag_bckg);
        textView.setLayoutParams(lparams);
        textView.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
        textView.setText(text);
        textView.setHint(key);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = textView.getText().toString();
                String uid = textView.getHint().toString();
                Intent intent = new Intent(GroupMessageActivity.this, ProfileActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        return textView;
    }
}
