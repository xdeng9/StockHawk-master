package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Joseph on 9/10/2016.
 */
public class Query {

    @SerializedName("count")
    private int count;
    @SerializedName("created")
    private String created;
    @SerializedName("lang")
    private String lang;
    @SerializedName("results")
    private Results results;

    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    public String getCreated(){
        return created;
    }

    public void setCreated(String created){
        this.created = created;
    }

    public String getLang(){
        return lang;
    }

    public void setLang(String lang){
        this.lang = lang;
    }

    public Results getResults(){
        return results;
    }

    public void setResults(Results results){
        this.results = results;
    }
}
