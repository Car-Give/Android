package com.example.cargive.feat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.cargive.R
import com.example.cargive.databinding.ActivityMypageBinding

class MypageActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMypageBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            back.setOnClickListener { finish() }
            userId.addTextChangedListener(object :TextWatcher{
                var maxText = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    maxText=userId.toString()
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(userPhone.length()==0&&userId.length()==0&&userName.length()==0&&userEmail.length()==0){
                        mypageBtn.setBackgroundResource(R.drawable.mypage_gray)
                    }
                    else{
                        mypageBtn.setBackgroundResource(R.drawable.mypage_blue)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            userName.addTextChangedListener(object :TextWatcher{
                var maxText = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    maxText=userName.toString()
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(userPhone.length()==0&&userId.length()==0&&userName.length()==0&&userEmail.length()==0){
                        mypageBtn.setBackgroundResource(R.drawable.mypage_gray)
                    }
                    else{
                        mypageBtn.setBackgroundResource(R.drawable.mypage_blue)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            userEmail.addTextChangedListener(object :TextWatcher{
                var maxText = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    maxText=userEmail.toString()
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(userPhone.length()==0&&userId.length()==0&&userName.length()==0&&userEmail.length()==0){
                        mypageBtn.setBackgroundResource(R.drawable.mypage_gray)
                    }
                    else{
                        mypageBtn.setBackgroundResource(R.drawable.mypage_blue)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            userPhone.addTextChangedListener(object :TextWatcher{
                var maxText = ""
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    maxText=userPhone.toString()
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(userPhone.length()==0&&userId.length()==0&&userName.length()==0&&userEmail.length()==0){
                        mypageBtn.setBackgroundResource(R.drawable.mypage_gray)
                    }
                    else{
                        mypageBtn.setBackgroundResource(R.drawable.mypage_blue)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
        }
    }
}