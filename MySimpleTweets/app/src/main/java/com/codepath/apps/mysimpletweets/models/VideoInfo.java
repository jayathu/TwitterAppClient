package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;

/**
 * Created by jnagaraj on 2/21/16.
 */

@Table(name = " VideoInfo")
public class VideoInfo extends Model{

    public ArrayList<Variants> getVariants() {
        return variants;
    }

    public void setVariants(ArrayList<Variants> variants) {
        this.variants = variants;
    }

    @Column(name = "variants")
    private ArrayList<Variants> variants;

    public VideoInfo() {
        super();
        //variants = new ArrayList<Variants>();
    }
}
