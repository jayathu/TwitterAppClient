package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

/**
 * Created by jnagaraj on 2/17/16.
 */
public class TweetRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;

    private final int WITH_IMAGE = 0, WITHOUT_MEDIA = 1, WITH_VIDEO = 3;

    private List<Tweet> mTweets;

    public TweetRecyclerAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());


        Log.d("TYPE ", viewType + "");
        switch(viewType) {
            case WITHOUT_MEDIA:
                View v1 = inflater.inflate(R.layout.item_tweet_result, parent, false);
                viewHolder = new TweetItemViewHolder(v1);
                break;
            case WITH_IMAGE:
                View v2 = inflater.inflate(R.layout.item_tweet_with_image, parent, false);
                viewHolder = new TweetItemViewHolderWithImage(v2);
                break;
            case WITH_VIDEO:
                View v3 = inflater.inflate(R.layout.item_tweet_with_video, parent, false);
                viewHolder = new TweetItemViewHolderWithVideo(v3);
                break;
            default:
                View v = inflater.inflate(R.layout.item_tweet_result, parent, false);
                viewHolder = new TweetItemViewHolder(v);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch(holder.getItemViewType()){
            case WITHOUT_MEDIA:
                TweetItemViewHolder vh1 = (TweetItemViewHolder) holder;
                configureViewHolder(vh1, position, context);
                break;
            case WITH_IMAGE:
                TweetItemViewHolderWithImage vh2 = (TweetItemViewHolderWithImage) holder;
                configureViewHolderWithImage(vh2, position, context);
                break;
            case WITH_VIDEO:
                TweetItemViewHolderWithVideo vh3 = (TweetItemViewHolderWithVideo) holder;
                configureViewHolderWithVideo(vh3, position, context);
                break;

            default:
                TweetItemViewHolder v = (TweetItemViewHolder) holder;
                configureViewHolder(v, position, context);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }


    //this method is required to tell the RecyclerView about the type of view to inflate based on the position
    @Override
    public int getItemViewType(int position) {

        if(mTweets.get(position).mediaTypePhoto()) {
            return WITH_IMAGE;
        }else if(mTweets.get(position).mediaTypeVideo()) {
            return WITH_VIDEO;
        }else {
            return WITHOUT_MEDIA;
        }

    }

    private void configureViewHolder(TweetItemViewHolder vh1, int position, Context context) {

        Tweet tweet = mTweets.get(position);
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(vh1.ivProfilePic);
        vh1.tvBody.setText(tweet.getBody());
        vh1.tvUsername.setText(tweet.getUser().getScreenName());
        vh1.tvDatePosted.setText(tweet.getRelativeTimeAgo());
    }

    private void configureViewHolderWithImage(TweetItemViewHolderWithImage vh2, int position, Context context) {

        Tweet tweet = mTweets.get(position);
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(vh2.ivProfilePic);
        vh2.tvBody.setText(tweet.getBody());
        vh2.tvUsername.setText(tweet.getUser().getScreenName());
        vh2.tvDatePosted.setText(tweet.getRelativeTimeAgo());
        Glide.with(context).load(tweet.getTweetImageUrl()).into(vh2.ivMediaImage);
    }


    private void configureViewHolderWithVideo(TweetItemViewHolderWithVideo vh3, int position, Context context) {

        Tweet tweet = mTweets.get(position);
        Glide.with(context).load(tweet.getUser().getProfileImageUrl()).into(vh3.ivProfilePic);
        vh3.tvBody.setText(tweet.getBody());
        vh3.tvUsername.setText(tweet.getUser().getScreenName());
        vh3.tvDatePosted.setText(tweet.getRelativeTimeAgo());

        String videoUrl = tweet.getTweetVideoUrl();
        vh3.ivMediaVideo.setVideoURI(Uri.parse("android.resource://" + context.getPackageName() + "/" + videoUrl));
        vh3.ivMediaVideo.requestFocus();
        vh3.ivMediaVideo.start();

    }

    // FOR SIMPLE TWEETS
    // WITH TEXT
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

    // FOR TWEETS WITH
    // EMBEDDED IMAGES

    public static class TweetItemViewHolderWithImage extends RecyclerView.ViewHolder {

        public TextView tvBody;
        public TextView tvUsername;
        public TextView tvDatePosted;
        public ImageView ivProfilePic;

        public ImageView ivMediaImage;

        public  TweetItemViewHolderWithImage(View itemView) {
            super(itemView);

            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvDatePosted = (TextView)itemView.findViewById(R.id.tvDatePosted);
            tvUsername = (TextView)itemView.findViewById(R.id.tvUsername);
            ivProfilePic = (ImageView)itemView.findViewById(R.id.ivProfilePic);
            ivMediaImage = (ImageView)itemView.findViewById(R.id.ivMediaImage);
        }
    }

    // FOR TWEETS WITH
    // EMBEDDED VIDEO

    public static class TweetItemViewHolderWithVideo extends RecyclerView.ViewHolder {

        public TextView tvBody;
        public TextView tvUsername;
        public TextView tvDatePosted;
        public ImageView ivProfilePic;

        public VideoView ivMediaVideo;

        public  TweetItemViewHolderWithVideo(View itemView) {
            super(itemView);

            tvBody = (TextView)itemView.findViewById(R.id.tvBody);
            tvDatePosted = (TextView)itemView.findViewById(R.id.tvDatePosted);
            tvUsername = (TextView)itemView.findViewById(R.id.tvUsername);
            ivProfilePic = (ImageView)itemView.findViewById(R.id.ivProfilePic);
            ivMediaVideo = (VideoView)itemView.findViewById(R.id.ivMediaVideo);
        }
    }
}
