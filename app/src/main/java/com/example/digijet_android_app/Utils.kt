package com.example.digijet_android_app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

class Utils {

    companion object {

        data class MovieData(
            val id: String,
            val title: String,
            val year: String,
            val image: String,
            val imDbRating: String
        )


        data class MovieResponse(
            val items: List<MovieData>
        )

        fun logout(context: Context, sharedPreferences: SharedPreferences) {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(context, WelcomeActivity::class.java)
            context.startActivity(intent)
            (context as AppCompatActivity).finish()
        }

        fun fetchMovies(): List<Movie> {

            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://imdb-api.com/en/API/Top250Movies/k_y9cglmlz")
                .build()

            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            // Используем библиотеку Gson для разбора JSON-ответа
            val gson = Gson()
            val movieResponse = gson.fromJson(responseData, MovieResponse::class.java)

            // Создаем список фильмов из полученных данных
            val movies = mutableListOf<Movie>()

            for (i in 0 until movieResponse.items.size step 2) {
                if (i + 1 < movieResponse.items.size) {
                    val item1 = movieResponse.items[i]
                    val item2 = movieResponse.items[i + 1]

                    val movie1 = Movie(
                        id = item1.id,
                        title = item1.title,
                        year = item1.year,
                        image = item1.image,
                        imDbRating = item1.imDbRating
                    )
                    val movie2 = Movie(
                        id = item2.id,
                        title = item2.title,
                        year = item2.year,
                        image = item2.image,
                        imDbRating = item2.imDbRating
                    )
                    movies.add(movie1)
                    movies.add(movie2)
                } else {
                    // Если остался только один фильм в списке, добавьте его отдельно
                    val item = movieResponse.items[i]
                    val movie = Movie(
                        id = item.id,
                        title = item.title,
                        year = item.year,
                        image = item.image,
                        imDbRating = item.imDbRating
                    )
                    movies.add(movie)
                }
            }
            Log.d("Movie1:", movies[0].toString())
            Log.d("Movie2:", movies[1].toString())
            Log.d("Movie3:", movies[2].toString())
            Log.d("Movie4:", movies[3].toString())

            return movies
        }

        fun setClickListenerAndImage(
            view: ImageView,
            borderDrawable: Int,
            filledDrawable: Int
        ) {
            view.setOnClickListener {
                val currentImage = view.drawable
                val newImage = if (currentImage.constantState == ContextCompat.getDrawable(
                        it.context,
                        borderDrawable
                    )?.constantState
                ) {
                    ContextCompat.getDrawable(it.context, filledDrawable)
                } else {
                    ContextCompat.getDrawable(it.context, borderDrawable)
                }
                view.setImageDrawable(newImage)
            }
        }
    }
}
