package com.example.recipebook.presentation.ui.categoryRecipes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recipebook.databinding.FragmentCategoryRecipesBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryRecipesFragment : Fragment() {

    private var _binding: FragmentCategoryRecipesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryRecipesViewModel by viewModels()
    private lateinit var recipesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipesAdapter = RecipeAdapter(
            onClick = { recipe ->
                val action = CategoryRecipesFragmentDirections.actionCategoryRecipesFragmentToDetailsFragment(recipe.id)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->
                viewModel.onFavouriteClick(recipe)
            }
        )

        binding.recipesRecyclerView.apply {
            adapter = recipesAdapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipesState.collectLatest { state ->
                if (!isAdded) return@collectLatest

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
                        binding.emptyText.visibility = View.VISIBLE
                        binding.emptyText.text = state.message
                        binding.recipesRecyclerView.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
