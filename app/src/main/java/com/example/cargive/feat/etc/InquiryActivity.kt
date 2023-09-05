package com.example.cargive.feat.etc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cargive.R
import com.example.cargive.data.etc.InquiryModel
import com.example.cargive.databinding.ActivityInquiryBinding

class InquiryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInquiryBinding
    private lateinit var adapter: InquiryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar) //커스텀한 toolbar를 액션바로 사용
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.toolbar_back)

        adapter = InquiryAdapter()
        binding.myInquiryList.adapter = adapter
        binding.myInquiryList.layoutManager = LinearLayoutManager(this)
        adapter.submitList(listOf(InquiryModel("내 위치가 이상해요", "2023 08 31")))


        binding.buttonFrame.setOnClickListener {
            val intent = Intent(this, WriteInquiryActivity::class.java)
            startActivity(intent)
        }
        binding.writeInquiryBtn.setOnClickListener {
            val intent = Intent(this, WriteInquiryActivity::class.java)
            startActivity(intent)
        }
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