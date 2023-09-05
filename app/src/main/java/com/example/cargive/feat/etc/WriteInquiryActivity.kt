package com.example.cargive.feat.etc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.cargive.R
import com.example.cargive.databinding.ActivityWriteInquiryBinding

class WriteInquiryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteInquiryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteInquiryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val items = listOf("질문 유형을 선택해주세요.", "유형 1", "유형 2")
        val adapter = TypeSpinnerAdapter(items, this)
        binding.inquiryTypeSpinner.adapter = adapter

        binding.inquiryTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    0 -> {
                        binding.postBtn.isEnabled = false
                        binding.postFrame.setBackgroundResource(R.drawable.round_disabled)
                    }
                    else -> {
                        Toast.makeText(applicationContext, "${items[position]} 눌림", Toast.LENGTH_SHORT).show()
                        if(binding.inquiryContent.length() != 0) {
                            binding.postBtn.isEnabled = true
                            binding.postFrame.setBackgroundResource(R.drawable.round_follow)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }

        binding.inquiryContent.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                binding.textLength.text = "${s?.length}/500"
                if(s?.length == 0 || binding.inquiryTypeSpinner.selectedItemPosition == 0) {
                    binding.postBtn.isEnabled = false
                    binding.postFrame.setBackgroundResource(R.drawable.round_disabled)
                } else {
                    binding.postBtn.isEnabled = true
                    binding.postFrame.setBackgroundResource(R.drawable.round_follow)
                }
            }

        })



        binding.postFrame.setOnClickListener {

        }
        binding.postBtn.setOnClickListener {

        }
    }
}