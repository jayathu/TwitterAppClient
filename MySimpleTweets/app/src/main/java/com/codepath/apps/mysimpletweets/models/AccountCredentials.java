package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jnagaraj on 2/19/16.
 */


/*

{
   "id":33143481,
   "id_str":"33143481",
   "name":"jayashree",
   "screen_name":"jayathu",
   "location":"",
   "description":"",
   "profile_image_url":"http:\/\/pbs.twimg.com\/profile_images\/501991114090364930\/bSQe0m2B_normal.jpeg"
}
 */


public class AccountCredentials {

    private String id;
    private String name;
    private String screen_name;
    private String profile_image_url;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public static AccountCredentials fromJSON(JSONObject jsonObject) {

        AccountCredentials credentials = new AccountCredentials();
        try{
            credentials.id = jsonObject.getString("id");
            credentials.name = jsonObject.getString("name");
            credentials.screen_name = jsonObject.getString("screen_name");
            credentials.profile_image_url = jsonObject.getString("profile_image_url");

        }catch (JSONException e) {
            e.printStackTrace();
        }

        return credentials;
    }
}
