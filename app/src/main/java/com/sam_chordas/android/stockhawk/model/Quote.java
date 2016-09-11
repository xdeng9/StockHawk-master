package com.sam_chordas.android.stockhawk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Joseph on 9/10/2016.
 */
public class Quote {

    @SerializedName("Symbol")
    private String symbol;
    @SerializedName("Date")
    private String date;
    @SerializedName("Open")
    private String open;
    @SerializedName("High")
    private String high;
    @SerializedName("Low")
    private String low;
    @SerializedName("Close")
    private String close;
    @SerializedName("Volume")
    private String volume;
    @SerializedName("Adj_Close")
    private  String adj_Close;

    public Quote(String symbol, String date, String open, String high, String low, String close, String volume, String adj_Close){
        this.symbol = symbol;
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.adj_Close = adj_Close;
    }

    public String getSymbol(){
        return  symbol;
    }

    public void setSymbol(String symbol){
        this.symbol = symbol;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getOpen(){
        return  open;
    }

    public void setOpen(String open){
        this.open = open;
    }

    public String getHigh(){
        return high;
    }

    public void setHigh(String high){
        this.high = high;
    }

    public String getLow(){
        return  low;
    }

    public void setLow(String low){
        this.low = low;
    }

    public String getClose(){
        return  close;
    }

    public void setClose(String close){
        this.close = close;
    }

    public String getVolume(){
        return  volume;
    }

    public void setVolume(String volume){
        this.volume = volume;
    }

    public String getAdj_Close(){
        return  adj_Close;
    }

    public void setAdj_Close(String adj_Close){
        this.adj_Close = adj_Close;
    }
}
