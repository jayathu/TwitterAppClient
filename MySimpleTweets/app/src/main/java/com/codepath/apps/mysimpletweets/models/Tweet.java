package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jnagaraj on 2/17/16.
 */

    //ActiveAndroid Model : Tweet

//Parse the JSON + Store the data, encapsulate state logic or display logic
@Table(name = "Tweets")
public class Tweet extends Model {

    @Column(name = "text")
    public String text;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long id; //unique id for the tweet

    @Column(name = "created_at")
    public String created_at;

    @Column(name = "relative_time_ago")
    public String relativeTimeAgo;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    public User user;

    @Column(name = "extended_entities")
    private ExtendedEntities extended_entities;

    public void setExtended_entities(ExtendedEntities entities) {
        if(entities != null) {
            extended_entities = entities;
        }
    }

    public String getTweetImageUrl() {

        if (extended_entities != null) {
            for (Media m : extended_entities.getMedia()) {
                if (m.getType().equals("photo")) {
                    return m.getMedia_url();
                }
            }
        }
        return "";
    }

    public String getTweetVideoUrl() {

        if (extended_entities != null) {
            for (Media m : extended_entities.getMedia()) {
                if (m.getType().equals("video")) {
                    VideoInfo videoInfo = m.getVideo_info();
                    for(Variants variants : videoInfo.getVariants()){
                        if(variants.getContent_type().equals("application/x-mpegURL")) {
                           return variants.getUrl();
                        }
                    }
                    if(videoInfo == null) {
                        Variants variants = videoInfo.getVariants().get(1);
                        return variants.getUrl();
                    }
                }
            }
        }
        return "";
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return text;
    }

    public long getUid() {
        return id;
    }

    public ExtendedEntities getExtended_entities() {
        return extended_entities;
    }

    public Tweet() {
        super();
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo() {

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(created_at).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Tweet fromGSON(JSONObject jsonObject)
    {
        Gson gson = new GsonBuilder().create();
        Tweet tweet = gson.fromJson(jsonObject.toString(), Tweet.class);

        ExtendedEntities extendedEntities = tweet.getExtended_entities();
        if(extendedEntities != null) {
            ArrayList<Media> medias = extendedEntities.getMedia();
            for (Media media : medias) {

                if (media.getType().equals("photo")) {

                } else {
                    VideoInfo videoInfo = media.getVideo_info();
                    for (Variants variants : videoInfo.getVariants()) {
                        Log.d("VIDEO URL : ", variants.getUrl());
                    }
                }
            }
        }


        return tweet;
    }

    public static ArrayList<Tweet> fromGSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);

                Tweet tweet = Tweet.fromGSON(tweetJson);
                if (tweet != null) {
                    tweets.add(tweet);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }

    public boolean mediaTypePhoto() {
        if (extended_entities != null) {
            Log.v("EXTENDED ENTITIES ", "NOT NULL");
            if(extended_entities.getMedia() != null) {

                for (Media m : extended_entities.getMedia()) {
                    Log.d("MEDIA TYPE", m.getType());
                    if (m.getType().equals("photo")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean mediaTypeVideo() {
        if (extended_entities != null) {
            if(extended_entities.getMedia() != null) {
                for (Media m : extended_entities.getMedia()) {
                    if (m.getType().equals("video")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

   /* private void SetMediaUrls() {

        mediaTypePhoto = false;
        mediaTypeVideo = false;

        ExtendedEntities entities = getExtended_entities();
        if(entities != null) {
            for(Media media : entities.getMedia()) {
                if(media.getType().equals("photo")) {
                    tweetImageUrl = media.getMedia_url();
                    mediaTypePhoto = true;
                }else if(media.getType().equals("video")){
                    VideoInfo videoInfo = media.getVideo_info();
                    tweetImageUrl = null;
                    for(Variants variants : videoInfo.getVariants()){
                        if(variants.getContent_type().equals("application/x-mpegURL")) {
                            tweetImageUrl = variants.getUrl();
                        }
                    }
                    if(videoInfo == null) {
                        Variants variants = videoInfo.getVariants().get(1);
                        tweetImageUrl = variants.getUrl();
                    }
                    mediaTypeVideo = true;
                }
            }
        }
    }*/


}
