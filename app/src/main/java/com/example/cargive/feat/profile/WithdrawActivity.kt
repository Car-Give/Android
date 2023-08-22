package com.example.cargive.feat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cargive.R
import com.example.cargive.databinding.ActivityWithdrawBinding

class WithdrawActivity : AppCompatActivity() {
    private val binding by lazy { ActivityWithdrawBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            back.setOnClickListener {
                finish()
            }
            withdrawCheck.setOnCheckedChangeListener { button, isChecked ->
                if(isChecked){withdrawBtn.setBackgroundResource(R.drawable.withdraw_blue) }
                else{
                    withdrawBtn.setBackgroundResource(R.drawable.withdraw_gray)
                }
            }
        }
    }
}