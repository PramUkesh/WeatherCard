package cn.fython.weathercard.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.data.WeatherList;
import cn.fython.weathercard.support.Utility;
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

        initActionBar();
        mList = new WeatherList();
        initListView();
        refreshListView();

        /* Create sample cards */
        createSampleWeatherCard("东莞");
        createSampleWeatherCard("广州");
        createSampleWeatherCard("北京");
        createSampleWeatherCard("武汉");
        createSampleWeatherCard("香港");
        createSampleWeatherCard("惠州");
	}

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        View v = LayoutInflater.from(
                new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light)
        ).inflate(R.layout.actionbar_main, null);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, layoutParams);
    }

    private void initListView() {
        mListView = (SwipeDismissListView) findViewById(R.id.listView);
        View view = new View(this);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utility.getActionBarHeight(this)
        );

        if (Build.VERSION.SDK_INT >= 19) {
            p.height += Utility.getStatusBarHeight(this);
        }

        view.setLayoutParams(p);

        // mListView.addHeaderView(view); //这里出现了奇怪的问题 一添加就报错

        mListView.setOnDismissCallback(new SwipeDismissListView.OnDismissCallback() {

            @Override
            public void onDismiss(int dismissPosition) {
                mList.remove(dismissPosition);
                refreshListView();
            }

        });
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
