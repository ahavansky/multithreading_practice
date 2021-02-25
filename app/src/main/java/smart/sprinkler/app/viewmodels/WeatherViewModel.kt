package smart.sprinkler.app.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import smart.sprinkler.app.api.RetrofitClient
import smart.sprinkler.app.api.model.DailyForecast

class WeatherViewModel : ViewModel() {
    private val mDailyForecastLiveData = MutableLiveData<List<DailyForecast>>()
    val dailyForecast: LiveData<List<DailyForecast>> = mDailyForecastLiveData

    init {
        viewModelScope.launch {
            try {
                mDailyForecastLiveData.value = RetrofitClient.getWeatherForecast().daily
            } catch (e: Exception) {
                Log.d(TAG, "getWeatherForecast() error ${e.message}")
            }
        }

    }

    companion object {
        private val TAG = WeatherViewModel::class.java.simpleName
    }
}
