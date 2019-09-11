package sergeytalyzin.weather.interfeces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import sergeytalyzin.weather.models.WeatherModel

interface WeatherApi {

    @GET("weather?")
    fun getWeather(@Query("q") city: String, @Query("APPID") key: String): Call<WeatherModel>
}