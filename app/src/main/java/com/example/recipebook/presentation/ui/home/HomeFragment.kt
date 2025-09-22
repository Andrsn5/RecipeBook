package com.example.recipebook.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipebook.databinding.FragmentHomeBinding
import com.example.recipebook.presentation.adapter.CategoriesAdapter
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var categoriesAdapter: CategoriesAdapter
    private lateinit var recipesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        setupRecyclerViews()
        observeViewModel()
        setupClickListeners()


        viewModel.loadCategories()
        viewModel.loadRecipes()
    }

    private fun setupAdapters() {
        categoriesAdapter = CategoriesAdapter { category ->
            viewModel.onCategorySelected(category)
        }

        recipesAdapter = RecipeAdapter(
            onClick = { recipe ->
                val action = HomeFragmentDirections.actionHomeFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.onFavouriteClick(recipe)
            }
        )
    }

    private fun setupRecyclerViews() {

        binding.categoriesRecyclerView.apply {
            adapter = categoriesAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                context,
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )
            setHasFixedSize(true)
        }


        binding.recipesRecyclerView.apply {
            adapter = recipesAdapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriesState.collectLatest { state ->
                if (!isAdded) return@collectLatest

                when (state) {
                    is UiState.Loading -> {

                    }
                    is UiState.Success -> {
                        val categories = state.data ?: emptyList()
                        categoriesAdapter.submitList(categories)
                    }
                    is UiState.Error -> {

                    }
                    else -> {}
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipesState.collectLatest { state ->
                if (!isAdded) return@collectLatest

                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyText.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val recipes = state.data ?: emptyList()
                        if (recipes.isEmpty()) {
                            binding.emptyText.visibility = View.VISIBLE
                        } else {
                            binding.emptyText.visibility = View.GONE
                            recipesAdapter.submitList(recipes)
                        }
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                        binding.emptyText.text = state.message
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.searchButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}