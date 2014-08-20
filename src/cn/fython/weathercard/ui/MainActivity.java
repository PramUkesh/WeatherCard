package cn.fython.weathercard.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.DataHelper;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.data.WeatherList;
import cn.fython.weathercard.support.CityNotFoundException;
import cn.fython.weathercard.support.Settings;
import cn.fython.weathercard.support.Utility;
import cn.fython.weathercard.support.WeatherTools;
import cn.fython.weathercard.support.adapter.CardAdapter;
import cn.fython.weathercard.view.SwipeDismissListView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class MainActivity extends SwipeBackActivity implements View.OnTouchListener {

    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private static SwipeBackLayout mSwipeBackLayout;
	private static SwipeDismissListView mListView;
    private static EditText mSearchEditText;
    private static CardAdapter mAdapter;

    public static UIHandler mUIHandler;

    private WeatherList mList;
    private DataHelper mDataHelper;

    private Settings mSets;

    private float mLastY = -1.0f;

    public static final int FIELD_NULL = 0, FIELD_NETWORK_NULL = 1,
            FIELD_CITY_NOT_FOUND = 2, FIELD_ADD_CARD = 3, FIELD_ADAPTER_NOTIFY_DATA_CHANGED = 4,
            FIELD_REFRESH_FINISHED = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        // Enable SystemBarTint for SDK >=19
        if (Build.VERSION.SDK_INT >= 19) {
            Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.transparent)));
        }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        // Init data
        mSets = Settings.getInstance(getApplicationContext());
        mDataHelper = new DataHelper(getApplicationContext());
        mList = mDataHelper.readFromInternal();
        mUIHandler = new UIHandler(getApplicationContext());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_main);
        mSwipeBackLayout = getSwipeBackLayout();
        mListView = (SwipeDismissListView) findViewById(R.id.listView);

        // Init UI
        initBackground();
        initSwipeRefreshLayout();
        initSwipeBackLayout();
        initActionBar();
        initListView();
        refreshListView();
        refreshAllWeatherCard(false);

	}

    private void initBackground() {
        if (mSets.getBoolean(Settings.Field.BACKGROUND, true)) {
            Drawable backgroundRes = Utility.getWallpaperBackground(getApplicationContext(), false);
            if (Build.VERSION.SDK_INT >= 16) {
                mSwipeRefreshLayout.setBackground(backgroundRes);
            } else {
                mSwipeRefreshLayout.setBackgroundDrawable(backgroundRes);
            }
        } else {
            mSwipeRefreshLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_blue_bright
        );
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshAllWeatherCard(true);
            }

        });
    }

    private void initSwipeBackLayout() {
        mSwipeBackLayout.setEdgeTrackingEnabled(
                mSets.getBoolean(Settings.Field.SWIPEBACK_ENABLED, true) ?
                SwipeBackLayout.EDGE_LEFT | SwipeBackLayout.EDGE_RIGHT : 0
        );
    }

    private void initActionBar() {
        ActionBar actionBar = getActionBar();

        // Hide title & icon
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Set up custom view
        View v = LayoutInflater.from(
                new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light)
        ).inflate(R.layout.actionbar_main, null);

        mSearchEditText = (EditText) v.findViewById(R.id.editText);
        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_NEXT) {
                    if (mSearchEditText.getText().toString().trim().length() < 1) {
                        mSearchEditText.clearFocus();
                    } else {
                        createSampleWeatherCard(mSearchEditText.getText().toString());
                    }
                    mSearchEditText.setText("");
                    return true;
                }
                return false;
            }
        });

        ImageButton ib_settings = (ImageButton) v.findViewById(R.id.imageButton);
        ib_settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.setClass(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }

        });
        ib_settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.item_settings),
                        Toast.LENGTH_SHORT
                ).show();
                return true;
            }
        });

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, layoutParams);
    }

    private void initListView() {
        // Add header to mListView
        View view = new View(this);
        ListView.LayoutParams p = new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Utility.getActionBarHeight(this)
        );

        if (Build.VERSION.SDK_INT >= 19) {
            p.height += Utility.getStatusBarHeight(this);
        }

        p.height += 10;
        view.setLayoutParams(p);
        mListView.addHeaderView(view);
        mListView.setHeaderClickable(false);

        // Add footer to mListView
        TextView tv = new TextView(this);
        ListView.LayoutParams p0 = new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        tv.setPadding(20, 20, 20, 20);
        tv.setGravity(Gravity.RIGHT);
        tv.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Holo_Small_Inverse);
        tv.setText(getString(R.string.footer_api_provider));

        tv.setLayoutParams(p);
        mListView.addFooterView(tv);
        mListView.setFooterClickable(false);

        // Set up Callback
        mListView.setOnTouchListener(this);
        mListView.setOnDismissCallback(new SwipeDismissListView.OnDismissCallback() {

            @Override
            public void onDismiss(int dismissPosition) {
                mList.remove(dismissPosition - 1);
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

    private void refreshAllWeatherCard(boolean showToast) {
        RefreshCardTask task = new RefreshCardTask();
        task.showToast = showToast;
        task.execute();
    }

    private void refreshListView() {
        mAdapter = new CardAdapter(
                new ContextThemeWrapper(MainActivity.this, android.R.style.Theme_Holo_Light),
                mList,
                new CardAdapter.OnMoreButtonClickListener() {
                    @Override
                    public void onMoreButtonClick(int position) {
                        showMoreMenu(position);
                    }
                }
        );
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mLastY == -1.0f) break;

                float y = ev.getY();

                if (y < mLastY - 10f) {
                    getActionBar().hide();
                } else if (y > mLastY + 10f) {
                    getActionBar().show();
                }

                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastY = -1.0f;
                break;
        }

        return false;
    }

    @Override
    public void onPause() {
        mDataHelper.saveToInternal(mList);
        super.onPause();
    }

    private void showMoreMenu(final int position){
        new AlertDialog.Builder(this).setItems(getResources().getStringArray(R.array.card_more_menu),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        switch (id) {
                            case 0:
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            mAdapter.setItem(position, mList.getAfterRefreshing(position));
                                            mUIHandler.sendEmptyMessage(FIELD_ADAPTER_NOTIFY_DATA_CHANGED);
                                        } catch (IOException e) {
                                            mUIHandler.sendEmptyMessage(FIELD_NETWORK_NULL);
                                            e.printStackTrace();
                                        } catch (CityNotFoundException e) {
                                            mUIHandler.sendEmptyMessage(FIELD_CITY_NOT_FOUND);
                                            e.printStackTrace();
                                        }
                                    }
                                }.start();
                                break;
                        }
                    }
                }
        ).show();
    }

    public class CheckTask extends AsyncTask<Void, Void, Weather> {

        public String cityName;
        public int days;

        @Override
        protected Weather doInBackground(Void... voids) {
            try {
                return WeatherTools.getWeatherByCity(cityName, days);
            } catch (IOException e) {
                mUIHandler.sendEmptyMessage(FIELD_NETWORK_NULL);
                e.printStackTrace();
                return null;
            } catch (CityNotFoundException e) {
                mUIHandler.sendEmptyMessage(FIELD_CITY_NOT_FOUND);
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Weather weather) {
            if (weather != null) {
                Message msg = new Message();
                msg.what = FIELD_ADD_CARD;
                Bundle data = new Bundle();
                data.putString("jsonString", weather.toJSONString());
                msg.setData(data);
                mUIHandler.sendMessage(msg);
            }
        }

    }

    public class RefreshCardTask extends AsyncTask<Void, Void, WeatherList> {

        public boolean showToast = false;

        @Override
        protected WeatherList doInBackground(Void... voids) {
            try {
                mList = mList.refreshAll();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CityNotFoundException e) {
                e.printStackTrace();
            }
            return mList;
        }

        @Override
        protected void onPostExecute(WeatherList wl) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (wl != null) {
                refreshListView();
                if (showToast) {
                    mUIHandler.sendEmptyMessage(FIELD_REFRESH_FINISHED);
                }
            }
        }

    }

    public class UIHandler extends Handler {

        private Context mContext;

        public UIHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIELD_NULL:
                    /* Handler Default Value */
                    /* Do nothing */
                    break;
                case FIELD_NETWORK_NULL:
                    Toast.makeText(mContext,
                            mContext.getString(R.string.error_network_null),
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
                case FIELD_CITY_NOT_FOUND:
                    Toast.makeText(mContext,
                            mContext.getString(R.string.error_city_not_found),
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
                case FIELD_REFRESH_FINISHED:
                    Toast.makeText(mContext,
                            mContext.getString(R.string.refresh_finished),
                            Toast.LENGTH_SHORT)
                            .show();
                    break;
                case FIELD_ADD_CARD:
                    Bundle data = msg.getData();
                    mList.add(new Weather(data.getString("jsonString")));
                    refreshListView();
                    break;
                case FIELD_ADAPTER_NOTIFY_DATA_CHANGED:
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }

    };

}
