package cn.fython.weathercard.support;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Map;

import cn.fython.weathercard.data.Weather;

public class WeatherTools {

    private static final String URL_SINA = "http://php.weather.sina.com.cn/xml.php?city=%s&password=DJOYnieT8234jlsK&day=%d";
    private static final String[] nodes = {
            "city", "status1", "status2", "direction1", "direction2", "power1", "power2",
            "chy_shuoming", "pollution_s", "gm_l", "gm_s", "yd_l", "yd_s", "savedate_weather",
            "temperature1", "temperature2", "ssd", "tgd1", "tgd2", "zwx", "chy", "pollution",
            "gm", "yd"
    };

    public static Weather getWeatherByCity(String cityName, int days) {
        String urlStr = null;
        try {
            urlStr = String.format(URL_SINA, URLEncoder.encode(cityName, "GBK"), days);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("Api", urlStr);

        XmlParser xp;
        Weather w;

        try {
            HttpGet httpGet = new HttpGet(urlStr);
            HttpResponse response = new DefaultHttpClient().execute(httpGet);

            xp = new XmlParser(response.getEntity().getContent());

            Map<String, String> map = xp.getValue(nodes);
            JSONObject data = new JSONObject();
            for (int i = 0; i < nodes.length; i++) {
                try {
                    data.put(nodes[i], map.get(nodes[i]));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            w = new Weather(data);
            Log.w("WeatherTools", w.toJSONString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return w;
    }

}
