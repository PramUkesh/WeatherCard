package cn.fython.weathercard.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;

import cn.fython.weathercard.R;
import cn.fython.weathercard.support.Weather;

public class WeatherCard extends Card {

    private Weather weather;

    public WeatherCard(Weather weather){
        this.weather = weather;
    }

    @Override
    public View getCardContent(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_weather, null);

        ((TextView) view.findViewById(R.id.title)).setText(weather.get(Weather.Field.City));
        ((TextView) view.findViewById(R.id.tv_tem_max)).setText(weather.get(Weather.Field.Temperature0) + "°C");
        ((TextView) view.findViewById(R.id.tv_tem_min)).setText(weather.get(Weather.Field.Temperature1) + "°C");

        return view;
    }




}