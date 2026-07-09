package com.example.kinoteka

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kinoteka.adapter.MediaAdapter
import com.example.kinoteka.databinding.ActivityMainBinding
import com.example.kinoteka.model.MediaItem
import com.example.kinoteka.model.Movie
import com.example.kinoteka.model.Series
import com.example.kinoteka.repository.MediaRepository

/**
 * Main screen — displays the list of all media items.
 * Supports adding items (with type selection) and deleting items.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.toolbar_main)

        // Load persisted data
        MediaRepository.load(this)

        adapter = MediaAdapter(
            items = MediaRepository.getAll(),
            onItemClick = { item ->
                // Navigate to detail screen
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("item_id", item.id)
                startActivity(intent)
            },
            onDeleteClick = { item ->
                // Show confirmation dialog before deletion
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_dialog_title))
                    .setMessage(getString(R.string.delete_dialog_message, item.title))
                    .setPositiveButton(getString(R.string.delete_positive)) { _, _ ->
                        MediaRepository.delete(item.id)
                        MediaRepository.save(this)
                        refreshList()
                    }
                    .setNegativeButton(getString(R.string.delete_negative), null)
                    .show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // FAB opens type-selection dialog
        binding.addButton.setOnClickListener {
            showAddDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    /** Refreshes the RecyclerView and toggles the empty-state placeholder. */
    private fun refreshList() {
        adapter.setItems(MediaRepository.getAll())
        binding.emptyText.visibility =
            if (adapter.itemCount == 0) android.view.View.VISIBLE
            else android.view.View.GONE
    }

    /** Asks the user to choose between Movie and Series, then creates a new item. */
    private fun showAddDialog() {
        val types = arrayOf(getString(R.string.add_movie), getString(R.string.add_series))
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.add_dialog_title))
            .setItems(types) { _, which ->
                val newItem: MediaItem = when (which) {
                    0 -> Movie(
                        id = MediaRepository.generateId(),
                        titleField = getString(R.string.default_movie_title),
                        director = "",
                        year = 2024,
                        genre = "",
                        duration = 0,
                        rating = 0f
                    )
                    1 -> Series(
                        id = MediaRepository.generateId(),
                        titleField = getString(R.string.default_series_title),
                        creators = "",
                        years = "",
                        genre = "",
                        seasons = 1,
                        status = getString(R.string.status_watching)
                    )
                    else -> return@setItems
                }
                MediaRepository.add(newItem)
                MediaRepository.save(this)
                // Open the new item in detail screen for editing
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("item_id", newItem.id)
                startActivity(intent)
            }
            .show()
    }
}
