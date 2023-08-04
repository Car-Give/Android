package com.example.cargive.feat.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.cargive.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var viewBinding:ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(viewBinding.root) // 뷰 바인딩의 루트 뷰로 설정

        // 2초 후에 로그인 화면으로 전환하기 위해 Handler를 사용하여 딜레이 설정
        Handler().postDelayed({
            // 로그인 화면으로 전환하는 인텐트 생성
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 현재 인트로 화면 종료
        }, 2000) // 2초(2000ms) 딜레이
    }
}
