package smart.sprinkler.app.api.model

import com.google.gson.annotations.SerializedName

data class CurrentWeatherForecast(
    val id: Long,
    @SerializedName("main")
    val weather: CurrentWeather
)
