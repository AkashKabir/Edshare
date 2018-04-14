package com.education.edushare.edushare;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.education.edushare.edushare.model.DashBoardModel;
import com.education.edushare.edushare.model.EntityInfo;
import com.education.edushare.edushare.model.SentimentInfo;
import com.education.edushare.edushare.model.TokenInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Akash Kabir on 17-11-2017.
 */

public class NewProjectActivity extends AppCompatActivity implements ApiFragment.Callback, View.OnClickListener {
    @BindView(R.id.txt_title)
    EditText txtTitle;
    @BindView(R.id.txt_details)
    EditText txtDetails;
    @BindView(R.id.txt_tags)
    EditText txtTags;
    @BindView(R.id.btn_tag)
    Button btnTag;
    @BindView(R.id.txt_ref)
    EditText txtRef;
    @BindView(R.id.btn_ref)
    Button btnRef;
    @BindView(R.id.btn_publish)
    Button btnPublish;
    @BindView(R.id.editText_tag)
    TextView editTextTag;
    @BindView(R.id.linearLayout_tag)
    LinearLayout linearLayoutTag;
    @BindView(R.id.editText_Ref)
    TextView editTextRef;
    @BindView(R.id.linearLayout_ref)
    LinearLayout linearLayoutRef;
    @BindView(R.id.ll_ref)
    LinearLayout llRef;
    @BindView(R.id.ll_tag)
    LinearLayout llTags;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String uname = "loggedinuser";
    String uorg="currentuserorg";
    FirebaseUser user;
    HashMap<String, String> nametag = new HashMap<>();
    HashMap<String, String> nameref = new HashMap<>();
    HashMap<String, String> members = new HashMap<>();
    private DatabaseReference mDatabase;

    /*****************************************************************/
    private static final int API_ENTITIES = 0;
    private static final int API_SENTIMENT = 1;
    private static final int API_SYNTAX = 2;
    final FragmentManager fm = getSupportFragmentManager();
    private static final String FRAGMENT_API = "api";
    private static final int LOADER_ACCESS_TOKEN = 1;
    private static final String STATE_SHOWING_RESULTS = "showing_results";

/*    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                // The icon button is clicked; start analyzing the input.
                case R.id.btn_publish:
                *//*    *//**//**//**//*startAnalyze();*//*
                    publishData();
                    break;
            }
        }
    };*/

    public DatabaseReference mref;
    @BindView(R.id.txt_notes)
    EditText txtNotes;
    @BindView(R.id.linearLayout_mem_ref)
    LinearLayout linearLayoutMemRef;
    @BindView(R.id.tv_team_Size)
    TextView tvTeamSize;
    @BindView(R.id.et_team_size)
    EditText etTeamSize;

    Typeface typeface2;
    //getting all the data and upload
    private void publishData() {
        btnPublish.setVisibility(View.GONE);
        Toast.makeText(this, "Publishing project", Toast.LENGTH_SHORT).show();

        user=FirebaseAuth.getInstance().getCurrentUser();
        String pname, pdetails, puid, userid, username, notes, org, teamsize,category;

        category="All";
        pname = txtTitle.getText().toString();
        pdetails = txtDetails.getText().toString();
        userid = user.getUid();
        username = user.getDisplayName();
        notes =txtNotes.getText().toString();
        org="";
        teamsize=etTeamSize.getText().toString();
        mDatabase=FirebaseDatabase.getInstance().getReference().child("PublishedProjects").child(category);
        String pushkey=mDatabase.push().getKey();
        mDatabase.child(pushkey).setValue(new DashBoardModel(pname,pdetails,userid,pushkey,username,notes,org,teamsize,nametag,nameref,members));
        mref = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("projectsInvolved");
        HashMap<String,String> map=new HashMap<>();
        map.put(pushkey,"created");
        map.put("name",pname);
        mref.child(pushkey).setValue(map);
    }

    /********************************************************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String type = getIntent().getStringExtra("type");
        typeface2 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        int type_flag = 0;
        if (type.equals("project")) {
            setContentView(R.layout.activity_new_project);
            type_flag = 0;
        } else if (type.equals("forum")) {
            type_flag = 1;
            setContentView(R.layout.activity_new_forum);
        }
        ButterKnife.bind(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        init();
//        initGoogleApi();
    }


    private void init() {
       /* txtDetails.setTypeface(typeface2);*/
        btnTag.setOnClickListener(this);
        btnRef.setOnClickListener(this);
        btnPublish.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_ref: {

                if (checkNulledt(txtRef))
                    Toast.makeText(this, "Enter some references", Toast.LENGTH_SHORT).show();
                else {
                    nameref.put("nullvalue",txtRef.getText().toString());
                    linearLayoutRef.addView(createNewTextView(txtRef.getText().toString()));
                    llRef.setVisibility(View.VISIBLE);
                    txtRef.setText("");
                }
                break;
            }
            case R.id.btn_tag: {

                if (checkNulledt(txtTags))
                    Toast.makeText(this, "Enter some Tags", Toast.LENGTH_SHORT).show();
                else {
                    nametag.put(txtTags.getText().toString(),"nullvalue");
                    linearLayoutTag.addView(createNewTextView(txtTags.getText().toString()));
                    llTags.setVisibility(View.VISIBLE);
                    txtTags.setText("");
                }
                break;
            }
            case R.id.btn_publish: {
                    publishData();
                //just for testing
           /*     Intent i = new Intent(NewProjectActivity.this, ProjectViewActivity.class);
                startActivity(i);
                finish();*/


                //
                break;
            }
        }
    }

    private boolean checkNulledt(TextView editTextRef) {
        if (editTextRef.getText().toString().equals(""))
            return true;
        return false;
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


    /***************************************************************************************************************************/
/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
    private void startAnalyze() {
        // Hide the software keyboard if it is up
        //mInput.clearFocus();
        // InputMethodManager ime = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //  ime.hideSoftInputFromWindow(mInput.getWindowToken(), 0);

        // Show progress
        Log.d("TAG", "textdetails: " + txtDetails.getText().toString());
        //Toast.makeText(this, "API REQUEST BEGINNING", Toast.LENGTH_SHORT).show();
        showProgress();

        // Call the API
        final String text = txtDetails.getText().toString();
        getApiFragment().analyzeEntities(text);
        getApiFragment().analyzeSentiment(text);
        getApiFragment().analyzeSyntax(text);
    }

    private ApiFragment getApiFragment() {
        return (ApiFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_API);
    }

    private void showProgress() {
        Log.d("TAG", "showProgress: ");
           /* mIntroduction.setVisibility(View.GONE);
            if (mResults.getVisibility() == View.VISIBLE) {
                mHidingResult = true;
                ViewCompat.animate(mResults)
                        .alpha(0.f)
                        .setListener(new ViewPropertyAnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(View view) {
                                mHidingResult = false;
                                view.setVisibility(View.INVISIBLE);
                            }
                        });
            }
            if (mProgress.getVisibility() == View.INVISIBLE) {
                mProgress.setVisibility(View.VISIBLE);
                ViewCompat.setAlpha(mProgress, 0.f);
                ViewCompat.animate(mProgress)
                        .alpha(1.f)
                        .setListener(null)
                        .start();
            }*/
    }

    private void showResults() {
        /*mIntroduction.setVisibility(View.GONE);
        if (mProgress.getVisibility() == View.VISIBLE) {
            ViewCompat.animate(mProgress)
                    .alpha(0.f)
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(View view) {
                            view.setVisibility(View.INVISIBLE);
                        }
                    });
        }
        if (mHidingResult) {
            ViewCompat.animate(mResults).cancel();
        }
        if (mResults.getVisibility() == View.INVISIBLE) {
            mResults.setVisibility(View.VISIBLE);
            ViewCompat.setAlpha(mResults, 0.01f);
            ViewCompat.animate(mResults)
                    .alpha(1.f)
                    .setListener(null)
                    .start();
        }*/
    }

    @Override
    public void onEntitiesReady(EntityInfo[] entities) {
        Toast.makeText(this, "Entities Ready: ", Toast.LENGTH_SHORT).show();
        for (EntityInfo e : entities) {
            Log.d("TAG", "onEntitiesReady: " + e.Display());
        }

    }

    @Override
    public void onSentimentReady(SentimentInfo sentiment) {
        Toast.makeText(this, "sentiment Ready: ", Toast.LENGTH_SHORT).show();
        Log.d("TAG", "sentiment: " + sentiment.Display());
    }

    @Override
    public void onSyntaxReady(TokenInfo[] tokens) {
        Toast.makeText(this, "tokens Ready: ", Toast.LENGTH_SHORT).show();
        for (TokenInfo e : tokens) {
            Log.d("TAG", "onEntitiesReady: " + e.Display());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void setEntities(EntityInfo[] entities) {

    }

    public void setSentiment(SentimentInfo sentiment) {

    }

    public void setTokens(TokenInfo[] tokens) {

    }

    private void prepareApi() {
        // Initiate token refresh
        getSupportLoaderManager().initLoader(LOADER_ACCESS_TOKEN, null,
                new LoaderManager.LoaderCallbacks<String>() {
                    @Override
                    public Loader<String> onCreateLoader(int id, Bundle args) {
                        return new AccessTokenLoader(NewProjectActivity.this);
                    }

                    @Override
                    public void onLoadFinished(Loader<String> loader, String token) {
                        getApiFragment().setAccessToken(token);
                    }

                    @Override
                    public void onLoaderReset(Loader<String> loader) {
                    }
                });
    }

    private void handleShareIntent() {
        final Intent intent = getIntent();
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_SEND)
                && TextUtils.equals(intent.getType(), "text/plain")) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (text != null) {
                txtDetails.setText(text);
            }
        }
    }

    private void initGoogleApi() {
        txtDetails.setMaxLines(Integer.MAX_VALUE);

        // Set up the view pager

       /* if (savedInstanceState == null) {
            // The app has just launched; handle share intent if it is necessary
            handleShareIntent();
        } else {
            // Configuration changes; restore UI states
            boolean results = savedInstanceState.getBoolean(STATE_SHOWING_RESULTS);
            if (results) {
                mIntroduction.setVisibility(View.GONE);
                mResults.setVisibility(View.VISIBLE);
                mProgress.setVisibility(View.INVISIBLE);
            } else {
                mResults.setVisibility(View.INVISIBLE);
            }
        }
*/
        // Bind event listeners
        txtDetails.setOnEditorActionListener(mOnEditorActionListener);
     /*   findViewById(R.id.btn_publish).setOnClickListener(mOnClickListener);*/
        if (getApiFragment() == null) {
            fm.beginTransaction().add(new ApiFragment(), FRAGMENT_API).commit();
        }
        /*// Prepare the API
        if (getApiFragment() == null) {
            fm.beginTransaction().add(new ApiFragment(), FRAGMENT_API).commit();
        }*/
        prepareApi();
    }

    private final TextView.OnEditorActionListener mOnEditorActionListener
            = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            // Enter pressed; Start analyzing the input.
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                startAnalyze();
                return true;
            }
            return false;
        }
    };
}



