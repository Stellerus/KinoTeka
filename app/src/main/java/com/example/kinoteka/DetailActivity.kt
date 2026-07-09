package com.example.kinoteka

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.kinoteka.databinding.ActivityDetailBinding
import com.example.kinoteka.model.MediaItem
import com.example.kinoteka.model.Movie
import com.example.kinoteka.model.Series
import com.example.kinoteka.repository.MediaRepository

/**
 * Detail screen — displays and allows editing of a single media item.
 * Implements edge‑to‑edge design: content draws behind system bars.
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var currentItem: MediaItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable edge-to-edge display
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets so toolbar and scrollable content
        // are not obscured by the status bar and navigation bar.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.updatePadding(top = systemBars.top)
            binding.scrollView.updatePadding(bottom = systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.toolbar_detail)

        // Back arrow → save changes and return
        binding.toolbar.setNavigationOnClickListener {
            saveAndFinish()
        }

        // Load the item by ID passed from the previous screen
        val itemId = intent.getStringExtra("item_id") ?: return
        currentItem = MediaRepository.getById(itemId)
        if (currentItem == null) {
            Toast.makeText(this, getString(R.string.item_not_found), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupSpinner()
        displayItem(currentItem!!)
    }

    /** Prepares the status dropdown for Series items. */
    private fun setupSpinner() {
        val statusOptions = arrayOf(
            getString(R.string.status_watching),
            getString(R.string.status_completed),
            getString(R.string.status_dropped)
        )
        binding.statusSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            statusOptions
        )
    }

    /** Displays type-specific fields based on whether the item is a Movie or Series. */
    private fun displayItem(item: MediaItem) {
        when (item) {
            is Movie -> displayMovie(item)
            is Series -> displaySeries(item)
        }
    }

    private fun displayMovie(movie: Movie) {
        binding.typeLabel.text = getString(R.string.label_type_movie)
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
        binding.typeLabel.text = getString(R.string.label_type_series)
        binding.movieFields.visibility = View.GONE
        binding.seriesFields.visibility = View.VISIBLE

        binding.editTitle.setText(series.title)
        binding.editCreators.setText(series.creators)
        binding.editYears.setText(series.years)
        binding.editGenreSeries.setText(series.genre)
        binding.editSeasons.setText(if (series.seasons > 0) series.seasons.toString() else "")

        val statusOptions = arrayOf(
            getString(R.string.status_watching),
            getString(R.string.status_completed),
            getString(R.string.status_dropped)
        )
        val position = statusOptions.indexOf(series.status)
        if (position >= 0) {
            binding.statusSpinner.setSelection(position)
        }
    }

    /** Reads field values, updates the item in the repository, and persists changes. */
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
                status = binding.statusSpinner.selectedItem?.toString()
                    ?: getString(R.string.status_watching)
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
