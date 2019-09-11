package sergeytalyzin.weather.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sergeytalyzin.weather.R
import sergeytalyzin.weather.models.City

interface ClickCallback {
    fun clickItem(cityName: String, cityCode: String)
    fun clickDelete(city: City)
}

class CitiesAdapter: RecyclerView.Adapter<CitiesAdapter.ViewHolder>() {

    private val cities = mutableListOf<City>()
    private lateinit var delegate: ClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_city, parent, false)

        return ViewHolder(delegate, view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cities[position])
    }

    override fun getItemCount() = cities.size

    fun setList(list: List<City>) {
        cities.clear()
        cities.addAll(list)
        notifyDataSetChanged()
    }

    fun attachDelegate(delegate: ClickCallback) {
        this.delegate = delegate
    }

    class ViewHolder(private val delegate: ClickCallback, itemView: View): RecyclerView.ViewHolder(itemView) {

        private val cityTextView = itemView.findViewById<TextView>(R.id.city_item_city)
        private val deleteItemCity = itemView.findViewById<ImageView>(R.id.delete_item_city)

        fun bind(city: City) {

            cityTextView.text = city.name

            itemView.setOnClickListener {
                delegate.clickItem(cityCode = city.code!!, cityName = city.name!!)
            }

            deleteItemCity.setOnClickListener {
                delegate.clickDelete(city = city)
            }
        }
    }
}