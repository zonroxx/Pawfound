package com.example.androidpetapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidpetapp.R
import com.example.androidpetapp.data.model.Pet
import com.example.androidpetapp.data.remote.normalizeEmulatorImageUrl
import com.example.androidpetapp.databinding.ItemPetBinding


class PetAdapter(
    private val onClick: (Pet) -> Unit
) : ListAdapter<Pet, PetAdapter.PetViewHolder>(DIFF) {

    inner class PetViewHolder(
        private val binding: ItemPetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pet: Pet) {
            binding.textName.text = pet.name ?: "Unnamed"
            binding.textFee.text = formatFee(pet.adoptionFee) ?: "-"

            binding.badgeBreed.text = pet.breed?.takeIf { it.isNotBlank() } ?: "Unknown"
            binding.badgeSize.text = pet.size?.takeIf { it.isNotBlank() } ?: "-"

            binding.textAgeValue.text = pet.age.toString()
            binding.textSexValue.text = pet.sex?.takeIf { it.isNotBlank() } ?: "-"
            binding.textMoodValue.text = pet.temperament?.takeIf { it.isNotBlank() } ?: "-"

            binding.badgeAvailability.text = pet.availabilityStatus ?: "-"
            binding.badgeAvailability.setBackgroundResource(availabilityBadge(pet.availabilityStatus))

            Glide.with(binding.imagePet)
                .load(normalizeEmulatorImageUrl(pet.photoUrl))
                .placeholder(R.drawable.ic_pet_placeholder)
                .error(R.drawable.ic_pet_placeholder)
                .centerCrop()
                .into(binding.imagePet)

            binding.root.setOnClickListener { onClick(pet) }
            binding.buttonViewDetails.setOnClickListener { onClick(pet) }
        }

        private fun availabilityBadge(status: String?): Int = when (status?.lowercase()) {
            "available" -> R.drawable.bg_pill_green
            "pending" -> R.drawable.bg_pill_cream
            "adopted" -> R.drawable.bg_pill_peach
            else -> R.drawable.bg_pill_white
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val binding = ItemPetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Pet>() {
            override fun areItemsTheSame(oldItem: Pet, newItem: Pet): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Pet, newItem: Pet): Boolean =
                oldItem == newItem
        }
    }
}
