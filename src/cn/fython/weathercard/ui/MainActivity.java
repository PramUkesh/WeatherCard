package cn.fython.weathercard.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.data.WeatherList;
import cn.fython.weathercard.support.WeatherTools;
import cn.fython.weathercard.support.adapter.CardAdapter;
import cn.fython.weathercard.view.SwipeDismissListView;

public class MainActivity extends Activity {

	private static SwipeDismissListView mListView;
    private static CardAdapter mAdapter;

    private WeatherList mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mList = new WeatherList();

        mListView = (SwipeDismissListView) findViewById(R.id.listView);
        mListView.setOnDismissCallback(new SwipeDismissListView.OnDismissCallback() {

            @Override
            public void onDismiss(int dismissPosition) {
                mList.remove(dismissPosition);
                refreshListView();
            }

        });

        refreshListView();

        createSampleWeatherCard("东莞");
        createSampleWeatherCard("广州");
        createSampleWeatherCard("北京");
	}

    public void createSampleWeatherCard(String cityName) {
        CheckTask task = new CheckTask();
        task.cityName = cityName;
        task.days = 0;
        task.execute();
    }

    private void refreshListView() {
        mAdapter = new CardAdapter(
                new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light),
                mList,
                new CardAdapter.OnMoreButtonClickListener() {
                    @Override
                    public void onMoreButtonClick(int position) {
                        Log.i("OnMoreButtonClick", "position:" + position);
                    }
                }
        );
        mListView.setAdapter(mAdapter);
    }

    public class CheckTask extends AsyncTask<Void, Void, Weather> {

        public String cityName;
        public int days;

        @Override
        protected Weather doInBackground(Void... voids) {
            return WeatherTools.getWeatherByCity(cityName, days);
        }

        @Override
        protected void onPostExecute(Weather weather) {
            mList.add(weather);
            refreshListView();
        }

    }
}
