package com.samuel.coosapp2.business.interactors.video

import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.datasource.network.handleUseCaseException
import com.samuel.coosapp2.business.domain.util.DataState
import com.samuel.coosapp2.business.domain.util.DataStoreKeys
import com.samuel.coosapp2.presentation.main.video.home.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetOrderAndFilter(
    private val appDataStoreManager: AppDataStore
) {
    fun execute(): Flow<DataState<OrderAndFilter>> = flow {
        emit(DataState.loading<OrderAndFilter>())
        val filter = appDataStoreManager.readStringValue(DataStoreKeys.VIDEO_FILTER)?.let { filter ->
            getFilterFromValue(filter)
        }?: getFilterFromValue(VideoFilterOptions.DATE_CREATED.value)
        val order = appDataStoreManager.readStringValue(DataStoreKeys.VIDEO_ORDER)?.let { order ->
            getOrderFromValue(order)
        }?: getOrderFromValue(VideoOrderOptions.ASC.value)
        emit(DataState.data(
            response = null,
            data = OrderAndFilter(order = order, filter = filter)
        ))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}