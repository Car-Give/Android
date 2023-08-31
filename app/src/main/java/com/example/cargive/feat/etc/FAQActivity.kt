package com.example.cargive.feat.etc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.R
import com.example.cargive.data.etc.FAQModel
import com.example.cargive.databinding.ActivityFaqactivityBinding

class FAQActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFaqactivityBinding
    private lateinit var adapter: FAQAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.toolbar_back)

        val list = listOf(FAQModel("알림을 받고싶지 않아요", "끄면 되요"), FAQModel("휴면 계정을 해제하고 싶어요", "해제하면 되요"))
        adapter = FAQAdapter()
        binding.faqList.layoutManager = LinearLayoutManager(this)
        binding.faqList.adapter = adapter
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