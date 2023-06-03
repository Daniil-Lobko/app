package com.example.digijet_android_app

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.digijet_android_app.Utils.Companion.setClickListenerAndImage
import com.squareup.picasso.Picasso

data class Movie(
    val id: String,
    val title: String,
    val year: String,
    val image: String,
    val imDbRating: String
)

data class SelectedMovieData(
    val userId: String,
    val id: String,
    val title: String,
    val year: String,
    val image: String,
    val imDbRating: String
)

class MovieAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var yearFromFilter: String? = null
    private var yearToFilter: String? = null
    private var ratingFromFilter: String? = null
    private var ratingToFilter: String? = null
    private var filteredMovies: List<Movie> = movies

    fun updateFilters(yearFrom: String?, yearTo: String?, ratingFrom: String?, ratingTo: String?) {
        yearFromFilter = yearFrom
        yearToFilter = yearTo
        ratingFromFilter = ratingFrom
        ratingToFilter = ratingTo

        filteredMovies = applyFilters(movies) // Применяем фильтры к исходному списку фильмов
        notifyDataSetChanged()
    }

    // Интерфейс для обработки кликов на фильмы
    interface OnMovieClickListener {
        fun onMovieClick(movie: Movie)
    }

    private var movieClickListener: OnMovieClickListener? = null

    // Метод для установки слушателя кликов на фильмы
    fun setOnMovieClickListener(listener: MainPageActivity) {
        movieClickListener = listener
    }


    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val movieImageView1: ImageView = itemView.findViewById(R.id.movieImageView1)
        val titleTextView1: TextView = itemView.findViewById(R.id.titleTextView1)
        val yearTextView1: TextView = itemView.findViewById(R.id.yearTextView1)
        val ratingTextView1: TextView = itemView.findViewById(R.id.ratingTextView1)

        val movieImageView2: ImageView = itemView.findViewById(R.id.movieImageView2)
        val titleTextView2: TextView = itemView.findViewById(R.id.titleTextView2)
        val yearTextView2: TextView = itemView.findViewById(R.id.yearTextView2)
        val ratingTextView2: TextView = itemView.findViewById(R.id.ratingTextView2)
        val film2: LinearLayout = itemView.findViewById(R.id.film2)

        val addFavorites: ImageButton = itemView.findViewById(R.id.addFavorites)
        val unwatched: ImageButton = itemView.findViewById(R.id.unwatched)
        val unstar: ImageButton = itemView.findViewById(R.id.unstar)
        val addFavorites2: ImageButton = itemView.findViewById(R.id.addFavorites2)
        val unwatched2: ImageButton = itemView.findViewById(R.id.unwatched2)
        val unstar2: ImageButton = itemView.findViewById(R.id.unstar2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie1 = filteredMovies.getOrNull(position * 2)
        val movie2 = filteredMovies.getOrNull(position * 2 + 1)

        // Применение фильтров
        val filteredMovies = applyFilters(listOfNotNull(movie1, movie2)).toList()

        if (filteredMovies.isNotEmpty()) {
            val filteredMovie1 = filteredMovies[0]
            val filteredMovie2 = filteredMovies.getOrNull(1)

            // Загрузка и отображение изображения с использованием Picasso
            Picasso.get().load(filteredMovie1.image).into(holder.movieImageView1)
            holder.titleTextView1.text = filteredMovie1.title
            holder.yearTextView1.text = filteredMovie1.year
            holder.ratingTextView1.text = filteredMovie1.imDbRating

            if (filteredMovie2 != null) {
                Picasso.get().load(filteredMovie2.image).into(holder.movieImageView2)
                holder.titleTextView2.text = filteredMovie2.title
                holder.yearTextView2.text = filteredMovie2.year
                holder.ratingTextView2.text = filteredMovie2.imDbRating
            } else {
                // Если второго фильма нет, скройте его элементы
                holder.movieImageView2.visibility = View.GONE
                holder.titleTextView2.visibility = View.GONE
                holder.yearTextView2.visibility = View.GONE
                holder.ratingTextView2.visibility = View.GONE
                holder.film2.visibility = View.GONE
            }
        }

        movie1?.let {
            // Загрузка и отображение изображения с использованием Picasso
            Picasso.get().load(it.image).into(holder.movieImageView1)

            // Установка названия, года и рейтинга фильма
            holder.titleTextView1.text = it.title
            holder.yearTextView1.text = it.year

            val rating1 = it.imDbRating.toFloat()
            setRatingBackgroundColor(holder.ratingTextView1, rating1)
            holder.ratingTextView1.text = it.imDbRating

            // Обработчик клика на первый фильм
            holder.itemView.findViewById<View>(R.id.movieImageView1).setOnClickListener {
                movieClickListener?.onMovieClick(movie1)
            }

            setClickListenerAndImage(
                holder.addFavorites,
                R.drawable.baseline_favorite_border_24,
                R.drawable.baseline_favorite_24
            )
            setClickListenerAndImage(
                holder.unwatched,
                R.drawable.baseline_remove_red_eye_24,
                R.drawable.baseline_remove_eye_24
            )
            setClickListenerAndImage(
                holder.unstar,
                R.drawable.baseline_done_outline_24,
                R.drawable.baseline_done_24
            )
        }

        movie2?.let {
            // Загрузка и отображение изображения с использованием Picasso
            Picasso.get().load(it.image).into(holder.movieImageView2)

            // Установка названия, года и рейтинга фильма
            holder.titleTextView2.text = it.title
            holder.yearTextView2.text = it.year
            holder.ratingTextView2.text = it.imDbRating

            val rating2 = it.imDbRating.toFloat()
            setRatingBackgroundColor(holder.ratingTextView2, rating2)
            holder.ratingTextView2.text = it.imDbRating

            // Обработчик клика на второй фильм
            holder.itemView.findViewById<View>(R.id.movieImageView2).setOnClickListener {
                movieClickListener?.onMovieClick(movie2)
            }

            setClickListenerAndImage(
                holder.addFavorites2,
                R.drawable.baseline_favorite_border_24,
                R.drawable.baseline_favorite_24
            )
            setClickListenerAndImage(
                holder.unwatched2,
                R.drawable.baseline_remove_red_eye_24,
                R.drawable.baseline_remove_eye_24
            )
            setClickListenerAndImage(
                holder.unstar2,
                R.drawable.baseline_done_outline_24,
                R.drawable.baseline_done_24
            )
        }
    }

    @SuppressLint("ResourceAsColor")
    fun setRatingBackgroundColor(textView: TextView, rating: Float) {
        when {
            rating < 5 -> {
                textView.setBackgroundColor(Color.parseColor("#80CD2626"))
            }

            rating >= 5 && rating < 8 -> {
                textView.setBackgroundColor(Color.parseColor("#FFB6A615"))
            }

            rating >= 8 && rating <= 10 -> {
                textView.setBackgroundColor(Color.parseColor("#367838"))
            }
        }
    }

    override fun getItemCount(): Int {
        return (filteredMovies.size + 1) / 2
    }

    private fun applyFilters(movies: List<Movie>): List<Movie> {
        // Prerequisites: Convert filter inputs to integers if they are not empty
        val yearFrom = yearFromFilter?.toIntOrNull()
        val yearTo = yearToFilter?.toIntOrNull()
        val ratingFrom = ratingFromFilter?.toFloatOrNull()
        val ratingTo = ratingToFilter?.toFloatOrNull()

        // Apply filters if they are set
        var filteredMovies = movies

        if (yearFrom != null && yearTo != null && ratingFrom != null && ratingTo != null) {
            filteredMovies = filteredMovies.filter {
                it.year.toInt() >= yearFrom &&
                        it.year.toInt() <= yearTo &&
                        it.imDbRating.toFloat() >= ratingFrom &&
                        it.imDbRating.toFloat() <= ratingTo
            }
        } else {
            if (yearFrom != null) {
                filteredMovies = filteredMovies.filter { it.year.toInt() >= yearFrom }
            }

            if (yearTo != null) {
                filteredMovies = filteredMovies.filter { it.year.toInt() <= yearTo }
            }

            if (ratingFrom != null) {
                filteredMovies = filteredMovies.filter { it.imDbRating.toFloat() >= ratingFrom }
            }

            if (ratingTo != null) {
                filteredMovies = filteredMovies.filter { it.imDbRating.toFloat() <= ratingTo }
            }
        }

        if (yearFrom == null && yearTo == null && ratingFrom == null && ratingTo == null) {
            return movies
        }

        return filteredMovies
    }

}
