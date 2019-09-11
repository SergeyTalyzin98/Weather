package sergeytalyzin.weather.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class City {
    @PrimaryKey(autoGenerate = true) var id: Int? = null
    var name: String? = null
    var code: String? = null
}