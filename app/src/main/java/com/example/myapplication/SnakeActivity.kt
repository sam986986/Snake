package com.example.myapplication

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.myapplication.databinding.ActivityMainBinding

class SnakeActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding

    private val callback = object : OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            // 返回鍵
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this,callback)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (savedInstanceState == null){
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(binding.fragmentContainerView.id,StartupFragment())
            }
        }
    }
}