package cn.fython.weathercard.support;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

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

    @TargetApi(19)
    public static void enableTint(Activity activity, Drawable drawable) {
        if (Build.VERSION.SDK_INT < 19) return;

        Window w = activity.getWindow();
        WindowManager.LayoutParams p = w.getAttributes();
        p.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        w.setAttributes(p);

        SystemBarTintManager m = new SystemBarTintManager(activity);
        m.setStatusBarTintEnabled(true);
        m.setStatusBarTintDrawable(drawable);
    }

}
