package com.codepath.apps.mysimpletweets.models;

import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

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
    public long uid;

    @Column(name = "screen_name")
    public String screenName;

    @Column(name = "profile_image_url")
    public String profileImageUrl;


    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    // Return cursor for result set for all todo items
    public static Cursor fetchResultCursor() {
        String tableName = Cache.getTableInfo(User.class).getTableName();
        // Query all items without any conditions
        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").
                from(User.class).toSql();
        // Execute query on the underlying ActiveAndroid SQLite database
        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);
        return resultCursor;
    }

    public static User fromJSON(JSONObject jsonObject) {

        User user = new User();
        try {
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");

        }catch (JSONException e){
            e.printStackTrace();
        }


        return user;
    }

    public static User findOrCreate(User user) {
        User existingUser = new Select().from(User.class).where("uid = ?", user.getUid()).executeSingle();
        if(existingUser == null) {
            existingUser = user;
            existingUser.save();
        }
        return existingUser;

    }
}
