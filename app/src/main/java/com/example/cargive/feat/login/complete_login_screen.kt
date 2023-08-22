package com.example.cargive.feat.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cargive.R
import com.example.cargive.databinding.ActivityCompleteLoginScreenBinding
import com.example.cargive.databinding.ActivityIntroBinding
import com.example.cargive.feat.map.MainActivity

class complete_login_screen : AppCompatActivity() {
    private lateinit var viewBinding:ActivityCompleteLoginScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCompleteLoginScreenBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.moveMainButton.setOnClickListener {// 버튼 누를 때 메인화면 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

}