package com.education.edushare.edushare;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.education.edushare.edushare.Adapters.ForumDetailsAdapter;
import com.education.edushare.edushare.model.ForumMessageModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 26-11-2017.
 */

public class ForumDetailsActivity extends AppCompatActivity {
    @BindView(R.id.tv_topic)
    TextView tvTopic;
    @BindView(R.id.tv_details)
    TextView tvDetails;
    @BindView(R.id.img_upvotes)
    ImageView imgUpvotes;
    @BindView(R.id.tv_upvotes)
    TextView tvUpvotes;
    @BindView(R.id.ll_upvotes)
    LinearLayout llUpvotes;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_name)
    LinearLayout llName;
    @BindView(R.id.ll_action)
    LinearLayout llAction;
    @BindView(R.id.img_stars)
    ImageView imgStars;
    @BindView(R.id.tv_stars)
    TextView tvStars;
    @BindView(R.id.ll_stars)
    LinearLayout llStars;
    @BindView(R.id.tv_org)
    TextView tvOrg;
    @BindView(R.id.ll_org)
    LinearLayout llOrg;
    @BindView(R.id.ll_down)
    LinearLayout llDown;
    @BindView(R.id.rv_forum)
    RecyclerView rvForum;

    public ArrayList<ForumMessageModel> arrayList=new ArrayList<>();
    ForumDetailsAdapter mAdapter;
    public String title,uid;
    int stars,upvotes;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_view);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        /*Intent rintent=getIntent();
        title=rintent.getStringExtra("title");
        uid=rintent.getStringExtra("uid");
        stars=rintent.getIntExtra("stars",0);
        upvotes=rintent.getIntExtra("upvotes",0);
        tvStars.setText(Integer.toString(stars));
        tvUpvotes.setText(Integer.toString(upvotes));
        tvTopic.setText(title);*/

        mAdapter = new ForumDetailsAdapter(this,arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvForum.setLayoutManager(mLayoutManager);
        rvForum.setItemAnimator(new DefaultItemAnimator());
        rvForum.setAdapter(mAdapter);

        /***********************************************************/
        prepareSampleData();
        /************************************************************/
    }
    /***********************************************************/
    private void prepareSampleData() {
        arrayList.add(new ForumMessageModel("uid", "Looking at these people! I can't stop laughing. I am zeus. ", "akash kabir",8000000, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "I am the richest man on the earth", "jeff Bezos",89, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "I have too much free time. I am awesome because i am the founder of google", "sergey brin", 666, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "what can i say ? you already know me..hey sergey! you suck", "larry page", 666, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "I need your personal data fassst. I am going to show it to my wife.", "mark zuckerberg",-20, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "Girls girls girls ...woooh I am a playboy ..Jealous ! aren't you. I am weird", "elon musk", 85, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "What can i say ! I am just changing the world..Real simple and amazing guy", "bill gates", 100000, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "wooh singles day! I hope everyone could be single.", "jack ma", -8, 0, "hello world", "nothing is in here"));
        arrayList.add(new ForumMessageModel("uid", "I am the fiance of a billionaire and i am hot too.", "Miranda kerr", 1000, 0, "hello world", "nothing is in here"));
        mAdapter.notifyDataSetChanged();
    }
    /***********************************************************/

}
