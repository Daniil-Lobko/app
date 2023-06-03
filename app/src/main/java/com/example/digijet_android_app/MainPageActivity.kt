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
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private var selectedMovieData: Movie? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        // Setup navigation drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        movieRecyclerView = findViewById(R.id.movieRecyclerView)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val savedUserId = sharedPreferences.getString("userId", null)
        val savedNickname = sharedPreferences.getString("nickname", null)
        val savedEmail = sharedPreferences.getString("email", null)

        val filterButton = findViewById<ImageButton>(R.id.filterButton)
        val filterLayout = findViewById<LinearLayout>(R.id.filterLayout)

        val menuHome = navigationView.findViewById<LinearLayout>(R.id.menu_home)
        menuHome.setOnClickListener {
            if (savedUserId != null) {
                Log.d("userId:", savedUserId)
            };
        }

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

        val menu_home = findViewById<LinearLayout>(R.id.menu_home)
        menu_home.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, MainPageActivity::class.java)
            startActivity(intent)
            finish()
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

        // Получите ссылки на элементы интерфейса в активности
        val applyButton = findViewById<Button>(R.id.applyButton)
        val yearFromEditText = findViewById<EditText>(R.id.yearFromEditText)
        val yearToEditText = findViewById<EditText>(R.id.yearToEditText)
        val ratingFromEditText = findViewById<EditText>(R.id.ratingFromEditText)
        val ratingToEditText = findViewById<EditText>(R.id.ratingToEditText)

        // Установите обработчик клика на кнопку фильтра
        filterButton.setOnClickListener {
            if (filterLayout.visibility == View.VISIBLE) {
                filterLayout.visibility = View.GONE
                movieRecyclerView.setVisibility(View.VISIBLE);
            } else {
                filterLayout.visibility = View.VISIBLE
                movieRecyclerView.setVisibility(View.GONE);
            }
        }

        // Установите обработчик клика на кнопку применения фильтра
        applyButton.setOnClickListener {
            val yearFrom = yearFromEditText.text.toString()
            val yearTo = yearToEditText.text.toString()
            val ratingFrom = ratingFromEditText.text.toString()
            val ratingTo = ratingToEditText.text.toString()

            movieAdapter.updateFilters(yearFrom, yearTo, ratingFrom, ratingTo)
            filterLayout.visibility = View.GONE
            movieRecyclerView.setVisibility(View.VISIBLE);
            filterLayout.isFocusable = false
            filterLayout.isFocusableInTouchMode = false

        }

        // Используем корутины для выполнения сетевого запроса
        GlobalScope.launch(Dispatchers.Main) {
            val movies = fetchMovies()

            // Создаем и устанавливаем адаптер с полученным списком фильмов
            movieAdapter = MovieAdapter(movies)
            movieAdapter.setOnMovieClickListener(this@MainPageActivity)
            movieRecyclerView.layoutManager = LinearLayoutManager(this@MainPageActivity)
            movieRecyclerView.adapter = movieAdapter
        }

        val emailEditText: TextView = findViewById(R.id.emailText)
        val nicknameEditText: TextView = findViewById(R.id.nicknameText)

        emailEditText.text = savedEmail
        nicknameEditText.text = savedNickname

    }

    override fun onMovieClick(movie: Movie) {
        // Обработка клика на фильм
        // Ваш код для вывода данных фильма в консоль или выполнения других действий

        val savedUserId = sharedPreferences.getString("userId", null)
        if (savedUserId != null) {
            Log.d("savedUserId", savedUserId)
        }
        val selectedMovie = savedUserId?.let {
            SelectedMovieData(
                userId = it,
                id = movie.id,
                title = movie.title,
                year = movie.year,
                image = movie.image,
                imDbRating = movie.imDbRating
            )
        }

        val movieDocument = firestore.collection("favorite-movies").document()

        // Устанавливаем значения полей фильма в документ
        if (selectedMovie != null) {
            movieDocument.set(selectedMovie)
                .addOnSuccessListener {
                    Log.d("Firestore", "Фильм успешно сохранен в Firestore")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Ошибка при сохранении фильма в Firestore", e)
                }
        }
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
