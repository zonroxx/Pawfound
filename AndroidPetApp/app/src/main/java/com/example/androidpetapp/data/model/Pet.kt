package com.example.androidpetapp.data.model

import java.io.Serializable


data class Pet(
    val id: Int? = null,
    val name: String? = null,
    val breed: String? = null,
    val age: Int = 0,
    val sex: String? = null,
    val size: String? = null,
    val color: String? = null,
    val weight: Double? = null,
    val photoUrl: String? = null,
    val healthStatus: String? = null,
    val vaccinationStatus: String? = null,
    val dewormed: Boolean? = null,
    val neuteredSpayed: Boolean? = null,
    val medicalNotes: String? = null,
    val temperament: String? = null,
    val houseTrained: Boolean? = null,
    val adoptionFee: Double? = null,
    val location: String? = null,
    val availabilityStatus: String? = null,
    val intakeDate: String? = null,
    val description: String? = null,
    val trainingLevel: String? = null,
    val contactInfo: String? = null
) : Serializable
