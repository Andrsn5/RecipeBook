package com.example.recipebook.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.databinding.FragmentHomeBinding
import com.example.recipebook.presentation.adapter.CategoriesAdapter
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import com.example.recipebook.presentation.ui.util.showError
import com.example.recipebook.presentation.ui.util.showOfflineBanner
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

        binding.recipesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem >= totalItemCount - 2) {
                    viewModel.loadMoreRecipes()
                }
            }
        })
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is HomeViewModel.HomeEvent.NavigateToCategory -> {
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeFragmentToCategoryRecipesFragment(event.categoryName)
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            val categories = state.data
                            categoriesAdapter.submitList(categories)
                        }
                        is UiState.Error -> {
                            val message = state.message
                            if (message.contains("интернет", ignoreCase = true) ||
                                message.contains("internet", ignoreCase = true) ||
                                message.contains("UnknownHost", ignoreCase = true) ||
                                message.contains("connect", ignoreCase = true)) {
                                showOfflineBanner()
                            } else {
                                showError(message)
                            }
                        }
                        else -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recipesState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val recipes = state.data
                            if (recipes.isEmpty()) {
                                binding.emptyText.visibility = View.VISIBLE
                            } else {
                                binding.emptyText.visibility = View.GONE
                                recipesAdapter.submitList(recipes)
                            }
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            val message = state.message
                            val cachedData = state.data

                            if (!cachedData.isNullOrEmpty()) {
                                binding.emptyText.visibility = View.GONE
                                recipesAdapter.submitList(cachedData)
                            } else {
                                binding.emptyText.visibility = View.VISIBLE
                            }

                            if (message.contains("интернет", ignoreCase = true) ||
                                message.contains("internet", ignoreCase = true) ||
                                message.contains("UnknownHost", ignoreCase = true) ||
                                message.contains("connect", ignoreCase = true)) {
                                showOfflineBanner(onRetry = { viewModel.loadRecipes() })
                            } else {
                                showError(message)
                            }
                        }
                        else -> {}
                    }
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