package com.example.androidpetapp.data.repository

import com.example.androidpetapp.data.model.Pet
import com.example.androidpetapp.data.remote.PetApi
import com.example.androidpetapp.data.remote.RetrofitClient


class PetRepository(private val api: PetApi = RetrofitClient.api) {

    suspend fun getAllPets(): List<Pet> = api.getAllPets()

    suspend fun getPet(id: Int): Pet = api.getPet(id)

    suspend fun addPet(pet: Pet): Pet = api.addPet(pet)

    suspend fun updatePet(id: Int, pet: Pet): Pet = api.updatePet(id, pet)

    suspend fun deletePet(id: Int) = api.deletePet(id)
}
