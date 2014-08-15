package cn.fython.weathercard.support;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import cn.fython.weathercard.R;

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

    public static Drawable getWallpaperBackground(Context context, boolean isLighter) {
        WallpaperManager wm = WallpaperManager.getInstance(context);
        Drawable backgroundD = wm.getDrawable();

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);

        Bitmap bitmap = ((BitmapDrawable) backgroundD).getBitmap();
        Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, dm.widthPixels, dm.heightPixels);

        Drawable colorD = new ColorDrawable(context.getResources().getColor(
                isLighter ? R.color.windowsTranslucentColor_lighter : R.color.windowsTranslucentColor
        ));
        Drawable[] arrD = new Drawable[] {new BitmapDrawable(b), colorD};
        LayerDrawable lD = new LayerDrawable(arrD);
        return lD;
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
