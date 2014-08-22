package cn.fython.weathercard.ui;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.DataHelper;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.data.WeatherList;
import cn.fython.weathercard.support.CityNotFoundException;
import cn.fython.weathercard.support.Settings;
import cn.fython.weathercard.support.Utility;
import cn.fython.weathercard.support.WeatherIconHelper;
import cn.fython.weathercard.support.WeatherTools;
import cn.fython.weathercard.ui.fragment.DailyFragment;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class DetailActivity extends SwipeBackActivity {

    private TextView tv_content;
    private ImageView iv_weather;
    private LinearLayout layout;

    private Settings mSets;

    private DataHelper mDataHelper;
    private WeatherList mWeatherList;
    private Weather mWeather;
    private Weather[] dayW;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable SystemBarTint for SDK >=19
        if (Build.VERSION.SDK_INT >= 19) {
            Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.transparent)));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        index = getIntent().getIntExtra("index", -1);

        mSets = Settings.getInstance(getApplicationContext());
        mDataHelper = new DataHelper(getApplicationContext());
        mWeatherList = mDataHelper.readFromInternal();
        mWeather = mWeatherList.get(index);
        dayW = new Weather[4];

        layout = (LinearLayout) findViewById(R.id.layout_detail);
        iv_weather = (ImageView) findViewById(R.id.iv_weather);
        tv_content = (TextView) findViewById(R.id.tv_content);

        initContent();
        initHeader();
        initSwipeBackLayout();
        initBackground();
        initActionBar();

        new RefreshTask().execute();
    }

    private void initHeader() {
        View view = findViewById(R.id.header);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utility.getActionBarHeight(this)
        );

        if (Build.VERSION.SDK_INT >= 19) {
            p.height += Utility.getStatusBarHeight(this);
        }

        p.height += 10;
        view.setLayoutParams(p);
    }

    private void initBackground() {
        if (mSets.getBoolean(Settings.Field.BACKGROUND, true)) {
            Drawable backgroundRes = Utility.getWallpaperBackground(getApplicationContext(), true);
            if (Build.VERSION.SDK_INT >= 16) {
                layout.setBackground(backgroundRes);
            } else {
                layout.setBackgroundDrawable(backgroundRes);
            }
        } else {
            layout.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void initSwipeBackLayout() {
        getSwipeBackLayout().setEdgeTrackingEnabled(
                mSets.getBoolean(Settings.Field.SWIPEBACK_ENABLED, true) ?
                        SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT : 0
        );
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(mWeather.get(Weather.Field.City));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void initContent() {
        iv_weather.setImageResource(
                WeatherIconHelper.getDrawableResourceByStatus(mWeather.get(Weather.Field.Status0))
        );
        tv_content.setText(
                String.format(
                        getString(R.string.tv_content),
                        mWeather.get(Weather.Field.Status0),
                        mWeather.get(Weather.Field.Temperature0),
                        mWeather.get(Weather.Field.Temperature1),
                        mWeather.get(Weather.Field.Power0),
                        mWeather.get(Weather.Field.Direction0),
                        mWeather.get(Weather.Field.Zwx),
                        mWeather.get(Weather.Field.Pollution)
                )
        );
    }

    public void initForecast() {
        replaceToFragment(R.id.fl0, DailyFragment.getInstance(
                        "明天",
                        dayW[0]
                )
        );
        replaceToFragment(R.id.fl1, DailyFragment.getInstance(
                        "后天",
                        dayW[1]
                )
        );
        replaceToFragment(R.id.fl2, DailyFragment.getInstance(
                        "大后天",
                        dayW[2]
                )
        );
        replaceToFragment(R.id.fl3, DailyFragment.getInstance(
                        "大大后天",
                        dayW[3]
                )
        );
    }

    public void replaceToFragment(int id, Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(id, fragment).commit();
    }

    public class RefreshTask extends AsyncTask<Void, Void, Weather> {

        @Override
        protected Weather doInBackground(Void... voids) {
            Weather w = null;
            try {
                w = mWeatherList.getAfterRefreshing(index);
                dayW[0] = WeatherTools.getWeatherByCity(w.get(Weather.Field.City), 1);
                dayW[1] = WeatherTools.getWeatherByCity(w.get(Weather.Field.City), 2);
                dayW[2] = WeatherTools.getWeatherByCity(w.get(Weather.Field.City), 3);
                dayW[3] = WeatherTools.getWeatherByCity(w.get(Weather.Field.City), 4);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CityNotFoundException e) {
                e.printStackTrace();
            }
            return w;
        }

        @Override
        protected void onPostExecute(Weather w) {
            if (w != null && !DetailActivity.this.isDestroyed()) {
                mWeather = w;
                initContent();
                initForecast();
            }
        }

    }

}
