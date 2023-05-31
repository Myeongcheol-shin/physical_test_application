package com.shino72.physical.ui.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.Games
import com.google.android.gms.games.LeaderboardsClient
import com.google.firebase.auth.FirebaseAuth
import com.shino72.physical.R
import com.shino72.physical.databinding.ActivityMainBinding
import com.shino72.physical.ui.global.MyApplication


class MainActivity : AppCompatActivity() {
    private val vm : MainViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var leaderboardsClient: LeaderboardsClient
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainViewModel = vm
        setContentView(binding.root)
        binding.rankingShowBtn.setOnClickListener {
            showLeaderboard()
        }
        vm.score.observe(this, Observer {
            if(it != -1.0)
            {
                submitScoreToLeaderboard(it.toLong() * 10)
            }
        })
        vm.status.observe(this, Observer {
            when(it)
            {
                Status.FAST -> {
                    binding.ranking.visibility = View.GONE
                    binding.gameBtn.setBackgroundColor(resources.getColor(com.shino72.physical.R.color.pauseColor))
                }
                Status.START -> {
                    binding.ranking.visibility = View.GONE
                    binding.gameBtn.setBackgroundColor(resources.getColor(com.shino72.physical.R.color.startColor))
                }
                Status.MAIN -> {
                    binding.ranking.visibility = View.VISIBLE
                    binding.gameBtn.setBackgroundColor(resources.getColor(com.shino72.physical.R.color.mainColor))
                }
                else -> {
                    binding.ranking.visibility = View.GONE
                }
            }
        })

    }
    private fun googleSignIn() {
        val signInIntent = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build()
        ).signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    @SuppressLint("VisibleForTests")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // 리더보드 사용을 위해 Google Play Games 서비스에 로그인
                leaderboardsClient = Games.getLeaderboardsClient(this, account!!)
                // 리더보드 표시
            } catch (e: ApiException) {
            }
        }


    }
    private fun showLeaderboard() {
        if(::leaderboardsClient.isInitialized)
        {
            leaderboardsClient.getLeaderboardIntent(MyApplication.LEADER_BOARD_ID)
                .addOnSuccessListener { intent ->
                    startActivityForResult(intent, RC_LEADERBOARD)
                }
                .addOnFailureListener { e ->
                }
        }
        else {
            googleSignIn()
        }
    }


    private fun submitScoreToLeaderboard(score: Long) {
        if(::leaderboardsClient.isInitialized)
        {
            leaderboardsClient.submitScoreImmediate(MyApplication.LEADER_BOARD_ID,score).addOnCompleteListener {
                Toast.makeText(applicationContext, "성공", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "실패", Toast.LENGTH_SHORT).show()

                }
        }
        else {
            googleSignIn()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
        private const val RC_LEADERBOARD = 9002
    }
}
