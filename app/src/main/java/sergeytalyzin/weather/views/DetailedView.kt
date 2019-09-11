package sergeytalyzin.weather.views

import com.arellomobile.mvp.MvpView
import sergeytalyzin.weather.models.WeatherModel

interface DetailedView: MvpView {

    fun startLoading()
    fun endLoading()
    fun showWeather(data: WeatherModel)
    fun showErrorConnection()
}