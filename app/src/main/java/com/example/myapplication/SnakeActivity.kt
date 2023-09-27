package com.example.myapplication

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.forEach
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.LayoutDialogMenuBinding
import com.example.myapplication.databinding.LayoutDialogStopBinding
import kotlinx.coroutines.launch

class SnakeActivity : SkinBaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SnakeViewModel by viewModels()
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {// 返回鍵
            // 禁用返回鍵所以留空
        }
    }

    companion object {
        private const val THEME = "theme"
    }

    override fun viewModel(): ViewModel = viewModel

    override fun initLayout(): View = binding.root
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this, callback)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel.theme.observe(this) {
            setStyle(it)
        }
        lifecycleScope.launch {
            viewModel.getValue(dataStore, intPreferencesKey(THEME), back = { value ->
                viewModel.setTheme(value)
            })
        }
        setContentView(binding.root)
        initView()
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(binding.fragmentContainerView.id, SnakeMainFragment())
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
                            replace(binding.fragmentContainerView.id, SnakeMainFragment())
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
                        when (viewModel.getTheme()) {
                            R.style.Theme_MyApplication -> dialogBinding.greedTheme.id
                            R.style.Theme_MyApplication_bule -> dialogBinding.blueTheme.id
                            else -> dialogBinding.greedTheme.id
                        }
                    )

                    dialogBinding.changeTheme.setOnCheckedChangeListener { _, checkId -> //切換主題並刷新畫面
                        when (checkId) {
                            R.id.blue_theme -> {
                                lifecycleScope.launch {
                                    viewModel.putValue(
                                        dataStore,
                                        intPreferencesKey(THEME),
                                        R.style.Theme_MyApplication_bule
                                    )
                                }
                                viewModel.setTheme(R.style.Theme_MyApplication_bule)
                                setStyle(R.style.Theme_MyApplication_bule)
                            }

                            R.id.greed_theme -> {
                                lifecycleScope.launch {
                                    viewModel.putValue(
                                        dataStore,
                                        intPreferencesKey(THEME),
                                        R.style.Theme_MyApplication
                                    )
                                }
                                viewModel.setTheme(R.style.Theme_MyApplication)
                                setStyle(R.style.Theme_MyApplication)
                            }
                        }
                    }
                }
            }
            true
        }
    }

    private fun setStyle(theme: Int) {
        setTheme(theme)
        binding.root.forEach {
            val typedValue = TypedValue()
            if (it is Toolbar) {
                this.theme.resolveAttribute(
                    androidx.appcompat.R.attr.colorPrimary,
                    typedValue,
                    true
                )
                it.setBackgroundResource(typedValue.resourceId)
            }
        }
    }
}