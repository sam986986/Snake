package com.example.myapplication

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.myapplication.Direction.*
import com.example.myapplication.CheckeredType.*
import com.example.myapplication.databinding.FragmentSnakeBinding
import kotlin.math.abs


class SnakeFragment : Fragment() {

    private lateinit var binding: FragmentSnakeBinding
    private lateinit var moveRunnable: Runnable
    private var speed = 500L
    private val viewModel: SnakeViewModel by activityViewModels()
    private val handler = Handler(Looper.getMainLooper())
    private var downX = 0f
    private var downY = 0f
    private var snakeData = SnakeData()
    private var head = Pair(0, 0)
    private var tail = Pair(0, 0)
    private var direction = CENTER //移動方向
    private var spaceSize = 0 //空白方格數量
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
        snakeData = viewModel.snakeData
        setOnTouchListener() // 設定滑動事件
        initView()
    }


    private fun initView() {
        viewModel.setShowStop(true)
        viewModel.setShowChangeTheme(false)
        setMove()
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
                    val typedValue = TypedValue()
                    requireContext().theme.resolveAttribute(R.attr.mainColor, typedValue, true)
                    view.setBackgroundResource(typedValue.resourceId)
                    linearLayout.addView(view)
                    location[x to y] = Checkered(view, SPACE, CENTER) // 將方格存入map
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

            val showKeyboard = viewModel.keyboard
            top.isVisible = showKeyboard
            down.isVisible = showKeyboard
            left.isVisible = showKeyboard
            right.isVisible = showKeyboard
            top.setOnClickListener {
                if (direction != DOWN && !isMove) { //只能往前或左右，不能回頭
                    direction = TOP
                    isMove = true // 不可改變移動方向，需移動完才能再次改變方向
                }
                viewModel.setStart(true)
            }
            down.setOnClickListener {//同上
                if (direction != TOP && !isMove) {
                    direction = DOWN
                    isMove = true
                }
                viewModel.setStart(true)
            }
            left.setOnClickListener {//同上
                if (direction != RIGHT && !isMove) {
                    direction = LEFT
                    isMove = true
                }
                viewModel.setStart(true)
            }
            right.setOnClickListener {//同上
                if (direction != LEFT && !isMove) {
                    direction = RIGHT
                    isMove = true
                }
                viewModel.setStart(true)
            }
            viewModel.start.observe(viewLifecycleOwner){start->
                if (start){
                    startMove()
                } else if (this@SnakeFragment::moveRunnable.isInitialized){
                    handler.removeCallbacks(moveRunnable)  //暫停
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener() {
        binding.root.setOnTouchListener { _, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                }

                MotionEvent.ACTION_UP -> {
                    val x = abs(event.x - downX)
                    val y = abs(event.y - downY)
                    if (x > y) {
                        if (event.x - downX > 30) {
                            binding.right.callOnClick() //往右滑動
                        }
                        if (downX - event.x > 30) {
                            binding.left.callOnClick() //往左滑動
                        }
                    } else {
                        if (event.y - downY > 30) {
                            binding.down.callOnClick() //往下滑動
                        }
                        if (downY - event.y > 30) {
                            binding.top.callOnClick() //往上滑動
                        }
                    }
                }
            }
            true
        }
    }
    private fun setMove(){

        speed = when (viewModel.snakeData.speed) { //移动速度
            "慢" -> 1000
            "中" -> 500
            "快" -> 200
            "極快" -> 100
            else -> 500
        }

        moveRunnable = object : Runnable {
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
                    viewModel.setStart(false)
                    parentFragmentManager.commit {
                        setReorderingAllowed(true)
                        remove(this@SnakeFragment)
                        replace(R.id.fragmentContainerView, StartupFragment())
                    }
                }
            }
        }
    }
    private fun startMove() { // 開始移動
        handler.postDelayed(moveRunnable, speed)
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

            if (type == SPACE) {
                val typedValue = TypedValue()
                requireContext().theme.resolveAttribute(R.attr.mainColor, typedValue, true)
                checkered.view.setBackgroundResource(typedValue.resourceId)
            } else {
                checkered.view.background =
                    AppCompatResources.getDrawable(
                        requireContext(),
                        checkered.type.color
                    ) // 依照格子型態變更顏色
            }
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
//            val t = TypedValue()
//            requireContext().theme.resolveAttribute(R.attr.buttonColor,t,true)
//            view.setBackgroundResource(t.resourceId)