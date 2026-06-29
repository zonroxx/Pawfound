package com.example.androidpetapp.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bumptech.glide.Glide
import com.example.androidpetapp.R
import com.example.androidpetapp.data.model.Pet
import com.example.androidpetapp.data.remote.normalizeEmulatorImageUrl
import com.example.androidpetapp.databinding.ActivityPetDetailsBinding
import com.example.androidpetapp.databinding.ItemDetailFieldBinding

class PetDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPetDetailsBinding
    private val viewModel: PetDetailsViewModel by viewModels()

    private var currentPet: Pet? = null

    // Returns RESULT_OK from the form after a successful edit (PUT).
    private val editLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) return@registerForActivityResult
        @Suppress("DEPRECATION")
        val updated = result.data?.getSerializableExtra(PetFormActivity.RESULT_PET) as? Pet
        if (updated != null) bind(updated)
        // Either way, tell the list to refresh when we navigate back.
        setResult(RESULT_OK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.detailsRoot) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top, bottom = bars.bottom)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }

        val pet = intentPet()
        if (pet == null) {
            Toast.makeText(this, "Pet not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        bind(pet)

        // Admin gate (UI only): Edit/Delete are hidden unless admin mode is on.
        binding.buttonActions.visibility =
            if (AdminMode.isAdminNow) android.view.View.VISIBLE else android.view.View.GONE

        binding.buttonEdit.setOnClickListener {
            currentPet?.let { editLauncher.launch(PetFormActivity.newEditIntent(this, it)) }
        }
        binding.buttonDelete.setOnClickListener { confirmDelete() }

        observeDelete()
    }

    @Suppress("DEPRECATION")
    private fun intentPet(): Pet? = intent.getSerializableExtra(EXTRA_PET) as? Pet

    private fun bind(pet: Pet) {
        currentPet = pet

        binding.textName.text = pet.name?.takeIf { it.isNotBlank() } ?: "Unnamed pet"
        binding.textSubtitle.text = listOfNotNull(
            pet.breed?.takeIf { it.isNotBlank() },
            pet.availabilityStatus?.takeIf { it.isNotBlank() }
        ).joinToString(" • ").ifBlank { " " }

        Glide.with(binding.imagePet)
            .load(normalizeEmulatorImageUrl(pet.photoUrl))
            .placeholder(R.drawable.ic_pet_placeholder)
            .error(R.drawable.ic_pet_placeholder)
            .centerCrop()
            .into(binding.imagePet)

        // Re-fillable: clear sections so this works for the initial bind and after an edit.
        resetSection(binding.sectionBasics)
        resetSection(binding.sectionPhysical)
        resetSection(binding.sectionHealth)
        resetSection(binding.sectionAdoption)

        // Basics
        addField(binding.sectionBasics, "Age", ageText(pet.age))
        addField(binding.sectionBasics, "Sex", pet.sex)
        addField(binding.sectionBasics, "Temperament", pet.temperament)
        addField(binding.sectionBasics, "Training Level", pet.trainingLevel)
        addField(binding.sectionBasics, "House Trained", yesNo(pet.houseTrained))
        ensureSection(binding.sectionBasics)

        // Physical
        addField(binding.sectionPhysical, "Size", pet.size)
        addField(binding.sectionPhysical, "Color", pet.color)
        addField(binding.sectionPhysical, "Weight", pet.weight?.let { "$it kg" })
        ensureSection(binding.sectionPhysical)

        // Health
        addField(binding.sectionHealth, "Health Status", pet.healthStatus)
        addField(binding.sectionHealth, "Vaccination", pet.vaccinationStatus)
        addField(binding.sectionHealth, "Dewormed", yesNo(pet.dewormed))
        addField(binding.sectionHealth, "Neutered / Spayed", yesNo(pet.neuteredSpayed))
        addField(binding.sectionHealth, "Medical Notes", pet.medicalNotes)
        ensureSection(binding.sectionHealth)

        // Adoption & contact
        addField(binding.sectionAdoption, "Adoption Fee", formatFee(pet.adoptionFee))
        addField(binding.sectionAdoption, "Availability", pet.availabilityStatus)
        addField(binding.sectionAdoption, "Location", pet.location)
        addField(binding.sectionAdoption, "Available Since", formatIntakeDate(pet.intakeDate))
        addField(binding.sectionAdoption, "Description", pet.description)
        addField(binding.sectionAdoption, "Contact", pet.contactInfo)
        ensureSection(binding.sectionAdoption)

        setupContactButton(pet)
    }

    private fun confirmDelete() {
        val pet = currentPet ?: return
        val id = pet.id ?: run {
            Toast.makeText(this, "Pet has no id; cannot delete", Toast.LENGTH_SHORT).show()
            return
        }
        val name = pet.name?.takeIf { it.isNotBlank() } ?: "this pet"
        AlertDialog.Builder(this)
            .setTitle("Delete $name?")
            .setMessage("Delete $name? This can't be undone.")
            .setPositiveButton("Delete") { _, _ -> viewModel.delete(id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeDelete() {
        viewModel.deleteState.observe(this) { state ->
            val loading = state is OpState.Loading
            binding.buttonDelete.isEnabled = !loading
            binding.buttonEdit.isEnabled = !loading
            when (state) {
                is OpState.Success -> {
                    Toast.makeText(this, "Pet deleted", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
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

    private fun setupContactButton(pet: Pet) {
        val email = pet.contactInfo?.trim()
        if (email.isNullOrBlank()) {
            binding.buttonContact.visibility = View.GONE
            return
        }
        binding.buttonContact.visibility = View.VISIBLE
        binding.buttonContact.setOnClickListener {
            val petName = pet.name?.takeIf { it.isNotBlank() } ?: "a pet"
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")).apply {
                putExtra(Intent.EXTRA_SUBJECT, "Adoption inquiry about $petName")
            }
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /** Adds a label/value row, but only when the value is present. */
    private fun addField(container: ViewGroup, label: String, value: String?) {
        if (value.isNullOrBlank()) return
        val row = ItemDetailFieldBinding.inflate(
            LayoutInflater.from(this), container, false
        )
        row.textLabel.text = label
        row.textValue.text = value
        container.addView(row.root)
    }

    /** Clears a section and makes it (and its title) visible again before re-fill. */
    private fun resetSection(container: ViewGroup) {
        container.removeAllViews()
        container.visibility = View.VISIBLE
        sectionTitleFor(container)?.visibility = View.VISIBLE
    }

    /** Hide a whole section (and its title) if no fields were added to it. */
    private fun ensureSection(container: ViewGroup) {
        if (container.childCount == 0) {
            container.visibility = View.GONE
            sectionTitleFor(container)?.visibility = View.GONE
        }
    }

    /** The section title TextView is the sibling immediately above the container. */
    private fun sectionTitleFor(container: ViewGroup): View? {
        val parent = container.parent as? ViewGroup ?: return null
        val index = parent.indexOfChild(container)
        return if (index > 0) parent.getChildAt(index - 1) else null
    }

    private fun ageText(age: Int): String =
        if (age == 1) "1 year" else "$age years"

    private fun yesNo(value: Boolean?): String? =
        when (value) {
            true -> "Yes"
            false -> "No"
            null -> null
        }

    companion object {
        private const val EXTRA_PET = "extra_pet"

        fun newIntent(context: Context, pet: Pet): Intent =
            Intent(context, PetDetailsActivity::class.java).putExtra(EXTRA_PET, pet)
    }
}
