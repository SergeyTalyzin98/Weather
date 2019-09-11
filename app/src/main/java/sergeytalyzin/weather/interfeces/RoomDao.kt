package sergeytalyzin.weather.interfeces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import sergeytalyzin.weather.models.City

@Dao
interface RoomDao {

    @Insert
    fun insert(city: City)

    @Delete
    fun delete(city: City)

    @Query("SELECT * FROM city WHERE name LIKE :name")
    fun getCity(name: String) : City?

    @Query("SELECT * FROM city")
    fun getAllCity() : List<City>
}