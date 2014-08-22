package cn.fython.weathercard.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.support.WeatherIconHelper;

public class DailyFragment extends Fragment {

    public static DailyFragment getInstance(String title, Weather weather) {
        DailyFragment dailyFragment = new DailyFragment();

        Bundle data = new Bundle();
        data.putString("title", title);
        data.putString("data", weather.toJSONString());
        dailyFragment.setArguments(data);

        return dailyFragment;
    }

    public DailyFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.weather_daily, null);

        String title = getArguments().getString("title");
        Weather mWeather = new Weather(getArguments().getString("data"));
        Log.i("TAG", mWeather.toJSONString());

        String statusName = mWeather.get(Weather.Field.Status0);

        ((TextView) v.findViewById(R.id.tv_dayname)).setText(title);
        ((ImageView) v.findViewById(R.id.iv_weather)).setImageResource(
                WeatherIconHelper.getDrawableResourceByStatus(statusName)
        );
        ((TextView) v.findViewById(R.id.tv_temperature)).setText(
                mWeather.get(Weather.Field.Temperature0)
                        + "/"
                        + String.format(
                        getString(R.string.temperature_c),
                        mWeather.get(Weather.Field.Temperature1)
                )
        );
        ((TextView) v.findViewById(R.id.tv_weather)).setText(statusName);

        return v;
    }

}
