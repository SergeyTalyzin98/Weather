package sergeytalyzin.weather.activities

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_detailed.*
import kotlinx.android.synthetic.main.add_listener_dialog.*
import sergeytalyzin.weather.R
import sergeytalyzin.weather.models.WeatherModel
import sergeytalyzin.weather.presenters.DetailedPresenter
import sergeytalyzin.weather.services.ListenerService
import sergeytalyzin.weather.views.DetailedView

class DetailedActivity : MvpAppCompatActivity(), DetailedView {

    @InjectPresenter
    lateinit var detailedPresenter: DetailedPresenter
    private lateinit var cityName: String
    private lateinit var cityCode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        setSupportActionBar(toolbar_detailed)

        cityCode = intent.getStringExtra("cityCode")
        cityName = intent.getStringExtra("cityName")
        title = cityName
        detailedPresenter.startGetWeather(cityCode = cityCode)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detailed_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.add_listener -> {

                if(!detailedPresenter.checkService(context = this@DetailedActivity, service = ListenerService())) {

                    val addCityDialog = AlertDialog.Builder(this@DetailedActivity).setCancelable(false)
                        .setView(layoutInflater.inflate(R.layout.add_listener_dialog, null)).show()

                    addCityDialog.cancel_listener_dialog.setOnClickListener {
                        addCityDialog.dismiss()
                    }

                    addCityDialog.ok_listener_dialog.setOnClickListener {
                        addCityDialog.cancel_listener_dialog.visibility = View.GONE
                        addCityDialog.ok_listener_dialog.visibility = View.GONE
                        addCityDialog.loading_listener_dialog.visibility = View.VISIBLE
                        detailedPresenter.addListener(cityCode = cityCode,
                            cityName = cityName,
                            context = applicationContext,
                            more = addCityDialog.more.isChecked,
                            less = addCityDialog.less.isChecked,
                            temp = addCityDialog.degrees_listener_dialog.text.toString(),
                            ok = {
                                startService(it)
                                addCityDialog.dismiss()
                            },
                            error = {
                                addCityDialog.cancel_listener_dialog.visibility = View.VISIBLE
                                addCityDialog.ok_listener_dialog.visibility = View.VISIBLE
                                addCityDialog.loading_listener_dialog.visibility = View.GONE
                                addCityDialog.description_listener_dialog.text = it
                            })
                    }

                    addCityDialog.more.setOnClickListener {
                        addCityDialog.less.isChecked = false
                    }

                    addCityDialog.less.setOnClickListener {
                        addCityDialog.more.isChecked = false
                    }
                }
                else {
                    Toast.makeText(this@DetailedActivity,
                        "Слушатель погоды уже создан!", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showWeather(data: WeatherModel) {

        val mTemp = "${data.main!!.temp!!} Градусов"
        temp.text = mTemp

        val anim = ObjectAnimator.ofFloat(dataAboutWeather, "Alpha", 0F, 1F)
        anim.duration = 1000
        anim.start()
    }

    override fun showErrorConnection() {
        loading_detailed.visibility = View.GONE
        dataAboutWeather.visibility = View.GONE
        errors_detailed.visibility = View.VISIBLE
        message_error.text = resources.getText(R.string.error_connection)

        val anim = ObjectAnimator.ofFloat(errors_detailed, "Alpha", 0F, 1F)
        anim.duration = 1000
        anim.start()

        btn_error.setOnClickListener {
            errors_detailed.visibility = View.GONE
            detailedPresenter.startGetWeather(cityCode = cityCode)
        }
    }

    override fun startLoading() {
        loading_detailed.visibility = View.VISIBLE
        dataAboutWeather.visibility = View.GONE
    }

    override fun endLoading() {
        loading_detailed.visibility = View.GONE
        dataAboutWeather.visibility = View.VISIBLE
    }
}
