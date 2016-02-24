package com.codepath.apps.mysimpletweets.models;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;

/**
 * Created by jnagaraj on 2/21/16.
 */

@Table(name = "ExtendedEntities")
public class ExtendedEntities extends Model {

    public void setMedia(ArrayList<Media> media) {
        this.media = new ArrayList<>();
        this.media = media;
    }

    @Column(name = "media")
    private ArrayList<Media> media;

    public ArrayList<Media> getMedia() {

        return media;
    }


    public ExtendedEntities() {
        super();
    }

    public static ExtendedEntities createIfExists(ExtendedEntities entities) {

        if(entities != null) {
            ExtendedEntities extendedEntities = new ExtendedEntities();
            ArrayList<Media> medias = new ArrayList<>();
            for (Media m : entities.getMedia()) {
                Media media = new Media(); //Save media to db

                media.setType(m.getType());

                Log.d("TYPE ", media.getType());
                if(m.getType().equals("photo")) {

                    media.setMedia_url(m.getMedia_url());
                    Log.d("media_url =  ", media.getMedia_url());

                }
                else if(m.getType().equals("video")) {
                    VideoInfo videoInfo = new VideoInfo();

                    ArrayList<Variants> variants = new ArrayList<>();
                    for(Variants v : videoInfo.getVariants()) {
                        Variants vr = new Variants();
                        vr.setUrl(v.getUrl());
                        vr.setContent_type(v.getContent_type());
                        vr.save();
                        variants.add(vr);
                    }
                    videoInfo.setVariants(variants);
                    videoInfo.save();
                    media.setVideo_info(videoInfo);
                }
                media.save();
                medias.add(media);
            }
            extendedEntities.setMedia(medias);
            extendedEntities.save();

            return extendedEntities;
        }

        return null;
    }

}
