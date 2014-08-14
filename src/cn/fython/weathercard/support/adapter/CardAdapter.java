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
import cn.fython.weathercard.support.WeatherIconHelper;

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
    public Weather getItem(int i) {
        return mList.get(i - 1);
    }

    @Override
    public long getItemId(int i) {
        return i - 1;
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
            mHolder.tv_date = (TextView) view.findViewById(R.id.tv_date);
            mHolder.tv_status = (TextView) view.findViewById(R.id.tv_status);
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
        mHolder.tv_status.setText(mList.get(i).get(Weather.Field.Status0));
        mHolder.tv_date.setText(
                String.format(mContext.getString(R.string.refresh_date),
                        mList.get(i).get(Weather.Field.SaveDate)
                )
        );
        mHolder.iv_weather.setImageResource(
                WeatherIconHelper.getDrawableResourceByStatus(
                        mList.get(i).get(Weather.Field.Status0)
                )
        );

        return view;
    }

    private class ViewHolder {

        public TextView tv_city, tv_max_tem, tv_min_tem, tv_status, tv_date;
        public ImageView iv_weather;

    }

    public interface OnMoreButtonClickListener {
        public void onMoreButtonClick(int position);
    }

}
