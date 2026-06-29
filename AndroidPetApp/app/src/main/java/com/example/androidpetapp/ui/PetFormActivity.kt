package com.example.androidpetapp.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.androidpetapp.R
import com.example.androidpetapp.data.model.Pet
import com.example.androidpetapp.databinding.ActivityPetFormBinding
import java.util.Calendar

class PetFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPetFormBinding
    private val viewModel: PetFormViewModel by viewModels()

    private var editingId: Int? = null

    private val sexValues = listOf("", "Male", "Female")
    private val sizeValues = listOf("", "Small", "Medium", "Large")
    private val temperamentValues =
        listOf("", "Friendly", "Calm", "Playful", "Gentle", "Aggressive")
    private val availabilityValues = listOf("Available", "Pending", "Adopted")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPetFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.formRoot) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        bindSpinner(binding.spinnerSex, R.array.form_sex_labels)
        bindSpinner(binding.spinnerSize, R.array.form_size_labels)
        bindSpinner(binding.spinnerTemperament, R.array.form_temperament_labels)
        bindSpinner(binding.spinnerAvailability, R.array.form_availability_labels)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.buttonCancel.setOnClickListener { finish() }
        binding.buttonSave.setOnClickListener { onSave() }
        binding.inputIntakeDate.setOnClickListener { showDatePicker() }

        val editingPet = intentPet()
        editingId = editingPet?.id
        if (editingPet != null) {
            binding.toolbar.title = "Edit Pet"
            binding.buttonSave.text = "Update Pet"
            prefill(editingPet)
        } else {
            binding.toolbar.title = "Add Pet"
            binding.buttonSave.text = "Add Pet"
            // Match the Angular form default.
            setSpinnerValue(binding.spinnerAvailability, availabilityValues, "Available")
        }

        observeState()
    }

    @Suppress("DEPRECATION")
    private fun intentPet(): Pet? = intent.getSerializableExtra(EXTRA_PET) as? Pet

    private fun bindSpinner(spinner: Spinner, labelArrayRes: Int) {
        spinner.adapter = ArrayAdapter.createFromResource(
            this, labelArrayRes, android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun prefill(pet: Pet) {
        binding.inputName.setText(pet.name.orEmpty())
        binding.inputBreed.setText(pet.breed.orEmpty())
        binding.inputPhotoUrl.setText(pet.photoUrl.orEmpty())
        binding.inputAge.setText(pet.age.toString())
        binding.inputColor.setText(pet.color.orEmpty())
        binding.inputWeight.setText(pet.weight?.toString().orEmpty())
        binding.inputFee.setText(pet.adoptionFee?.toString().orEmpty())
        binding.inputDescription.setText(pet.description.orEmpty())
        binding.inputHealthStatus.setText(pet.healthStatus.orEmpty())
        binding.inputVaccinationStatus.setText(pet.vaccinationStatus.orEmpty())
        binding.inputMedicalNotes.setText(pet.medicalNotes.orEmpty())
        binding.inputTrainingLevel.setText(pet.trainingLevel.orEmpty())
        binding.inputLocation.setText(pet.location.orEmpty())
        binding.inputIntakeDate.setText(pet.intakeDate.orEmpty())
        binding.inputContactInfo.setText(pet.contactInfo.orEmpty())

        binding.switchDewormed.isChecked = pet.dewormed == true
        binding.switchNeutered.isChecked = pet.neuteredSpayed == true
        binding.switchHouseTrained.isChecked = pet.houseTrained == true

        setSpinnerValue(binding.spinnerSex, sexValues, pet.sex.orEmpty())
        setSpinnerValue(binding.spinnerSize, sizeValues, pet.size.orEmpty())
        setSpinnerValue(binding.spinnerTemperament, temperamentValues, pet.temperament.orEmpty())
        setSpinnerValue(
            binding.spinnerAvailability, availabilityValues,
            pet.availabilityStatus?.takeIf { it.isNotBlank() } ?: "Available"
        )
    }

    private fun onSave() {
        val name = binding.inputName.text?.toString()?.trim().orEmpty()
        if (name.isEmpty()) {
            binding.inputName.error = "Name is required"
            binding.inputName.requestFocus()
            return
        }

        val pet = Pet(
            id = null, // server assigns (add) / uses path id (edit); never sent in body
            name = name,
            breed = textOrNull(binding.inputBreed.text?.toString()),
            age = binding.inputAge.text?.toString()?.trim()?.toIntOrNull() ?: 0,
            sex = textOrNull(spinnerValue(binding.spinnerSex, sexValues)),
            size = textOrNull(spinnerValue(binding.spinnerSize, sizeValues)),
            color = textOrNull(binding.inputColor.text?.toString()),
            weight = binding.inputWeight.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0,
            photoUrl = textOrNull(binding.inputPhotoUrl.text?.toString()),
            healthStatus = textOrNull(binding.inputHealthStatus.text?.toString()),
            vaccinationStatus = textOrNull(binding.inputVaccinationStatus.text?.toString()),
            dewormed = binding.switchDewormed.isChecked,
            neuteredSpayed = binding.switchNeutered.isChecked,
            medicalNotes = textOrNull(binding.inputMedicalNotes.text?.toString()),
            temperament = textOrNull(spinnerValue(binding.spinnerTemperament, temperamentValues)),
            houseTrained = binding.switchHouseTrained.isChecked,
            adoptionFee = binding.inputFee.text?.toString()?.trim()?.toDoubleOrNull() ?: 0.0,
            location = textOrNull(binding.inputLocation.text?.toString()),
            availabilityStatus = spinnerValue(binding.spinnerAvailability, availabilityValues),
            intakeDate = textOrNull(binding.inputIntakeDate.text?.toString()),
            description = textOrNull(binding.inputDescription.text?.toString()),
            trainingLevel = textOrNull(binding.inputTrainingLevel.text?.toString()),
            contactInfo = textOrNull(binding.inputContactInfo.text?.toString())
        )

        viewModel.save(editingId, pet)
    }

    private fun observeState() {
        viewModel.saveState.observe(this) { state ->
            val loading = state is OpState.Loading
            binding.progressSave.visibility = if (loading) View.VISIBLE else View.GONE
            binding.buttonSave.isEnabled = !loading
            binding.buttonCancel.isEnabled = !loading

            when (state) {
                is OpState.Success -> {
                    val data = Intent().putExtra(RESULT_PET, state.pet)
                    setResult(RESULT_OK, data)
                    Toast.makeText(
                        this,
                        if (editingId == null) "Pet added" else "Pet updated",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                is OpState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    viewModel.consumeError()
                }
                else -> Unit
            }
        }
    }

    private fun showDatePicker() {
        val today = Calendar.getInstance()
        // Seed the picker from the current value if it's a valid yyyy-MM-dd.
        binding.inputIntakeDate.text?.toString()?.takeIf { it.isNotBlank() }?.let { raw ->
            val parts = raw.split("-")
            if (parts.size == 3) {
                val y = parts[0].toIntOrNull()
                val m = parts[1].toIntOrNull()
                val d = parts[2].toIntOrNull()
                if (y != null && m != null && d != null) today.set(y, m - 1, d)
            }
        }
        DatePickerDialog(
            this,
            { _, year, month, day ->
                binding.inputIntakeDate.setText(
                    String.format("%04d-%02d-%02d", year, month + 1, day)
                )
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun spinnerValue(spinner: Spinner, values: List<String>): String {
        val pos = spinner.selectedItemPosition
        return values.getOrElse(pos) { "" }
    }

    private fun setSpinnerValue(spinner: Spinner, values: List<String>, value: String) {
        val index = values.indexOf(value).takeIf { it >= 0 } ?: 0
        spinner.setSelection(index)
    }

    private fun textOrNull(value: String?): String? = value?.trim()?.takeIf { it.isNotEmpty() }

    companion object {
        private const val EXTRA_PET = "extra_pet"
        const val RESULT_PET = "result_pet"

        fun newAddIntent(context: Context): Intent =
            Intent(context, PetFormActivity::class.java)

        fun newEditIntent(context: Context, pet: Pet): Intent =
            newAddIntent(context).putExtra(EXTRA_PET, pet)
    }
}
