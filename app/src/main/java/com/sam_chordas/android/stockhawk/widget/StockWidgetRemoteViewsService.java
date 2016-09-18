package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.model.Quote;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by administrator on 9/17/16.
 */
public class StockWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent){
        return new RemoteViewsFactory() {

            private Cursor data;

            @Override
            public void onCreate() {
                //Nothing to do here
            }

            @Override
            public void onDataSetChanged() {
                if(data !=null){
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        null,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if(data!=null){
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if(position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)){
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote_widget);
                String bidPrice = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
                int isUp = data.getInt(data.getColumnIndex(QuoteColumns.ISUP));

                views.setTextViewText(R.id.stock_symbol, symbol);
                views.setTextViewText(R.id.bid_price, bidPrice);
                views.setTextViewText(R.id.change, change);

                if(isUp == 1){
                    views.setTextColor(R.id.change, getResources().getColor(
                            R.color.material_green_700));
                }else{
                    views.setTextColor(R.id.change, getResources().getColor(
                            R.color.material_red_700));
                }
                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(MyStocksActivity.SYMBOL_KEY, symbol);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return null;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if(data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(QuoteColumns._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
