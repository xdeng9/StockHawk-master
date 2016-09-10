package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.sam_chordas.android.stockhawk.R;

public class StockDetailActivity extends AppCompatActivity {

    private String mSymbol;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);

        Intent intent = getIntent();
        mSymbol = intent.getStringExtra(MyStocksActivity.SYMBOL_KEY);
        getSupportActionBar().setTitle(mSymbol);
    }

}
