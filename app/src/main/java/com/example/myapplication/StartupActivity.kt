package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.StartupBinding

class StartupActivity : AppCompatActivity() {

    private lateinit var binding: StartupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StartupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.apply {
            button.setOnClickListener {
                val height = constrintlayout.height
                val width = constrintlayout.width

                val intent = Intent(this@StartupActivity, MainActivity::class.java)
                intent.putExtra("height", height)
                intent.putExtra("width", width)

                startActivity(intent)
                finish()
            }
        }
    }
}