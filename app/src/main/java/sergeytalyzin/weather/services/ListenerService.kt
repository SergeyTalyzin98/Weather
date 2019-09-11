package sergeytalyzin.weather.services

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sergeytalyzin.weather.MBroadcast
import sergeytalyzin.weather.R
import sergeytalyzin.weather.activities.DetailedActivity
import sergeytalyzin.weather.models.RetrofitHelper
import java.net.UnknownHostException

class ListenerService : Service() {

    private var codeCity = ""
    private var nameCity = ""
    private var temp: Double = 0.0
    private var moreOrLess: Char = '-'
    private val broadcastAction = "close_service_action"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val br = MBroadcast(Intent(this@ListenerService, ListenerService::class.java))
        val intentFilter = IntentFilter(broadcastAction)
        registerReceiver(br, intentFilter)
    }

    private fun createNotification(title: String, text: String) : NotificationCompat.Builder {

        var channelId = ""

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channelId = createChannel("defaultWeatherChannel", "Default Channel")

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_cloud)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel(id: String, name: String): String {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notificationChannels.forEach {
            if (it.id == id) return id
        }

        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)
        notificationManager.createNotificationChannel(channel)
        return id
    }

    private fun createAnswer(city: String, temp: String) {

        val resIntent = Intent(this@ListenerService, DetailedActivity::class.java)
            .putExtra("cityName", nameCity)
            .putExtra("cityCode", codeCity)
        val resPendingIntent = PendingIntent.getActivity(this@ListenerService,
            0, resIntent, 0)

        val nc = createNotification("Погода в $city", "$temp Градусов")
        nc.setDefaults(Notification.DEFAULT_VIBRATE)
        nc.setDefaults(Notification.DEFAULT_SOUND)
        nc.setContentIntent(resPendingIntent)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(21, nc.build())
        stopService(Intent(this@ListenerService, ListenerService::class.java))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        codeCity = intent!!.getStringExtra("cityCode")
        nameCity = intent.getStringExtra("cityName")
        temp = intent.getDoubleExtra("temp", 0.0)
        moreOrLess = intent.getCharExtra("moreOrLess", '-')

        val closeServiceIntent = Intent(broadcastAction)
        val closeService = PendingIntent.getBroadcast(this@ListenerService, 1, closeServiceIntent, 0)

        val moreOrLessWord = if(moreOrLess == '+')
            "больше"
        else
            "меньше"
        val nc = createNotification("Weather", "Сообщить, если температура в городе $nameCity станет $moreOrLessWord $temp Градусов")
        nc.addAction(R.drawable.ic_close, "Остановить", closeService)
        startForeground(17, nc.build())

        GlobalScope.launch {

            while (true) {
                delay(1000 * 60 * 10)

                try {
                    val weather = RetrofitHelper().getWeather(city = codeCity)
                    weather.kelvinToDegrees()

                    if (moreOrLess == '+') {
                        if (weather.main!!.temp!! > temp) {
                            createAnswer(nameCity, weather.main!!.temp!!.toString())
                            break
                        }
                    } else if (moreOrLess == '-') {
                        if (weather.main!!.temp!! < temp) {
                            createAnswer(nameCity, weather.main!!.temp!!.toString())
                            break
                        }
                    }
                }catch (e: UnknownHostException) {}
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}

