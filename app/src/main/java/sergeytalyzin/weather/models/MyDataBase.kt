package sergeytalyzin.weather.models

import androidx.room.Database
import androidx.room.RoomDatabase
import sergeytalyzin.weather.interfeces.RoomDao

@Database(entities = [City::class], version = 1)
abstract class MyDataBase : RoomDatabase() {
    abstract val roomDao: RoomDao
}
