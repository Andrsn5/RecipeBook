package com.example.recipebook.presentation.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipebook.databinding.FragmentFavoriteBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

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
        adapter = RecipeAdapter(
            onClick = { recipe ->
                val action =
                    FavoritesFragmentDirections.actionFavoritesFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.toggleFavorite(recipe.id)
            }
        )
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyView.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val list = state.data ?: emptyList()
                        if (list.isEmpty()) {
                            binding.emptyView.visibility = View.VISIBLE
                        } else {
                            binding.emptyView.visibility = View.GONE
                            adapter.submitList(list)
                        }
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                        binding.emptyView.text = state.message
                    }
                    else -> {}
                }
            }
        }

        viewModel.loadFavorite()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}