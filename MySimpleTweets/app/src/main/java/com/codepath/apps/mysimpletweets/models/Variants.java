package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by jnagaraj on 2/21/16.
 */

@Table(name = "Variants")
public class Variants extends Model {

    @Column(name = "url")
    private String url;

    @Column(name = "content_type")
    private String content_type;

    public String getContent_type() {
        return content_type;
    }

    public String getUrl() {
        return url;
    }
}
