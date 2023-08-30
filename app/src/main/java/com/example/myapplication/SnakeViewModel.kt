package com.example.myapplication

import androidx.lifecycle.ViewModel

class SnakeViewModel:ViewModel() {

    private var snakeData = SnakeData()
    fun setSnakeData(snakeData: SnakeData){
        this.snakeData = snakeData
    }

    fun getSnakeData():SnakeData{
        return snakeData
    }
}