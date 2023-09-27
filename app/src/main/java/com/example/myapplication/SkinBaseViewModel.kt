package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class SkinBaseViewModel:ViewModel() {

    var theme = MutableLiveData(R.style.Theme_MyApplication)

    fun setTheme(style:Int) = theme.postValue(style)

    fun getTheme():Int = theme.value ?: R.style.Theme_MyApplication
}