package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.TweetParcel;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TweetDetails extends AppCompatActivity {

    @Bind(R.id.tvName)
    TextView tvName;

    @Bind(R.id.tvScreenName)
    TextView tvScreenName;

    @Bind(R.id.tvBody)
    TextView tvBody;

    @Bind(R.id.ivMediaImage)
    ImageView ivMediaImage;

    @Bind(R.id.ivProfilePic)
    ImageView ivProfilePic;
/*
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/GOTHAM-THIN.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );*/

        setContentView(R.layout.activity_tweet_details);

        ButterKnife.bind(this);
        Intent intent = getIntent();
        TweetParcel parcel = Parcels.unwrap(intent.getParcelableExtra("TWEET_DETAILS"));

        tvName.setText(parcel.Name);
        tvScreenName.setText(parcel.screenName);
        Glide.with(this).load(parcel.profileImageUrl).into(ivProfilePic);
        tvBody.setText(parcel.Text);
        if(parcel.imageThumbnail != null) {
            Glide.with(this).load(parcel.imageThumbnail).into(ivMediaImage);
        }
    }
}
