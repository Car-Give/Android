package com.example.cargive.feat.etc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.R
import com.example.cargive.databinding.ActivityAnnoucementBinding
import com.example.cargive.data.etc.AnnounceModel

class AnnoucementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnnoucementBinding
    private val type = "notice"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnoucementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.toolbar_back)
        getUsageHistory()
    }

    private fun getUsageHistory() {
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val list = listOf(AnnounceModel(type, "주차", "2023 08 19"))
        val adapter = AnnounceAdapter()
        binding.announcementList.adapter = adapter
        binding.announcementList.layoutManager = LinearLayoutManager(this)
        adapter.submitList(list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}