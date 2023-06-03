package com.example.digijet_android_app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPageActivity : AppCompatActivity(), MovieAdapter.OnMovieClickListener {

    private lateinit var movieRecyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private var selectedMovieData: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        setupNavigationDrawer()
        setupViews()
        setupFilterButton()
        setupApplyButton()
        setupMovieRecyclerView()

        val savedNickname = sharedPreferences.getString("nickname", null)
        val savedEmail = sharedPreferences.getString("email", null)

        findViewById<TextView>(R.id.emailText).text = savedEmail
        findViewById<TextView>(R.id.nicknameText).text = savedNickname
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

        val favoriteButton = findViewById<LinearLayout>(R.id.favoriteButton)
        favoriteButton.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, FavoriteMoviesActivity::class.java)
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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun setupFilterButton() {
        val filterButton = findViewById<ImageButton>(R.id.filterButton)
        val filterLayout = findViewById<LinearLayout>(R.id.filterLayout)

        filterButton.setOnClickListener {
            if (filterLayout.visibility == View.VISIBLE) {
                filterLayout.visibility = View.GONE
                movieRecyclerView.visibility = View.VISIBLE
            } else {
                filterLayout.visibility = View.VISIBLE
                movieRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupApplyButton() {
        val applyButton = findViewById<Button>(R.id.applyButton)

        applyButton.setOnClickListener {
            val yearFrom = findViewById<EditText>(R.id.yearFromEditText).text.toString()
            val yearTo = findViewById<EditText>(R.id.yearToEditText).text.toString()
            val ratingFrom = findViewById<EditText>(R.id.ratingFromEditText).text.toString()
            val ratingTo = findViewById<EditText>(R.id.ratingToEditText).text.toString()

            movieAdapter.updateFilters(yearFrom, yearTo, ratingFrom, ratingTo)
            movieRecyclerView.adapter = movieAdapter
            findViewById<LinearLayout>(R.id.filterLayout).visibility = View.GONE
            movieRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupMovieRecyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            val movies = fetchMovies()

            movieAdapter = MovieAdapter(movies, applicationContext)
            movieAdapter.setOnMovieClickListener(this@MainPageActivity)
            movieRecyclerView.layoutManager = LinearLayoutManager(this@MainPageActivity)
            movieRecyclerView.adapter = movieAdapter
        }
    }

    override fun onMovieClick(movie: Movie) {
        val savedUserId = sharedPreferences.getString("userId", null)
        savedUserId?.let { userId ->
            val moviesCollection = firestore.collection("favorite-movies")

            moviesCollection
                .whereEqualTo("title", movie.title)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        val selectedMovie = SelectedMovieData(
                            userId = userId,
                            id = movie.id,
                            title = movie.title,
                            year = movie.year,
                            image = movie.image,
                            imDbRating = movie.imDbRating
                        )

                        moviesCollection
                            .add(selectedMovie)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Фильм успешно сохранен в Firestore")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Ошибка при сохранении фильма в Firestore", e)
                            }
                    } else {
                        Log.d("Firestore", "Фильм уже существует в коллекции")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Ошибка при проверке фильма в Firestore", exception)
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
