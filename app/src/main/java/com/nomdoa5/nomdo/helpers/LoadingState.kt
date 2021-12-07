package com.nomdoa5.nomdo.helpers

sealed class LoadingState {
    object Success: LoadingState()
    data class Error(val message: String) : LoadingState()
    object Loading: LoadingState()
    object Empty: LoadingState()
}
