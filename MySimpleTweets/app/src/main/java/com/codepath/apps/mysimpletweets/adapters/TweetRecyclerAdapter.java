package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

/**
 * Created by jnagaraj on 2/17/16.
 */
public class TweetRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private List<Tweet> mTweets;

    public TweetRecyclerAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_tweet_result, parent, false);
        viewHolder = new TweetItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        TweetItemViewHolder viewHolder = (TweetItemViewHolder) holder;
        Tweet tweet = mTweets.get(position);
        //Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfilePic);
        viewHolder.tvBody.setText(tweet.getBody());
        viewHolder.tvUsername.setText(tweet.getUser().getScreenName());
        viewHolder.tvDatePosted.setText(tweet.getRelativeTimeAgo());

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class TweetItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvBody;
        public TextView tvUsername;
        public TextView tvDatePosted;
        public ImageView ivProfilePic;

        public  TweetItemViewHolder(View itemView) {
            super(itemView);

            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvDatePosted = (TextView)itemView.findViewById(R.id.tvDatePosted);
            tvUsername = (TextView)itemView.findViewById(R.id.tvUsername);
            ivProfilePic = (ImageView)itemView.findViewById(R.id.ivProfilePic);
        }
    }
}
