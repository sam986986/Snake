package com.example.myapplication

import android.os.Bundle
import android.util.TypedValue
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.LayoutDialogMenuBinding
import com.example.myapplication.databinding.LayoutDialogStopBinding

class SnakeActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SnakeViewModel by viewModels()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {// 返回鍵
            // 禁用返回鍵所以留空
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(viewModel.theme) //設定主題
        setContentView(binding.root)
        initView()
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(binding.fragmentContainerView.id, StartupFragment())
            }
        }
    }

    private fun initView() {
        setToolbar()
    }

    private fun setToolbar() {
        viewModel.showChangeTheme.observe(this) { value ->
            binding.toolbar.menu.findItem(R.id.change_theme).isVisible = value //是否顯示切換主題按鈕
        }
        viewModel.showStop.observe(this) { value ->
            binding.toolbar.menu.findItem(R.id.stop).isVisible = value //是否顯示暫停按鈕
        }

        val typedValue = TypedValue()
        theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        binding.toolbar.setBackgroundResource(typedValue.resourceId) //toolbar主題顏色變更

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.stop -> { //暫停按鈕
                    val start = viewModel.getStart() //確認是否已開始遊戲
                    viewModel.setStart(false)
                    val dialogBinding = LayoutDialogStopBinding.inflate(layoutInflater)
                    val dialog = AlertDialog
                        .Builder(this)
                        .setView(dialogBinding.root)
                        .setCancelable(false)
                        .show()
                    dialogBinding.backGame.setOnClickListener {//返回遊戲
                        if (start) { //已經開始遊戲的返回遊戲後繼續移動
                            viewModel.setStart(true)
                        }
                        dialog.dismiss()
                    }

                    dialogBinding.backHome.setOnClickListener {//返回主頁
                        supportFragmentManager.commit {
                            setReorderingAllowed(true)
                            replace(binding.fragmentContainerView.id, StartupFragment())
                            dialog.dismiss()
                        }
                    }

                    dialogBinding.restart.setOnClickListener {//重新開始
                        recreate()
                    }

                }

                R.id.change_theme -> { //切換主題顏色

                    val dialogBinding = LayoutDialogMenuBinding.inflate(layoutInflater)
                    AlertDialog
                        .Builder(this)
                        .setView(dialogBinding.root)
                        .show()

                    dialogBinding.changeTheme.check( //確認目前主題顏色
                        when (viewModel.theme) {
                            R.style.Theme_MyApplication -> dialogBinding.greedTheme.id
                            R.style.Theme_MyApplication_bule -> dialogBinding.blueTheme.id
                            else -> dialogBinding.greedTheme.id
                        }
                    )

                    dialogBinding.changeTheme.setOnCheckedChangeListener { _, checkId -> //切換主題並刷新畫面
                        when (checkId) {
                            R.id.blue_theme -> {
                                viewModel.theme = R.style.Theme_MyApplication_bule
                                recreate()
                            }

                            R.id.greed_theme -> {
                                viewModel.theme = R.style.Theme_MyApplication
                                recreate()
                            }
                        }
                    }
                }
            }

            true
        }
    }
}