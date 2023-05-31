package com.example.digijet_android_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
    val userId : String,
    val id: String,
    val title: String,
    val year: String,
    val image: String,
    val imDbRating: String
)


class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
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
        holder.ratingTextView1.text = movie1.imDbRating

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
    }



    override fun getItemCount(): Int {
        return movies.size / 2
    }
}