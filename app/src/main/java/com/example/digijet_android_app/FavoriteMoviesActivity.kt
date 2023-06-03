package com.example.digijet_android_app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteMoviesActivity : AppCompatActivity(), MovieAdapter.OnMovieClickListener {

    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_movies)

        setupNavigationDrawer()
        setupViews()
    }

    private fun setupNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        val menuLogout = navigationView.findViewById<LinearLayout>(R.id.menu_logout)
        menuLogout.setOnClickListener { logout() }

        val iconButton = findViewById<ImageButton>(R.id.iconButton)
        iconButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        val closeButton = findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener { drawerLayout.closeDrawer(GravityCompat.START) }

        val menuHome = findViewById<LinearLayout>(R.id.menu_home)
        menuHome.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
        }

        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupViews() {
        movieRecyclerView = findViewById(R.id.movieRecyclerView)
    }


    override fun onMovieClick(movie: Movie) {
        // Handle movie click event if needed
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun fetchFavoriteMovies(): List<Movie> = withContext(Dispatchers.IO) {
        val movies = Utils.fetchMovies()

        val userId = "YOUR_USER_ID"
        val favoriteMovieIds = getFavoriteMovieIds(userId)

        val favoriteMovies = movies.filter { movie ->
            favoriteMovieIds.contains(movie.id)
        }

        favoriteMovies
    }

    private fun getFavoriteMovieIds(userId: String): List<String> {
        val favoriteMovieIds = listOf("movie_id_1", "movie_id_2", "movie_id_3")

        if (favoriteMovieIds.isEmpty()) {
            return listOf("You don't have any favorite movies")
        }

        return favoriteMovieIds
    }

    private fun logout() {
        // Handle logout if needed
    }
}
