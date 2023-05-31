package com.example.digijet_android_app

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


class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

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

        holder.addFavorites.setOnClickListener {
            val currentImage = holder.addFavorites.drawable
            val newImage = if (currentImage.constantState == ContextCompat.getDrawable(it.context, R.drawable.baseline_favorite_border_24)?.constantState) {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_favorite_24)
            } else {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_favorite_border_24)
            }
            holder.addFavorites.setImageDrawable(newImage)
        }

        holder.unwatched.setOnClickListener {
            val currentImage = holder.unwatched.drawable
            val newImage = if (currentImage.constantState == ContextCompat.getDrawable(it.context, R.drawable.baseline_remove_red_eye_24)?.constantState) {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_remove_eye_24)
            } else {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_remove_red_eye_24)
            }
            holder.unwatched.setImageDrawable(newImage)
        }

        holder.unstar.setOnClickListener {
            val currentImage = holder.unstar.drawable
            val newImage = if (currentImage.constantState == ContextCompat.getDrawable(it.context, R.drawable.baseline_star_border_24)?.constantState) {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_star_24)
            } else {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_star_border_24)
            }
            holder.unstar.setImageDrawable(newImage)
        }

        holder.addFavorites2.setOnClickListener {
            val currentImage = holder.addFavorites2.drawable
            val newImage = if (currentImage.constantState == ContextCompat.getDrawable(it.context, R.drawable.baseline_favorite_border_24)?.constantState) {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_favorite_24)
            } else {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_favorite_border_24)
            }
            holder.addFavorites2.setImageDrawable(newImage)
        }

        holder.unwatched2.setOnClickListener {
            val currentImage = holder.unwatched2.drawable
            val newImage = if (currentImage.constantState == ContextCompat.getDrawable(it.context, R.drawable.baseline_remove_red_eye_24)?.constantState) {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_remove_eye_24)
            } else {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_remove_red_eye_24)
            }
            holder.unwatched2.setImageDrawable(newImage)
        }

        holder.unstar2.setOnClickListener {
            val currentImage = holder.unstar2.drawable
            val newImage = if (currentImage.constantState == ContextCompat.getDrawable(it.context, R.drawable.baseline_star_border_24)?.constantState) {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_star_24)
            } else {
                ContextCompat.getDrawable(it.context, R.drawable.baseline_star_border_24)
            }
            holder.unstar2.setImageDrawable(newImage)
        }
    }

    override fun getItemCount(): Int {
        return movies.size / 2
    }
}