package com.example.cargive.feat.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.example.cargive.databinding.ActivityIntroBinding
import com.example.cargive.feat.login.LoginActivity

class IntroActivity : Activity() { // AppCompatActivity가 아닌 Activity를 상속받도록 변경

    private lateinit var viewBinding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상단바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        viewBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}
