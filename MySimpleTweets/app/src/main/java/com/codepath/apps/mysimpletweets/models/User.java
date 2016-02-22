package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by jnagaraj on 2/17/16.
 */


/*
"user": {
      "name": "OAuth Dancer",
      "profile_sidebar_fill_color": "DDEEF6",
      "profile_background_tile": true,
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url":
 */
//ActiveAndroid Model: USER

@Table(name = "Users")
public class User extends Model {

    @Column(name = "name")
    public String name;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long id;

    @Column(name = "screen_name")
    public String screen_name;

    @Column(name = "profile_image_url")
    public String profile_image_url;


    public String getName() {
        return name;
    }

    public long getUid() {
        return id;
    }

    public String getScreenName() {
        return screen_name;
    }

    public String getProfileImageUrl() {
        return profile_image_url;
    }


    /*public static User fromJSON(JSONObject jsonObject) {

        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("uid");
            user.screenName = jsonObject.getString("screen_name");
            user.profile_image_url = jsonObject.getString("profile_image_url");

        }catch (JSONException e){
            e.printStackTrace();
        }


        return user;
    }*/


    public static User findOrCreate(User user) {
        User existingUser = new Select().from(User.class).where("uid = ?", user.getUid()).executeSingle();
        if(existingUser == null) {
            existingUser = user;
            existingUser.save();
        }
        return existingUser;

    }
}
