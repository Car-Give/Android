package com.example.cargive.feat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cargive.R
import com.example.cargive.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBookmarkBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}