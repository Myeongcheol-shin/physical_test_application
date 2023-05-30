package com.shino72.physical.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.shino72.physical.R
import com.shino72.physical.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val vm : MainViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.mainViewModel = vm
        setContentView(binding.root)

        vm.status.observe(this, Observer {
            when(it)
            {
                Status.FAST -> {
                    binding.gameBtn.setBackgroundColor(resources.getColor(R.color.pauseColor))
                }
                Status.START -> {
                    binding.gameBtn.setBackgroundColor(resources.getColor(R.color.startColor))
                }
                Status.MAIN -> {
                    binding.gameBtn.setBackgroundColor(resources.getColor(R.color.mainColor))
                }
                else -> {
                }
            }
        })
    }
}