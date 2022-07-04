package com.samuel.coosapp2.datasource.datastore

import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.domain.util.Constants.DARK_THEME_KEY_RAW

class AppDataStoreManagerFake: AppDataStore {

    private val datastore: MutableMap<String, Any> = mutableMapOf()

    override fun setValue(key: String, value: String) {
        datastore[key] = value
    }

    override suspend fun readStringValue(key: String): String? {
        datastore[key]?.let {
            return it.toString()
        } ?: return null
    }

    override suspend fun setTheme(isDarkTheme: Boolean) {
        datastore[DARK_THEME_KEY_RAW] = isDarkTheme
    }

    override suspend fun readThemeValue(): Boolean? {
        datastore[DARK_THEME_KEY_RAW]?.let {
            return it as Boolean
        } ?: return null
    }
}