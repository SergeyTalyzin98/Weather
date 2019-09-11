package sergeytalyzin.weather.presenters

import android.content.Context
import androidx.room.Room
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import sergeytalyzin.weather.adapters.CitiesAdapter
import sergeytalyzin.weather.models.City
import sergeytalyzin.weather.models.MyDataBase
import sergeytalyzin.weather.models.RetrofitHelper
import sergeytalyzin.weather.views.CitiesView
import java.net.UnknownHostException

@InjectViewState
class CitiesPresenter(context: Context): MvpPresenter<CitiesView>() {

    private val db = Room.databaseBuilder(context, MyDataBase::class.java, "db_cities").build()

    fun getAndSetCities(adapter: CitiesAdapter) {

        GlobalScope.launch {
            val cities = db.roomDao.getAllCity()

            GlobalScope.launch(Dispatchers.Main) {
                adapter.setList(cities)
            }
        }
    }

    fun deleteCity(city: City, adapter: CitiesAdapter) {

        GlobalScope.launch {
            db.roomDao.delete(city)

            GlobalScope.launch(Dispatchers.Main) {
                getAndSetCities(adapter)
            }
        }
    }

    fun addCity(name: String, code: String, completed: () -> Unit, error: (message: String) -> Unit ) {

        GlobalScope.launch {
            val city = City()
            city.name = name
            city.code = code
            try {
                RetrofitHelper().getWeather(city = city.code!!)
                if(db.roomDao.getCity(city.name!!) == null) {
                    db.roomDao.insert(city = city)
                    GlobalScope.launch(Dispatchers.Main) {
                        completed()
                    }
                }
                else {
                    GlobalScope.launch(Dispatchers.Main) {
                        error("Такой город уже существует")
                    }
                }
            }catch (e: HttpException) {
                GlobalScope.launch(Dispatchers.Main) {
                    error("Проверьте правильность введенных данных")
                }
            } catch (e: UnknownHostException) {
                GlobalScope.launch(Dispatchers.Main) {
                    error("Ошибка соединения с сервером")
                }
            }
        }
    }

    fun openDetailedActivity(cityCode: String, cityName: String) =
        viewState.startDetailedActivity(cityCode = cityCode, cityName = cityName)
}