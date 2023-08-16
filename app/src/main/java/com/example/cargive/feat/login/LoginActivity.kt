package com.example.cargive.feat.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.cargive.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLoginBinding
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.kakaologinButton.setOnClickListener {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    // 카카오 로그인 실패 처리
                    Log.e(TAG, "카카오 로그인 실패: ${error.message}")
                } else if (token != null) {
                    // 카카오 로그인 성공 처리
                    Log.i(TAG, "카카오 로그인 성공")
                    requestKakaoUserInfo()
                }
            }
        }
    }

    private fun requestKakaoUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // 사용자 정보 요청 실패 처리
                Log.e(TAG, "사용자 정보 요청 실패: ${error.message}")
            } else if (user != null) {
                val userId = user.id
                val nickname = user.kakaoAccount?.profile?.nickname
                val isEmailVerified = user.kakaoAccount?.isEmailVerified ?: false

                // 이메일 미인증 시 동의창 띄우기
                if (!isEmailVerified) {
                    UserApiClient.instance.loginWithNewScopes(this, listOf("account_email")) { oAuthResponse, consentError ->
                        if (consentError != null) {
                            // 동의 실패 처리
                            Log.e(TAG, "동의 실패: ${consentError.message}")
                        } else {
                            // 동의 성공 처리
                            Log.i(TAG, "동의 성공")
                            // 동의창 띄운 후 추가 작업 수행
                            // 예를 들어, 사용자 정보 요청 등
                        }
                    }

                } else {
                    // 이미 이메일 인증된 사용자의 처리
                    Log.i(TAG, "이미 이메일 인증된 사용자")
                    // 추가 작업 수행
                }
            }
        }
    }
}
