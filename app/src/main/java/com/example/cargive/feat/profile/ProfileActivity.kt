package com.example.cargive.feat.profile

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.cargive.R
import com.example.cargive.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.bookmark.setOnClickListener {
            val intent=Intent(this,BookmarkActivity::class.java)
            startActivity(intent)
        }
    }
}