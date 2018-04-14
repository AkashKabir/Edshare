package com.education.edushare.edushare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.education.edushare.edushare.Adapters.CommunityAdapter;
import com.education.edushare.edushare.model.CommunityModel;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Akash Kabir on 21-11-2017.
 */

public class CommunityFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.rv_dash)
    RecyclerView rvDash;
    @BindView(R.id.pb_dash)
    DotProgressBar pbDash;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.ll_container)
    CoordinatorLayout llContainer;
    Unbinder unbinder;
    public View itemview;
    public ArrayList<CommunityModel> arrayList = new ArrayList<>();
    CommunityAdapter mAdapter;
    String TagArray[] = {"a", "b", "c", "Artificial Intelligence", "google india how are you"};
    ;
    @BindView(R.id.tv_nothing)
    TextView tvNothing;
    @BindView(R.id.pb_con)
    DotProgressBar pbCon;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemview = inflater.inflate(R.layout.fragment_community, container, false);
        unbinder = ButterKnife.bind(this, itemview);
        init();
        return itemview;
    }

    private void init() {
        mAdapter = new CommunityAdapter(getActivity(), arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvDash.setLayoutManager(mLayoutManager);
        rvDash.setItemAnimator(new DefaultItemAnimator());
        rvDash.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new CommunityAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getActivity(), ForumDetailsActivity.class);
                intent.putExtra("title", arrayList.get(position).getName());
                intent.putExtra("stars", arrayList.get(position).getStars());
                intent.putExtra("uid", arrayList.get(position).getUid());
                intent.putExtra("upvotes", arrayList.get(position).getUpvotes());
                startActivity(intent);
            }
        });
        /***********************************************************/
        prepareSampleData();
        /************************************************************/
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewProjectActivity.class);
                intent.putExtra("type", "forum");
                startActivity(intent);
            }
        });
    }

    /***********************************************************/
    private void prepareSampleData() {
        pbDash.setVisibility(View.GONE);
        llContainer.setBackgroundResource(0);
        arrayList.add(new CommunityModel("hello it is amazing", TagArray, "1", "g", 456, 23, 0, 0));
        arrayList.add(new CommunityModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.2", TagArray, "1", "a", 12, 23, 0, 0));
        arrayList.add(new CommunityModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.3", TagArray, "1", "b", 789, 893, 0, 0));
        arrayList.add(new CommunityModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.4", TagArray, "1", "c", 4000, 2, 0, 0));
        arrayList.add(new CommunityModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.5", TagArray, "1", "d", 45, 233, 0, 0));
        arrayList.add(new CommunityModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.6", TagArray, "1", "e", 23, 83, 0, 0));
        arrayList.add(new CommunityModel("hello it is amazing Big data framework for Ecosystem enterprise systems using process mining tools.7", TagArray, "1", "f", 0, 70, 0, 0));
        mAdapter.notifyDataSetChanged();
    }

    /***********************************************************/


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {

    }
}
