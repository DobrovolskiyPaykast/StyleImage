package com.example.styleandimage.SupportFunctions

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(val context: Context) { // класс для работы с мини БД
    private val PREFS_NAME = "kotlincodes"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveStr(KEY_NAME1: String, value1: String, KEY_NAME2: String, value2: String) { // сохранение строки
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME1, value1)
        editor.putString(KEY_NAME2, value2)
        editor.commit()
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.commit()
    }

    fun getValueString(KEY_NAME: String): String? { // получение строки
        return sharedPref.getString(KEY_NAME, null)
    }

    fun clearSharedPreference() { // очистка БД
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.commit()
    }
}