package com.example.androidpetapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpetapp.data.model.Pet
import com.example.androidpetapp.data.repository.PetRepository
import com.example.androidpetapp.ui.PetListUiState.Companion.ALL
import kotlinx.coroutines.launch

class PetViewModel(
    private val repository: PetRepository = PetRepository()
) : ViewModel() {

    private var allPets: List<Pet> = emptyList()
    private var isLoading = true
    private var error: String? = null

    private var searchTerm = ""
    private var sizeFilter = ALL
    private var temperamentFilter = ALL
    private var availabilityFilter = ALL

    private val _uiState = MutableLiveData(PetListUiState(isLoading = true))
    val uiState: LiveData<PetListUiState> = _uiState

    init {
        loadPets()
    }

    fun loadPets() {
        isLoading = true
        error = null
        emitState()
        viewModelScope.launch {
            try {
                allPets = repository.getAllPets()
                error = null
            } catch (e: Exception) {
                allPets = emptyList()
                error = e.message ?: "Failed to load pets"
            } finally {
                isLoading = false
                emitState()
            }
        }
    }

    fun setSearchTerm(value: String) {
        if (value == searchTerm) return
        searchTerm = value
        emitState()
    }

    fun setSizeFilter(value: String) {
        if (value == sizeFilter) return
        sizeFilter = value
        emitState()
    }

    fun setTemperamentFilter(value: String) {
        if (value == temperamentFilter) return
        temperamentFilter = value
        emitState()
    }

    fun setAvailabilityFilter(value: String) {
        if (value == availabilityFilter) return
        availabilityFilter = value
        emitState()
    }

    fun clearFilters() {
        searchTerm = ""
        sizeFilter = ALL
        temperamentFilter = ALL
        availabilityFilter = ALL
        emitState()
    }

    /** Number of active dropdown filters (search is excluded, matching Angular). */
    private fun activeFiltersCount(): Int {
        var count = 0
        if (sizeFilter != ALL) count++
        if (temperamentFilter != ALL) count++
        if (availabilityFilter != ALL) count++
        return count
    }

    private fun applyFilters(): List<Pet> {
        val query = searchTerm.trim().lowercase()
        return allPets.filter { pet ->
            val matchesSearch = query.isEmpty() ||
                pet.name.containsIgnoreCase(query) ||
                pet.breed.containsIgnoreCase(query) ||
                pet.description.containsIgnoreCase(query)

            val matchesSize = sizeFilter == ALL || pet.size.equalsIgnoreCase(sizeFilter)
            val matchesTemperament =
                temperamentFilter == ALL || pet.temperament.equalsIgnoreCase(temperamentFilter)
            val matchesAvailability =
                availabilityFilter == ALL || pet.availabilityStatus.equalsIgnoreCase(availabilityFilter)

            matchesSearch && matchesSize && matchesTemperament && matchesAvailability
        }
    }

    private fun emitState() {
        val filtered = applyFilters()
        _uiState.value = PetListUiState(
            isLoading = isLoading,
            error = error,
            filteredPets = filtered,
            totalCount = allPets.size,
            shownCount = filtered.size,
            activeFiltersCount = activeFiltersCount(),
            searchTerm = searchTerm,
            sizeFilter = sizeFilter,
            temperamentFilter = temperamentFilter,
            availabilityFilter = availabilityFilter
        )
    }
}

private fun String?.containsIgnoreCase(lowercaseQuery: String): Boolean =
    this != null && lowercase().contains(lowercaseQuery)

private fun String?.equalsIgnoreCase(other: String): Boolean =
    this != null && equals(other, ignoreCase = true)
