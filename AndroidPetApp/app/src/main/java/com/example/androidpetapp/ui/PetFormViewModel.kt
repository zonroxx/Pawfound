package com.example.androidpetapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpetapp.data.model.Pet
import com.example.androidpetapp.data.repository.PetRepository
import kotlinx.coroutines.launch

class PetFormViewModel(
    private val repository: PetRepository = PetRepository()
) : ViewModel() {

    private val _saveState = MutableLiveData<OpState>(OpState.Idle)
    val saveState: LiveData<OpState> = _saveState

    fun save(id: Int?, pet: Pet) {
        if (_saveState.value == OpState.Loading) return
        _saveState.value = OpState.Loading
        viewModelScope.launch {
            try {
                val result = if (id == null) repository.addPet(pet)
                else repository.updatePet(id, pet)
                _saveState.value = OpState.Success(result)
            } catch (e: Exception) {
                _saveState.value = OpState.Error(e.message ?: "Could not save pet")
            }
        }
    }

    /** Reset after an error has been shown, so the same error isn't re-handled. */
    fun consumeError() {
        if (_saveState.value is OpState.Error) _saveState.value = OpState.Idle
    }
}
