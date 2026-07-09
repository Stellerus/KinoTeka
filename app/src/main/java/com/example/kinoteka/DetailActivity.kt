package com.example.kinoteka

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.kinoteka.databinding.ActivityDetailBinding
import com.example.kinoteka.model.MediaItem
import com.example.kinoteka.model.Movie
import com.example.kinoteka.model.Series
import com.example.kinoteka.repository.MediaRepository

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var currentItem: MediaItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = systemBars.top)
            binding.scrollView.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Детали"

        binding.toolbar.setNavigationOnClickListener {
            saveAndFinish()
        }

        val itemId = intent.getStringExtra("item_id") ?: return
        currentItem = MediaRepository.getById(itemId)
        if (currentItem == null) {
            Toast.makeText(this, "Объект не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupSpinner()
        displayItem(currentItem!!)
    }

    private fun setupSpinner() {
        val statusOptions = arrayOf("Смотрю", "Просмотрено", "Брошено")
        binding.statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusOptions)
    }

    private fun displayItem(item: MediaItem) {
        when (item) {
            is Movie -> displayMovie(item)
            is Series -> displaySeries(item)
        }
    }

    private fun displayMovie(movie: Movie) {
        binding.typeLabel.text = "Фильм"
        binding.movieFields.visibility = View.VISIBLE
        binding.seriesFields.visibility = View.GONE

        binding.editTitle.setText(movie.title)
        binding.editDirector.setText(movie.director)
        binding.editYear.setText(if (movie.year > 0) movie.year.toString() else "")
        binding.editGenre.setText(movie.genre)
        binding.editDuration.setText(if (movie.duration > 0) movie.duration.toString() else "")
        binding.editRating.setText(if (movie.rating > 0f) movie.rating.toString() else "")
    }

    private fun displaySeries(series: Series) {
        binding.typeLabel.text = "Сериал"
        binding.movieFields.visibility = View.GONE
        binding.seriesFields.visibility = View.VISIBLE

        binding.editTitle.setText(series.title)
        binding.editCreators.setText(series.creators)
        binding.editYears.setText(series.years)
        binding.editGenreSeries.setText(series.genre)
        binding.editSeasons.setText(if (series.seasons > 0) series.seasons.toString() else "")

        val statusOptions = arrayOf("Смотрю", "Просмотрено", "Брошено")
        val position = statusOptions.indexOf(series.status)
        if (position >= 0) {
            binding.statusSpinner.setSelection(position)
        }
    }

    private fun saveAndFinish() {
        val item = currentItem ?: return

        val updatedItem: MediaItem = when (item) {
            is Movie -> item.copy(
                titleField = binding.editTitle.text.toString(),
                director = binding.editDirector.text.toString(),
                year = binding.editYear.text.toString().toIntOrNull() ?: 0,
                genre = binding.editGenre.text.toString(),
                duration = binding.editDuration.text.toString().toIntOrNull() ?: 0,
                rating = binding.editRating.text.toString().toFloatOrNull() ?: 0f
            )
            is Series -> item.copy(
                titleField = binding.editTitle.text.toString(),
                creators = binding.editCreators.text.toString(),
                years = binding.editYears.text.toString(),
                genre = binding.editGenreSeries.text.toString(),
                seasons = binding.editSeasons.text.toString().toIntOrNull() ?: 1,
                status = binding.statusSpinner.selectedItem?.toString() ?: "Смотрю"
            )
            else -> return
        }

        MediaRepository.update(updatedItem)
        MediaRepository.save(this)
        finish()
    }

    override fun onBackPressed() {
        saveAndFinish()
        super.onBackPressed()
    }
}
