package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetRecyclerAdapter;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.AccountCredentials;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.ComposeTweetDialogActionListener{

    final long INIT_ID = 1;
    private long lastTweetId = INIT_ID;
    private TwitterClient client;
    private AccountCredentials credentials;

    private ArrayList<Tweet> tweets;
    private TweetRecyclerAdapter tweetRecyclerAdapter;
    private RecyclerView rvResults;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);


        tweets = new ArrayList<>();

        tweetRecyclerAdapter = new TweetRecyclerAdapter(tweets);
        rvResults = (RecyclerView)findViewById(R.id.rvTweets);
        rvResults.setAdapter(tweetRecyclerAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvResults.setLayoutManager(linearLayoutManager);

        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                populateTimelineOnRefresh();
                int curSize = tweetRecyclerAdapter.getItemCount();
                tweetRecyclerAdapter.notifyItemRangeInserted(curSize, tweets.size() - 1);
            }
        });

        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline();
        getAccountCredentials();

        swipeContainer = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tweets.clear();
                tweetRecyclerAdapter.notifyDataSetChanged();
                populateTimeline();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void onComposeAction(MenuItem menuItem)
    {
        Toast.makeText(this, "Happy !", Toast.LENGTH_SHORT).show();
    }

    public void onComposeTweet(View view)
    {
        Toast.makeText(this, "Happy !", Toast.LENGTH_SHORT).show();
        showComposeDialog();
    }

    // Send an API request to get the timeline json
    // Fill the RecyclerView by creating the tweet object from the json
    private void populateTimeline() {
        client.getLatestTweets(new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                tweets.addAll(Tweet.fromJSONArray(response));
                tweetRecyclerAdapter.notifyDataSetChanged();
                lastTweetId = tweets.get(tweets.size() - 1).getUid();
                Log.d("SUCCESS", lastTweetId + "");
                swipeContainer.setRefreshing(false);
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, 1);
    }

    private void populateTimelineOnRefresh() {
        client.getOlderTweets(new JsonHttpResponseHandler() {

            // SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                tweets.addAll(Tweet.fromJSONArray(response));
                tweetRecyclerAdapter.notifyDataSetChanged();
                lastTweetId = tweets.get(tweets.size() - 1).getUid();
                Log.d("SUCCESS", lastTweetId + "");
            }

            // FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        }, lastTweetId);
    }

    private void getAccountCredentials() {

        client.getAccountCredientials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                credentials = AccountCredentials.fromJSON(response);
                Log.d("DEBUG CREDENTIALS", credentials.getProfile_image_url());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void onComposeTweet(final String tweet) {

        client.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("SUCCESS", response.toString());

                tweets.clear();
                tweetRecyclerAdapter.notifyDataSetChanged();
                populateTimeline();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("FAILED", errorResponse.toString());

            }
        }, tweet);

    }

    private void showComposeDialog() {

        FragmentManager fm = getSupportFragmentManager();

        ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance("Compose", credentials.getProfile_image_url());
        composeTweetFragment.show(fm, "dialog_compose_tweet");

    }


}
