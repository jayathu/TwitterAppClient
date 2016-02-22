package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;

/**
 * Created by jnagaraj on 2/21/16.
 */

@Table(name = "ExtendedEntities")
public class ExtendedEntities extends Model {

    @Column(name = "media")
    private ArrayList<Media> media;

    public ArrayList<Media> getMedia() {
        return media;
    }


    public ExtendedEntities() {
        super();
        media = new ArrayList<Media>();
    }
}
