package com.example.recipebook.presentation.ui.categoryRecipes

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.databinding.FragmentCategoryRecipesBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import com.example.recipebook.presentation.ui.util.showError
import com.example.recipebook.presentation.ui.util.showOfflineBanner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryRecipesFragment : Fragment() {

    private var _binding: FragmentCategoryRecipesBinding? = null
    private val binding get() = _binding!!

    private val args: CategoryRecipesFragmentArgs by navArgs()
    private val viewModel: CategoryRecipesViewModel by viewModels()
    private lateinit var recipesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupRecyclerView()
        setupScrollListener()
        observeViewModel()
    }

    private fun setupAdapter() {
        recipesAdapter = RecipeAdapter(
            onClick = { recipe ->
                val action = CategoryRecipesFragmentDirections.actionCategoryRecipesFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.onFavouriteClick(recipe)
            }
        )
    }

    private fun setupRecyclerView() {
        binding.recipesRecyclerView.apply {
            adapter = recipesAdapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }
    }

    private fun setupScrollListener() {
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

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recipesState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                            binding.recipesRecyclerView.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val recipes = state.data
                            if (recipes.isEmpty()) {
                                binding.emptyText.visibility = View.VISIBLE
                                binding.emptyText.text = "No recipes found"
                                binding.recipesRecyclerView.visibility = View.GONE
                            } else {
                                binding.emptyText.visibility = View.GONE
                                binding.recipesRecyclerView.visibility = View.VISIBLE
                                recipesAdapter.submitList(recipes)
                            }
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            val message = state.message
                            val cachedData = state.data

                            if (!cachedData.isNullOrEmpty()) {
                                binding.emptyText.visibility = View.GONE
                                binding.recipesRecyclerView.visibility = View.VISIBLE
                                recipesAdapter.submitList(cachedData)
                            } else {
                                binding.emptyText.visibility = View.VISIBLE
                                binding.recipesRecyclerView.visibility = View.GONE
                            }

                            if (message.contains("интернет", ignoreCase = true) ||
                                message.contains("internet", ignoreCase = true) ||
                                message.contains("UnknownHost", ignoreCase = true) ||
                                message.contains("connect", ignoreCase = true)) {
                                showOfflineBanner(onRetry = {
                                    args.categoryName.let { viewModel.loadRecipes(it) }
                                })
                            } else {
                                showError(message)
                            }
                        }
                        is UiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                            binding.emptyText.text = "No recipes found"
                            binding.recipesRecyclerView.visibility = View.GONE
                        }
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
