package com.example.cargive.feat.profile

import android.content.Context
import android.content.SharedPreferences

class Carfile (context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("id", Context.MODE_PRIVATE)

    fun getImgAddress(defValue: String): String {
        return prefs.getString("img_address", defValue)!!
    }
    fun setImgAddress(address: String) {
        prefs.edit().putString("img_address", address).apply()
    }
}