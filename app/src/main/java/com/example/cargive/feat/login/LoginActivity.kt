package com.example.cargive.feat.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cargive.R
import com.example.cargive.databinding.ActivityLoginBinding
import com.example.cargive.feat.profile.BookmarkActivity
import com.example.cargive.feat.profile.ProfileActivity

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.test.setOnClickListener{
            var intent=Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }


    }
}