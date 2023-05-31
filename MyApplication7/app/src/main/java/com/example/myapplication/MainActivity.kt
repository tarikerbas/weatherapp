import android.os.AsyncTask
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import com.example.myapplication.R

class MainActivity : AppCompatActivity() {
    private val API_KEY = "a5755d0eb3f3d3c6dcb8624b13425b98"
    private val API_URL = "https://api.openweathermap.org/data/2.5/weather"

    private lateinit var addressTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var temperatureTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addressTextView = findViewById(R.id.address)
        statusTextView = findViewById(R.id.status)
        temperatureTextView = findViewById(R.id.temp)

        FetchWeatherTask().execute()
    }

    private inner class FetchWeatherTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void): String? {
            var connection: HttpURLConnection? = null
            try {
                val apiKey = "YOUR_API_KEY"
                val location = "Istanbul, TR"

                val url = URL("$API_URL?q=$location&appid=$apiKey")
                connection = url.openConnection() as HttpURLConnection
                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()
                    return response.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }

            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result != null) {
                val weatherData = parseWeatherData(result)
                updateWeatherUI(weatherData)
            }
        }
    }

    private fun parseWeatherData(data: String): WeatherData {
        val jsonObject = JSONObject(data)
        val address = jsonObject.getString("name")
        val status = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
        val temperature = jsonObject.getJSONObject("main").getString("temp")

        return WeatherData(address, status, temperature)
    }

    private fun updateWeatherUI(weatherData: WeatherData) {
        addressTextView.text = weatherData.address
        statusTextView.text = weatherData.status
        temperatureTextView.text = weatherData.temperature
    }

    data class WeatherData(val address: String, val status: String, val temperature: String)
}