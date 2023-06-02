package com.example.digijet_android_app

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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
        val movie1 = movies[position * 2]
        val movie2 = movies[position * 2 + 1]

        // Загрузка и отображение изображения с использованием Picasso
        Picasso.get().load(movie1.image).into(holder.movieImageView1)
        Picasso.get().load(movie2.image).into(holder.movieImageView2)

        // Установка названия, года и рейтинга фильма
        holder.titleTextView1.text = movie1.title
        holder.yearTextView1.text = movie1.year

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

        val rating1 = movie1.imDbRating.toFloat()
        setRatingBackgroundColor(holder.ratingTextView1, rating1)
        holder.ratingTextView1.text = movie1.imDbRating

        val rating2 = movie2.imDbRating.toFloat()
        setRatingBackgroundColor(holder.ratingTextView2, rating2)
        holder.ratingTextView2.text = movie2.imDbRating

        holder.titleTextView2.text = movie2.title
        holder.yearTextView2.text = movie2.year
        holder.ratingTextView2.text = movie2.imDbRating

        // Обработчик клика на первый фильм
        holder.itemView.findViewById<View>(R.id.movieImageView1).setOnClickListener {
            movieClickListener?.onMovieClick(movie1)
        }

        // Обработчик клика на второй фильм
        holder.itemView.findViewById<View>(R.id.movieImageView2).setOnClickListener {
            movieClickListener?.onMovieClick(movie2)
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

        setClickListenerAndImage(holder.addFavorites, R.drawable.baseline_favorite_border_24, R.drawable.baseline_favorite_24)
        setClickListenerAndImage(holder.unwatched, R.drawable.baseline_remove_red_eye_24, R.drawable.baseline_remove_eye_24)
        setClickListenerAndImage(holder.unstar, R.drawable.baseline_done_outline_24, R.drawable.baseline_done_24)

        setClickListenerAndImage(holder.addFavorites2, R.drawable.baseline_favorite_border_24, R.drawable.baseline_favorite_24)
        setClickListenerAndImage(holder.unwatched2, R.drawable.baseline_remove_red_eye_24, R.drawable.baseline_remove_eye_24)
        setClickListenerAndImage(holder.unstar2, R.drawable.baseline_done_outline_24, R.drawable.baseline_done_24)
    }

    override fun getItemCount(): Int {
        return movies.size / 2
    }
}