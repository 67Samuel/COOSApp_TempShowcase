package com.samuel.coosapp2.business.datasource.datastore

import kotlinx.coroutines.flow.Flow


interface AppDataStore {

    fun setValue(
        key: String,
        value: String
    )

    suspend fun readStringValue(
        key: String,
    ): String?

    suspend fun setTheme(isDarkTheme: Boolean)

    suspend fun readThemeValue(): Boolean?

}