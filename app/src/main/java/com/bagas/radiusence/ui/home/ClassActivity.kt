package com.bagas.radiusence.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bagas.radiusence.R
import com.bagas.radiusence.databinding.ActivityClassBinding

class ClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClassBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationIcon(R.drawable.ic_back)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}