package com.samuel.coosapp2.business.interactors.session

import android.util.Log
import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.datasource.network.handleUseCaseException
import com.samuel.coosapp2.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SetTheme
@Inject
constructor(
    private val appDataStore: AppDataStore,
) {
    private val TAG: String = "SetThemeDebug"

    fun execute(isDarkTheme: Boolean): Flow<DataState<Boolean>> = flow {
        appDataStore.setTheme(isDarkTheme)

        emit(DataState.data(response = null, data = isDarkTheme))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }

}