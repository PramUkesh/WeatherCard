package cn.fython.weathercard.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Weather {

    public static class Field {

        public static final String City = "city", Status0 = "status1", Status1 = "status2",
            Direction0 = "direction1", Direction1 = "direction2", Power0 = "power1",
            Power1 = "power2", Chy_S = "chy_shuoming", Pollution_S = "pollution_s",
            Gm_L = "gm_l", Gm_S = "gm_s", Yd_L = "yd_l", Yd_S = "yd_s", SaveDate = "savedate_weather",
            Temperature0 = "temperature1", Temperature1 = "temperature2", Ssd = "ssd",
            Tgd0 = "tgd1", Tgd1 = "tgd2", Zwx = "zwx", Chy = "chy", Pollution = "pollution",
            Gm = "gm", Yd = "yd";

    }

    private JSONObject data;

    public Weather() {
        data = new JSONObject();
    }

    public Weather(String jsonstr) {
        try {
            data = new JSONObject(jsonstr);
        } catch (JSONException e) {
            data = new JSONObject();
            e.printStackTrace();
        }
    }

    public Weather(JSONObject jsonobj) {
        data = jsonobj;
    }

    public String get(String name) {
        try {
            return data.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getJSONObject () {
        return data;
    }

    public String toJSONString() {
        return data.toString();
    }

}
