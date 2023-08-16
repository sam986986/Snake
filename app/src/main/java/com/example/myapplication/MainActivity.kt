package com.example.myapplication

import android.app.ActionBar.LayoutParams
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.Direction.*
import com.example.myapplication.CheckeredType.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tail: Pair<Int, Int>
    private val speed = 500L //移動速度
    private var head = Pair(0, 0)
    private var height = 0
    private var width = 0
    private val length = 11 //方格寬高長度
    private var start = false
    private var direction = CENTER //移動方向
    private var spaceSize = 0 //空白方格數量
    private var isMove = false
    private val location = mutableMapOf<Pair<Int, Int>, Checkered>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        height = intent.getIntExtra("height", 0)
        width = intent.getIntExtra("width", 0)
        spaceSize = length * length - 2
        initView()
    }

    private fun initView() {
        binding.apply {
            repeat(length) { y ->
                val linearLayout = LinearLayout(applicationContext)
                val linearLayoutParams =
                    LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                linearLayout.orientation = LinearLayout.HORIZONTAL
                linearLayout.layoutParams = linearLayoutParams
                repeat(length) { x ->
                    val view = View(applicationContext)
                    val layoutParams = LayoutParams(
                        width / length - 1,
                        width / length - 1
                    )

                    layoutParams.setMargins(1, 1, 1, 1)
                    view.layoutParams = layoutParams
                    view.background =
                        AppCompatResources.getDrawable(this@MainActivity, SPACE.color)
                    linearLayout.addView(view)
                    location[Pair(x, y)] = Checkered(view, SPACE, CENTER)
                }
                linearlayout.addView(linearLayout)
            }
            head = Pair(length / 2, length / 2)
            tail = Pair(length / 2, length / 2 + 1)
            changeCheckered(head, HEAD, TOP) // 建立蛇頭
            changeCheckered(tail, BODY, TOP) // 建立蛇尾
            direction = TOP
            addFood()

            top.setOnClickListener {
                if (direction != DOWN && !isMove) {
                    direction = TOP
                    isMove = true
                }
                if (!start) {
                    start()
                }
            }
            down.setOnClickListener {
                if (direction != TOP && !isMove) {
                    direction = DOWN
                    isMove = true
                }
                if (!start) {
                    start()
                }
            }
            left.setOnClickListener {
                if (direction != RIGHT && !isMove) {
                    direction = LEFT
                    isMove = true

                }
                if (!start) {
                    start()
                }
            }
            right.setOnClickListener {
                if (direction != LEFT && !isMove) {
                    direction = RIGHT
                    isMove = true
                }
                if (!start) {
                    start()
                }
            }
        }
    }

    private fun start() {
        start = true
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(object : Runnable {
            override fun run() {
                changeCheckered(head, BODY, direction)
                when (direction) {
                    TOP -> head = Pair(head.first, head.second - 1)
                    DOWN -> head = Pair(head.first, head.second + 1)
                    LEFT -> head = Pair(head.first - 1, head.second)
                    RIGHT -> head = Pair(head.first + 1, head.second)
                    else -> {}
                }
                if (checkHead()) {
                    changeCheckered(head, HEAD, direction)
                    isMove = false
                    handler.postDelayed(this, speed)
                } else {
                    val intent = Intent(this@MainActivity, StartupActivity::class.java)
                    startActivity(intent)
                    finish()
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
                AppCompatResources.getDrawable(this@MainActivity, checkered.type.color)
            if (direction != null) {
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
            var rd = (1..spaceSize).random()
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
        val checkered = location[head] ?: return false
        when (checkered.type) {
            FOOD -> { //碰到食物時增加長度，並新增食物
                addFood()
            }

            BODY -> {
                if (head == tail) {
                    changeTail()
                } else {
                    return false
                }
            }

            else -> { //改變尾巴位置
                changeTail()
            }
        }
        return true
    }
}