package com.example.myapplication

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.Direction.*
import com.example.myapplication.CheckeredType.*
import com.example.myapplication.databinding.FragmentSnakeBinding


class SnakeFragment : Fragment() {

    private lateinit var binding: FragmentSnakeBinding
    private lateinit var viewModel: SnakeViewModel

    private var snakeData = SnakeData()
    private var head = Pair(0, 0)
    private var tail = Pair(0, 0)
    private var direction = CENTER //移動方向
    private var spaceSize = 0 //空白方格數量
    private var start = false
    private var isMove = false
    private val location = mutableMapOf<Pair<Int, Int>, Checkered>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSnakeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SnakeViewModel::class.java]
        snakeData = viewModel.getSnakeData()
        initView()
    }


    private fun initView() {
        binding.apply {
            val length = when (snakeData.length) {
                "大" -> 21
                "中" -> 15
                "小" -> 9
                else -> 15
            }
            spaceSize = length * length - 2

            repeat(length) { y -> //開始建立方格
                val linearLayout = LinearLayout(context)
                val linearLayoutParams =
                    LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                linearLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.layoutParams = linearLayoutParams
                repeat(length) { x ->
                    val view = View(context)
                    val layoutParams = LinearLayout.LayoutParams(
                        snakeData.width / length - 2,
                        snakeData.width / length - 2
                    )
                    layoutParams.setMargins(1, 1, 1, 1) //底層背景為黑色，所以設定Margins會有邊線效果
                    layoutParams.weight = 1f
                    view.layoutParams = layoutParams
                    view.background =
                        AppCompatResources.getDrawable(requireContext(), SPACE.color)
                    linearLayout.addView(view)
                    location[Pair(x, y)] = Checkered(view, SPACE, CENTER) // 將方格存入map
                }
                linearlayout.addView(linearLayout)
            }
            linearlayout.setPadding(0, 1, 0, 1)
            head = Pair(length / 2, length / 2)
            tail = Pair(length / 2, length / 2 + 1)
            changeCheckered(head, HEAD, TOP) // 建立蛇頭
            changeCheckered(tail, BODY, TOP) // 建立蛇尾
            direction = TOP
            addFood()

            top.setOnClickListener {
                if (direction != DOWN && !isMove) { //只能往前或左右，不能回頭
                    direction = TOP
                    isMove = true // 不可改變移動方向，需移動完才能再次改變方向
                }
                if (!start) {
                    startMove()
                }
            }
            down.setOnClickListener {//同上
                if (direction != TOP && !isMove) {
                    direction = DOWN
                    isMove = true
                }
                if (!start) {
                    startMove()
                }
            }
            left.setOnClickListener {//同上
                if (direction != RIGHT && !isMove) {
                    direction = LEFT
                    isMove = true

                }
                if (!start) {
                    startMove()
                }
            }
            right.setOnClickListener {//同上
                if (direction != LEFT && !isMove) {
                    direction = RIGHT
                    isMove = true
                }
                if (!start) {
                    startMove()
                }
            }
        }
    }

    private fun startMove() { // 開始移動
        start = true
        val handler = Handler(Looper.getMainLooper())
        val speed: Long = when (viewModel.getSnakeData().speed) { //移动速度
            "慢" -> 1000
            "中" -> 500
            "快" -> 200
            "極快" -> 100
            else -> 500
        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                changeCheckered(head, BODY, direction) // 頭原本的位置改為身體
                when (direction) { // 變更頭的座標
                    TOP -> head = Pair(head.first, head.second - 1)
                    DOWN -> head = Pair(head.first, head.second + 1)
                    LEFT -> head = Pair(head.first - 1, head.second)
                    RIGHT -> head = Pair(head.first + 1, head.second)
                    else -> {}
                }
                if (checkHead()) { //判斷是否撞到障礙物
                    changeCheckered(head, HEAD, direction) //實際變更頭的位置
                    isMove = false // 可以開始改變移動方向
                    handler.postDelayed(this, speed)
                } else { //輸了
                    requireActivity().supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        replace(R.id.fragmentContainerView, StartupFragment())
                    }
                }
            }
        }, speed)
    }

    private fun changeCheckered(
        //更新格子型態及顏色
        pair: Pair<Int, Int>,
        type: CheckeredType,
        direction: Direction? = null,
    ) {
        val checkered = location[pair]
        if (checkered != null) {
            checkered.type = type
            checkered.view.background =
                AppCompatResources.getDrawable(
                    requireContext(),
                    checkered.type.color
                ) // 依照格子型態變更顏色
            if (direction != null) { // 參數有帶入的話變更方向
                checkered.direction = direction
            }
        }
    }

    private fun changeTail() {  //更新尾巴位置
        val checkered = location[tail]
        if (checkered != null) {
            when (checkered.direction) {
                TOP -> {
                    changeCheckered(tail, SPACE, CENTER)
                    tail = Pair(tail.first, tail.second - 1)
                }

                DOWN -> {
                    changeCheckered(tail, SPACE, CENTER)
                    tail = Pair(tail.first, tail.second + 1)
                }

                LEFT -> {
                    changeCheckered(tail, SPACE, CENTER)
                    tail = Pair(tail.first - 1, tail.second)
                }

                RIGHT -> {
                    changeCheckered(tail, SPACE, CENTER)
                    tail = Pair(tail.first + 1, tail.second)
                }

                else -> {}
            }
        }
    }

    private fun addFood() { // 新增食物
        if (spaceSize > 0) {
            var rd = (1..spaceSize).random() //隨機位置新增食物
            location.forEach { (pair, checkered) ->
                if (checkered.type == SPACE && rd == 1) {
                    changeCheckered(pair, FOOD)
                    spaceSize--
                    return
                } else if (checkered.type == SPACE) {
                    rd--
                }
            }
        }
    }

    private fun checkHead(): Boolean { //判斷head是否碰到食物，身體或者牆壁，碰到身體跟牆壁時判斷輸
        val checkered = location[head] ?: return false //為空的時候代表碰到牆壁，回傳false
        when (checkered.type) {
            FOOD -> addFood() //碰到食物時增加長度，並新增食物
            BODY -> {
                if (head == tail) {
                    changeTail()
                } else {
                    return false
                }
            }

            else -> changeTail() //改變尾巴位置
        }
        return true
    }
}