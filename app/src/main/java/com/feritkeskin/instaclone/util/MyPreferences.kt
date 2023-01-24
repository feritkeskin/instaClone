package com.feritkeskin.instaclone.util

import android.content.Context
import androidx.preference.PreferenceManager

class MyPreferences(context: Context?) {

    companion object {
        private const val DARK_STATUS = "io.github.manuelernesto.DARK_STATUS"
        private const val USER_NAME = "userName"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

    var userName = preferences.getString(USER_NAME, "")
        set(value) = preferences.edit().putString(USER_NAME, value).apply()

}