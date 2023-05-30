package com.example.digijet_android_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request




class MainPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // Setup navigation drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        val menuLogout = navigationView.findViewById<LinearLayout>(R.id.menu_logout)
        menuLogout.setOnClickListener {
            logout()
        }

        val iconButton = findViewById<ImageButton>(R.id.iconButton)
        iconButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Create ActionBarDrawerToggle and attach it to the DrawerLayout
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        movieRecyclerView = findViewById(R.id.movieRecyclerView)

        // Используем корутины для выполнения сетевого запроса
        GlobalScope.launch(Dispatchers.Main) {
            val movies = fetchMovies()

            // Создаем и устанавливаем адаптер с полученным списком фильмов
            movieAdapter = MovieAdapter(movies)
            movieRecyclerView.layoutManager = LinearLayoutManager(this@MainPageActivity)
            movieRecyclerView.adapter = movieAdapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logout()
        }
        // Close the navigation drawer after an item is selected
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle ActionBarDrawerToggle clicks
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun fetchMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://imdb-api.com/en/API/Top250Movies/k_kpn1nxdc")
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

        movies
    }

    private fun logout() {
        // Очищаем данные сессии из SharedPreferences
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Переходим на экран WelcomeActivity
        val intent = Intent(this@MainPageActivity, WelcomeActivity::class.java)
        startActivity(intent)
        finish() // Закрываем текущую активность
    }
}

data class MovieResponse(
    val items: List<MovieData>
)

data class MovieData(
    val id: String,
    val title: String,
    val year: String,
    val image: String,
    val imDbRating: String
)



