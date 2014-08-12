package cn.fython.weathercard.support;

import android.content.Context;
import android.util.TypedValue;

public class Utility {

    public static int getActionBarHeight(Context context) {
        TypedValue v = new TypedValue();

        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, v, true)) {
            return TypedValue.complexToDimensionPixelSize(v.data, context.getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
