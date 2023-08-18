package com.example.cargive.feat.profile

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.R
import com.example.cargive.databinding.ActivityProfilecarBinding

class ProfilecarActivity : AppCompatActivity() {
    private val binding by lazy { ActivityProfilecarBinding.inflate(layoutInflater) }
    private var carList= arrayListOf<MycarModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding) {
            backItem.setOnClickListener { finish() }

        }
        binding.addMycar.setOnClickListener {
            val intent=Intent(this,PluscarActivity::class.java)
            startActivity(intent)
        }
        carList= arrayListOf(
            MycarModel("car","12바 1234","점검날짜: ","주행거리: "),
            MycarModel("car","12나 1234","점검날짜: ","주행거리: "),
            MycarModel("car","12가 1234","점검날짜: ","주행거리: ")
        )
        binding.listMycar.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        binding.listMycar.setHasFixedSize(true)

        binding.listMycar.adapter=MycarAdapter(carList)
    }
}