package com.example.myapplication

import android.view.View

data class Checkered(
    var view: View,
    var type: CheckeredType,
    var direction: Direction,
)

enum class CheckeredType(val color: Int) {
    SPACE(R.color.sakura),
    BODY(R.color.ake),
    HEAD(R.color.red),
    FOOD(R.color.purple_200)
}

enum class Direction {
    TOP,
    DOWN,
    RIGHT,
    LEFT,
    CENTER
}