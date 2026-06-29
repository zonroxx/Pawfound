package com.example.androidpetapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidpetapp.R
import com.example.androidpetapp.databinding.FragmentPetsBinding


class PetsFragment : Fragment() {

    private var _binding: FragmentPetsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PetViewModel by viewModels()

    private val detailsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) viewModel.loadPets()
    }

    private val addLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) viewModel.loadPets()
    }

    private val adapter = PetAdapter { pet ->
        detailsLauncher.launch(PetDetailsActivity.newIntent(requireContext(), pet))
    }

    // Parallel to the label string-arrays in res/values/arrays.xml (order matters).
    private val sizeValues = listOf("all", "small", "medium", "large")
    private val temperamentValues =
        listOf("all", "friendly", "calm", "playful", "gentle", "aggressive")
    private val availabilityValues = listOf("all", "available", "pending", "adopted")

    private var isSyncing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerPets.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPets.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.loadPets() }
        binding.buttonRetry.setOnClickListener { viewModel.loadPets() }
        binding.buttonClear.setOnClickListener { viewModel.clearFilters() }
        binding.fabAdd.setOnClickListener {
            addLauncher.launch(PetFormActivity.newAddIntent(requireContext()))
        }

        setupSpinner(binding.spinnerSize, R.array.size_filter_labels, sizeValues) {
            viewModel.setSizeFilter(it)
        }
        setupSpinner(
            binding.spinnerTemperament, R.array.temperament_filter_labels, temperamentValues
        ) {
            viewModel.setTemperamentFilter(it)
        }
        setupSpinner(
            binding.spinnerAvailability, R.array.availability_filter_labels, availabilityValues
        ) {
            viewModel.setAvailabilityFilter(it)
        }
        setupSearch()

        observeState()

        // Admin gate: only show the add FAB when admin mode is on.
        AdminMode.isAdmin.observe(viewLifecycleOwner) { isAdmin ->
            binding.fabAdd.visibility = if (isAdmin) View.VISIBLE else View.GONE
        }
    }

    private fun setupSpinner(
        spinner: Spinner,
        labelArrayRes: Int,
        values: List<String>,
        onValueSelected: (String) -> Unit
    ) {
        spinner.adapter = ArrayAdapter.createFromResource(
            requireContext(), labelArrayRes, android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                if (isSyncing) return
                onValueSelected(values[pos])
            }

            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
    }

    private fun setupSearch() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isSyncing) return
                viewModel.setSearchTerm(s?.toString().orEmpty())
            }
        })
    }

    private fun observeState() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            syncFilterWidgets(state)

            val hasContent = state.filteredPets.isNotEmpty()
            val hasAnyPets = state.totalCount > 0
            binding.swipeRefresh.isRefreshing = state.isLoading && hasAnyPets
            binding.progressBar.visibility =
                if (state.isLoading && !hasAnyPets) View.VISIBLE else View.GONE

            adapter.submitList(state.filteredPets)

            binding.textCount.text =
                "Showing ${state.shownCount} of ${state.totalCount} pets"
            binding.buttonClear.visibility =
                if (state.activeFiltersCount > 0 || state.searchTerm.isNotEmpty()) View.VISIBLE
                else View.GONE

            when {
                state.error != null && !hasAnyPets ->
                    showState("Couldn't load pets.\n${state.error}", showRetry = true)
                !state.isLoading && !hasAnyPets ->
                    showState("No pets found.", showRetry = false)
                !state.isLoading && !hasContent ->
                    showState("No pets match your filters.", showRetry = false)
                else -> hideState()
            }
        }
    }

    private fun syncFilterWidgets(state: PetListUiState) {
        isSyncing = true

        if (binding.editSearch.text.toString() != state.searchTerm) {
            binding.editSearch.setText(state.searchTerm)
            binding.editSearch.setSelection(state.searchTerm.length)
        }
        syncSpinner(binding.spinnerSize, sizeValues, state.sizeFilter)
        syncSpinner(binding.spinnerTemperament, temperamentValues, state.temperamentFilter)
        syncSpinner(binding.spinnerAvailability, availabilityValues, state.availabilityFilter)

        isSyncing = false
    }

    private fun syncSpinner(spinner: Spinner, values: List<String>, value: String) {
        val index = values.indexOf(value).takeIf { it >= 0 } ?: 0
        if (spinner.selectedItemPosition != index) spinner.setSelection(index)
    }

    private fun showState(message: String, showRetry: Boolean) {
        binding.stateContainer.visibility = View.VISIBLE
        binding.textState.text = message
        binding.buttonRetry.visibility = if (showRetry) View.VISIBLE else View.GONE
    }

    private fun hideState() {
        binding.stateContainer.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerPets.adapter = null
        _binding = null
    }
}
