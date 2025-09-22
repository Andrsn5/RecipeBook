package com.example.recipebook.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.databinding.FragmentSearchBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = RecipeAdapter(
            onClick = { recipe ->
                val action = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.toggleFavorite(recipe.id)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.searchInput.addTextChangedListener { text ->
            viewModel.search(text.toString())
        }

        binding.searchButton.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(InputMethodManager::class.java)
        inputMethodManager.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (!isAdded || _binding == null) return@collect // Защита от NPE

                when(state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyText.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.data)
                        binding.emptyText.visibility = if (state.data.isEmpty()) View.VISIBLE else View.GONE
                        binding.recyclerView.visibility = if (state.data.isEmpty()) View.GONE else View.VISIBLE
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyText.text = state.message
                    }
                    is UiState.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}