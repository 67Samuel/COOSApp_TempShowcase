package com.samuel.coosapp2.business.datasource.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.samuel.coosapp2.business.domain.util.Constants.DARK_THEME_KEY_RAW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val APP_DATASTORE = "app"

class AppDataStoreManager(
    val context: Application
): AppDataStore {

    private val TAG: String = "AppDataStoreManagerDebug"

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)
    private val scope = CoroutineScope(Main)

    override fun setValue(
        key: String,
        value: String
    ) {
        scope.launch {
            context.dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
    }

    override suspend fun readStringValue(
        key: String,
    ): String? {
        return context.dataStore.data.first()[stringPreferencesKey(key)]
    }

    override suspend fun readThemeValue(): Boolean? {
        return context.dataStore.data.first()[DARK_THEME_KEY]
    }

    override suspend fun setTheme(isDarkTheme: Boolean) {
        context.dataStore.edit {
            it[DARK_THEME_KEY] = isDarkTheme
        }
    }

    companion object {
        private val DARK_THEME_KEY = booleanPreferencesKey(DARK_THEME_KEY_RAW)
    }
}