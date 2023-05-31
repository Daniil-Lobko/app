package com.example.digijet_android_app

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
        val movies = Utils.fetchMovies()
        Log.d("Movie1:", movies[0].toString())
        movies
    }

    private fun logout() {
        Utils.logout(this, sharedPreferences)
    }
}
