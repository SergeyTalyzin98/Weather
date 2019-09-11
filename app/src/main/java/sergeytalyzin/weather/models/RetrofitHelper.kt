package sergeytalyzin.weather.models

import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import sergeytalyzin.weather.interfeces.WeatherApi

class RetrofitHelper {

    private val keyApi = "e5a1fb49cfca9578aed2f92b6dc29a46"
    private val api = Retrofit.Builder()
        .baseUrl("https://ru.api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(WeatherApi::class.java)

    suspend fun getWeather(city: String) : WeatherModel =
        api.getWeather(city = city, key = keyApi).await()
}