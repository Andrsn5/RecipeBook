package com.example.recipebook.presentation.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipebook.databinding.FragmentFavoriteBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupAdapter()
        setupRecyclerView()


        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = RecipeAdapter(
            onClick = { recipe ->
                val action = FavoriteFragmentDirections.actionFavoritesFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.toggleFavorite(recipe)
            }
        )

    }

    private fun setupRecyclerView() {

        binding.recyclerView.apply {
            // ВАЖНО: убедимся, что адаптер установлен
            adapter = this@FavoriteFragment.adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)

            setBackgroundColor(0x10FF0000)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoritesState.collect { state ->
                if (!isAdded) return@collect

                when (state) {
                    is UiState.Loading -> {
                        showLoading(true)
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        showLoading(false)
                        val favorites = state.data ?: emptyList()

                        if (favorites.isEmpty()) {
                            showEmptyState(true)
                            binding.recyclerView.visibility = View.GONE
                            adapter.submitList(emptyList())
                        } else {
                            showEmptyState(false)
                            binding.recyclerView.visibility = View.VISIBLE
                            adapter.submitList(favorites)

                        }
                    }
                    is UiState.Error -> {
                        showLoading(false)
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyView.text = state.message ?: "Произошла ошибка"
                    }
                    is UiState.Empty -> {
                        showLoading(false)
                        showEmptyState(true)
                        binding.recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(show: Boolean) {
        binding.emptyView.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}