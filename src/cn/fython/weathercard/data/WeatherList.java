package cn.fython.weathercard.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cn.fython.weathercard.support.CityNotFoundException;
import cn.fython.weathercard.support.WeatherTools;

public class WeatherList {

    private ArrayList<Weather> array;

    public WeatherList() {
        array = new ArrayList<Weather>();
    }

    public WeatherList(String json) throws JSONException {
        array = new ArrayList<Weather>();

        JSONObject jsonObj = new JSONObject(json);
        JSONArray jsonArray = jsonObj.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            array.add(new Weather(jsonArray.getJSONObject(i)));
        }
    }

    public void add(Weather w) {
        array.add(w);
    }

    public void set(int position, Weather w) {
        array.set(position, w);
    }

    public int getSize() {
        return array.size();
    }

    public void remove(int position) {
        array.remove(position);
    }

    public String getName(int position) {
        return array.get(position).get(Weather.Field.City);
    }

    public Weather get(int position) {
        return array.get(position);
    }

    public Weather getAfterRefreshing(int position) throws IOException, CityNotFoundException {
        Weather w = WeatherTools.getWeatherByCity(array.get(position).get(Weather.Field.City), 0);
        array.set(position, w);
        return w;
    }

    public WeatherList refreshAll() throws IOException, CityNotFoundException {
        for (int i = 0; i < array.size(); i++) {
            array.set(i, WeatherTools.getWeatherByCity(array.get(i).get(Weather.Field.City), 0));
        }
        return this;
    }

    public String toJSONString() {
        JSONObject jsonObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            jsonArray.put(array.get(i).getJSONObject());
        }
        try {
            jsonObj.put("data", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

}
