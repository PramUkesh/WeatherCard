package cn.fython.weathercard.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.DataHelper;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.data.WeatherList;
import cn.fython.weathercard.support.CityNotFoundException;
import cn.fython.weathercard.support.Utility;
import cn.fython.weathercard.support.WeatherTools;
import cn.fython.weathercard.support.adapter.CardAdapter;
import cn.fython.weathercard.view.SwipeDismissListView;

public class MainActivity extends Activity implements View.OnTouchListener {

	private static SwipeDismissListView mListView;
    private static EditText mSearchEditText;
    private static CardAdapter mAdapter;

    public static UIHandler mUIHandler;

    private WeatherList mList;
    private DataHelper mDataHelper;

    private float mLastY = -1.0f;

    public static final int FIELD_NULL = 0, FIELD_NETWORK_NULL = 1,
            FIELD_CITY_NOT_FOUND = 2, FIELD_ADD_CARD = 3, FIELD_ADAPTER_NOTIFY_DATA_CHANGED = 4,
            FIELD_REFERSH_FINISHED = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 19) {
            Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.transparent)));
        }

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        RelativeLayout l = (RelativeLayout) findViewById(R.id.layout_main);
        Drawable backgroundRes = Utility.getWallpaperBackground(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 16) {
            l.setBackground(backgroundRes);
        } else {
            l.setBackgroundDrawable(backgroundRes);
        }

        mDataHelper = new DataHelper(getApplicationContext());
        mList = mDataHelper.readFromInternal();
        mUIHandler = new UIHandler(getApplicationContext());

        initActionBar();
        initListView();
        refreshAllWeatherCard();

	}

    private void initActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
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

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, layoutParams);
    }

    private void initListView() {
        mListView = (SwipeDismissListView) findViewById(R.id.listView);
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

    private void refreshAllWeatherCard() {
        new RefreshCardTask().execute();
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
            if (wl != null) {
                refreshListView();
                mUIHandler.sendEmptyMessage(FIELD_REFERSH_FINISHED);
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
                case FIELD_REFERSH_FINISHED:
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
