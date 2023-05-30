package com.example.digijet_android_app

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainPageActivity : AppCompatActivity() {

    companion object {
        private const val API_URL = "https://imdb-api.com/en/API/Top250Movies/k_kpn1nxdc" // Замените на вашу ссылку API
        private const val TAG = "Top250Films"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        val backButton: Button = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            // Переадресация на экран WelcomeActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        FetchMoviesTask().execute()
    }

    private inner class FetchMoviesTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            var connection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            var result: String

            try {
                val url = URL(API_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                val stream = connection.inputStream
                reader = BufferedReader(InputStreamReader(stream))
                val builder = StringBuilder()

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    builder.append(line)
                }

                result = builder.toString()
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching movies: ${e.message}")
                result = ""
            } finally {
                reader?.close()
                connection?.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            if (result.isNotEmpty()) {
                try {
                    val jsonObject = JSONObject(result)
                    val moviesArray = jsonObject.getJSONArray("items")

                    for (i in 0 until moviesArray.length()) {
                        val movieObject = moviesArray.getJSONObject(i)
                        val title = movieObject.getString("title")
                        val year = movieObject.getString("year")
                        val rating = movieObject.getString("imDbRating")
                        val logoUrl = movieObject.getString("image")

                        // Выводим информацию в консоль
                        Log.d(TAG, "Title: $title")
                        Log.d(TAG, "Year: $year")
                        Log.d(TAG, "Rating: $rating")
                        Log.d(TAG, "Logo URL: $logoUrl")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing JSON: ${e.message}")
                }
            }
        }

    }
}
