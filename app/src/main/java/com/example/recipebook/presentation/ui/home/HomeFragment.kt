package com.example.recipebook.presentation.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.R
import com.example.recipebook.RecipeApplication
import com.example.recipebook.databinding.FragmentHomeBinding
import com.example.recipebook.presentation.adapter.CategoriesAdapter
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.categories.CategoriesViewModel
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModelRecipe: HomeViewModel by viewModels()
    private val viewModelCategory : CategoriesViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var recipesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModelRecipe.loadRecipes()
        viewModelCategory.getCategories()



        binding.searchButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
            findNavController().navigate(action)
        }

        recipesAdapter = RecipeAdapter(
            onClick = { recipe ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe -> viewModelRecipe.toggleFavorite(recipe.id) }
        )

        binding.recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recipesRecyclerView.adapter = recipesAdapter


        categoriesAdapter = CategoriesAdapter { category ->
            viewModelRecipe.filterByCategory(category.name)
        }

        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoriesAdapter


        lifecycleScope.launch {
            viewModelRecipe.state.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        recipesAdapter.submitList(state.data)
                        Log.d("HomeFragment", "success: ${state}")
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d("HomeFragment", "Error: ${state.message}")
                    }
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            viewModelCategory.state.collect { state ->
                if (state is UiState.Success) {
                    categoriesAdapter.submitList(state.data)
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



