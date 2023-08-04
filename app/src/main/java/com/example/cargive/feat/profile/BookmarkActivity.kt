package com.example.cargive.feat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.R
import com.example.cargive.databinding.ActivityBookmarkBinding

class BookmarkActivity : AppCompatActivity() {
    private val binding by lazy { ActivityBookmarkBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val profileList= arrayListOf(
            BookmarkModel("우리동네 주차장","6개"),
            BookmarkModel("직장 근처","2개")
        )
        binding.listBmark.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.listBmark.setHasFixedSize(true)

        binding.listBmark.adapter=ProfileAdapter(profileList)
        binding.back.setOnClickListener { finish() }

    }
}