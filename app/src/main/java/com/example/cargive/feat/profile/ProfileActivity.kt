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
        binding.profileCar.setOnClickListener {
            val intent=Intent(this,ProfilecarActivity::class.java)
            startActivity(intent)
        }
        binding.alarm.setOnClickListener {
            val intent=Intent(this,AlarmActivity::class.java)
            startActivity(intent)
        }
        binding.withdrawProfil.setOnClickListener{
            val intent=Intent(this,WithdrawActivity::class.java)
            startActivity(intent)
        }
        binding.setting.setOnClickListener {
            val intent=Intent(this,SettingActivity::class.java)
            startActivity(intent)
        }
        binding.profil.setOnClickListener {
            val intent=Intent(this,MypageActivity::class.java)
            startActivity(intent)
        }

        binding.back.setOnClickListener { finish() }

    }
}