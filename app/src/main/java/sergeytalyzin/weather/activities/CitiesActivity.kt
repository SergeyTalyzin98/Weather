package sergeytalyzin.weather.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_city_dialog.*
import kotlinx.android.synthetic.main.content_cities.*
import sergeytalyzin.weather.R
import sergeytalyzin.weather.adapters.CitiesAdapter
import sergeytalyzin.weather.adapters.ClickCallback
import sergeytalyzin.weather.models.City
import sergeytalyzin.weather.presenters.CitiesPresenter
import sergeytalyzin.weather.views.CitiesView

class CitiesActivity : MvpAppCompatActivity(), CitiesView {

    @InjectPresenter
    lateinit var citiesPresenter: CitiesPresenter

    @ProvidePresenter
    fun provideCitiesPresenter(): CitiesPresenter {
        return CitiesPresenter(this@CitiesActivity)
    }

    private val citiesAdapter = CitiesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        citiesAdapter.attachDelegate(object : ClickCallback {

            override fun clickItem(cityName: String, cityCode: String) {
                citiesPresenter.openDetailedActivity(cityCode = cityCode, cityName = cityName)
            }

            override fun clickDelete(city: City) {
                citiesPresenter.deleteCity(city, citiesAdapter)
            }
        })

        recycler_cities.layoutManager = LinearLayoutManager(this@CitiesActivity)
        recycler_cities.adapter = citiesAdapter

        citiesPresenter.getAndSetCities(citiesAdapter)

        fab.setOnClickListener {
            val addCityDialog = AlertDialog.Builder(this@CitiesActivity).setCancelable(false)
                .setView(layoutInflater.inflate(R.layout.add_city_dialog, null)).show()

            addCityDialog.cancelDialog.setOnClickListener {
                addCityDialog.dismiss()
            }

            addCityDialog.okDialog.setOnClickListener {
                addCityDialog.cancelDialog.visibility = View.GONE
                addCityDialog.okDialog.visibility = View.GONE
                addCityDialog.loading_dialog.visibility = View.VISIBLE
                citiesPresenter.addCity(name = addCityDialog.nameCityDialog.text.toString(),
                    code = addCityDialog.codeCityDialog.text.toString(), completed = {
                        citiesPresenter.getAndSetCities(citiesAdapter)
                        addCityDialog.dismiss()
                    }, error = {
                        addCityDialog.cancelDialog.visibility = View.VISIBLE
                        addCityDialog.okDialog.visibility = View.VISIBLE
                        addCityDialog.loading_dialog.visibility = View.GONE
                        addCityDialog.description.text = it
                    })
            }
        }
    }

    override fun startDetailedActivity(cityCode: String, cityName: String) {
        val intent = Intent(this@CitiesActivity, DetailedActivity::class.java)
            .putExtra("cityCode", cityCode)
            .putExtra("cityName", cityName)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun startLoading() {

    }

    override fun endLoading() {

    }
}
