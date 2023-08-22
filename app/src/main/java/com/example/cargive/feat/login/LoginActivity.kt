package com.example.cargive.feat.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.cargive.R
import com.example.cargive.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

class LoginActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityLoginBinding
    private val RC_SIGN_IN = 123
    private val TAG = "LoginActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.kakaoButton.setOnClickListener {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    // 카카오 로그인 실패 처리
                    Log.e(TAG, "카카오 로그인 실패: ${error.message}")
                } else if (token != null) {
                    val kakaoAccessToken = token.accessToken // 엑세스 토큰 얻기
                    requestKakaoUserInfo()
                    // 카카오 로그인 성공 처리
                    Log.i(TAG, "카카오 로그인 성공")
                    Log.i(TAG, kakaoAccessToken)
                    requestKakaoUserInfo()
                }
            }
        }
        viewBinding.naverButton.setOnClickListener {
            naverLogin()
        }

        viewBinding.googleButton.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
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
                        UserApiClient.instance.loginWithNewScopes(
                            this,
                            listOf("account_email")
                        ) { oAuthResponse, consentError ->
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

        private fun naverLogin() {
            val oauthLoginCallback = object : OAuthLoginCallback {
                override fun onSuccess() {
                    val naverAccessToken = NaverIdLoginSDK.getAccessToken()
                    // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                    Log.d("NaverLogin", "RefreshToken -> ${NaverIdLoginSDK.getRefreshToken()}")
                    Log.d("NaverLogin", "Expires -> ${NaverIdLoginSDK.getExpiresAt()}")
                    Log.d("NaverLogin", "Type -> ${NaverIdLoginSDK.getTokenType()}")
                    Log.d("NaverLogin", "State -> ${NaverIdLoginSDK.getState()}")
                    Log.d(TAG, "네이버 로그인 성공")
                    Log.d(TAG, "네이버 AccessToken -> $naverAccessToken")
                }

                override fun onFailure(httpStatus: Int, message: String) {
                    val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                    val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                    Toast.makeText(
                        this@LoginActivity,
                        "errorCode:$errorCode, errorDesc:$errorDescription",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(errorCode: Int, message: String) {
                    onFailure(errorCode, message)
                }
            }

            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account.idToken!!)
                    Log.d(TAG, "Google AccessToken -> ${account.idToken}")
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase 로그인 성공
                    Log.d(TAG, "signInWithCredential:success")
                    val user = FirebaseAuth.getInstance().currentUser
                    // 로그인 성공 후의 추가 작업 수행
                } else {
                    // Firebase 로그인 실패
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

}
