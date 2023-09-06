package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SnakeViewModel : ViewModel() {

    var snakeData = SnakeData()
    var keyboard = false
    var theme = R.style.Theme_MyApplication

    var showStop = MutableLiveData(false)
    var showChangeTheme = MutableLiveData(true)
    var start = MutableLiveData(false)

    fun setShowStop(boolean: Boolean) = showStop.postValue(boolean)
    fun setShowChangeTheme(boolean: Boolean) = showChangeTheme.postValue(boolean)

    fun setStart(boolean: Boolean) {
        if (boolean != start.value) {
            start.postValue(boolean)
        }
    }

    fun getStart(): Boolean {
        return start.value!!
    }


}