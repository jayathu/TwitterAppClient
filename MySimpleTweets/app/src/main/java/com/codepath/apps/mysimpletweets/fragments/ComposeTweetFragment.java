package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;

/**
 * Created by jnagaraj on 2/19/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    private EditText etCompose;
    private Button btnTweet;
    private ImageView ivProfilePic;
    private final String DEFAULT_PIC = "http://pbs.twimg.com/profile_images/501991114090364930/bSQe0m2B_normal.jpeg";

    public ComposeTweetFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetFragment newInstance(String title, String imageUrl) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("imageUrl", imageUrl);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etCompose = (EditText)view.findViewById(R.id.etcompose);
        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePicCompose);
        String title = getArguments().getString("title", "Enter Name");
        String profileUrl = getArguments().getString("imageUrl", DEFAULT_PIC);
        getDialog().setTitle(title);
        Glide.with(view.getContext()).load(profileUrl).into(ivProfilePic);

        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }
}
