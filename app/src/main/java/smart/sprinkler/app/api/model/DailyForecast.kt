package smart.sprinkler.app.api.model

import java.text.SimpleDateFormat
import java.util.*

data class DailyForecast(
    val dt: Long,
    val temp: Temperature
) {
    fun getDate(): String {
        return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).run {
            format(Date(dt * 1000L).apply { timeZone = TimeZone.getTimeZone("Europe/Moscow") })
        }
    }
}
