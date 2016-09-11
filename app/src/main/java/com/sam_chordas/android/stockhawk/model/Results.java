package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Joseph on 9/10/2016.
 */
public class Results {
    @SerializedName("quote")
    private List<Quote> quotes;

    public List<Quote> getQuotes(){
        return quotes;
    }

    public void setQuotes(List<Quote> quotes){
        this.quotes = quotes;
    }
}
