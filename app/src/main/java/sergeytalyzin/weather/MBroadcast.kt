package sergeytalyzin.weather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MBroadcast(private val intent: Intent): BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        context.stopService(this.intent)
    }
}