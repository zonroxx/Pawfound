package com.example.androidpetapp.ui

import com.example.androidpetapp.data.model.Pet


sealed interface OpState {
    data object Idle : OpState
    data object Loading : OpState
    data class Success(val pet: Pet?) : OpState
    data class Error(val message: String) : OpState
}
