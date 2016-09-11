package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Joseph on 9/10/2016.
 */
public class QuoteResponse {

    @SerializedName("query")
    private Query query;

    public Query getQuery(){
        return query;
    }

    public void setQuery(Query query){
        this.query = query;
    }
}
