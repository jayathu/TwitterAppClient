package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    ArrayList<Tweet> tweets;

    private TwitterClient client;

    @Bind(R.id.editText)
    EditText editText;

    @Bind(R.id.tvcharcount)
    TextView tvcharcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test);

        ButterKnife.bind(this);

        editText.addTextChangedListener(new TextWatcher() {

            final static int WORD_COUNT = 2;
            int charLeft;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                charLeft = WORD_COUNT - editable.length();
                if(charLeft > 0) {
                    //tvcharcount.setText(String.valueOf(editable.length()));
                    tvcharcount.setText(String.valueOf(String.valueOf(charLeft)));
                }
            }
        });
        //tweets = new ArrayList<>();

        //client = TwitterApplication.getRestClient(); //singleton client
        //getAccountCredentials();

        //populateTimeline();
    }


    private void getAccountCredentials() {

            client.getAccountCredientials(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            });
    }

    private void populateTimeline() {

            client.getLatestTweets(new JsonHttpResponseHandler() {

                // SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    tweets.clear();
                    tweets.addAll(Tweet.fromGSONArray(response));
                }

                // FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }, 1);
    }
}
