package com.sam_chordas.android.stockhawk.rest;

import com.sam_chordas.android.stockhawk.model.QuoteResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Joseph on 9/10/2016.
 */
public interface ApiInterface{
    @GET("public/yql")
    Call<QuoteResponse> getHistoricalData(@Query("q") String yql,
                                          @Query("env") String env,
                                          @Query("format") String format);
}