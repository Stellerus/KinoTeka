package com.example.kinoteka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kinoteka.adapter.MediaAdapter
import com.example.kinoteka.databinding.ActivityMainBinding
import com.example.kinoteka.model.MediaItem
import com.example.kinoteka.model.Movie
import com.example.kinoteka.model.Series
import com.example.kinoteka.repository.MediaRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MediaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Кинотека"

        MediaRepository.load(this)

        adapter = MediaAdapter(
            items = MediaRepository.getAll(),
            onItemClick = { item ->
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("item_id", item.id)
                startActivity(intent)
            },
            onDeleteClick = { item ->
                AlertDialog.Builder(this)
                    .setTitle("Удалить?")
                    .setMessage("Вы уверены, что хотите удалить \"${item.title}\"?")
                    .setPositiveButton("Удалить") { _, _ ->
                        MediaRepository.delete(item.id)
                        MediaRepository.save(this)
                        refreshList()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.addButton.setOnClickListener {
            showAddDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        adapter.setItems(MediaRepository.getAll())
        if (adapter.itemCount == 0) {
            binding.emptyText.visibility = android.view.View.VISIBLE
        } else {
            binding.emptyText.visibility = android.view.View.GONE
        }
    }

    private fun showAddDialog() {
        val types = arrayOf("Фильм", "Сериал")
        AlertDialog.Builder(this)
            .setTitle("Добавить")
            .setItems(types) { _, which ->
                val newItem: MediaItem = when (which) {
                    0 -> Movie(
                        id = MediaRepository.generateId(),
                        titleField = "Новый фильм",
                        director = "",
                        year = 2024,
                        genre = "",
                        duration = 0,
                        rating = 0f
                    )
                    1 -> Series(
                        id = MediaRepository.generateId(),
                        titleField = "Новый сериал",
                        creators = "",
                        years = "",
                        genre = "",
                        seasons = 1,
                        status = "Смотрю"
                    )
                    else -> return@setItems
                }
                MediaRepository.add(newItem)
                MediaRepository.save(this)
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra("item_id", newItem.id)
                startActivity(intent)
            }
            .show()
    }
}
