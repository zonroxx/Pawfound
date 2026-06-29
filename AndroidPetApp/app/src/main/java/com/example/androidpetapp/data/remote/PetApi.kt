package com.example.androidpetapp.data.remote

import com.example.androidpetapp.data.model.Pet
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface PetApi {

    @GET("api/pets")
    suspend fun getAllPets(): List<Pet>

    @GET("api/pets/{id}")
    suspend fun getPet(@Path("id") id: Int): Pet

    @POST("api/add-pet")
    suspend fun addPet(@Body pet: Pet): Pet

    @PUT("api/update-pet/{id}")
    suspend fun updatePet(@Path("id") id: Int, @Body pet: Pet): Pet

    @DELETE("api/delete-pet/{id}")
    suspend fun deletePet(@Path("id") id: Int)
}
