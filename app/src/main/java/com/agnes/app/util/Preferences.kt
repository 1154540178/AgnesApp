package com.agnes.app.util

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    private const val PREF_NAME = "agnes_prefs"
    private const val KEY_API_KEY = "api_key"
    private const val KEY_API_BASE_URL = "api_base_url"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key).apply()
    }

    fun getApiBaseUrl(): String = prefs.getString(KEY_API_BASE_URL, DEFAULT_AGNES_API_BASE_URL) ?: DEFAULT_AGNES_API_BASE_URL

    fun setApiBaseUrl(url: String) {
        prefs.edit().putString(KEY_API_BASE_URL, url).apply()
    }
}
