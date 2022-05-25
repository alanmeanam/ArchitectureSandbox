package com.example.architecturesandbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.architecturesandbox.databinding.ActivityMainBinding
import com.example.architecturesandbox.movie.presentation.ui.activity.MovieActivity
import com.example.architecturesandbox.utils.beginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.moviesBtn.setOnClickListener {
            beginActivity<MovieActivity>()
        }
    }

}