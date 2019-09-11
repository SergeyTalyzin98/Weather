package sergeytalyzin.weather.views

import com.arellomobile.mvp.MvpView

interface CitiesView: MvpView {

    fun startLoading()
    fun endLoading()
    fun startDetailedActivity(cityCode: String, cityName: String)
}