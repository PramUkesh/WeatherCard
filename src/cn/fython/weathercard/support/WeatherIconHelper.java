package cn.fython.weathercard.support;

import cn.fython.weathercard.R;

public class WeatherIconHelper {

    public static int getDrawableResourceByStatus(String statusName) {
        if (statusName.indexOf("雾") != -1) {
            return R.drawable.weather_foggy;
        }
        if (statusName.indexOf("雨") != -1) {
            return statusName.indexOf("雷") != -1 || statusName.indexOf("暴") != -1 ?
                    R.drawable.weather_storm : R.drawable.weather_rainy;
        }
        if (statusName.indexOf("云") != -1) {
            return statusName.indexOf("多") != -1 ? R.drawable.weather_cloudy_big : R.drawable.weather_cloudy;
        }
        if (statusName.indexOf("晴") != -1) {
            return R.drawable.weather_sunny;
        }
        if (statusName.indexOf("雹") != -1) {
            return R.drawable.weather_hail;
        }
        return 0;
    }

}
