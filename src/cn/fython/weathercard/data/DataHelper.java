package cn.fython.weathercard.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataHelper {

    private Context mContext;

    private static final String TAG = "DataHelper";

    public DataHelper(Context context) {
        this.mContext = context;
    }

    public WeatherList readFromInternal() {
        String jsonData;
        try {
            jsonData = readFile(mContext, "data.json");
        } catch (IOException e) {
            jsonData = "{\"data\":[]}";
            Log.i(TAG, "文件不存在,初始化新的文件.");
            e.printStackTrace();
        }
        try {
            return new WeatherList(jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
            return new WeatherList();
        }
    }

    public void saveToInternal(WeatherList wl) {
        try {
            saveFile(mContext, "data.json", wl.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFile(Context context, String name, String text) throws IOException {
        FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
        fos.write(text.getBytes());
        fos.close();
    }

    private static String readFile(Context context, String name) throws IOException{
        File file = context.getFileStreamPath(name);
        InputStream is = new FileInputStream(file);

        byte b[] = new byte[(int) file.length()];

        is.read(b);
        is.close();

        String string = new String(b);

        return string;
    }

}
