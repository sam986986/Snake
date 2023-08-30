package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.StartupBinding
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class StartupFragment : Fragment() {

    private lateinit var binding: StartupBinding
    private lateinit var viewModel: SnakeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SnakeViewModel::class.java]
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

            val mapSizeArray = resources.getStringArray(R.array.map_size)
            val speedArray = resources.getStringArray(R.array.speed)
            mapSize.setText(viewModel.getSnakeData().length) // 預設文字
            speed.setText(viewModel.getSnakeData().speed)
            (binding.mapSize as MaterialAutoCompleteTextView).setSimpleItems(mapSizeArray) // 設定下拉選單
            (binding.speed as MaterialAutoCompleteTextView).setSimpleItems(speedArray)

            button.setOnClickListener {
                viewModel.setSnakeData(setSnakeData())
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.fragmentContainerView, SnakeFragment())
                }
            }
        }
    }

    private fun setSnakeData():SnakeData{
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