package smart.sprinkler.app.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import smart.sprinkler.app.api.RetrofitClient
import smart.sprinkler.app.api.model.DailyForecast
import smart.sprinkler.app.plusAssign


class WeatherViewModel : ViewModel() {
    private val mDisposables = CompositeDisposable()
    private val mDailyForecastLiveData = MutableLiveData<List<DailyForecast>>()
    val dailyForecast: LiveData<List<DailyForecast>> = mDailyForecastLiveData

    init {
        mDisposables +=
            RetrofitClient
                .getWeatherForecast()
                .map { it.daily }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { dailyForecast, exception ->
                    mDailyForecastLiveData.value = dailyForecast
                    Log.d(TAG, "getWeatherForecast() error ${exception.message}")
                }

    }

    override fun onCleared() {
        super.onCleared()
        mDisposables.clear()
    }

    companion object {
        private val TAG = WeatherViewModel::class.java.simpleName
    }
}
