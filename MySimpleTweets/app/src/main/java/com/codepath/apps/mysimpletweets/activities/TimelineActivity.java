package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.codepath.apps.mysimpletweets.DividerItemDecoration;
import com.codepath.apps.mysimpletweets.EndlessRecyclerViewScrollListener;
import com.codepath.apps.mysimpletweets.ItemClickSupport;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetRecyclerAdapter;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.AccountCredentials;
import com.codepath.apps.mysimpletweets.models.ExtendedEntities;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.TweetParcel;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements ComposeTweetFragment.ComposeTweetDialogActionListener{

    private static final boolean TEST_OFFLINE = false;

    final long INIT_ID = 1;
    private long lastTweetId = INIT_ID;
    private TwitterClient client;
    private AccountCredentials credentials;

    private ArrayList<Tweet> tweets;
    private TweetRecyclerAdapter tweetRecyclerAdapter;
    private RecyclerView rvResults;
    private SwipeRefreshLayout swipeContainer;
    private String account_id;


   /* @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ACCOUNT_ID", account_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       /* CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/GOTHAM-THIN.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );*/

        setContentView(R.layout.activity_timeline);

        account_id = "";
        if(savedInstanceState != null) {
            account_id = savedInstanceState.getString("ACCOUNT_ID");
        }

        tweets = new ArrayList<>();

        tweetRecyclerAdapter = new TweetRecyclerAdapter(tweets);
        rvResults = (RecyclerView)findViewById(R.id.rvTweets);
        rvResults.setAdapter(tweetRecyclerAdapter);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        rvResults.addItemDecoration(itemDecoration);


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

        ItemClickSupport.addTo(rvResults).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        Log.d("PARCEL", "tapped on " + tweets.get(position).text);
                        Tweet tweet = tweets.get(position);
                        Intent intent = new Intent(getApplicationContext(), TweetDetails.class);
                        TweetParcel parcel = new TweetParcel();

                        parcel.Name = tweet.user.name;
                        parcel.screenName = tweet.user.getScreenName();
                        parcel.Text = tweet.getBody();
                        parcel.profileImageUrl = tweet.getUser().getProfileImageUrl();
                        if(tweet.mediaTypePhoto()) {

                            parcel.imageThumbnail = tweet.getTweetImageUrl();

                        }else if(tweet.mediaTypeVideo()) {

                            parcel.videoThumnail = tweet.getTweetVideoUrl();
                        }


                        intent.putExtra("TWEET_DETAILS", Parcels.wrap(parcel));
                        startActivity(intent);
                    }
                }
        );



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

    public void onComposeTweet(View view)
    {
        showComposeDialog();
    }

    // Send an API request to get the timeline json
    // Fill the RecyclerView by creating the tweet object from the json
    private void populateTimeline() {

        if(TEST_OFFLINE){
            tweets.clear();
            tweetRecyclerAdapter.notifyDataSetChanged();
            LoadTweetsOffline();
            tweets.addAll(GetCachedTweets());
            tweetRecyclerAdapter.notifyDataSetChanged();
            return;
        }


        if(!isOnline()) {
            tweets.clear();
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
                    StoreTweetsToLocalDatabase(tweets);
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

        Log.d("SAVE ", "StoreTweetsToLocalDatabase");
        for(Tweet t : tweets){

            User user = User.findOrCreate(t.getUser());
            ExtendedEntities entities = ExtendedEntities.createIfExists(t.getExtended_entities());

            Tweet tweet = new Tweet();
            tweet.id = t.getUid();
            tweet.text = t.getBody();
            tweet.relativeTimeAgo = t.getRelativeTimeAgo();
            tweet.created_at = t.created_at;

            if(entities != null) {
                tweet.setExtended_entities(entities);
            }

            tweet.user = user;

            tweet.save();
        }
    }

    private List<Tweet> GetCachedTweets() {

        return new Select().from(Tweet.class).limit(100).execute();
    }

    private void LoadTweetsOffline(){
        String jsonObjectString = loadJSONFromAsset();
        try {
            JSONArray jsonArray = new JSONArray(jsonObjectString);
            tweets.addAll(Tweet.fromGSONArray(jsonArray));

        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private  String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("json/home_timeline.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

}
