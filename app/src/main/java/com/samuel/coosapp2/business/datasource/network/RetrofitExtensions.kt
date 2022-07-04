package com.samuel.coosapp2.business.datasource.network

import android.accounts.NetworkErrorException
import android.util.Log
import com.samuel.coosapp2.business.domain.util.DataState
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_GETTING_THEME_SETTINGS
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_UNKNOWN
import com.samuel.coosapp2.business.domain.util.ErrorHandling.UNABLE_TO_RETRIEVE_VIDEO_FROM_NETWORK
import com.samuel.coosapp2.business.domain.util.MessageType
import com.samuel.coosapp2.business.domain.util.Response
import com.samuel.coosapp2.business.domain.util.UIComponentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import retrofit2.HttpException
import java.io.IOException

fun <T> handleUseCaseException(e: Throwable): DataState<T> {
    e.printStackTrace()
    when (e) {
        is HttpException -> { // Retrofit exception
            val errorResponse = getHttpExceptionMessage(e)
            return DataState.error<T>(
                response = Response(
                    message = errorResponse,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
        is NetworkErrorException,
        is ClosedReceiveChannelException,
        is CancellationException -> {
            return DataState.error<T>(
                response = Response(
                    message = UNABLE_TO_RETRIEVE_VIDEO_FROM_NETWORK,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
        is IOException -> {
            return DataState.error<T>(
                response = Response(
                    message = ERROR_GETTING_THEME_SETTINGS,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Error()
                )
            )
        }
        else -> {
            return DataState.error<T>(
                response = Response(
                    message = e.message,
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                )
            )
        }
    }
}

private fun getHttpExceptionMessage(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}