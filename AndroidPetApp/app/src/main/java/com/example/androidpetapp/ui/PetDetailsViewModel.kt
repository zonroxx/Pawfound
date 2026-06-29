package com.example.androidpetapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpetapp.data.repository.PetRepository
import kotlinx.coroutines.launch

class PetDetailsViewModel(
    private val repository: PetRepository = PetRepository()
) : ViewModel() {

    private val _deleteState = MutableLiveData<OpState>(OpState.Idle)
    val deleteState: LiveData<OpState> = _deleteState

    fun delete(id: Int) {
        if (_deleteState.value == OpState.Loading) return
        _deleteState.value = OpState.Loading
        viewModelScope.launch {
            try {
                repository.deletePet(id)
                _deleteState.value = OpState.Success(null)
            } catch (e: Exception) {
                _deleteState.value = OpState.Error(e.message ?: "Could not delete pet")
            }
        }
    }

    fun consumeError() {
        if (_deleteState.value is OpState.Error) _deleteState.value = OpState.Idle
    }
}
