package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.myapplication.databinding.StartupBinding

class SnakeMainFragment : Fragment() {

    private lateinit var binding: StartupBinding
    private val viewModel: SnakeViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = StartupBinding.inflate(inflater)
        return binding.root
    }

    private fun initView() {
        binding.apply {
            keyboard.isChecked = viewModel.keyboard

            mapSize.setText(viewModel.snakeData.length) // 預設文字
            speed.setText(viewModel.snakeData.speed)

            mapSize.setSimpleItems(resources.getStringArray(R.array.map_size)) // 設定下拉選單
            speed.setSimpleItems(resources.getStringArray(R.array.speed))
            viewModel.setShowStop(false)
            viewModel.setShowChangeTheme(true)

            button.setOnClickListener {
                viewModel.snakeData = setSnakeData()
                viewModel.keyboard = keyboard.isChecked
                parentFragmentManager.commit {
                    replace(R.id.fragmentContainerView, SnakeFragment())
                }
            }

        }
    }

    private fun setSnakeData(): SnakeData {
        val snakeData = SnakeData()
        binding.apply {
            snakeData.height = constrintlayout.height
            snakeData.width = constrintlayout.width
            snakeData.length = mapSize.text.toString() // 地圖大小
            snakeData.speed = speed.text.toString() // 移動速度
        }
        return snakeData
    }
}