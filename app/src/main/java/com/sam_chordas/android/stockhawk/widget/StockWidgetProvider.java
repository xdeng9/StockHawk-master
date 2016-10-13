package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.support.v4.app.TaskStackBuilder;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;

/**
 * This class is responsiable for updating the stock hawk wiget.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){

        for(int appWidgetId : appWidgetIds){

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stock);
            Intent intent = new Intent(context, StockWidgetRemoteViewsService.class);
            views.setRemoteAdapter(R.id.stock_widget_list, intent);
            Intent onClickIntent = new Intent(context, StockDetailActivity.class);
            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(onClickIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.stock_widget_list, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent){

        super.onReceive(context, intent);
        if(intent.getAction().equals("com.sam_chordas.android.stockhawk.ACTION_DATA_CHANGED")){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgets = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgets, R.id.stock_widget_list);
        }
    }
}
