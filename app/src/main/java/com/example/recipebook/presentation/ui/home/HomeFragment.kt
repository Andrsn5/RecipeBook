package com.example.recipebook.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.databinding.FragmentHomeBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.favorite.FavoritesFragmentDirections
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RecipeAdapter(
            onClick = { recipe ->
                val action =
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.toggleFavorite(recipe.id)
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        
        lifecycleScope.launch { 
            viewModel.state.collect { state ->
                when(state) {
                    is UiState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.data)
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.text = state.message
                        binding.emptyText.visibility = View.VISIBLE
                    }
                    is UiState.Empty -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.text = "Нет рецептов"
                        binding.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}