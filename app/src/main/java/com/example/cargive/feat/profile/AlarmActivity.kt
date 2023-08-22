package com.example.cargive.feat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cargive.R
import com.example.cargive.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAlarmBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            back.setOnClickListener { finish() }
        }
    }
}