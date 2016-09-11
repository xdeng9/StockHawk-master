package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.QuoteResponse;
import com.sam_chordas.android.stockhawk.rest.ApiClient;
import com.sam_chordas.android.stockhawk.rest.ApiInterface;
import com.sam_chordas.android.stockhawk.rest.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockDetailActivity extends AppCompatActivity {
    private final static String TAG = StockDetailActivity.class.getSimpleName();

    private String mSymbol;
    private List<Quote> mQuotes;
    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent intent = getIntent();
        mSymbol = intent.getStringExtra(MyStocksActivity.SYMBOL_KEY);
        getSupportActionBar().setTitle(mSymbol.toUpperCase());

        mChart = (LineChart) findViewById(R.id.chart);
        getHistoricalData(mSymbol, Utils.getStartDate(3), Utils.getEndDate());

    }

    private void getHistoricalData(String symbol, String startDate, String endDate){
        String q="select * from yahoo.finance.historicaldata where symbol = \""+symbol+"\" and startDate=\""+
                startDate+"\" and endDate =\""+endDate+"\"";
        String env ="store://datatables.org/alltableswithkeys";;
        String format="json";

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<QuoteResponse> call = apiService.getHistoricalData(q, env, format);
        call.enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
               List<Quote> quotes = response.body().getQuery().getResults().getQuotes();
                Collections.reverse(quotes);
                mQuotes = quotes;
                plotLineGraph();
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void plotLineGraph(){

        List<Entry> entries = new ArrayList<Entry>();
        int i=0;
        for (Quote quote: mQuotes){
            entries.add(new Entry(i,Float.valueOf(quote.getClose())));
            i++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Test");
        LineData lineData = new LineData(dataSet);
        dataSet.setDrawCircles(false);
        mChart.setDescription("");
        mChart.setData(lineData);
        mChart.invalidate();
    }

}
