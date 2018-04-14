package com.education.edushare.edushare;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;

/**
 * Created by Akash Kabir on 10-12-2017.
 */

public class CustomTv extends LinearLayout {
    @BindView(R.id.tv_link)
    TextView tvLink;

    public CustomTv(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View item=inflater.inflate(R.layout.custom_item, this);
        tvLink=(TextView)item.findViewById(R.id.tv_link);
    }

    public CustomTv(Context context) {
        this(context, null);
    }

    public void add(String text){
        tvLink.setText(text);
    }
}
