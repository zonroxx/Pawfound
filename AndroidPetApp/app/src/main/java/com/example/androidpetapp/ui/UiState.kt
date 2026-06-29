package com.example.androidpetapp.ui

import com.example.androidpetapp.data.model.Pet

data class PetListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val filteredPets: List<Pet> = emptyList(),
    val totalCount: Int = 0,
    val shownCount: Int = 0,
    val activeFiltersCount: Int = 0,
    val searchTerm: String = "",
    val sizeFilter: String = ALL,
    val temperamentFilter: String = ALL,
    val availabilityFilter: String = ALL
) {
    companion object {
        const val ALL = "all"
    }
}
