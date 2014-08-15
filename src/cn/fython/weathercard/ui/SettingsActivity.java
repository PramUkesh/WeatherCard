package cn.fython.weathercard.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import cn.fython.weathercard.R;
import cn.fython.weathercard.support.Settings;
import cn.fython.weathercard.support.Utility;
import me.imid.swipebacklayout.lib.app.SwipeBackPreferenceActivity;

public class SettingsActivity extends SwipeBackPreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {

    private CheckBoxPreference mPrefSwipeBack, mPrefBackground;
    private Preference mPrefVersion;
    private Preference mPrefProjWeb;

    private Settings mSets;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        mSets = Settings.getInstance(getApplicationContext());

        // Enable SystemBarTint for SDK >=19
        if (Build.VERSION.SDK_INT >= 19) {
            getListView().setFitsSystemWindows(true);
            Utility.enableTint(this, new ColorDrawable(getResources().getColor(R.color.transparent)));
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);
        initBackground();

        mPrefSwipeBack = (CheckBoxPreference) findPreference(Settings.Field.SWIPEBACK_ENABLED);
        mPrefBackground = (CheckBoxPreference) findPreference(Settings.Field.BACKGROUND);
        mPrefVersion = findPreference("version");
        mPrefProjWeb = findPreference("project_web");

        String version = "Unknown";
        int ver_num = 2333333;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            ver_num = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (Exception e) {
            // Keep the default value
        }

        mPrefSwipeBack.setChecked(mSets.getBoolean(Settings.Field.SWIPEBACK_ENABLED, true));
        mPrefBackground.setChecked(mSets.getBoolean(Settings.Field.BACKGROUND, true));
        mPrefVersion.setSummary(String.format(getString(R.string.content_version_code), version) + "(" + ver_num +")");

        mPrefSwipeBack.setOnPreferenceChangeListener(this);
        mPrefBackground.setOnPreferenceChangeListener(this);
        mPrefProjWeb.setOnPreferenceClickListener(this);
    }

    private void initBackground() {
        View v = getListView();
        if (mSets.getBoolean(Settings.Field.BACKGROUND, true)) {
            Drawable backgroundRes = Utility.getWallpaperBackground(getApplicationContext(), true);
            if (Build.VERSION.SDK_INT >= 16) {
                v.setBackground(backgroundRes);
            } else {
                v.setBackgroundDrawable(backgroundRes);
            }
        } else {
            v.setBackgroundColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public boolean onPreferenceClick(Preference pref) {
        if (pref == mPrefProjWeb) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse(getString(R.string.content_project_web)));
            startActivity(i);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference pref, Object o) {
        Boolean b = Boolean.parseBoolean(o.toString());

        if (pref == mPrefSwipeBack) {
            mSets.putBoolean(Settings.Field.SWIPEBACK_ENABLED, b);
            showNeedRestartToast();
            return true;
        } else if (pref == mPrefBackground) {
            mSets.putBoolean(Settings.Field.BACKGROUND, b);
            showNeedRestartToast();
            return true;
        }

        return false;
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

    private void showNeedRestartToast() {
        Toast.makeText(
                getApplicationContext(),
                getString(R.string.need_restart),
                Toast.LENGTH_SHORT
        ).show();
    }

}
