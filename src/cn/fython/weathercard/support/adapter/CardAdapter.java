package cn.fython.weathercard.support.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.fython.weathercard.R;
import cn.fython.weathercard.data.Weather;
import cn.fython.weathercard.data.WeatherList;

public class CardAdapter extends BaseAdapter {

    private WeatherList mList;
    private OnMoreButtonClickListener onMoreButtonClickListener;

    private Context mContext;

    public CardAdapter(Context context, WeatherList weatherList,
                       OnMoreButtonClickListener onMoreButtonClickListener) {
        mContext = context;
        mList = weatherList;
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    @Override
    public int getCount() {
        return mList.getSize();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder mHolder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.card_weather, null);

            mHolder = new ViewHolder();
            mHolder.tv_city = (TextView) view.findViewById(R.id.title);
            mHolder.tv_max_tem = (TextView) view.findViewById(R.id.tv_tem_max);
            mHolder.tv_min_tem = (TextView) view.findViewById(R.id.tv_tem_min);
            mHolder.iv_weather = (ImageView) view.findViewById(R.id.iv_weather);
            view.findViewById(R.id.ib_more).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (onMoreButtonClickListener != null) {
                        onMoreButtonClickListener.onMoreButtonClick(i);
                    }
                }

            });

            view.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) view.getTag();
        }

        mHolder.tv_city.setText(mList.getName(i));
        mHolder.tv_max_tem.setText(mList.get(i).get(Weather.Field.Temperature0) + "°C");
        mHolder.tv_min_tem.setText(mList.get(i).get(Weather.Field.Temperature1) + "°C");

        return view;
    }

    private class ViewHolder {

        public TextView tv_city, tv_max_tem, tv_min_tem;
        public ImageView iv_weather;

    }

    public interface OnMoreButtonClickListener {
        public void onMoreButtonClick(int position);
    }

}
