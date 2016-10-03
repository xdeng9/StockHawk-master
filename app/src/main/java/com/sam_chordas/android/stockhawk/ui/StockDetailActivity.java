package com.sam_chordas.android.stockhawk.ui;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
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

import android.app.LoaderManager;
import android.content.Loader;

/*
* Displays stock's historical performance in line graph
*/
public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static String TAG = StockDetailActivity.class.getSimpleName();
    private final static int ONE_MONTH = 0;
    private final static int THREE_MONTH = 1;
    private final static int SIX_MONTH = 2;
    private final static int ONE_YEAR = 3;
    private final static int URL_LOADER = 0;

    private String mSymbol;
    private List<Quote> mQuotes;
    private ArrayList<String> mDates;
    private LineChart mChart;
    private TabLayout mTab;
    private TextView mBidPrice;
    private TextView mChange;
    private TextView mChangePercent;
    private TextView mDate;

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
        mDates = new ArrayList<String>();

        getHistoricalData(mSymbol, Utils.getStartDate(), Utils.getEndDate());
        getLoaderManager().initLoader(URL_LOADER, null, this);

        mTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mTab.setTabMode(TabLayout.MODE_FIXED);
        mTab.setOnTabSelectedListener(new TabOnClickListener());
    }

    //Fetch historical data using Retrofit library
    private void getHistoricalData(String symbol, String startDate, String endDate) {
        String q = "select * from yahoo.finance.historicaldata where symbol = \"" + symbol + "\" and startDate=\"" +
                startDate + "\" and endDate =\"" + endDate + "\"";
        String env = "store://datatables.org/alltableswithkeys";
        String format = "json";

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<QuoteResponse> call = apiService.getHistoricalData(q, env, format);
        call.enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                List<Quote> quotes = response.body().getQuery().getResults().getQuotes();
                Collections.reverse(quotes);
                mQuotes = quotes;

                for (Quote quote : quotes) {
                    mDates.add(quote.getDate());
                }

                setupGraph();
                plotLineGraph(ONE_MONTH);
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
        return new CursorLoader(
                this,
                QuoteProvider.Quotes.withSymbol(mSymbol),
                null,
                QuoteColumns.ISCURRENT + "=?",
                new String[]{"1"},
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mBidPrice.setText(getString(R.string.bid_price,
                    data.getString(data.getColumnIndex("bid_price"))));
            mChange.setText(data.getString(data.getColumnIndex("change")));
            mChangePercent.setText(data.getString(data.getColumnIndex("percent_change")));
            if (data.getInt(data.getColumnIndex("is_up")) == 1) {
                mChange.setTextColor(Color.GREEN);
                mChangePercent.setTextColor(Color.GREEN);
            } else {
                mChange.setTextColor(Color.RED);
                mChangePercent.setTextColor(Color.RED);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void plotLineGraph(int duration) {

        List<Entry> entries = new ArrayList<Entry>();
        int length = mQuotes.size();

        switch (duration) {
            case (ONE_MONTH):
                for (int i = length - length / 12; i < length; i++) {
                    entries.add(new Entry(i, Float.valueOf(mQuotes.get(i).getClose())));
                }
                break;
            case (THREE_MONTH):
                for (int i = length - length / 4; i < length; i++) {
                    entries.add(new Entry(i, Float.valueOf(mQuotes.get(i).getClose())));
                }
                break;
            case (SIX_MONTH):
                for (int i = length / 2; i < length; i++) {
                    entries.add(new Entry(i, Float.valueOf(mQuotes.get(i).getClose())));
                }
                break;
            case (ONE_YEAR):
                int i = 0;
                for (Quote quote : mQuotes) {
                    entries.add(new Entry(i, Float.valueOf(quote.getClose())));
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

    //Set up line chart
    private void setupGraph() {
        YAxis yAxis = mChart.getAxisRight();
        yAxis.setTextSize(12f);
        yAxis.setTextColor(Color.WHITE);
        mChart.getAxisLeft().setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5, true);
        mChart.setDescription("");
        mChart.getLegend().setEnabled(false);
        mChart.setExtraOffsets(28, 0, 6, 6);
        xAxis.setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Utils.getFriendlyLabel(mDates.get((int) value));
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
    }

    private class TabOnClickListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {

            switch (tab.getPosition()) {

                case (ONE_MONTH):
                    plotLineGraph(ONE_MONTH);
                    break;
                case (THREE_MONTH):
                    plotLineGraph(THREE_MONTH);
                    break;
                case (SIX_MONTH):
                    plotLineGraph(SIX_MONTH);
                    break;
                case (ONE_YEAR):
                    plotLineGraph(ONE_YEAR);
                    break;
            }
            tab.select();
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
        }
    }
}
