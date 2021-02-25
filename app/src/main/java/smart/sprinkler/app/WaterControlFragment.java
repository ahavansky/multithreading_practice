package smart.sprinkler.app;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import smart.sprinkler.app.view.WaterAreaGroup;
import smart.sprinkler.app.viewmodels.WeatherViewModel;

public class WaterControlFragment extends Fragment {
    public final static int WATER_AREA_NUMBER = 5;
    private String[] mWaterArea = new String[WATER_AREA_NUMBER];
    private final static int WEATHERCAST_DAYS = 3;
    private final Weathercast[] mWeathercastData = new Weathercast[WEATHERCAST_DAYS];
    // checkboxes at left
    private boolean[] mWaterAreaScheduler;
    // radiobutton's styled checkboxes at right
    private boolean[] mWaterAreaIndicator;
    private WaterAreaGroup mWaterAreaGroup;
    private static final String KEY_SCHEDULED_ARR = "scheduled";
    private WeatherViewModel mViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Array for weathercast data for today, tomorrow and the day next tomorrow;
        String[] date = getResources().getStringArray(R.array.waeathercast_date);
        TypedArray image = getResources().obtainTypedArray(R.array.control_water_weathercast_img);
        int[] temperature = getResources().getIntArray(R.array.control_water_weathercast_temp);

        for (int j = 0; j < WEATHERCAST_DAYS; j++) {
            mWeathercastData[j] = new Weathercast(date[j], image.getResourceId(j, 0), temperature[j]);
        }
        image.recycle();

        // Array for area names;
        mWaterArea = getResources().getStringArray(R.array.waterArea);

        // Array for area indication;
        mWaterAreaIndicator = getBooleanArray(getResources().getIntArray(R.array
                .water_control_indicator_state));


        if (savedInstanceState == null) {
            // Array for initial scheduled areas;
            mWaterAreaScheduler = getBooleanArray(getResources().getIntArray(R.array
                    .water_control_scheduler_state));
            mViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        } else {
            mWaterAreaScheduler = savedInstanceState.getBooleanArray(KEY_SCHEDULED_ARR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(KEY_SCHEDULED_ARR, mWaterAreaGroup.waterSchedulerState());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_control_water, null);

        // create weathercast list
        RecyclerView weatherCastList = myView.findViewById(R.id.weatherCastListView);
        weatherCastList.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false));
        weatherCastList.setAdapter(new WeathercastViewAdapter(R.layout.item_weathercast, mWeathercastData));

        // temp, humidity TextViews
        TextView conditionsTemp = myView.findViewById(R.id.waterControlValueTemp);
        conditionsTemp.setText(getString(R.string.value_degree, getResources().getInteger(R.integer.current_temp)));
        TextView conditionsMoisture = myView.findViewById(R.id.waterControlValueHumidity);
        conditionsMoisture.setText(getString(R.string.value_percent, getResources().getInteger(R.integer.current_humidity)));

        //sprinkle checkbox
        final CheckBox sprinkler = myView.findViewById(R.id.water_control_sprinkle);
        sprinkler.setOnClickListener(v -> {
            if (!sprinkler.isChecked()) {
                for (int i = 0; i < WATER_AREA_NUMBER; i++) {
                    mWaterAreaGroup.setWateringScheduler(i, false);
                }
            }
        });

        // create water area list
        mWaterAreaGroup = myView.findViewById(R.id.waterAreaGroup);
        mWaterAreaGroup.setLabels(mWaterArea);
        for (int k = 0; k < mWaterAreaIndicator.length; k++) {
            mWaterAreaGroup.setWateringIndicator(k, mWaterAreaIndicator[k]);
            mWaterAreaGroup.setWateringScheduler(k, mWaterAreaScheduler[k]);
        }

        mViewModel.getDailyForecast().observe(getViewLifecycleOwner(), dailyForecasts -> {
            // ui retated actions
        });

        return myView;
    }

    private boolean[] getBooleanArray(int[] iArray) {
        int length = iArray.length;
        boolean[] res = new boolean[length];
        for (int j = 0; j < length; j++) {
            res[j] = iArray[j] == 1;
        }
        return res;
    }

    private static class Weathercast {
        final String date;
        final int bg_id;
        final int temp;

        Weathercast(String date, int bgResId, int temperature) {
            this.date = date;
            this.bg_id = bgResId;
            this.temp = temperature;
        }
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        private TextView mDateView;
        private TextView mWeatherView;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            mDateView = itemView.findViewById(R.id.item_weathercast_date);
            mWeatherView = itemView.findViewById(R.id.item_weathercast_weather);
        }
    }

    private class WeathercastViewAdapter extends RecyclerView.Adapter<WeatherViewHolder> {
        private final Weathercast[] mWeathercasts;
        private final int layoutResourceId;

        WeathercastViewAdapter(int resource, Weathercast[] objects) {
            mWeathercasts = objects;
            layoutResourceId = resource;
        }

        @NonNull
        @Override
        public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
            return new WeatherViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
            if (mWeathercasts == null || position < 0 || position >= mWeathercasts.length) {
                return;
            }
            Weathercast weathercastItem = mWeathercasts[position];

            holder.mDateView.setText(weathercastItem.date);
            holder.mWeatherView.setText(getString(R.string.value_degree, weathercastItem.temp));
            Drawable weatherIcon = getResources().getDrawable(weathercastItem.bg_id);
            weatherIcon.setBounds(0, 0, 240, 160);
            holder.mWeatherView.setCompoundDrawables(weatherIcon, null, null, null);
        }

        @Override
        public int getItemCount() {
            if (mWeathercasts == null) {
                return 0;
            } else {
                return mWeathercasts.length;
            }
        }
    }
}
