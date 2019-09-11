package sergeytalyzin.weather.presenters

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import sergeytalyzin.weather.models.RetrofitHelper
import sergeytalyzin.weather.models.WeatherModel
import sergeytalyzin.weather.services.ListenerService
import sergeytalyzin.weather.views.DetailedView
import java.net.UnknownHostException

@InjectViewState
class DetailedPresenter : MvpPresenter<DetailedView>() {

    private var weatherAboutCity: WeatherModel? = null

    fun startGetWeather(cityCode: String) {

        if (weatherAboutCity == null) {
            GlobalScope.launch(Dispatchers.Main) {

                try {
                    viewState.startLoading()
                    weatherAboutCity = RetrofitHelper().getWeather(city = cityCode)
                    weatherAboutCity!!.kelvinToDegrees()
                    viewState.showWeather(weatherAboutCity!!)
                    viewState.endLoading()
                } catch (e: UnknownHostException) {
                    viewState.showErrorConnection()
                }
            }
        }
    }

    fun checkService(context: Context, service: Service) : Boolean {

        (context.getSystemService(ACTIVITY_SERVICE) as ActivityManager)
            .getRunningServices(Integer.MAX_VALUE).forEach {
                if(it.service.className == service.javaClass.name)
                    return true
            }

        return false
    }

    fun addListener(cityCode: String, cityName: String, context: Context, more: Boolean, less: Boolean, temp: String,
                    ok: (intent: Intent) -> Unit, error: (message: String) -> Unit) {

        val moreOrLess = if (more && !less)
            '+'
        else if (!more && less)
            '-'
        else {
            error("Проверьте правильность введенных данных")
            return
        }

        try {

            if(temp.toDouble() > 100) {
                error("Проверьте правильность введенных данных")
                return
            }

            val intent = Intent(context, ListenerService::class.java)
                .putExtra("cityCode", cityCode)
                .putExtra("cityName", cityName)
                .putExtra("temp", temp.toDouble())
                .putExtra("moreOrLess", moreOrLess)
            ok(intent)

        } catch (e: NumberFormatException) {
            error("Проверьте правильность введенных данных")
        }
    }
}