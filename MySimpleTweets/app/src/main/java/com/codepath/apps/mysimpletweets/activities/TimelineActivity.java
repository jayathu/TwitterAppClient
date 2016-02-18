package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetRecyclerAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetRecyclerAdapter tweetRecyclerAdapter;
    private RecyclerView rvResults;

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

        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline();

    }


    // Send an API request to get the timeline json
    // Fill the RecyclerView by creating the tweet object from the json
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            // SUCCESS

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("DEBUG", response.toString());

                //JSON HERE
                //DESERIALIZE JSON
                //CREATE MODELS
                //LOAD THE MODELS DATA INTO VIEW

                tweets.addAll(Tweet.fromJSONArray(response));
                tweetRecyclerAdapter.notifyDataSetChanged();

            }


            // FAILURE


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
