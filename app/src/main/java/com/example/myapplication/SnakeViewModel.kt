package com.example.myapplication

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.MutableLiveData

class SnakeViewModel : SkinBaseViewModel() {

    var snakeData = SnakeData()
    var keyboard = false

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
        key: Preferences.Key<T>,
        content: T,
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