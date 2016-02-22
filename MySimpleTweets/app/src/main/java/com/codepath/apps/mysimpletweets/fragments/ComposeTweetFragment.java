package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jnagaraj on 2/19/16.
 */
public class ComposeTweetFragment extends DialogFragment {

    private ComposeTweetDialogActionListener listener;

    public interface ComposeTweetDialogActionListener {
        void onComposeTweet(String tweetMsg);
    }

    @Bind(R.id.btnTweet)
    Button btnTweet;

    @Bind(R.id.tvcharcount)
    TextView tvCharCountDisplay;

    @Bind(R.id.etcompose)
    EditText etCompose;

    @Bind(R.id.ivProfilePicCompose)
    ImageView ivProfilePic;

    @Bind(R.id.ivcancel)
    ImageView incancel;

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

        ButterKnife.bind(this, view);


        etCompose.addTextChangedListener(new TextWatcher() {

            final static int WORD_COUNT = 140;
            int charLeft;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {

                charLeft = WORD_COUNT - s.length();
                if(charLeft >= 0) {
                    tvCharCountDisplay.setText(String.valueOf(String.valueOf(charLeft)));
                }

                if(charLeft < 0) {
                    btnTweet.setEnabled(false);
                }
                else{
                    btnTweet.setEnabled(true);
                }
            }
        });


        String title = getArguments().getString("title", "Enter Name");
        String profileUrl = getArguments().getString("imageUrl", DEFAULT_PIC);
        getDialog().setTitle(title);
        Glide.with(view.getContext()).load(profileUrl).into(ivProfilePic);

        etCompose.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        View.OnClickListener onClickComposeTweet = new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                listener = (ComposeTweetDialogActionListener) getActivity();
                listener.onComposeTweet(etCompose.getText().toString());
                dismiss();

            }
        };

        View.OnClickListener onClickCancelTweet = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };


        btnTweet.setOnClickListener(onClickComposeTweet);
        incancel.setOnClickListener(onClickCancelTweet);

    }

}
