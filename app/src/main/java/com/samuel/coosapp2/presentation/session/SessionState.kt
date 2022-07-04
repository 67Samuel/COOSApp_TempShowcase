package com.samuel.coosapp2.presentation.session

import com.samuel.coosapp2.business.domain.util.Queue
import com.samuel.coosapp2.business.domain.util.StateMessage

data class SessionState(
    val isLoading: Boolean = false,
    val isDarkTheme: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)