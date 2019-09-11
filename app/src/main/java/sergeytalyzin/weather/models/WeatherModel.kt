package sergeytalyzin.weather.models

import com.squareup.moshi.Json

class WeatherModel {

    private var convertedKelvinToDegrees = false

    @Json(name = "coord")
    var coord: Coord? = null
    @Json(name = "weather")
    var weather: List<Weather>? = null
    @Json(name = "base")
    var base: String? = null
    @Json(name = "main")
    var main: Main? = null
    @Json(name = "wind")
    var wind: Wind? = null
    @Json(name = "rain")
    var rain: Rain? = null
    @Json(name = "clouds")
    var clouds: Clouds? = null
    @Json(name = "dt")
    var dt: Int? = null
    @Json(name = "sys")
    var sys: Sys? = null
    @Json(name = "timezone")
    var timezone: Int? = null
    @Json(name = "id")
    var id: Int? = null
    @Json(name = "name")
    var name: String? = null
    @Json(name = "cod")
    var cod: Int? = null

    private fun changeTemp(temp: Double) : Double {

        val newTemp = temp.minus(273.15).toString()
        var finishTemp = ""

        for((index, value) in newTemp.withIndex()) {

            if(value == '.') {

                if(newTemp.length > index+2) {
                    if (newTemp[index + 2] != '0') {
                        finishTemp += newTemp[index]
                        finishTemp += newTemp[index + 1]
                        finishTemp += newTemp[index + 2]
                        break
                    }
                    else if(newTemp[index + 1] != '0') {
                        finishTemp += newTemp[index]
                        finishTemp += newTemp[index + 1]
                        break
                    }
                    else break
                }
                else if(newTemp.length > index+1 && newTemp[index + 1] != '0') {
                    finishTemp += newTemp[index]
                    finishTemp += newTemp[index + 1]
                    break
                }
                else break
            }

            finishTemp += value
        }

        return finishTemp.toDouble()
    }

    fun kelvinToDegrees() {

        if(!convertedKelvinToDegrees) {
            this.main!!.temp = changeTemp(main!!.temp!!)
            convertedKelvinToDegrees = true
        }
    }

    class Coord {

        @Json(name = "lon")
        var lon: Double? = null
        @Json(name = "lat")
        var lat: Double? = null

    }

    class Wind {

        @Json(name = "speed")
        var speed: Double? = null
        @Json(name = "deg")
        var deg: Double? = null

    }

    class Weather {

        @Json(name = "id")
        var id: Int? = null
        @Json(name = "main")
        var main: String? = null
        @Json(name = "description")
        var description: String? = null
        @Json(name = "icon")
        var icon: String? = null

    }

    class Sys {

        @Json(name = "message")
        var message: Double? = null
        @Json(name = "country")
        var country: String? = null
        @Json(name = "sunrise")
        var sunrise: Int? = null
        @Json(name = "sunset")
        var sunset: Int? = null

    }

    class Rain {

        @Json(name = "3h")
        var _3h: Double? = null

    }

    class Main {

        @Json(name = "temp")
        var temp: Double? = null
        @Json(name = "pressure")
        var pressure: Double? = null
        @Json(name = "humidity")
        var humidity: Int? = null
        @Json(name = "temp_min")
        var tempMin: Double? = null
        @Json(name = "temp_max")
        var tempMax: Double? = null
        @Json(name = "sea_level")
        var seaLevel: Double? = null
        @Json(name = "grnd_level")
        var grndLevel: Double? = null

    }

    class Clouds {

        @Json(name = "all")
        var all: Int? = null

    }
}