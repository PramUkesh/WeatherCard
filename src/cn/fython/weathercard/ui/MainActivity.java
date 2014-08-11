package cn.fython.weathercard.ui;

import com.fima.cardsui.views.CardUI;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import cn.fython.weathercard.R;
import cn.fython.weathercard.card.WeatherCard;
import cn.fython.weathercard.support.Weather;
import cn.fython.weathercard.support.WeatherTools;

public class MainActivity extends Activity {

	private static CardUI mCardUI; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCardUI = (CardUI) findViewById(R.id.cardUI);
        mCardUI.setSwipeable(true);
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

    public class CheckTask extends AsyncTask<Void, Void, Weather> {

        public String cityName;
        public int days;

        @Override
        protected Weather doInBackground(Void... voids) {
            return WeatherTools.getWeatherByCity(cityName, days);
        }

        @Override
        protected void onPostExecute(Weather weather) {
            mCardUI.addCard(new WeatherCard(weather), true);
        }

    }
}
