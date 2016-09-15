package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.model.QuoteResponse;
import com.sam_chordas.android.stockhawk.rest.ApiClient;
import com.sam_chordas.android.stockhawk.rest.ApiInterface;
import com.sam_chordas.android.stockhawk.rest.Utils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockDetailActivity extends AppCompatActivity {
    private final static String TAG = StockDetailActivity.class.getSimpleName();
    private final static int ONE_MONTH = 1;
    private final static int THREE_MONTH = 3;
    private final static int SIX_MONTH = 6;
    private final static int ONE_YEAR = 12;

    private String mSymbol;
    private List<Quote> mQuotes;
    private LineChart mChart;
    private TabLayout mTab;
    private TextView mBidPrice;
    private TextView mChange;
    private TextView mChangePercent;
    private TextView mDate;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent intent = getIntent();
        mSymbol = intent.getStringExtra(MyStocksActivity.SYMBOL_KEY);
        getSupportActionBar().setTitle(mSymbol.toUpperCase());

        mChart = (LineChart) findViewById(R.id.chart);
        mTab = (TabLayout) findViewById(R.id.tab);
        mBidPrice = (TextView) findViewById(R.id.bid_price);
        mChange = (TextView) findViewById(R.id.change);
        mChangePercent = (TextView) findViewById(R.id.change_percent);
        mDate = (TextView) findViewById(R.id.date);
        mDate.setText(Utils.getFriendlyDate());

        mTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mTab.setTabMode(TabLayout.MODE_FIXED);

        getHistoricalData(mSymbol, Utils.getStartDate(), Utils.getEndDate());

        mCursor = getContentResolver().query(QuoteProvider.Quotes.withSymbol(mSymbol),
                null,
                QuoteColumns.ISCURRENT + "=?",
                new String[]{"1"},
                null);

        if(mCursor.moveToFirst()){
            mBidPrice.setText(getString(R.string.bid_price,
                    mCursor.getString(mCursor.getColumnIndex("bid_price"))));
            mChange.setText(mCursor.getString(mCursor.getColumnIndex("change")));
            mChangePercent.setText(mCursor.getString(mCursor.getColumnIndex("percent_change")));
            if (mCursor.getInt(mCursor.getColumnIndex("is_up")) == 1){
                mChange.setTextColor(Color.GREEN);
                mChangePercent.setTextColor(Color.GREEN);
            } else {
                mChange.setTextColor(Color.RED);
                mChangePercent.setTextColor(Color.RED);
            }

        }

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
                setupGraph();
                plotLineGraph(THREE_MONTH);
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void plotLineGraph(int duration){

        List<Entry> entries = new ArrayList<Entry>();
        int length = mQuotes.size();

        switch (duration){
            case (ONE_MONTH):
                for(int i=length-length/12; i< length; i++){
                    entries.add(new Entry(i,Float.valueOf(mQuotes.get(i).getClose())));
                }
                break;
            case (THREE_MONTH):
                for(int i=length-length/4; i< length; i++){
                    entries.add(new Entry(i,Float.valueOf(mQuotes.get(i).getClose())));
                }
                break;
            case (SIX_MONTH):
                //entries.clear();
                for(int i=length/2; i< length; i++){
                    entries.add(new Entry(i,Float.valueOf(mQuotes.get(i).getClose())));
                }
                break;
            case(ONE_YEAR):
                int i=0;
                for (Quote quote: mQuotes){
                    entries.add(new Entry(i,Float.valueOf(quote.getClose())));
                    i++;
                }
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        LineData lineData = new LineData(dataSet);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        mChart.setData(lineData);
        mChart.invalidate();
    }

    private void setupGraph(){
        mChart.setNoDataText("Loading chart..."); //Not working
        YAxis yAxis = mChart.getAxisRight();
        yAxis.setTextSize(12f);
        yAxis.setTextColor(Color.WHITE);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getXAxis().setDrawLabels(false);
        mChart.setDescription("");
    }

}
