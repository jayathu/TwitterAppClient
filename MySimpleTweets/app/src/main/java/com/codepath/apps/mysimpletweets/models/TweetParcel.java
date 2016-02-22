package com.codepath.apps.mysimpletweets.models;

import org.parceler.Parcel;

/**
 * Created by jnagaraj on 2/21/16.
 */

@Parcel
public class TweetParcel {

    public String Name;
    public String Text;
    public String screenName;
    public String profileImageUrl;
    public String imageThumbnail = null;
    public String videoThumnail = null;

    public TweetParcel() {

    }
}
