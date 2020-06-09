# Stock Hawk
An Android app that lets users track stock prices in real-time.

<img src="https://github.com/xdeng9/StockHawk-master/blob/master/screenshot/device-2016-09-18-160049.png" width="300"/><img src="https://github.com/xdeng9/StockHawk-master/blob/master/screenshot/device-2016-09-18-160251.png" width="300"/><img src="https://github.com/xdeng9/StockHawk-master/blob/master/screenshot/device-2016-09-18-155627.png" width="220"/>

## Features:
- Add multiple stocks to watchlist
- Keep track of stocks' historical performance
- RTL support
- Stock Hawk widget


## Fetching historical stock prices using Retrofit library
``` Java
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

        });
    }
```

## Libraries:
- Retrofit (http://square.github.io/retrofit/)
- MPAndroidChart (https://github.com/PhilJay/MPAndroidChart)

## API:
- Used Yahoo's finance API for stock prices.
