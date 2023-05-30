package com.example.digijet_android_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.digijet_android_app.Movie
import com.example.digijet_android_app.MovieAdapter
import com.example.digijet_android_app.R

class MainPageActivity : AppCompatActivity() {

    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        movieRecyclerView = findViewById(R.id.movieRecyclerView)
        movieAdapter = MovieAdapter(getMovies()) // Замените getMovies() на метод получения списка фильмов из API

        movieRecyclerView.layoutManager = LinearLayoutManager(this)
        movieRecyclerView.adapter = movieAdapter
    }

    // Метод для получения списка фильмов из API, вам нужно заменить его на свою реализацию


    private fun getMovies(): List<Movie> {
        // Возвращайте список фильмов, полученных из API
        // Пример:
        return listOf(
            Movie("1", "Фильм 1", "2021", "https://example.com/image1.jpg", "7.5"),
            Movie("2", "Фильм 2", "2022", "https://example.com/image2.jpg", "8.0"),
            Movie("3", "Фильм 3", "2023", "https://example.com/image3.jpg", "7.2")
        )
    }
}
