package com.example.myapplication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

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
        return start.value ?: false
    }


    suspend fun <T> putValue(
        dataStore: DataStore<Preferences>,
        content: T,
        key: Preferences.Key<T>,
    ) {
        dataStore.edit { preferences ->
            preferences[key] = content
        }
    }

    suspend fun <T> getValue(
        dataStore: DataStore<Preferences>,
        key: Preferences.Key<T>,
        back: (T) -> Unit,
    ) {
        dataStore.edit { preferences ->
            val value = preferences[key]
            if (value != null) {
                back(value)
            }
        }
    }

}