package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetRecyclerAdapter;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.AccountCredentials;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.ComposeTweetDialogActionListener{

    final long INIT_ID = 1;
    private long lastTweetId = INIT_ID;
    private TwitterClient client;
    private AccountCredentials credentials;

    private ArrayList<Tweet> tweets;
    private TweetRecyclerAdapter tweetRecyclerAdapter;
    private RecyclerView rvResults;
    private SwipeRefreshLayout swipeContainer;
    private String account_id;


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ACCOUNT_ID", account_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        account_id = "";
        if(savedInstanceState != null) {
            account_id = savedInstanceState.getString("ACCOUNT_ID");
        }

        tweets = new ArrayList<>();

        tweetRecyclerAdapter = new TweetRecyclerAdapter(tweets);
        rvResults = (RecyclerView)findViewById(R.id.rvTweets);
        rvResults.setAdapter(tweetRecyclerAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvResults.setLayoutManager(linearLayoutManager);

        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.d("SWIPTE TO REFRESH", tweets.size() + "");
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

        if(!isOnline()) {
            tweets.clear();;
            tweetRecyclerAdapter.notifyDataSetChanged();
            tweets.addAll(GetCachedTweets());
            tweetRecyclerAdapter.notifyDataSetChanged();
        }else {

            client.getLatestTweets(new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    tweets.clear();
                    tweetRecyclerAdapter.notifyDataSetChanged();

                    //tweets.addAll(Tweet.fromJSONArray(response));
                    tweets.addAll(Tweet.fromGSONArray(response));
                    tweetRecyclerAdapter.notifyDataSetChanged();

                    lastTweetId = tweets.get(tweets.size() - 1).getUid();
                    Log.d("lastTweedId", lastTweetId + "");
                    swipeContainer.setRefreshing(false);
                    StoreTweetsToLocalDatabase(tweets);
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }, 1);
        }
    }

    private void populateTimelineOnRefresh() {
        if(!isOnline()){
                Toast.makeText(this, "Please connect to Internet.", Toast.LENGTH_SHORT).show();
        }else {
            client.getOlderTweets(new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                    tweets.addAll(Tweet.fromGSONArray(response));
                    tweetRecyclerAdapter.notifyDataSetChanged();
                    lastTweetId = tweets.get(tweets.size() - 1).getUid();
                    Log.d("Refresh Id", lastTweetId + "");
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }, lastTweetId);
        }
    }

    private void getAccountCredentials() {

        if(!isOnline()) {
            Log.d("CREDENTIALS" , account_id);
            credentials = AccountCredentials.findCredentials(account_id);
        }else {
            client.getAccountCredientials(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    credentials = AccountCredentials.fromJSON(response);
                    account_id = credentials.getAccountId();
//                    Log.d("DEBUG CREDENTIALS", credentials.getProfile_image_url());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
        }
    }

    public void onComposeTweet(final String tweet) {

        client.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("SUCCESS", response.toString());

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

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void StoreTweetsToLocalDatabase(ArrayList<Tweet> tweets) {

        for(Tweet t : tweets){

            User user = User.findOrCreate(t.getUser());

            Tweet tweet = new Tweet();
            tweet.id = t.getUid();
            tweet.text = t.getBody();
            tweet.relativeTimeAgo = t.getRelativeTimeAgo();
            tweet.created_at = t.created_at;
            tweet.user = user;

            tweet.save();
        }
    }

    private List<Tweet> GetCachedTweets() {

        return new Select().from(Tweet.class).limit(100).execute();
    }

    private void SaveAccountCredentials(AccountCredentials credentials) {
        AccountCredentials accountCredentials = new AccountCredentials(credentials);
        accountCredentials.save();

    }
}
